(ns maria.util
  (:require [goog.events :as events]
            [goog.object :as gobj]
            [chia.view :as v]
            [clojure.string :as string]
            [applied-science.js-interop :as j])
  (:require-macros [maria.util :refer [for-map]]))

(defn loader [message]
  [:.w-100.sans-serif.tc
   [:.pa3.gray message]])

(defn stop! [e]
  (.stopPropagation e)
  (.preventDefault e))

(defn clipboard-text [e]
  (some-> (or (j/get e :clipboardData)
              (j/get js/window :clipboardData))
          (j/call :getData "text")))

(defn some-str [s]
  (when (and (string? s) (not (identical? s "")))
    s))

(defn capitalize [s]
  (str (string/upper-case (first s))
       (subs s 1)))

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

(defn scroll-into-view [y-pos]
  (let [{:keys [scrollY innerHeight]} (j/lookup js/window)]
    (when (or (< y-pos scrollY)
              (> y-pos (+ scrollY innerHeight)))
      (.scrollTo js/window 0 (-> y-pos
                                 (- (/ innerHeight 2)))))))

(def DEBUG true)

(defn log-ret [label x]
  (if DEBUG (do
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

(defn rect->abs-pos [rect [x y]]
  (let [x-offset (.-scrollX js/window)
        y-offset (.-scrollY js/window)]
    [(case x
       :left (+ x-offset (.-left rect))
       :right (+ x-offset (.-right rect))
       :center (+ x-offset
                  (.-left rect)
                  (/ (.-width rect) 2)))
     (case y
       :top (+ y-offset (.-top rect))
       :bottom (+ y-offset
                  (.-bottom rect))
       :center (+ y-offset
                  (.-top rect)
                  (/ (.-height rect) 2)))]))

(def space \u00A0)



(defn distinct-by
  ;; COPIED from medley.core: https://github.com/weavejester/medley/blob/master/src/medley/core.cljc
  ;; Copyright © 2017 James Reeves
  ;; Distributed under the Eclipse Public License version 1.0
  "Returns a lazy sequence of the elements of coll, removing any elements that
  return duplicate values when passed to a function f."
  ([f]
   (fn [rf]
     (let [seen (volatile! #{})]
       (fn
         ([] (rf))
         ([result] (rf result))
         ([result x]
          (let [fx (f x)]
            (if (contains? @seen fx)
              result
              (do (vswap! seen conj fx)
                  (rf result x)))))))))
  ([f coll]
   (let [step (fn step [xs seen]
                (lazy-seq
                  ((fn [[x :as xs] seen]
                     (when-let [s (seq xs)]
                       (let [fx (f x)]
                         (if (contains? seen fx)
                           (recur (rest s) seen)
                           (cons x (step (rest s) (conj seen fx)))))))
                    xs seen)))]
     (step coll #{}))))

(defn map-vals
  "Build map k -> (f v) for [k v] in map, preserving the initial type"
  [f m]
  (cond
    (sorted? m)
    (reduce-kv (fn [out-m k v] (assoc out-m k (f v))) (sorted-map) m)
    (map? m)
    (persistent! (reduce-kv (fn [out-m k v] (assoc! out-m k (f v))) (transient {}) m))
    :else
    (for-map [[k v] m] k (f v))))

(defn map-keys
  "Build map (f k) -> v for [k v] in map m"
  [f m]
  (if (map? m)
    (persistent! (reduce-kv (fn [out-m k v] (assoc! out-m (f k) v)) (transient {}) m))
    (for-map [[k v] m] (f k) v)))

(defn guard-> [x f]
  (when (f x) x))

(defn guard->> [f x]
  (when (f x) x))
