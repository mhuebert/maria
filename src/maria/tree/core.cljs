(ns maria.tree.core
  (:require [maria.tree.parse :as parse]
            [maria.tree.unwrap :as unwrap]
            [cljs.test :refer-macros [is are]]))

(def ast parse/ast)
(def string unwrap/string)
(def sexp unwrap/sexp)

(defn comment? [n] (#{:uneval :comment} (get n :tag)))
(defn whitespace? [n] (#{:space :newline :comma} (get n :tag)))
(defn solid? [n]
  (boolean (#{:string :token :regex :var :keyword :space :newline :comma :comment} (get n :tag))))
(def container? (complement solid?))

(def log (atom []))

(defn within? [r1 c1 {:keys [tag row col end-row end-col] :as n}]
  ;{:pre [(> c1 0) (> r1 0)]}
  (let [in? (and (>= r1 row)
                 (<= r1 end-row)
                 (>= c1 col)
                 (< c1 end-col))]
    (swap! log conj {:within? [[r1 c1] [row col end-row end-col]]
                     :in?     in?
                     :solid?  (solid? n)
                     :tag     (get n :tag)
                     :sexp    (sexp n)})
    in?))

(defn node-at [{:keys [value] :as n} r c]
  (when (within? r c n)
    (if (and (not= (get n :tag) :base)
             (or (solid? n) (not (seq value))))
      n
      (or (some-> (filter (partial within? r c) value)
                  first
                  (node-at r c))
          n))))

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
