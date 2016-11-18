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

;; (introduce evaluation, the tool, maybe terminology. say nothing about data types.)

;; (introduce functions: asking the computer questions)

;; ("so, like, what are these things we're evaluating? Clojure will tell us. Clojure is helpful. Clojure is your friend. Clojure would never hurt you. Clojure just wants to be loved.")

;; we ask clojure questions like this:

(what-is "duck")

;; wait, what did we just do?

(what-is what-is)

;; we just asked Clojure a question by evaluating a function.

;; we gave it "duck" -- which is apparently a "string". what does Clojure give us back?

(what-is (what-is "duck"))  ;; (have them eval both inner and outer form)

;; we gave a function a string, and it returned a string.

;; --> a string is a "string" of characters (letters)

(first "duck")

;; weird.

(what-is (first "duck"))

;; so that's a character. hey, wait, that `first` thing. before evaluating anything, what do YOU think `first` is?

;; ok, now ask Clojure:
(what-is first)

(comment
  ;; XXX maybe an aside?
  first ;; #function[clojure.core/first--4339]
  )

;; (anyway, back to our duck.)

;; lots of things can be characters, and therefore strings. before evaluating, make a guess for what you think Clojure will tell you.

"漢字"

(what-is "漢字")

(first "漢字")

(what-is (first "漢字"))

(what-is "😀💥✨💖")

(what-is (first "😀💥✨💖")) ;; XXX eval'ing inner form may not return prettily; if so then nix this

;; so, functions are how we ask the computer questions.

;; we asked `first` of "😀💥✨💖" -- what if we want to ask about the 💥 or 💖 in "😀💥✨💖" ? how do you guess we could get to those characters in our string?

;; to get 💥  from "😀💥✨💖" ?
...

;; to get 💖 from "😀💥✨💖" ?
...

;; XXX (these are very leading questions--which might be unavoidable at this stage. but generally one wants to avoid asking questions for which there is only one answer available for the student to guess)

;; (maybe then show the general case with `nth`? that could also be stultifying)

;; --------------
;; XXX (possible exercise that might be too open-ended)
;; what makes a string a string? try to create a string that the computer will recognize. (replace the ellipsis with your string)

...

;; and how would you check if your string is a string? (replace the ellipsis with how you'd ask)

...

;; so a string is "characters" that go between double quotes. let's push those boundaries. is there anyhing you could put between two double quotes that wouldn't make a string? try to break strings. brainstorm with your partner. try to confuse the computer.

...
...
...

;; XXX (that exercise might be too open-ended)
;; --------------




;; FIXME TODO (drawing shapes goes here)
(circle 20)

(colorize "blue" (circle 20))

(colorize "yellow" (rectangle 500))




;; FIXME TODO (introduce example that lends itself to (let) -- doing multiple things? doing a single thing after building "state", like a poor man's threading?)
;; (the bindings vector is OK to use for a short while before formally introducing vectors, because it's just The Way Things Are Done...and then we introduce vectors by reminding them they already know it.)





;; (transition to new example--string, number, vector, whatever. maybe something intentionally unwieldy.)

;; let's pretend we're the red cross, and we need to get supplies to some refugee camps
"38.4225° N, 27.1438° E"
"37.0099° N, 37.7969° E"
"36.6463° N, 37.0825° E"

;; ok, but that's three separate strings. In Clojure, we do that like this:

["38.4225° N, 27.1438° E"
 "37.0099° N, 37.7969° E"
 "36.6463° N, 37.0825° E"]

;; what is that? ask the computer.

...

;; vectors are a(n indexed) collection of values. the same functions we used with strings work here:

(first ["38.4225° N, 27.1438° E"
        "37.0099° N, 37.7969° E"
        "36.6463° N, 37.0825° E"])

(second ["38.4225° N, 27.1438° E"
         "37.0099° N, 37.7969° E"
         "36.6463° N, 37.0825° E"])

(last ["38.4225° N, 27.1438° E"
       "37.0099° N, 37.7969° E"
       "36.6463° N, 37.0825° E"])

;; man, it's annoying to keep copying that whole vector around. couldn't we--

(def camps ["38.4225° N, 27.1438° E"
            "37.0099° N, 37.7969° E"
            "36.6463° N, 37.0825° E"])

;; 🎉🎉🎉


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

