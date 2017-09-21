(ns maria.frames.live-frame
  (:require [maria.pages.live-layout :as repl]
            [maria.eval :as e]


            [cells.cell :as cell]

            [lark.commands.exec]
            [maria.show :as show]

            [clojure.set]
            [clojure.string]
            [clojure.walk]

            [maria.html]
            [maria.user :include-macros true]
            [re-view-routing.core :as r]
            [re-view.core :as v :refer [defview]]
            [maria.views.floating.floating-search]

            [goog.events :as events]

            [maria.frames.frame-communication :as frame]
            [maria.commands.code]
            [clojure.string :as string]
            [maria.persistence.local :as local]
            [maria.persistence.github :as github]
            [maria.frames.live-actions :as user-actions]

            [maria.live.analyze]
            [re-db.d :as d]
            [re-db.patterns :as patterns]))

(extend-type cell/Cell
  cell/ICellStore
  (put-value! [this value]
    (d/transact! [[:db/add :cells (name this) value]]))
  (get-value [this]
    (d/get :cells (name this)))
  (invalidate! [this]
    (patterns/invalidate! d/*db* :ea_ [:cells (name this)]))
  show/IShow
  (show [this] (cell/view this)))

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

  (e/init)

  (v/render-to-dom (repl/layout {}) "maria-env")

  (frame/listen frame/trusted-frame user-actions/handle-message)
  (frame/send frame/trusted-frame :frame/ready))



;(frame/listen "*" (partial println :editor-listen-all))



(main)