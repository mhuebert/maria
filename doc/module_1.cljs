(ns maria.module-1
  (:require
   [maria.messages :refer [what-is]]))
;; pull in `what-is` from maria.messages
;; pull in clojure.repl/doc

;; Hi. Those two semicolons to my left mean I'm a comment--I don't
;; evaluate to anything. In Clojure, the programming language we're
;; using today, everything that's not a comment evaluates to
;; something.

"duck"

;; Put your cursor to the right of "duck", after the quotation
;; mark. Press `command-enter`. Bam. You just had Clojure evaluate a
;; duck. In this case, this duck evaluates to "duck".

;; The "duck" we evaluated is an *expression*. The evaluation of our
;; "duck" expression appeared in the output panel on the right. That's
;; how we're going to work: type and evaluate expressions on the left;
;; evaluation shows up on the right.

;; What else can we evaluate? Well, Clojure can help us draw.

(circle 20)

;; This time, Clojure evaluated a circle for us. You might notice that
;; this expression is enclosed by parentheses--`( )`. This makes the
;; expression a function. The function name is the first thing inside
;; the parentheses. After the function name go the values we want to
;; give to the function. Those values are called the parameters. Here,
;; we're evaluating the function `circle` a radius parameter of `20`.

;; XXX we need arglists hints so I can explain them here

;; How do you think you write an expression that draws a bigger
;; circle? Type your expression in after this comment, where we put
;; the ellipsis `...`. XXX Remember to check the arg list.

...

;; What do you think happens if you give `circle` too many parameters?
;; Let's try:

(circle 20 20)

;; That's an error message. If we write code that Clojure can't
;; understand, it will tell us it can't run it. If it can, Clojure
;; will try to tell you how to fix your code. Don't worry if you write
;; code that gives an error message. It happens to all of us.

;; What do you think this next expression will draw?

(colorize "blue" (rectangle 200 50))

;; We colorized our circle by passing it to another function. Notice
;; how the `rectangle` function is nested inside the `colorize`
;; function?

;;;;

;; Now let's draw a bunch of circles, each one a different size. We can list the sizes we want in what's called a "vector", which looks like this:

[2 4 8 16 32 64 128]

;; Vectors start and end with square brackets `[ ]` and can have
;; anything in them. Mostly they're used to hold values, like the
;; sizes our circles will be. The vector itself is also a value, which
;; means it can be a parameter to a function.

;; To make a circle for each size we have in our vector, we use the
;; `mapv` function. What it does is take a function and a collection
;; of values, and applies the function to each of those values. So in
;; our case, we give the function `mapv` the `circle` function and our
;; vector of sizes:

(mapv circle [2 4 8 16 32 64 128])

;; Great! We applied `circle` to 2, and then to 4, and then to 8, and
;; then to 16, and so on. What did we get from evaluating it? A
;; vector! A vector with a circle of each size we asked for.

;; That's nice, but what if we wanted to make all those circles some
;; color? Like this:

(colorize "red" (circle 50))

;; ...but with a different size for each circle.

;; To do that, we need to write our own function that combines
;; `(colorize "red" ...)` and `(circle ...)`. So let's do it!

;; We can create our own functions with the `fn` function. It's the
;; function function!

;; The `fn` function's first parameter is a vector of all the
;; parameters the function you create will accept. We want our function to accept the same parameters as `circle`: just one, the radius. So the outline of our function should look like this:

(fn [radius] ...)

;; Try to fill in that outline so that your function will return a
;; circle of size `radius` in your favorite color.

...

;; Don't peek.

;;
;;
;;
;;
;;
;;
;;
;;
;;
;;
;;
;;
;;
;;
;;
;;
;;
;;
;;
;;
;;
;;
;;

;; Here's what I got:

(fn [radius] (colorize "blue" (circle radius)))

;; I'll warn you, evaluating that function gives me something
;; weird. "#object[Function" blah blah blah. Don't worry too much
;; about it, except to know that `fn` evaluates to a function
;; definition. The evaluation of the function itself isn't so helpful,
;; but when we call it with a parameter, we get what we're looking
;; for:

((fn [radius] (colorize "mauve" (circle radius))) 50)

;; Awesome. Let's apply our new function to our circles:

(mapv (fn [radius] (colorize "cyan" (circle radius)))
      [2 4 8 16 32 64 128])

;; Lovely!



;;;;; XXX





;; Let's draw a bunch of shapes side-by-side using the `line-up` function:

(line-up (circle 25)
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
