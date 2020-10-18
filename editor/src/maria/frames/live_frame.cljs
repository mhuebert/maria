(ns maria.frames.live-frame
  (:require [maria.pages.live-layout :as repl]
            [maria.eval :as e]
            [maria.friendly.kinds :as kinds]
            [maria.friendly.messages :as messages]
            [shapes.core :as shapes]

            [lark.commands.exec]

            [clojure.set]
            [clojure.string]
            [clojure.walk]

            [maria.html]
            [maria.user :include-macros true]
            [chia.routing :as r]
            [chia.view :as v]
            [maria.views.floating.floating-search]

            [goog.events :as events]

            [maria.frames.frame-communication :as frame]
            [maria.commands.code]
            [clojure.string :as str]
            [maria.frames.live-actions :as user-actions]

            [chia.db :as d]
            [maria.util :as util]
            [maria.friendly.messages :as messages]))

(extend-protocol kinds/IDoc
  shapes/Shape
  (doc [this] "a shape: some geometry that Maria can draw"))

(defn navigate [a]
  (frame/send frame/trusted-frame [:window/navigate (let [origin (.. js/window -location -origin)
                                                          href (.-href a)]
                                                      (cond-> (.-href a)
                                                              (str/starts-with? href origin)
                                                              (subs (count origin)))) {:popup? (= (.-target a) "_blank")}]))

(events/listen js/window "click"
               (fn [^js e]
                 (when-let [a (.closest (.-target e) "a")]
                   (.preventDefault e)
                   (when (util/some-str (.-href a))
                     (navigate a)))))

(events/listen js/window "mousedown"
               (fn [e]
                 (when-let [a (.closest ^js (.-target e) "a")]
                   (when (= (.-origin a) (.. js/window -location -origin))
                     (let [href (.-href a)]
                       (prn :set-once (str/replace href (.-origin a) (d/get :window/location :origin)))
                       (set! (.-href a) (str/replace href (.-origin a) (d/get :window/location :origin)))
                       (events/listenOnce js/window "mouseup" #(set! (.-href a) href)))))))



(v/defclass not-found []
  [:div "We couldn't find this page!"])

(defn render []
  (v/render-to-dom (repl/layout {}) "maria-env"))

(defn ^:export init []

  @e/compiler-ready

  (messages/override-analyzer-messages!)

  (render)

  (frame/listen frame/trusted-frame user-actions/handle-message)
  (frame/send frame/trusted-frame :frame/ready))
