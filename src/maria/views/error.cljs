(ns maria.views.error
  (:require [re-view.core :as v :refer [defview]]
            [maria.eval :as e]))

(defview error-boundary
  "Error boundary, per React 16"
  {:view/did-catch (fn [{:keys [block-id] :as this} error info]
                     (.log js/console "error-info" info)
                     (e/handle-block-error block-id error))}
  [{:keys [view/state]} child]
  (if @state
    [:.bg-washed-red.flex.items-center.tc.pa2 "Render error"]
    child))
