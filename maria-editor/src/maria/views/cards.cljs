(ns maria.views.cards
  (:require [re-view.core :as v :refer [defview]]))

(def card-classes #_"mv2" "mb3 shadow-4 bg-white pv1")

(v/defn card
  {:spec/props {:spec/keys []}
   :spec/children []}
  [& items]
  (into [:div {:class card-classes}] items))

(v/defn plain [& items]
  (into [:.mh3] items))