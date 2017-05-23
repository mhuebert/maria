(ns maria.views.repl-ns
  (:require [re-view.core :as v :refer [defview]]
            [maria.ns-utils :as ns-utils]
            [re-view-material.icons :as icons]
            [clojure.string :as string]
            [maria.views.repl-ui :as repl-ui]))

(defn docs-link [namespace name]
  (when (#{'cljs.core 'cljs.core$macros 'clojure.core} namespace)
    (list [:.gray.di "view on "]
          [:a {:href   (str "https://clojuredocs.org/clojure.core/" name)
               :target "_blank"
               :rel    "noopener noreferrer"} "clojuredocs.org"])))




(defview doc
  {:life/initial-state #(:expanded? %)
   :key                :name}
  [{:keys [doc
           arglists
           view/state
           standalone?] :as this}]
  [:.ph3.bt.b--near-white
   {:class (when standalone? repl-ui/card-classes)}
   [:.code.flex.items-center.pointer.mv1
    {:on-click #(swap! state not)}
    (when standalone?
      [:span.o-60 (namespace (:name this)) "/"])
    (str (name (:name this)))
    [:.flex-auto]
    [:span.o-50
     (if @state icons/ArrowDropUp
                icons/ArrowDropDown)]]
   (when @state
     (list
       [:.mv1.blue (string/join ", " (map str (second arglists)))]
       [:.gray.mv2 doc]))])

(defview dir
  {:life/initial-state {:expanded? false}}
  [{:keys [view/state]} c-state ns]
  (let [defs (->> (:defs (ns-utils/ns-map @c-state ns))
                  (seq)
                  (sort)
                  (map second)
                  (filter #(not (:private (:meta %)))))
        c (count defs)
        limit 10
        {:keys [expanded?]} @state]
    [:.sans-serif
     {:class repl-ui/card-classes}
     [:.b.pv2.ph3.f5 (str ns)]
     (map doc (cond->> defs
                       (not expanded?) (take limit)))
     (when (and (not expanded?) (> c limit))
       [:.o-50.pv2.flex.items-center.ph3.pointer.bt.b--near-white
        {:on-click #(swap! state assoc :expanded? true)}
        [:span "Show All (" (- c limit) " more)"]
        [:.flex-auto] icons/ExpandMore])]))

