(ns maria.cells.core
  (:refer-clojure :exclude [empty?])
  (:require [magic-tree.core :as tree]
            [clojure.string :as string]
            [fast-zip.core :as z]
            [re-db.d :as d]
            [re-view.core :as v]
            [maria.util :as util]
            [maria.eval :as e]
            [re-view-prosemirror.core :as pm]
            [magic-tree-codemirror.edit :as edit]
            [re-view-prosemirror.commands :as commands]))

(def cell-index (volatile! {}))

(defn get-view [cell]
  (@cell-index (:id cell)))

(defn editor [cell]
  (.getEditor (get-view cell)))

(defn focus!
  ([cell]
   (focus! cell nil))
  ([cell coords]
   (when-not (get-view cell)
     (v/flush!))
   (.focus (get-view cell) coords)))

(defn mount [id view]
  (vswap! cell-index assoc id view))

(defn unmount [id]
  (vswap! cell-index dissoc id))

(defprotocol ICell
  (empty? [this])
  (emit [this])
  (at-end? [this])
  (at-start? [this])
  (selection-expand [this])
  (selection-contract [this])
  #_(render [this]))

(defprotocol ICode
  (eval [this]))

(defprotocol IText
  (prepend-paragraph [this])
  (trim-paragraph-left [this]))

(extend-type nil ICell
  (empty? [this] true))

;; TODO
;; eval cells to re-db, so that we can track prev. evaluation results
;; and get rid of the existing weird eval tracking code.

(defrecord CodeCell [id value]
  ICell

  (empty? [this]
    (= 0 (count (filter (complement tree/whitespace?) value))))

  (at-end? [this]
    (let [cm (editor this)
          cursor (.getCursor cm)]
      (= [(.lastLine cm)
          (count (.getLine cm (.lastLine cm)))]
         [(.-line cursor) (.-ch cursor)])))

  (at-start? [this]
    (let [cursor (.getCursor (editor this))]
      (= [0 0] [(.-line cursor) (.-ch cursor)])))

  (emit [this]
    (when-not (empty? this)
      (str (->> (mapv (comp string/trim-newline tree/string) value)
                (string/join "\n\n")))))

  (selection-expand [this]
    (edit/expand-selection (editor this)))

  (selection-contract [this]
    (edit/shrink-selection (editor this)))

  ICode
  (eval [this]
    (e/logged-eval-str id (emit this))))

(defrecord ProseCell [id value]
  ICell

  (empty? [this]
    (util/whitespace-string? value))

  (at-start? [this]
    (let [state (.-state (editor this))]
      (= (.-pos (pm/cursor-$pos state))
         (.-pos (pm/start-$pos state)))))

  (at-end? [this]
    (let [state (.-state (editor this))]
      (= (.-pos (pm/cursor-$pos state))
         (.-pos (pm/end-$pos state)))))

  (emit [this]
    (when-not (empty? this)
      (.replace (->> (string/split value #"\n")
                     (mapv #(string/replace % #"^ ?" "\n;; "))
                     (clojure.string/join)) #"^\n" "")))

  (selection-expand [this]
    (commands/apply-command (editor this) commands/expand-selection))

  (selection-contract [this]
    (commands/apply-command (editor this) commands/contract-selection))

  IText
  (prepend-paragraph [this]
    (when-let [prose-view (editor this)]
      (let [state (.-state prose-view)
            dispatch (.-dispatch prose-view)]
        (dispatch (-> (.-tr state)
                      (.insert 0 (.createAndFill (pm/get-node state :paragraph)))
                      (.scrollIntoView))))))
  (trim-paragraph-left [this]
    (when-let [prose-view (:prose-editor-view @(:view/state (get-view this)))]
      (let [new-value (.replace value #"^[\n\s]*" "")]
        (.resetDoc prose-view new-value)
        (assoc this :value new-value)))))

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
  (if (clojure.core/empty? cells) [(->ProseCell (d/unique-id) "")] cells))

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

(defn before [cells id]
  (last (take-while (comp (partial not= id) :id) cells)))

(defn after [cells id]
  (second (drop-while (comp (partial not= id) :id) cells)))

(defn edge-left [cell]
  (when (at-start? cell)
    (let [{:keys [cells id]} (get-view cell)]
      (some-> (before cells id)
              (focus! :end)))))

(defn edge-right [cell]
  (when (at-end? cell)
    (let [{:keys [cells id]} (get-view cell)]
      (some-> (after cells id)
              (focus! :start)))))

(defn delete-cell [{:keys [view/state] :as cell-list-view} cell]
  (let [next-cells (splice-by-id (:cells @state) (:id cell) [])]
    (swap! state assoc :cells next-cells)
    (let [{:keys [before after]} (meta next-cells)]
      (if before (focus! before :end)
                 (focus! after :start)))))