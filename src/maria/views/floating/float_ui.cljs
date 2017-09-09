(ns maria.views.floating.float-ui
  (:require [re-view.core :as v :refer [defview]]
            [commands.registry :refer-macros [defcommand]]
            [re-db.d :as d]
            [re-view-routing.core :as r]
            [goog.events :as events])
  (:import [goog.events EventType]))

(def BOTTOM_PADDING 30)

(defview FloatingContainer
  {:view/did-mount    (fn [{:keys [view/state cancel-events] :as this}]
                        (let [callback (fn [e]
                                         (when-not (r/closest (.-target e) (partial = (v/dom-node this)))
                                           (d/transact! [[:db/retract-attr :ui/globals :floating-hint]])))]
                          (events/listen js/window (or (some-> cancel-events (to-array))
                                                       #js ["mousedown" "blur"]) callback true)
                          (v/swap-silently! state assoc :callback callback)))
   :view/will-unmount (fn [{:keys [view/state cancel-events]}]
                        (events/unlisten js/window (or (some-> cancel-events (to-array))
                                                       #js ["mousedown" "blur"]) (:callback @state) true))}
  [{:keys [rect component props]}]
  (let [scroll-y (.-scrollY js/window)
        inner-height (.-innerHeight js/window)
        top (+ (.-bottom rect)
               scroll-y
               10)
        left (+ (.-left rect) (.-scrollX js/window))
        max-height (-> inner-height
                       (- (- top (.-scrollY js/window)))
                       (- BOTTOM_PADDING))]
    [:.absolute.z-9999
     {:style {:top        top
              :left       left}}
     (component (merge props
                       #:ui {:top top
                             :left left
                             :max-height max-height}))]))

(defn display-hint []
  (some-> (d/get :ui/globals :floating-hint)
          (FloatingContainer)))

(defn floating-hint! [{:keys [rect element] :as content}]
  (d/transact! [[:db/add :ui/globals :floating-hint content]]))

(defn current-hint [] (d/get :ui/globals :floating-hint))

(defn clear-hint! []
  (d/transact! [[:db/retract-attr :ui/globals :floating-hint]]))

(defcommand :floating-ui/exit
  {:bindings ["Esc"]
   :private  true
   :when     #(d/contains? :ui/globals :floating-hint)}
  [context]
  (clear-hint!))