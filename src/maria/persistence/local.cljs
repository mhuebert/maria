(ns maria.persistence.local
  (:require [re-db.d :as d]))

(def init-storage
  "Given a unique id, initialize a local-storage backed source"
  (memoize (fn
             [id]
             (d/transact! [[:db/add id :local-value (aget js/window "localStorage" id)]])
             (d/listen {:ea_ [[id :local-value]]}
                       #(aset js/window "localStorage" id (d/get id :local-value)))
             id)))

