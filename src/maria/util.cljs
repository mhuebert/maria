(ns maria.util
  (:require [goog.events :as events]
            [goog.object :as gobj]
            [re-view.core :as v]
            [clojure.string :as string]))

(defn loader [message]
  [:.w-100.sans-serif.tc
   [:.pa3.gray message]])

(defn stop! [e]
  (.stopPropagation e)
  (.preventDefault e))

(defn some-str [s]
  (when (and (string? s) (not (identical? s "")))
    s))

(defn spaced-string [the-name]
  (str (string/upper-case (first the-name)) (string/replace (subs the-name 1) "-" " ")))

(defn handle-captured-events [{:keys [view/props] :as this}]
  (let [dom-node (v/dom-node this)]
    (doseq [[key f] props]
      (when (= (namespace key) "capture-event")
        (events/listen dom-node (name key) #(f % this) true)))))

(defn vector-splice
  "Splice items into vector at index, replacing n items"
  [the-vector from-i to-i items]
  (-> (subvec the-vector 0 from-i)
      (into items)
      (into (subvec the-vector (inc to-i) (count the-vector)))))

(defn whitespace-string? [s]
  (re-find #"^[\s\n]*$" s))

(defn is-object? [o]
  (== (js/Object o) o))

(deftype JSLookup [o]
  ILookup
  (-lookup [this k]
    (let [value (gobj/get o (name k))]
      (if (is-object? value)
        (new JSLookup value) value)))
  (-lookup [this k not-found]
    (let [value (gobj/get o (name k) not-found)]
      (if (is-object? value)
        (new JSLookup value) value)))
  IDeref
  (-deref [this] o))

(defn js-lookup [o]
  (JSLookup. o))

#_(defn js-lookup
    "Wrap a js object to support `get` by keyword"
    [o]
    (if (or (not (is-object? o))
            (satisfies? ILookup o))
      o
      (specify! o
        ILookup
        (-lookup
          ([this k]
            ;; recursively wrap in js-lookup, for nested lookups
           (js-lookup (gobj/get this (name k))))
          ([this k not-found]
           (js-lookup (gobj/get this (name k) not-found)))))))

(defn scroll-into-view [y-pos]
  (let [{:keys [scrollY innerHeight]} (js-lookup js/window)]
    (when (or (< y-pos scrollY)
              (> y-pos (+ scrollY innerHeight)))
      (.scrollTo js/window 0 (-> y-pos
                                 (- (/ innerHeight 2)))))))

(def *debug* true)

(defn log-ret [label x]
  (if *debug* (do
                (prn label x)
                x)
              x))

;; from https://groups.google.com/forum/#!topic/clojure-dev/NaAuBz6SpkY
(defn take-until
  "Returns a lazy sequence of successive items from coll until
   (pred item) returns true, including that item. pred must be
   free of side-effects."
  [pred coll]
  (lazy-seq
    (when-let [s (seq coll)]
      (if (pred (first s))
        (cons (first s) nil)
        (cons (first s) (take-until pred (rest s)))))))