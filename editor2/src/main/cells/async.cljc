(ns cells.async
  (:require [re-db.reactive :as r]))

(defn init [] {`loading? (r/atom nil)})

(defn !loading? [x]
  (get (meta x) `loading?))

(defn error! [cell e]
  (reset! cell (cond-> e
                 (string? e)
                 (ex-info {}))))

(defn loading! [cell]
  (reset! (!loading? cell) true))

(defn complete!
  ([cell] (reset! (!loading? cell) nil))
  ([cell value]
   (reset! cell value)
   (complete! cell)))

(def error :error)
(def loading? :loading)


