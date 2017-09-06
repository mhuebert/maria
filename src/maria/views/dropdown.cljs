(ns maria.views.dropdown
  (:require [re-view.core :as v :refer [defview]]
            [maria-commands.exec :as exec]
            [maria-commands.registry :refer-macros [defcommand]]
            [maria.util :as util]
            [re-db.d :as d]
            [maria.views.icons :as icons]))

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
  {:bindings ["Enter"]
   :priority 9
   :when     :modal/dropdown}
  [{:keys [modal/dropdown]}]
  (.select dropdown))

(defcommand :dropdown/number
  {:bindings ["1" "2" "3" "4" "5" "6" "7" "8" "9"]
   :priority 9
   :when     :modal/dropdown}
  [{:keys [modal/dropdown key]}]
  (.selectN dropdown (dec (js/parseInt key))))

(def PAGE_SIZE 9)

(defview numbered-list
  {:view/initial-state      {:selection -1
                             :page      0}
   :down                    (fn [{:keys   [view/state]
                                  [items] :view/children}]
                              (let [{:keys [selection page]} @state
                                    current-page+ (take 10 (drop (* page PAGE_SIZE) items))
                                    [selection page] (if (>= selection (min 8 (dec (count current-page+))))
                                                       [0 (cond-> page
                                                                  (first (drop 9 current-page+)) (inc))]
                                                       [(min (inc selection) 8) page])]
                                (swap! state assoc :selection selection :page page)))
   :up                      (fn [{:keys   [view/state]
                                  [items] :view/children}]
                              (let [{:keys [selection page]} @state
                                    [selection page] (if (<= selection 0)
                                                       [8 (max 0 (dec page))]
                                                       [(dec selection) page])]
                                (swap! state assoc :selection selection :page page)))
   :select-n                (fn [{:keys   [on-select! view/state]
                                  [items] :view/children} n]
                              (let [offset (* PAGE_SIZE (:page @state))
                                    n (+ offset n)]
                                (when (and (< n (count items))
                                           (> n -1))
                                  (on-select! (:value (nth items n)))
                                  true)))
   :select                  (fn [this]
                              (let [selection (:selection @(:view/state this))]
                                (.selectN this selection)))
   :view/will-receive-state (fn [{:keys   [view/state view/prev-state on-selection]
                                  [items] :view/children}]
                              (let [{:keys [selection page]} @state
                                    item-i (+ selection (* page PAGE_SIZE))]
                                (when (and on-selection
                                           (not= selection (:selection prev-state)))
                                  (on-selection (when (and (> item-i -1)
                                                           (< item-i (count items)))
                                                  (:value (nth items item-i)))))))
   :view/will-receive-props
                            (fn [{[prev-items] :view/prev-children
                                  [items]      :view/children
                                  :as          this}]
                              (when (not= items prev-items)
                                (swap! (:view/state this) assoc :selection -1))
                              (when-not (nil? items)
                                (swap! (:view/state this) assoc :last-items items)))
   :view/did-mount          #(exec/set-context! {:modal/dropdown %})
   :view/will-unmount       #(exec/set-context! {:modal/dropdown nil})}
  [{:keys [view/state on-select!] :as this} items]

  (let [bg-selected "rgba(0,0,0,0.025)"
        {:keys [selection page]} @state
        waiting? (nil? items)
        mobile? (d/get :UI :mobile-width?)
        legend #(do [:span.ml2.o-70.monospace
                     {:style {:color "#000"}}
                     [:span (when waiting? {:class "o-50"}) %]])
        items (if waiting? (:last-items @state) items)
        trigger-event (if mobile? :on-click
                                  :on-mouse-down)
        offset (* page PAGE_SIZE)
        items (drop offset items)
        more? (first (drop PAGE_SIZE items))]
    [:div.bg-white.shadow-4.br1
     {:class (when waiting? "o-50")}
     (when waiting? [:.progress-indeterminate])
     (when (> page 0)
       [:.tc.pointer.flex.flex-column.items-center.h1
        {:on-mouse-down #(do
                           (util/stop! %)
                           (swap! state update :page dec))
         :style         {:border-bottom "1px solid rgba(0,0,0,0.05)"}}
        icons/ArrowDropUp])
     (->> (take PAGE_SIZE items)
          (map-indexed (fn [i {:keys [value label]}]
                         [:.nowrap.flex.items-center.pointer
                          {:key            (+ offset i)
                           :on-mouse-enter #(swap! state assoc :selection (+ offset i))
                           :class          (when (= i selection) "bg-darken-lightly")
                           :style          {:border-bottom "1px solid rgba(0,0,0,0.05)"}
                           trigger-event   #(do (on-select! value)
                                                (util/stop! %))}
                          (when-not mobile? (legend (if (< i 9) (inc i) " ")))
                          label])))
     (when more?
       [:.tc.pointer.flex.flex-column.items-center.h1
        {:on-mouse-down #(do
                           (util/stop! %)
                           (swap! state update :page inc))} icons/ArrowDropDown])]))
