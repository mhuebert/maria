(ns maria.core
  (:require [cljsjs.react]
            [cljsjs.react.dom]

            [maria.views.pages.walkthrough :as walkthrough]
            [maria.views.pages.repl :as repl]
            [maria.views.pages.paredit-inspect :as paredit]

    ;; include to precompile for self-hosted env
            [clojure.set]
            [clojure.string]
            [clojure.walk]

            [maria.html]
            [maria.user :include-macros true]
            [re-view-routing.core :as r]
            [re-view.core :as v :refer [defview]]
            [cljs.core.match :refer-macros [match]]
            [re-db.d :as d]

            [clojure.spec.alpha :include-macros true]
            [clojure.string :as string]

            [cognitect.transit :as t]))

(enable-console-print!)
(def deserialize (partial t/read (t/reader :json)))
(def serialize (partial t/write  (t/writer :json)))

(defview not-found []
  [:div "We couldn't find this page!"])

(defonce _ (r/listen #(d/transact! [(assoc % :db/id :router/location)])))

(defview layout []
  [:.h-100
   (match (d/get :router/location :segments)
          (:or [] ["env"]) (repl/layout)
          ["gist" id] (repl/layout {:gist-id id})
          ["walkthrough"] (walkthrough/main)
          ["paredit"] (paredit/examples)
          :else (not-found))])

(defn main []
  (v/render-to-dom (layout {:x 1}) "maria-env"))

(def parent-origin (if (string/includes? (.. js/window -location -origin) ":5000")
                     "http://localhost:5000"
                     "https://www.maria.cloud"))

(.addEventListener js/window "message"
                   (fn [e]
                     (when (and (= parent-origin (.-origin e))
                                (= (.-parent js/window) (.-source e)))

                       (match (deserialize (.-data e))
                              [:editor/set-content source] (prn :set-source! source))

                       (.log js/console "env got the message" e))) false)

(.postMessage (.-parent js/window) "env is ready" "http://localhost:5000")

(main)
