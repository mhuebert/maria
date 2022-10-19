(ns cells.async
  (:require [re-db.reactive :as r]))

(defn init [] {`status (r/atom nil)})

(defn !status
  "Async metadata is stored in a ratom containing `true` for loading-state,
   or an instance of js/Error."
  [x]
  (get (meta x) `status))

(defn error! [cell e] (reset! (!status cell)
                              {:error (cond-> e
                                              (string? e)
                                              (ex-info {:cell cell}))}))
(defn loading! [cell] (reset! (!status cell) {:loading true}))
(defn complete!
  ([cell] (reset! (!status cell) nil))
  ([cell value]
   (reset! cell value)
   (complete! cell)))

(def error :error)
(def loading? :loading)

(defn promise? [x] (instance? js/Promise x))


