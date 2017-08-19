(ns cells.cell
  (:require [re-view.util :as util]))

(def bindings (vec (mapcat #(do [(symbol %) (symbol "cells.cell" %)]) ["interval"])))

(defmacro defcell [name & args]
  (let [[docstring body] (util/parse-opt-args [string?] args)
        full-name (str *ns* "/" name)]
    `(def ~name
       ~@(when docstring (list docstring))
       (let ~bindings
         (~'cells.cell/make-cell ~full-name (fn ~@body))))))

(defmacro cell [& args]
  `(let ~bindings
     (~'cells.cell/make-cell (fn ~@args))))