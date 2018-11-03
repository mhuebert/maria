(ns maria.frames.trusted-frame
  (:require
   [chia.routing :as routing]
   [chia.view :as v]
   [maria.frames.trusted-routes :as routes]
   [chia.triple-db :as d]
   [chia.reactive :as reactive]))

(v/defview layout []
  [:div {:style {:width "100%"
                 :height "100%"}}
   #_(remote-progress)
   (routes/match-route-segments (d/get :router/location :segments))])

(defonce _
         (do
           (routing/listen #(d/transact! [(assoc % :db/id :router/location)]))
           (d/listen #(set! (.-title js/document) (or (d/get :window :title) "Maria"))
                     {:ea_ [[:window :title]]})
           (v/render-to-dom (layout) "maria-index")))

