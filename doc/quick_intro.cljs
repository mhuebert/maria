;;;; A Quick Introduction to Clojure with Pictures.


;;;; 1. Ready...

;; Hi! This tutorial will introduce you to programming by drawing pictures using the Clojure programming language.

;; This environment you're in is called Maria, after education pioneer Maria Montessori. Working with Maria is the friendliest way we know how to share the elegance of programming Clojure, even if you eventually use some other editor.


;;;; 2. Set...

;; This text you're reading is a comment. That just means it's meant for humans like you to read it, and the computer ignores it. You can tell it's a comment because of the two semicolons at the start of the line.

;; In Clojure, everything that isn't a comment is an expression. You can evaluate expressions, which is like asking the expression what it is or what it does. Here is a simple expression:

"duck"

;; Put your cursor to the same line as the expression "duck", and press `command-enter`. BOOM. You just evaluated a duck. This "duck" expression evaluates to just itself: "duck". Clojure printed the evaluation of our "duck" expression in the output panel on the right. That's how you're going to work: type and evaluate expressions on the left; and read the result of our evaluations on the right.


;;;; 2. Go!

;; An expression can also be a function call. To call a function, put an open parenthesis before the function name, then expressions for the parameters you give the function, and then a close parenthesis, like this:

(circle 50)

;; The evaluation of the `circle` function call is a circle. The `circle` function needs a parameter for how big the circle will be. You just asked the computer to draw a circle with a 50-pixel radius. How would you find out the parameters to a function like `circle`? Why, you can ask Clojure!

(doc circle)

;; The `doc` function returns the documentation for any function. You pass it the parameter `circle`, so it prints `circle`'s documentation for us. The computer is your friend and wants to help you.

;; Try giving circle the wrong number of arguments, just to see what happens:

(circle 20 10)

;; Notice that the result is an error instead of a picture or value. If you write code that Clojure can't understand, it will tell you it can't run it. If it can, Clojure will try to tell you how to fix your code.

;; Don't worry if you create an error. It happens to all of us.


;;;; 3. Drawing

;; As you might guess, there's a `rectangle` function much like `circle`. Let's find out how it works:

(doc rectangle)

;; OK, so it takes two parameters: width and height. Let's take it for a spin.

(rectangle 200 50)

;; That's pretty much what one would expect. But these one-tone shapes bore me. Let's add some color!

(colorize "blue" (rectangle 250 100))


;; That's a nice change of pace. You can also combine shapes with `stack`:

(stack (colorize "red" (rectangle 50 50))
       (colorize "blue" (rectangle 50 50)))

;; and also `line-up`:

(line-up (colorize "red" (rectangle 50 50))
         (colorize "blue" (rectangle 50 50))
         (colorize "green" (rectangle 50 50)))

;; and you can combine `stack` and `line-up`:

(stack
 (stack (colorize "red" (rectangle 50 50))
        (colorize "blue" (rectangle 50 50))
        (colorize "green" (rectangle 50 50)))
 (line-up (colorize "orange" (rectangle 50 50))
          (colorize "red" (rectangle 50 50))
          (colorize "blue" (rectangle 50 50))
          (colorize "green" (rectangle 50 50))))

;; If you have some time, take a minute and play around a little. Make your own composite shapes.


;;;; 4. Your friend the computer

;; You already saw how `doc` is a way to ask Clojure what it knows about a function. You can also ask the computer what kind of thing something is. What was that value you evaluated up above?

(what-is "duck")

;; We'll explore all sorts of Clojure expressions and values together, and so Maria wants you to have `what-is` by your side in case you're ever curious or confused. So...what is `what-is`?

(what-is what-is)

;; Please don't be shy about using `doc` and `what-is`. Ask the computer what it knows. The ideal state of programming is a natural, ongoing conversation with the computer, where you ask it questions, it explains things, and you tell it to do favors for you.

;; TODO add parameter hints to their toolbelt here -- if their cursor is inside parens enclosing a function, show the arglist for that function


;;;; 5. Functions upon functions upon functions

;; What if you want to draw a whole bunch of shapes? Typing "rectangle" over and over again would be a chore. You don't have to live like that. Our friend the computer wants to help us, and it is REALLY GOOD at repetitive chores.

(map circle [2 4 8 16 32 64 128])

;; There are a bunch of new things in that one line, so let's inspect them one by one. What's with the square brackets?

(what-is [2 4 8 16 32 64 128])

;; OK. So you have a vector of integers.

;; What's `map`?

(what-is map)

;; OK, how does it work?

(doc map)

;; Oof. Maybe that's a bit technical. Let's break it down for what you're doing right now: `map` applies a function to every value in a collection. That means `(map circle [10 20 30])` returns the result of evaluating the function `circle` on the value 10, then on the value 20, then on the value 30: once for each element in the vector.

;; So let's look again at our code from above:

(map circle [2 4 8 16 32 64 128])

;; This returns the result of evaluating `circle` with radius 2, then radius 4, then radius 8, and so on for each element in our vector of numbers.

;; One neat thing about `map` is that you can give it more than one collection. To make that work, you need to `map` a function that uses more than one parameter. Remember `colorize`?

(doc colorize)

;; The first parameter to `colorize` is a color (as a string), and the second is a shape. (By the way, if you want to see which colors you use, evaluate `colors`.) That means that if you want to `map` with the `colorize` function, you can give it two collections: first a collection of colors, and then a collection of rectangles. Then `map` will execute `colorize` using the first element of each collection, then the second element of each collection, then the third, and so on. So:
(map colorize
     ["red" "blue" "yellow"]
     [(rectangle 20 20) (rectangle 50 50) (rectangle 100 100)])

;; This is like calling each of these:

(colorize "red" (rectangle 20 20))
(colorize "blue" (rectangle 50 50))
(colorize "yellow" (rectangle 100 100))


;;;; Choosing names

;; Often when programming you need to name something you've created, so you can use it later. This is useful, but it's also a major source of trouble. There's an old saying in programming, attributed to Phil Carlton: "There are only two hard things in Computer Science: cache invalidation and naming things." Let's leave cache invalidation for another day and focus on names.

;; For instance, what if you want to pick which colors you'll use while drawing? For this you could use `let`, like this:

(let [palette ["red" "orange" "yellow" "green" "blue" "purple"]]
  (colorize (rand-nth palette) (circle 50)))

;; The first parameter to `let` is always a vector that takes pairs of names and their definitions. The rest of the parameters to `let` are expressions that can use those names. So here, we "let" the name "palette" be a vector of color names, and then evaluated `colorize` on a circle using a random-picked color from our palette.


(let [palette ["red" "orange" "yellow" "green" "blue" "purple"]]
  (apply line-up
         (map colorize
              (take 3 (repeatedly (fn [] (rand-nth palette))))
              (repeat (circle 50)))))

;; TODO `let` and `fn`, then `def`, then `defn`



;; XXX perhaps 1. introduce `def` with a color palette!
(def palette ["red" "orange" "yellow" "green" "blue" "purple"])

;; then 2. use that to introduce `fn` with something colorful:
(apply line-up
       (map colorize
            (take 5 (repeatedly (fn [] (rand-nth palette))))
            (repeat (circle 50))))



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
