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

