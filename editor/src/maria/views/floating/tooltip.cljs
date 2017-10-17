(ns maria.views.floating.tooltip
  (:require [re-view.core :as v :refer [defview]]
            [maria.views.floating.float-ui :as ui]
            [re-db.d :as d]
            [goog.events :as events]
            [goog.dom.dataset :as data]
            [re-view.routing :as r]
            [maria.util :as util]
            [cljs.tools.reader.edn :as edn]))

(defview Tooltip
  {:view/did-mount    (fn [{:keys [view/state]}]
                        (let [the-events #js ["mouseover" "mouseout" "mousedown" "mouseenter" "mouseleave"]
                              body (.-body js/document)
                              callback (fn [e]
                                         (let [target (.-target e)
                                               body-event (#{"mouseenter" "mouseleave" "mousedown"} (.-type e))]
                                           (if body-event

                                             (swap! state dissoc :tooltip)
                                             (when (data/has target "tooltip")
                                               (swap! state assoc :tooltip
                                                      ;; mouseenter and mouseleave only trigger on currentTarget (body)
                                                      (when (or (= "mouseover" (.-type e))
                                                                (some-> (.-relatedTarget e)
                                                                        (r/closest #(and (not= % js/document)
                                                                                         (data/has % "tooltip")))))
                                                        {:float/pos    (util/rect->abs-pos (.getBoundingClientRect target)
                                                                                           [:center :bottom])
                                                         :float/offset [0 5]
                                                         :content      (edn/read-string (data/get target "tooltip"))}))))))]
                          (events/listen body the-events callback)
                          (v/swap-silently! state assoc :unlisten #(events/unlisten body the-events callback))))
   :view/will-unmount #((:unlisten @(:view/state %)))}
  [{:keys [view/state]}]
  (when-let [{:keys [content float/pos float/offset]} (:tooltip @state)]
    (ui/FloatingContainer {:float/pos    pos
                           :float/offset offset
                           :element      [:.absolute.tc
                                          {:style {:width 150
                                                   :left  -75}}
                                          [:.pa1.br1.bg-black.white.f7.sans-serif.dib content]]})))

