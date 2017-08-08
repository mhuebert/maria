(ns maria.cells.core
  (:refer-clojure :exclude [empty?])
  (:require [magic-tree.core :as tree]
            [clojure.string :as string]
            [fast-zip.core :as z]
            [re-db.d :as d]
            [re-view.core :as v]))

(def cell-index (volatile! {}))

(defn get-view
  "Retrieve the mounted view for the cell, if it exists"
  [cell]
  (@cell-index (:id cell)))

(defn editor
  "Retrieve the editor for the cell, if it exists"
  [cell]
  (.getEditor (get-view cell)))

(defn focus!
  ([cell]
   (focus! cell nil))
  ([cell coords]
   (when-not (get-view cell)
     (v/flush!))
   (.focus (get-view cell) coords)))

(defn mount [cell view]
  (vswap! cell-index assoc (:id cell) view))

(defn unmount [cell]
  (vswap! cell-index dissoc (:id cell)))

(defprotocol ICell
  (empty? [this])
  (emit [this])
  (at-end? [this])
  (at-start? [this])
  (selection-expand [this])
  (selection-contract [this])
  (render [this props]))

(defprotocol ICode
  (eval [this]))

(defprotocol IText
  (prepend-paragraph [this])
  (trim-paragraph-left [this]))

(extend-type nil ICell
  (empty? [this] true))

(defrecord CodeCell [id value])
(defrecord ProseCell [id value])


(defn emit-many
  "Return the concatenated source of a list of editor groups."
  [cells]
  (transduce (comp (filter (complement empty?))
                   (map emit))
             (fn [out item]
               (str out item "\n\n"))
             "\n"
             cells))

(defn some-right [loc]
  (when loc (z/right loc)))

(def loc-tag (comp :tag z/node))

(defn comment-block-loc? [loc]
  (when loc
    (#{:newline :comment :space :comma} (loc-tag loc))))

(defn comment-locs->string [locs]
  (->> (mapv (comp (fn [{:keys [tag value]}]
                     (case tag (:newline :space :comma) value
                               :comment (.replace value #"^;+" ""))) z/node) locs)
       (string/join)))

(defn from-zip [loc]
  (loop [loc (z/down loc)
         out []
         n 0]
    (if (> n 1000)
      (do (prn :LOOP loc (z/node loc))
          out)
      (if-let [tag (some-> loc (loc-tag))]
        (case tag
          :comment
          (let [comment-locs (take-while comment-block-loc? (iterate some-right loc))
                cell (->ProseCell (d/unique-id) (comment-locs->string comment-locs))]
            (recur (some-right (last comment-locs))
                   (cond-> out
                           (not (empty? cell)) (conj cell))
                   (inc n)))
          (:newline :space :comma)
          (recur (some-right loc)
                 out
                 (inc n))
          (let [cell (->CodeCell (d/unique-id) [(z/node loc)])]
            (recur (some-right loc)
                   (cond-> out
                           (not (empty? cell)) (conj cell))
                   (inc n))))
        out))))

(defn from-source [s]
  (-> (tree/string-zip s)
      (from-zip)))

(defn ensure-cells [cells]
  (if (clojure.core/empty? cells)
    [(->ProseCell (d/unique-id) "")]
    cells))

(defn splice-by-id
  ([cells id values]
   (splice-by-id cells id 0 values))
  ([cells id n values]
   (if (and (clojure.core/empty? values)
            (= 1 (count cells)))
     (let [cells (ensure-cells nil)]
       (with-meta cells {:before (first cells)}))
     (let [pred #(not= id (:id %))
           [before [match & after]] (split-with pred cells)
           before (cond->> before
                           (neg? n) (drop-last (.abs js/Math n)))
           after (cond->> after
                          (pos? n) (drop n))]
       (assert match)
       (with-meta (-> (vec before)
                      (into values)
                      (into after))
                  {:before (last before)
                   :after  (first after)})))))

(defn before [cells cell]
  (last (take-while (comp (partial not= (:id cell)) :id) cells)))

(defn after [cells cell]
  (second (drop-while (comp (partial not= (:id cell)) :id) cells)))

(defn delete-cell [{:keys [view/state] :as cell-list-view} cell]
  (let [next-cells (splice-by-id (:cells @state) (:id cell) [])]
    (swap! state assoc :cells next-cells)
    (let [{:keys [before after]} (meta next-cells)]
      (if before (focus! before :end)
                 (focus! after :start)))))