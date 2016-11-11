(ns maria.core
  (:require
    [maria.eval :refer [eval-src]]
    [maria.codemirror :refer [editor]]
    [re-db.d :as d]
    [re-view.subscriptions :as subs]
    [re-view.core :as v :refer-macros [defcomponent]]))

(enable-console-print!)

;; to support multiple editors
(defonce editor-id 1)

(defn display-result [{:keys [value error warnings]}]
  [:div
   [:.bg-white.b--near-white.bt.br.bb.bw4
    (cond error (str error)
          (js/React.isValidElement value) value
          :else (str value))]
   (when (seq warnings)
     [:.bg-light-gray.pa3 {:key "warnings"}
      (str warnings)])])

(defcomponent app
              :subscriptions {:source      (subs/db [editor-id :source])
                              :eval-result (subs/db [editor-id :eval-result])}
              :render
              (fn [_ _ {:keys [eval-result source]}]
                [:.bg-near-white.cf
                 [:.w-50.fl.pa3
                  (editor {:value         source
                           :event/keydown #(when (and (= 13 (.-which %2)) (.-metaKey %2))
                                            (d/transact! [[:db/add editor-id :eval-result (eval-src (d/get editor-id :source))]]))
                           :event/change  #(d/transact! [[:db/add editor-id :source (.getValue %1)]])})]
                 [:.w-50.fl.pa3
                  (display-result eval-result)]]))

(defn main []
  (v/render-to-dom (app) "maria"))

(main)
