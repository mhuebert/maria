(ns cells.cell
  (:require [re-db.reactive :as r]))

(defprotocol IAsyncStatus
  :extend-via-metadata true
  (-async-status [this]))

#_(defprotocol IView
  :extend-via-metadata true
  (-view [this]))

#_(defn with-view [cell view]
  (r/update-meta! cell assoc `-view (fn [cell] (view (r/peek cell)))))

(defn satisfies-async-status? [x]
  (and (satisfies? IMeta x) (get (meta x) `-async-status )))

(defn make-cell [prev f]
  (let [async-state (r/atom nil)
        cell (r/make-reaction (fn [] (f r/*owner*))
                              :meta {`-async-status (fn [this] async-state)})]
    (if prev
      (let [watches (r/get-watches prev)
            prev-val (r/peek prev)]
        (r/dispose! prev)
        (r/migrate-watches! watches prev-val (r/invalidate! cell)))
      (r/invalidate! cell))))

(defn ^:macro cell [&form &env expr]
  `(make-cell nil (fn [~'self] ~expr)))

(defn ^:macro defcell [&form &env the-name & body]
  (let [[docstring body] (if (string? (first body))
                           [(first body) (rest body)]
                           [nil body])
        [options body] (if (and (map? (first body)) (> (count body) 1))
                         [(first body) (rest body)]
                         [nil body])]
    `(do
       (defonce ~the-name nil)
       (let [^r/Reaction prev-cell# ~the-name]
         (def ~(with-meta the-name options)
           ~@(when docstring (list docstring))
           (make-cell prev-cell# (fn [~'self] ~@body)))
         ~the-name))))

(defn error! [cell e] (reset! (-async-status cell) {:error e}))
(defn loading! [cell] (reset! (-async-status cell) {:loading true}))
(defn complete! [cell] (reset! (-async-status cell) nil))

(defn async-status [x]
  (when-let [f (and (satisfies? IMeta x)
                    (get (meta x) `-async-status))]
    (f x)))

(defn status "Returns :error, :loading, or nil"
  [cell]
  (some #{:error :loading} (-async-status cell)))

(def loading? (comp #{:loading} status))
(def error? (comp #{:error} status))
(def error (comp :error status))
(defn message [cell] (some-> (error cell) str))

