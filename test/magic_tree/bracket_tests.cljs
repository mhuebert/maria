(ns magic-tree.bracket-tests
  (:require [magic-tree.core :as tree]
            [cljs.test :refer-macros [deftest testing is are run-tests]]))

(defn round-trip [source]
  (-> source
      (tree/ast)
      (tree/string)))

(deftest close-brackets

  (testing "brackets auto-close"
    (are [before after]
      (= after (round-trip before))
      "(" "()"
      "[" "[]"
      "{" "{}"
      "#{" "#{}"
      "'(" "'()"))

  (testing "brackets auto-wrap"
    (are [before after]
      (= after (round-trip before))
      "[[]" "[[]]"
      "(1" "(1)"))

  (testing "Extra brackets at the end are removed"
    (are [before after]
      (= after (round-trip before))
      "()]" "()"))



  )