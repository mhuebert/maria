(ns maria.views.dropdown
  (:require [re-view.core :as v :refer [defview]]
            [commands.exec :as exec]
            [commands.registry :refer-macros [defcommand]]
            [maria.util :as util]
            [re-db.d :as d]
            [maria.views.icons :as icons]))

(defcommand :dropdown/up
  {:bindings ["Up"]
   :private  true
   :priority 9
   :when     :modal/dropdown}
  [{:keys [modal/dropdown]}]
  (.up dropdown))

(defcommand :dropdown/down
  {:bindings ["Down"]
   :private  true
   :priority 9
   :when     :modal/dropdown}
  [{:keys [modal/dropdown]}]
  (.down dropdown))

(defcommand :dropdown/select
  {:bindings ["Enter"]
   :private  true
   :priority 9
   :when     :modal/dropdown}
  [{:keys [modal/dropdown]}]
  (.select dropdown))

(defcommand :dropdown/number
  {:bindings ["1" "2" "3" "4" "5" "6" "7" "8" "9"]
   :private  true
   :priority 9
   :when     :modal/dropdown}
  [{:keys [modal/dropdown key]}]
  (.selectN dropdown (dec (js/parseInt key))))

(def DEFAULT_PAGE_SIZE 9)

(defview numbered-list
  {:view/initial-state      (fn [{:keys [ui/max-height default-selection] :as this}]
                              {:selection (or default-selection -1)
                               :PAGE_SIZE (if max-height (-> (.floor js/Math (-> max-height
                                                                                    (- 32)
                                                                                    (/ 30)))
                                                             (min 9)
                                                             (max 1))
                                                         DEFAULT_PAGE_SIZE)
                               :page      0})
   :down                    (fn [{:keys [view/state items]}]
                              (when (seq items)
                                (let [{:keys [selection page PAGE_SIZE]} @state
                                      current-page+ (take (inc PAGE_SIZE) (drop (* page PAGE_SIZE) items))
                                      [selection page] (if (>= selection (min (dec PAGE_SIZE) (dec (count current-page+))))
                                                         [0 (cond-> page
                                                                    (first (drop PAGE_SIZE current-page+)) (inc))]
                                                         [(min (inc selection) (dec PAGE_SIZE)) page])]
                                  (swap! state assoc :selection selection :page page))))
   :up                      (fn [{:keys [view/state items]}]
                              (when (seq items)
                                (let [{:keys [selection page PAGE_SIZE]} @state
                                      [selection page] (if (<= selection 0)
                                                         [(dec PAGE_SIZE) (max 0 (dec page))]
                                                         [(dec selection) page])]
                                  (swap! state assoc :selection selection :page page))))
   :select-n                (fn [{:keys [on-select! view/state items]} n]
                              (let [{:keys [page PAGE_SIZE]} @state
                                    offset (* PAGE_SIZE page)
                                    n (+ offset n)]
                                (when (and (< n (count items))
                                           (> n -1))
                                  (on-select! (:value (nth items n)))
                                  true)))
   :select                  (fn [this]
                              (let [selection (:selection @(:view/state this))]
                                (.selectN this selection)))
   :view/will-receive-state (fn [{:keys [view/state view/prev-state on-selection items]}]
                              (let [{:keys [selection page PAGE_SIZE]} @state
                                    item-i (+ selection (* page PAGE_SIZE))]
                                (when (and on-selection
                                           (not= selection (:selection prev-state)))
                                  (on-selection (when (and (> item-i -1)
                                                           (< item-i (count items)))
                                                  (:value (nth items item-i)))))))
   :view/will-receive-props
                            (fn [{{prev-items :items} :view/prev-props
                                  {items :items}      :view/props
                                  :as                 this}]
                              (when (not= items prev-items)
                                (swap! (:view/state this) assoc :selection (or (:default-selection this) -1)))
                              (when-not (nil? items)
                                (swap! (:view/state this) assoc :last-items items)))
   :view/did-mount          #(exec/set-context! {:modal/dropdown %})
   :view/will-unmount       #(exec/set-context! {:modal/dropdown nil})}
  [{:keys [view/state on-select! items class]}]
  (let [{:keys [selection page PAGE_SIZE]} @state
        waiting? (nil? items)
        mobile? (d/get :UI :mobile-width?)
        legend #(do [:span.o-70.monospace.bg-darken-lightly.ph2.inline-flex.items-center.f7
                     {:style {:color        "#000"
                              :border-right "1px solid rgba(0,0,0,0.03)"}}
                     [:span (when waiting? {:class "o-50"}) %]])
        items (if waiting? (:last-items @state) items)
        trigger-event (if mobile? :on-click
                                  :on-mouse-down)
        offset (* page PAGE_SIZE)
        items (drop offset items)
        more? (first (drop PAGE_SIZE items))]
    [:div.bg-white.br1
     {:class (str class " " (when waiting? "o-50"))}
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
                         [:.nowrap.flex.items-center.pointer.items-stretch
                          {:key            (+ offset i)
                           :on-mouse-enter #(swap! state assoc :selection i)
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
