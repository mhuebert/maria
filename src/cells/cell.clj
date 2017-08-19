(ns cells.cell
  (:require [re-view.util :as util]))

(def bindings (vec (mapcat #(do [(symbol %) (symbol "cells.cell" %)])
                           ["interval"
                            "slurp"])))

(defmacro defcell [name & body]
  (let [[docstring body] (util/parse-opt-args [string?] body)
        full-name (str *ns* "/" name)]
    `(def ~name
       ~@(when docstring (list docstring))
       (let ~bindings 
         (~'cells.cell/make-cell ~full-name (fn [~'self] ~@body))))))

(defmacro cell [& body]
  `(let ~bindings
     (~'cells.cell/make-cell (fn [~'self] ~@body))))