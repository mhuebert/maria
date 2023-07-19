(ns maria.editor.util
  (:require #?(:cljs [yawn.hooks :as h])
            #?(:cljs ["react" :as react])
            [applied-science.js-interop :as j]
            [clojure.string :as str]
            [promesa.core :as p]
            [re-db.reactive :as r])
  #?(:cljs (:require-macros [maria.editor.util :as util :refer [defmacro:sci]])))

#?(:cljs
   (defn use-watch [x]
     (let [id (h/use-callback #js{})]
       (h/use-sync-external-store
         (h/use-callback
           (fn [changed!]
             (add-watch x id (fn [_ _ _ _] (changed!)))
             #(remove-watch x id))
           #js[x])
         #(r/peek x)))))

(defn guard
  "Returns x when (f x) is truthy"
  ([f] (fn [x] (guard x f)))
  ([x f] (when (f x) x)))

(defn some-str [s] (when-not (str/blank? s) s))

(defn ensure-prefix [s pfx]
  (cond->> s
           (not (str/starts-with? s pfx)) (str pfx)))

(defn strip-prefix [s pfx]
  (cond-> s
          (str/starts-with? s pfx)
          (subs (count pfx))))

#?(:cljs
   (defn type:number
     "Adds ^number type to return value"
     ^number [x] x))

#?(:cljs
   (defn akeep-first
     "Returns the first truthy value returned by f applied to items in coll,
      moving from start in direction 1 (forward) or -1 (back)"
     [f start direction ^js coll]
     (let [move (case direction 1 inc -1 dec)
           end (case direction 1 (count coll) -1 -1)]
       (loop [i start]
         (when-not (== i end)
           (let [x (aget coll i)]
             (if-some [ret (f x)]
               ret
               (recur (move i)))))))))

(defmacro defmacro:sci
  "Given name and a function with &form and &env params, defines a Clojure(Script) macro and a sci-compatible macro function with the same name."
  [name & args]
  (let [f `(fn ~name ~@args)]
    (if (:ns &env)
      `(def ~(with-meta name {:macro true}) ~f)
      `(let [f# ~f]
         (defmacro ~name [& args#] (apply f# ~'&form ~'&env args#))))))

#?(:cljs
   (defn fetch
     "Uses browser's fetch api to request url"
     [url & {:as opts :keys [headers then] :or {then identity}}]
     (.catch
       (p/-> (js/fetch url (j/lit {:method "GET"
                                   :headers (clj->js (or headers {}))}))
             then)
       (fn [e] (js/console.debug e)))))

#?(:cljs
   (defn use-fetch
     "Uses browser's fetch api to request url"
     [url & {:as opts}]
     (let [[v v!] (h/use-state nil)]
       (h/use-effect #(do (v! nil)
                          (p/catch (p/-> (fetch url opts) v!) str))
                     [url])
       v)))

#?(:cljs
   (defn use-promise [f deps]
     (let [[v v!] (h/use-state nil)
           !current-key (h/use-ref key)]
       (h/use-effect #(do (reset! !current-key key)
                          (v! nil)
                          (p/let [result (f)]
                            (when (= @!current-key key) (v! result))))
                     deps)
       v)))

(defn update-some [m updaters]
  (reduce-kv (fn [m k f]
               (if-let [v (get m k)]
                 (assoc m k (f v))
                 m)) m updaters))

(defn dissoc-value [m k v]
  (cond-> m
          (identical? (m k) v)
          (dissoc k)))

(defn extract-title [source]
  (some->> (str/split-lines source)
           (drop-while #(not (re-find #"^\s*;" %)))
           (drop-while #(re-find #"^[;\s]+$" %))
           first
           (re-find #"\s*;+[\s#]*(.*)")
           second))

(defn slug [title]
  (-> title
      (str/replace #"\s+" "_")
      (str/replace #"[^\w\d_]" "")
      str/lower-case
      (str/split #"_+")
      (as-> slug
            (->> (reduce (fn [out segment]
                           (if (>= (count out) 35)
                             (reduced out)
                             (str out "_" segment)))
                         (first slug)
                         (rest slug))))))

(comment
  (some-> "; Issue #256"
          extract-title
          slug))