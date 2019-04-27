(ns lark.tree.node
  (:require [lark.tree.reader :as rd]
            [fast-zip.core :as z]
   #?(:clj
            [lark.tree.util :refer [contains-identical-keyword?]]))
  #?(:cljs (:require-macros [lark.tree.util :refer [contains-identical-keyword?]])))

(defn comment?
  "Returns true if node is a comment - either `;` or `#_` but not `(comment ...)`"
  [node]
  (contains-identical-keyword? [:comment :uneval]
                               (.-tag node)))

(defn whitespace?
  [node]
  (rd/whitespace-tag? (.-tag node)))

(defn newline?
  [node]
  (= :newline (.-tag node)))

(def sexp?
  "Returns false if node does not have corresponding s-expression (eg. comments and whitespace)"
  (every-pred (complement comment?)
              (complement whitespace?)))

(defn terminal-node? [node]
  (let [tag (.-tag node)]
    (and (not (contains-identical-keyword? [:list :vector :map] tag))
         (contains-identical-keyword? [:space
                                       :symbol
                                       :keyword
                                       :token
                                       :string
                                       :number
                                       :newline
                                       :comma
                                       :comment
                                       :comment-block
                                       :unmatched-delimiter] tag))))

(def may-contain-children? (complement terminal-node?))

(defn has-edges?
  "Returns true if node has 'edges' that mark boundaries. See unwrap/edges for details."
  [node]
  (when node
    (contains? rd/edges (.-tag node))))

(defn edges [node]
  (when node
    (get rd/edges (.-tag node))))

(defn ast-zip
  "Given AST, returns zipper"
  [ast]
  (z/zipper
   may-contain-children?
   (fn [{:keys [children]}] (when-not (empty? children) children))
   (fn [node children] (assoc node :children children))
   ast))