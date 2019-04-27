(ns maria.blocks.blocks
  (:refer-clojure :exclude [empty?])
  (:require [clojure.string :as string]

            [lark.tree.core :as tree]
            [lark.commands.registry :refer-macros [defcommand]]
            [lark.editor :as Editor]

            [re-view.core :as v]
            [re-db.d :as d]

            [re-view.prosemirror.markdown :as markdown]

            [cells.cell :as cell]
            [maria.util :as util]

            [cells.eval-context :as eval-context]
            [fast-zip.core :as z]
            [lark.tree.node :as node]
            [lark.tree.emit :as emit]
            [lark.tree.ext :as ext]
            [lark.tree.reader :as rd]))

(defprotocol IBlock

  (empty? [this])
  (kind [this])
  (state [this])
  (tag [this])
  (render [this props]))

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

(defrecord ProseBlock [id node]
  IBlock
  (state [this] (get node :prose/state)))

(defn commentize-source [source]
  (emit/string (rd/ValueNode :comment-block source)))

(defn update-prose-block-state [block editor-state]
  (assoc block :node {:prose/state editor-state
                      :prose/source (commentize-source (.serialize markdown/serializer editor-state))}))

(defrecord WhitespaceBlock [id node]
  IBlock
  (kind [this] :whitespace)

  Object
  (toString [this] (:value node))

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

(defn from-ast
  "Returns a block, given a lark.tree AST node."
  [node]
  (let [tag (.-tag node)
        value (.-value node)]
    (case tag
      :comment-block (->ProseBlock (d/unique-id) (assoc node :prose/source (commentize-source value)
                                                             :prose/state (.parse markdown/parser value)))
      (:newline :space :comma nil) (->WhitespaceBlock (d/unique-id) node)
      (->CodeBlock (d/unique-id) node))))

(defn create
  "Returns a block, given a kind (:code or :prose) and optional value."
  ([kind]
   (create kind (case kind
                  :prose ""
                  :code [])))
  ([kind value]
   ;; TODO
   ;; create a real ast?
   (from-ast (case kind :prose (-> (rd/EmptyNode :comment-block)
                                   (assoc! :value value))
                        :code (-> (rd/EmptyNode :base)
                                  (assoc! :children value))))))

(def emit-list
  "Returns the concatenated source for a list of blocks."
  (fn [blocks]
    (reduce (fn [out block]
              (if (whitespace? block)
                out
                (let [source (str block)]
                  (if-not (clojure.core/empty? source)
                    (str out source "\n\n")
                    out))))
            ""
            blocks)))

(defn ensure-blocks [blocks]
  (if-not (first (remove whitespace? blocks))
    [(create :prose)]
    blocks))

(defn from-source
  "Returns a vector of blocks from a ClojureScript source string."
  [source]
  (->> (tree/ast source)
       (ext/group-comment-blocks)
       (:children)
       (reduce (fn [out node]
                 (cond-> out
                         (not (or (node/whitespace? node)
                                  (and (= :comment-block (:tag node))
                                       (util/whitespace-string? (:value node)))))
                         (conj (from-ast node)))) [])
       (ensure-blocks)))

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
                  :after (first after-blocks)})]
     (let [removed-blocks (->> replaced-blocks*
                               (filterv (comp (complement (set (mapv :id values))) :id)))]
       (doseq [block removed-blocks]
         (when (satisfies? eval-context/IDispose block)
           (eval-context/dispose! block))))
     result)))

(defcommand :doc/print-to-console
            "Prints the current document to console as a string."
            {:when :block-list}
            [{:keys [block-list]}]
            (print (-> (.getBlocks block-list)
                       (ensure-blocks)
                       (emit-list))))