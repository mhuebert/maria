(ns prosemirror.core
  (:require [clojure.string :as str]
            ["prosemirror-view" :as view]
            ["prosemirror-state" :as state]
            ["prosemirror-keymap" :as keymap]
            ["prosemirror-transform" :as transform]
            ["prosemirror-schema-list" :as list-schema]
            ["prosemirror-inputrules" :as input-rules]
            ["prosemirror-model" :as prosemirror-model]
            ["prosemirror-commands" :as prosemirror-commands]
            ["prosemirror-history" :as prosemirror-history]
            [applied-science.js-interop :as j]))

;; javacript interop with the Prosemirror bundle
;; (type hints for externs inference)

(set! *warn-on-infer* true)

(def Schema state/Schema)
(def EditorView view/EditorView)
(def EditorState state/EditorState)
(def Selection state/Selection)
(def TextSelection state/TextSelection)
(def NodeSelection state/NodeSelection)


(defn ensure-schema [state-or-schema]
  (cond-> state-or-schema
          (= (j/get state-or-schema :constructor) state/EditorState) (j/get :schema)))

(defn get-mark [state-or-schema mark-name]
  (if (keyword? mark-name)
    (j/get-in (ensure-schema state-or-schema) [:marks (name mark-name)])
    mark-name))

(defn get-node [state-or-schema node-name]
  (aget (ensure-schema state-or-schema) "nodes" (name node-name)))

(defn scroll-into-view [tr]
  (.scrollIntoView tr))

(defn toggle-mark
  [mark-name]
  (fn [state dispatch]
    (let [the-command (prosemirror-commands/toggleMark (get-mark state mark-name))]
      (the-command state dispatch))))

(defn cursor-node [state]
  (let [sel (.-selection state)]
    (or (.-node sel) (.-parent (.-$from sel)))))

(defn cursor-depth [state]
  (.-depth (.-$from (.-selection state))))

