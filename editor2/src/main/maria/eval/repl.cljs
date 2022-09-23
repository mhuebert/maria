(ns maria.eval.repl
  (:refer-clojure :exclude [eval])
  (:require [sci.impl.resolve :as sci.resolve]
            [sci.core :as sci]
            [clojure.string :as str]))

(defonce ^:dynamic *context* (atom nil))

(defn eval-string
  "Evaluates string `s` as one or multiple Clojure expressions"
  [source]
  (sci/eval-string* @*context* source))

(defn eval
  "Evaluates form as a Clojure expression"
  [form]
  (eval-string (pr-str form)))

(defn ^:macro doc
  "Show documentation for given symbol"
  [&form &env sym]
  (-> (sci.impl.resolve/resolve-symbol @maria.eval.repl/*context* sym)
      meta
      :doc))

(defn ^:macro dir
  "Display public vars in namespace (symbol)"
  [&form &env ns]
  `'~(some->> @maria.eval.repl/*context*
              :env
              deref
              :namespaces
              (#(% ns))
              keys
              (filter symbol?)
              sort))
