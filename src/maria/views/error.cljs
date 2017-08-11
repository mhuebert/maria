(ns maria.views.error
  (:require [re-view.core :as v :refer [defview]]
            [re-db.d :as d]))

(defview error-boundary
  "Error boundary, per React 16"
  {:view/did-catch (fn [{:keys [cell-id] :as this} error info]
                     (.log js/console "error" error)
                     (.log js/console "error-info" info)
                     (let [eval-log (d/get cell-id :eval-log)]
                       (d/transact! [[:db/update-attr cell-id :eval-log update (dec (count eval-log)) assoc :error (js/Error. "Render error")]])))}
  [{:keys [view/state] :as this} child]
  (if @state
    [:.bg-washed-red.flex.items-center.tc.pa2 "Render error"]
    child))
