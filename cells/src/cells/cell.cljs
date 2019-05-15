(ns cells.cell
  (:require [cells.linked-graph :as g]
            [chia.util :as u]
            [chia.reactive :as r]
            [applied-science.js-interop :as j]
            [chia.util.perf :as perf])
  (:require-macros cells.cell))

(def ^:dynamic *self*
  "Tracks the currently-evaluating cell."
  nil)

(def ^:dynamic *default-view*
  "Views are implemented as metadata on cells. A rendering environment
   (such as a notebook) can override the default view for cells
   without affecting views attached to particular cells."
  identity)

(def ^:dynamic *error-handler*
  "Errors are caught during evaluation of cells and their bound-fns.
   By default, we re-throw the error with information about where it
   originated. This behaviour can be overridden here."
  (fn [error]
    (throw (ex-info "Error evaluating cell" {:cell *self*} error))))

(defprotocol ICell
  "Marker protocol to determine if a thing is a cell")

(defn cell?
  "Returns true of `x` is a cell."
  [x]
  (satisfies? ICell x))

;;;;;;;;;;;;;;;
;;
;; Read logging

(def ^:private ^:dynamic *read-log* nil)

(declare maybe-activate)

(defn- log-observation! [cell]
  (maybe-activate cell)
  (when-not (identical? cell *self*)
    (some-> *read-log* (vswap! conj cell))
    (r/observe-simple! cell))
  cell)

;;;;;;;;;;;;;;;
;;
;; Async metadata
;;

(defn- get-async [cell]
  (log-observation! cell)
  (.. cell -state -async))

(declare mark-changed!)

(defn- set-async! [cell v]
  (let [state (.-state cell)
        before (.-async state)]
    (when-not (identical? before v)
      (set! (.-async state) v)
      (mark-changed! cell))
    cell))

(defn loading! [cell]
  (set-async! cell :loading))

(defn error! [cell error]
  (set-async! cell error)
  (*error-handler* error)
  cell)

(defn complete! [cell]
  (set-async! cell nil))

(defn status
  "Returns :error, :loading, or nil"
  [cell]
  (let [st (get-async cell)]
    (if (or (nil? st) (keyword? st)) st :error)))

(defn loading? [cell]
  (perf/identical? :loading (get-async cell)))

(defn complete? [cell]
  (nil? (get-async cell)))

(defn- error-st? [st]
  (and (some? st)
       (not (perf/identical? :loading st))))

(defn error [cell]
  (-> (get-async cell)
      (u/guard error-st?)))

(defn error? [cell]
  (error-st? (get-async cell)))

(def message (comp str error))

;;;;;;;;;;;;;;;;;;
;;
;; Lifecycle cleanup

(defn dispose!
  "Cleans up when a cell is deactivated."
  [cell]
  (doseq [f (vals (.. cell -state -on-dispose))]
    (f))
  (-> cell .-state .-on-dispose (set! nil))
  cell)

(defn on-dispose
  "Registers function `f` at `key` to be called when cell is deactivated."
  [cell key f]
  (assert (not (contains? (.. cell -state -on-dispose) key))
          "`on-dispose` was called with a key that already exists")
  (j/update-in! cell [.-state .-on-dispose] assoc key f))

;;;;;;;;;;;;;;;;;;
;;
;; Activation

(defn active? [cell]
  (.. cell -state -active))

(defn necessary?
  "Returns true if there is a path from `cell` to any watched cell"
  [cell]
  (not (empty? (g/immediate-dependents cell))))

(defn watched?
  "Returns true if `cell` is watched directly"
  [cell]
  (some? (.. cell -state -watches)))

(defn- maybe-deactivate
  "When a cell is unwatched and unnecessary, deactivate"
  [cell]
  {:pre [(cell? cell)]}
  (when (and (active? cell)
             (not (watched? cell))
             (not (necessary? cell)))
    (doseq [dep (g/immediate-dependencies cell)]
      (g/un-depend! cell dep))
    (dispose! cell)
    (-> cell .-state .-active (set! false)))
  cell)

(declare eval-and-set!)

(defn- maybe-activate
  "When a cell gains an observor, make sure it is active"
  [cell]
  (when-not (active? cell)
    (-> cell .-state .-active (set! true))
    (eval-and-set! cell))
  cell)

;;;;;;;;;;;;;;;;;;
;;
;; Evaluation

(defn- eval-cell [cell]
  (let [f (j/get-in cell [.-state .-f])]
    (try (f cell)
         (catch :default e
           (dispose! cell)                                  ;; always dispose on error
           (error! cell e)))))

(def ^:dynamic ^:private *to-evaluate* nil)
(def ^:dynamic ^:private *changed* nil)
(def ^:dynamic ^:private *evaluated* nil)

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

(defn notify-watches [cell]
  (let [st (.-state cell)
        value (.-value st)]
    (-notify-watches cell (.-prev-value st) value)
    (set! (.-prev-value st) value)))

(defn- stabilize!
  "Recomputes transitive dependents of `cell`.
   Returns set of changed cells."
  [cell]
  (binding [*to-evaluate* (volatile! (vec (g/immediate-dependents cell)))
            *changed* (volatile! #{cell})
            *evaluated* (volatile! #{})]
    (loop [i 0]
      (when-some [cell (nth @*to-evaluate* i nil)]
        (when (and (not (@*changed* cell))
                   (not (@*evaluated* cell)))
          (vswap! *evaluated* conj cell)
          (eval-and-set! cell))
        (recur (inc i))))
    @*changed*))

(defn- mark-changed! [cell]
  (if (nil? *to-evaluate*)
    (doseq [changed (stabilize! cell)]
      (notify-watches changed))
    (do (vswap! *changed* conj cell)
        (vswap! *to-evaluate* into (g/immediate-dependents cell))))
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
    (j/update! state .-dependents disj dep)
    (maybe-deactivate this))
  (immediate-dependencies [this]
    (.-dependencies state))
  (immediate-dependents [this]
    (.-dependents state))

  r/ITransitionSimple
  (on-transition [this observed?]
    (if observed?
      (do
        (-add-watch this ::r/transition
                    (fn [_ _ _ _] (r/invalidate-readers! this)))
        (maybe-activate this))
      (do
        (-remove-watch this ::r/transition)
        (maybe-deactivate this))))

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
    (j/update! state .-watches dissoc-empty key)
    (maybe-deactivate this))

  IDeref
  (-deref [this]
    (log-observation! this)
    (.-value state))

  IReset
  (-reset! [this newval]
    (let [oldval (.-value state)]
      (when (not= oldval newval)
        (j/assoc! state .-value newval)
        (mark-changed! this)))
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
                .-owner owner
                .-active false
                .-async nil)

         nil))

(defn update-cell* [cell f]
  (-> (.-state cell)
      (j/assoc! .-f f .-value nil))
  (eval-and-set! cell))

(defn cell*
  "Returns cell for function `f`. Optional `memo-key`, a string, will cause cell to
   be memoized on the currently-evaluating cell or reactive reader."
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