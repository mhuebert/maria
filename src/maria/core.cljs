(ns maria.core
  (:require
    [maria.eval :refer [eval-str]]
    [re-db.d :as d]
    [re-view.subscriptions :as subs]
    [re-view.core :as v :refer-macros [defcomponent]]))

(enable-console-print!)
(defonce main-cell-id (str (gensym)))


(defcomponent editor
  :subscriptions {:cell (subs/db [main-cell-id])}
  :render
  (fn [_ _ {{:keys [cell/eval-result cell/source]} :cell}]
    [:div
     [:textarea {:on-change   #(d/transact! [[:db/add main-cell-id :cell/source (.-value (.-currentTarget %))]])
                 :on-key-down #(when (and (= 13 (.-which %)) (.-metaKey %))
                                (d/transact! [[:db/add main-cell-id :cell/eval-result (eval-str source)]]))
                 :value       source}]
     [:div
      (let [{:keys [value error]} eval-result]
        (cond error (str error)
              (js/React.isValidElement value) value
              :else (str value)))]]))

(defn main []
  (v/render-to-dom (editor) "maria"))

(main)
