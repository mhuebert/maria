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

(defn doc
  "Show documentation for given symbol" ;; TODO should accept unquoted sym (use a macro)
  [sym]
  (-> (sci.resolve/resolve-symbol @*context* sym)
      meta
      :doc))

(defn dir
  "Display public vars in namespace (symbol)" ;; TODO should accept unquoted sym (use a macro)
  [ns]
  (some->> @*context*
           :env
           deref
           :namespaces
           (#(% ns))
           keys
           (filter symbol?)
           sort))