(ns cells.cell
  (:require [com.stuartsierra.dependency :as dep]
            [clojure.set :as set]
            [cells.util :as util]
            [cells.eval-context :as eval-context :refer [on-dispose dispose!]]
            [maria.show :refer [IShow] ])
  (:require-macros [cells.cell :refer [defcell cell cell-fn]]))

(def ^:dynamic *cell-stack* (list))
(def ^:dynamic *computing-dependents* false)
(def ^:dynamic *debug* false)
(defonce -cells (volatile! {}))


(defn log
  [& args]
  (when *debug* (prn args)))

;;;;
;; Mutating state
;;
;; Cells keep internal state. From within a cell, a user can call swap! or reset!.
;; A cell can only be mutated by its own copy of swap!/reset!, a limitation which
;; may prove difficult to maintain/manage.

(defprotocol ICellStore
  "Protocol for getting and putting cell values."
  (-put-value! [this value])
  (-get-value [this]))

(defprotocol ICellView
  "Protocol for shallow copies of cells with different views"
  (with-view [this view-fn]))

#_(defprotocol ISwap*
    "Swap value of cell. (avoid ISwap because this should not be a public interface)"
    (-swap-cell! [this f]
                 [this f a]
                 [this f a b]
                 [this f a b xs]))

(defprotocol ISet!
  (-set! [this newval]
         "Set cell value without notifying dependent cells."))

#_(defprotocol IReset*
    (-set-cell! [this newval]
                "Set cell value without notifying dependent cells.")
    (reset-cell! [this newval]
                 "Set cell value, notifying dependent cells."))

#_(defn swap-cell!
    "swap! a cell value"
    ([a f]
     (-swap-cell! a f))
    ([a f x]
     (-swap-cell! a f x))
    ([a f x y]
     (-swap-cell! a f x y))
    ([a f x y & more]
     (-swap-cell! a f x y more)))

(defn cell-name
  "Accepts a cell or its name, and returns its name."
  [cell]
  (cond-> cell
          (not (keyword? cell)) (name)))

;;;;
;; Dependencies are handled with stuart sierra's dependency library.
;;

(defonce dep-graph (volatile! (dep/graph)))

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

(def ^:dynamic *eval-context* (eval-context/new-context))

(defprotocol IReactiveCompute
  (-set-function! [this f])

  (-compute [this] "evaluate cell")
  (-compute-dependents! [this])
  (-compute! [this] "evaluate cell and set value")
  (-compute-with-dependents! [this] "evaluate cell and flow updates to dependent cells"))

(defprotocol IAsync
  "TO BE IMPROVED
  Just an experiment to see what it might feel like store and read status information on cells."
  (-set-async-state! [this status] [this status message] "Set loading status")

  (status [this])
  (message [this] "Read message associated with async state")

  (error? [this])
  (loading? [this]))

(def ^:dynamic *read-log* nil)



(declare make-cell)

(deftype Cell [id ^:mutable f ^:mutable state eval-context]

  ICellStore
  (-get-value [this] (:value state))
  (-put-value! [this value] (set! state (assoc state :value value)))

  IPrintWithWriter
  (-pr-writer [this writer _]
    (write-all writer (str "cell#" id)))

  INamed
  (-name [this] id)

  IShow
  (show [this] ((:view state) this))

  ICloneable
  (-clone [this]
    (make-cell (keyword (namespace id) (util/unique-id)) f state @this))

  ICellView
  (with-view [this view-fn]
    (let [cell (new Cell id f (assoc state :view view-fn) eval-context)]
      (-set! cell @this)
      cell))

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
    (-get-value this))

  ISet!
  (-set! [this newval]
    (log :-set-cell! this)
    (-put-value! this newval))
  IReset
  (-reset! [this newval]
    (log :-reset! this newval)
    (let [oldval @this]
      (-set! this newval)
      (-notify-watches this oldval newval))
    (-compute-dependents! this)
    newval)

  ISwap
  (-swap! [this f] (-reset! this (f @this)))
  (-swap! [this f a] (-reset! this (f @this a)))
  (-swap! [this f a b] (-reset! this (f @this a b)))
  (-swap! [this f a b xs] (-reset! this (apply f @this a b xs)))

  eval-context/IDispose
  (on-dispose [this f]
    (set! state (update state :dispose-fns conj f)))
  (-dispose! [this]
    (doseq [f (get state :dispose-fns)]
      (f))
    (set! state (update state :dispose-fns empty))
    this)

  IReactiveCompute
  (-compute-dependents! [this]
    (when-not *computing-dependents*
      (binding [*computing-dependents* true]
        (let [deps (transitive-dependents-sorted this) #_(topo-sort (transitive-dependents this))]
          (log :-compute-dependents! this deps)
          (doseq [cell-id deps]
            (some-> (@-cells cell-id)
                    (-compute-with-dependents!)))))))

  (-set-function! [this newf]
    (set! f newf))

  (-compute [this]
    (binding [*cell-stack* (cons this *cell-stack*)
              *eval-context* eval-context]
      (try
        (f this)
        (catch js/Error e
          (dispose! this)
          (throw e)))))

  (-compute! [this]
    (-reset! this (-compute this)))

  (-compute-with-dependents! [this]
    (if (= this (first *cell-stack*))
      (log :-compute-with-dependents! this "Return - in current cell")
      (do
        (log :-compute-with-dependents! this)
        (dispose! this)
        (binding [*read-log* (volatile! #{})]
          (let [value (-compute this)
                next-deps (disj @*read-log* (name this))
                prev-deps (dependencies this)]
            (doseq [added (set/difference next-deps prev-deps)]
              (depend this added))
            (doseq [removed (set/difference prev-deps next-deps)]
              (remove-edge this removed))
            (-reset! this value)))))
    this)

  ISeqable
  (-seq [this]
    ((fn cell-seq
       [this]
       (cons @this
             (lazy-seq (cell-seq (-compute-with-dependents! this))))) (clone this))))



(defn purge-cell! [cell]
  (log :purge-cell! cell)
  (eval-context/-dispose! cell)
  (-set! cell nil)
  (vswap! -cells dissoc (name cell))
  (remove-node cell)
  #_(let [dependents (map name (topo-sort (dependents cell)))]
      (js/setTimeout #(doseq [dep dependents]
                        (when-let [other-cell (get @-cells dep)]
                          (log :recompute-dependents-of-purged cell other-cell)
                          (-compute! other-cell)
                          ))))
  (log :purged-cell-dependents (dependents cell))
  #_(-compute-dependents! cell)
  )

(def empty-cell-state {:view        deref
                       :dispose-fns []})

(defn make-cell
  "Makes a new cell. Memoized by id."
  ([f]
   (make-cell (keyword "cells.temp" (util/unique-id)) f))
  ([id f] (make-cell id f {} nil))
  ([id f state value]
   (or (get @-cells id)
       (let [cell (->Cell id f (merge empty-cell-state state) *eval-context*)]
         (log :make-cell id)
         (on-dispose *eval-context* #(purge-cell! cell))
         (vswap! -cells assoc id cell)
         (-set! cell value)
         (-compute-with-dependents! cell)))))

(defn reset-namespace [ns]
  (let [ns (str ns)
        the-cells (filterv (fn [[id cell]]
                             (= (namespace id) ns)) @-cells)]
    (doseq [cell (topo-sort (map second the-cells))]
      (purge-cell! cell)
      (remove-all cell))))
(vector)