(def auto-join #(prosemirror-commands/autoJoin % (fn [] true)))

(def wrap-in-list (comp auto-join (fn [list-tag]
                                    (fn [state dispatch]
                                      (let [the-command (list-schema/wrapInList (get-node state list-tag))]
                                        (the-command state dispatch))))))
(defn set-block-type
  ([block-tag]
   (set-block-type block-tag nil))
  ([block-tag attrs]
   (fn [state dispatch]
     (let [the-command (prosemirror-commands/setBlockType (get-node state block-tag) attrs)]
       (the-command state dispatch)))))


(defn input-rule-wrap-inline
  ;; from textblockTypeInputRule
  [pattern node-tag attrs]
  (input-rules/InputRule. pattern
                          (fn [state match start end]
                            (let [$start     (.. state -doc (resolve start))
                                  start-node (.node $start -1)
                                  attrs      (if (fn? attrs) (attrs match) attrs)
                                  the-node   (get-node state node-tag)]
                              (if-not (-> start-node
                                          (.canReplaceWith (.index $start -1) (.indexAfter $start -1) the-node attrs))
                                nil
                                (-> (.-tr state)
                                    (.delete start end)
                                    (.setBlockType start start the-node attrs)))))))

(defn input-rule-wrap-block
  ;; from wrappingInputRule
  ([pattern node-tag attrs] (input-rule-wrap-block pattern node-tag attrs nil))
  ([pattern node-tag attrs join-predicate]
   (input-rules/InputRule. pattern
                           (fn [state match start end]
                             (let [node-type (get-node state node-tag)
                                   attrs     (if (fn? attrs) (attrs match) attrs)
                                   tr        (.delete (.-tr state) start end)
                                   $start    (.resolve (.-doc tr) start)
                                   range     (.blockRange $start)
                                   wrapping  (and range
                                                  (transform/findWrapping range node-type attrs))]
                               (when wrapping
                                 (.wrap tr range wrapping)
                                 (let [before (-> tr
                                                  (.-doc)
                                                  (.resolve (dec start))
                                                  (.-nodeBefore))]
                                   (when (and before
                                              (= (.-type before) node-type)
                                              (transform/canJoin (.-doc tr) (dec start))
                                              (or (not join-predicate) (join-predicate match before)))
                                     (.join tr (dec start)))
                                   tr)))))))


(def lift prosemirror-commands/lift)

(defn lift-list-item [state dispatch]
  (let [the-command (list-schema/liftListItem (get-node state :list_item))]
    (the-command state dispatch)))

(defn sink-list-item [state dispatch]
  (let [the-command (list-schema/sinkListItem (get-node state :list_item))]
    (the-command state dispatch)))


(defn range-nodes [node start end]
  (let [out #js []]
    (.nodesBetween node start end #(.push out %))
    (vec out)))

(defn selection-nodes [doc selection]
  (->> (.-ranges selection)
       (reduce (fn [nodes range]
                 (into nodes (range-nodes doc (.. range -$from -pos) (.. range -$to -pos)))) [])))

(defn mark-name [mark]
  (.. mark -type -name))

(defn pos-mark [state $pos mark]
  (let [^js/MarkType mark (get-mark state mark)
        stored-marks      (.-storedMarks state)
        cursor-marks      (some-> $pos (.marks))]
    (first (filter #(= mark (.-type %))
                   (cond-> #js []
                           stored-marks (.concat stored-marks)
                           cursor-marks (.concat cursor-marks)))))
  )

(defn has-mark? [pm-state mark]
  (let [^js/MarkType mark (get-mark pm-state mark)]
    (if-let [cursor (.. pm-state -selection -$cursor)]
      (.isInSet mark (or (.-storedMarks pm-state) (.marks cursor)))
      (every? true? (mapv (fn [range]
                            (.rangeHasMark (.-doc pm-state)
                                           (.. range -$from -pos)
                                           (.. range -$to -pos)
                                           mark))
                          (.. pm-state -selection -ranges))))))

(defn toggle-mark-tr
  ([state mark-name] (toggle-mark-tr state mark-name nil))
  ([state mark-name attrs]
   (let [sel       (.-selection state)
         empty     (.-empty sel)
         $cursor   (.-$cursor sel)
         mark-type (get-mark state mark-name)
         the-mark  (.create mark-type attrs)
         the-node  (cursor-node state)
         tr        (.-tr state)]
     (when (and $cursor
                (not (or (and empty (not $cursor))
                         (not (.-inlineContent the-node))
                         (not (.allowsMarks (.-type the-node) #js [the-mark])))))
       (if (.isInSet mark-type (or (.-storedMarks state) (.marks $cursor)))
         (.removeStoredMark tr mark-type)
         (.addStoredMark tr the-mark))))))


(defn add-link-tr
  [state from to label href]
  (let [tr   (.-tr state)
        link (get-mark state :link)
        from from]
    (-> tr
        (.insertText label from to)
        (.addMark from (+ from (count label)) (.create link #js {:href href}))
        (.removeStoredMark link))))

(defn add-image-tr
  [state from to label href]
  (let [tr    (.-tr state)
        image (get-node state :image)]
    (-> tr
        (.setSelection (.create TextSelection (.-doc state) from to))
        (.replaceSelectionWith (.createAndFill image #js {:src   href
                                                          :title label
                                                          :alt   label})))))

(defn state [pm-view]
  (.-state pm-view))

(defn transact! [pm-view tr]
  (.updateState pm-view (.apply (.-state pm-view) tr)))

(defn destroy! [pm-view]
  (.destroy pm-view))

(defn is-list? [node]
  (str/ends-with? (aget node "type" "name") "list"))

(defn first-ancestor [pos pred]
  (loop [node  (.node pos)
         depth (some-> pos .-depth)]
    (cond (not node) nil
          (pred node) node
          :else (recur (.node pos (dec depth))
                       (dec depth)))))

(defn descends-from? [$from kind attrs]
  (first-ancestor $from (fn [node]
                          (.hasMarkup node kind attrs))))

(defn has-markup? [state node-type-name attrs]
  (let [selection (.-selection state)
        kind      (get-node state node-type-name)]
    (if-let [node (.-node selection)]
      (.hasMarkup node kind attrs)
      (let [$from (.-$from selection)]
        (and (<= (.-to selection) (.end $from))
             (.hasMarkup (.-parent $from) kind attrs))))))

(defn wrap-in [state type-name]
  (prosemirror-commands/wrapIn (get-node state type-name)))

(defn node-type [node]
  (aget node "type"))

(defn in-list? [state list-type-name]
  (= list-type-name (some-> (first-ancestor (.. state -selection -$from) is-list?)
                            (aget "type" "name"))))

(def is-node-type?
  (fn [state node-tag]
    (= (.-type (cursor-node state)) (get-node state node-tag))))

(defn heading-level [state]
  (let [node (cursor-node state)]
    (when (= (.-type node) (get-node state :heading))
      (.-level (.-attrs node)))))

(def split-list-item
  (fn [state dispatch]
    ((list-schema/splitListItem (get-node state :list_item)) state dispatch)))

;; + or - the level
;; if para - only up
;; if heading - determine up or down


(defn start-$pos [state]
  (.-$head (.atStart Selection (.. state -doc))))

(defn end-$pos [state]
  (.-$head (.atEnd Selection (.. state -doc))))

(defn cursor-$pos [state]
  (.-$head (.-selection state)))




;function markApplies(doc, ranges, type) {
;  for (let i = 0; i < ranges.length; i++) {
;    let {$from, $to} = ranges[i]
;    // at depth zero? then can = doc.contentMatch.
;    //
;    let can = $from.depth == 0 ? doc.contentMatchAt(0).allowsMark(type) : false
;    doc.nodesBetween($from.pos, $to.pos, node => {
;      if (can) return false
;      can = node.inlineContent && node.contentMatchAt(0).allowsMark(type)
;    })
;    if (can) return true
;  }
;  return false
;}

#_(defn mark-applies? [doc ranges type]
    (let [node-matches #(and (.-inlineContent %)
                             (-> % (.contentMatchAt 0) (.allowsMark type)))]
      (every? identity (for [range ranges
                             :let [$from     (.-$from range)
                                   root-can? (and (= 0 (.-depth $from))
                                                  (node-matches doc))
                                   nodes     (range-nodes doc (.-pos $from) (.-pos (.-$to range)))]]
                         (or (and root-can? (empty? nodes))
                             (every? identity (for [node nodes]
                                                (and (not root-can?)
                                                     (node-matches node)))))))))
#_(defn toggle-mark-tr
    ([state mark-name]
     (toggle-mark-tr state mark-name nil))
    ([state mark-name attrs]
     (let [sel               (.-selection state)
           ranges            (.-ranges sel)
           doc               (.-doc state)
           empty             (.-empty sel)
           $cursor           (.-$cursor sel)
           mark-type         (get-mark state mark-name)
           tr                (.-tr state)
           invalid-selection (or (and empty (not $cursor))
                                 (not (mark-applies? doc ranges mark-type)))]
       (when-not invalid-selection
         (let [has-mark (has-mark? state mark-name)]
           (if $cursor
             (if has-mark (.removeStoredMark tr mark-type)
                          (.addStoredMark tr (.create mark-type attrs)))
             (reduce (fn [tr range]
                       (if has-mark
                         (.removeMark tr
                                      (.. range -$from -pos)
                                      (.. range -$to -pos)
                                      mark-type)
                         (.addMark tr
                                   (.. range -$from -pos)
                                   (.. range -$to -pos)
                                   (.create mark-type attrs)))) tr ranges)))))))

(defn toggle-ranges-mark
  ([state ranges mark] (toggle-ranges-mark state ranges mark nil))
  ([state ranges mark attrs]
   (let [mark     (get-mark state mark)
         has-mark (has-mark? state mark)]
     (reduce (fn [tr range]
               (if has-mark
                 (.removeMark tr
                              (.. range -$from -pos)
                              (.. range -$to -pos)
                              mark)
                 (.addMark tr
                           (.. range -$from -pos)
                           (.. range -$to -pos)
                           (.create mark attrs)))) (.-tr state) ranges))))

(defn mark-extend [state $pos mark]
  (let [mark        (get-mark state mark)
        parent      (.-parent $pos)
        start-index (loop [start-index (.index $pos)]
                      (if (or (<= start-index 0)
                              (not (.isInSet mark (.. $pos -parent (child (dec start-index)) -marks))))
                        start-index
                        (recur (dec start-index))))
        end-index   (loop [end-index (.indexAfter $pos)]
                      (if (or (>= end-index (.. $pos -parent -childCount))
                              (not (.isInSet mark (.. $pos -parent (child end-index) -marks))))
                        end-index
                        (recur (inc end-index))))
        [start-pos end-pos] (loop [start-pos (.start $pos)
                                   end-pos   start-pos
                                   i         0]
                              (if (>= i end-index)
                                [start-pos end-pos]
                                (let [size (.. parent (child i) -nodeSize)]
                                  (if (< i start-index)
                                    (recur (+ start-pos size) (+ end-pos size) (inc i))
                                    (recur start-pos (+ end-pos size) (inc i))))))]
    {:from start-pos
     :to   end-pos}))

(defn cursor-coords [pm-view]
  (when-let [coords (some->> (.. pm-view -state -selection -$cursor)
                             (.-pos)
                             (.coordsAtPos pm-view))]
    #js {:left (.-left coords)
         :top  (-> (.-top coords)
                   (+ (/ (- (.-bottom coords)
                            (.-top coords))
                         2)))}))

(defn coords-selection [pm-view position]
  (some->> (.posAtCoords pm-view position)
           (.-pos)
           (.resolve (.. pm-view -state -doc))
           (.near Selection)))