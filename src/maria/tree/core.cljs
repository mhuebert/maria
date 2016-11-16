(ns maria.tree.core
  (:require [maria.tree.parse :as parse]
            [maria.tree.unwrap :as unwrap]
            [cljs.test :refer-macros [is are]]))

(def ast parse/ast)
(def string unwrap/string)
(def sexp unwrap/sexp)

(defn comment? [node] (#{:uneval :comment} (get node :tag)))
(defn whitespace? [node] (#{:space :newline :comma} (get node :tag)))
(defn solid? [node]
  (boolean (#{:string :token :regex :var :keyword :space :newline :comma :comment} (get node :tag))))
(def container? (complement solid?))

(def log (atom []))

(defn within? [target-row target-col {:keys [tag row col end-row end-col] :as node}]
  ;{:pre [(> c1 0) (> r1 0)]}
  (let [in? (and (>= target-row row)
                 (<= target-row end-row)
                 (>= target-col col)
                 (< target-col end-col))]
    (swap! log conj {:within? [[target-row target-col] [row col end-row end-col]]
                     :in?     in?
                     :solid?  (solid? node)
                     :tag     (get node :tag)
                     :sexp    (sexp node)})
    in?))

(defn node-at [{:keys [value] :as node} row col]
  (when (within? row col node)
    (if (and (not= (get node :tag) :base)
             (or (solid? node) (not (seq value))))
      node
      (or (some-> (filter (partial within? row col) value)
                  first
                  (node-at row col))
          node))))

;are [sample result]
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
  (let [result-node (node-at (ast sample-str) row col)]
    (is (= (sexp result-node) result-sexp))
    (is (= (string result-node) result-string))))
