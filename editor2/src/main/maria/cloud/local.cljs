(ns maria.cloud.local
  (:refer-clojure :exclude [get])
  (:require [applied-science.js-interop :as j]
            [maria.cloud.transit :as t]
            [re-db.reactive :as r]))

(defn local-id [id] (str "maria.cloud/" id))

(defn get
  ([id] (get id nil))
  ([id not-found]
   (if-let [v (j/get-in js/window [:localStorage (local-id id)])]
     (t/read v)
     not-found)))

(defn put! [id v]
  (j/assoc-in! js/window [:localStorage (local-id id)] (t/write v)))

(defn update! [id f & args]
  (j/assoc-in! js/window [:localStorage (local-id id)] (t/write (apply (get id) f args))))

(defonce ratom* (memoize (fn [id]
                           (doto (r/atom (get id) :meta {:id id})
                             (add-watch ::sync (fn [_ _ _ new-value] (put! id new-value)))))))
(defn ratom
  ([id] (ratom* id))
  ([id init]
   (let [!rx (ratom* id)]
     (when (nil? @!rx) (reset! !rx init))
     !rx)))