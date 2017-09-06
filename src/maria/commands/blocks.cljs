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

(defn focus-adjacent! [{:keys [blocks block]} dir]
  (some-> ((case dir :right Block/right
                     :left Block/left) blocks block)
          (Editor/of-block)
          (Editor/focus! (case dir :right :start
                                   :left :end)))
  true)



(defcommand :block/next-block-jump
  {:bindings ["M2-Tab"]}
  [context]
  (focus-adjacent! context :right))

(defcommand :block/next-block
  {:bindings ["Down"
              "Right"]
   :when     #(some-> % :editor Editor/at-end?)}
  [context]
  (when (Editor/at-end? (:editor context))
    (focus-adjacent! context :right)))

(defcommand :block/prev-block
  {:bindings ["Up"
              "Left"]
   :when     #(some-> % :editor Editor/at-start?)}
  [context]
  (when (Editor/at-start? (:editor context))
    (focus-adjacent! context :left)))

(defcommand :block/prev-block-jump
  {:bindings ["M2-Shift-Tab"]}
  [context]
  (focus-adjacent! context :left))


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