(ns cells.impl
  (:require [cells.async :as a]
            [re-db.reactive :as r]))

#_(defn with-view [cell view]
    (r/update-meta! cell assoc `-view (fn [cell] (view (r/peek cell)))))

(defn make-cell
  ([memo-key f]
   (let [!cache (-> r/*owner* meta ::cache)]
     (assert !cache "Cannot memoize a cell outside of a another cell")
     (or (@!cache memo-key)
         (let [cell (make-cell f)]
           (swap! !cache assoc memo-key cell)
           cell))))
  ([f]
   (r/make-reaction {:meta (assoc (a/init) ::cache (atom {}))
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
              (catch js/Error e (a/error! cell e))))))))

(defn cell? [x]
  (and (instance? r/Reaction x)
       (::cache (meta x))))

