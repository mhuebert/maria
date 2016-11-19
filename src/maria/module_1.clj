(ns maria.module-1
  (:require
   [maria.messages :refer [what-is]]))
;; pull in `what-is` from maria.messages
;; pull in clojure.repl/doc

;; Hi. Those two semicolons to my left mean I'm a comment--I don't
;; evaluate to anything. In Clojure, everything that's not a comment
;; evaluates to something.

"duck"

;; Put your cursor to the right of "duck", after the quotation
;; mark. Press `command-enter`. Bam. You just evaluated a duck.

;; We call "duck" an `expression`. What happened when you pressed
;; `command-enter` was that Clojure, the programming language,
;; returned to you the `value` that the expression `evaluates` to.

;; In this case, "duck" evaluates to "duck". Why? What does "duck", like, mean, dude? (XXX perhaps too much idiom for nonnative speakers)

;; Clojure will tell us what "duck" is. Clojure is helpful. Clojure is
;; your friend. Clojure would never hurt you. Clojure just wants to be
;; loved.

;; We ask clojure questions like this. Go ahead, evaluate it. (Put
;; your cursor at the end of the line and press `command-enter`.)

(what-is "duck")

;; Wait--what did we just evaluate?

(what-is what-is)

;; we just asked Clojure a question by evaluating a function. to call a function, we write the function name and then the expressions we want to pass to the function (the "arguments" or "parameters"), and we wrap the whole thing up nice and tidy with parentheses.

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

(doc function)

;; XXX need jump to definition
;; XXX need to see arglist
;; XXX need to finish explanation of both of those as part of examining the innards of functions

;; Functions are usually questions, but they can also be
;; orders. Clojure will do stuff for us, if we ask nicely. Clojure
;; doesn't want "please" and "thank you", though. It just wants
;; parentheses.

;; Let's ask Clojure to draw for us. Clojure is a great art buddy.

(circle 20)

;; How do you think you could make that circle bigger? XXX Remember to check the arg list.

;; While we're here, let's try giving `circle` the wrong number of
;; arguments, just to see what happens:

(circle 20 20) ;; (draws the circle but warns about wrong arity)

;; What do you think this next expression will draw?

(colorize "blue" (circle 20))

;; Let's say we wanted to draw a square.

(square 5) ;; ERROR

;; Come on, Clojure. Help us out.

(rectangle 25 25)

;; Fine. Let's line up a whole row of squares across the screen.

(line-up (rectangle 25 25) (rectangle 25 25) (rectangle 25 25) (rectangle 25 25) (rectangle 25 25) (rectangle 25 25) (rectangle 25 25) (rectangle 25 25) (rectangle 25 25) (rectangle 25 25)) 
;; XXX it would be nice to have spaces between them (margin/padding)

;; Man, that was a lot of typing. I hate typing.

;; --What's that, Clojure?

;; Clojure says we don't have to type `(rectangle 25 25)` over and
;; over. We can type it once and Clojur will remember it for us:

(let [r (rectangle 25 25)]
  (line-up r r r r r r r r r r))

;; That's nicer.


;; XXX we should do something else before immediately showing `def`, maybe
(def r (rectangle 25 25))

;; We just told Clojure to "define" the letter r as the expression `(rectangle 25 25)` for later. That let's us say:

(line-up (rectangle 25 25) (rectangle 25 25) (rectangle 25 25) (rectangle 25 25))

;; But we still want a SQUARE, don't we? Why do you make us write `25`
;; twice every time we call `rectangle`, Clojure? I thought you were
;; my friend, Clojure. Couldn't you give us a `square` function?

;; Clojure feels bad that it didn't give you a `square` function. But
;; Clojure has an idea. You're a programmer now, and Clojure is here
;; to help you. You and Clojure can MAKE a `square` function:

(def square (fn [side] (rectangle side side)))

;; Once we evaluate that, we can `(square 5)` as often as we please:

(square 5)

(square 5)

(square 5)

(square 5)

;; Awesome. How did we do that?

;; We defined `square` with the `def` function:
(doc def)

;; We defined 


;; If you said 





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
