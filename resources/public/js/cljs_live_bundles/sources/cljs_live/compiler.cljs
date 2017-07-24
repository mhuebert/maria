(ns cljs-live.compiler
  (:require [cognitect.transit :as transit]
            [goog.net.XhrIo :as xhr]
            [clojure.string :as string]))

(enable-console-print!)

(def debug? false)

(defn log [& args]
  (when debug? (apply println args)))

(def cljs-cache (atom {"provided" #{}}))

(defn is-provided?
  "Determine if a namespace is already provided. If using :optimizations :none,
  we can use `goog.isProvided_`, otherwise we need to use the list of `provided`
  namespaces in our cljs-live cache.

  It would be better to dynamically determine what has been 'provided',
  but I haven't found a way to do this for namespaces included in the
  compiled script."
  [name]
  (or (contains? *loaded-libs* name)
      (contains? (get @cljs-cache "provided") (str name))))

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
       (take 100)
       (map #(re-matches #".*goog\.provide\(['\"](.*)['\"]\)" %))
       (remove nil?)
       (map #(drop 1 %))
       (last)))

(defn namespace-content
  "Get local js content for a namespace"
  [name]
  (let [path (or (get-in @cljs-cache ["name-to-path" (munge name)])
                 (str (string/replace (munge (str name)) \. \/) ".js"))]
    (get @cljs-cache path)))

(defn wrapped-goog-provide
  "Wrap goog.provide to ignore 'Namespace %name already provided' errors"
  [goog-provide]
  (fn [name]
    (set! *loaded-libs* (conj *loaded-libs* name))
    (try (goog-provide name) (catch js/Error _
                               (log "goog provide: namespace already provided." name)))))

(defn wrapped-goog-require
  "Wrap goog.require to avoid reloading existing namespaces"
  [goog-require]
  (fn [name reload]
    (if (or (not (is-provided? name)) reload)
      (let [content (namespace-content name)]
        (cond content (do (set! *loaded-libs* (conj *loaded-libs* name))
                          (js/eval content))
              :else (do (set! *loaded-libs* (conj *loaded-libs* name))
                        (goog-require name reload))))
      (log "goog require: prevented the load of " name))))

(defonce _
         (do
           (set! *loaded-libs* (or *loaded-libs* #{}))
           (aset js/goog "provide" (wrapped-goog-provide (aget js/goog "provide")))
           (aset js/goog "require" (wrapped-goog-require (aget js/goog "require")))))

(defn- transit-json->cljs
  [json]
  (let [rdr (transit/reader :json)]
    (transit/read rdr json)))

(def blank-result {:source "" :lang :js})

(defn cache-str [path macros]
  (let [caches @cljs-cache]
    (first (for [ext (cond-> ["" ".cljs" ".cljc"]
                             macros (conj ".clj"))
                 :let [the-cache (get caches (str path ext ".cache.json"))]
                 :when the-cache]
             (transit-json->cljs the-cache)))))

(defn load-fn
  "Load requirements from bundled deps"
  [c-state {:keys [path macros name]} cb]

  (let [path (cond-> path
                     macros (str "$macros"))
        name (if-not macros name
                            (symbol (str name "$macros")))
        js-provided? (is-provided? name)
        c-state-provided? (contains? (get-in @c-state [::loaded]) name)]
    (swap! c-state update ::loaded (fnil conj #{}) name)
    (cb (if (and (*loaded-libs* (str name)) c-state-provided?)
          blank-result
          (let [[source lang] (when-not js-provided?
                                (or (some-> (or
                                              (get @cljs-cache (str path ".js"))
                                              (get @cljs-cache (get-in @cljs-cache ["name-to-path" (munge (str name))])))
                                            (list :js))
                                    (some-> (get @cljs-cache (str path ".clj"))
                                            (list :clj))))
                cache (cache-str path macros)
                result (cond-> (assoc blank-result :name name)
                               cache (merge {:cache cache})
                               source (merge {:source source
                                              :lang   lang}))]
            (when (and cache (not source))
              ;; if we have the cache but not the source,
              ;; we assume the source is provided.
              (set! *loaded-libs* (conj *loaded-libs* (str name))) 0)

            (when debug?
              (when (and (not js-provided?) (not source) #_(.test #"^goog" (str name)))
                (log (str "Missing dependency: " name)))
              (log [(if (boolean source) "source" "      ")
                    (if (boolean cache) "cache" "     ")
                    (if js-provided? "js-provided" "           ")
                    (when (boolean source) lang)] name))
            result)))))

(defn get-json [path cb]
  (xhr/send path
            (fn [e]
              (cb (.. e -target getResponseJson)))))

(defn fetch-bundle [path cb]
  (get-json path (comp cb js->clj)))

(defn add-bundle!
  "Add a bundle to the local cache."
  [bundle]
  (let [bundle (reduce-kv (fn [bundle k v]
                            (cond-> bundle
                                    (or (string/starts-with? v "goog")
                                        (string/starts-with? k "goog")
                                        (and (string/ends-with? k ".js")
                                             ;; for a JS file, see if a goog.provide statement shows up
                                             ;; in teh first 300 characters.
                                             (some-> v (> (.indexOf (subs v 0 300) "goog.provide") -1))))


                                    ;;
                                    ;; parse google provide statements to enable dependency resolution
                                    ;; for arbitrary google closure modules.
                                    ;; this is a slow operation so we don't want to do it on all files.

                                    (update "name-to-path" merge (let [provides (parse-goog-provides v)]
                                                                   (when (empty? provides)
                                                                     (log k ": " provides "\n"))
                                                                   (apply hash-map (interleave provides (repeat k))))))) bundle bundle)]
    (swap! cljs-cache (partial merge-with (fn [v1 v2]
                                            (if (coll? v1) (into v1 v2) v2))) bundle)
    bundle))

(defn load-bundles!
  "Load multiple bundles. When finished, set window.CLJS_LIVE to entire bundle cache & evaluate callback."
  ([paths] (load-bundles! paths #()))
  ([paths cb]
   (let [bundles (atom {})
         loaded (atom 0)
         total (count paths)]
     (doseq [path paths]
       (fetch-bundle path
                     (fn [bundle]
                       (let [bundle (add-bundle! bundle)]
                         (swap! bundles merge bundle)
                         (swap! loaded inc)
                         (when (= total @loaded)
                           (aset js/window "CLJS_LIVE" (clj->js @cljs-cache))
                           (cb @bundles)))))))))



