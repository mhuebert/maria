(ns maria.module-1
  (:require
   [maria.messages :refer [what-is]]))
;; pull in `what-is` from maria.messages
;; pull in clojure.repl/doc

;; Hi. Those two semicolons to my left mean I'm a comment, so I don't
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
;; this expression is enclosed by parentheses: `( )`. This makes the
;; expression a function. The function name is the first thing inside
;; the parentheses. After the function name go the values we want to
;; give to the function. Those values are called the parameters. Here,
;; we're evaluating the function `circle` with a radius of `20`.

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

;; Now let's draw a bunch of circles, each one a different size. We
;; can list the sizes we want in what's called a "vector", which looks
;; like this:

[2 4 8 16 32 64 128]

;; Vectors start and end with square brackets `[ ]` and can have
;; anything in them. Mostly they're used to hold values, like the
;; sizes our circles will be. The vector itself is also a value, which
;; means it can be a parameter to a function.

;; To make a circle for each size we have in our vector, we use the
;; `map` function. It applies a function to each value in a
;; collection. So in our case, we "map" the function `circle` across a
;; vector of sizes:

(map circle [2 4 8 16 32 64 128])

;; Great! We applied `circle` to 2, and then to 4, and then to 8, and
;; then to 16, and so on. (Notice that the evaluation is contained in
;; parentheses. That's because `map` returns a list, and in Clojure,
;; lists go inside parentheses.)

;; That's nice, but what if we wanted to make all those circles some
;; color? Like this:

(colorize "red" (circle 50))

;; ...but with a different size for each circle.

;; To do that, we need to write our own function that combines
;; `(colorize "red" ...)` and `(circle ...)`. So let's do it!

;; We can create our own functions with the `fn` function. It's the
;; function function!

;; `fn`'s first parameter is a vector of all the parameters the
;; function you create will accept. We want our function to be like
;; `circle`, so it will take one parameter: the radius. So the outline
;; of our function should look like this:

(fn [radius] ...)

;; Putting `radius` in the parameter list means that we can type
;; `radius` anywhere in our function to use the value of whatever
;; input we get. This lets us refer to values that we don't know
;; yet--like `x` is used in algebra.

;; Fill in that outline so that your function will return a
;; circle of size `radius` in your favorite color.

;; A word of warning: your function will evaluate to something weird,
;; like "#object[Function" blah blah blah. Don't worry, that's
;; normal. That's a function definition.

(fn [radius] ...)

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

;; Super! Evaluating the function itself isn't so
;; helpful ("#object[Function"), but we can use our function like we
;; use other functions to do things. All we have to do is put our
;; function as the first thing inside a set of parentheses, like this:

((fn [radius] (colorize "darkcyan" (circle radius))) 50)

;; Awesome. We can also apply our new function to those circles from
;; before:

(map (fn [radius] (colorize "cyan" (circle radius)))
     [2 4 8 16 32 64 128])

;; Lovely!

;; By this point you've evaluated functions,
;; used vectors, and mapped functions across values...phew! But
;; the most important milestone is that you've created a programmer's best friend: your own
;; function. You're well on your way.
