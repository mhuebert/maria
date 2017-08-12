(ns maria.frames.trusted-frame
  (:require
    [re-view-routing.core :as routing]
    [re-view.core :as v :refer [defview]]
    [maria.frames.trusted-routes :as routes]
    [re-db.d :as d]))

(enable-console-print!)

(defview remote-progress []
  [:div {:style {:width    "50%"
                 :height   10
                 :left     0
                 :top      0
                 :position "absolute"}}

   (when (> (d/get :remote/status :in-progress) 0)
     [:.progress-indeterminate])])

(defview layout []
  [:div {:style {:width  "100%"
                 :height "100%"}}
   (remote-progress)
   (routes/match-route-segments (d/get :router/location :segments))])

(defonce _
         (do
           (routing/listen #(d/transact! [(assoc % :db/id :router/location)]))
           (d/listen {:ea_ [[:window :title]]} #(set! (.-title js/document) (or (d/get :window :title) "Maria")))
           (v/render-to-dom (layout) "maria-index")))

