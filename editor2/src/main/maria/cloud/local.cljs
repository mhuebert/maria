(ns maria.cloud.local
  (:require [applied-science.js-interop :as j]
            [maria.cloud.transit :as t]
            [re-db.reactive :as r]))

(defn local-id [id] (str "maria.local/" id))

(defonce ratom
  (memoize
   (fn [id init]
     (let [v (if-let [v (j/get-in js/window [:localStorage (local-id id)])]
               (t/read v)
               ::new)
           !rx (r/atom v)]
       (add-watch !rx ::sync
                  (fn [_ _ _ new-value]
                    (j/assoc-in! js/window [:localStorage (local-id id)] (t/write new-value))))
       (when (= v ::new)
         (reset! !rx init))
       !rx))))
