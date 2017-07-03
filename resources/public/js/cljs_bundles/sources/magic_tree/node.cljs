(ns magic-tree.node
  (:require [magic-tree.emit :as unwrap]
            [fast-zip.core :as z]))

(defn comment?
  "Returns true if node is a comment - either `;` or `#_` but not `(comment ...)`"
  [node]
  (#{:uneval :comment} (get node :tag)))

(defn whitespace?
  [node]
  (#{:space :newline :comma} (get node :tag)))

(defn newline?
  [node]
  (= :newline (get node :tag)))

(def sexp?
  "Returns false if node does not have corresponding s-expression (eg. comments and whitespace)"
  (every-pred (complement comment?)
              (complement whitespace?)))

(defn terminal-node? [node]
  (boolean (#{:string :token :regex :var :keyword :namespaced-keyword :space :newline :comma :comment} (get node :tag))))

(def may-contain-children? (complement terminal-node?))

(defn has-edges?
  "Returns true if node has 'edges' that mark boundaries. See unwrap/edges for details."
  [node]
  (contains? unwrap/edges (get node :tag)))

