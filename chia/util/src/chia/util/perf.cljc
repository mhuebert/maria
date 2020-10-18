(ns chia.util.perf
  (:refer-clojure :exclude [str identical? array aget])
  (:require [clojure.core :as core]
            [chia.util.macros :as m]
            #?(:cljs [applied-science.js-interop :as j]))
  #?(:cljs (:require-macros [chia.util.perf])))

(m/defmacro str
  [x y]
  (m/case :cljs `(let [x# ~x
                       x# (if (some? x#) x# "")]
                   (if-some [y# ~y]
                     (~'js* "~{} += ~{}" x# y#)
                     x#))
          :clj `(core/str ~x ~y)))

(m/defmacro kw== [x y]
  #?(:cljs `(core/keyword-identical? ~x ~y)
     :clj  `(core/identical? ~x ~y)))

(m/defmacro identical? [a b]
  (m/case :clj `(core/identical? ~a ~b)
          :cljs (if (keyword? a)
                  `(and (keyword? ~b)
                        (core/identical? (.-fqn ~a)
                                         (.-fqn ~b)))
                  `(core/identical? ~a ~b))))

(m/defmacro identical-in?
  "Returns true if `x` is identical to any item in `coll` (expands to sequential `identical?` comparisons)."
  [coll x]
  `(or ~@(for [option coll]
           `(identical? ~option ~x))))

(m/defmacro keyword-in?
  "Returns true if `x` is identical to any item in `coll` (expands to sequential `keyword-identical?` (cljs) or `identical?` (clj) comparisons)."
  [coll x]
  (let [x-sym (gensym "x")]
    `(do (assert (keyword ~x))
         (let [~x-sym ~(m/case :cljs `(~'.-fqn ~x)
                               :clj x)]
           (or ~@(for [option coll]
                   `(core/identical? ~(m/case :cljs `(~'.-fqn ~option)
                                              :clj option) ~x-sym)))))))

(m/defmacro unchecked-kw-identical? [a b]
  (m/case :cljs `(core/identical? (.-fqn ~a) (.-fqn ~b))
          :clj `(core/identical? ~a ~b)))

(defn butlastv [v]
  (cond-> v
          (> (count v) 0) (pop)))

(defn lastv [v]
  (let [len (count v)]
    (when-not (zero? len)
      (peek v))))

(defn assoc-lastv [path v]
  (assoc path (dec (count path)) v))

(defn update-lastv [path f]
  (update path (dec (count path)) f))

(defn mapa [f s]
  #?(:cljs (reduce (fn [out x] (j/push! out (f x))) #js[] s)
     :clj  (reduce (fn [out x] (conj out (f x))) [] s)))

(m/defmacro array [& items]
  `(~(m/case :clj 'vector :cljs 'array)
    ~@items))

(m/defmacro aget [a n]
  `(~(m/case :clj `nth :cljs `core/aget) ~a ~n))