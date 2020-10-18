(ns chia.view.hiccup-test
  (:require [cljs.test :refer [deftest is are testing]]
            ["react" :as react]
            ["react-dom" :as react-dom]
            [chia.view.hiccup :refer [element]]
            [chia.view.hiccup.impl :as hiccup]))

(enable-console-print!)

(defn element-args [form]
  (let [tag (nth form 0)
        props (nth form 1 nil)
        props? (or (nil? props) (map? props))
        parsed-key (hiccup/parse-key-memo (name tag))]
    (-> (into [(.-tag parsed-key) (hiccup/props->js parsed-key (when props? props))] (drop (if props? 2 1) form))
        (update 1 js->clj :keywordize-keys true))))

(deftest hiccup-test


  (testing "Parse props"

    (is (= (element-args [:h1#page-header])
           ["h1" {:id "page-header"}])
        "Parse ID from element tag")

    (is (= ["div" {:className "red"}]
           (element-args [:div.red])
           (element-args [:div {:class "red"}])
           #_(element-args [:div {:classes ["red"]}]))
        "Two ways to specify a class")

    (is (= ["div" {:className "red"}]
           (element-args [:div.red]))
        "Two ways to specify a class")

    (is (= (element-args [:.red {:class "white black"
                                 #_#_:classes ["purple"]}])
           ["div" {:className "red white black"}])
        "Combine classes from element tag and :class")

    (is (= (element-args [:.red])
           ["div" {:className "red"}])
        "If tag name is not specified, use a `div`")

    (is (= (element-args [:div {:data-collapse true
                                :aria-label    "hello"}])
           ["div" {:data-collapse true
                   :aria-label    "hello"}])
        "Do not camelCase data- and aria- attributes")

    (is (= (element-args [:div {:some/attr true
                                :someAttr  "hello"}])
           ["div" {:someAttr "hello"}])
        "Elide namespaced attributes")

    (is (= (element-args [:div {:style {:font-family "serif"
                                        :custom-attr "x"}}])
           ["div" {:style {:fontFamily "serif"
                           :customAttr "x"}}])
        "camelCase ALL style attributes")

    (is (= (element-args [:custom-element])
           ["custom-element" {}])
        "Custom element tag")



    (is (= (element-args [:#el.pink {:data-collapse true
                                     :aria-label    "hello"
                                     :class         "bg-black"
                                     #_#_:classes ["white"]
                                     :style         {:font-family "serif"
                                                     :font-size   12}}])
           ["div" {:data-collapse true
                   :aria-label    "hello"
                   :className     "pink bg-black"
                   :style         {:fontFamily "serif"
                                   :fontSize   12}
                   :id            "el"}])
        "All together")))
