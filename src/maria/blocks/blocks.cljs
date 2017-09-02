(ns maria.blocks.blocks
  (:refer-clojure :exclude [empty?])
  (:require [magic-tree.core :as tree]
            [re-view.core :as v]
            [clojure.string :as string]
            [maria-commands.exec :as exec]
            [re-view-prosemirror.core :as pm]
            [re-view-prosemirror.markdown :as markdown]
            [cells.cell :as cell]
            [maria.util :as util]
            [re-db.d :as d]
            [cells.eval-context :as eval-context]
            [maria.eval :as e]))

(def view-index (volatile! {}))

(defn view [this]
  (@view-index (:id this)))

(defn editor [this]
  (some-> (view this)
          (.getEditor)))

(defn focused-block []
  (get-in @exec/context [:block-view :block]))

(defn mount [this view]
  (vswap! view-index assoc (:id this) view))

(defn unmount [this]
  (vswap! view-index dissoc (:id this)))

(defprotocol IBlock

  (empty? [this])
  (emit [this])
  (kind [this])
  (state [this])

  (render [this props]))

(defprotocol IAppend
  (append? [this other-block] "Return true if other block can be joined to next block")
  (append [this other-block] "Append other-block to this block."))

(defn focus!
  ([block]
   (focus! :unknown block nil))
  ([label block] (focus! label block nil))
  ([label block coords]
    #_(prn :focus! label coords (:id block) (let [out (emit block)]
                                              (subs out 0 (min 30 (count out)))))
   (when-not (view block)
     (v/flush!))
   (when-let [the-view (view block)]
     (exec/set-context! {(keyword "block" (kind block)) true
                         :block-view                    the-view})
     (.focus the-view coords))))

(defn update-view
  [block]
  (some-> (view block)
          (v/force-update)))

(defprotocol ICursor
  (get-history-selections [this])
  (get-cursor [this])
  (put-selections! [this selections])

  (cursor-coords [this])

  (start [this])
  (end [this])

  (selection-expand [this])
  (selection-contract [this]))

(defn at-start? [block]
  (some-> (get-cursor block)
          (= (start block))))

(defn at-end? [block]
  (= (get-cursor block)
     (end block)))

(defprotocol IEval
  (eval! [this] [this kind value])
  (eval-log [this])
  (eval-log! [this value]))

(defprotocol IParagraph
  (prepend-paragraph [this]))

(extend-type nil IBlock
  (empty? [this] true))

(defrecord CodeBlock [id node]
  IBlock
  (state [this] node))

(defrecord ProseBlock [id doc]
  IBlock
  (state [this] doc))

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
    (:newline :space :comma nil) nil
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
              (let [source (-> (emit block)
                               (string/trim-newline))]
                (if-not (clojure.core/empty? source)
                  (str out source "\n\n")
                  out))) "\n" blocks)))

(defn from-source
  "Returns a vector of blocks from a ClojureScript source string."
  [source]
  (->> (tree/ast (:ns @e/c-env) source)
       :value
       (reduce (fn [blocks node]
                 (if-let [block (from-node node)]
                   (if (some-> (peek blocks)
                               (append? block))
                     (update blocks (dec (count blocks)) append block)
                     (conj blocks block))
                   blocks)) [])))


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

(defn splice-block
  ([blocks block values]
   (splice-block blocks block 0 values))
  ([blocks block n values]
   (if (and (clojure.core/empty? values)
            (= 1 (count blocks)))
     (let [blocks (ensure-blocks nil)]
       (eval-context/dispose! block)
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
             removed-blocks (set (filterv (comp (complement incoming-block-ids) :id) replaced-blocks))]
         (doseq [block removed-blocks]
           (eval-context/dispose! block)))
       (with-meta result
                  {:before (when-not (neg? start) (nth result start))
                   :after  (when-not (> end (dec (count result)))
                             (nth result end))})))))

(defn before [blocks block]
  (last (take-while (comp (partial not= (:id block)) :id) blocks)))

(defn after [blocks block]
  (second (drop-while (comp (partial not= (:id block)) :id) blocks)))

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