(ns maria.views.cards
  (:require [maria.views.icons :as icons]))

(def card-classes "mb3 nl3 nr3 bg-darken-lightly pv1")

(defn arrow [direction]
  (-> icons/ArrowPointingDown
      (icons/style {:transition "all ease 0.2s"
                    :transform  (str "rotate(" (case direction
                                                 :down 0
                                                 :left 90
                                                 :up 180
                                                 :right -90) "deg)")})))