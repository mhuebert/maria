(ns maria.views.floating-hint
  (:require [re-view.core :as v :refer [defview]]
            [re-db.d :as d]
            [re-view-routing.core :as r]
            [goog.events :as events]))

(defview hint-view
  {:view/did-mount    (fn [{:keys [view/state] :as this}]
                        (let [callback (fn [e]
                                         (when-not (r/closest (.-target e) (partial = (v/dom-node this)))
                                           (d/transact! [[:db/retract-attr :ui/globals :floating-hint]])))]
                          (events/listen js/window #js ["click" #_"resize" #_"scroll" "keydown"] callback)
                          (v/swap-silently! state assoc :callback callback)))
   :view/will-unmount (fn [{:keys [view/state]}]
                        (events/unlisten js/window #js ["click" #_"resize" #_"scroll" "keydown"] (:callback @state)))}
  [{:keys [rect element]}]
  (let [top (+ (.-bottom rect)
               (.-scrollY js/window)
               10)
        left (+ (.-left rect) (.-scrollX js/window))]
    [:.absolute.z-999
     {:style {:top  top
              :left left}}
     element]))

(defn display-hint []
  (some-> (d/get :ui/globals :floating-hint)
          (hint-view)))

(defn floating-hint! [{:keys [rect element] :as content}]
  (d/transact! [[:db/add :ui/globals :floating-hint content]]))

(defn hide-hint! []
  (d/transact! [[:db/retract-attr :ui/globals :floating-hint]]))