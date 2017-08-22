(ns cells.cell
  (:require [re-db.d :as d]
            [com.stuartsierra.dependency :as dep]
            [cells.util]
            [clojure.set :as set]
            [cells.util :as util])
  (:require-macros [cells.cell :refer [defcell cell cell-fn]]))

(def ^:dynamic *cell-stack* (list))
(def ^:dynamic *computing-dependents* false)
(def ^:dynamic *debug* false)
(defonce -cells (volatile! {}))
(defonce dep-graph (volatile! (dep/graph)))

(defn log
  [& args]
  (when *debug* (prn args)))

(defprotocol ISwap*
  (-swap-cell! [this f]
               [this f a]
               [this f a b]
               [this f a b xs]))

(defprotocol IReset*
  (reset-cell! [this newval]))

(defn swap-cell!
  "Like regular swap! but for cells. Separate protocol because we want to discourage manipulating state from the outside."
  ([a f]
   (-swap-cell! a f))
  ([a f x]
   (-swap-cell! a f x))
  ([a f x y]
   (-swap-cell! a f x y))
  ([a f x y & more]
   (-swap-cell! a f x y more)))


(defn cell-name [cell]
  (cond-> cell
          (not (keyword? cell)) (name)))

(defn dependencies [cell]
  (dep/immediate-dependencies @dep-graph (cell-name cell)))
(defn dependents [cell]
  (dep/immediate-dependents @dep-graph (cell-name cell)))
(defn remove-node [cell]
  (vswap! dep-graph dep/remove-node (cell-name cell)))
(defn remove-edge [cell other-cell]
  (vswap! dep-graph dep/remove-edge (cell-name cell) (cell-name other-cell)))
(defn remove-all [cell]
  (vswap! dep-graph dep/remove-all (cell-name cell)))
(defn depend [cell other-cell]
  (vswap! dep-graph dep/depend (cell-name cell) (cell-name other-cell)))

(defn transitive-dependents [cell]
  (dep/transitive-dependents @dep-graph (cell-name cell)))

(defn topo-sort [cells]
  (sort (dep/topo-comparator @dep-graph) cells))

(defn transitive-dependents-sorted [cell]
  (topo-sort (transitive-dependents cell))
  #_(let [cells (transitive-dependents cell)
          include (conj cells (cell-name cell))
          sparser-graph (dep/->MapDependencyGraph
                          (select-keys (:dependencies @dep-graph) include)
                          (select-keys (:dependents @dep-graph) include))
          faster-sort (sort (dep/topo-comparator sparser-graph) cells)
          ]))

(defprotocol IDispose
  (on-dispose [context f] "Register a callback to be fired when context is disposed.")
  (-dispose! [context]))

(defprotocol IHandleError
  (handle-error [this e]))

(defonce -dispose-callbacks (volatile! {}))

(defn dispose! [value]
  (when (satisfies? IDispose value)
    (-dispose! value)))

