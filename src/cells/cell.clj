(ns cells.cell
  (:require [re-view.util :as util]))

(def bindings (vec (mapcat #(do [(symbol %) (symbol "cells.cell" %)])
                           ["interval"
                            "fetch"
                            "geo-location"])))

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

(defmacro cell-fn
  "Returns an anonymous function which will evaluate with the current cell in the stack"
  [& body]
  `(let [the-cell# (first ~'cells.cell/*cell-stack*)]
     (fn [& args#]
       (binding [~'cells.cell/*cell-stack* (cons the-cell# ~'cells.cell/*cell-stack*)]
         (apply (fn ~@body) args#)))))