(ns maria.live.magic-tree
  (:require [magic-tree.core :as tree]
            [magic-tree.range :as range]
            [fast-zip.core :as z]))

(defn error-range [source error-location]
  (when-let [highlights (some-> (tree/ast source)
                                (tree/ast-zip)
                                (tree/node-at error-location)
                                (z/node)
                                (tree/node-highlights))]
    (case (count highlights)
      0 nil
      1 (first highlights)
      2 (merge (second highlights)
               (range/boundaries (first highlights)  :left)))))