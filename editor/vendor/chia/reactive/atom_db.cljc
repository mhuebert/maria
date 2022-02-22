(ns chia.reactive.atom-db
  "Convenience namespace for the common practice of using a single global atom for app state."
  (:refer-clojure :exclude [deref get get-in select-keys contains? assoc! dissoc! swap! reset!])
  (:require [chia.reactive.atom :as ra]))

(defonce db (atom {}))

(def deref
  "Reactively dereferences any atom, creates a dependency on it for the current reader (ie. chia.reactive/*reader*)"
  (partial ra/deref db))
(def get
  "Like Clojure's `get`, creates dependency on key `k`."
  (partial ra/get db))
(def get-in
  "Like Clojure's `get-in`, creates dependency on `path`."
  (partial ra/get-in db))
(def select-keys
  "Like Clojure's `select-keys`, creates dependency on the given keys `ks`."
  (partial ra/select-keys db))
(def contains?
  "Like Clojure's `contains?`, creates dependency on the key `k`."
  (partial ra/contains? db))
(def apply-in
  "Applies `f` to value at `path` followed by `args`. Creates dependency on the result of this computation."
  (partial ra/apply-in db))
(def keys-in
  "Returns keys at `path`, creates a dependency on the result."
  (partial ra/keys-in db))

(def assoc!
  "Like Clojure's `assoc`, but mutates the provided `ref`."
  (partial ra/assoc! db))
(def assoc-in!
  "Like Clojure's `assoc-in`, but mutates the provided `ref`."
  (partial ra/assoc-in! db))
(def dissoc!
  "Like Clojure's `dissoc`, but mutates the provided `ref`."
  (partial ra/dissoc! db))
(def update!
  "Like Clojure's `update`, but mutates the provided `ref`."
  (partial ra/update! db))
(def update-in!
  "Like Clojure's `update-in`, but mutates the provided `ref`."
  (partial ra/update-in! db))
(def swap!
  (partial ra/swap! db))
(def reset!
  (partial ra/reset! db))