(ns maria.live.magic-tree
  (:require [magic-tree.core :as tree]
            [magic-tree.range :as range]
            [fast-zip.core :as z]
            [maria.eval :as e]))

(defn highlights-for-position
  "Return ranges for appropriate highlights for a position within given Clojure source."
  [source position]
  (when-let [highlights (some-> (tree/ast (:ns @e/c-env) source)
                                (tree/ast-zip)
                                (tree/node-at position)
                                (z/node)
                                (tree/node-highlights))]
    (case (count highlights)
      0 nil
      1 (first highlights)
      2 (merge (second highlights)
               (range/boundaries (first highlights) :left)))))