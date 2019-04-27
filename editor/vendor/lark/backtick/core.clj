(ns lark.backtick.core)

;template quoting: from https://github.com/brandonbloom/backtick
;Copyright © 2012 Brandon Bloom
;Distributed under the Eclipse Public License, the same as Clojure.
;Modified 2016, 2017 Matthew Huebert

(def ^:dynamic ^:private *gensyms*)

(defn resolve-sym [sym]
  (let [n (name sym)]
    (if (= (last n) \#)
      (if-let [gs (@*gensyms* sym)]
        gs
        (let [gs (gensym (str (subs n 0 (dec (count n))) "__auto__"))]
          (swap! *gensyms* assoc sym gs)
          gs))
      sym)))

(defn unquote? [form]
  (and (seq? form) (= (first form) 'clojure.core/unquote)))

(defn unquote-splicing? [form]
  (and (seq? form) (= (first form) 'clojure.core/unquote-splicing)))

(defn quote-fn* [form]
  (cond
    (symbol? form) `'~(resolve-sym form)
    (unquote? form) (second form)
    (unquote-splicing? form) (throw "splice not in list")
    (record? form) `'~form
    (coll? form)
    (let [xs (if (map? form) (apply concat form) form)
          parts (for [x xs]
                  (if (unquote-splicing? x)
                    (second x)
                    [(quote-fn* x)]))
          cat (doall `(concat ~@parts))]
      (cond
        (vector? form) `(vec ~cat)
        (map? form) `(apply hash-map ~cat)
        (set? form) `(set ~cat)
        (seq? form) `(apply list ~cat)
        :else (throw "Unknown collection type")))
    :else `'~form))

(defmacro template [form]
  (binding [*gensyms* (atom {})]
    (quote-fn* form)))
