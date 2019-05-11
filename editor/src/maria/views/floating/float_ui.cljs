(ns maria.views.floating.float-ui
  (:require [chia.view :as v]
            [lark.commands.registry :refer-macros [defcommand]]
            [chia.db :as d]
            [chia.routing :as routing]
            [goog.events :as events]
            [maria.views.error :as error]
            [chia.reactive :as r])
  (:import [goog.events EventType]))

(defn tear-down! [{:keys [view/state]}]
                              (when-let [teardown (:teardown @state)]
                                (teardown)))

(defn setup-listener! [{:keys [view/state cancel-events] :as this
                                  :or   {cancel-events ["mousedown" "focus" "scroll"]}}]
  (tear-down! this)
                              (let [the-events (to-array cancel-events)
        this-node (v/dom-node this)
                                    callback (fn [e]
                   (when (and (not (routing/closest (.-target e) (partial = this-node)))
                                                          (or (not= (.-type e) "scroll")
                                                              (= (.-target e) js/document)))
                     (tear-down! this)
                                                 (d/transact! [[:db/retract-attr :ui/globals :floating-hint]])))]
                                (events/listen js/window the-events callback true)
    (r/silently (swap! state assoc :teardown #(do (events/unlisten js/window the-events callback true)
                                                  (r/silently (swap! state dissoc :teardown)))))))

(v/defclass FloatingContainer
  {:view/did-mount (fn [this] (setup-listener! this))
   :view/did-update (fn [{cancel-events :cancel-events
                                  {prev-cancel-events :cancel-events} :view/prev-props :as this}]
                              (when-not (= cancel-events prev-cancel-events)
                        (setup-listener! this)))
   :view/will-unmount (fn [this] (tear-down! this))}
  [{:keys [component props element float/offset]
    [left top] :float/pos
    :or   {offset [0 0]}}]
  [error/error-boundary
    {:on-error (fn [_ _])}
    (let [left (+ left (first offset))
          top (+ top (second offset))
          BOTTOM_PADDING (or (-> (.getElementById js/document "bottom-bar")
                                 (.-width)) 0)
          max-height (-> (.-innerHeight js/window)
                         (- (- top (.-scrollY js/window)))
                         (- BOTTOM_PADDING))]
      [:.absolute.z-9999
       {:style {:top  top
                :left left}}
       (or element
           (component (merge props
                             #:ui {:top        top
                                   :left       left
                                  :max-height max-height})))])])

(defn show-floating-view []
  (some-> (d/get :ui/globals :floating-hint)
          (FloatingContainer)))

(defn floating-view! [data]
  (d/transact! [[:db/add :ui/globals :floating-hint data]]))

(defn current-view [] (d/get :ui/globals :floating-hint))

(defn clear! []
  (d/transact! [[:db/retract-attr :ui/globals :floating-hint]]))

(defcommand :floating-ui/exit
  {:bindings ["Esc"]
   :private  true
   :when     #(d/contains? :ui/globals :floating-hint)}
  [context]
  (clear!))