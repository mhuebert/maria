(ns maria.commands.prose
  (:require [commands.registry :refer-macros [defcommand]]
            [cljsjs.codemirror :as CM]
            [structure.edit :as edit]
            [re-view-prosemirror.commands :as commands :refer [apply-command]]
            [re-view-prosemirror.markdown :as prose]
            [re-view-prosemirror.core :as pm]
            [maria.blocks.blocks :as Block]
            [maria.editors.editor :as Editor]
            [maria.util :as util]
            [maria.blocks.history :as history]
            [re-view.core :as v]
            [maria.views.icons :as icons]))

(defn empty-root-paragraph? [state]
  (and (= 1 (pm/cursor-depth state))
       (pm/is-node-type? state :paragraph)
       (commands/empty-node? (pm/cursor-node state))))

(defn split-with-code-block [block-list block state {:keys [content cursor-coords]}]
  (when (empty-root-paragraph? state)
    (let [$head (.. state -selection -$head)
          markdown-before (prose/serialize-selection (.between pm/TextSelection (pm/start-$pos state) $head))
          markdown-after (prose/serialize-selection (.between pm/TextSelection $head (pm/end-$pos state)))
          new-blocks (or (some-> content (Block/from-source))
                         [(Block/create :code)])]
      (.splice block-list block (cond-> []
                                        (not (util/whitespace-string? markdown-before))
                                        (conj (Block/create :prose markdown-before))

                                        true (into new-blocks)
                                        (not (util/whitespace-string? markdown-after))
                                        (conj (Block/create :prose markdown-after))))
      (-> (first new-blocks)
          (Editor/of-block)
          (Editor/focus! cursor-coords))
      true)))

(defn enter [{:keys [editor block block-list]}]
  (commands/apply-command editor
                          (commands/chain
                            (fn [state dispatch]
                              (split-with-code-block block-list block state nil))
                            commands/enter)))

(defcommand :prose/newline
  {:bindings       ["Shift-Enter"
                    "M2-Enter"]
   :when           :block/prose
   :private        true
   :intercept-when true}
  [context]
  (apply-command (:editor context) commands/newline-in-code))

(defcommand :format/bold
  {:bindings ["M1-b"]
   :icon     icons/FormatBold
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/inline-bold))

(defcommand :format/italic
  {:bindings ["M1-i"]
   :icon     icons/FormatItalic
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/inline-italic))


(defcommand :format/code-inline
  {:bindings ["M3-`"
              "M1-`"]
   :icon     icons/Code
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/inline-code))


(defcommand :format/bullet-list
  {:bindings ["Shift-Ctrl-8"]
   :when     :block/prose
   :icon     icons/FormatListBulleted}
  [context]
  (apply-command (:editor context) commands/block-list-bullet))


(defcommand :format/numbered-list
  {:bindings ["Shift-Ctrl-9"]
   :when     :block/prose
   :icon     icons/FormatListOrdered}
  [context]
  (apply-command (:editor context) commands/block-list-ordered))


(defcommand :format/paragraph
  {:bindings ["Shift-Ctrl-0"]
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/block-paragraph))

(defcommand :format/code-block
  {:when :block/prose
   :icon icons/Code}
  [context]
  (apply-command (:editor context) (pm/set-block-type :code_block)))


(defcommand :format/outdent
  {:bindings ["Shift-Tab"]
   :when     :block/prose
   :icon     icons/FormatOutdent}
  [context]
  (apply-command (:editor context) (commands/chain commands/heading->paragraph
                                                   commands/outdent)))


(defcommand :format/indent
  {:bindings ["Tab"]
   :when     :block/prose
   :icon     icons/FormatIndent}
  [context]
  (apply-command (:editor context) commands/indent))

(defn remove-empty-block [{:keys [blocks block-list block]}]
  (fn [_ _]
    (when (and (Block/empty? block) (Block/left blocks block))
      (let [result (.splice block-list block [])
            {:keys [before after]} (meta result)]
        (if before (some-> (Editor/of-block before) (Editor/focus! :end))
                   (some-> (Editor/of-block after) (Editor/focus! :start)))
        true))))

(defn clear-previous-empty [{:keys [blocks block-list block editor]}]
  (fn [_ _]
    (when (and (Editor/at-start? editor)
               (some-> (Block/left blocks block) (Block/empty?)))
      (.splice block-list (Block/left blocks block) block [block])
      true)))

(defn join-with-prev [{:keys [blocks block-list block editor]}]
  (fn [_ _]
    (when (Editor/at-start? editor)

      (let [before (Block/left blocks block)]
        (when (= :prose (some-> before (Block/kind)))
          (when-let [editor (Editor/of-block before)]
            (let [sel (.atEnd pm/Selection (Block/state before))
                  anchor (.-anchor sel)
                  state (:view/state block-list)
                  _ (binding [history/*ignored-op* true]
                      (.dispatch editor (-> (.. editor -state -tr)
                                            (.insert anchor (.-content (Block/state block)))
                                            (.join (inc anchor) (.-depth sel))
                                            (.setSelection sel))))
                  next-before (assoc before :doc (.. editor -state -doc))]

              (-> (Editor/of-block next-before)
                  (Editor/focus!))
              (history/add! state (Block/splice-blocks blocks before block [next-before]))

              true)))))))

(defcommand :prose/backspace
  {:bindings ["Backspace"]
   :private  true
   :when     :block/prose}
  [{:keys [editor block blocks block-list] :as context}]
  (apply-command editor
                 (commands/chain
                   commands/open-link
                   commands/open-image
                   (join-with-prev context)
                   (remove-empty-block context)
                   (clear-previous-empty context)
                   commands/backspace)))

(defcommand :prose/space
  {:bindings       ["Space"]
   :when           :block/prose
   :private        true
   :intercept-when false}
  [context]
  (apply-command (:editor context) commands/end-link))

(defcommand :edit/join-up
  {:bindings ["M2-Up"]
   :private  true
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/join-up))

(defcommand :edit/join-down
  {:bindings ["M2-Down"]
   :private  true
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/join-down))

(defcommand :format/clear-styles
  {:bindings ["Esc"]
   :private  true
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/clear-stored-marks))


(defcommand :format/larger
  {:when           :block/prose
   :intercept-when :block/prose
   :bindings       ["M1-="]
   :icon           icons/FormatSize}
  [context]
  (apply-command (:editor context) (partial commands/adjust-font-size dec)))

(defcommand :format/smaller
  {:when           :block/prose
   :intercept-when :block/prose
   :bindings       ["M1-Dash"]
   :icon           icons/FormatSize}
  [context]
  (apply-command (:editor context) (partial commands/adjust-font-size inc)))

(defn input-rules [block-view]
  [commands/rule-blockquote-start
   commands/rule-block-list-bullet-start
   commands/rule-block-list-numbered-start
   commands/rule-block-code-start
   commands/rule-toggle-code
   commands/rule-block-heading-start
   commands/rule-paragraph-start
   commands/rule-image-and-links
   (pm/InputRule.
     #"^\($"
     (fn [state [bracket] & _]
       (when (empty-root-paragraph? state)
         (js/setTimeout
           #(split-with-code-block (:block-list block-view)
                                   (:block block-view)
                                   state {:content       (str bracket (edit/other-bracket bracket))
                                          :cursor-coords (CM/Pos 0 1)}) 0)
         (.-tr state))))])

(defn select-up [this]
  (commands/apply-command this commands/expand-selection))

(defn select-reverse [this]
  (commands/apply-command this commands/contract-selection))