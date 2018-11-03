(ns maria.views.error
  (:require [chia.view :as v]
            [maria.eval :as e]))

(v/defview error-boundary
  "Error boundary, per React 16"
  {:view/did-catch (fn [{:keys [block-id on-error view/state] :as this} error info]
                     (.log js/console "error-info" info)
                     (let [result {:error error :info info}]
                       (when on-error (on-error result))
                       (reset! state result)))}
  [{:keys [view/state error-content]} child]
  (if @state
    (or (when error-content (error-content @state))
        [:.bg-washed-red.flex.items-center.tc.pa2 (or (some->> (:error @state) (.-message))
                                                      "Render error")])
    child))
