;; # Hi!
;; This environment is called Maria, and we're going to show you
;; some things you can do with it. 😃 Don't worry if anything's new
;; and a little confusing at first, we're going to explain everything
;; as we go.

;; First, let's ask the computer some questions. We'll ask our
;; questions in a programming language called Clojure. Here we go!

;; Put your cursor at the end of the line with "puppy" and press
;; `control-enter` (`command-enter` on Mac):

"puppy"

;; You just "evaluated" a puppy! 🐶 In Clojure, the word evaluate means
;; something special. It's what happens when you tell the computer
;; to "run" your code and tell you what it thinks it means.

;; Clojure is a language full of things called expressions, and
;; "puppy" is one of them. All expressions can be evaluated, as you
;; just did. What kind of expression is "puppy"?

;; Evaluate this expression to find out:

(what-is "puppy")

;; It's a _string_, which is a funny way of saying "some text". You
;; can tell it's a string because it's wrapped in double-quotes. There
;; are several other kinds of things in Clojure that we'll get to know
;; later, but in the meantime let's talk about those parentheses.

;; Whenever you see an expression in parentheses, it's kind of like a
;; sentence where the first thing after the `(` is treated as the
;; verb. We call these verbs "functions", and when we evaluate a form
;; in parens it _calls_ the function at the beginning of the
;; expression on the _arguments_ that follow. (These arguments don't
;; have anything to do with arguing, it's just a word from maths for
;; what's given to a function).

;; What do you think a `circle` function with an argument of `50`
;; might do? Try evaluating it to find out:

(circle 50)

;; We get a circle with a 50-pixel radius. I wonder what giving
;; `circle` two arguments would do.

(circle 20 10)

;; 😱 We got an error. Don't worry, this happens to every
;; programmer. Stay positive ✌️ and track down the misunderstanding
;; between you and the computer.

;; When we write code that Clojure can't understand, Maria will tell
;; us. It will even try to tell us how to fix our code. In this case,
;; we gave `circle` one too many arguments.

;; What if we hadn't already known that `circle` needed to be given
;; just one radius argument? How would we find out the arguments to a
;; function?  Why, we can ask our friend the computer!

(doc circle)

;; The `doc` function is short for "documentation". It tells us what a
;; function does and what parameters it needs. We gave `doc` the
;; parameter `circle`, so it gives us documentation for the `circle`
;; function. The computer wants to help us.

;; Also, we can examine the different pieces of that expression with
;; `what-is`:

(what-is 50)

(what-is circle)

(what-is (circle 50))

;; We can even apply the `what-is` function to the `what-is` function
;; to find out what `what-is` is! 😹

(what-is what-is)


;; ## Asking the computer to do things

;; Try creating your own expression to use some other numbers with
;; `circle`.

;; ⬇ your code goes here 😀

;; ⬆ your code goes here

;; In fact, you should feel free to experiment with this document
;; at any time. Try absurd numbers! Move things around!
;; This is *your* playground.


;; ## Shapes and Colors

;; As you might guess, there's a `rectangle` function much like
;; `circle`. Let's find out how it works:

(doc rectangle)

;; OK, so it takes two parameters: width and height. Let's take it for
;; a spin.

(rectangle 200 50)

;; Well, that's not too surprising.

;; You can also nest expressions inside other expressions to create
;; bigger ones. For example, these black shapes are a little
;; boring. Let's add some color!

(colorize "blue" (rectangle 250 100))

;; Now try putting your cursor after each closing paren `)`, one at a
;; time, using your powers of evaluation on each sub-expression.

(colorize "blue" (rectangle 250 (* 2 50)))

;; Do you now know what `*` does?

;; Maybe, if you feel like it, change "blue" to "purple" or another
;; color. For a list of colors that Maria understands, try evaluating:

color-names

;; We can also combine expressions to create a layer of shapes, by
;; using the `layer` function:

(doc layer)

;; By default, things in layers all share the same top/left corner,
;; and thus often overlap:

(layer
 (colorize "aqua" (square 50))
 (colorize "magenta" (circle 25)))

;; But we can also position them within a layer using the `position`
;; function, which takes an `x` and a `y` to tell it where to put a
;; shape:

(layer
 (colorize "springgreen" (circle 25))
 (position 50 25 (colorize "pink" (circle 25))))

;; Why not try evaluating each of these sub-expressions too?

;; Now that you are experienced with evaluating individual expressions
;; inside a big nested expression, let's draw a face with an
;; expression:

(layer
 (colorize "aqua" (circle 40))
 (position 10 10 (colorize "magenta" (triangle 24)))
 (position 45 10 (colorize "magenta" (triangle 24)))
 (position 40 55 (colorize "white" (circle 10))))

;; If you have some time, take a minute and play around a little. Make
;; your own shape combinations, evaluating inner expressions to make
;; sure you know how they fit into the expression containing them. 🕸️


;; ## Powers of fun

;; What if we want to draw a whole bunch of shapes? Typing "rectangle"
;; over and over again is a 😴 chore. We don't have time for that! Our
;; friend the computer wants to help, and it is REALLY GOOD at
;; repetitive chores. 🤖

;; Let's start by evaluating this:

(what-is [1 2 3 4])

;; Vectors are written using square braces `[]`, and they're a great
;; way to write down some things in order. Vectors evaluate to
;; themselves, like strings and numbers do. Try evaluating this one:

[1 2 3 4]

;; Now suppose we wanted a vector of circles in increasing sizes. We
;; could do something like this:

[(circle 16) (circle 32) (circle 64) (circle 128)]

;; But there's a better, shorter way! When we have a bunch of things,
;; and we want to call a function on each thing, we can use `map`,
;; which is a special function. `map` gives us a new collection that
;; is the result of calling our function on each thing.

;; That means we can make a vector of all the sizes we want our
;; circles to be, and then map the function `circle` over that vector:

(map circle [16 32 64 128])

;; Have a look at both of those expressions. As far as the computer is
;; concerned, it's the same, but with the magic of 'map', you can
;; repeat a function without extra typing. Map is a great way to do
;; avoid repeating yourself. 📢

;; What if we wanted purple circles? It would be nice if there was a
;; function to turn a number into a purple circle. Let's make a new
;; function right now! To start with, here's a really simple example
;; of a function:

(what-is (fn [radius] (circle radius)))

;; ... it's a function! But what does it mean? First, evaluate this
;; giant expression, which will draw a small diagram to help explain
;; how functions work:


(layer
  (position 50 60 (text "(fn [radius] (circle radius))"))
  (colorize "grey" (position 60 70 (triangle 10)))
  (position 0 102 (text "function"))
  (position 95 25 (rotate 60 (colorize "grey" (triangle 10))))
  (position 90 20 (text "argument(s)"))
  (colorize "grey" (position 170 70 (triangle 10)))
  (position 170 102 (text "expression")))

;; Take a look at the diagram Maria just drew for us.

;; `fn` is a special kind of function that returns a brand new
;; function. Whenever you see an expression that starts with `fn`,
;; that's what it's doing: creating a function.

;; The part in square brackets `[]` shows the arguments that this
;; function will accept, and the order in which it will expect
;; them. In this case, it's only one argument called
;; `radius`. Arguments are a kind of placeholder that will be filled
;; in when someone calls this function later.

;; Finally, every function contains an expression that will be
;; evaluated for us when we call it. Inside this expression the
;; arguments used to call this function will be available by the names
;; they were given in the square bracket part before.

;; Try to guess what this expression will return, then evaluate it!

(map (fn [radius] (circle radius))
     [16 32 64 128])

;; This function just wraps the `circle` function in another function,
;; so it doesn't do anything different than calling circle
;; directly. But we can change it to also call `colorize` on each
;; circle like this:

(map (fn [radius] (colorize "purple" (circle radius)))
     [16 32 64 128])

;; 💜 💜 💜 💜

;; Great! Now we have the power to map colors!

;; But what if we wanted two sets of purple circles in different
;; sizes? One idea: we could use the 'map' function on two different
;; vectors like this:

[(map (fn [radius] (colorize "purple" (circle radius)))
      [16 32 64 128])
 (map (fn [radius] (colorize "purple" (circle radius)))
      [16 8 4 2])]

;; It works, but it's kind of a shame that we have to type out our
;; function twice just because we want two sequences of purple
;; circles. There's a better way! We can use `let` to give our
;; function a name.  Then we can call our function by that name as
;; many times as we need:

(let [make-purple-circle (fn [radius] (colorize "purple" (circle radius)))]
  [(map make-purple-circle [16 32 64 128])
   (map make-purple-circle [16 8 4 2])])


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; XXX dragons

;; That's great, but there's a catch. Names we give with `let` only
;; apply within the `let` expression. That's useful, so that names we
;; create while we're drawing cartoons don't mess up the names we
;; create while we're drawing landscapes. Names are surprisingly
;; tricky, so often it's better to keep them contained.

;; Then again, sometimes we'll need a name for everything we
;; do--cartoons, landscapes, portraits, all our work. Then things get
;; serious. That's when we need to start defining things for
;; certain. We define names that we need all over with `def`.

;; Say we're going to draw a bunch of shapes, and we want to use just
;; a few colors over and over. Picasso might choose five different
;; blues...a fan of old movies might choose ten shades of black and
;; white. Yours is up to you. Put a few color names into a vector,
;; like this:

["blue" "turquoise" "midnightblue"]

;; (Remember you can use anything in `color-names`.)

;; ⬇ your colors go here 😀

;; ⬆ your colors go here

;; Now, we'll define that as our color palette so we can use it over
;; and over just by calling its name. All we need to do is use `def`,
;; giving it a name and our vector:

;; your vector of colors goes here ⬇
(def palette                      )

;; Once we evaluate that, our `palette` is in our toolbox, ready
;; whenever we need it! Let's take a look at your palette:

(map (fn [color] (colorize color (rectangle 20 20))) palette)

;; Awesome.

;; FIXME this transition is weak

;; Now that we have our palette, we want to go wild with some
;; shapes. Maybe we want to draw the same shape a bunch of different
;; times, a bunch of different ways. For instance, hearts. So many, in
;; fact, that we want a heart-drawing function. First, let's sketch
;; out how to draw that.

;; TODO heart shape function, which we then colorize with their palette for great justice

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; XXX dragons

(doc map)

;; Oof. Maybe that's a bit technical. Let's break it down for what
;; we're doing right now: `map` applies a function to every value in a
;; collection. That means `(map circle [10 20 30])` returns the result
;; of evaluating the function `circle` on the value 10, then on the
;; value 20, then on the value 30: once for each element in the
;; vector.

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

;; Does it annoy you that we had to type out `5` a bunch of times? It annoys me. It's so tedious. Did I even get the number of `5`s right? Thankfully, our friend the computer LOVES doing things over and over, and we can just ask it to `repeat` as many `5`s as we need:

(map rectangle
     (repeat 5)
     [10 20 5 50 100 70 76 33 20 90])

;; How does `repeat` do that?

(doc repeat)

;; Wow. Infinity.😲😯😵

;; We can do the same thing with colors: `colorize` takes a color and a shape. That means that if we want to `map` with the `colorize` function, we can give it a vector of colors and then a vector of shapes. Then `map` will execute `colorize` using the first element of each vector, then the second element of each vector, then the third, and so on. So:
(map colorize
     ["red" "blue" "yellow"]
     [(rectangle 20 20) (rectangle 50 50) (rectangle 100 100)])

;; The first element of the first vector and the first element of the second vector get used to call the function. So that expression with `map` is like evaluating each of these individual expressions:

(colorize "red" (rectangle 20 20))
(colorize "blue" (rectangle 50 50))
(colorize "yellow" (rectangle 100 100))


;; ## The power of names

;; Often when programming we need to name something we've created. For instance, we can let the name "palette" be a vector of colors:;; FIXME

(let [palette ["red" "orange" "yellow" "green" "blue" "purple"]]
  (map colorize
       palette
       (repeat (circle 50))))

;; And there's our color palette. Lovely. We can read this as saying, "Let 'palette' be the name for this vector, ['red' 'orange' blah blah blah] while we evaluate the next expression."

;; Names can be helpful, but too many names scattered across our code gets hard to keep track of. That's why we use `let` to create names we only need for right now. Watch--try to use `palette` outside the `let`:

palette

;; The name `palette` only means something *inside the `let`*. That way we know it won't cause trouble somewhere else.

;; If we need a name that we'll use over and over, we need to define it with `def`:
(def palette ["red" "orange" "yellow" "green" "blue" "purple"])

;; Now we can use `palette` anywhere. We can blindly grab a color from our palette with `rand-nth`, which is a random-picker function:
(colorize (rand-nth palette) (circle 50))

;; Let's make a more complex shape with our new color palette.
(apply above
       (map colorize
            [(rand-nth palette) (rand-nth palette) (rand-nth palette)]
            ;; FIXME requires creating triangle function
            [(circle 50) (triangle 100) (rectangle 100 100)]))

;; It's kind of annoying that we must repeat ourselves for those random colors. And we don't have to! Just like `repeat` will give us as many "nouns" we need, `repeatedly` will give us as many "verby" function calls we need.

(doc repeatedly)

;; So to use `repeatedly`, we need a function. But `(rand-nth palette)` isn't a function, it's a function *call*--it evaluates to some random color name, not the function itself:

(what-is (rand-nth palette))

;; What we need to do is create a function, which we can do `fn`:
(what-is (fn [] (rand-nth palette)))

;; All the functions we've used so far have had names, but this one doesn't, because we're just using it once.

;; Our anonymous function has square brackets to declare its parameters, of which it has none. Now we can call this anonymous function `repeatedly`:
(apply above
       (map colorize
            (repeatedly (fn [] (rand-nth palette)))
            ;; FIXME requires creating triangle function
            [(circle 50) (triangle 100 100 100) (rectangle 100 100)]))

;; `repeatedly` will go on forever just like `repeat` if we let it, but it knows we only need to call that function as many times as there are shapes. (Clojure is crafty like that.)

;; We can use an infinite number of circles alongside our infinite random color choices, as long as we say how many we want to `take` from the infinite bucket:
(apply line-up
       (map colorize
            (take 10 (repeatedly (fn [] (rand-nth palette))))
            (repeat (circle 50))))

;; You know, we keep using that anonymous function. That's a good sign that maybe we should name it. Just like we named `palette`, we use `def` to name our function:
(def rand-color (fn [] (rand-nth palette)))

;; Now `rand-color` stands shoulder-to-shoulder with `circle` and `map`:
(what-is rand-color)

;; And now it's much more concise to get a random color in our expression:
(apply line-up
       (map colorize
            (take 10 (rand-color))
            (repeat (rectangle 50))))

;; Because we define functions so often, there's a special shorthand for giving them names:
(defn rand-color []
  (rand-nth palette))

;; We use the result of `defn` exactly the same way as the result of `def fn...`:
;; FIXME insert operatic amazing concluding shape here
(apply line-up
       (map colorize
            (take 10 (rand-color))
            (repeat (rectangle 50))))

;; Congratulations! Now that we've created a function, you are a True Programmer. Give yourself a high-five. I am right now giving you a high-five.

;; You've been introduced to the essence of code: writing expressions, asking the computer questions, and creating functions. Where to go with that power is up to you: the next step is finding interesting ways to put functions together to create cool stuff.
