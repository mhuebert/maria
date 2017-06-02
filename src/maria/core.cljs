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

            [clojure.spec.alpha :include-macros true]))

(enable-console-print!)

(defview not-found []
  [:div "We couldn't find this page!"])

(defonce _ (r/listen #(d/transact! [(assoc % :db/id :router/location)])))

(defview layout []
  [:.h-100
   (match (d/get :router/location :segments)
          [] (repl/layout)
          ["gist" id] (repl/layout {:gist-id id})
          ["walkthrough"] (walkthrough/main)
          ["paredit"] (paredit/examples)
          :else (not-found))])

(defn main []
  (v/render-to-dom (layout {:x 1}) "maria-main"))

(main)
