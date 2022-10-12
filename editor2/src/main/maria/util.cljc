(ns maria.util
  #?(:cljs (:require-macros [maria.util :as util :refer [defmacro:sci]])))

(defn guard
  "Returns x when (f x) is truthy"
  ([f] (fn [x] (guard x f)))
  ([x f] (when (f x) x)))

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

(comment

  (defmacro:sci macro-test
    [&form &env a b c]
    [(str &form)
     (mapv (comp keyword key) (:locals &env))
     a
     b
     c])

  #?(:cljs
     (let [x 1]
       (prn {:fn macro-test
             :macro-application (util/macro-test 1 2 3)
             :fn-application (macro-test '(a-form) {:locals nil} 1 2 3)}))))