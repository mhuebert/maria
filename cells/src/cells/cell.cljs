(ns cells.cell
  (:require [cells.linked-graph :as g]
            [chia.util :as u]
            [chia.reactive :as r]
            [applied-science.js-interop :as j]
            [clojure.set :as set]
            [chia.util.perf :as perf])
  (:require-macros [cells.cell :as c]))

(def ^:dynamic *self* nil)


(defn self
  "Returns currently evaluating cell"
  []
  *self*)

(def ^:dynamic *default-view* identity)

(def ^:dynamic *error-handler*
  "Optionally catch errors occurring cell evaluation (and within cell/bound-fn)"
  (fn [error]
    (throw (ex-info "Error evaluating cell" {:cell *self*} error))))

(defprotocol ICell)

(defn cell? [x] (satisfies? ICell x))

(defprotocol IDispose
  (on-dispose [cell key f]
              "Register a callback to be fired when context is disposed.")
  (-dispose! [cell]))

(defn dispose! [cell]
  (when (satisfies? IDispose cell)
    (-dispose! cell)))

;;;;;;;;;;;;;;;
;;
;; simple STM
;;

(def ^:private ^:dynamic *tx-changes*
  ;; map of {cell, change-obj} for current transaction
  nil)

(defn- write-cell! [cell attrs-obj]
  {:post [(cell? %)]}
  (if (some? *tx-changes*)
    ;; adds `attr-obj` to pending changes for `cell` within current transaction
    (vswap! *tx-changes* update cell #(if (some? %) (j/extend! % attrs-obj) attrs-obj))
    (j/extend! (.-state cell) attrs-obj))
  cell)

(defn- cell-changed? [cell changes]
  ;; compares async metadata + value
  (or (and (j/contains? changes .-async)
           (not= (j/get changes .-async)
                 (c/get cell .-async)))
      (and (j/contains? changes .-value)
           (not= (j/get changes .-value)
                 (c/get cell .-value)))))

(defn- tx! [f]
  ;; evaluates `f` for cell side effects - commits changes to cell graph atomically
  (if (some? *tx-changes*)
    (f)
    (doseq [[cell changes] (binding [*tx-changes* (volatile! {})] (f) @*tx-changes*)]
      (let [changed? (cell-changed? cell changes)
            oldval (when changed? (c/get cell .-value))]
        (write-cell! cell changes)
        (when changed?
          (-notify-watches cell oldval (c/get cell .-value)))))))


;;;;;;;;;;;;;;;
;;
;; Read logging

(def ^:private ^:dynamic *read-log* nil)

(declare maybe-activate)

(defn log-read! [cell]
  {:pre [(cell? cell)] :post [(cell? %)]}
  (maybe-activate cell)
  (when-not (identical? cell *self*)
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
  (c/get cell .-async))

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
;; Activation

(defn active? [cell]
  (boolean (c/get cell .-active)))

(defn isolated?
  "Returns true if no cell depends on this cell"
  [cell]
  (empty? (g/immediate-dependents cell)))

(defn watched?
  "Returns true if cell has any number of watchers"
  [cell]
  (some? (c/get cell .-watches)))

(defn- maybe-deactivate
  "When a cell has no more observors, deactivate"
  [cell]
  (when (and (active? cell) (not (watched? cell)) (isolated? cell))
    (doseq [dep (g/immediate-dependencies cell)]
      (g/un-depend! cell dep))
    (-dispose! cell)
    (c/assoc! cell .-active false))
  cell)

(declare eval-and-set!)

(defn- maybe-activate
  "When a cell gains a new observor, make sure it is active"
  [cell]
  (when (not (active? cell))
    (c/assoc! cell .-active true)
    (tx! #(eval-and-set! cell)))
  cell)

;;;;;;;;;;;;;;;;;;
;;
;; Evaluation

(defn- eval-cell [cell]
  {:pre [(cell? cell)]}
  (let [f (c/get cell .-f)]
    (try (f cell)
         (catch :default e
           (-dispose! cell)
           (error! cell e)))))

(defn- eval-and-set! [cell]
  {:pre [(cell? cell)]}
  (when-not (identical? cell *self*)
    (binding [*self* cell
              *read-log* (volatile! #{})]
      (-dispose! cell)
      (let [value (eval-cell cell)
            next-deps (disj @*read-log* cell)]
        (g/transition-deps! cell next-deps)
        (-reset! cell value))))
  cell)

(def ^:dynamic ^:private *computing-dependents* false)

(defn- eval-dependents! [cell]
  {:pre [(cell? cell)] :post [(cell? %)]}
  (when-not *computing-dependents*
    (binding [*computing-dependents* true]
      (doseq [dep (g/dependents cell)]
        (eval-and-set! dep))))
  cell)

;;;;;;;;;;;;;;;;;;
;;
;; Cell views

(defn with-view
  "Attaches `view-fn` to cell"
  [cell view-fn]
  {:pre [(cell? cell)]}
  (vary-meta cell assoc :cell/view view-fn))

(defn get-view
  "Returns current view-fn for cell"
  [cell]
  {:pre [(cell? cell)]}
  (get (meta cell) :cell/view *default-view*))

(defn view
  "Returns view of cell"
  [cell]
  {:pre [(cell? cell)]}
  ((get-view cell) cell))

;;;;;;;;;;;;;;;;;;
;;
;; Cell type

(defn dissoc-empty [coll x]
  (let [out (dissoc coll x)]
    (if (empty? out) nil out)))

(def set-conj (fnil conj #{}))

;; expose
(def immediate-dependencies g/immediate-dependencies)
(def immediate-dependents g/immediate-dependents)
(def dependencies g/dependencies)
(def dependents g/dependents)

(deftype Cell [state meta]

  ICell

  g/ILinkedGraph
  (add-dependency! [this dep]
    (c/update! this .-dependencies set-conj dep))
  (remove-dependency! [this dep]
    (c/update! this .-dependencies disj dep))
  (add-dependent! [this dep]
    (c/update! this .-dependents set-conj dep))
  (remove-dependent! [this dep]
    (-> (c/update! this .-dependents disj dep)
        (maybe-deactivate)))
  (immediate-dependencies [this]
    (c/get this .-dependencies))
  (immediate-dependents [this]
    (c/get this .-dependents))

  r/ITransition
  (on-transition [this transition]
    (assert (cell? this))
    (case transition
      :observed (do
                  (-add-watch this ::r/transition (fn [_ _ _ _] (r/invalidate-readers! this)))
                  (maybe-activate this))
      :un-observed (do
                   (-remove-watch this ::r/transition)
                   (maybe-deactivate this))))

  IEquiv
  (-equiv [this other]
    (-equiv [this other]
            (if (instance? Cell other)
              (identical? state (.-state other))
              false)))

  IWithMeta
  (-with-meta [this m] (Cell. state m))

  IMeta
  (-meta [_] meta)

  IPrintWithWriter
  (-pr-writer [this writer _] (write-all writer (str "⚪️")))

  IDispose
  (on-dispose [this key f]
    (c/update! this .-onDispose assoc key f))
  (-dispose! [this]
    (doseq [f (vals (c/get this .-onDispose))]
      (f))
    (c/assoc! this .-onDispose nil)
    this)

  IWatchable
  (-notify-watches [this oldval newval]
    (doseq [f (vals (c/get this .-watches))]
      (f this oldval newval)))
  (-add-watch [this key f]
    (c/update! this .-watches (fnil assoc {}) key f))
  (-remove-watch [this key]
    (-> (c/update! this .-watches dissoc-empty key)
        (maybe-deactivate)))

  IDeref
  (-deref [this]
    (log-read! this)
    (c/get this .-value))

  IReset
  (-reset! [this newval]
    (when (not (identical? newval (c/get this .-value)))
      (tx!
       (fn []
         (write-cell! this (j/obj .-value newval))
         (eval-dependents! this))))
    newval)

  ISwap
  (-swap! [this f] (-reset! this (f (c/get this .-value))))
  (-swap! [this f a] (-reset! this (f (c/get this .-value) a)))
  (-swap! [this f a b] (-reset! this (f (c/get this .-value) a b)))
  (-swap! [this f a b xs] (-reset! this (apply f (c/get this .-value) a b xs))))


;;;;;;;;;;;;;;;;;;
;;
;; Cell construction

(defn- make-cell [f owner]
  (let [cell (Cell. (j/obj .-f f
                           .-value nil
                           .-dependencies #{}
                           .-dependents #{}
                           .-owner owner)
                    nil)]

    cell))

(defn cell*
  "Returns a cell for function `f` and `options`, an object of optional properties:
  - memo: string key for memoizing cell on current parent
  - def: true if cell is standalone, not memoized to any parent
  - updateExisting: an existing cell that should be updated with new function `f`"
  [f options]
  {:pre [(object? options)]}
  (let [def? (.-def? options)
        existing-cell (.-update-existing options)
        memo-key (.-memo-key options)
        owner (when-not def?
                ;; cells are either standalone (created via `defcell`)
                ;; or they are memoized on an "owner"
                (or *self*
                    r/*reader*))]

    (cond existing-cell
          (do (write-cell! existing-cell (j/obj .-f f
                                                .-value nil))
              (tx! #(eval-and-set! existing-cell))
              existing-cell)

          memo-key (u/memoized-on owner memo-key
                     (make-cell f owner))

          :else (make-cell f owner))))

(defn cell
  [key value]
  (assert key "Cells created by functions require a key")
  (cell* (constantly value) (j/obj .-memo-key (str "#" (hash key)))))
