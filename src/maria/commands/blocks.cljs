(ns maria.commands.blocks
  (:require [commands.registry :refer-macros [defcommand]]
            [maria.blocks.blocks :as Block]
            [maria.editors.editor :as Editor]
            [maria.blocks.history :as history]
            [maria.commands.prose :as prose]
            [maria.commands.code :as code]
            [re-view-prosemirror.commands :as commands]
            [maria.views.icons :as icons]))

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

(defcommand :navigate/next-block
  {:bindings ["Down"
              "Right"
              "M2-Tab"]}
  [context]
  (if (and (#{"ArrowDown" "ArrowRight"} (:key context))
           (not (some-> (:editor context) (Editor/at-end?))))
    false
    (focus-adjacent! context :right)))

(defcommand :navigate/prev-block
  {:bindings ["Up"
              "Left"
              "M2-Shift-Tab"]}
  [context]
  (if (and (#{"ArrowUp" "ArrowLeft"} (:key context))
           (not (some-> (:editor context) (Editor/at-start?))))
    false
    (focus-adjacent! context :left)))

(defcommand :select/up
  "Expand current selection"
  {:bindings ["M1-1"
              "M1-Up"]
   :when     :block}
  [{:keys [editor block/prose block/code] :as context}]
  (cond code (code/select-up editor)
        prose (prose/select-up editor)
        :else nil))

(defcommand :select/back
  "Contract selection (reverse of expand-selection)"
  {:bindings ["M1-2"
              "M1-Down"]
   :when     :block}
  [{:keys [editor block/prose block/code] :as context}]
  (cond code (code/select-reverse editor)
        prose (prose/select-reverse editor)
        :else nil))

(defcommand :select/all
  {:bindings ["M1-A"]}
  [context]
  (cond (:block/prose context)
        (commands/apply-command (:editor context) commands/select-all)
        (:block/code context)
        (.execCommand (:editor context) "selectAll")
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