(ns cells.core
  (:require [re-db.d :as d]
            [re-db.patterns :as patterns :include-macros true]
            [maria.blocks.blocks]))

(def ^:dynamic *current-cell* nil)

(defprotocol ICompute
  (-compute [this]
            "Compute a value without changing state")
  (-compute! [this]
             "Compute a value, changing state of the cell"))

(defprotocol IInterval
  (-set-interval [this n f])
  (-clear-intervals [this]))

(defprotocol IClearState
  (-clear-state [this]))

(defn interval [n f]
  (let [cell *current-cell*]
    (-set-interval cell n #(-reset! cell (f @cell)))
    (f @cell)))

(defonce -intervals (volatile! {}))
(defonce -unlisteners (volatile! {}))
(defonce -patterns (volatile! {}))

(deftype Cell [name ^:mutable f]

  IInterval
  (-set-interval [this n timed-fn]
    (vswap! -intervals update name (fnil conj #{}) (js/setInterval timed-fn n)))
  (-clear-intervals [this]
    (doseq [interval (get @-intervals name)]
      (js/clearInterval interval))
    (vswap! -intervals dissoc name))

  IDeref
  (-deref [this]
    (d/get ::cells name))

  IReset
  (-reset! [this newval]
    (let [oldval @this]
      (d/transact! [[:db/add ::cells name newval]])
      newval))

  ISwap
  (-swap! [this f] (reset! this (f @this)))
  (-swap! [this f a] (reset! this (f @this a)))
  (-swap! [this f a b] (reset! this (f @this a b)))
  (-swap! [this f a b xs] (reset! this (apply f @this a b xs)))

  IClearState
  (-clear-state [this]
    (-clear-intervals this)
    (vswap! -patterns dissoc name)
    (when-let [unlisten (get @-unlisteners name)]
      (unlisten)
      (vswap! -unlisteners dissoc name)))

  ICompute
  (-compute [this]
    (-clear-intervals this)
    (f this))
  (-compute! [this]
    (let [{patterns :patterns value :value} (binding [*current-cell* this]
                                              (patterns/capture-patterns (-compute this)))

          ;; avoid depending on self
          patterns (update patterns :ea_ disj [::cells name])
          prev-patterns (get @-patterns name)
          unlisten (get @-unlisteners name)]
      (when (not= patterns prev-patterns)
        (when unlisten (unlisten))
        (vswap! -patterns assoc name patterns)
        (vswap! -unlisteners assoc name (when-not (= patterns {:ea_ #{}})
                                         (d/listen patterns #(-compute! this)))))
      (-reset! this value))))

(defn make-cell
  ([f]
   (make-cell (d/unique-id) (f)))
  ([name f]
   (let [cell (->Cell name f)]
     (-clear-state cell)
     (-compute! cell)
     cell)))

