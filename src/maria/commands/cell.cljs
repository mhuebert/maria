(ns maria.commands.cell
  (:require [maria-commands.registry :refer-macros [defcommand]]
            [maria.cells.core :as Cell]))


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