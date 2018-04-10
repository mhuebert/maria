(ns cells.cell
  (:require [com.stuartsierra.dependency :as dep]
            [clojure.set :as set]
            [cells.util :as util]
            [cells.eval-context :as eval-context :refer [on-dispose dispose!]])
  (:require-macros [cells.cell]))

(def ^:dynamic *cell-stack* (list))
(def ^:dynamic *computing-dependents* false)
(def ^:dynamic *debug* false)
(defonce -cells (volatile! {}))

(defn log
  [& args]
  (when *debug* (prn args)))

(defprotocol ICellStore
  "Protocol for getting and putting cell values.
  This allows an interactive environment to control how cell values are persisted,
  and to facilitate reactivity."
  (put-value! [this value])
  (get-value [this])
  (invalidate! [this]))

(defprotocol ICellView
  "Cell views are attached as metadata & allow multiple (different) views on identical cells."
  (view [this])
  (with-view [this view-fn] "Wraps a cell with a view"))

(defprotocol IRenderHiccup
  "Protocol for"
  (render-hiccup [this]))

(defprotocol ISet!
  (-set! [this newval]
         "Set cell value without notifying dependent cells."))

(defn- cell-name
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
  ;; maybe make this faster by pruning the graph?
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


;; temporary, experimental purposes
(def ^:dynamic *allow-deref-while-loading?* true)

(defprotocol IStatus
  "Experimental: protocol to store 'status' information on a cell.
  Differs from metadata, in that mutations to the status of a cell
  propagate to all copies."
  (status! [this]
           [this status]
           [this status message] "Set loading status")

  (status [this])
  (message [this] "Read message associated with async state")

  (error? [this])
  (loading? [this]))

(defn status-view
  "Experimental: cells that implement IStatus can 'show' themselves differently depending on status."
  [this]
  (render-hiccup (case (status this)
                   :loading [:.cell-status
                             [:.circle-loading
                              [:div]
                              [:div]]]
                   :error [:div.pa3.bg-darken-red.br2
                           (or (message this)
                               [:.circle-error
                                [:div]
                                [:div]])]
                   nil)))

(defn default-view [self]
  (if (status self)
    (status-view self)
    @self))

(def ^:dynamic *read-log*
  "Dynamic var to track dependencies of a cell while its function is evaluated."
  nil)

(declare cell*)

(deftype Cell
  [id ^:mutable f ^:mutable state eval-context __meta]

  ICellStore
  (get-value [this] (:value state))
  (put-value! [this value] (set! state (assoc state :value value)))
  (invalidate! [this])

  IWithMeta
  (-with-meta [this new-meta]
    (-> (new Cell id f state eval-context new-meta)
        (-set! @this)))

  IMeta
  (-meta [_] __meta)

  IPrintWithWriter
  (-pr-writer [this writer _]
    (write-all writer (str "cell#" id)))

  INamed
  (-name [this] id)

  ICloneable
  (-clone [this]
    (cell* (keyword (namespace id) (util/unique-id)) f state))

  ICellView
  (view [this]
    ((get __meta :cell/view default-view) this))
  (with-view [this view-fn]
    (with-meta this (assoc (meta this) :cell/view view-fn)))

  IStatus
  (status! [this]
    (status! this nil nil))
  (status! [this value]
    (status! this value nil))
  (status! [this value message]
    (set! state (assoc state
                  :cell.status/status value
                  :cell.status/message message))
    (invalidate! this)
    (-compute-dependents! this))
  (status [this]
    @this
    (:cell.status/status state))
  (message [this]
    @this
    (:cell.status/message state))
  (loading? [this] (= :loading (status this)))
  (error? [this] (= :error (status this)))

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

    (cond-> this (or *allow-deref-while-loading?*
                     (not= (:cell.status/status state) :loading))
            (get-value)))

  ISet!
  (-set! [this newval]
    (log ::-set-cell! this)
    (put-value! this newval)
    this)
  IReset
  (-reset! [this newval]
    (log ::-reset! this newval)
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
      (log ::-compute-with-dependents! this "Return - in current cell")
      (do
        (log ::-compute-with-dependents! this)
        (dispose! this)
        (binding [*read-log* (volatile! #{})]
          (let [value (-compute this)
                next-dependencies (disj @*read-log* (name this))
                prev-dependencies (dependencies this)]
            (doseq [added (set/difference next-dependencies prev-dependencies)]
              (depend this added))
            (doseq [removed (set/difference prev-dependencies next-dependencies)]
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
  (log ::purge-cell! cell)
  (eval-context/-dispose! cell)
  (-set! cell nil)
  (vswap! -cells dissoc (name cell))
  (remove-node cell)
  (log :purged-cell-dependents (dependents cell)))


(def empty-cell-state {:initial-value nil
                       :dispose-fns   []})

(defn cell*
  "Should not be called directly, use `cell` macro or function instead.

  Returns a new cell, or an existing cell if `id` has been seen before.
  `f` should be a function that, given the cell's previous value, returns its next value.
  `state` is not for public use."
  ([f]
   (cell* (keyword "cells.temp" (str "_" (util/unique-id))) f))
  ([id f] (cell* id f {}))
  ([id f state]
   (or (get @-cells id)
       (let [cell (->Cell id f (merge empty-cell-state state) *eval-context* {})]
         (log ::cell* id)
         (on-dispose *eval-context* #(purge-cell! cell))
         (vswap! -cells assoc id cell)
         (-set! cell (:initial-value state))
         (-compute-with-dependents! cell)))))

(defn cell
  "Returns a cell, given initial `value` and a `key` which should be unique per cell container."
  [key value]
  (let [cell-container-id (some-> (first *cell-stack*)
                                  (name))
        ns (if cell-container-id
             (namespace cell-container-id)
             "cells.temp")
        prefix (if cell-container-id (name cell-container-id) "base")]
    (cell* (keyword ns (str "_" prefix "." key))
           (constantly value))))

(defn reset-namespace
  "Purges and removes all cells in the provided namespace."
  [ns]
  (let [ns (str ns)
        the-cells (filterv (fn [[id cell]]
                             (= (namespace id) ns)) @-cells)]
    (doseq [cell (topo-sort (map second the-cells))]
      (purge-cell! cell)
      (remove-all cell))))