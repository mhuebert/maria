(ns maria.views.floating.float-ui
  (:require [re-view.core :as v :refer [defview]]
            [commands.registry :refer-macros [defcommand]]
            [re-db.d :as d]
            [re-view-routing.core :as r]
            [goog.events :as events])
  (:import [goog.events EventType]))

(def BOTTOM_PADDING 30)

(defview FloatingContainer
  {:teardown                (fn [{:keys [view/state]}]
                              (when-let [teardown (:teardown @state)]
                                (teardown)))
   :setupListener           (fn [{:keys [view/state cancel-events] :as this
                                  :or   {cancel-events ["mousedown" "blur"]}}]
                              (.teardown this)
                              (let [the-events (to-array cancel-events)
                                    callback (fn [e]
                                               (when-not (r/closest (.-target e) #(= % (.-currentTarget e)))
                                                 (d/transact! [[:db/retract-attr :ui/globals :floating-hint]])))]
                                (events/listen js/window the-events callback true)
                                (v/swap-silently! state assoc :teardown #(events/unlisten js/window the-events callback true))))
   :view/did-mount          (fn [this] (.setupListener this))
   :view/will-receive-props (fn [{cancel-events                       :cancel-events
                                  {prev-cancel-events :cancel-events} :view/prev-props :as this}]
                              (when-not (= cancel-events prev-cancel-events)
                                (.setupListener this)))
   :view/will-unmount       (fn [this] (.teardown this))}
  [{:keys [rect component props element offset]
    :or {offset [0 10]}}]

  (let [scroll-y (.-scrollY js/window)
        inner-height (.-innerHeight js/window)
        top (+ (.-bottom rect)
               scroll-y
               (second offset))
        left (+ (.-left rect)
                (.-scrollX js/window)
                (first offset))
        max-height (-> inner-height
                       (- (- top (.-scrollY js/window)))
                       (- BOTTOM_PADDING))]
    [:.absolute.z-9999
     {:style {:top  top
              :left left}}
     (or element
         (component (merge props
                           #:ui {:top        top
                                 :left       left
                                 :max-height max-height})))]))

(defn display-hint []
  (some-> (d/get :ui/globals :floating-hint)
          (FloatingContainer)))

(defn floating-hint! [data]
  (d/transact! [[:db/add :ui/globals :floating-hint data]]))

(defn current-hint [] (d/get :ui/globals :floating-hint))

(defn clear-hint! []
  (d/transact! [[:db/retract-attr :ui/globals :floating-hint]]))

(defcommand :floating-ui/exit
  {:bindings ["Esc"]
   :private  true
   :when     #(d/contains? :ui/globals :floating-hint)}
  [context]
  (clear-hint!))