(def -default-context-state (volatile! {:dispose-fns #{}}))

(def ^:dynamic *eval-context* (reify
                                IDispose
                                (on-dispose [context f]
                                  (vswap! -default-context-state update :dispose-fns conj f))
                                (-dispose! [context]
                                  (doseq [f (:dispose-fns @-default-context-state)]
                                    (f))
                                  (vswap! -default-context-state update :dispose-fns empty))
                                IHandleError
                                (handle-error [this e] (throw e))))

(defprotocol IReactiveCompute
  (-set-function! [this f])

  (-compute-dependents! [this])
  (-compute [this] "evaluate cell")
  (-compute! [this] "reset cell with a new computed value"))

(defprotocol IAsync
  (-set-async-state! [this status] [this status message] "Set loading status")

  (status [this])
  (message [this] "Read message associated with async state")

  (error? [this])
  (loading? [this]))



(def ^:dynamic *read-log* nil)

(def blank-cell-state {})
(declare make-cell)

(deftype Cell [id ^:mutable f ^:mutable state eval-context]

  IPrintWithWriter
  (-pr-writer [this writer _]
    (write-all writer (str "cell#" id)))

  INamed
  (-name [this] id)

  ICloneable
  (-clone [this]
    (make-cell (keyword (namespace id) (util/unique-id)) f))

  IAsync
  (-set-async-state! [this value] (-set-async-state! this value nil))
  (-set-async-state! [this value message]
    (set! state (assoc state
                  :async/status value
                  :async/message message))
    (-compute-dependents! this))
  (status [this]
    @this
    (:async/status state))
  (message [this]
    @this
    (:async/message state))
  (loading? [this]
    (= :loading (status this)))
  (error? [this]
    (= :error (status this)))

  IWatchable
  (-notify-watches [this oldval newval]
    (doseq [f (vals (:watches state))]
      (f this oldval newval)))
  (-add-watch [this key f]
    (set! state (update state :watches assoc key f)))
  (-remove-watch [this key]
    (set! state (update state :watches dissoc key f)))

  IDeref
  (-deref [this]
    (when *read-log*
      (vswap! *read-log* conj (name this)))
    (d/get ::cells id))

  IReset*
  (reset-cell! [this newval]
    (log :-reset! this)
    (let [oldval @this]
      (d/transact! [[:db/add ::cells id newval]])
      (-notify-watches this oldval newval))
    (-compute-dependents! this)
    newval)

  ISwap*
  (-swap-cell! [this f] (reset-cell! this (f @this)))
  (-swap-cell! [this f a] (reset-cell! this (f @this a)))
  (-swap-cell! [this f a b] (reset-cell! this (f @this a b)))
  (-swap-cell! [this f a b xs] (reset-cell! this (apply f @this a b xs)))

  IDispose
  (on-dispose [this f]
    (vswap! -dispose-callbacks update id conj f))
  (-dispose! [this]
    (doseq [f (get @-dispose-callbacks id)]
      (f))
    (vswap! -dispose-callbacks dissoc id)
    this)

  IReactiveCompute
  (-compute-dependents! [this]
    (when-not *computing-dependents*
      (binding [*computing-dependents* true]
        (let [deps (transitive-dependents-sorted this) #_(topo-sort (transitive-dependents this))]
          (log (str "Computing dependents for " (name this)) deps)
          (doseq [cell-id deps]
            (-compute! (@-cells cell-id)))))))

  (-set-function! [this newf]
    (set! f newf))


  (-compute [this]
    (binding [*cell-stack* (cons this *cell-stack*)
              *eval-context* eval-context]
      (try
        (reset-cell! this (f this))
        (catch js/Error e
          (dispose! this)
          (throw e)))))
  (-compute! [this]
    (if (= this (first *cell-stack*))
      (log :-compute! "Return - in current cell")
      (do
        (log :-compute! this)
        (dispose! this)
        (binding [*read-log* (volatile! #{})]
          (let [value (-compute this)
                next-deps (disj @*read-log* (name this))
                prev-deps (dependencies this)]
            (doseq [added (set/difference next-deps prev-deps)]
              (depend this added))
            (doseq [removed (set/difference prev-deps next-deps)]
              (remove-edge this removed))

            (reset-cell! this value)))))
    this)

  ISeqable
  (-seq [this]
    ((fn cell-seq
       [this]
       (cons @this
             (lazy-seq (cell-seq (-compute! this))))) (clone this))))



(defn purge-cell! [cell]
  (log :purge-cell! cell)
  (-dispose! cell)
  (d/transact! [[:db/retract-attr ::cells (name cell)]])
  (vswap! -cells dissoc (name cell))
  #_(-compute-dependents! cell)
  (remove-all cell))

(defn make-cell
  "Makes a new cell. Memoized by id."
  ([f]
   (make-cell (keyword "cells.temp" (d/unique-id)) f))
  ([id f]
   (or (get @-cells id)
       (let [cell (->Cell id f {} *eval-context*)]
         (log :make-cell id)
         (on-dispose *eval-context* #(purge-cell! cell))
         (vswap! -cells assoc id cell)
         (-compute! cell)))))

(defn reset-namespace [ns]
  (let [ns (str ns)
        the-cells (filterv (fn [[id cell]]
                             (= (namespace id) ns)) @-cells)]
    (doseq [cell (topo-sort (map second the-cells))]
      (purge-cell! cell))))
(vector)