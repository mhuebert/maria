(ns maria.views.error
  (:require [re-view.core :as v :refer [defview]]
            [cljs-live.eval :as e]
            [re-db.d :as d]))

(defview error-boundary
  "Error boundary, per React 16"
  {:view/did-catch (fn [{:keys [cell-id] :as this} error info]
                     (.log js/console "error" error)
                     (.log js/console "error-info" info)
                     (let [eval-log (d/get cell-id :eval-log)
                           result (-> (peek eval-log)
                                      (assoc :error (or error (js/Error. "Render error"))
                                             :error/kind :eval)
                                      (e/add-error-position))]
                       (d/transact! [[:db/update-attr cell-id :eval-log assoc (dec (count eval-log)) result]])))}
  [{:keys [view/state] :as this} child]
  (if @state
    [:.bg-washed-red.flex.items-center.tc.pa2 "Render error"]
    child))
