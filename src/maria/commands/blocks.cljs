(ns maria.commands.blocks
  (:require [maria-commands.registry :refer-macros [defcommand]]
            [maria.blocks.blocks :as Block]
            [maria.block-views.editor :as Editor]
            [maria.blocks.history :as history]
            [re-db.d :as d]))

(defcommand :eval/doc
  "Evaluate whole doc"
  {:bindings ["M1-M2-Enter"]
   :when     :block-list}
  [{:keys [block-list]}]
  (doseq [block (.getBlocks block-list)]
    (when (satisfies? Block/IEval block)
      (Block/eval! block :string (Block/emit block)))))

(defcommand :block/next-block
  {:bindings ["Down"
              "Right"]
   :when     #(some-> % :editor Editor/at-end?)}
  [{:keys [block blocks block-view editor]}]
  (when (Editor/at-end? editor)
    (some-> (Block/right blocks block)
            (Editor/of-block)
            (Editor/focus! :start))))

(defcommand :block/previous-block
  {:bindings ["Up"
              "Left"]
   :when     #(some-> % :editor Editor/at-start?)}
  [{:keys [block blocks editor]}]

  (when (Editor/at-start? editor)
    (some-> (Block/left blocks block)
            (Editor/of-block)
            (Editor/focus! :end))))

(defcommand :selection/expand
  "Expand current selection"
  {:bindings ["M1-1"
              "M1-Up"]
   :when     :block}
  [{:keys [editor] :as context}]
  (Editor/selection-expand editor))

(defcommand :selection/shrink
  "Contract selection (reverse of expand-selection)"
  {:bindings ["M1-2"
              "M1-Down"]
   :when     :block}
  [{:keys [editor] :as context}]
  (Editor/selection-contract editor))

(defcommand :block/undo
  {:bindings ["M1-z"]}
  [{:keys [block-list]}]
  (history/undo (:view/state block-list)))

(defcommand :block/redo
  {:bindings ["M1-Shift-z"]}
  [{:keys [block-list]}]
  (history/redo (:view/state block-list)))