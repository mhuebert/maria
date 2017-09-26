(ns maria.views.cards
  (:require [re-view.core :as v :refer [defview]]))

(def card-classes #_"mv2" "mb3 nl3 nr3 bg-darken-lightly  pv1" #_"shadow-4 bg-white")

(v/defn card
  {:spec/props {:spec/keys []}
   :spec/children []}
  [& items]
  (into [:div {:class card-classes}] items))

(v/defn plain [& items]
  (into [:.mh3] items))