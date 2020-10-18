(ns chia.view.routing-test
  (:require [chia.view.util :refer [find-or-append-element]]
            [chia.view.legacy :as vlegacy :refer [defclass]]
            [cljs.test :refer [deftest is are testing]]
            [chia.db :as d]
            [chia.routing :as routing]
            [cljs.core.match :refer-macros [match]]
            [chia.view :as v]))

(enable-console-print!)

;; log segments -- tests usage of routing by itself
(defonce segments-log (atom nil))

;; log view renders -- tests usage of routing in combination with chia.view and chia.db
(defonce view-log (atom nil))

(defonce unlisten-key (atom nil))

(defonce test-element (find-or-append-element "routing-test"))

(defn test-setup! []
  (v/unmount-from-dom test-element)
  (v/flush!)
  (reset! segments-log [])
  (reset! view-log [])
  (when @unlisten-key
    (routing/unlisten @unlisten-key))
  (reset! unlisten-key
          (routing/listen (fn [{:keys [segments] :as location}]
                            ;; log segments
                            (swap! segments-log conj segments)
                            ;; write location to chia.db
                            ;; (triggers render of views that reference this data)
                            (d/transact! [(assoc location :db/id :router/location)])))))

(defclass index
          [this]
          (swap! view-log conj :index)
          [:div])

(defclass not-found [this]
          (swap! view-log conj :not-found)
          [:div])

(defclass page [{:keys [page-id]}]
          (swap! view-log conj [:page page-id])
          [:div])

(defclass root [_]
          ;; using core.match to pattern-match on location segments.
          (match (d/get :router/location :segments)
         [] (index)
         ["page" page-id] (page {:page-id page-id})
         :else (not-found)))


(deftest routing-test

  (testing "Basic routing"

    (test-setup!)

    (let [render #(v/render-to-dom % test-element)]

      ;; set initial route to root
      (routing/nav! "/")
      ;; first render
      (render (root))

      ;; route changes should trigger re-render
      (routing/nav! "/non-existing-route")
      (v/flush!)
      (routing/nav! "/page/1")
      (v/flush!)
      (routing/nav! "/page/2")
      (v/flush!)

      (is (= (drop 1 @segments-log) '([]
                                      ["non-existing-route"]
                                      ["page" "1"]
                                      ["page" "2"])))

      (is (= @view-log [:index
                        :not-found
                        [:page "1"]
                        [:page "2"]])
          "Views render on route change"))))