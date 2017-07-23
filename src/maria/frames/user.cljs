(ns maria.frames.user
  (:require [cljsjs.react]
            [cljsjs.react.dom]
            [maria.views.pages.repl :as repl]
            [cljs.core.match :refer-macros [match]]

            [maria.commands.exec]

    ;; include to precompile for self-hosted env
            [clojure.set]
            [clojure.string]
            [clojure.walk]

            [maria.html]
            [maria.user :include-macros true]
            [re-view-routing.core :as r]
            [re-view.core :as v :refer [defview]]

            [goog.events :as events]

            [maria.frames.communication :as frame]
            [maria.commands.commands]
            [clojure.string :as string]
            [maria.persistence.local :as local]
            [maria.persistence.github :as github]
            [maria.frames.user-actions :as user-actions]

            [maria.live.analyzer]))

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

  (local/init-storage "new" github/blank)
  (repl/init)

  (v/render-to-dom (repl/layout {:window-id 1}) "maria-env")

  (frame/listen frame/trusted-frame user-actions/handle-message)
  (frame/send frame/trusted-frame :frame/ready))



;(frame/listen "*" (partial println :editor-listen-all))



(main)