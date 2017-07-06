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

            [clojure.spec.alpha :include-macros true]

            [goog.events :as events]

            [maria.frames.communication :as frame]))

(enable-console-print!)

(defn navigate [a]
  (frame/send frame/parent-frame [:window/navigate (.-href a) {:popup? (= (.-target a) "_blank")}]))

(events/listen js/window "click"
               (fn [e]
                 (when-let [a (r/closest (.-target e) r/link?)]
                   (.preventDefault e)
                   (navigate a))))

(defview not-found []
  [:div "We couldn't find this page!"])

(defn main []
  (v/render-to-dom (repl/layout {:window-id 1}) "maria-env"))

#_(frame/listen "*" (partial println :editor-listen-all))



(main)