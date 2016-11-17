(ns maria.tree.core
  (:require [maria.tree.parse :as parse]
            [maria.tree.emit :as unwrap]
            [cljs.test :refer-macros [is are]]))

(def ast parse/ast)
(def string unwrap/string)
(def sexp unwrap/sexp)

(defn comment? [node] (#{:uneval :comment} (get node :tag)))
(defn whitespace? [node] (#{:space :newline :comma} (get node :tag)))
(def sexp? (every-pred (complement comment?)
                       (complement whitespace?)))

(defn terminal-node? [node]
  (boolean (#{:string :token :regex :var :keyword :space :newline :comma :comment} (get node :tag))))

(def can-have-children? (complement terminal-node?))

(defn edge-ranges [node]
  (when (can-have-children? node)
    (let [[left right] (get unwrap/edges (get node :tag))]
      (cond-> []
              left (conj {:row     (:row node) :end-row (:row node)
                          :col     (dec (:col node))
                          :end-col (dec (+ (:col node) (count left)))})
              right (conj {:row     (:end-row node) :end-row (:end-row node)
                           :col     (- (:end-col node) (count right))
                           :end-col (:end-col node)})))))

(def log (atom []))

(defn within? [{r :row c :col} {:keys [row col end-row end-col]}]
  (and (>= r row)
       (<= r end-row)
       (if (= r row) (>= c col) true)
       (if (= r end-row) (<= c end-col) true)))

(defn node-at [{:keys [value] :as node} pos]
  (when (within? pos node)
    (if
      (or (terminal-node? node) (not (seq value)))
      node
      (or (some-> (filter (partial within? pos) value)
                  first
                  (node-at pos))
          (when-not (= :base (get node :tag))
            node)))))

(comment

  (assert (within? {:row 1 :col 1}
                   {:row     1 :col 1
                    :end-row 1 :end-col 2}))

  (doseq [[sample-str [row col] result-sexp result-string] [["1" [1 1] 1 "1"]
                                                            ["[1]" [1 1] [1] "[1]"]
                                                            ["#{}" [1 1] #{} "#{}"]
                                                            ["\"\"" [1 1] "" "\"\""]
                                                            ["(+ 1)" [1 0] nil nil]
                                                            ["(+ 1)" [1 1] '(+ 1) "(+ 1)"]
                                                            ["(+ 1)" [1 2] '+ "+"]
                                                            ["(+ 1)" [1 3] nil " "]
                                                            ["(+ 1)" [1 4] 1 "1"]
                                                            ["(+ 1)" [1 5] '(+ 1) "(+ 1)"]
                                                            ["(+ 1)" [1 6] nil nil]
                                                            ["\n1" [2 1] 1 "1"]]]
    (reset! log [])
    (let [result-node (node-at (ast sample-str) {:row row
                                                 :col col})]
      (is (= (sexp result-node) result-sexp))
      (is (= (string result-node) result-string)))))