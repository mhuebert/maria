(ns lark.tree.cursor
  (:require [lark.tree.nav :as nav]
            [fast-zip.core :as z]
            [lark.tree.node :as n]
            [lark.tree.range :as range]
            [lark.tree.reader :as r]
            [lark.tree.util :as util]))

(defn pos-offset [{node-line :line
                   node-col :column} {pos-line :line
                                      pos-col :column}]
  [(- pos-line node-line)
   (if (= node-line pos-line)
     (- pos-col node-col)
     pos-col)])

(defn path
  ([zipper pos] (path zipper pos nil))
  ([zipper pos cursor-loc]
   (let [loc-at-pos (nav/navigate zipper pos)
         node (z/node loc-at-pos)
         [i loc sticky data] (or

                              (when cursor-loc
                                [0 cursor-loc :cursor-space (-> (pos-offset (z/node cursor-loc) pos)
                                                                #_(update 1 (partial min 2)))])

                              (when-let [inner-range (and (n/has-edges? node)
                                                          (range/inner-range node))]
                                (cond (range/pos= pos inner-range)
                                      [1 loc-at-pos :inner-left]
                                      (range/pos= pos (range/end inner-range))
                                      [2 loc-at-pos :inner-right]))
                              (when-let [adjacent-loc (->> [loc-at-pos (z/left loc-at-pos) (z/right loc-at-pos)]
                                                           (remove nil?)
                                                           (filter (comp nav/path-node-pred z/node))
                                                           (first))]
                                (let [adjacent-node (z/node adjacent-loc)]
                                  (cond (range/pos= pos adjacent-node)
                                        [3 adjacent-loc :outer-left]
                                        (range/pos= pos (range/end adjacent-node))
                                        [4 adjacent-loc :outer-right])))
                              (when (some-> (z/up loc-at-pos)
                                            (z/node)
                                            (range/inner-range)
                                            (range/pos= pos))
                                [5 (z/up loc-at-pos) :inner-left])
                              (when (and (not (n/whitespace? node))
                                         (n/terminal-node? node)
                                         (range/within? node pos))
                                [6 loc-at-pos :terminal-offset (pos-offset node pos)])
                              (when (and (nil? (z/right loc-at-pos))
                                         (z/up loc-at-pos))
                                [7 (z/up loc-at-pos) :inner-right])

                              (when (some-> (z/left loc-at-pos)
                                            (z/node)
                                            (util/guard-> nav/path-node-pred)
                                            (range/end)
                                            (= pos))
                                [8 (z/left loc-at-pos) :outer-right])

                              (when-let [loc (first (->> (nav/right-locs loc-at-pos)
                                                         (take-while (comp (complement n/newline?) z/node))
                                                         (filter (comp (complement n/whitespace?) z/node))))]
                                [9 loc :outer-left])
                              (when-let [loc (or (some-> loc-at-pos
                                                         (util/guard-> (comp n/newline? z/node)))
                                                 (first (->> (nav/left-locs loc-at-pos)
                                                             (take-while (comp (complement n/newline?) z/node))
                                                             (filter (comp (complement n/whitespace?) z/node)))))]
                                [10 loc :outer-right])
                              (when-let [loc (->> [loc-at-pos (z/up loc-at-pos)]
                                                  (keep identity)
                                                  (filter (comp #(range/pos= node %)
                                                                range/inner-range
                                                                z/node))
                                                  (first))]
                                [11 loc :inner-left])
                              [12 loc-at-pos :not-found])]
     [(nav/get-path loc)
      sticky
      data])))

(defn resolve-offset [[line-offset column-offset] {node-line :line node-col :column}]
  {:line (+ line-offset node-line)
   :column (if (zero? line-offset)
             (+ node-col column-offset)
             column-offset)})


(defn position [zipper [path sticky data]]
  (case sticky
    :cursor-space
    (let [space-loc (if (= 0 (last path))
                      (-> (nav/get-loc zipper (drop-last path))
                          (z/down))
                      (-> (nav/get-loc zipper (-> (vec path)
                                                  (update (dec (count path)) dec)
                                                  (seq)))
                          (z/right)))]
      (resolve-offset data (z/node space-loc)))
    (let [loc (nav/get-loc zipper path)
          node (z/node loc)]
      (assoc (case sticky
               :outer-right (range/bounds node :right)
               :outer-left (range/bounds node :left)
               :inner-right (-> (range/inner-range node)
                                (range/bounds :right))
               :inner-left (-> (range/inner-range node)
                               (range/bounds :left))
               :terminal-offset (resolve-offset data node))
        :node node))))