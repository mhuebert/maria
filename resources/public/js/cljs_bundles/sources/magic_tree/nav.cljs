(ns magic-tree.nav
  (:refer-clojure :exclude [range])
  (:require [fast-zip.core :as z]
            [magic-tree.node :as n]
            [magic-tree.range :as range]))

(defn child-locs [loc]
  (take-while identity (iterate z/right (z/down loc))))
(defn right-locs [loc]
  (take-while identity (iterate z/right (z/right loc))))
(defn left-locs [loc]
  (take-while identity (iterate z/left (z/left loc))))
(defn top-loc [loc]
  (first (filter #(or (= :base (get (z/node %) :tag))
                      (= :base (get (z/node (z/up %)) :tag))) (iterate z/up loc))))

(defn node-at [ast pos]
  (cond (= (type ast) z/ZipperLocation)
        (let [loc ast
              {:keys [value] :as node} (z/node loc)
              found (when (range/within? node pos)
                      (if
                        (or (n/terminal-node? node) (not (seq value)))
                        loc
                        (or
                          (some-> (filter #(range/within? % pos) (child-locs loc))
                                  first
                                  (node-at pos))
                          ;; do we want to avoid 'base'?
                          loc #_(when-not (= :base (get node :tag))
                                  loc))))]
          (if (let [found-node (some-> found z/node)]
                (and (= (get pos :line) (get found-node :end-line))
                     (= (get pos :column) (get found-node :end-column))))
            (or (z/right found) found)
            found))
        (map? ast) (when (range/within? ast pos)
                     (if
                       (or (n/terminal-node? ast) (not (seq (get ast :value))))
                       ast
                       (or (some-> (filter #(range/within? % pos) (get ast :value))
                                   first
                                   (node-at pos))
                           (when-not (= :base (get ast :tag))
                             ast))))
        :else (throw (js/Error "Invalid argument passed to `node-at`"))))

(defn mouse-eval-region
  "Select sexp under the mouse. Whitespace defers to parent."
  [loc]
  (or (and (n/sexp? (z/node loc)) loc)
      (z/up loc)))

(defn nearest-bracket-region
  "Highlight brackets for specified sexp, or nearest sexp to the left, or parent."
  [loc]
  (or (->> (cons loc (left-locs loc))
           (filter (comp #(or (n/sexp? %)
                              (= :uneval (get % :tag))) z/node))
           first)
      (z/up loc)))