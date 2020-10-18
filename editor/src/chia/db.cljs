(ns chia.db
  (:refer-clojure :exclude [get get-in contains? select-keys namespace])
  (:require [chia.db.core :as d]))

(defonce ^:dynamic *db* (d/create {}))

(def entity (partial d/entity *db*))
(def get (partial d/get *db*))
(def get-in (partial d/get-in *db*))
(def select-keys (partial d/select-keys *db*))

(def entity-ids (partial d/entity-ids *db*))
(def entities (partial d/entities *db*))

(def contains? (partial d/contains? *db*))
(def touch (partial d/touch *db*))

(def transact! (partial d/transact! *db*))
(def listen (partial d/listen *db*))
(def unlisten (partial d/unlisten *db*))
(def merge-schema! (partial d/merge-schema! *db*))

(def unique-id d/unique-id)
