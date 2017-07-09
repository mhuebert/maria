(ns maria.user-frame
  (:require [cljsjs.react]
            [cljsjs.react.dom]
            [maria.views.pages.repl :as repl]
            [cljs.core.match :refer-macros [match]]

    ;; include to precompile for self-hosted env
            [clojure.set]
            [clojure.string]
            [clojure.walk]

            [maria.html]
            [maria.user :include-macros true]
            [re-view-routing.core :as r]
            [re-view.core :as v :refer [defview]]

            [clojure.spec.alpha :include-macros true]

            [goog.events :as events]

            [maria.frame-communication :as frame]
            [re-db.d :as d]
            [clojure.string :as string]))

(enable-console-print!)

(defn navigate [a]
  (frame/send frame/trusted-frame [:window/navigate (let [origin (.. js/window -location -origin)
                                                          href (.-href a)]
                                                      (cond-> (.-href a)
                                                              (string/starts-with? href origin)
                                                              (subs (count origin)))) {:popup? (= (.-target a) "_blank")}]))

(events/listen js/window "click"
               (fn [e]
                 (when-let [a (r/closest (.-target e) r/link?)]
                   (.preventDefault e)
                   (navigate a))))

(defview not-found []
  [:div "We couldn't find this page!"])

(defn main []

  (v/render-to-dom (repl/layout {:window-id 1}) "maria-env")

  (frame/listen frame/trusted-frame #(match % [:db/transactions txs]
                                            (d/transact! txs)))
  (frame/send frame/trusted-frame :frame/ready))



;(frame/listen "*" (partial println :editor-listen-all))



(main)