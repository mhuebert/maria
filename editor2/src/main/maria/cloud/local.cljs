(ns maria.cloud.local
  (:refer-clojure :exclude [get])
  (:require [applied-science.js-interop :as j]
            [maria.cloud.transit :as t]
            [re-db.reactive :as r]))

(defn local-id [id] (str "maria.local/" id))

(defn get
  ([id] (get id nil))
  ([id not-found]
   (if-let [v (j/get-in js/window [:localStorage (local-id id)])]
     (t/read v)
     not-found)))

(defn put! [id v]
  (j/assoc-in! js/window [:localStorage (local-id id)] (t/write v)))

(defonce ratom
  (memoize
   (fn [id init]
     (let [v (get id ::not-found)
           !rx (r/atom v)]
       (add-watch !rx ::sync
                  (fn [_ _ _ new-value] (put! id new-value)))
       (when (= v ::not-found)
         (reset! !rx init))
       !rx))))
