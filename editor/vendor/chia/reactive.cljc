(ns chia.reactive
  "Central point where reactivity is coordinated"
  (:require [chia.util.macros :as m]
            [applied-science.js-interop :as j])
  #?(:cljs (:require-macros [chia.reactive :as r :refer [log-observation*]])))

(declare transition! dependencies dependents)

(def ^:dynamic *reader*
  "The reader being evaluated"
  nil)

(def ^:dynamic *non-reactive*
  "Flag to temporarily suspend reactivity"
  false)

(def ^:dynamic *reader-dependency-log*
  "Keeps track of what data sources a reader accesses during compute"
  nil)

(def ^:dynamic *schedule* (fn [f] (f)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Reactive readers
;;
;; A reactive reader does two things:
;;
;; 1. Wraps its evaluation in the `with-dependency-tracking!` macro
;;    (to handle dependency bookkeeping)
;;
;; 2. Implements `invalidate!`, to recompute when a dependency changes
;;

(defn handle-next-deps!
  [reader next-deps]
  {:pre [reader]}
  (let [prev-deps (get @dependencies reader)]
    (doseq [source (-> (set (keys next-deps))
                       (into (keys prev-deps)))]
      (transition! source reader
                   (get prev-deps source)
                   (get next-deps source)))))

(m/defmacro with-dependency-tracking!
  "Evaluates `body`, creating dependencies for `reader` with arbitrary data sources."
  [{:as   options
    :keys [schedule
           reader]} & body]
  {:pre [(map? options)]}
  `(let [reader# ~reader
         result# (binding [*reader* reader#
                           *reader-dependency-log* (volatile! {})]
                   (let [value# (do ~@body)]
                     (j/obj .-value value#
                            .-deps @*reader-dependency-log*)))]
     ((or ~schedule *schedule*) #(handle-next-deps! reader# (.-deps result#)))
     (.-value result#)))

(defprotocol IRecompute
  (-recompute! [reader]
    "Recompute a reader when a dependency has been invalidated."))

(defn recompute!
  "Invalidates `reader` (triggers re-evaluation)"
  ([reader] (recompute! reader nil))
  ([reader info]
   (when-not *non-reactive*
     (if (satisfies? IRecompute reader)
       (-recompute! reader)
       ;; in the simplest case, a reader is simply a function.
       (reader info)))))

(defn invalidate-readers!
  "Invalidates all readers of `source`"
  [source]
  (doseq [reader (keys (get @dependents source))]

    (recompute! reader))
  source)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Reactive data sources
;;
;; There are two kinds of sources:
;; 1. simple sources, which only know if they are observed or not-observed,
;; 2. pattern sources, which track per-reader 'patterns' to support dependency
;;    on a subset of a source.

;; A simple source:
;; - calls `observe-simple!` when its value is dereferenced
;; - implements `on-transition` to know when its `observed?` state changes
;; - calls `invalidate-readers!` when its value changes

(defprotocol ITransitionSimple
  (on-transition [source observed?]
    "Called when `source` is added or removed from the reactive graph."))

;; A pattern source:
;; - calls `observe-pattern!` when its value is accessed
;; - implements `on-transition-pattern` to know when each reader's patterns change,
;;   maintains its own index of readers and patterns
;; - when its value changes, calls `invalidate-reader!` for readers
;;   whose patterns are associated with data that has changed

(defprotocol ITransitionPattern
  "Protocol which enables a reactive data source to support pattern-based dependencies."
  (on-transition-pattern [source reader prev-patterns next-patterns]
    "Called when a reader has evaluated, and `source` is in prev- or next-patterns.

    `next-patterns` is the result of successive applications of `observe-pattern!` during a read.
    `prev-patterns` is for comparison with the last read."))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Dependency graph, with edge data
;;

;; {<source> {<reader> <pattern>}}
(defonce ^:private dependents (volatile! {}))

;; {<reader> {<source> <pattern>}}
(defonce ^:private dependencies (volatile! {}))

(defn- add-dependent [dependents source reader edge-data]
  (assoc-in dependents [source reader] edge-data))

(defn- remove-dependent [dependents source reader]
  (if (> (count (get dependents source)) 1)
    (update dependents source dissoc reader)
    (dissoc dependents source)))

(defn- add-dependency [dependencies reader source edge-data]
  (assoc-in dependencies [reader source] edge-data))

(defn- remove-dependency [dependencies reader source]
  (if (> (count (get dependencies reader)) 1)
    (update dependencies reader dissoc source)
    (dissoc dependencies reader)))

(defn- add-dependent! [source reader edge-data]
  (let [first-edge? (not (contains? @dependents source))]
    (vswap! dependents add-dependent source reader edge-data)
    (vswap! dependencies add-dependency reader source edge-data)
    ;; returns true if this is the first edge from `source`
    first-edge?))

(defn- remove-dependent! [source reader]
  (vswap! dependents remove-dependent source reader)
  (vswap! dependencies remove-dependency reader source)
  ;; returns true if this was the last edge from `source`
  (not (contains? @dependents source)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Read functions
;;

(defn transition-simple! [source reader prev next]
  (cond (nil? prev) (let [first-dep? (add-dependent! source reader ::simple)]
                      (when (true? first-dep?) (on-transition source true)))
        (nil? next) (let [last-dep? (remove-dependent! source reader)]
                      (when (true? last-dep?) (on-transition source false)))))

(defn transition-patterns! [source reader prev next]
  (if (empty? next)
    (remove-dependent! source reader)
    (add-dependent! source reader next))
  (on-transition-pattern source reader prev next))

(defn transition!
  "Updates watch for a source<>reader combo. Handles effectful updating of `source`."
  [source reader prev-patterns next-patterns]
  (when (not= prev-patterns next-patterns)
    (if (or (keyword? prev-patterns) (keyword? next-patterns))
      (transition-simple! source reader prev-patterns next-patterns)
      (transition-patterns! source reader prev-patterns next-patterns))))

(defn dispose-reader!
  "Removes reader from reactive graph."
  [reader]
  (doseq [[source patterns] (get @dependencies reader)]
    (transition! source reader patterns nil))
  reader)

(m/defmacro silently
  [& body]
  `(binding [*non-reactive* true]
     ~@body))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; For implementing data sources
;;

(m/defmacro ^:private log-observation* [source expr]
  `(do (when (and (some? *reader*) (not *non-reactive*))
         (vswap! *reader-dependency-log* assoc ~source ~expr))
       ~source))

(m/defmacro observe-simple!
  "Logs simple observation of `source`, which should implement `ITransitionSimple`"
  [source]
  `(log-observation* ~source ::simple))

(m/defmacro observe-pattern!
  "Logs observation of `source`, which should implement `ITransitionPattern`.
   `f` will be called with result of previous call to `observe-pattern!` for `source`"
  [source f & args]
  `(log-observation* ~source (~f (get @*reader-dependency-log* ~source) ~@args)))