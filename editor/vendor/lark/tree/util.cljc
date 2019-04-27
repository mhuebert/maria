(ns lark.tree.util
  #?(:cljs (:require-macros
            [net.cgrand.macrovich :as macros]
            [lark.tree.util])
     :clj
           (:require [net.cgrand.macrovich :as macros])))

;; Successive `identical?` comparisons are _significantly_ faster than idiomatic alternatives such as `(contains? #{:k1 :k2} the-keyword)`,
;; results in a 2x overall speedup in parse/ast.

#?(:clj
   (defmacro contains-identical-keyword?
     "Returns true if `x` is identical to any item in `coll` (expands to sequential `keyword-identical?` (cljs) or `identical?` (clj) comparisons)."
     [coll x]
     `(or ~@(for [option coll]
              `((macros/case :clj ~'identical? :cljs ~'keyword-identical?)
                ~option ~x)))))

#?(:clj
   (defmacro contains-identical?
     "Returns true if `x` is identical to any item in `coll` (expands to sequential `identical?` comparisons)."
     [coll x]
     `(or ~@(for [option coll]
              `(identical? ~option ~x)))))

(defn some-str [s]
  (when (and (string? s) (not (identical? s "")))
    s))

(defn guard-> [x f]
  (when (f x) x))

(defn guard->> [f x]
  (when (f x) x))

#?(:cljs (defn log-current-stack []
           (try (throw (js/Error.))
                (catch js/Error e
                  (js/console.log (.-stack e))))))