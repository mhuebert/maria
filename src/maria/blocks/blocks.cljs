(ns maria.blocks.blocks
  (:refer-clojure :exclude [empty?])
  (:require [magic-tree.core :as tree]
            [re-view.core :as v]
            [clojure.string :as string]
            [maria-commands.exec :as exec]
            [re-db.d :as d]
            [re-view-prosemirror.markdown :as markdown]
            [cells.cell :as cell]
            [maria.util :as util]
            [cells.eval-context :as eval-context]
            [maria.eval :as e]
            [maria.editors.editors :as Editor]))

(defprotocol IBlock

  (empty? [this])
  (emit [this])
  (kind [this])
  (state [this])
  (tag [this])

  (append? [this other-block] "Return true if other block can be joined to next block")
  (append [this other-block] "Append other-block to this block."))

(defn update-view
  [block]
  (some-> (Editor/view block)
          (v/force-update)))

(defprotocol IEval
  (eval! [this] [this kind value])
  (eval-log [this])
  (eval-log! [this value]))

(extend-type nil IBlock
  (empty? [this] true))

(defrecord CodeBlock [id node]
  IBlock
  (state [this] node))

(defrecord ProseBlock [id doc]
  IBlock
  (state [this] doc))

(defrecord WhitespaceBlock [id content]
  IBlock
  (kind [this] :whitespace)
  (emit [this] content)

  IFn
  (-invoke [this props] [:div]))

(defn whitespace? [block]
  (= :whitespace (kind block)))

(extend-protocol IEquiv
  CodeBlock
  (-equiv [this other] (and (= (:id this) (:id other))
                            (= (state this) (state other))))
  ProseBlock
  (-equiv [this other] (and (= (:id this) (:id other))
                            (= (state this) (state other)))))

(defn from-node
  "Returns a block, given a magic-tree AST node."
  [{:keys [tag] :as node}]
  ;; GET ID FROM NODE
  ;; CAN WE GET IT FROM DEF, DEFN, etc. in a structured way?
  ;; IE IF FIRST SYMBOL STARTS WITH DEF, READ NEXT SYMBOL
  (case tag
    :comment (->ProseBlock (d/unique-id) (.parse markdown/parser (:value node)))
    (:newline :space :comma nil) (->WhitespaceBlock (d/unique-id) (:value node))
    (->CodeBlock (d/unique-id) node)))

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
              (if (whitespace? block)
                out
                (let [source (-> (emit block)
                                 (string/trim-newline))]
                  (if-not (clojure.core/empty? source)
                    (str out source "\n\n")
                    out)))) "\n" blocks)))

(defn from-source
  "Returns a vector of blocks from a ClojureScript source string."
  [source]
  (->> (tree/ast (:ns @e/c-env) source)
       (:value)
       (mapv from-node)
       #_(reduce (fn [out node]
                   (cond-> out
                           (not (tree/whitespace? node))
                           (conj (from-node node)))) [])))


(defn ensure-blocks [blocks]
  (if-not (seq blocks)
    [(create :prose)]
    blocks))

(defn id-index
  ([blocks id] (id-index blocks id 0))
  ([blocks id start]
   (let [end-i (count blocks)]
     (loop [i start]
       (cond
         (= i end-i) nil
         (= (:id (nth blocks i)) id) i
         :else (recur (inc i)))))))

(defn lefts
  ([blocks block]
   (lefts blocks block (dec (id-index blocks (:id block)))))
  ([blocks block n]
   (if (neg? n)
     nil
     (cons (nth blocks n) (lazy-seq (lefts blocks block (dec n)))))))

(defn rights
  ([blocks block]
   (rights blocks block (inc (id-index blocks (:id block)))))
  ([blocks block n]
   (if (>= n (count blocks))
     nil
     (cons (nth blocks n) (lazy-seq (rights blocks block (inc n)))))))

(defn left [blocks block]
  (first (filter (complement whitespace?) (lefts blocks block))))

(defn right [blocks block]
  (first (filter (complement whitespace?) (rights blocks block))))

(defn splice-blocks
  ([blocks block values]
   (splice-blocks blocks block nil values))
  ([blocks from-block to-block values]
   (let [[before-blocks the-rest] (split-with #(not= (:id %) (:id from-block)) blocks)
         [replaced-blocks* after-blocks] (if (nil? to-block)
                                           [(take 1 the-rest)
                                            (rest the-rest)]
                                           [(util/take-until #(= (:id %) (:id to-block)) the-rest)
                                            (rest (drop-while #(not= (:id %) (:id to-block)) the-rest))])
         result (with-meta
                  (-> (vec before-blocks)
                      (into values)
                      (into after-blocks))
                  {:before (last before-blocks)
                   :after  (first after-blocks)})]
     (let [removed-blocks (->> replaced-blocks*
                               (filterv (comp (complement (set (mapv :id values))) :id)))]
       (doseq [block removed-blocks]
         (when (satisfies? eval-context/IDispose block)
           (eval-context/dispose! block))))
     result)))

#_(defn join-blocks [blocks]
    ;; PROBLEM
    ;; unable to handle cursor properly when joining blocks.
    ;; unable to 'thread' cursor movements through splice-blocks and
    ;; join-blocks.

    (let [the-focused-block (focused-block)]
      (loop [blocks blocks
             dropped-indexes []
             block-to-focus nil
             i 0]
        (cond (> i (- (count blocks) 2))
              (do (when (and the-focused-block block-to-focus)
                    (let [[block selection] block-to-focus]
                      (.log js/console " :set-selection" selection)
                      (prn :focused the-focused-block)
                      (.log js/console (get-history-selections the-focused-block))
                      (put-selections! block selection)
                      (focus! :join-blocks block)))
                  (with-meta blocks {:dropped dropped-indexes}))
              (append? (nth blocks i) (nth blocks (inc i)))
              (let [block (nth blocks i)
                    next-block (nth blocks (inc i))
                    joined-block (append block next-block)
                    next-focused-block (cond (= (:id block) (:id the-focused-block))
                                             [block (get-history-selections block)]
                                             (= (:id next-block) (:id the-focused-block))
                                             [block (let [sel (get-history-selections next-block)]
                                                      (.create pm/TextSelection (state joined-block)
                                                               (+ (.. (state block) -content -size) (.-anchor sel))))]
                                             :else nil)]
                (recur (util/vector-splice blocks i 2 [joined-block])
                       (conj dropped-indexes (+ (inc i) (count dropped-indexes)))
                       (or block-to-focus
                           next-focused-block)
                       i))
              :else (recur blocks dropped-indexes block-to-focus (inc i))))))