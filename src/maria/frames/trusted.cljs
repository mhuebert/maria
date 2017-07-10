(ns maria.frames.trusted
  (:require
    [cljsjs.react]
    [cljsjs.react.dom]
    [re-view-routing.core :as routing]
    [re-view.core :as v :refer [defview]]
    [maria.frames.trusted-routes :as routes]
    [re-db.d :as d]))

(enable-console-print!)

(defview layout []
  (routes/match-route-segments (d/get :router/location :segments)))

(defonce _
         (do
           (routing/listen #(d/transact! [(assoc % :db/id :router/location)]))
           (d/transact! [{:db/id         "intro"
                          :default-value ";; intro"}])
           (v/render-to-dom (layout) "maria-index")))

