;;;; Hi! This is A Quick Introduction to Clojure with Pictures.


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

;; OK. So we have a vector of integers.

;; What's `map`?

(what-is map)

;; OK, how does it work?

(doc map)

;; That's a bit technical. For our purposes here, `map` applies a function to every value in a collection. That means our code above created a circle of radius 2, a circle of radius 4, a circle of radius 8, and so on for each element in our vector of numbers.
