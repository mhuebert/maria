(ns maria.views.floating.tooltip
  (:require [chia.view :as v]
            [maria.views.floating.float-ui :as ui]
            [goog.events :as events]
            [goog.dom.dataset :as data]
            [chia.routing :as routing]
            [maria.util :as util]
            [cljs.tools.reader.edn :as edn]
            [chia.reactive :as r]))

(v/defview Tooltip
  {:view/did-mount
   (fn [{:keys [view/state]}]
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
                                                     (routing/closest #(and (not= % js/document)
                                                                            (data/has % "tooltip")))))
                                     {:float/pos (util/rect->abs-pos (.getBoundingClientRect target)
                                                                     [:center :bottom])
                                      :float/offset [0 5]
                                      :content (edn/read-string (data/get target "tooltip"))}))))))]
       (events/listen body the-events callback)
       (r/silently
        (swap! state assoc :unlisten #(events/unlisten body the-events callback)))))
   :view/will-unmount #((:unlisten @(:view/state %)))}
  [{:keys [view/state]}]
  (when-let [{:keys [content float/pos float/offset]} (:tooltip @state)]
    (ui/FloatingContainer {:float/pos pos
                           :float/offset offset
                           :element [:.absolute.tc
                                     {:style {:width 150
                                              :left -75}}
                                     [:.pa1.br2.bg-black.white.f7.sans-serif.dib
                                      content]]})))

