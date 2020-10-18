(ns chia.view.bench
  (:require
   ["benchmark" :as b]
   ["react" :as react :refer [Fragment] :rename {createElement rce}]
   ["react-dom/server" :as rdom]
   [hx.react :as hx]
   [reagent.core :as reagent]
   [rum.core :as rum]
   [fulcro.client.dom :as fulcro-dom]
   [cljs.test :as t]
   [chia.view :as v]
   [chia.view.legacy :as legacy]
   [chia.view.hiccup :as hiccup]
   [sablono.interpreter :as sab]
   [uix.compiler.reagent :as uix]
   #_[re-view.core :as rv])
  (:require-macros [chia.view.bench :refer [hicada]]))

(def element react/createElement)
(def to-string rdom/renderToString)

(def sample-props {:style {:font-size 10}
                   :class "red"})

(defn reagent-render [{:keys [title body items]}]
  (reagent/as-element
   [:div.card
    [:div.card-title title]
    [:div.card-body body]
    [:ul.card-list
     (for [item items]
       ^{:key item} [:li item])]
    [:div.card-footer
     [:div.card-actions
      [:button "ok"]
      [:button "cancel"]]]]))

(defn uix-interpret [{:keys [title body items]}]
  (uix/as-element
   [:div.card
    [:div.card-title title]
    [:div.card-body body]
    [:ul.card-list
     (for [item items]
       ^{:key item} [:li item])]
    [:div.card-footer
     [:div.card-actions
      [:button "ok"]
      [:button "cancel"]]]]))

#_(defn shadow-render [{:keys [title body items]}]
    (to-string
     (shadow/<<
      [:div.card
       [:div.card-title title]
       [:div.card-body body]
       [:ul.card-list
        (shadow/render-seq
         items
         identity
         (fn [item]
           (shadow/<< [:li item])))]
       [:div.card-footer
        [:div.card-actions
         [:button "ok"]
         [:button "cancel"]]]])))

