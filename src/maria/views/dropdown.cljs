(ns maria.views.dropdown
  (:require [re-view.core :as v :refer [defview]]
            [maria-commands.exec :as exec]
            [maria-commands.registry :refer-macros [defcommand]]
            [maria.util :as util]
            [re-db.d :as d]))

(defcommand :dropdown/up
  {:bindings ["Up"]
   :priority 9
   :when     :modal/dropdown}
  [{:keys [modal/dropdown]}]
  (.up dropdown))

(defcommand :dropdown/down
  {:bindings ["Down"]
   :priority 9
   :when     :modal/dropdown}
  [{:keys [modal/dropdown]}]
  (.down dropdown))

(defcommand :dropdown/select
  {:bindings ["Right" "Enter"]
   :priority 9
   :when     :modal/dropdown}
  [{:keys [modal/dropdown]}]
  (.select dropdown))

(defcommand :dropdown/number
  {:bindings ["1" "2" "3" "4" "5" "6" "7" "8" "9"]
   :priority 9
   :when     :modal/dropdown}
  [{:keys [modal/dropdown key]}]
  (prn :the-k key)
  (.selectN dropdown (dec (js/parseInt key))))


(defview numbered-list
  {:view/initial-state {:selection 0}
   :down               (fn [{:keys   [view/state]
                             [items] :view/children}]
                         (let [{:keys [selection]} @state]
                           (swap! state assoc :selection
                                  (if (>= selection (dec (count items)))
                                    0
                                    (min (inc selection)
                                         (dec (count items)))))))
   :up                 (fn [{:keys   [view/state]
                             [items] :view/children}]
                         (let [{:keys [selection]} @state]
                           (swap! state assoc :selection
                                  (if (<= selection 0)
                                    (max 0 (dec (count items)))
                                    (max 0 (dec selection))))))
   :select-n           (fn [{[items] :view/children} n]
                         (when (and (< n (count items))
                                    (> n -1))
                           ((:action (nth items n)))))
   :select             (fn [this]
                         (.selectN this (:selection @(:view/state this))))
   :view/will-receive-props
                       (fn [{[prev-items] :view/prev-children
                             [items]      :view/children
                             :as          this}]
                         (when (not= items prev-items)
                           (swap! (:view/state this) assoc :selection 0))
                         (when-not (nil? items)
                           (swap! (:view/state this) assoc :last-items items)))
   :view/did-mount     #(exec/set-context! {:modal/dropdown %})
   :view/will-unmount  #(exec/set-context! {:modal/dropdown nil})}
  [{:keys [view/state] :as this} items]
  (let [bg-selected "rgba(0,0,0,0.025)"
        {:keys [selection]} @state
        waiting? (nil? items)
        mobile? (d/get :UI :mobile-width?)
        legend #(do [:span.ml2.o-70.monospace
                     {:style {:color "#000"}}
                     [:span (when waiting? {:class "o-50"}) %]])
        items (if waiting? (:last-items @state) items)
        trigger-event (if mobile? :on-click
                                  :on-mouse-down)]
    [:div.bg-white.shadow-4
     {:class (when waiting? "o-50")}
     (when waiting? [:.progress-indeterminate])
     (->> items
          (map-indexed (fn [i {:keys [action label]}]
                         [:.nowrap.flex.items-center.pointer
                          {:key            i
                           :on-mouse-enter #(swap! state assoc :selection i)
                           :style          {:background-color (when (= i selection) bg-selected)
                                            :border-bottom    "1px solid rgba(0,0,0,0.05)"}
                           trigger-event   (comp action util/stop!)}
                          (when-not mobile? (legend (if (< i 9) (inc i) " ")))
                          label])))]))
