(ns lark.value-viewer.core
  (:require [goog.object :as gobj]
            [re-view.util :as v-util]
            [re-view.core :as v :refer [defview]]
            [re-view.hiccup.core :as hiccup])
  (:import [goog.async Deferred]))

(def space \u00A0)

(defn kind [thing]
  (cond
    (char? thing) :character
    (false? thing) :false
    (keyword? thing) :keyword
    (seq? thing) :sequence
    (list? thing) :list
    (map? thing) :map
    (var? thing) :var
    (fn? thing) :function
    (nil? thing) :nil
    (number? thing) :number
    (set? thing) :set
    (string? thing) :string
    (symbol? thing) :symbol
    (true? thing) :true
    (vector? thing) :vector
    (object? thing) :object
    (instance? Atom thing) :atom
    :else nil))

(def ArrowPointingDown
  [:svg {:fill "currentColor", :height "24", :view-box "0 0 24 24", :width "24", :xmlns "http://www.w3.org/2000/svg"}
   [:path {:d "M7 10l5 5 5-5z"}]
   [:path {:d "M0 0h24v24H0z", :fill "none"}]])

(def ArrowPointingUp
  (-> ArrowPointingDown
      (update-in [1 :style] assoc :transform "rotate(180deg)")))

(defn bracket-type [value]
  (cond (vector? value) ["[" "]"]
        (set? value) ["#{" "}"]
        (map? value) ["{" "}"]
        :else ["(" ")"]))

(defn wrap-value [[lb rb] value]
  [:.inline-flex.items-stretch
   [:.flex.items-start.nowrap lb]
   [:div.v-top value]
   [:.flex.items-end.nowrap rb]])

(extend-protocol hiccup/IEmitHiccup
  Keyword
  (to-hiccup [this] (str this)))

(declare format-value)

(defview display-deferred
  {:view/will-mount (fn [{:keys [deferred view/state]}]
                      (-> deferred
                          (.addCallback #(swap! state assoc :value %1))
                          (.addErrback #(swap! state assoc :error %))))}
  [{:keys [view/state]}]
  (let [{:keys [value error] :as s} @state]
    [:div
     [:.gray.i "goog.async.Deferred"]
     [:.pv3 (cond (nil? s) [:.progress-indeterminate]
                  error (str error)
                  :else (or (some-> value (format-value)) [:.gray "Finished."]))]]))

(def expander-outter :.dib.bg-darken.ph2.pv1.mh1.br2)
(def inline-centered :.inline-flex.items-center)

(def ^:dynamic *format-depth-limit* 3)

(defn expanded? [{:keys [view/state]} depth]
  (if (boolean? (:collection-expanded? @state))
    (:collection-expanded? @state)
    (and depth (< depth *format-depth-limit*))))

(defn toggle-depth [{:keys [view/state] :as this} depth label]
  (let [is-expanded? (expanded? this depth)
        class (if is-expanded?
                "cursor-zoom-out hover-bg-darken "
                "cursor-zoom-in gray hover-black")]
    [:.dib {:class    class
            :on-click #(swap! state assoc :collection-expanded? (not is-expanded?))} label]))

(defview format-collection
  {:view/initial-state {:limit-n              20
                        :collection-expanded? nil}}
  [{state :view/state :as this} depth value]
  (let [{:keys [limit-n]} @state
        [lb rb] (bracket-type value)
        more? (= (count (take (inc limit-n) value)) (inc limit-n))
        hover-class (if (even? depth) "hover-bg-darken" "hover-bg-lighten")]
    (cond (empty? value)
          (str space lb rb space)
          (expanded? this depth) [:.inline-flex.items-stretch
                                  {:class hover-class}
                                  [:.flex.items-start.nowrap (if (empty? value) (str space lb)
                                                                                (toggle-depth this depth (str space lb space)))]
                                  [:div.v-top (interpose " " (v-util/map-with-keys (partial format-value (inc depth)) (take limit-n value)))]
                                  (when more? [:.flex.items-end [expander-outter {:class    "pointer"
                                                                                  :on-click #(swap! state update :limit-n + 20)} "…"]])
                                  [:.flex.items-end.nowrap (str space rb space)]]
          :else [:.inline-flex.items-center.gray.nowrap
                 {:class hover-class} (toggle-depth this depth (str space lb "…" rb space))])))

(defview format-map
  {:view/initial-state {:limit-n              20
                        :collection-expanded? nil}}
  [{state :view/state :as this} depth value]
  (let [{:keys [limit-n]} @state
        [lb rb] (bracket-type value)
        more? (= (count (take (inc limit-n) value)) (inc limit-n))
        last-n (if more? limit-n (count value))
        hover-class (if (even? depth) "hover-bg-darken" "hover-bg-lighten")]
    (if (or (empty? value) (expanded? this depth))
      [:table.relative.inline-flex.v-mid
       {:class hover-class}
       [:tbody
        (or (some->> (seq (take limit-n value))
                     (map-indexed (fn [n [a b]]
                                    [:tr
                                     {:key n}
                                     [:td.v-top.nowrap
                                      (when (= n 0) (toggle-depth this depth (str space lb space)))]
                                     [:td.v-top
                                      (format-value (inc depth) a) space]
                                     [:td.v-top
                                      (format-value (inc depth) b)]
                                     [:td.v-top.nowrap (when (= (inc n) last-n) (str space rb space))]])))
            [:tr [:td.hover-bg-darken.nowrap (str space lb rb space)]])
        (when more? [:tr [:td {:col-span 2}
                          [expander-outter {:on-click #(swap! state update :limit-n + 20)} [inline-centered "…"]]]])]]
      [:.inline-flex.items-center.gray
       {:class hover-class} (toggle-depth this depth (str space lb "…" rb space))])))

(defview format-function
  {:view/initial-state (fn [_ value] {:expanded? false})}
  [{:keys [view/state]} value]
  (let [{:keys [expanded?]} @state
        fn-name (gobj/get value "name" "ƒ")]
    [:span
     [expander-outter {:on-click #(swap! state update :expanded? not)}
      [inline-centered  [:span.o-50.mr1 fn-name]
       (-> (if expanded? ArrowPointingUp
                         ArrowPointingDown)
           (update 1 assoc :width 20 :height 20 :class "mln1 mrn1 o-50"))]
      (when expanded?
        (.toString value))]]))

(defprotocol IView
  (view [this] "Returns a view for `this`"))

(defn format-value
  ([value] (format-value 1 value))
  ([depth value]

   (when (> depth 200)
     (prn value)
     (throw (js/Error. "Format depth too deep!")))
   (cond (v/is-react-element? value) value
         (satisfies? IView value) (format-value depth (view value))
         (satisfies? hiccup/IEmitHiccup value) value
         :else
         (case (kind value)
           (:vector
             :sequence
             :set) (format-collection depth value)

           :map (format-map depth value)

           :var [:div
                 [:.o-50.mb2 (str value)]
                 (format-value depth @value)]

           :nil "nil"

           :function (format-function value)

           :atom (wrap-value [[:span.gray.mr1 "#Atom"] nil]
                             (format-value depth (gobj/get value "state")))

           (cond
             (v/is-react-element? value) value
             (instance? cljs.core/Namespace value) (str value)
             (instance? Deferred value) (display-deferred {:deferred value})
             :else (try (pr-str value)
                        (catch js/Error e
                          (do "error printing result"
                              (.log js/console e)
                              (prn (type value))
                              (prn :kind (kind value))
                              (.log js/console value)
                              (prn value)))))))))
