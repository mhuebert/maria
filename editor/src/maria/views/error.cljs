(ns maria.views.error
  (:require [chia.view :as v]
            [maria.eval :as e]
            [applied-science.js-interop :as j]))

(v/defclass error-boundary
  "Error boundary, per React 16"
  {:view/did-catch
   (fn [{:keys [on-error]} error info]
     (when on-error (on-error error info)))
   :static/get-derived-state-from-error
   (fn [error info] #js{:error-state #js[error info]})}
  [{:as   this
    :keys [fallback]} child]
  (if-some [[error info] (j/get-in this [:state :error-state])]
    (or (when fallback (fallback error info))
        [:.bg-washed-red.flex.items-center.tc.pa2 (or (some->> error (.-message))
                                                      "Render error")])
    child))