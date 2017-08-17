(ns maria.cells.core
  (:refer-clojure :exclude [empty?])
  (:require [magic-tree.core :as tree]
            [clojure.core.match :refer-macros [match]]
            [fast-zip.core :as z]
            [re-db.d :as d]
            [re-view.core :as v]
            [clojure.string :as string]
            [cljs.pprint :refer [pprint]]
            [maria.util :as util]
            [maria-commands.exec :as exec]
            [re-view-prosemirror.core :as pm]))

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
   (some-> (get-view cell)
           (.focus coords))))

(defn mount [cell view]
  (vswap! cell-index assoc (:id cell) view))

(defn unmount [cell]
  (vswap! cell-index dissoc (:id cell)))

(defprotocol ICursor
  (cursor-edge [this])
  (cursor-coords [this])
  (at-end? [this])
  (at-start? [this])
  (selection-expand [this])
  (selection-contract [this]))

(defprotocol ICell
  (empty? [this])
  (emit [this])
  (kind [this])

  (append? [this other-cell])
  (append [this other-cell])

  (render [this props]))

(defprotocol IEval
  (eval [this]))

(defprotocol IParagraph
  (prepend-paragraph [this])
  (trim-paragraph-left [this]))

(extend-type nil ICell
  (empty? [this] true))

(defrecord CodeCell [id node])
(defrecord ProseCell [id node])

(defn from-node
  "Returns a cell, given a magic-tree AST node."
  [{:keys [tag] :as node}]
  (when-let [create* (case tag
                       :comment ->ProseCell
                       (:newline :space :comma nil) nil
                       ->CodeCell)]
    (create* (d/unique-id) node)))

(defn create
  "Returns a cell, given a kind (:code or :prose) and optional value."
  ([kind]
   (create kind (case kind
                  :prose ""
                  :code [])))
  ([kind value]
   (from-node {:tag   (case kind :prose :comment
                                 :code :base)
               :value value})))

(def emit-list
  "Returns the concatenated source for a list of cells."
  (fn [cells]
    (reduce (fn [out cell]
              (let [source (-> (emit cell)
                               (string/trim-newline))]
                (if-not (clojure.core/empty? source)
                  (str out source "\n\n")
                  out))) "\n" cells)))

(defn from-source
  "Returns a vector of cells from a ClojureScript source string."
  [source]
  (->> (tree/ast source)
       :value
       (reduce (fn [cells node]
                 (if-let [cell (from-node node)]
                   (if (some-> (peek cells) (append? cell))
                     (update cells (dec (count cells)) append cell)
                     (conj cells cell))
                   cells)) [])))


(defn join-cells [cells]
  (let [focused-cell (:cell (:cell-view @exec/context))
        focused-view (some-> focused-cell (editor))
        focused-coords (when (and focused-view (= :prose (kind focused-cell)))
                         (some-> focused-view (pm/cursor-coords)))]
    (loop [cells cells
           dropped []
           focused-cell focused-cell
           i 0]
      (cond (> i (- (count cells) 2))
            (do
              (when focused-coords
                (js/setTimeout
                  #(focus! focused-cell focused-coords) 0))
              (with-meta cells {:dropped dropped}))
            (append? (nth cells i) (nth cells (inc i)))
            (let [cell (nth cells i)
                  other-cell (nth cells (inc i))
                  next-focused-cell (when (or (= cell focused-cell)
                                              (= other-cell focused-cell))
                                      cell)]

              (recur (util/vector-splice cells i 2 [(append (nth cells i) (nth cells (inc i)))])
                     (conj dropped (+ (inc i) (count dropped)))
                     (or next-focused-cell focused-cell)
                     i))
            :else (recur cells dropped focused-cell (inc i))))))

(defn ensure-cells [cells]
  (if-not (seq cells)
    [(create :prose)]
    cells))

(defn id-index [cells id]
  (let [end-i (count cells)]
    (loop [i 0] (cond
                  (= i end-i) nil
                  (= (:id (nth cells i)) id) i
                  :else (recur (inc i))))))

(defn splice-by-id
  ([cells id values]
   (splice-by-id cells id 0 values))
  ([cells id n values]
   (if (and (clojure.core/empty? values)
            (= 1 (count cells)))
     (let [cells (ensure-cells nil)]
       (with-meta cells {:before (first cells)}))
     (let [index (cond-> (id-index cells id)
                         (neg? n) (+ n))
           _ (assert index)
           n (inc (.abs js/Math n))
           result (util/vector-splice cells index n values)
           start (dec index)
           end (-> index
                   (+ (count values)))]
       (with-meta result
                  {:before (when-not (neg? start) (nth result start))
                   :after  (when-not (> end (dec (count result)))
                             (nth result end))})))))

(defn before [cells cell]
  (last (take-while (comp (partial not= (:id cell)) :id) cells)))

(defn after [cells cell]
  (second (drop-while (comp (partial not= (:id cell)) :id) cells)))

#_(defn delete-cell [{:keys [view/state] :as cell-list-view} cell]
    (let [next-cells (splice-by-id (:cells @state) (:id cell) [])]
      (swap! state assoc :cells next-cells)
      (let [{:keys [before after]} (meta next-cells)]
        (if before (focus! before :end)
                   (focus! after :start)))))

#_(defn splice-by-id-with-join
    ([cells id values]
     (splice-by-id-with-join cells id 0 values))
    ([cells id n values]
     (if (and (clojure.core/empty? values)
              (= 1 (count cells)))
       (let [cells (ensure-cells nil)]
         (with-meta cells {:before (first cells)}))
       (let [index (cond-> (id-index cells id)
                           (neg? n) (+ n))
             _ (assert index)
             n (inc (.abs js/Math n))
             result (util/vector-splice cells index n values)
             joined-result (join-cells result)
             {:keys [dropped]} (meta joined-result)
             start (dec index)
             end (-> index
                     (+ (count values)))
             adjusted-start (- start (count (take-while #(<= % start) dropped)))
             adjusted-end (- end (count (take-while #(< % end) dropped)))]
         (with-meta joined-result
                    {:before (when-not (neg? adjusted-start) (nth joined-result adjusted-start))
                     :after  (when-not (> adjusted-end (dec (count joined-result)))
                               (nth joined-result adjusted-end))})))))