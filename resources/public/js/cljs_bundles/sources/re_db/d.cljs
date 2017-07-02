(ns re-db.d
  (:refer-clojure :exclude [get get-in contains? select-keys namespace])
  (:require [re-db.core :as d])
  (:require-macros [re-db.d]))

(defonce ^:dynamic *db* (d/create {}))

(defn partial-deref
  "Partially apply a (an atom) to f, but deref the atom at time of application."
  [f a]
  (fn [& args]
    (apply f @a args)))

(def entity (partial-deref d/entity *db*))
(def get (partial-deref d/get *db*))
(def get-in (partial-deref d/get-in *db*))
(def select-keys (partial-deref d/select-keys *db*))

(def entity-ids (partial-deref d/entity-ids *db*))
(def entities (partial-deref d/entities *db*))

(def contains? (partial-deref d/contains? *db*))
(def touch (partial-deref d/touch *db*))

(def transact! (partial d/transact! *db*))
(def listen (partial d/listen *db*))
(def unlisten (partial d/unlisten *db*))
(def merge-schema! (partial d/merge-schema! *db*))

(def unique-id d/unique-id)