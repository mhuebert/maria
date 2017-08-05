(ns maria.util
  (:require [goog.events :as events]
            [re-view.core :as v ]))

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