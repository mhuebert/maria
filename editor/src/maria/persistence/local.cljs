(ns maria.persistence.local
  (:require [chia.db :as d]
            [chia.reactive :as r]
            [maria.persistence.transit :as t]
            [goog.functions :as gf]
            [applied-science.js-interop :as j]))

(defn local-id [id] (str "maria.local/" id))

(defn local-get [id]
  (some-> (j/get-in js/window [:localStorage (local-id id)])
          (t/deserialize)))

(defn local-put! [id data]
  (j/assoc! (.-localStorage js/window) (local-id id) (t/serialize data)))

(defn local-update! [id f & args]
  (local-put! id (apply f (local-get id) args)))

(def init-storage
  "Given a unique id, initialize a local-storage backed source"
  (memoize (fn
             ([id]
              (d/transact! [{:local (local-get id)
                             :db/id id}])
              (d/listen (gf/throttle #(local-put! id (d/get id :local)) 300)
                        {:ea_ [[id :local]]}))
             ([id initial-content]
              (when (and (nil? (local-get id)) initial-content)
                (local-put! id initial-content))
              (init-storage id)))))

