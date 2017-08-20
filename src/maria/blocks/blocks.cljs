(ns maria.blocks.blocks
  (:refer-clojure :exclude [empty?])
  (:require [magic-tree.core :as tree]
            [clojure.core.match :refer-macros [match]]
            [re-db.d :as d]
            [re-view.core :as v]
            [clojure.string :as string]
            [cljs.pprint :refer [pprint]]
            [maria.eval :as e]
            [maria.util :as util]
            [maria-commands.exec :as exec]
            [re-view-prosemirror.core :as pm]))

(def view-index (volatile! {}))

(defn view [this]
  (@view-index (:id this)))

(defn editor [this]
  (.getEditor (view this)))

(defn mount [this view]
  (vswap! view-index assoc (:id this) view))

(defn unmount [this]
  (vswap! view-index dissoc (:id this)))

(defprotocol IBlock

  (empty? [this])
  (emit [this])
  (kind [this])

  (render [this props]))

(defprotocol IJoin
  (join-forward? [this other-block])
  (join-forward [this other-block]))

(defn focus!
  ([block]
   (focus! block nil))
  ([block coords]
   (when-not (view block)
     (v/flush!))
   (some-> (view block)
           (.focus coords))))

(defprotocol ICursor
  (cursor-edge [this])
  (cursor-coords [this])
  (at-end? [this])
  (at-start? [this])
  (selection-expand [this])
  (selection-contract [this]))

(defprotocol IEval
  (eval [this] [this kind value])
  (eval-log [this])
  (-eval-log! [this value]))

(defprotocol IParagraph
  (prepend-paragraph [this])
  (trim-paragraph-left [this]))

(extend-type nil IBlock
  (empty? [this] true))

(defrecord CodeBlock [id node])
(defrecord ProseBlock [id node])

(defn from-node
  "Returns a block, given a magic-tree AST node."
  [{:keys [tag] :as node}]
  ;; GET ID FROM NODE
  ;; CAN WE GET IT FROM DEF, DEFN, etc. in a structured way?
  ;; IE IF FIRST SYMBOL STARTS WITH DEF, READ NEXT SYMBOL
  (when-let [create* (case tag
                       :comment ->ProseBlock
                       (:newline :space :comma nil) nil
                       ->CodeBlock)]
    (create* (d/unique-id) node)))

(defn create
  "Returns a block, given a kind (:code or :prose) and optional value."
  ([kind]
   (create kind (case kind
                  :prose ""
                  :code [])))
  ([kind value]
   (from-node {:tag   (case kind :prose :comment
                                 :code :base)
               :value value})))

(def emit-list
  "Returns the concatenated source for a list of blocks."
  (fn [blocks]
    (reduce (fn [out block]
              (let [source (-> (emit block)
                               (string/trim-newline))]
                (if-not (clojure.core/empty? source)
                  (str out source "\n\n")
                  out))) "\n" blocks)))

(defn from-source
  "Returns a vector of blocks from a ClojureScript source string."
  [source]
  (->> (tree/ast source)
       :value
       (reduce (fn [blocks node]
                 (if-let [block (from-node node)]
                   (if (some-> (peek blocks)
                               (join-forward? block))
                     (update blocks (dec (count blocks)) join-forward block)
                     (conj blocks block))
                   blocks)) [])))


(defn join-blocks [blocks]
  (let [focused-block (:block (:block-view @exec/context))
        focused-view (some-> focused-block (editor))
        focused-coords (when (and focused-view (= :prose (kind focused-block)))
                         (some-> focused-view (pm/cursor-coords)))]
    (loop [blocks blocks
           dropped []
           focused-block focused-block
           i 0]
      (cond (> i (- (count blocks) 2))
            (do
              (when focused-coords
                (js/setTimeout
                  #(focus! focused-block focused-coords) 0))
              (with-meta blocks {:dropped dropped}))
            (join-forward? (nth blocks i) (nth blocks (inc i)))
            (let [block (nth blocks i)
                  other-block (nth blocks (inc i))
                  next-focused-block (when (or (= block focused-block)
                                               (= other-block focused-block))
                                       block)]

              (recur (util/vector-splice blocks i 2 [(join-forward (nth blocks i) (nth blocks (inc i)))])
                     (conj dropped (+ (inc i) (count dropped)))
                     (or next-focused-block focused-block)
                     i))
            :else (recur blocks dropped focused-block (inc i))))))

(defn ensure-blocks [blocks]
  (if-not (seq blocks)
    [(create :prose)]
    blocks))

(defn id-index [blocks id]
  (let [end-i (count blocks)]
    (loop [i 0] (cond
                  (= i end-i) nil
                  (= (:id (nth blocks i)) id) i
                  :else (recur (inc i))))))

(defn dispose-block [block]
  (when (satisfies? e/IDispose block)
    (e/dispose! block)))

(defn splice-block
  ([blocks block values]
   (splice-block blocks block 0 values))
  ([blocks block n values]
   (if (and (clojure.core/empty? values)
            (= 1 (count blocks)))
     (let [blocks (ensure-blocks nil)]
       (dispose-block block)
       (with-meta blocks {:before (first blocks)}))
     (let [index (cond-> (id-index blocks (:id block))
                         (neg? n) (+ n))
           n (inc (.abs js/Math n))
           result (util/vector-splice blocks index n values)
           start (dec index)
           end (-> index
                   (+ (count values)))]
       (assert index)
       (let [incoming-block-ids (into #{} (mapv :id values))
             replaced-blocks (subvec blocks index (+ index n))
             removed-blocks (filterv (comp (complement incoming-block-ids) :id) replaced-blocks)]
         (doseq [block removed-blocks]
           (dispose-block block)))
       (with-meta result
                  {:before (when-not (neg? start) (nth result start))
                   :after  (when-not (> end (dec (count result)))
                             (nth result end))})))))

(defn before [blocks block]
  (last (take-while (comp (partial not= (:id block)) :id) blocks)))

(defn after [blocks block]
  (second (drop-while (comp (partial not= (:id block)) :id) blocks)))

#_(defn delete-block [{:keys [view/state] :as block-list-view} block]
    (let [next-blocks (splice-block (:blocks @state) (:id block) [])]
      (swap! state assoc :blocks next-blocks)
      (let [{:keys [before after]} (meta next-blocks)]
        (if before (focus! before :end)
                   (focus! after :start)))))

#_(defn splice-by-id-with-join
    ([blocks id values]
     (splice-by-id-with-join blocks id 0 values))
    ([blocks id n values]
     (if (and (clojure.core/empty? values)
              (= 1 (count blocks)))
       (let [blocks (ensure-blocks nil)]
         (with-meta blocks {:before (first blocks)}))
       (let [index (cond-> (id-index blocks id)
                           (neg? n) (+ n))
             _ (assert index)
             n (inc (.abs js/Math n))
             result (util/vector-splice blocks index n values)
             joined-result (join-blocks result)
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