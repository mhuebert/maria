(ns maria.commands.cells
  (:require [maria-commands.registry :refer-macros [defcommand]]
            [maria.cells.core :as Cell]
            [maria.eval :as e]))

(defcommand :eval/doc
  "Evaluate whole doc"
  {:bindings ["M1-M2-Enter"]
   :when     #(or (:cell-list %) (:cell/code %))}
  [{:keys [cell-list cell-view editor]}]
  (if cell-list
    (doseq [cell (:cells @(:view/state cell-list))]
      (when (satisfies? Cell/ICode cell)
        (Cell/eval cell)))
    (e/logged-eval-str (:id cell-view) (.getValue editor))))

(defcommand :cell/next-cell
  {:bindings ["Down"
              "Right"]
   :when     #(some-> (:cell %) Cell/at-end?)}
  [{:keys [cell]}]
  (Cell/edge-right cell))

(defcommand :cell/previous-cell
  {:bindings ["Up"
              "Left"]
   :when     #(some-> (:cell %) Cell/at-start?)}
  [{:keys [cell]}]
  (Cell/edge-left cell))

(defcommand :selection/expand
  "Expand current selection"
  {:bindings ["M1-]" "M1-1"]
   :when     :cell}
  [{:keys [cell] :as context}]
  (Cell/selection-expand cell))

(defcommand :selection/shrink
  "Contract selection (reverse of expand-selection)"
  {:bindings ["M1-[" "M1-2"]
   :when     :cell}
  [{:keys [cell] :as context}]
  (Cell/selection-contract cell))
