(ns maria.util
  (:require [goog.events :as events]
            [goog.object :as gobj]
            [re-view.core :as v]))

(defn some-str [s]
  (when (and (string? s) (not (identical? s "")))
    s))

(defn handle-captured-events [{:keys [view/props] :as this}]
  (let [dom-node (v/dom-node this)]
    (doseq [[key f] props]
      (when (= (namespace key) "capture-event")
        (events/listen dom-node (name key) #(f % this) true)))))

(defn vector-splice
  "Splice items into vector at index, replacing n items"
  [the-vector index n items]
  (-> (subvec the-vector 0 index)
      (into items)
      (into (subvec the-vector (+ index n) (count the-vector)))))

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

(defn scroll-to-cursor [cm]
  (-> (.cursorCoords cm)
      (.-top)
      (scroll-into-view)))

(def *debug* false)
(defn log-ret [label x]
  (if *debug* (do
                (prn label x)
                x)
              x))