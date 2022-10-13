(ns cells.cell
  (:require [re-db.reactive :as r]))

(defn guard
  ([f] (fn [x] (when (f x) x)))
  ([x f] (when (f x) x)))

(defprotocol IAsyncStatus
  :extend-via-metadata true
  (-async-status [this]))

#_(defprotocol IView
  :extend-via-metadata true
  (-view [this]))

#_(defn with-view [cell view]
  (r/update-meta! cell assoc `-view (fn [cell] (view (r/peek cell)))))

(defn make-cell [f]
  (let [!async-status (r/atom nil)]
    (-> (r/make-reaction (fn [] (f r/*owner*))
                         :meta {`-async-status (fn [this] !async-status)})
        r/invalidate!)))

(defn migrate-prev [from to]
  (when from
    (let [watches (r/get-watches from)
          prev-val (r/peek from)]
      (r/dispose! from)
      (r/migrate-watches! watches prev-val to))))

(defn ^:macro cell [&form &env expr]
  `(make-cell (fn [~'self] ~expr)))

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
           (make-cell (fn [~'self] ~@body)))
         (migrate-prev prev-cell# ~the-name)
         ~the-name))))

(defn async-status
  "Async metadata is stored in a ratom containing `true` for loading-state,
   or an instance of js/Error."
  [x]
  (when-let [f (get (meta x) `-async-status)]
    (f x)))

(defn error! [cell e] (reset! (-async-status cell) (cond-> e
                                                           (string? e)
                                                           (ex-info {:cell cell}))))
(defn error [state] (guard state #(instance? js/Error %)))

(defn loading! [cell] (reset! (-async-status cell) true))
(defn loading? [state] (true? state))

(defn complete! [cell] (reset! (-async-status cell) nil))

