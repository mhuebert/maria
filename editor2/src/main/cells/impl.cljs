(ns cells.impl
  (:require [re-db.reactive :as r]
            [cells.async :as a]))

#_(defn with-view [cell view]
  (r/update-meta! cell assoc `-view (fn [cell] (view (r/peek cell)))))

(defn make-cell [f]
  (-> (r/make-reaction (fn [] (f r/*owner*)) :meta (a/init))
      r/invalidate!))

(defn after-redef
  "Replace prev-cell with cell, keep references intact and trigger recompute of dependent reactions."
  [prev-cell cell]
  (when prev-cell
    (let [watches (r/get-watches prev-cell)
          prev-val (r/peek prev-cell)]
      (r/dispose! prev-cell)
      (r/migrate-watches! watches prev-val cell))))



