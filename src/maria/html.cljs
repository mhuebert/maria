(ns maria.html
  (:require [re-view.core :as view]))

(defn html [x]
  (when-not (vector? x)
    (throw (js/Error. "Argument to `html` must be a vector.")))
  (view/component x))