(defn react-render [{:keys [title body items]}]

  (element "div" #js {:className "card"}
           (element "div" #js {:className "card-title"} title)
           (element "div" #js {:className "card-body"} body)
           (element "ul" #js {:className "card-list"}
                    (.apply element
                            nil
                            (reduce (fn [out item]
                                      (doto out (.push (element "li" #js {} item))))
                                    #js[Fragment nil] items)))
           (element "div" #js {:className "card-footer"}
                    (element "div" #js {:className "card-actions"}
                             (element "button" nil "ok")
                             (element "button" nil "cancel")))))

(v/defview chia-view [{:keys [title body items]}]
  [:div.card
   [:div.card-title title]
   [:div.card-body body]
   [:ul.card-list
    (for [item items]
      [:li {:key item} item])]
   [:div.card-footer
    [:div.card-actions
     [:button "ok"]
     [:button "cancel"]]]])

#_(rlegacy/defclass re-view [{:keys [title body items]}]
              [:div.card
               [:div.card-title title]
               [:div.card-body body]
               [:ul.card-list
                (for [item items]
                  [:li {:key item} item])]
               [:div.card-footer
                [:div.card-actions
                 [:button "ok"]
                 [:button "cancel"]]]])

(legacy/defclass chia-legacy [{:keys [title body items]}]
                [:div.card
   [:div.card-title title]
   [:div.card-body body]
   [:ul.card-list
    (for [item items]
      [:li {:key item} item])]
   [:div.card-footer
    [:div.card-actions
     [:button "ok"]
     [:button "cancel"]]]])

(defn chia-hiccup [{:keys [title body items]}]
  (hiccup/element
   [:div.card
    [:div.card-title title]
    [:div.card-body body]
    [:ul.card-list
     (for [item items]
       [:li {:key item} item])]
    [:div.card-footer
     [:div.card-actions
      [:button "ok"]
      [:button "cancel"]]]]))

(defn hx-render [{:keys [title body items]}]
  (hx/f
   [:div {:class "card"}
    [:div {:class "card-title"} title]
    [:div {:class "card-body"} body]
    [:ul {:class "card-list"}
     (for [item items]
       [:li {:key item} item])]
    [:div {:class "card-footer"}
     [:div {:class "card-actions"}
      [:button "ok"]
      [:button "cancel"]]]]))

(rum/defc rum-render [{:keys [title body items]}]
          [:div.card
           [:div.card-title title]
           [:div.card-body body]
           [:ul.card-list
            (for [item items]
              [:li {:key item} item])]
           [:div.card-footer
            [:div.card-actions
             [:button "ok"]
             [:button "cancel"]]]])

(defn sablono-interpret [{:keys [title body items]}]
  (sab/interpret
   [:div.card
    [:div.card-title title]
    [:div.card-body body]
    [:ul.card-list
     (for [item items]
       [:li {:key item} item])]
    [:div.card-footer
     [:div.card-actions
      [:button "ok"]
      [:button "cancel"]]]]))

(defn hicada-render [{:keys [title body items]}]
  (hicada
   [:div.card
    [:div.card-title title]
    [:div.card-body body]
    [:ul.card-list
     (for [item items]
       ^{:key item} [:li item])]
    [:div.card-footer
     [:div.card-actions
      [:button "ok"]
      [:button "cancel"]]]]))


#_(defn fulcro-dom-render [{:keys [title body items]}]
    (fulcro-dom/div {:className "card"}
                    (fulcro-dom/div {:className "card-title"} title)
                    (fulcro-dom/div {:className "card-body"} body)
                    (fulcro-dom/ul {:className "card-list"}
                                   (shadow/render-seq
                                    items
                                    identity
                                    (fn [item]
                                      (fulcro-dom/li {} item))))
                    (fulcro-dom/div {:className "card-footer"}
                                    (fulcro-dom/div {:className "card-actions"}
                                                    (fulcro-dom/button "ok")
                                                    (fulcro-dom/button "cancel")))))

(defn log-cycle [event]
  (println (.toString (.-target event))))

(defn ^:dev/after-load main [& args]
  (let [test-data {:title "hello world"
                   :body  "body"
                   :items (shuffle (range 10))}
        suite (b/Suite.)]
    (aset js/window "Benchmark" suite)
    #_(println "chia")
    #_(println (chia-view test-data))
    #_(println "react")
    #_(println (react-render test-data))
    #_(println "reagent")
    #_(println (reagent-render test-data))
    #_(println "hx")
    #_(println (hx-render test-data))
    #_(println "rum")
    #_(println (rum-render test-data))
    ;(js/console.profile "chia")
    ;(dotimes [n 10000] (to-string (chia-view test-data)))
    ;(js/console.profileEnd)

    #_(when-not (= (to-string (react-render test-data))
                   (to-string (reagent-render test-data))
                   (to-string (chia-view test-data))
                   (to-string (chia-hiccup test-data))
                   (to-string (re-view test-data))
                   (to-string (hx-render test-data))
                   (to-string (rum-render test-data))
                   (to-string (sablono-interpret test-data))
                   (to-string (uix-interpret test-data))
                   (to-string (hicada-render test-data)))
        (doseq [[n x] {'react-render      (to-string (react-render test-data))
                       'reagent-render    (to-string (reagent-render test-data))
                       'chia-view         (to-string (chia-view test-data))
                       'chia-hiccup       (to-string (chia-hiccup test-data))
                       're-view           (to-string (re-view test-data))
                       'hx-render         (to-string (hx-render test-data))
                       'rum-render        (to-string (rum-render test-data))
                       'sablono-interpret (to-string (sablono-interpret test-data))
                       'uix-interpret     (to-string (uix-interpret test-data))
                       'hicada-render     (to-string (hicada-render test-data))}]
          (print n)
          (print x))
        (throw (ex-info "not equal!" {})))
    (print :react "\n" (to-string (react-render test-data)))
    (print :chia-legacy "\n" (to-string (chia-legacy test-data)))

    (-> suite

        (.add "reagent/interpret" (comp to-string #(reagent-render test-data)))
        #_(.add "uix/interpret" (comp to-string #(uix-interpret test-data)))


        (.add "chia-legacy/interpret" (comp to-string #(chia-legacy test-data)))
        (.add "chia-wrapped/interpret" (comp to-string #(chia-view test-data)))
        (.add "chia-hiccup/interpret" (comp to-string #(chia-hiccup test-data)))


        #_(.add "re-view/interpret" (comp to-string #(re-view test-data)))
        #_(.add "react" (comp to-string #(react-render test-data)))
        #_(.add "rum/macro" (comp to-string #(rum-render test-data)))
        #_(.add "hicada/macro" (comp to-string #(hicada-render test-data)))
        #_(.add "sablono/interpret" (comp to-string #(sablono-interpret test-data)))
        #_(.add "hx/interpret" (comp to-string #(hx-render test-data)))




        #_(.add "shadow" #(shadow-render test-data))
        #_(.add "fulcro-dom" (comp to-string #(fulcro-dom-render test-data)))
        (.on "cycle" log-cycle)
        (.run))))

(defonce _ (main))


