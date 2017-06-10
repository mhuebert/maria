;;;; A Quick Introduction to Clojure with Shapes

;; Hi! This tutorial will introduce you to programming by helping you draw some shapes.

;; Here is a simple expression:

"puppy"

;; Put your cursor at the end of the line with "puppy" and press `control-enter` (`command-enter` on Mac). BOOM. You just evaluated a puppy. This expression evaluates to itself: "puppy". Clojure (the programming language we're using) printed the evaluation of our "puppy" expression in the output panel on the right.

;; That's how you'll explore: type and evaluate expressions on the left; and read the result of your evaluations on the right.


;;;; Asking the Computer Questions

;; You can think of expressions like "puppy" as nouns. They describe a thing and they evaluate to themselves. Expressions that are verbs return something other than themselves, and we call those functions. Functions are how we ask the computer questions or to do something for us.

;; We write these function expressions inside parentheses that hold the function name (the "verb") followed by all the parameters (the "nouns" you give the function). It looks like this:

(circle 50)

;; The "verb" (function name) is `circle`, and we give it the "noun" `50`. Everything stays inside the parentheses, so there's no confusion about which verbs go with which nouns. When you evaluate this expression you're asking the computer to draw a circle with a 50-pixel radius.

;; What if you didn't know that `circle` needed to be given a radius? How would you find out the parameters to a function? Why, you can ask your friend the computer!

(what-is circle)

;; TODO transitions in this whole section

(what-is what-is)

;; If you're not sure what a certain expression is, you can ask!

(what-is 50)

;; TODO (explain _)

;; FIXME transition
(doc circle)

;; The `doc` function is short for "documentation". It tells you what a function does and what parameters it needs. You gave `doc` the parameter `circle`, so it gives you documentation for the `circle` function. The computer wants to help you.

;; TODO (once we have nice results) try and fail (doc "puppy")

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


;;;; Powers of fun

;; What if you want to draw a whole bunch of shapes? Typing "rectangle" over and over again is a chore. You don't have to live like that. Our friend the computer wants to help us, and it is REALLY GOOD at repetitive chores.

(map circle [2 4 8 16 32 64 128])

;; There are a bunch of new things in that one line, so let's inspect them one by one. What's with the square brackets?

(what-is [2 4 8 16 32 64 128])

;; OK. So square brackets mean a vector. This vector has numbers in it.

;; TODO maybe introduce (vector) here?
;; TODO maybe (map vector [1 2 3])
;; TODO maybe then (map vector [1 2 3] [4 5 6])

;; What's `map`?

(what-is map)

;; OK, how does it work?

(doc map)

;; Oof. Maybe that's a bit technical. Let's break it down for what you're doing right now: `map` applies a function to every value in a collection. That means `(map circle [10 20 30])` returns the result of evaluating the function `circle` on the value 10, then on the value 20, then on the value 30: once for each element in the vector.

;; So let's look again at our code from above:

(map circle [2 4 8 16 32 64 128])

;; This returns the result of evaluating `circle` with radius 2, then radius 4, then radius 8, and so on for each number in our vector.


;; XXX NEW
;; Let's try something similar with `rectangle`.

(map rectangle [10 20 5 50 100])

;; Hmm...all squares is fine, but what if we want to draw a city skyline, or a bar chart? We can just give `map` a vector of widths AND a vector of heights, and it will stitch them together for us:

(map rectangle
     [5 5 5 5 5 5 5 5 5 5 5 5]
     [10 20 5 50 100 70 76 33 20 90])

;; Does it annoy you that you had to type out `5` a bunch of times? It annoys me. It's so tedious. Did I even get the number of `5`s right? Thankfully, our friend the computer LOVES doing things over and over, and we can just ask it to `repeat` as many `5`s as we need:

(map rectangle
     (repeat 5)
     [10 20 5 50 100 70 76 33 20 90])

;; How does `repeat` do that?

(doc repeat)

;; Wow. Infinity.😲😯😵

;; We can do the same thing with colors: `colorize` takes a color and a shape. That means that if you want to `map` with the `colorize` function, you can give it a vector of colors and then a vector of shapes. Then `map` will execute `colorize` using the first element of each vector, then the second element of each vector, then the third, and so on. So:
(map colorize
     ["red" "blue" "yellow"]
     [(rectangle 20 20) (rectangle 50 50) (rectangle 100 100)])

;; The first element of the first vector and the first element of the second vector get used to call the function. So that expression with `map` is like evaluating each of these individual expressions:

(colorize "red" (rectangle 20 20))
(colorize "blue" (rectangle 50 50))
(colorize "yellow" (rectangle 100 100))


;;;; The power of names

;; Often when programming you need to name something you've created. For instance, you can let the name "palette" be a vector of colors:;; FIXME

(let [palette ["red" "orange" "yellow" "green" "blue" "purple"]]
  (map colorize
       palette
       (repeat (circle 50))))

;; And there's our color palette. Lovely. You can read this as saying, "Let 'palette' be the name for this vector, ['red' 'orange' blah blah blah] while we evaluate the next expression."

;; Names can be helpful, but too many names scattered across your code gets hard to keep track of. That's why we use `let` to create names we only need for right now. Watch--try to use `palette` outside the `let`:

palette

;; The name `palette` only means something *inside the `let`*. That way we know it won't cause trouble somewhere else.

;; If you need a name that you'll use over and over, you need to define it with `def`:
(def palette ["red" "orange" "yellow" "green" "blue" "purple"])

;; Now you can use `palette` anywhere. You can blindly grab a color from our palette with `rand-nth`, which is a random-picker function:
(colorize (rand-nth palette) (circle 50))

;; Let's make a more complex shape with our new color palette.
(apply stack
       (map colorize
            [(rand-nth palette) (rand-nth palette) (rand-nth palette)]
            ;; FIXME requires creating triangle function
            [(circle 50) (triangle 100) (rectangle 100 100)]))

;; It's kind of annoying that we must repeat ourselves for those random colors. And we don't have to! Just like `repeat` will give you as many "nouns" you need, `repeatedly` will give you as many "verby" function calls you need.

(doc repeatedly)

;; So to use `repeatedly`, we need a function. But `(rand-nth palette)` isn't a function, it's a function *call*--it evaluates to some random color name, not the function itself:

(what-is (rand-nth palette))

;; What we need to do is create a function, which we can do `fn`:
(what-is (fn [] (rand-nth palette)))

;; All the functions you've used so far have had names, but this one doesn't, because we're just using it once.

;; Your anonymous function has square brackets to declare its parameters, of which it has none. Now you can call this anonymous function `repeatedly`:
(apply stack
       (map colorize
            (repeatedly (fn [] (rand-nth palette)))
            ;; FIXME requires creating triangle function
            [(circle 50) (triangle 100 100 100) (rectangle 100 100)]))

;; `repeatedly` will go on forever just like `repeat` if you let it, but it knows you only need to call that function as many times as there are shapes. (Clojure is crafty like that.)

;; We can use an infinite number of circles alongside our infinite random color choices, as long as we say how many we want to `take` from the infinite bucket:
(apply line-up
       (map colorize
            (take 10 (repeatedly (fn [] (rand-nth palette))))
            (repeat (circle 50))))

;; You know, we keep using that anonymous function. That's a good sign that maybe we should name it. Just like we named `palette`, we use `def` to name our function:
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
