(ns chia.util.macros
  (:refer-clojure :exclude [defmacro defn case])
  (:require [clojure.core :as core]
            [net.cgrand.macrovich :as macros])
  #?(:cljs (:require-macros
            [chia.util.macros])))

(core/defmacro macro-time
  "Only evaluates `body` at macro-expansion time
   (wraps macrovich/deftime)"
  [& body]
  `(macros/deftime ~@body))

(core/defmacro defmacro
  "Like defmacro, but only evaluated at macro-definition time"
  [& body]
  `(macro-time
    (core/defmacro ~@body)))

(core/defmacro defn
  "Like defn, but only evaluated at macro-definition time"
  [& body]
  `(macro-time
    (core/defn ~@body)))

(core/defmacro case
  "Like reader conditionals, but reliable within macros
   (wraps macrovich/case)"
  [& body]
  `(macros/case ~@body))