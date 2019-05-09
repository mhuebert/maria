(ns cells.cell
  (:require [cells.linked-graph :as g]
            [chia.util :as u]
            [chia.reactive :as r]
            [applied-science.js-interop :as j]
            [chia.util.perf :as perf])
  (:require-macros [cells.cell :as c]))

(def ^:dynamic *self* nil)

(def ^:dynamic *default-view*
  "Views are implemented as metadata on cells. A rendering environment
   (such as a notebook) can override the default view for cells
   without affecting views attached to particular cells."
  identity)

(def ^:dynamic *error-handler*
  (fn [error]
    (throw (ex-info "Error evaluating cell" {:cell *self*} error))))

(defprotocol ICell
  "Marker protocol to determine if a thing is a cell")

(defn cell? [x]
  (satisfies? ICell x))

;;;;;;;;;;;;;;;
;;
;; Read logging

(def ^:private ^:dynamic *read-log* nil)

(declare ensure-active)

(defn- log-read! [cell]
  (ensure-active cell)
  (when-not (identical? cell *self*)
    (some-> *read-log* (vswap! conj cell))
    (r/log-read! cell))
  cell)

;;;;;;;;;;;;;;;
;;
;; Async metadata
;;
;; async-state is represented as a [loading? error] tuple.

(defn loading! [cell]
  (c/set-watched! cell .-async [true nil]))

(defn error! [cell error]
  (*error-handler* error)
  (c/set-watched! cell .-async [false error]))

(defn complete! [cell]
  (c/set-watched! cell .-async [false nil]))

(defn- async-state [cell]
  ;; reading async-state is equivalent to reading a cell's value,
  ;; as reactivity is concerned.
  (log-read! cell)
  (j/get-in cell [.-state .-async]))

(defn status
  "Returns :error, :loading, or nil"
  [cell]
  (let [st (async-state cell)]
    (cond (some? (nth st 1)) :error
          (true? (nth st 0)) :loading)))

(defn loading? [cell]
  (= :loading (status cell)))

(defn error [cell]
  (some-> (async-state cell) (nth 1)))

(def error? (comp boolean error))

;;backwards-compat
(def message (comp str error))

(defn complete? [cell]
  (let [[loading? error] (async-state cell)]
    (and (not loading?)
         (nil? error))))

;;;;;;;;;;;;;;;;;;
;;
;; Lifecycle cleanup

(defn dispose!
  "Cleans up when a cell is deactivated."
  [cell]
  (doseq [f (vals (.. cell -state -on-dispose))]
    (f))
  (j/assoc-in! cell [.-state .-on-dispose] nil)
  cell)

(defn on-dispose
  "Registers function `f` at `key` to be called when cell is deactivated."
  [cell key f]
  (j/update-in! cell [.-state .-on-dispose] assoc key f))

;;;;;;;;;;;;;;;;;;
;;
;; Activation

(defn active? [cell]
  (boolean (j/get-in cell [.-state .-active])))

(defn necessary?
  "Returns true if there is a path from `cell` to a watched cell"
  [cell]
  (not (empty? (g/immediate-dependents cell))))

(defn watched?
  "Returns true if `cell` has at least one watcher"
  [cell]
  (some? (j/get-in cell [.-state .-watches])))

(defn- deactivate
  "When a cell has no more observors, deactivate"
  [cell]
  (when (and (active? cell)
             (not (watched? cell))
             (not (necessary? cell)))
    (doseq [dep (g/immediate-dependencies cell)]
      (g/un-depend! cell dep))
    (dispose! cell)
    (j/assoc-in! cell [.-state .-active] false))
  cell)

(declare eval-and-set!)

(defn- ensure-active
  "When a cell gains an observor, make sure it is active"
  [cell]
  (when-not (active? cell)
    (j/assoc-in! cell [.-state .-active] true)
    (eval-and-set! cell))
  cell)

;;;;;;;;;;;;;;;;;;
;;
;; Evaluation

(defn- eval-cell [cell]
  (let [f (j/get-in cell [.-state .-f])]
    (try (f cell)
         (catch :default e
           ;; always clean up after an error
           (dispose! cell)
           (error! cell e)))))

