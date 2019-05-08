(ns cells.cell
  (:refer-clojure :exclude [bound-fn])
  (:require [clojure.core :as core]
            [cells.util :as util]
            [applied-science.js-interop :as j]))

(defmacro defcell
  "Defines a named cell."
  [the-name & body]
  (let [[docstring body] (if (string? (first body))
                           [(first body) (rest body)]
                           [nil body])
        [options body] (if (and (map? (first body)) (> (count body) 1))
                         [(first body) (rest body)]
                         [nil body])]
    `(do
       ;; support re-evaluation without breaking links
       (declare ~the-name)
       (let [prev-cell# ~the-name]
         (def ~(with-meta the-name options)
           ~@(when docstring (list docstring))
           (~'cells.cell/cell*
            (fn [~'self] ~@body)
            (~'applied-science.js-interop/obj .-def? true .-update-existing prev-cell#)))))))

(defmacro cell
  "Returns an anonymous cell. Only one cell will be returned per lexical instance of `cell`,
  unless a unique `key` is provided. `self` is brought into scope, referring to the current cell."
  ([expr]
   `(~'cells.cell/cell nil ~expr))
  ([key expr]
   (let [id (util/unique-id)]
     `(~'cells.cell/cell*
       (fn [~'self] ~expr)
       (~'applied-science.js-interop/obj .-memo-key (str ~id "#" (hash ~key)))))))

(defmacro bound-fn
  "Returns an anonymous function which will evaluate in the context of the current cell
   (useful for handling async-state)"
  [& body]
  `(let [cell# ~'cells.cell/*self*
         error-handler# ~'cells.cell/*error-handler*]
     (fn [& args#]
       (binding [~'cells.cell/*self* cell#
                 ~'cells.cell/*error-handler* error-handler#]
         (try (apply (fn ~@body) args#)
              (catch ~'js/Error e#
                (~'cells.cell/error! cell# e#)))))))

(defmacro set-watched! [cell k value]
  `(let [cell# ~cell
         state# (.-state cell#)
         before# (~k state#)
         after# ~value]
     (when (not= before# after#)
       (set! (~k state#) after#)
       (~'-notify-watches cell# before# after#))
     cell#))