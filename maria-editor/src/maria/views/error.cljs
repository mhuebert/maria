(ns maria.views.error
  (:require [re-view.core :as v :refer [defview]]
            [maria.eval :as e]))

(defview error-boundary
  "Error boundary, per React 16"
  {:view/did-catch (fn [{:keys [block-id on-error view/state] :as this} error info]
                     (.log js/console "error-info" info)
                     (when on-error (on-error error info))
                     (reset! state {:error error :info info}))}
  [{:keys [view/state error-content]} child]
  (if @state
    (or (when error-content (error-content))
        [:.bg-washed-red.flex.items-center.tc.pa2 "Render error"])
    child))