(defn- eval-and-set! [cell]
  (when-not (identical? cell *self*)
    (binding [*self* cell
              *read-log* (volatile! #{})]
      (dispose! cell)
      (let [value (eval-cell cell)
            next-deps (disj @*read-log* cell)]
        (g/transition-deps! cell next-deps)
        (-reset! cell value))))
  cell)

;; marker, to avoid duplicate computation
(def ^:dynamic ^:private *computing-dependents* false)

(defn- eval-dependents! [cell]
  (when (false? *computing-dependents*)
    (binding [*computing-dependents* true]
      (doseq [dep (g/dependents cell)]
        ;; POSSIBLE IMPROVEMENT
        ;; instead of eagerly computing the entire tree of dependent cells,
        ;; only follow cells that have changed in a given cycle.
        (eval-and-set! dep))))
  cell)

;;;;;;;;;;;;;;;;;;
;;
;; Cell views

(defn with-view
  "Attaches `view-fn` to the metadata of `cell`"
  [cell view-fn]
  (vary-meta cell assoc :cell/view view-fn))

(defn view-fn
  "Returns current view-fn for cell"
  [cell]
  (get (meta cell) :cell/view *default-view*))

(defn view
  "Returns view of `cell`"
  [cell]
  ((view-fn cell) cell))

;;;;;;;;;;;;;;;;;;
;;
;; Cell type

(defn dissoc-empty [coll x]
  (let [out (dissoc coll x)]
    (if (empty? out) nil out)))

(def set-conj (fnil conj #{}))

(deftype Cell [state meta]

  ICell

  g/ILinkedGraph
  (add-dependency! [this dep]
    (j/update! state .-dependencies set-conj dep))
  (remove-dependency! [this dep]
    (j/update! state .-dependencies disj dep))
  (add-dependent! [this dep]
    (j/update! state .-dependents set-conj dep))
  (remove-dependent! [this dep]
    (-> (j/update! state .-dependents disj dep)
        (deactivate)))
  (immediate-dependencies [this]
    (.-dependencies state))
  (immediate-dependents [this]
    (.-dependents state))

  r/ITransition
  (on-transition [this transition]
    (case transition
      :observed (do
                  (-add-watch this ::r/transition
                              (fn [_ _ _ _] (r/invalidate-readers! this)))
                  (ensure-active this))
      :un-observed (do
                     (-remove-watch this ::r/transition)
                     (deactivate this))))

  IEquiv
  (-equiv [this other]
    (-equiv [this other]
            (and (instance? Cell other)
                 (identical? state (.-state other)))))

  IWithMeta
  (-with-meta [this m] (Cell. state m))

  IMeta
  (-meta [_] meta)

  IPrintWithWriter
  (-pr-writer [this writer _] (write-all writer (str "⚪️")))

  IWatchable
  (-notify-watches [this oldval newval]
    (doseq [f (vals (.-watches state))]
      (f this oldval newval)))
  (-add-watch [this key f]
    (j/update! state .-watches (fnil assoc {}) key f))
  (-remove-watch [this key]
    (-> (j/update! state .-watches dissoc-empty key)
        (deactivate)))

  IDeref
  (-deref [this]
    (log-read! this)
    (.-value state))

  IReset
  (-reset! [this newval]
    (when-not (= newval (.-value state))
      (c/set-watched! this .-value newval)
      (eval-dependents! this))
    newval)

  ISwap
  (-swap! [this f] (-reset! this (f (j/get-in this [.-state .-value]))))
  (-swap! [this f a] (-reset! this (f (j/get-in this [.-state .-value]) a)))
  (-swap! [this f a b] (-reset! this (f (j/get-in this [.-state .-value]) a b)))
  (-swap! [this f a b xs] (-reset! this (apply f (j/get-in this [.-state .-value]) a b xs))))


;;;;;;;;;;;;;;;;;;
;;
;; Cell construction

(defn- make-cell [f owner]
  (Cell. (j/obj .-f f
                .-value nil
                .-dependencies #{}
                .-dependents #{}
                .-owner owner)

         nil))

(defn update-cell* [cell f]
  (j/assoc! (.-state cell) .-f f .-value nil)
  (eval-and-set! cell))

(defn cell*
  "Returns a cell for function `f` and `options`, an object of optional properties:
  - memo: string key for memoizing cell on current parent
  - def: true if cell is standalone, not memoized to any parent
  - updateExisting: an existing cell that should be updated with new function `f`"
  ([f] (cell* f nil))
  ([f memo-key]
   (let [owner (or *self*
                   r/*reader*)]
     (if (some? memo-key)
       (u/memoized-on owner memo-key (make-cell f owner))
       (make-cell f owner)))))

(defn cell
  [key value]
  (assert key "Cells created by functions require a key")
  ;; TODO -
  ;; `hash` does not guarantee uniqueness
  (cell* (constantly value) (str "#" (hash key))))

;; Expose graph fns

(def immediate-dependencies g/immediate-dependencies)
(def immediate-dependents g/immediate-dependents)
(def dependencies g/dependencies)
(def dependents g/dependents)