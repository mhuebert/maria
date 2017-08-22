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
  [& body]
  (let [named? (> (count body) 1)
        the-name (when named? (first body))
        body (if named? (rest body) body)
        ;; unique ID for this lexical occurrence of `cell`
        lexical-marker (str "_" (util/unique-id))
        namespace-segment (str *ns*)
        ;; if name value is provided, append its hash to this cell id
        cell-name (if the-name `(keyword ~namespace-segment (str ~lexical-marker "._" (hash ~the-name)))
                               (keyword namespace-segment lexical-marker))
        ]
    `(let ~(cell-bindings cell-name)
       (~'cells.cell/make-cell ~cell-name (fn [~'self] ~@body)))))

(defmacro cell-fn
  "Returns an anonymous function which will evaluate with the current cell in the stack"
  [& body]
  `(let [the-cell# (first ~'cells.cell/*cell-stack*)]
     (fn [& args#]
       (binding [~'cells.cell/*cell-stack* (cons the-cell# ~'cells.cell/*cell-stack*)]
         (apply (fn ~@body) args#)))))