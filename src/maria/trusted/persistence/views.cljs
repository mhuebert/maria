(ns maria.trusted.persistence.views
  (:require [re-view.core :as v :refer [defview]]))

(defview load
         {:life/initial-state {:loading? true}
          :life/did-mount     (fn [{:keys [load-fn view/state]}]
                                (load-fn (partial reset! state)))}
         [{:keys [on-loading on-success on-error view/state]}]
         (let [{:keys [loading? value error]} @state]
           (cond loading? (on-loading)
                 value (on-success value)
                 error (on-error error))))