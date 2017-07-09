(ns maria.persistence.local
  (:require [re-db.d :as d]
            [maria.persistence.transit :as t]
            [goog.functions :as gf]
            [goog.object :as gobj]))

(defn local-id [id] (str "maria.local/" id))

(defn local-put [id data]
  (aset js/window "localStorage" (local-id id) (t/serialize data)))

(defn local-get [id]
  (some-> (gobj/get js/window "localStorage")
          (gobj/get (local-id id))
          (t/deserialize)))

(def init-storage
  "Given a unique id, initialize a local-storage backed source"
  (memoize (fn [id]
             (d/transact! [{:local (local-get id)
                            :db/id id}])
             (d/listen {:ea_ [[id :local]]}
                       (gf/debounce #(local-put id (d/get id :local)) 500))
             id)))

