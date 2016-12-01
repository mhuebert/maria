(ns maria.tree.node
  (:require [maria.tree.emit :as unwrap]
            [fast-zip.core :as z]))



#_(defn within? [container pos]
  (condp = (type container)
    z/ZipperLocation
    (within? (z/node container) pos)

    PersistentArrayMap
    #_maria.tree.parse/Node
    (let [{r :line c :column} pos
          {:keys [line column end-line end-column]} container]
      (and (>= r line)
           (<= r end-line)
           (if (= r line) (>= c column) true)
           (if (= r end-line) (<= c end-column) true)))))

(defn contains-fn [include-boundaries?]
  (let [[gt lt] (case include-boundaries?
                      true [>= <=]
                      false [> <])]
    (fn within? [container pos]
      (condp = (type container)
        z/ZipperLocation
        (within? (z/node container) pos)

        PersistentArrayMap
        #_maria.tree.parse/Node
        (let [{r :line c :column} pos
              {:keys [line column end-line end-column]} container]
          (and (>= r line)
               (<= r end-line)
               (if (= r line) (gt c column) true)
               (if (= r end-line) (lt c end-column) true)))))))

(def within-inner? (contains-fn true))
(def within? (contains-fn false))

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
              left (conj {:line       (:line node) :end-line (:line node)
                          :column     (:column node)
                          :end-column (+ (:column node) (count left))})
              right (conj {:line       (:end-line node) :end-line (:end-line node)
                           :column     (- (:end-column node) (count right))
                           :end-column (:end-column node)})))))

(defn has-edges? [node]
  (contains? unwrap/edges (get node :tag)))

(defn inner-range [{:keys [line column end-line end-column tag]}]
  (when-let [[left right] (get unwrap/edges tag)]
    {:line       line
     :column     (+ column (count left))
     :end-line   end-line
     :end-column (- end-column (count right))}))

(defn at-boundary? [node pos])