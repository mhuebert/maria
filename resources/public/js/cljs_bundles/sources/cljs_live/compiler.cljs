(ns cljs-live.compiler
  (:require [cognitect.transit :as transit]
            [goog.net.XhrIo :as xhr]
            [clojure.string :as string]))

(enable-console-print!)

(def debug? false)

(defn log [& args]
  (when debug? (apply println args)))

(def cljs-cache (atom {}))

(defn is-provided
  "Determine if a namespace is already provided. If using :optimizations :none,
  we can use `goog.isProvided_`, otherwise we need to use the list of `provided`
  namespaces in our cljs-live cache.

  It would be better to dynamically determine what has been 'provided',
  but I haven't found a way to do this for namespaces included in the
  compiled script."
  [name]
  (or (contains? *loaded-libs* name)
      (if-let [provided? (aget js/goog "isProvided_")]
        (provided? name)
        (contains? (set (get @cljs-cache "provided")) name))))

;; from cljs.js-deps
(defn parse-goog-provides
  "Given the lines from a JavaScript source file, parse the provide
  and require statements and return them in a map. Assumes that all
  provide and require statements appear before the first function
  definition."
  [lines]
  (->> (for [line (string/split lines "\n")
             x (string/split line #";")
             :when (not (re-matches #"^ *\*.*" x))]
         x)
       (take-while #(not (re-matches #".*=[\s]*function\(.*\)[\s]*[{].*" %)))
       (map #(re-matches #".*goog\.provide\(['\"](.*)['\"]\)" %))
       (remove nil?)
       (map #(drop 1 %))
       (reduce (fn [provides ns]
                 (let [munged-ns (string/replace (last ns) "_" "-")]
                   (conj provides munged-ns))) [])))

(defn namespace-content
  "Get local js content for a namespace"
  [name]
  (let [path (or (get-in @cljs-cache ["name-to-path" (munge name)])
                 (str (string/replace (munge (str name)) \. \/) ".js"))]
    (get @cljs-cache path)))

(defonce _
         (let [goog-provide (aget js/goog "provide")
               goog-require (aget js/goog "require")]
           (set! *loaded-libs* (or *loaded-libs* #{}))

           ;; wrap goog.provide to ignore 'Namespace %name already provided' errors
           (aset js/goog "provide"
                 (fn [name]
                   (set! *loaded-libs* (conj *loaded-libs* name))
                   (try (goog-provide name) (catch js/Error _))))

           ;; wrap goog.require to avoid reloading existing namespaces
           (aset js/goog "require"
                 (fn [name reload]
                   (when (or (not (is-provided name)) reload)
                     (let [content (namespace-content name)]
                       (cond content (do (set! *loaded-libs* (conj *loaded-libs* name))
                                         (js/eval content))
                             :else (do (set! *loaded-libs* (conj *loaded-libs* name))
                                       (goog-require name reload)))))))))

(defn- transit-json->cljs
  [json]
  (let [rdr (transit/reader :json)]
    (transit/read rdr json)))

(def blank-result {:source "" :lang :js})

(defn load-fn
  "Load requirements from bundled deps"
  [c-state {:keys [path macros name]} cb]
  (let [path (cond-> path
                     macros (str "$macros"))
        name (if-not macros name
                            (symbol (str name "$macros")))
        js-provided? (is-provided (munge (str name)))
        c-state-provided? (contains? (get-in @c-state [::loaded]) name)]
    (swap! c-state update ::loaded (fnil conj #{}) name)
    (cb (if (and (*loaded-libs* (str name)) c-state-provided?)
          blank-result
          (let [[source lang] (when-not js-provided?
                                (or (some-> (get @cljs-cache (get-in @cljs-cache ["name-to-path" (munge (str name))] (str path ".js")))
                                            (list :js))
                                    (some-> (get @cljs-cache (str path ".clj"))
                                            (list :clj))))
                cache (some-> (get @cljs-cache (str path ".cache.json")) (transit-json->cljs))
                result (cond-> blank-result
                               cache (merge {:cache cache})
                               source (merge {:source source
                                              :lang   lang}))]
            (when (or cache source)
              (set! *loaded-libs* (conj *loaded-libs* (str name))))
            (log [(if (boolean source) "source" "      ")
                  (if (boolean cache) "cache" "     ")] name)
            result)))))

(defn get-json [path cb]
  (xhr/send path
            (fn [e]
              (cb (.. e -target getResponseJson)))))

(defn fetch-bundle [path cb]
  (get-json path (comp cb js->clj)))

(defn load-bundles!
  ([paths] (load-bundles! paths #()))
  ([paths cb]
   (let [bundles (atom {})
         loaded (atom 0)
         total (count paths)]
     (doseq [path paths]
       (fetch-bundle path
                     (fn [bundle]
                       (let [bundle (reduce-kv (fn [bundle k v]
                                                 (cond-> bundle
                                                         (.test #"^goog" k)
                                                         (update "name-to-path" merge (let [provides (parse-goog-provides v)]
                                                                                        (when (empty? provides)
                                                                                          (println k ": " provides "\n"))
                                                                                        (apply hash-map (interleave provides (repeat k))))))) bundle bundle)]
                         (swap! cljs-cache (partial merge-with (fn [v1 v2]
                                                                 (if (coll? v1) (into v1 v2) v2))) bundle)
                         (swap! bundles merge bundle)
                         (swap! loaded inc)
                         (when (= total @loaded)
                           (aset js/window "CLJS_LIVE" (clj->js @cljs-cache))
                           (cb @bundles)))))))))



