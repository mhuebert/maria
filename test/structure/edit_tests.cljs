(ns structure.edit-tests
  (:require [structure.codemirror]
            [structure.edit :as edit]
            [structure.test-utils :as utils :refer [test-exec]]
            [cljs.test :refer-macros [deftest is are testing]]))

(defn prn-ret-cm [cm]
  (prn (.getValue cm))
  (.log js/console (count (.listSelections cm)))
  cm)

(test-exec edit/expand-selection-left "(<a>)")

(deftest edit-commands

  (testing "round-trip selections"

    (doseq [val ["|a"
                 "|a|"
                 "a|b"
                 "<a>"
                 "a<b>c"
                 "a<bc>de<f>h"]]
      (is (= val (-> (utils/editor)
                     (doto (.setValue val))
                     (utils/deserialize-selections)
                     (utils/serialize-selections)
                     (.getValue)))))

    )

  (are [cmd source post-source]
    (= (test-exec cmd source) post-source)

    edit/kill! "(prn 1 |2 3)" "(prn 1 |)"
    edit/kill! "[1 2 |'a b c' 3 4]" "[1 2 |]"
    edit/kill! "[1 2 '|a b c']" "[1 2 '|']"

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

    edit/uneval! "|[]" "|#_[]"
    edit/uneval! "[]|" "#_[]|"
    edit/uneval! "a|bcd" "#_abcd|"

    edit/unwrap! "[|]" " | "
    edit/unwrap! "[| ]" " |  "
    edit/unwrap! " [|]" "  | "
    edit/unwrap! "[ |]" "  | "
    edit/unwrap! "[ \"abc|\" ]" "[  abc|  ]"

    edit/expand-selection "(|a)" "(<a>)"
    edit/expand-selection "(a|b)" "(<ab>)"
    edit/expand-selection "(<a > )" "(<a  >)"
    edit/expand-selection "(<a b>)" "<(a b)>"

    edit/expand-selection-left "(<a>)" "<(a)>"
    edit/expand-selection-left "(b <a>)" "(<b a>)"
    edit/expand-selection-left "c (<b a>)" "c <(b a)>"
    edit/expand-selection-left "c <(b a)>" "<c (b a)>"

    ))

(comment edit/uneval-top-level-form "[[|]]" "#_[[|]]"
         edit/uneval-top-level-form "[\n[|]]" "#_[\n[|]]")
