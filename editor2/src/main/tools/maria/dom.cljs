(ns tools.maria.dom
  (:require [applied-science.js-interop :as j]
            [tools.maria.string :refer [strip-prefix]]))

(defn find-or-create-element [id]
  (let [selector (strip-prefix (name id) "#")]
    (or (j/get js/window selector)
        (doto (js/document.createElement "div")
          (j/call :setAttribute :id selector)
          (js/document.body.appendChild)))))
