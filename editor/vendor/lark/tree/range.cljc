(ns lark.tree.range
  (:require [lark.tree.node :as n]
            [lark.tree.reader :as rd]
            [fast-zip.core :as z]))

(defn lt [pos1 pos2]
  (or (< (:line pos1) (:line pos2))
      (and (= (:line pos1) (:line pos2))
           (< (:column pos1) (:column pos2)))))

(comment
  (assert (lt {:line 0 :column 0} {:line 0 :column 1}))
  (assert (lt {:line 0 :column 0} {:line 1 :column 0}))
  (assert (not (lt {:line 0 :column 1} {:line 0 :column 0})))
  (assert (not (lt {:line 1 :column 0} {:line 0 :column 0})))
  (assert (not (lt {:line 0 :column 0} {:line 0 :column 0}))))

(defn contains-fn [include-edges?]
  (let [[greater-than less-than] (case include-edges?
                                   true [>= <=]
                                   false [> <])]
    (fn within? [container pos]
      (and container
           (if (= (type container) z/ZipperLocation)
             (within? (z/node container) pos)
             (let [{pos-line :line pos-column :column} pos
                   {end-pos-line :end-line end-pos-column :end-column
                    :or          {end-pos-line   pos-line
                                  end-pos-column pos-column}} pos
                   {:keys [line column end-line end-column]} container]
               (and (>= pos-line line)
                    (<= end-pos-line end-line)
                    (if (= pos-line line) (greater-than pos-column column) true)
                    (if (= end-pos-line end-line) (less-than end-pos-column end-column) true))))))))

(def within? (contains-fn true))
(def within-inner? (contains-fn false))

(defn edge-ranges [node]
  (when (n/has-edges? node)
    (let [[left right] (get rd/edges (get node :tag))]
      (cond-> []
              left (conj {:line       (:line node) :end-line (:line node)
                          :column     (:column node)
                          :end-column (+ (:column node) (count left))})
              right (conj {:line       (:end-line node) :end-line (:end-line node)
                           :column     (- (:end-column node) (count right))
                           :end-column (:end-column node)})))))

(defn inner-range [{:keys [line column end-line end-column tag] :as node}]
  (if-let [[left right] (get rd/edges tag)]
    {:line       line
     :column     (+ column (count left))
     :end-line   end-line
     :end-column (- end-column (count right))}
    node))

(defn ->end [{:keys [line column]}]
  {:end-line line :end-column column})

(defn end [{:keys [end-line end-column]}]
  {:line end-line :column end-column})

(defn bounds
  "Returns position map for left or right boundary of the node."
  ([node] (select-keys node [:line :column :end-line :end-column]))
  ([node side]
   (case side :left {:line (get node :line)
                     :column (get node :column)}
              :right (if-let [end-line (:end-line node)]
                       {:line   end-line
                        :column (:end-column node)}
                       (bounds node :left)))))

(defn range= [p1 p2]
  (= (bounds p1)
     (bounds p2)))

(defn pos= [p1 p2]
  (= (bounds p1 :left)
     (bounds p2 :left)))

(defn empty-range? [node]
  (and (or (= (:line node) (:end-line node)) (nil? (:end-line node)))
       (or (= (:column node) (:end-column node)) (nil? (:end-column node)))))

(defn node-highlights
  "Get range(s) to highlight for a node. For a collection, only highlight brackets."
  [node]
  (if (n/may-contain-children? node)
    (if (second (get rd/edges (get node :tag)))
      (edge-ranges node)
      (update (edge-ranges (first (:children node))) 0 merge (bounds node :left)))
    [node]))