(ns paredit.edit-test
  (:require [magic-tree-codemirror.addons]
            [magic-tree-codemirror.edit :as edit]
            [magic-tree.test-utils :refer [test-exec]]
            [cljs.test :refer-macros [deftest is are]]))

(deftest edit-commands
  (are [cmd source post-source]
    (= (test-exec cmd source) post-source)

    edit/kill "(prn 1 |2 3)" "(prn 1 |)"
    edit/kill "[1 2 |'a b c' 3 4]" "[1 2 |]"
    edit/kill "[1 2 '|a b c']" "[1 2 '|']"

    edit/cut-form "|(+ 1)" "|"
    edit/cut-form "(|+ 1)" "(| 1)"

    edit/hop-left "( )|" "|( )"
    edit/hop-left "( |)" "(| )"
    edit/hop-left "( a|)" "( |a)"
    edit/hop-left "( ab|c)" "( |abc)"
    edit/hop-left "((|))" "(|())"

    edit/comment-line "abc|\ndef" ";;abc\ndef|"
    edit/comment-line "abc\n|def" "abc\n;;def|"
    edit/comment-line "abc|" ";;abc|"

    edit/uneval-form "|[]" "#_|[]"
    edit/uneval-top-level-form "[[|]]" "#_[[|]]"
    edit/uneval-top-level-form "[\n[|]]" "#_[\n[|]]"))


