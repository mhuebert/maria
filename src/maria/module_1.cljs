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

;; Our duck evaluation (which is also "duck") appeared in the output
;; panel on the right. That's how we're going to work: type and
;; evaluate code on the left; output displays on the right.

;; We call "duck" an `expression`. What happened when you pressed
;; `command-enter` was that Clojure, the programming language,
;; returned to you the `value` that the expression `evaluates` to. In
;; this case, "duck" evaluates to "duck".

;; What else can we evaluate? Well, Clojure can help us draw.

(circle 20)

;; Clojure evaluated a circle for us. This time, our expression calls
;; a function. To call a function, we create a pair of
;; parenteses--`()`--and put the function name inside, followed by all
;; the expressions we want to give to the function (the
;; "parameters"). Here, `circle` is the function we call, and we give
;; it a radius parameter of `20`.

;; XXX we need arglists hints so I can explain them here

;; How do you think you could make that circle bigger? Type your code
;; in where we put the ellipsis `...`. XXX Remember to check the arg
;; list.

...

;; What do you think happens if you give `circle` too many parameters?
;; Let's try:

(circle 20 20)

;; That's an error message. If we write code that Clojure can't
;; understand, it will tell us it can't run it. If it can, Clojure
;; will try to tell you how to fix your code.

;; What do you think this next expression will draw?

(colorize "blue" (rectangle 200 50))

;; We colorized our circle by passing it to another function. Notice
;; how the `rectangle` function is nested inside the `colorize`
;; function?

;; Let's draw a bunch of shapes side-by-side using the `line-up` function:

(line-up (circle 25)
         (circle 25)
         (circle 25)
         (circle 25)
         (circle 25)
         (circle 25)
         (circle 25)
         (circle 25)
         (circle 25))

;; Man, that was a lot of typing. And so repetitive! When I see myself
;; repeating the same code over and over, I get suspicious.

;; Luckily, we can code ourselves a shorter solution. We don't have to
;; type `(circle 25)` over and over. We can type it once and Clojure
;; will remember it for us:

(let [c (circle 25)]
  ;; Now we can type just `c`:
  (line-up c c c c c c c c c))

;; We used `let` to define a name `c` for our expression. The `let`
;; function's first parameter is a pair of square brackets (`[]`) with
;; pairs of names and values inside.

;; We can have multiple pairs if we want:

(let [c (circle 25)
      r (rectangle 50 50)]
  (line-up c r c r c r c r c r))

;; Can you change that expression to make all the circles one color
;; and the squares another?

...

;; Nice. Now let's say we wanted to draw a checkerboard: a big square
;; made up of lots of little squares, alternating colors. Let's start
;; with our smallest unit: a single square. We'll build our
;; checkerboard code step by step, so that it gradually grows into the
;; final shape we want.

(square 50) ;; ERROR

;; Oops. Clojure knows rectangles, but apparently not squares.

(rectangle 50 50)

;; We add a dash of color...

(colorize "red" (rectangle 50 50))

(colorize "black" (rectangle 50 50))

;; We can put those together with `let` to make a 2x2 checker. That
;; will be our building block.

(let [r (colorize "red" (rectangle 50 50))
      b (colorize "black" (rectangle 50 50))]
  (stack (line-up r b)
         (line-up b r)))

;; OK, now let's repeat that checker until it forms a row. We'll do
;; that with the `repeat` function, which just calls a function as
;; many times as it's told.

(let [r (colorize "red" (rectangle 50 50)) ;; same as above
      b (colorize "black" (rectangle 50 50)) ;; same as above
      checker (stack (line-up r b)
                     (line-up b r))]
  (apply line-up (repeat 4 checker)))
;; XXX XXX XXX apply is waaaaay too advanced!

;; ...and we `repeat` the rows to make a complete board. (Again, we
;; repeat our `r`, `b`, and `checker` definitions in our `let`.)

(let [r (colorize "red" (rectangle 50 50))
      b (colorize "black" (rectangle 50 50))
      checker (stack (line-up r b)
                     (line-up b r)) 
      row (apply line-up (repeat 4 checker))]
  (apply stack (repeat 4 row)))
;; XXX XXX XXX apply is waaaaay too advanced!

;; Cool!

;; Now, what if we wanted to play with that checkerboard? To draw five
;; of them across the screen, or to show it to friends later? Our
;; `(let)` can only do one thing at a time, and is getting too long
;; anyway. We're still repeating ourselves too much!

;; What we need is to create a name for our checkerboard and use it
;; anywhere we want--not just inside a `let`. For that, we use `def`:

(def checkerboard
  (let [r (colorize "red" (rectangle 25 25))
        b (colorize "black" (rectangle 25 25))
        checker (stack (line-up r b)
                       (line-up b r)) 
        row (apply line-up (repeat 4 checker))]
    (stack (repeat 4 row))))

checkerboard

;; Look what we can do now!

(stack (repeat 5 checkerboard))

(line-up (repeat 50 checkerboard))

;; XXX transition/more

;; XXX introduce `fn` with building `rainbow`, not `square`. one of them is literally square.
;; XXX
;; XXX
;; XXX
;; XXX see comment in users.cljs -- mixed types, gradual build
;; XXX
;; XXX
;; XXX
;; XXX
