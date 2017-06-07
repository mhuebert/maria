;;;; A Quick Introduction to Clojure with Pictures.


;;;; 1. Ready...

;; Hi! This tutorial will introduce you to programming by drawing pictures using the Clojure programming language.

;; This environment we're in is called Maria, after education pioneer Maria Montessori. Working with Maria is the friendliest way we know how to share the elegance of programming Clojure, even if you eventually use some other editor like Emacs or Cursive.


;;;; 2. Set...

;; To draw pictures, we need to evaluate expressions.

;; In Clojure, everything except a comment is an expression. This text you're reading is a comment, bcause of the two semicolons at the start of the line. This is an expression:

"duck"

;; Put your cursor to the same line as the expression "duck", and press `command-enter`. BOOM. You just evaluated a duck. This duck expression is just a value, so it evaluates to itself: "duck". Clojure printed the evaluation of our "duck" expression in the output panel on the right. That's how we're going to work: type and evaluate expressions on the left; evaluation shows up on the right.

;; Try evaluating some other values:

5

18247575


;;;; 2. Go!

;; An expression can also be a function call. To call a function, put an open parenthesis before the function name, then expressions for the function parameters, and then a close parenthesis, like this:

(circle 20)

;; The evaluation of the `circle` function call is a circle. The parameter to `circle` determines the circle's radius, in pixels. How do we find out the parameters to `circle`? Why, we ask Clojure!

(doc circle)

;; The `doc` function returns the documentation for any function. We pass it the parameter `circle`, so it prints `circle`'s documentation for us. The computer is your friend and wants to help you.

;; Try giving circle the wrong number of arguments, just to see what happens:

(circle 20 10)

;; Notice that the result is an error instead of a picture or value. If we write code that Clojure can't understand, it will tell us it can't run it. If it can, Clojure will try to tell you how to fix your code.

;; Don't worry if you create an error. It happens to all of us.


;;;; 3. Drawing

;; As you might guess, there's a `rectangle` function much like `circle`. Let's find out how it works:

(doc rectangle)

;; OK, so it takes two parameters: width and height. Let's take it for a spin.

(rectangle 100 25)

;; That's pretty much what one would expect. But these one-tone shapes bore me. Let's add some color!

(colorize "blue" (rectangle 50 50))

;; That's a nice change of pace. We can also combine shapes with `stack` and `line-up`:

(stack (colorize "red" (rectangle 20 20))
       (colorize "blue" (rectangle 20 20)))

(line-up (colorize "red" (rectangle 20 20))
         (colorize "blue" (rectangle 20 20))
         (colorize "green" (rectangle 20 20)))

(stack
 (stack (colorize "red" (rectangle 20 20))
        (colorize "blue" (rectangle 20 20))
        (colorize "green" (rectangle 20 20)))
 (line-up (colorize "orange" (rectangle 20 20))
          (colorize "red" (rectangle 20 20))
          (colorize "blue" (rectangle 20 20))
          (colorize "green" (rectangle 20 20))))

;; If you have some time, take a minute and play around a little. Make your own composite shapes.


;;;; 4. Your friend the computer

;; We already saw how `doc` is a way to ask Clojure what it knows about a function. You can also ask the computer what kind of thing something is. What was that value we evaluated up above?

(what-is "duck")

;; We'll explore all sorts of Clojure expressions and values together, and so we want you to have `what-is` by your side in case you're ever curious or confused. So...what is `what-is`?

(what-is what-is)

;; Please don't be shy about using `doc` and `what-is`. Ask the computer what it knows. The ideal state of programming is a natural, ongoing conversation with the computer, where you ask it questions, it explains things, and you tell it to do favors for you.

;; TODO add parameter hints to their toolbelt here -- if their cursor is inside parens enclosing a function, show the arglist for that function


;;;; 5. Functions upon functions upon functions

;; What if we want to draw a whole bunch of shapes? Typing "rectangle" over and over again would be a chore. We don't have to live like that. Our friend the computer wants to help us, and it is REALLY GOOD at repetitive chores.

(map circle [2 4 8 16 32 64 128])

;; There are a bunch of new things in that one line, so let's inspect them one by one. What's with the square brackets?

(what-is [2 4 8 16 32 64 128])

;; OK. So we have an ordered collection of numbers--a vector of integers.

;; What's `map`?

(what-is map)

;; OK, how does it work?

(doc map)

;; Oof. Maybe that's a bit technical. Let's break it down for what we're doing right now: `map` applies a function to every value in a collection. That means `(map circle [10 20 30])` returns the result of evaluating the function `circle` on the value 10, then on the value 20, then on the value 30: once for each element in the vector.

;; So let's look again at our code from above:

(map circle [2 4 8 16 32 64 128])

;; This returns the result of evaluating `circle` with radius 2, then radius 4, then radius 8, and so on for each element in our vector of numbers.

;; One neat thing about `map` is that you can give it more than one collection. To make that work, you need to `map` a function that uses more than one parameter. Remember `colorize`?

(doc colorize)

;; The first parameter to `colorize` is a color (as a string), and the second is a shape. (By the way, if you want to see which colors you we use, evaluate `colors`.) That means that if we want to `map` with the `colorize` function, we can give it two collections: first a collection of colors, and then a collection of rectangles. Then `map` will execute `colorize` using the first element of each collection, then the second element of each collection, then the third, and so on. So:
(map colorize
     ["red" "blue" "yellow"]
     [(rectangle 20 20) (rectangle 50 50) (rectangle 100 100)])

;; This is like calling each of these:

(colorize "red" (rectangle 20 20))
(colorize "blue" (rectangle 50 50))
(colorize "yellow" (rectangle 100 100))


;;;; Choosing names

;; Often when programming we need to name something we've created, so we can use it later. This is useful, but it's also a major source of trouble. There's an old saying in programming, attributed to Phil Carlton: "There are only two hard things in Computer Science: cache invalidation and naming things." Let's leave cache invalidation for another day and focus on names.


;; TODO `let` and `fn`, then `def`, then `defn`


;; XXX checkerboard? too direct a theft from Racket



;; XXX (or?) Let's make a rainbow:

(apply line-up
       (map colorize
            ["red" "orange" "yellow" "green" "blue" "purple"]
            (repeat (rectangle 20 20))))

;; TODO

(let [rainbow (apply line-up
                     (map colorize
                          ["red" "orange" "yellow" "green" "blue" "purple"]
                          (repeat (rectangle 20 20))))]
  (apply stack (repeat 20 [rainbow (rectangle 120 20)])))



(let [rainbow (fn [shape]
                (mapv colorize
                      ["red" "orange" "yellow" "green" "blue" "purple"]
                      (repeat shape)))]
  (rainbow (rectangle 20 20)))
