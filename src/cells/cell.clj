(ns cells.cell
  (:require [re-view.util :as util]))

(def bindings (vec (mapcat #(do [(symbol %) (symbol "cells.cell" %)])
                           ["interval"
                            "fetch"])))

(defmacro defcell
  "Defines a named cell."
  [name & body]
  (let [[docstring body] (util/parse-opt-args [string?] body)
        full-name (str *ns* "/" name)]
    `(def ~name
       ~@(when docstring (list docstring))
       (let ~bindings
         (~'cells.cell/make-cell ~full-name (fn [~'self] ~@body))))))

(defmacro cell
  "Returns an anonymous cell."
  [& body]
  `(let ~bindings
     (~'cells.cell/make-cell ~(util/uuid) (fn [~'self] ~@body))))

(defmacro in-cell
  [& body]
  `(if-not ~'cells.cell/*cell*
     (~'cells.cell/cell ~@body)
     (do ~@body)))