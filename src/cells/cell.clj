(ns cells.cell
  (:require [cells.util :as util]))

(def lib-bindings
  (reduce (fn [bindings sym]
            (into bindings [(symbol (name sym)) sym]))
          []
          '[cells.lib/interval
            cells.lib/timeout
            cells.lib/fetch
            cells.lib/geo-location]))

(defn cell-bindings [cell-name]
  (into lib-bindings
        `[~'reset! (partial ~'cells.lib/restricted-reset! ~cell-name)
          ~'swap! (partial ~'cells.lib/restricted-swap! ~cell-name)]))

(defmacro defcell
  "Defines a named cell."
  [the-name & body]
  (let [[docstring body] (if (string? (first body))
                           [(first body) (rest body)]
                           [nil body])
        cell-name (keyword (str *ns*) (str the-name))]
    `(def ~the-name
       ~@(when docstring (list docstring))
       (let ~(cell-bindings cell-name)
         (~'cells.cell/make-cell ~cell-name (fn [~'self] ~@body))))))

(defmacro cell
  "Returns an anonymous cell."
  [the-name & body]
  (let [named? (and (or (string? the-name)
                        (keyword? the-name)
                        (symbol? the-name)
                        (number? the-name)) (seq body))
        unique-segment (str "_" (util/unique-id))
        cell-name (if named? `(keyword ~unique-segment (munge ~the-name))
                             (keyword (str *ns*) unique-segment))
        body (if named? body (cons the-name body))]
    `(let ~(cell-bindings cell-name)
       (~'cells.cell/make-cell ~cell-name (fn [~'self] ~@body)))))

(defmacro cell-fn
  "Returns an anonymous function which will evaluate with the current cell in the stack"
  [& body]
  `(let [the-cell# (first ~'cells.cell/*cell-stack*)]
     (fn [& args#]
       (binding [~'cells.cell/*cell-stack* (cons the-cell# ~'cells.cell/*cell-stack*)]
         (apply (fn ~@body) args#)))))