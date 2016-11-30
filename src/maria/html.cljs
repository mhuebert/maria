(ns maria.html
  (:require [re-view.core :as v]))

(defn html [x]
  (when-not (vector? x)
    (throw (js/Error. "Argument to `html` must be a vector.")))
  (v/view x))
