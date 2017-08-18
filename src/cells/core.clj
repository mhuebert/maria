(ns cells.core
  (:require [re-view.util :as util]))

(defmacro defcell [name & args]
  (let [[docstring arglist body] (util/parse-opt-args [string? vector?] args)
        full-name (symbol (str *ns*) (str name))]
    `(def ~name
       ~@(when docstring (list docstring))
       (~'cells.core/make-cell '~full-name (fn ~arglist ~@body)))))