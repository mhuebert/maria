(ns maria.commands.blocks
  (:require [lark.commands.registry :refer-macros [defcommand]]
            [maria.blocks.blocks :as Block]
            [lark.editors.editor :as Editor]
            [maria.blocks.history :as history]
            [maria.commands.prose :as prose]
            [maria.commands.code :as code]
            [re-view-prosemirror.commands :as commands]
            [maria.views.icons :as icons]
            [lark.structure.codemirror :as cm]))

(defcommand :eval/doc
  "Evaluate whole doc"
  {:bindings ["M1-M2-Enter"]
   :when     :block-list}
  [{:keys [block-list]}]
  (doseq [block (.getBlocks block-list)]
    (when (satisfies? Block/IEval block)
      (Block/eval! block :string (Block/emit block)))))

(defn focus-adjacent! [{:keys [blocks block]} dir]
  (some-> ((case dir :right Block/right
                     :left Block/left) blocks block)
          (Editor/of-block)
          (Editor/focus! (case dir :right :start
                                   :left :end)))
  true)


(defcommand :navigate/forward
  {:bindings ["Down"
              "Right"]
   :when     :block-list}
  [context]
  (if (and (:binding-vec context)
           (not (some-> (:editor context) (Editor/at-end?))))
    false
    (focus-adjacent! context :right)))

(defcommand :navigate/backward
  {:bindings ["Up"
              "Left"]
   :when     :block-list}
  [context]
  (if (and (:binding-vec context)
           (not (some-> (:editor context) (Editor/at-start?))))
    false
    (focus-adjacent! context :left)))

(defcommand :select/up
  "Expand current selection"
  {:bindings ["M1-1"
              "M1-Up"]
   :when     :block
   :icon     icons/Select}
  [{:keys [editor block/prose block/code] :as context}]
  (cond code (do (code/select-up editor) true)
        prose (do (prose/select-up editor) true)
        :else nil))

(defcommand :select/back
  "Contract selection (reverse of expand-selection)"
  {:bindings ["M1-Down"
              "M1-2"]
   :when     :block
   :icon     icons/Select}
  [{:keys [editor block/prose block/code] :as context}]
  (cond code (do (code/select-reverse editor) true)
        prose (do (prose/select-reverse editor) true)
        :else nil))

(defcommand :select/all
  {:bindings ["M1-a"]
   :when     :editor
   :icon     icons/Select}
  [{:keys [editor block/prose block/code]}]
  (cond prose
        (commands/apply-command editor commands/select-all)
        code
        (do
          (cm/unset-cursor-root! editor)
          (.execCommand editor "selectAll"))
        :else nil))

(defcommand :history/undo
  {:bindings ["M1-z"]
   :icon     icons/Undo}
  [{:keys [block-list]}]
  (history/undo (:view/state block-list)))

(defcommand :history/redo
  {:bindings ["M1-Shift-z"]
   :icon     icons/Redo}
  [{:keys [block-list]}]
  (history/redo (:view/state block-list)))

(defcommand :enter
  {:bindings ["Enter"]
   :private  true}
  [context]
  (cond (:block/prose context)
        (prose/enter context)
        (:block/code context)
        (code/enter context)
        :else nil))

(defcommand :navigate/focus-start
  {:private true}
  [context]
  (some-> (filter (complement Block/whitespace?) (.getBlocks (:block-list context)))
          (first)
          (Editor/of-block)
          (Editor/focus! :start)))

(defcommand :navigate/focus-end
  {:private true
   :when    :block-list}
  [context]
  (some-> (filter (complement Block/whitespace?) (.getBlocks (:block-list context)))
          (last)
          (Editor/of-block)
          (Editor/focus! :end)))