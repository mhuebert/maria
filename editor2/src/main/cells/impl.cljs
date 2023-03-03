(ns cells.impl
  (:require [re-db.reactive :as r]
            [cells.async :as a]))

#_(defn with-view [cell view]
    (r/update-meta! cell assoc `-view (fn [cell] (view (r/peek cell)))))

(defn make-cell [f]
  (r/make-reaction {:meta (a/init)
                    :detached true}
    (fn []
      (let [cell r/*owner*]
        (try (let [^js v (f r/*owner*)]
               (if (a/promise? v)
                 (do
                   (a/loading! cell)
                   (-> v
                       (.then #(a/complete! cell %))
                       (.catch v #(a/error! cell %))))
                 v)
               v)
             (catch js/Error e (a/error! cell e)))))))



