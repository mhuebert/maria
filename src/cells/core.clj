(ns cells.core
  (:require [re-view.util :as util]))

(def bindings (vec (mapcat #(do [(symbol %) (symbol "cells.core" %)]) ["interval"])))

(defmacro defcell [name & args]
  (let [[docstring body] (util/parse-opt-args [string?] args)]
    `(def ~name
       ~@(when docstring (list docstring))
       (let ~bindings
         (~'cells.core/make-cell (:id ~'maria.eval/*eval-block*) (fn ~@body))))))

(defmacro cell [& args]
  `(let ~bindings
     (~'cells.core/make-cell (:id ~'maria.eval/*eval-block*)
       (fn ~@args))))