;;;; A Quick Introduction to Clojure with Shapes

;; Hi! This tutorial will introduce you to programming by helping you draw some shapes.

;; Here is a simple expression:

"puppy"

;; Put your cursor on the same line as "puppy" and press `control-enter` (`command-enter` on Mac). BOOM. You just evaluated a puppy. This expression evaluates to itself: "puppy". Clojure (the programming language we're using) printed the evaluation of our "puppy" expression in the output panel on the right.

;; That's how you'll explore: type and evaluate expressions on the left; and read the result of your evaluations on the right.


;;;; Asking the Computer Questions

;; You can ask the computer questions by calling functions, which will return expressions. To call a function, put an open parenthesis, then the function name, then parameters you give the function, and then a close parenthesis. It looks like this:

(circle 50)

;; The function is `circle` and you give it the parameter `50`. Evaluating the `circle` function asks the computer to draw a circle with a 50-pixel radius.

;; What if you didn't know that `circle` needed to be given a radius? How would you find out the parameters to a function? Why, you can ask your friend the computer!

(doc circle)

;; The `doc` function is short for "documentation". It tells you what a function does and what parameters it needs. You gave `doc` the parameter `circle`, so it gives you documentation for the `circle` function. The computer wants to help you.

;; Try giving circle the wrong number of parameters, just to see what happens:

(circle 20 10)

;; You got an error instead of a shape or expression. Don't worry if you create an error. It happens to all of us. If you write code that Clojure can't understand, it will tell you. Clojure will try to tell you how to fix your code. This is the ideal: programmer and computer, working together, each doing the part of the task they do best.


;;;; Shapes and Colors

;; As you might guess, there's a `rectangle` function much like `circle`. Let's find out how it works:

(doc rectangle)

;; OK, so it takes two parameters: width and height. Let's take it for a spin.

(rectangle 200 50)

;; That's pretty much what one would expect. But these one-tone shapes bore me. Let's add some color!

(colorize "blue" (rectangle 250 100))

;; That blue is nice. You can also combine shapes with `stack`:

(stack (colorize "red" (rectangle 50 50))
       (colorize "blue" (rectangle 50 50)))

;; and also `line-up`:

(line-up (colorize "red" (rectangle 50 50))
         (colorize "blue" (rectangle 50 50))
         (colorize "green" (rectangle 50 50)))

;; Wow--that is a lot of nested function calls. Let's make sure you understand how they work together.

;; Put your cursor on a `50` inside the `rectangle` call, then evaluate it. You should get `50`: a number evaluates to itself.

;; Now put your cursor after a `(rectangle 50 50)`, to the right of the first close-parenthesis. Evaluate it. You should get a black square.

;; Now put your cursor at the end of the first line and evaluate it. You should get a red square.

;; OK, now that you're experienced with evaluating individual expressions inside a big nested expression, let's combine `stack` and `line-up`:

(stack
 (stack (colorize "red" (rectangle 50 50))
        (colorize "blue" (rectangle 50 50))
        (colorize "green" (rectangle 50 50)))
 (line-up (colorize "orange" (rectangle 50 50))
          (colorize "red" (rectangle 50 50))
          (colorize "blue" (rectangle 50 50))
          (colorize "green" (rectangle 50 50))))

;; If you have some time, take a minute and play around a little. Make your own composite shapes, evaluating inner expressions to make sure you know how they fit into the expression containing them.


;;;; More questions to ask

;; You already saw how `doc` is a way to ask Clojure what it knows about a function. You can also ask the computer what kind of thing something is. What was that value you evaluated up above?

(what-is "puppy")

;; We'll explore all sorts of Clojure expressions and values together, and so Maria wants you to have `what-is` by your side in case you're ever curious or confused. So...what is `what-is`?

(what-is what-is)

;; Please don't be shy about using `doc` and `what-is`. Ask the computer what it knows. It's like a dog that never gets tired of playing fetch: it will keep answering you as long as you have questions to ask.

;; TODO add parameter hints to their toolbelt here -- if their cursor is inside parens enclosing a function, show the arglist for that function


;;;; Powers of fun

;; What if you want to draw a whole bunch of shapes? Typing "rectangle" over and over again is a chore. You don't have to live like that. Our friend the computer wants to help us, and it is REALLY GOOD at repetitive chores.

(map circle [2 4 8 16 32 64 128])

;; There are a bunch of new things in that one line, so let's inspect them one by one. What's with the square brackets?

(what-is [2 4 8 16 32 64 128])

;; OK. So square brackets mean a vector. This vector has numbers in it.

;; What's `map`?

(what-is map)

;; OK, how does it work?

(doc map)

;; Oof. Maybe that's a bit technical. Let's break it down for what you're doing right now: `map` applies a function to every value in a collection. That means `(map circle [10 20 30])` returns the result of evaluating the function `circle` on the value 10, then on the value 20, then on the value 30: once for each element in the vector.

;; So let's look again at our code from above:

(map circle [2 4 8 16 32 64 128])

;; This returns the result of evaluating `circle` with radius 2, then radius 4, then radius 8, and so on for each number in our vector.

;; One neat thing about `map` is that you can give it more than one collection. To make that work, you need to `map` a function that uses more than one parameter. We'll use `colorize`.

;; `colorize` takes a color and a shape. That means that if you want to `map` with the `colorize` function, you can give it two collections: first a collection of colors, and then a collection of shapes. Then `map` will execute `colorize` using the first element of each collection, then the second element of each collection, then the third, and so on. So:
(map colorize
     ["red" "blue" "yellow"]
     [(rectangle 20 20) (rectangle 50 50) (rectangle 100 100)])

;; The first element of the first collection and the first element of the second collection get used to call the function. So that expression with `map` is like evaluating each of these individual expressions:

(colorize "red" (rectangle 20 20))
(colorize "blue" (rectangle 50 50))
(colorize "yellow" (rectangle 100 100))


;;;; The power of names

;; Often when programming you need to name something you've created. For instance, you can let the name "palette" be a vector of colors:

(let [palette ["red" "orange" "yellow" "green" "blue" "purple"]]
  (rand-nth palette))

;; You can read this as saying, "Let 'palette' be the name for this vector, ['red' 'orange' blah blah blah] while we evaluate the next expression." The next expression says, "Get a random element from 'palette'." Evaluate it a few times--you'll see a different random color from your palette every time!

;; Names can be helpful, but too many names scattered across your code gets hard to keep track of. That's why we use `let` to create names we only need for right now. Watch--try to use `palette` outside the `let`:

palette

;; The name `palette` only means something *inside the `let`*. That way we know it won't cause trouble somewhere else.

;; If you need a name that you'll use over and over, you need to define it with `def`:
(def palette ["red" "orange" "yellow" "green" "blue" "purple"])

;; Now we can use `palette` anywhere.
(colorize (rand-nth palette) (circle 50))

;; Let's make a more complex shape with our new color palette.
(apply stack
       (map colorize
            [(rand-nth palette) (rand-nth palette) (rand-nth palette)]
            ;; FIXME requires creating triangle function
            [(circle 50) (triangle 100 100 100) (rectangle 100 100)]))

;; It's kind of annoying that we must repeat ourselves for those random colors. And we don't have to! If you need to do something over and over, you can create a function with `fn`:
(what-is (fn [] (rand-nth palette)))

;; All the functions you've used so far have had names, but this one doesn't, because we're just using it once.

;; Your anonymous function has square brackets to declare its parameters, of which it has none. Now you can call this anonymous function `repeatedly`:
(apply stack
       (map colorize
            (repeatedly (fn [] (rand-nth palette)))
            ;; FIXME requires creating triangle function
            [(circle 50) (triangle 100 100 100) (rectangle 100 100)]))

;; `repeatedly` will go on forever if you ask it to, but it knows you only need to call that function as many times as there are shapes. (Clojure is crafty like that.)

;; You can get an infinite list of shapes, too, as long as you tell it how many you want:
(apply line-up
       (map colorize
            (take 3 (repeatedly (fn [] (rand-nth palette))))
            (repeat (circle 50))))

;; You know, we keep using that anonymous function. That's a good sign that maybe we should name it. Just like with `palette`, we use `def`:
(def rand-color (fn [] (rand-nth palette)))

;; Now `rand-color` stands shoulder-to-shoulder with `circle` and `map`:
(what-is rand-color)

;; And now it's much more concise to get a random color in your expression:
(apply line-up
       (map colorize
            (take 10 (rand-color))
            (repeat (rectangle 50))))

;; Because we define functions so often, there's a special shorthand for giving them names:
(defn rand-color []
  (rand-nth palette))

;; You use the result of `defn` exactly the same way as the result of `def fn...`:
;; FIXME insert operatic amazing concluding shape here
(apply line-up
       (map colorize
            (take 10 (rand-color))
            (repeat (rectangle 50))))

;; Congratulations! Now that you've created a function, you are a True Programmer. Give yourself a high-five.

;; You've been introduced to the essence of code: writing expressions, asking the computer questions, and creating functions. Where to go with that power is up to you: the next step is finding interesting ways to put functions together to create cool stuff.
