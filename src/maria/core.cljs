(ns maria.core
  (:require [cljsjs.react]
            [cljsjs.react.dom]
            [maria.views.pages.repl :as repl]

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

            [goog.events :as events]

            [maria.frame-communication :as frame]))

(enable-console-print!)

(defn navigate [href]
  (frame/send frame/parent-window [:url/navigate href]))

(events/listen js/window "click"
               (fn [e]
                 (when-let [href (and (= "A" (.. e -target -tagName))
                                      (.. e -target -href))]
                   (.preventDefault e)
                   (navigate href))))

(defview not-found []
  [:div "We couldn't find this page!"])

(defn main []
  (v/render-to-dom (repl/layout) "maria-env"))

(frame/listen "*" (partial println :editor-listen-all))

(frame/listen "parent"
              (fn [message] (match message
                                   [:source/reset data] (d/transact! [(assoc data :db/id :parent/source)]))))

(main)