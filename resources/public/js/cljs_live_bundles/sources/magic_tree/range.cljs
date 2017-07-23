(ns magic-tree.range
  (:require [magic-tree.emit :as unwrap]
            [magic-tree.node :as n]
            [fast-zip.core :as z]))

(defn contains-fn [include-boundaries?]
  (let [[gt lt] (case include-boundaries?
                  true [>= <=]
                  false [> <])]
    (fn within? [container pos]
      (condp = (type container)
        z/ZipperLocation
        (within? (z/node container) pos)

        PersistentArrayMap
        #_magic-tree.parse/Node
        (let [{r :line c :column} pos
              {:keys [line column end-line end-column]} container]
          (and (>= r line)
               (<= r end-line)
               (if (= r line) (gt c column) true)
               (if (= r end-line) (lt c end-column) true)))))))

(defn at-boundary? [node pos])
(def within? (contains-fn true))
(def inside? (contains-fn false))

(defn edge-ranges [node]
  (when (n/has-edges? node)
    (let [[left right] (get unwrap/edges (get node :tag))]
      (cond-> []
              left (conj {:line       (:line node) :end-line (:line node)
                          :column     (:column node)
                          :end-column (+ (:column node) (count left))})
              right (conj {:line       (:end-line node) :end-line (:end-line node)
                           :column     (- (:end-column node) (count right))
                           :end-column (:end-column node)})))))

(defn inner-range [{:keys [line column end-line end-column tag]}]
  (when-let [[left right] (get unwrap/edges tag)]
    {:line       line
     :column     (+ column (count left))
     :end-line   end-line
     :end-column (- end-column (count right))}))

(defn boundaries
  "Returns position map for left or right boundary of the node."
  ([node] (select-keys node [:line :column :end-line :end-column]))
  ([node side]
   (case side :left (select-keys node [:line :column])
              :right {:line   (:end-line node)
                      :column (:end-column node)})))

(defn node-highlights
  "Get range(s) to highlight for a node. For a collection, only highlight brackets."
  [node]
  (if (n/may-contain-children? node)
    (if (second (get unwrap/edges (get node :tag)))
      (edge-ranges node)
      (update (edge-ranges (first (:value node))) 0 merge (boundaries node :left)))
    [node]))