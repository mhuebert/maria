(ns cells.cell
  (:require [cells.owner :as owner]
            [chia.util :as u]
            [chia.reactive :as r]
            [applied-science.js-interop :as j]
            [clojure.set :as set]
            [chia.util.perf :as perf])
  (:require-macros [cells.cell :as c]))

(def ^:dynamic *cell* nil)

(defn self
  "Returns currently evaluating cell"
  []
  *cell*)

(def ^:dynamic *default-view* identity)

(def ^:dynamic *error-handler*
  "Optionally catch errors occurring cell evaluation (and within cell/bound-fn)"
  (fn [error]
    (throw (ex-info "Error evaluating cell" {:cell *cell*} error))))

;;;;;;;;;;;;;;;
;;
;; simple STM
;;

(def ^:private ^:dynamic *tx-changes*
  ;; map of {cell, change-obj} for current transaction
  nil)

(defn- write-cell! [cell attrs-obj]
  (if *tx-changes*
    ;; adds `attr-obj` to pending changes for `cell` within current transaction
    (vswap! *tx-changes* update cell #(if (some? %) (j/extend! % attrs-obj) attrs-obj))
    (j/extend! (.-state cell) attrs-obj)))

(defn- cell-changed? [cell changes]
  ;; compares async metadata + value
  (or (and (j/contains? changes .-async)
           (not= (j/get changes .-async)
                 (c/read cell .-async)))
      (and (j/contains? changes .-value)
           (not= (j/get changes .-value)
                 (c/read cell .-value)))))

(defn- tx! [f]
  ;; evaluates `f` for cell side effects - commits changes to cell graph atomically
  (if (some? *tx-changes*)
    (f)
    (doseq [[cell changes] (binding [*tx-changes* (volatile! {})] (f) @*tx-changes*)]
      (let [changed? (cell-changed? cell changes)]
        (write-cell! cell changes)
        (when changed?
          (r/invalidate-readers! cell))))))


;;;;;;;;;;;;;;;
;;
;; Read logging

(def ^:private ^:dynamic *read-log* nil)

(declare maybe-activate)

(defn log-read! [cell]
  (maybe-activate cell)
  (when-not (identical? cell *cell*)
    (some-> *read-log* (vswap! conj cell))
    (r/log-read! cell))
  cell)

;;;;;;;;;;;;;;;
;;
;; Async metadata

(defn loading! [cell]
  (tx! #(c/assoc! cell .-async [true nil])))

(defn error! [cell error]
  (*error-handler* error)
  (tx! #(c/assoc! cell .-async [false error])))

(defn complete! [cell]
  (tx! #(c/assoc! cell .-async [false nil])))

(defn- async-state [cell]
  (log-read! cell)
  (c/read cell .-async))

(defn status [cell]
  (let [st (async-state cell)]
    (cond (some? (nth st 1)) :error
          (true? (nth st 0)) :loading)))

(defn loading? [cell]
  (perf/identical? :loading (status cell)))

(defn error [cell]
  (some-> (async-state cell) (nth 1)))

(def error? (comp boolean error))
(def message (comp str error))

(defn complete? [cell]
  (let [[loading? error] (async-state cell)]
    (and (not loading?)
         (nil? error))))

;;;;;;;;;;;;;;;;;;
;;
;; Dependency graph

(def set-conj (fnil conj #{}))

(defprotocol IGraph
  (-add-dependency! [cell dep])
  (-remove-dependency! [cell dep])
  (-add-dependent! [cell dep])
  (-remove-dependent! [cell dep])
  (-immediate-dependencies [cell])
  (-immediate-dependents [cell]))

(defn transitive-sorted [f]
  (fn -transitive-sorted
    ([cell]
     (->> cell
          (-transitive-sorted [#{cell} []])
          (second)))
    ([[seen results] cell]
     (let [new (set/difference (f cell) seen)]
       (reduce -transitive-sorted
               [(into (conj seen cell) new)
                (-> results
                    (cond-> (not (seen cell)) (conj cell))
                    (into new))]
               new)))))

(def dependencies (transitive-sorted -immediate-dependencies))
(def dependents (transitive-sorted -immediate-dependents))

(defn depend! [cell dep]
  (-add-dependency! cell dep)
  (-add-dependent! dep cell))

(defn un-depend! [cell dep]
  (-remove-dependency! cell dep)
  (-remove-dependent! dep cell))

(defn transition-deps! [cell next-dependency-nodes]
  (let [prev-dependencies (c/read cell .-dependencies)]
    (doseq [added (set/difference next-dependency-nodes prev-dependencies)]
      (depend! cell added))
    (doseq [removed (set/difference prev-dependencies next-dependency-nodes)]
      (un-depend! cell removed))
    nil))

(defn active? [cell]
  (c/read cell .-active))

(defn isolated? [cell]
  (and (false? (c/read cell .-has_readers))
       (empty? (-immediate-dependents cell))))

(defn maybe-deactivate [cell]
  (when (and (active? cell) (isolated? cell))
    (doseq [dep (-immediate-dependencies cell)]
      (un-depend! cell dep))
    (owner/-dispose! cell)
    (c/assoc! cell .-active false))
  cell)

(declare eval-and-set!)

(defn maybe-activate [cell]
  (when (not (active? cell))
    (c/assoc! cell .-active true)
    (tx! #(eval-and-set! cell)))
  cell)

;;;;;;;;;;;;;;;;;;
;;
;; Promises

(defn handle-promise! [cell promise]

  (comment
   (when-let [old-promise (c/read cell .-promise)]
     ;; how to 'dispose' of a promise?
     ))

  (c/assoc! cell .-promise promise)
  (let [done? (volatile! false)
        wrap-cb (fn [f]
                  (reset! done? true)
                  (when (identical? (c/read cell .-promise)
                                    promise)
                    (f)))]
    (-> promise
        (j/call :then (wrap-cb #(-reset! cell %)))
        (j/call :catch (wrap-cb #(error! cell %))))
    (when-not @done?
      (loading! cell)
      (-reset! cell nil))))

;;;;;;;;;;;;;;;;;;
;;
;; Cell evaluation

(defn- eval-cell [cell]
  (let [f (c/read cell .-f)]
    (try (f cell)
         (catch :default e
           (owner/dispose! cell)
           (error! cell e)))))

(defn handle-value [cell value]
  (if (u/promise? value)
    (handle-promise! cell value)
    (-reset! cell value)))

(defn- eval-and-set! [cell]
  (if (identical? cell *cell*)
    cell
    (binding [*cell* cell
              *read-log* (volatile! #{})
              owner/*owner* (c/read cell .-owner)]
      (owner/-dispose! cell)
      (let [value (eval-cell cell)
            next-deps (disj @*read-log* cell)]
        (transition-deps! cell next-deps)
        (handle-value cell value))
      cell)))

(def ^:dynamic ^:private *computing-dependents* false)

(defn- eval-dependents! [cell]
  (when-not *computing-dependents*
    (binding [*computing-dependents* true]
      (doseq [dep (dependents cell)]
        (eval-and-set! dep))))
  cell)

;;;;;;;;;;;;;;;;;;
;;
;; Cell views

(defn with-view
  "Attaches `view-fn` to cell"
  [cell view-fn]
  (vary-meta cell assoc :cell/view view-fn))

(defn get-view [cell]
  (get (meta cell) :cell/view *default-view*))

(defn view [cell]
  ((get-view cell) cell))

;;;;;;;;;;;;;;;;;;
;;
;; Cell type

(deftype Cell [state meta]

  IGraph
  (-add-dependency! [this dep]
    (c/assoc! this .-dependencies (set-conj (-immediate-dependencies this) dep)))
  (-remove-dependency! [this dep]
    (c/assoc! this .-dependencies (disj (-immediate-dependencies this) dep)))
  (-add-dependent! [this dep]
    (c/assoc! this .-dependents (set-conj (-immediate-dependents this) dep)))
  (-remove-dependent! [this dep]
    (-> this
        (c/assoc! .-dependents (disj (-immediate-dependents this) dep))
        (maybe-deactivate)))
  (-immediate-dependencies [this]
    (c/read this .-dependencies))
  (-immediate-dependents [this]
    (c/read this .-dependents))

  r/ITransition
  (on-transition [this transition]
    (c/assoc! this .-has_readers (case transition :added true :removed false))
    (case transition
      :added (maybe-activate this)
      :removed (maybe-deactivate this)))

  IEquiv
  (-equiv [this other]
    (-equiv [this other]
            (if (instance? Cell other)
              (identical? state (.-state other))
              false)))

  IWithMeta
  (-with-meta [this m]
    (if (identical? m meta)
      this
      (Cell. state m)))

  IMeta
  (-meta [_] meta)

  IPrintWithWriter
  (-pr-writer [this writer _] (write-all writer (str "⚪️")))

  owner/IDispose
  (on-dispose [this key f]
    (c/update! this .-internal update :on-dispose assoc key f))
  (-dispose! [this]
    (doseq [f (vals (:on-dispose (c/read this .-internal)))]
      (f))
    (c/update! this .-internal dissoc :on-dispose)
    this)

  IDeref
  (-deref [this]
    (log-read! this)
    (c/read this .-value))

  IReset
  (-reset! [this newval]
    (when (not (identical? newval (c/read this .-value)))
      (tx!
       (fn []
         #_(complete! this)
         (write-cell! this (j/obj .-value newval))
         (eval-dependents! this))))
    newval)

  ISwap
  (-swap! [this f] (-reset! this (f (c/read this .-value))))
  (-swap! [this f a] (-reset! this (f (c/read this .-value) a)))
  (-swap! [this f a b] (-reset! this (f (c/read this .-value) a b)))
  (-swap! [this f a b xs] (-reset! this (apply f (c/read this .-value) a b xs))))

;;;;;;;;;;;;;;;;;;
;;
;; Cell construction

(defn- make-cell [f owner def?]
  (let [cell (Cell. (j/obj .-f f
                           .-value nil
                           .-dependencies #{}
                           .-dependents #{}
                           .-owner owner
                           .-internal {})
                    nil)]

    cell))

(defn cell*
  "Returns a new cell, or an existing cell if `id` has been seen before.
  `f` should be a function that, given the cell's previous value, returns its next value.
  `state` is not for public use."
  [f {:as   options
      :keys [prev-cell
             def?
             memo-key]}]
  (let [owner (when-not def?
                (or *cell*
                    owner/*owner*
                    r/*reader*))]

    (assert (or def? memo-key) "Anonymous cells must provide `memo-key`")

    (cond prev-cell
          (do (write-cell! prev-cell (j/obj .-f f
                                            .-value nil))
              (tx! #(eval-and-set! prev-cell))
              prev-cell)

          def? (make-cell f owner def?)

          memo-key (u/memoized-on owner memo-key
                     (make-cell f owner def?))

          :else (throw (js/Error. (str "Invalid arguments to `cell*` " options))))))

(defn cell
  [key value]
  (assert key "Cells created by functions require a :key")
  (cell* (constantly value) {:memo-key (str "#" (hash key))}))
