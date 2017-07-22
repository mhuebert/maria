(ns re-view-material.ext
  ;; components for easier use of re-view-material but not included in official MDC repo
  (:require [re-view.core :as v :refer [defview]]
            [re-view-material.util :as util]
            [re-view.util :as v-util]
            [goog.dom.classes :as classes]
            [goog.dom :as gdom]))


(defview WithTrigger
  "Wraps component to accept an additional (first) child argument, a 'trigger'.
  When trigger is clicked, .open() is called on the component.
  Component and trigger are wrapped in an otherwise inert span.

  :component - will be opened (via `.open`) on click of trigger
  :container-classes - vector of class strings to be added to container span."
  [{:keys [view/props view/state component container-classes]} & args]
  (let [[trigger & items] (v-util/flatten-seqs args)]
    [:span
     {:classes  container-classes

      ;; use :ref to get DOM element of trigger.
      :ref      #(when % (swap! state assoc :Trigger (gdom/getFirstElementChild %)))
      :on-click (fn [e]
                  (let [Trigger (:Trigger @state)]
                    ;; check if click was on or inside trigger
                    (when (util/closest (.-target e) #(= % Trigger))
                      (.open (:Component @state)))))}
     trigger
     (apply component
            (-> props
                (dissoc :container-classes :component)
                ;;
                (update :style merge {:z-index 999})
                (assoc :ref #(when % (swap! state assoc :Component %))))
            items)]))

(defn with-trigger
  "Partially apply WithTrigger to component, keeping original component's name and docstring."
  ([component] (with-trigger component {}))
  ([component props]
   (v/partial WithTrigger {:react-keys {:display-name (-> (aget component "reViewBase")
                                                          :react-keys
                                                          :display-name
                                                          (str "WithTrigger"))}}
              (assoc props :component component))))



#_(defview Menu
    "Menus appear above all other in-app UI elements and appear on top of the triggering element.

    trigger is the element that will cause the menu to open (on click).

    items should be instances of ui/SimpleMenuItem.

    [Usage docs](https://material.io/guidelines/components/menus.html#menus-behavior)"
    [{:keys [view/props view/state trigger-anchor?]
      :or   {trigger-anchor? true}} trigger & items]
    [:span
     {:ref      #(when %
                   (swap! state assoc :Trigger (gdom/getFirstElementChild %)))
      :style    {:z-index 999}
      :class    (when trigger-anchor? "mdc-menu-anchor")
      :on-click (fn [e]
                  (let [Trigger (:Trigger @state)]
                    (when (util/closest (.-target e) #(= % Trigger))
                      (.open (:SimpleMenu @state)))))}
     trigger
     (apply ui/SimpleMenuBase
            (-> props
                (dissoc :trigger-anchor?)
                (assoc :ref #(when % (swap! state assoc :SimpleMenu %))))
            items)])





