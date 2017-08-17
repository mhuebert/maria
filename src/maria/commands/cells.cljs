(ns maria.commands.cells
  (:require [maria-commands.registry :refer-macros [defcommand]]
            [maria.cells.core :as Cell]))

(defcommand :eval/doc
  "Evaluate whole doc"
  {:bindings ["M1-M2-Enter"]
   :when     :cell-list}
  [{:keys [cell-list]}]
  (doseq [cell (.getCells cell-list)]
    (when (satisfies? Cell/IEval cell)
      (Cell/eval cell))))

(defcommand :cell/next-cell
  {:bindings ["Down"
              "Right"]
   :when     #(some-> (:cell %) Cell/at-end?)}
  [{:keys [cell cells]}]
  (when (Cell/at-end? cell)
    (some-> (Cell/after cells cell)
            (Cell/focus! :start))))

(defcommand :cell/previous-cell
  {:bindings ["Up"
              "Left"]
   :when     #(some-> (:cell %) Cell/at-start?)}
  [{:keys [cell cells]}]
  (when (Cell/at-start? cell)
    (some-> (Cell/before cells cell)
            (Cell/focus! :end))))

(defcommand :selection/expand
  "Expand current selection"
  {:bindings ["M1-]"
              "M1-1"]
   :when     :cell}
  [{:keys [cell] :as context}]
  (Cell/selection-expand cell))

(defcommand :selection/shrink
  "Contract selection (reverse of expand-selection)"
  {:bindings ["M1-["
              "M1-2"]
   :when     :cell}
  [{:keys [cell] :as context}]
  (Cell/selection-contract cell))
