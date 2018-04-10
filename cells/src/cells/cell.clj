(ns cells.cell
  (:require [cells.util :as util]))

(def lib-bindings
  (reduce (fn [bindings sym]
            (into bindings [(symbol (name sym)) sym]))
          []
          '[cells.lib/interval
            cells.lib/timeout
            cells.lib/fetch]))

(defmacro defcell
  "Defines a named cell."
  [the-name & body]
  (let [[docstring body] (if (string? (first body))
                           [(first body) (rest body)]
                           [nil body])
        [meta body] (if (and (map? (first body)) (> (count body) 1))
                      [(first body) (rest body)]
                      [nil body])
        cell-name (keyword (str *ns*) (str the-name))]
    `(def ~the-name
       ~@(when docstring (list docstring))
       (let ~lib-bindings
         (cond-> (~'cells.cell/cell* ~cell-name (fn [~'self] ~@body))
                 ~meta (with-meta ~meta))))))

(defn- cell-name
  "Construct a cell-name, incorporating the runtime-value of `key` if provided."
  [key]
  (let [uuid (str "_" (util/unique-id))
        namespace-segment (str *ns*)]
    (if key `(keyword ~namespace-segment (str ~uuid "._" (hash ~key)))
            (keyword namespace-segment uuid))))

(defmacro cell
  "Returns an anonymous cell. Only one cell will be returned per lexical instance of `cell`,
  unless a unique `key` is provided. Helper functions in `lib-bindings` (eg. interval) are
  hoisted into scope, as is `self`, which refers to the current cell."
  ([expr]
   `(~'cells.cell/cell nil ~expr))
  ([key expr]
   `(let ~lib-bindings
      (~'cells.cell/cell* ~(cell-name key) (fn [~'self] ~expr)))))

(defmacro cell-fn
  "Returns an anonymous function which will evaluate with the current cell in the stack.
  Similar to Clojure's `bound-fn`, but only cares about the currently bound cell."
  [& body]
  `(let [the-cell# (first ~'cells.cell/*cell-stack*)
         context# ~'cells.cell/*eval-context*]
     (fn [& args#]
       (binding [~'cells.cell/*cell-stack* (cons the-cell# ~'cells.cell/*cell-stack*)]
         (try (apply (fn ~@body) args#)
              (catch ~'js/Error error#
                (~'cells.eval-context/handle-error context# error#)))))))