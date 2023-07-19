(ns maria.cloud.local-sync
  "Triples synced to localStorage via re-db"
  (:require [maria.cloud.local :as local]
            [re-db.api :as db]
            [re-db.reactive :as r]))

(defn db-id [id] {:entity/local id})

(def sync-entity!
  (memoize (fn [id]
             (let [db-id (db-id id)]
               (when-let [init (local/get db-id)]
                 (db/transact! [(assoc init :db/id db-id)]))
               (r/reaction!
                 (local/put! db-id (db/get db-id)))))))

(def sync-attr!
  (memoize (fn [id attr]
             (let [db-id (db-id id)]
               (when-let [init (get (local/get db-id) attr)]
                 (db/transact! [[:db/add db-id attr init]]))
               (r/reaction!
                 (local/update! db-id assoc attr (db/get db-id attr)))))))


(defn get-entity [id]
  (sync-entity! id)
  (db/get (db-id id)))

(defn swap-entity [id f & args]
  (sync-entity! id)
  (db/transact! [(-> (apply f (get-entity id) args)
                     (assoc :db/id (db-id id)))]))

(defn get-attr [id attr]
  (sync-attr! id attr)
  (get (get-entity id) attr))

(defn set-attr [id attr v]
  (sync-attr! id attr)
  (db/transact! [[:db/add (db-id id) attr v]]))