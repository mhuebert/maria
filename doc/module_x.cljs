(ns maria.module-x
  (:require
   [maria.messages :refer [what-is]]))

g





;;;; XXX transition
;; Why? Have no fear. Clojure will tell us what "duck" is. Clojure is
;; helpful. Clojure is your friend.

;; We ask Clojure questions like this. Go ahead, evaluate it. (Put
;; your cursor at the end of the line and press `command-enter`.)

(what-is "duck")

;; Wait--what did we just evaluate?

(what-is what-is)

;; We just asked Clojure a question by evaluating a function. to call
;; a function, we write the function name and then the expressions we
;; want to pass to the function (the "arguments" or "parameters"), and
;; we wrap the whole thing up nice and tidy with parentheses.

;; so, when we write `(what-is "duck")` we're telling Clojure:
;;  1. we're calling a function (because we're using parentheses)
;;  2. the question (function) we're asking Clojure is `what-is` (because it goes first)
;;  3. we're asking `what-is` on the expression `"duck"`

;; Let's ask Clojure some other questions.

(first "duck")

;; Weird. What is that? --wait! You know how to ask Clojure what a thing is. Go ahead.

(what-is (first "duck"))

;; Woohoo! So the first thing in "duck" is a character. And that `first` thing--what's that? Before evaluating anything, what do YOU think `first` is?

;; ok, now ask Clojure:
(what-is first)

;; So we know `first` is a function. But what does it do? When we want
;; to learn what's inside an expression, usually we evaluate it, or
;; use `first` and similar functions (which we'll explore later). When
;; we want to learn what's inside a function, we use `doc`:

(doc what-is)

(doc doc)

(doc first)

;; XXX need jump to definition
;; XXX need to see arglist
;; XXX need to finish explanation of both of those as part of examining the innards of functions

;; Functions are usually questions, but they can also be
;; orders. Clojure will do stuff for us, if we ask nicely. Clojure
;; doesn't want "please" and "thank you", though. It just wants
;; parentheses.











;; (XXX either transition or culling needed--this was snipped from `what-is` intro)
;; what does Clojure give us back?

(what-is (what-is "duck"))  ;; (have them eval both inner and outer form)

;; we gave a function a string, and it returned a string.

;; --> a string is a "string" of characters (letters)















;; anyway, back to our duck.

"duck"

;; before moving to the next bit, try to answer these questions in your own words. they're not trick questions, it's just looking back at what you've already evaluated above.
;; what do we know about "duck"? (...it's a string, strings are made up of characters)
;; what can we do with "duck"? (...pass it to functions, examine it)

;; lots of things can be characters, and therefore strings. before evaluating, make a guess for what you think Clojure will tell you when you evaluate this expression.

"漢字"

;; and what do you think will Clojure say when you evaluate this?
(what-is "漢字")

;; and this?
(first "漢字")

;; and this?
(what-is (first "漢字"))

;; or what about this?
(what-is "😀💥✨💖")

(comment ;; XXX is this section possible with emoji?
  (what-is (first "😀💥✨💖")) ;; XXX eval'ing inner form may not return prettily; if so then nix this

  ;; great. you and Clojure are working together great. Clojure is your little helper. You and Clojure are going to do great things together.

  ;; we asked `first` of "😀💥✨💖" -- what if we want to ask about the 💥 or 💖 in "😀💥✨💖" ? how do you guess we could get to those characters in our string?

  ;; to get 💥  from "😀💥✨💖" ?
  ...

  ;; to get 💖 from "😀💥✨💖" ?
  ...
  )

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

abc
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



;; for later
;; By the way, (what-is first) is nicer than evaluating a function name on its own:
first ;; #function[clojure.core/first--4339]










;; XXX
;; Suppose we want to make two rows of colored circles, using the
;; function we just wrote? It would be wasteful to write it
;; twice. What we do instead is give it a name, like this:
(let [cyan-circles (fn [radius] (colorize "darkcyan" (circle radius)))]
  (vector (mapv cyan-circles [2 4 8 16 32 64 128])
          (mapv cyan-circles [128 64 32 16 8 4 2])))
;; XXX (I'd like a more elegant way to use the function twice. Perhaps
;; I should just name it when I use it once, and not revisit it to
;; introduce `let`?)

;; XXX introduce `fn` with building `rainbow` or similar--see comment
;; in users.cljs -- mixed types, gradual build


;; TODO introduce
(def rand-circles
  ;; FIXME `colors` doesn't yet work--need to run cljs-live deps?
  (fn [radius] (colorize (rand-nth colors) (circle radius))))

;; ...

(defn rand-circles [radius]
  (colorize (rand-nth colors) (circle radius)))
