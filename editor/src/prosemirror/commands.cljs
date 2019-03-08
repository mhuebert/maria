(ns prosemirror.commands
  (:require [prosemirror.core :as pm]
            ["prosemirror-state" :as state]
            ["prosemirror-history" :as history]
            ["prosemirror-commands" :as commands]
            ["prosemirror-inputrules" :as input-rules]
            [applied-science.js-interop :as j]))

(def chain commands/chainCommands)

(def undo history/undo)

(def redo history/redo)

(def inline-bold (pm/toggle-mark :strong))

(def inline-italic (pm/toggle-mark :em))

(def inline-code (pm/toggle-mark :code))

(def block-list-bullet (pm/wrap-in-list :bullet_list))

(def block-list-ordered (pm/wrap-in-list :ordered_list))

(def block-paragraph (pm/set-block-type :paragraph))

(defn block-heading [i]
  (pm/set-block-type :heading #js {"level" i}))

(def outdent (chain
               pm/lift-list-item
               pm/lift))

(def indent (chain pm/sink-list-item block-list-bullet))

(def hard-break
  (chain
    commands/exitCode
    (fn [^js/pm.EditorState state dispatch]
      (dispatch (->> (j/call (pm/get-node state :hard_break) :create)
                     (.replaceSelectionWith (.-tr state))
                     (pm/scroll-into-view)))
      true)))

(def newline-in-code commands/newlineInCode)

(defn empty-node? [node]
  (= 0 (j/get-in node [:content :size])))

(defn delete-cursor-node [state dispatch]
  (let [pos (j/get-in state [:selection :$anchor :pos])]
    (dispatch (j/call (.-tr state) :.deleteRange (max 0 (dec pos)) (inc pos)))))

(def enter (chain pm/split-list-item
                  commands/createParagraphNear
                  commands/liftEmptyBlock
                  commands/splitBlock))

(defn clear-empty-non-paragraph-nodes
  "If the cursor is in an empty node which is a heading or code-block, convert the node to a paragraph."
  [state dispatch]
  (let [node    (pm/cursor-node state)
        $cursor (.-$anchor (.-selection state))]
    (when (and (#{(pm/get-node state :heading)
                  (pm/get-node state :code_block)} (.-type node))
               (or (= 0 (.-size (.-content node)))
                   (= 0 (some-> $cursor (.-parentOffset)))))
      ((pm/set-block-type :paragraph) state dispatch))))

(def backspace (chain commands/deleteSelection
                      clear-empty-non-paragraph-nodes
                      commands/joinBackward
                      input-rules/undoInputRule))


(def join-up commands/joinUp)

(def join-down commands/joinDown)

(def select-parent-node commands/selectParentNode)

(def select-all commands/selectAll)

(def selection-stack (atom '()))

(defn clear-selections! []
  (reset! selection-stack '()))

(defn stack-selection! [n]
  (when (not= n (first @selection-stack))
    (swap! selection-stack conj n)))

(defn read-selection! []
  (let [n (second @selection-stack)]
    (swap! selection-stack rest)
    n))

(defn select-word [state dispatch]
  ;; TODO
  ;; implement `select-word` as the first step of `expand-selection`
  ;; also: select word by default on M1, to match behaviour of code
  )

(defn expand-selection
  "Expand selection upwards, by block."
  [state dispatch]
  (let [original-selection (.-selection state)
        had-selected-node? (and (not= (.-from original-selection)
                                      (.-to original-selection))
                                (let [node-selection (.create state/NodeSelection (.-doc state) (.-from original-selection))]
                                  (and (= (.-from original-selection)
                                          (.-from node-selection))
                                       (= (.-to original-selection)
                                          (.-to node-selection)))))]
    (when (= (.-from original-selection) (.-to original-selection))
      (clear-selections!))
    (loop [sel original-selection]
      (let [$from (.-$from sel)
            to    (.-to sel)
            same  (.sharedDepth $from to)]
        (if (= same 0)
          (do
            (stack-selection! 0)
            (select-all state dispatch))
          (let [pos            (.before $from same)
                $pos           (.resolve (.-doc state) pos)
                the-node       (.-nodeAfter $pos)
                node-selection (pm/NodeSelection. $pos)]
            (if (and (= 1 (.-childCount the-node))
                     had-selected-node?)
              (recur node-selection)
              (when dispatch
                (stack-selection! pos)
                (dispatch (.setSelection (.-tr state) node-selection))))))))))

(defn contract-selection [state dispatch]
  (when dispatch
    (let [sel (.-selection state)]
      (if (= (.-from sel) (.-to sel))
        (clear-selections!)
        (dispatch (.setSelection (.-tr state) (if-let [pos (read-selection!)]
                                                (pm/NodeSelection. (.resolve (.-doc state) pos))
                                                (.near pm/Selection (.-$anchor sel)))))))))

(defn heading->paragraph [state dispatch]
  (when (pm/is-node-type? state :heading)
    ((pm/set-block-type :paragraph) state dispatch)))

(defn adjust-font-size [f state dispatch]
  (let [node (pm/cursor-node state)]
    (when-let [heading-level (condp = (.-type node)
                               (pm/get-node state :paragraph) 4
                               (pm/get-node state :heading) (let [level (.-level (.-attrs node))]
                                                              (cond-> level
                                                                      (>= level 4) (inc)))
                               :else nil)]
      (let [target-index (min (f heading-level) 7)]
        ((cond (<= target-index 3) (pm/set-block-type :heading #js {:level target-index})
               (= target-index 4) (pm/set-block-type :paragraph)
               :else (pm/set-block-type :heading #js {:level (dec target-index)})) state dispatch)))))

(defn clear-stored-marks [state dispatch]
  (dispatch (reduce (fn [tr mark]
                      (.removeStoredMark tr mark)) (.-tr state)
                    (.. state -selection -$cursor (marks)))))

;;;;;; Input rules

;; TODO: mark input rules, see https://discuss.prosemirror.net/t/input-rules-for-wrapping-marks/537/10

(def rule-blockquote-start
  (pm/input-rule-wrap-block
    #"^>\s"
    :blockquote
    nil))

(def rule-toggle-code
  (input-rules/InputRule. #"[^`\\]+`$"
                          (fn [state & _]
                            (pm/toggle-mark-tr state :code))))

(def rule-block-list-bullet-start
  (pm/input-rule-wrap-block
    #"^\s*([-+*])\s$"
    :bullet_list
    nil))

(def rule-block-list-numbered-start
  (pm/input-rule-wrap-block
    #"^(\d+)\.\s$"
    :ordered_list
    (fn [match] #js {"order" (second match)})))

(def rule-block-code-start
  (pm/input-rule-wrap-inline
    #"^`$"
    :code_block
    nil))

(def rule-paragraph-start
  (pm/input-rule-wrap-inline
    #"^/p$"
    :paragraph
    nil))

(comment
  ;; need to implement a transform that adds an hr element
  ;; also need to handle selection/delete of hr
  (def hr
    (input-rules/InputRule. #"—-" "---")))

(def rule-block-heading-start
  (pm/input-rule-wrap-inline
    #"^(#{1,6})\s$"
    :heading
    (fn [match]
      #js {"level" (count (second match))})))

;; TODO
;; command to increase/decrease header level
#_(defn size-change
    [mode]
    (fn [state dispatch]
      (let [set-p   (pm/set-block-type :paragraph)
            set-h1  (pm/set-block-type :heading #js {"level" 1})
            active? (or (set-p state) (set-h1 state))]
        )))


(defn apply-command [prosemirror command]
  (command (.-state prosemirror) (.-dispatch prosemirror) prosemirror))


(defn open-link
  ([state dispatch] (open-link false state dispatch nil))
  ([state dispatch view] (open-link false state dispatch view))
  ([force? state dispatch view]
   (when-let [$cursor (.. state -selection -$cursor)]
     (when-let [link-mark (pm/pos-mark state $cursor :link)]
       (let [{:keys [from to]} (pm/mark-extend state $cursor :link)
             text (str "[" (.textBetween (.-doc state) from to) "](" (.. link-mark -attrs -href) ")")]
         (when (or force? (= to (.-pos $cursor)))
           (dispatch (-> (.-tr state)
                         (.removeMark from
                                      to
                                      (pm/get-mark state :link))
                         (.insertText text from to)))
           true))))))

(defn open-image [state dispatch]
  (when-let [$cursor (.. state -selection -$cursor)]
    (let [image-node (.-nodeBefore $cursor)
          image-type (pm/get-node state :image)
          to         (.-pos $cursor)]
      (when (= (some-> image-node (.-type)) image-type)
        (let [from (- to (.-nodeSize image-node))
              text (str "![" (.. image-node -attrs -title) "](" (.. image-node -attrs -src) ")")]
          (when (= to (.-pos $cursor))
            (dispatch (-> (.-tr state)
                          (.insertText text from to)))
            true))))))

(defn end-link [state dispatch]
  (when-let [$cursor (.. state -selection -$cursor)]
    (let [the-mark (pm/get-mark state :link)]
      (when (pm/has-mark? state the-mark)
        (let [{:keys [to]} (pm/mark-extend state $cursor the-mark)]
          (when (= to (.-pos $cursor))
            (when (re-find #"[\.\s]" (.textBetween (.-doc state) (dec to) to))
              (dispatch (-> (.-tr state) (.removeMark (dec to) to the-mark)
                            (.removeStoredMark the-mark))))))))))

(def rule-image-and-links (input-rules/InputRule.
                            #"(\!?)\[(.*)\]\((.*)\)\s?$"
                            (fn [state [the-match kind label target] from to]
                              (if (= kind "!")
                                (pm/add-image-tr state from to label target)
                                (pm/add-link-tr state from to label target)))))