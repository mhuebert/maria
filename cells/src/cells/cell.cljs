(ns cells.cell
  (:require [cells.util :as util]
            [cells.owner :as owner]
            [chia.util :as u]
            [chia.reactive :as r]
            [applied-science.js-interop :as j]
            [goog.object :as gobj]
            [clojure.set :as set])
  (:require-macros [cells.cell :as c]))

;;;;;;;;;;;;;;;;;;
;;
;; Dynamic state

(def ^:dynamic *cell*
  "Currently evaluating cell"
  nil)

(def ^:dynamic ^:private *read-log*
  "Track cell dependencies during eval" nil)

(def ^:dynamic *tx-log*
  "Track cell changes during tx"
  nil)

(def ^:dynamic *error-handler*
  (fn [error] (throw error)))

(def ^:dynamic *default-view* identity)

(defn handle-error [error]
  (prn :handle-error error)
  (*error-handler* (ex-info "Error evaluating cell" {:cell *cell*} error)))

(defn- tx-cell [cell]
  (when (some? *tx-log*)
    (get @*tx-log* cell)))

(defn- notify-cell-changed! [cell oldval newval]
  (r/invalidate-readers! cell)
  (-notify-watches cell oldval newval))

(defn- mutate-cell! [cell attr-obj]
  {:pre [(object? attr-obj)]}
  (if *tx-log*
    (vswap! *tx-log* update cell #(j/extend! % attr-obj))
    (j/extend! (.-state cell) attr-obj)))

(defn- tx! [f]
  (let [tx-log (binding [*tx-log* (volatile! {})]
                 (f)
                 @*tx-log*)]
    (doseq [[cell changes] tx-log]
      (let [oldval (c/read cell .-value)
            newval (j/get changes .-value)
            changed? (or (and (j/contains? changes .-async)
                              (not= (j/get changes .-async)
                                    (c/read cell .-async)))
                         (and (j/contains? changes .-value)
                              (not= (j/get changes .-value)
                                    (c/read cell .-value))))]
        (mutate-cell! cell changes)
        (when changed?
          (notify-cell-changed! cell oldval newval))))
    nil))

(defn log-read! [cell]
  (when-not (identical? cell *cell*)
    (when *read-log*
      (vswap! *read-log* conj cell))
    (r/log-read! cell))
  cell)


;;;;;;;;;;;;;;;;;;
;;
;; Cell status

(declare eval-dependents!)

(defn update-internal! [cell f & args]
  (c/update! cell .-internal #(apply f % args))

  ;; do not propagate?
  #_(tx! #(eval-dependents! cell))

  (r/invalidate-readers! cell))

(defn loading! [cell]
  (tx! #(c/assoc! cell .-async [true nil])))

(defn error! [cell error]
  (handle-error error)
  (tx! #(c/assoc! cell .-async [false error])))

(defn complete! [cell]
  (tx! #(c/assoc! cell .-async [false nil])))

(defn- async-state [cell]
  (log-read! cell)
  (c/read cell .-async))

(defn loading? [cell]
  (some-> (async-state cell) (nth 0)))

(defn error [cell]
  (some-> (async-state cell) (nth 1)))

(def error? (comp boolean error))

(defn complete? [cell]
  (let [[loading? error] (async-state cell)]
    (and (not loading?)
         (nil? error))))

;;;;;;;;;;;;;;;;;;
;;
;; Dependency graph

(def set-conj (fnil conj #{}))

(defn immediate-dependencies [cell]
  (c/read cell .-dependencies))

(defn immediate-dependents [cell]
  (c/read cell .-dependents))

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

(def dependents (transitive-sorted immediate-dependents))
(def dependencies (transitive-sorted immediate-dependencies))

(defn depend! [cell dep]
  (c/assoc! cell .-dependencies
            (set-conj (immediate-dependencies cell) dep))
  (c/assoc! dep .-dependents
            (set-conj (immediate-dependents dep) cell)))

(defn remove-edge! [cell dep]
  (c/assoc! cell .-dependencies
            (disj (immediate-dependencies cell) dep))
  (c/assoc! dep .-dependents
            (disj (immediate-dependents dep) cell)))

(defn transition-deps! [cell next-dependency-nodes]
  (let [prev-dependencies (c/read cell .-dependencies)]
    (doseq [added (set/difference next-dependency-nodes prev-dependencies)]
      (depend! cell added))
    (doseq [removed (set/difference prev-dependencies next-dependency-nodes)]
      (remove-edge! cell removed))
    nil))

;;;;;;;;;;;;;;;;;;
;;
;; Cell evaluation

(defn handle-promise! [cell promise]
  ;; how to 'dispose' of a promise?
  (when-let [old-promise (c/read cell .-promise)]
    (c/assoc! cell .-promise promise))
  (let [done? (volatile! false)
        wrap-cb (fn [f]
                  (when (identical? (c/read cell .-promise)
                                    promise)
                    (f)))]
    (-> promise
        (j/call :then (wrap-cb #(-reset! cell %)))
        (j/call :catch (wrap-cb #(error! cell %))))
    (when-not @done?
      (loading! cell)
      (-reset! cell nil))))

(defn- eval-cell [cell]
  (let [f (c/read cell .-f)]
    (try (f cell)
         (catch :default e
           (owner/dispose! tx-cell)
           (error! cell e)))))

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
        (if (u/promise? value)
          (handle-promise! cell value)
          (-reset! cell value)))
      cell)))

(def ^:dynamic ^:private *computing-dependents* false)

(defn- eval-dependents! [cell]
  (when-not *computing-dependents*
    (binding [*computing-dependents* true]
      (doseq [cell (dependents cell)]
        (eval-and-set! cell))))
  cell)

;;;;;;;;;;;;;;;;;;
;;
;; Cell views


(defn get-view [cell]
  (get (meta cell) :cell/view *default-view*))

(defn view [cell]
  ((get-view cell) cell))

(defn with-view
  "Attaches `view-fn` to cell"
  [cell view-fn]
  (vary-meta cell assoc :cell/view view-fn))

;;;;;;;;;;;;;;;;;;
;;
;; Cell type

(deftype Cell [state meta]

  Object
  (equiv [this other]
    (-equiv this other))

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
    (c/update! this .-internal update ::on-dispose assoc key f))
  (-dispose! [this]
    (doseq [f (vals (::on-dispose (c/read this .-internal)))]
      (f))
    (c/update! this .-internal dissoc ::on-dispose)
    this)

  ;; Atom behaviour

  IWatchable
  (-notify-watches [this oldval newval]
    (doseq [f (vals (::watches (c/read this .-internal)))]
      (f this oldval newval)))
  (-add-watch [this key f]
    (c/update! this .-internal update ::watches assoc key f))
  (-remove-watch [this key]
    (c/update! this .-internal update ::watches dissoc key))

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
         (mutate-cell! this (j/obj .-value newval))
         (eval-dependents! this))))
    newval)

  ISwap
  (-swap! [this f] (-reset! this (f (c/read this .-value))))
  (-swap! [this f a] (-reset! this (f (c/read this .-value) a)))
  (-swap! [this f a b] (-reset! this (f (c/read this .-value) a b)))
  (-swap! [this f a b xs] (-reset! this (apply f (c/read this .-value) a b xs)))

  ILookup
  (-lookup [this k]
    (-lookup this k nil))
  (-lookup [this k not-found]
    (r/log-read! this)
    (case k
      :async/loading? (some-> (c/read this .-async) (nth 0))
      :async/error (some-> (c/read this .-async) (nth 1))
      (throw (js/Error. (str "Unknown key on cell: " k))))))

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

    (when owner
      (owner/on-dispose owner cell #(owner/-dispose! cell)))

    (tx! #(eval-and-set! cell))
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
          (do (mutate-cell! prev-cell (j/obj .-f f
                                             .-value nil))
              (tx! #(eval-and-set! prev-cell))
              prev-cell)

          def? (make-cell f owner def?)

          memo-key (u/memoized-on owner memo-key
                     (make-cell f owner def?))

          :else (throw (js/Error. (str "Invalid arguments to `cell*` " options))))))


;; WIP
(defn cell
  [key value]
  (assert key "Cells created by functions require a :key")
  (cell* (constantly value) {:memo-key (str "#" (hash key))}))