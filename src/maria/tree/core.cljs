(ns maria.tree.core
  (:require [maria.tree.parse :as parse]
            [maria.tree.emit :as unwrap]
            [maria.tree.node :as n]
            [fast-zip.core :as z]
            [cljs.test :refer-macros [is are]]))

(def ast parse/ast)
(defn ast-zip [ast]
  (z/zipper
    n/can-have-children?
    :value
    (fn [node children] (assoc node :value children))
    ast))
(def string-zip (comp ast-zip parse/ast))
(def string unwrap/string)
(def sexp unwrap/sexp)

(def comment? n/comment?)
(def whitespace? n/whitespace?)
(def newline? n/newline?)
(def sexp? n/sexp?)
(def can-have-children? n/can-have-children?)
(def terminal-node? n/terminal-node?)
(def edge-ranges n/edge-ranges)

(def log (atom []))

(defn child-locs [loc]
  (take-while identity (iterate z/right (z/down loc))))
(defn right-locs [loc]
  (take-while identity (iterate z/right (z/right loc))))
(defn left-locs [loc]
  (take-while identity (iterate z/left (z/left loc))))

(defn get-pos [node pos]
  (condp = (type node)
    z/ZipperLocation
    (let [loc node
          node (z/node loc)
          found (when (n/within? pos node)
                  (if
                    (or (terminal-node? node) (not (seq (get node :value))))
                    loc
                    (or
                      (some-> (filter (partial n/within? pos) (child-locs loc))
                              first
                              (get-pos pos))
                      (when-not (= :base (get node :tag))
                        loc))))]
      (if (let [found-node (some-> found z/node)]
            (and (whitespace? found-node)
                 (= (get pos :row) (get found-node :end-row))
                 (= (get pos :col) (get found-node :end-col))))
        (or (z/right found) found)
        found))

    PersistentArrayMap
    (when (n/within? pos node)
      (if
        (or (terminal-node? node) (not (seq (get node :value))))
        node
        (or (some-> (filter (partial n/within? pos) (get node :value))
                    first
                    (get-pos pos))
            (when-not (= :base (get node :tag))
              node))))))

(defn mouse-eval-region
  "Select sexp under the mouse. Whitespace defers to parent."
  [loc]
  (or (and (sexp? (z/node loc)) loc)
      (z/up loc)))

(defn nearest-bracket-region
  "Highlight brackets for specified sexp, or nearest sexp to the left, or parent."
  [loc]
  (or (->> (cons loc (left-locs loc))
           (filter (comp sexp? z/node))
           first)
      (z/up loc)))

(comment

  (assert (n/within? {:row 1 :col 1}
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
    (let [result-node (get-pos (ast sample-str) {:row row
                                                 :col col})]
      (is (= (sexp result-node) result-sexp))
      (is (= (string result-node) result-string)))))

(comment
  (let [sample-code-string ""]
    (let [_ (.profile js/console "parse-ast")
          ast (time (parse/ast sample-code-string))
          _ (.profileEnd js/console)]
      (println :cljs-core-string-verify (= (unwrap/string ast) sample-code-string)))))