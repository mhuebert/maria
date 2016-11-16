(ns maria.module-1
  (:require
   [maria.messages :refer [what-is]]))
;; pull in `what-is` from maria.messages
;; pull in clojure.repl/doc

;; Hi. Those two semicolons to my left mean I'm a comment--I don't
;; evaluate to anything. Everything else in Clojure evaluates to
;; something.

"duck"

;; Put your cursor to the right of "duck", after the quotation
;; mark. Press command-enter. Bam. You just evaluated a duck.

;; (introduce evaluation, the tool, maybe terminology. minimal data
;; types.)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; possibilities
1
2/3
99999999.3 ;; if we do three numbers in a row, students will be bored to tears
["duck" "hamster" "whale"] ;; god, vectors are boring if presented without context
""
"Aller Anfang ist schwer."
"😀💥✨💖"
"漢字" ;; kanji
["h" "hi" "hip" "hipp" "hippo" "hippop" "hippopo" "hippopot" "hippopota" "hippopotam" "hippopotamu" "hippopotamus"]
[]
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; (introduce functions: asking the computer questions)
(= "mouse" "rat")

;; so that's `false`. what is `false`? 

(= "duck" "duck") ;; and that's `true`

;; are these strings, like "duck"?

(= true "true")

(= false "false")

;; no...okay...

;; (we walk them through true/false/string by introducing `what-is`)

(what-is what-is)  ;; maybe it goes here?
(what-is true)
(what-is false)
(what-is "true")
(what-is what-is) ;; ...or maybe it goes here?
(what-is =)
(what-is (what-is =)) ;; whoa

;; less likely explorations that I'm keeping here as mental kibble:
(what-is "😀💥✨💖")
(what-is "love") ;; 🎵 baby don't hurt me 🎤 
(what-is \d)
;; //less likely explorations


;;;; transition to inspecting vectors (maybe we introduced them as a
;;;; data type already) while holding on to the thread of asking
;;;; Clojure, our dear colleague, questions (via functions)

["crawl" "walk" "run"]

(first ["crawl" "walk" "run"])

(rest ["crawl" "walk" "run"])

(second ["crawl" "walk" "run"]) ;; (this...

(last ["crawl" "walk" "run"])

(nth ["crawl" "walk" "run"] 0) ;; ...and this make me think the example should be >3 elements)

(count ["crawl" "walk" "run"]) ;; (this is probably overkill; don't try give them all the tools at once)

;; (or apply those functions to some other vector that's representative, interesting, and not boring)

["pants" "socks" "shoes" "coat" "backpack"] ;; pants are so andronormative

["pick apples" "wash apples" "chop apples" "bake apple pie"] ;; so ethnocentric, you American

["sign up for ClojureBridge" "have a lovely time talking to Clojure" "build awesome things with Clojure"]

;; (regardless of which vector we explore, we want to introduce
;; functions, vectors, and inspecting things)

;;;; (introduce `doc`)

(doc first)

(doc last) ;; etc.

;;;; (I'd like to introduce `def` soon, and let them create their own
;;;; functions, but ideally I want to present them with a *PROBLEM* or
;;;; *QUESTION* that def'ing and functions solve.)

(def my-pet "Deutscher Schäferhund") ;; XXX this example is dry; could use some outside perspective

my-pet

(= my-pet "Deutscher Schäferhund")

;; (maybe run off with the `pet` idea a bit? so we can explore/introduce other ideas)
(conj [my-pet] "chihuahua")

my-pet ;; still just the Schaferhund

;;;; (more here)

;;;; (transition)

;;;; (introduce fn)

(fn [x] (inc x)) ;; this is so boring I'm in tears, please add emoji or SOMETHING
