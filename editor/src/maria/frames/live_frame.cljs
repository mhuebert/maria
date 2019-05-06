(ns maria.frames.live-frame
  (:require [maria.pages.live-layout :as repl]
            [maria.eval :as e]
            [maria.friendly.kinds :as kinds]
            [shapes.core :as shapes]

            [cells.cell :as cell]

            [lark.commands.exec]

            [clojure.set]
            [clojure.string]
            [clojure.walk]

            [maria.html]
            [maria.user :include-macros true]
            [re-view.routing :as r]
            [re-view.core :as v :refer [defview]]
            [maria.views.floating.floating-search]

            [goog.events :as events]

            [maria.frames.frame-communication :as frame]
            [maria.commands.code]
            [clojure.string :as string]
            [maria.frames.live-actions :as user-actions]

            [re-db.d :as d]
            [re-db.patterns :as patterns]
            [maria.util :as util]))

(extend-type cell/Cell
  cell/ICellStore
  (put-value! [this value]
    (d/transact! [[:db/add :cells (name this) value]]))
  (get-value [this]
    (d/get :cells (name this)))
  (invalidate! [this]
    (patterns/invalidate! d/*db* :ea_ [:cells (name this)])))

(extend-protocol kinds/IDoc
  shapes/Shape
  (doc [this] "a shape: some geometry that Maria can draw"))

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
                   (when (util/some-str (.-href a))
                     (navigate a)))))

(events/listen js/window "mousedown"
               (fn [e]
                 (when-let [a (r/closest (.-target e) r/link?)]
                   (when (= (.-origin a) (.. js/window -location -origin))
                     (let [href (.-href a)]
                       (set! (.-href a) (string/replace href (.-origin a) (d/get :window/location :origin)))
                       (events/listenOnce js/window "mouseup" #(set! (.-href a) href)))))))



(defview not-found []
  [:div "We couldn't find this page!"])

(defn render []
  (v/render-to-dom (repl/layout {}) "maria-env"))

(defn init []

  @e/compiler-ready

  (render)

  (frame/listen frame/trusted-frame user-actions/handle-message)
  (frame/send frame/trusted-frame :frame/ready))