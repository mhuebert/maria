(ns maria.tree.node
  (:require [maria.tree.emit :as unwrap]
            [fast-zip.core :as z]))

(defn within? [container pos]
  (condp = (type container)
    z/ZipperLocation
    (within? (z/node container) pos)

    PersistentArrayMap
    #_maria.tree.parse/Node
    (let [{r :row c :col} pos
          {:keys [row col end-row end-col]} container]
      (and (>= r row)
           (<= r end-row)
           (if (= r row) (>= c col) true)
           (if (= r end-row) (<= c end-col) true)))))

(defn comment? [node] (#{:uneval :comment} (get node :tag)))
(defn whitespace? [node] (#{:space :newline :comma} (get node :tag)))
(defn newline? [node] (= :newline (get node :tag)))
(def sexp? (every-pred (complement comment?)
                       (complement whitespace?)))

(defn terminal-node? [node]
  (boolean (#{:string :token :regex :var :keyword :namespaced-keyword :space :newline :comma :comment} (get node :tag))))

(def can-have-children? (complement terminal-node?))

(defn edge-ranges [node]
  (when (can-have-children? node)
    (let [[left right] (get unwrap/edges (get node :tag))]
      (cond-> []
              left (conj {:row     (:row node) :end-row (:row node)
                          :col     (:col node)
                          :end-col (+ (:col node) (count left))})
              right (conj {:row     (:end-row node) :end-row (:end-row node)
                           :col     (- (:end-col node) (count right))
                           :end-col (:end-col node)})))))