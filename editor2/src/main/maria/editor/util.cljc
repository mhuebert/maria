(ns maria.editor.util
  (:require #?(:cljs [yawn.hooks :as h])
            #?(:cljs ["react" :as react])
            [applied-science.js-interop :as j]
            [clojure.string :as str]
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
   (defn with-element
     ([init] (with-element nil init))
     ([{:keys [el props] :or {el :div}} init]
      (j/let [!destroy (h/use-ref nil)
              ref-fn (h/use-callback
                      (fn [el]
                        (when-let [f (guard @!destroy fn?)] (f))
                        (when el
                          (reset! !destroy (init el)))
                        nil))]
        [el (assoc props :ref ref-fn)]))))