(ns maria.magic-tree
  (:require [magic-tree.core :as tree]
            [fast-zip.core :as z]))

(defn error-ranges [source error-location]
  ;; namespace warnings have no/zero position
  (some-> (tree/ast source)
          (tree/ast-zip)
          (tree/node-at error-location)
          (z/node)
          (tree/node-highlights)))