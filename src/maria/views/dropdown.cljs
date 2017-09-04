(ns maria.views.dropdown
  (:require [re-view.core :as v :refer [defview]]
            [goog.events :as events]
            [maria.util :as util]
            [re-db.d :as d]))

(defn list-item [] (keyword (str (if (d/get :UI :mobile-width?) ".pa3" ".pa2") ".nowrap.flex.items-center.pointer")))

(defview numbered-list
  {:view/initial-state {:selection 0}
   :view/will-receive-props
                       (fn [{[prev-items] :view/prev-children
                             [items]      :view/children
                             :as          this}]
                         (when (not= items prev-items)
                           (swap! (:view/state this) assoc :selection 0))
                         (when-not (nil? items)
                           (swap! (:view/state this) assoc :last-items items)))
   :view/did-mount     (fn [{:keys [view/state] :as this}]
                         (swap! state assoc :listener
                                (events/listen js/window "keydown"
                                               (fn [e]
                                                 (let [keycode (.-keyCode e)
                                                       items (first (:view/children this))
                                                       selection (:selection @state)
                                                       a-number (when (and (not (.-shiftKey e))

                                                                           (> keycode 48)
                                                                           (< keycode 58))
                                                                  (- keycode 48))
                                                       activate-index #(let [action (:action (nth items %))]
                                                                         (action))]
                                                   (if a-number
                                                     (do (util/stop! e)
                                                         (if (<= a-number (count items))
                                                           (activate-index (dec a-number))))
                                                     (case keycode
                                                       ;; UP
                                                       38 (do (util/stop! e)
                                                              (swap! state assoc :selection
                                                                     (if (<= selection 0)
                                                                       (max 0 (dec (count items)))
                                                                       (max 0 (dec selection)))))
                                                       ;; DOWN
                                                       40 (do (util/stop! e)
                                                              (swap! state assoc :selection
                                                                     (if (>= selection (dec (count items)))
                                                                       0
                                                                       (min (inc selection)
                                                                            (dec (count items))))))

                                                       ;; right
                                                       39 (when (and (not (.-ctrlKey e))
                                                                     (not (.-altKey e)))
                                                            (util/stop! e)
                                                            (activate-index selection))

                                                       13 (do (util/stop! e)
                                                              (activate-index selection))

                                                       nil))))
                                               true)))
   :view/will-unmount  #(events/unlistenByKey (:listener @(:view/state %)))}
  [{:keys [bg view/state] :as this} items]
  (let [bg-selected "#ddd"
        {:keys [selection]} @state
        waiting? (nil? items)
        mobile? (d/get :UI :mobile-width?)
        legend #(do [:span.mr2.o-70
                     {:style {:color "#000"}}
                     [:span (when waiting? {:class "o-50"}) %]])
        items (if waiting? (:last-items @state) items)
        trigger-event (if mobile? :on-click
                                  :on-mouse-down)]
    [:div
     {:class (when waiting? "o-50")}
     (when waiting? [:.progress-indeterminate])
     (->> items
          (map-indexed (fn [i {:keys [action label]}]
                         [(list-item)
                          {:key            i
                           :on-mouse-enter #(swap! state assoc :selection i)
                           :style          {:background-color (when (= i selection) bg-selected)
                                            :border-bottom    "1px solid rgba(0,0,0,0.05)"}
                           trigger-event   (comp action util/stop!)}
                          (when-not mobile? (legend (inc i)))
                          label])))]))
