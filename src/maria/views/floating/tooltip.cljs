(ns maria.views.floating.tooltip
  (:require [re-view.core :as v :refer [defview]]
            [maria.views.floating.float-ui :as ui]
            [re-db.d :as d]
            [goog.events :as events]
            [goog.dom.dataset :as data]
            [re-view-routing.core :as r]))

(defview Tooltip
  {:view/did-mount    (fn [{:keys [view/state]}]
                        (let [the-events #js ["mouseover" "mouseout"  "mouseenter" "mouseleave"]
                              body (.-body js/document)
                              callback (fn [e]
                                         (let [target (.-target e)]
                                           (if (#{"mouseenter" "mouseleave"} (.-type e))
                                             (swap! state dissoc :tooltip)
                                             (when (data/has target "tooltip")
                                               (swap! state assoc :tooltip
                                                      ;; mouseenter and mouseleave only trigger on currentTarget (body)
                                                      (when (or (= "mouseover" (.-type e))
                                                                (some-> (.-relatedTarget e)
                                                                        (r/closest #(and (not= % js/document)
                                                                                         (data/has % "tooltip")))))
                                                        (let [rect (.getBoundingClientRect target)]
                                                          {:rect rect
                                                           :offset [(/ (.-width rect) 2) 5]
                                                           :title (data/get target "tooltip")})))))))]
                          (events/listen body the-events callback)
                          (v/swap-silently! state assoc :unlisten #(events/unlisten body the-events callback))))
   :view/will-unmount #((:unlisten @(:view/state %)))}
  [{:keys [view/state]}]
  (when-let [{:keys [title rect offset]} (:tooltip @state)]
    (ui/FloatingContainer {:rect    rect
                           :offset  offset
                           :element [:.absolute.tc
                                     {:style {:width 150
                                              :left  -75}}
                                     [:.pa1.br1.bg-white.f7.sans-serif.gray.dib title]]})))

