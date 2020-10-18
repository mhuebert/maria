(ns chia.view.core-test
  (:require [cljs.test :refer [deftest is are testing]]
            [chia.view :as v]
            [chia.view.legacy :as legacy]
            [chia.view.util :as u]
            [goog.object :as gobj]
            [goog.dom :as gdom]
            ["react-dom" :as react-dom]))


(defonce render-count (atom nil))

(defonce lifecycle-log (atom nil))

(defonce test-element (u/find-or-append-element "chia-view-test"))

(defn test-start! []
  (reset! render-count 0)
  (reset! lifecycle-log {})
  (v/unmount-from-dom test-element))

(defn log-args [method this]
  (swap! lifecycle-log assoc method (assoc (select-keys this [:view/props
                                                              :view/prev-props
                                                              :view/prev-state
                                                              :view/children])
                                      :view/state (some-> (:view/state this) (deref))))
  true)

(legacy/defclass apple
  {:view/initial-state (fn [this]
                         (log-args :view/initial-state this)
                         {:eaten? false})

   :view/did-mount #(log-args :view/did-mount %1)

   :static/get-derived-state-from-props
   (fn [props $state]
     (swap! lifecycle-log assoc :static/get-derived-state-from-props
            {:view/props (gobj/get $state "props")
             :view/prev-props (gobj/get $state "prev-props")
             :view/prev-state (gobj/get $state "prev-state")
             :view/children (gobj/get $state "children")
             :view/state (some-> (.-state $state) (deref))})
     $state)

   :view/will-receive-state #(log-args :view/will-receive-state %1)

   :view/should-update #(log-args :view/should-update %1)

   :view/did-update #(log-args :view/did-update %1)

   :view/will-unmount #(log-args :view/will-unmount %1)
   :pRef (fn [& args]
           (println "I am a ref that was called!" args))}
  [{:keys [view/state] :as this} _]
  (log-args :view/render this)
  (swap! render-count inc)
  [:div "I am an apple."
   (when-not (:eaten? @state)
     [:p {:ref #(when % (swap! state assoc :p %))
          :style {:font-weight "bold"}} " ...and I am brave and alive."])])


;; a heavily logged component

(def util (.. react-dom -__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED -ReactTestUtils))
(def init-child [:div {:style {:width 100
                               :height 100
                               :background "red"
                               :border-radius 100}}])


(deftest basic

  (test-start!)

  (let [render #(let [c (v/render-to-dom (apple %1 %2) test-element)]
                     #_ (v/flush!)
                      c)
        c (render {:color "red"} init-child)]

    (testing "initial state"
      (is (false? (:eaten? @(:view/state c))))
      (is (= 1 @render-count))
      (is (= "red" (get-in @lifecycle-log [:view/initial-state :view/props :color]))
          "Read props from GetInitialState")
      (is (= "red" (get-in c [:view/props :color]))
          "Read props"))

    (testing "update state"

      ;; Update State
      (swap! (:view/state c) update :eaten? not)
      (v/flush!)

      (is (true? (:eaten? @(:view/state c)))
          "State has changed")
      (is (= 2 @render-count)
          "Component was rendered"))

    (testing "render with new props"

      (render {:color "green"} nil)
      (is (= "green" (:color c))
          "Update Props")
      (is (= 3 @render-count)
          "Force rendered"))

    (testing "children"

      (render {} [:div "div"])
      (is (= 1 (count (:view/children c)))
          "Has one child")
      (is (= :div (ffirst (:view/children c)))
          "Read child")

      (render {} [:p "Paragraph"])
      (is (= :p (ffirst (:view/children c)))
          "New child - force render")

      (render nil [:span "Span"])
      (is (= :span (ffirst (:view/children c)))
          "New child - normal render"))

    (testing "refs"
      (is (= "bold" (-> (:p @(:view/state c))
                        .-style
                        .-fontWeight))
          "Read react ref"))))
