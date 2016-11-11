(ns maria.core
  (:require
    [maria.eval :refer [eval-src]]
    [cljs.pprint :refer [pprint]]
    [maria.codemirror :refer [editor]]
    [re-db.d :as d]
    [re-view.subscriptions :as subs]
    [re-view.core :as v :refer-macros [defcomponent]]))

(enable-console-print!)

;; to support multiple editors
(defonce editor-id 1)

(defn display-result [{:keys [value error warnings]}]
  [:div

   (cond error [:.pa3.dark-red (str error)]
         (js/React.isValidElement value) value
         :else (when value [:.bg-white.pa3.mb3 (str value)]))
   (when (seq warnings)

     [:.bg-light-gray.pa3.pre
      [:.dib.dark-red "Warnings: "]
      (for [warning (distinct (map #(dissoc % :env) warnings))]
        (str "\n" (with-out-str (pprint warning))))])])

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
