(ns maria.views.dropdown
  (:require [chia.view :as v]
            [lark.commands.exec :as exec]
            [lark.commands.registry :refer-macros [defcommand]]
            [maria.util :as util]
            [chia.db :as d]
            [maria.views.icons :as icons]
            [maria.views.error :as error]))

(defcommand :dropdown/up
  {:bindings ["Up"]
   :private true
   :priority 9
   :when :modal/dropdown}
  [{:keys [modal/dropdown]}]
  (.up dropdown))

(defcommand :dropdown/down
  {:bindings ["Down"]
   :private true
   :priority 9
   :when :modal/dropdown}
  [{:keys [modal/dropdown]}]
  (.down dropdown))

(defcommand :dropdown/select
  {:bindings ["Tab" "Enter"]
   :private true
   :priority 9
   :when (fn [{:keys [binding modal/dropdown] :as context}]
           (and (get context :modal/dropdown)
                (or (= binding "Tab")
                    (:select-on-enter dropdown))))}
  [{:keys [modal/dropdown]}]
  (.select dropdown))

#_(defcommand :dropdown/number
    {:bindings ["1" "2" "3" "4" "5" "6" "7" "8" "9"]
     :private true
     :priority 9
     :when :modal/numbered-dropdown}
    [{:keys [modal/dropdown key]}]
    (.selectN dropdown (dec (js/parseInt key))))

(def DEFAULT_PAGE_SIZE 9)

(defn page-count [item-count page-size]
  (.ceil js/Math (/ item-count page-size)))

(v/defclass numbered-list
            {:view/initial-state (fn [{:keys [ui/max-height on-selection default-selection items] :as this}]
                                   (when (and default-selection
                                              on-selection
                                              (not (neg? default-selection))
                                              (>= (bounded-count (inc default-selection) items)
                                                  (inc default-selection)))
                                     (some-> (nth items default-selection)
                                             :value
                                             (on-selection)))
                                   {:selection (or default-selection 0)
                                    :PAGE_SIZE (if max-height (-> (.floor js/Math (-> max-height
                                                                                      (- 32)
                                                                                      (/ 30)))
                                                                  (min 9)
                                                                  (max 1))
                                                              DEFAULT_PAGE_SIZE)
                                    :page      0})
             :down               (fn [{:keys [view/state items]}]
                                   (when (seq items)
                                     (let [{:keys [selection page PAGE_SIZE]} @state
                                           item-count (count items)
                                           page-count (page-count item-count PAGE_SIZE)
                                           last-page? (= page (dec page-count))
                                           current-max-selection (if last-page?
                                                                   (dec (- item-count (* page PAGE_SIZE)))
                                                                   (dec PAGE_SIZE))
                                           [selection page] (if (>= selection current-max-selection)
                                                              [0 (if last-page? 0 (inc page))]
                                                              [(min (inc selection) (dec PAGE_SIZE)) page])]
                                          (swap! state assoc :selection selection :page page))))
             :up                 (fn [{:keys [view/state items]}]
                                   (when (seq items)
                                     (let [{:keys [selection page PAGE_SIZE]} @state
                                           item-count (count items)
                                           [selection page] (if (<= selection 0)
                                                              (let [page (max 0 (dec page)) #_(max 0 (if (= page 0) (dec (page-count item-count PAGE_SIZE))
                                                                                                                    (dec page)))
                                                                    selection (min (dec PAGE_SIZE)
                                                                                   (dec (- item-count (* page PAGE_SIZE))))]
                                                                   [selection page])
                                                              [(dec selection) page])]
                                          (swap! state assoc :selection selection :page page))))
             :select-n           (fn [{:keys [on-select! view/state items]} n]
                                   (let [{:keys [page PAGE_SIZE]} @state
                                         offset (* PAGE_SIZE page)
                                         n (+ offset n)]
                                        (when (and (< n (count items))
                                                   (> n -1))
                                          (on-select! (:value (nth items n)))
                                          true)))
             :select             (fn [this]
                                   (let [selection (:selection @(:view/state this))]
                                        (.selectN this selection)))
             :view/did-update    (fn [{:as   this
                                       :keys [props prev-props]}]
                                   (when (not= props prev-props)
                                     (swap! (:view/state this) assoc :selection (or (:default-selection this) 0))
                                     (when (some? props)
                                       (swap! (:view/state this) assoc :last-items props)))
                                   ;; NOTE - was in will-receive-state
                                   (let [{:keys [view/state view/prev-state on-selection items]} this]
                                        (when (not= @state prev-state)

                                          (let [{:keys [selection page PAGE_SIZE]} @state
                                                item-i (+ selection (* page PAGE_SIZE))]
                                               (when (and on-selection
                                                          (not= selection (:selection prev-state)))
                                                 (on-selection (when (and (> item-i -1)
                                                                          (< item-i (count items)))
                                                                 (:value (nth items item-i)))))))))
             :view/did-mount     #(exec/set-context! {:modal/dropdown          %
                                                      :modal/numbered-dropdown (:numbered? %)})
             :view/will-unmount  #(exec/set-context! {:modal/dropdown          nil
                                                      :modal/numbered-dropdown nil})}
  [{:keys [view/state on-select! items class ui/max-height numbered?]}]
  (let [{:keys [selection page PAGE_SIZE]} @state
        waiting? (nil? items)
        mobile? (d/get :UI :mobile-width?)
        legend #(do [:span.o-70.monospace.gray.mh2.ph1.inline-flex.items-center.f7 %])
        items (if waiting? (:last-items @state) items)
        trigger-event (if mobile? :on-click
                                  :on-mouse-down)
        offset (* page PAGE_SIZE)
        less? (> page 0)
        more? (= (bounded-count (inc (+ offset PAGE_SIZE)) items) (inc (+ offset PAGE_SIZE)))
        page-count (page-count (count items) PAGE_SIZE)
        items (drop offset items)]
    (error/error-boundary {:on-error #(do (prn "View error: numbered-list")
                                          [:fixed])}
                          [:div.bg-white.br1.overflow-y-auto
                           {:class (str class " " (when waiting? "o-50"))
                            :style {:max-height (min (- max-height 32)
                                                     300)}}
                           (when waiting? [:.progress-indeterminate])
                           (->> (take PAGE_SIZE items)
                                (map-indexed (fn [i {:keys [value label]}]
                                               [:.nowrap.flex.items-center.pointer.items-stretch
                                                {:key (+ offset i)
                                                 :on-click #(swap! state assoc :selection i)
                                                 :class (str (when (= i selection) "bg-darken-lightly")
                                                             (when-not numbered? " pl2"))
                                                 :style {:border-bottom "1px solid rgba(0,0,0,0.05)"}
                                                 trigger-event #(do (on-select! value)
                                                                    (util/stop! %))}
                                                (when (and numbered?
                                                           (not mobile?))
                                                  (legend (if (< i 9) (inc i) " ")))
                                                label])))
                           #_(when more?
                               [:.tc.items-center.flex.items-stretch.o-50.hover-o-100
                                {:on-click #(swap! state update :page inc)}
                                (icons/style icons/ExpandMore {:transform "scale(0.7)"})])])))

