;; # Hi!

;; This environment is called Maria. We're going to learn to program
;; with Maria. 😃 Don't worry if anything's new or a little confusing at
;; first–we'll explain everything as we go.

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
;; just did. What kind of expression is "puppy"? Evaluate this
;; expression to find out:

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

;; What do you think evaluating this next function might do? Say your
;; guess out loud before trying it–it's fun and helpful to try to
;; predict what the computer will do.

(circle 50)

;; We get a circle with a 50-pixel radius. Ask yourself: what's the
;; name of the function we're calling here? What are its arguments?

;; I wonder what giving `circle` two arguments would do.

(circle 20 10)

;; 😱 Don't worry, errors happens to every programmer. Stay positive ✌️
;; and track down the misunderstanding between you and the
;; computer. In this case, we gave `circle` one too many
;; arguments. Notice that when we write code that Clojure can't
;; understand, Maria told us. Maria will even try to tell us how to
;; fix our code. Thanks, invisible robot helper.

;; What if we hadn't already known that `circle` needed to be given
;; just one radius argument? How would we find out the arguments to a
;; function? Well, our friend Maria knows, and will tell us. Put your
;; cursor on the `color` expression. At the bottom left of your
;; browser you should see a short description of the `color`
;; function. The part in square brackets, "`[radius]`", tells us what
;; arguments the `circle` function takes: one argument called
;; `radius`. The last part of Maria’s hints are a description of what
;; the function does.

;; Sometimes the description of what a function does is quite long, so
;; you’ll want to take a better look at it than the quick hint that
;; Maria shows. You can do that with this expression:

(doc circle)

;; The `doc` function is short for "documentation". It tells us what a
;; function does and what arguments it needs. We gave `doc` the
;; argument `circle`, so it gives us documentation for the `circle`
;; function. The computer knows things and wants to share what it
;; knows with us.

;; Remember, we can also examine the different pieces of that
;; expression with `what-is`:

(what-is 50)

(what-is circle)

(what-is (circle 50))

;; We can even apply the `what-is` function to the `what-is` function
;; to find out what `what-is` is! 😹

(what-is what-is)


;; ## Shapes 🔺 and Colors 🌈

;; Now that you've seen the basics of evaluating expressions, calling
;; functions, and asking the computer clarifying questions with
;; `what-is` and `doc`, let's learn some more through toying with some
;; shapes and colors.

;; Try creating your own expression to use some other numbers with
;; `circle`.

;; ⬇ your code goes here 😀

;; ⬆ your code goes here

;; In fact, you should feel free to experiment with this document
;; at any time. Try absurd numbers! Move things around!
;; This is *your* playground.

;; As you might guess, there's a `rectangle` function much like
;; `circle`. Let's find out how it works:

(doc rectangle)

;; OK, so it takes two arguments: width and height. Let's take it for
;; a spin. You fill in the arguments:

(rectangle )

;; You can also nest expressions inside other expressions to create
;; bigger ones. For example, these black shapes are a little
;; boring. Let's add some color!

(colorize "blue" (rectangle 250 100))

;; Now try putting your cursor after each closing paren `)`, one at a
;; time, using your powers of evaluation on each sub-expression.

(colorize "blue" (rectangle 250 (* 2 50)))

;; Do you now know what `*` does?

;; Maybe, if you feel like it, change "blue" to "purple" or another
;; color. (For a list of colors that Maria understands, try evaluating
;; `color-names`.)

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


;; ## 🖳 Computing Superpowers 💪🏽

;; What if we want to draw a whole bunch of shapes? Typing "rectangle"
;; over and over again is a 😴 chore. We don't have time for that! Our
;; friend the computer wants to help, and it is REALLY GOOD at
;; repetitive chores. 🤖

;; Let's start by evaluating this:

(what-is [1 2 3 4])

;; "Vectors" are how we store sequences of things. They're written
;; using square braces `[]` and they evaluate to themselves like
;; strings and numbers. This vector describes the order of prehistoric
;; eras:

["Stone Age" "Bronze Age" "Iron Age"]

;; This comes in handy when we want to do something like draw a bunch
;; of related shapes. For instance, we could make a vector of circles
;; like so:

[(circle 16) (circle 32) (circle 64) (circle 128)]

;; But there's a better, shorter way! When we have a bunch of things,
;; and we want to call a function on each thing, we can use
;; `map`. `map` is a great function that gives us a new collection
;; that is the result of calling our function on each thing.

;; That means we can map the function `circle` over a vector of all
;; the sizes we want our circles to be:

(map circle [16 32 64 128])

;; Have a look at both of those two expressions. As far as the
;; computer is concerned, they do the same thing, but the magic of
;; `map` saves typing and improves clarity. Map is a great way to
;; avoid repeating yourself.

;; FIXME Let's make a new
;; function right now! To start with, here's a really simple example
;; of a function:

(fn [radius] (circle radius))

;; If that doesn't make any sense, try wrapping it in a `what-is`
;; call. Go ahead.

;; ... it's a function! But what does it mean?

;; `fn` is a special kind of function that returns a brand new
;; function. Whenever you see an expression that starts with `fn`,
;; that's what it's doing: creating a function.

;; To go a bit deeper in how `fn` works, evaluate this giant
;; expression. It will draw a small diagram to help explain how
;; functions work.

(layer
 (position 50 60 (text "(fn [radius] (circle radius))"))
 (colorize "grey" (position 60 70 (triangle 10)))
 (position 0 102 (text "function"))
 (position 95 25 (rotate 60 (colorize "grey" (triangle 10))))
 (position 90 20 (text "argument(s)"))
 (colorize "grey" (position 170 70 (triangle 10)))
 (position 170 102 (text "expression")))

;; Take a look at the diagram Maria just drew for us.

;; First, there's the `fn`, which means this expression will evaluate
;; to a function.

;; The part in square brackets `[]` shows the arguments that this
;; function will accept, and the order in which it will expect
;; them. In this case, it's only one argument called
;; `radius`. Arguments are a kind of placeholder that will be filled
;; in when someone calls this function later.

;; Finally, every function contains an expression that will be
;; evaluated for us when we call it. Inside this expression the
;; arguments used to call this function will be available by the names
;; they were given in the square bracket part before.

;; Try to guess what this expression will return 🤔, then evaluate it.

(map (fn [radius] (circle radius))
     [16 32 64 128])

;; This function just wraps the `circle` function in another function,
;; so it doesn't do anything different than calling circle
;; directly. But we can change it to also call `colorize` on each
;; circle, like this:

(map (fn [radius] (colorize "purple" (circle radius)))
     [16 32 64 128])

;; 💜 💜 💜 💜 Instead of a "circle" function, we wrote a "purple
;; circle" function. The power to create our own functions is the
;; heart of the power of programming.


;; ## 👨🏾‍🚀 Names 👩🏻‍🚀

;; So far, none of the functions we've created have had names. We've
;; created these "anonymous" functions, used them briefly, and that
;; was it. The same goes for the values we've been using, like strings
;; and vectors–none of them have had names, either.

;; But sometimes you'll need to use a particular function call over
;; and over. That's when you should consider naming it, so the
;; computation only gets done once, and the result is saved for the
;; next time we need it. For instance, if we're going to use a
;; specific set of colors for some shapes, we might want to create a
;; named color palette so we don't have to repeat the list of colors
;; over and over. For this, we can use `let`, like this palette of
;; blues:

(let [palette ["blue" "turquoise" "midnightblue"]]
  palette)

;; `let` takes two arguments. The first argument is a vector with
;; pairs: first a name, and then an expression that gets that
;; name. The second argument is an expression that uses the names from
;; the first vector. Here, we didn't do anything to the name
;; `palette`, so we just get its value. Notice that our name doesn't
;; do anything outside the `let`:

palette

;; That's actually a good thing. It's helpful to name things, but most
;; of the time we only want our names for a short time. One thing
;; programmers have found out is that having a lot of names that work
;; everywhere gets confusing.

;; Now let's do something with a name we create. This time, we'll make
;; a new palette of colors, and show them off instead of just
;; returning the names:

(let [palette ["red" "orange" "yellow" "green" "blue" "indigo" "violet"]]
  (map (fn [color] (colorize color (rectangle 20 20))) palette))

;; Super. Let's try another palette, but this time we just want to
;; sample from it one at a time:

(let [palette ["grey" "black" "white" "darkgrey" "lightgrey" "slate"]]
  (colorize (rand-nth palette) (circle 25)))

;; Evaluate this one more than once. We snuck in a new function there,
;; `rand-nth`, that grabs a random color from our palette to colorize
;; the circle. (The "rand" in `rand-nth` comes from random; the "nth"
;; comes from maths, where it's common to write "1, 2, 3, and so on"
;; as "1, 2, 3, n". So instead of getting "1st" or "2nd" one from our
;; vector, it's a random "nth". 🤓)

;; But...is "slate" really a color? I think I saw it before when we
;; played with `color-names`, but I just can't remember. And that list
;; of colors is so long, it's a pain to look through it to
;; check. Maybe...maybe the computer could help?

;; ...

;; The computer says yes, it would love to help, and that it is really
;; really super good at looking at long lists of things.

;; First let's take a look at our data. There are a lot of colors, so
;; let's grab just a few:

(take 5 color-names)

;; Hmm. We've got square brackets, some text in double-quotes, and a
;; colored square. Let's ask this data some questions. (Feel free to
;; `what-is` any of these if you're not sure.)

(first color-names)

(first (first color-names))

(second (first color-names))

;; So each entry in color-names seems to be a vector with two values:
;; first a string for the color name, and second a shape using that
;; color. We don't really need the shapes, so let's work with only all
;; the names:

(map first color-names)

;; To ask whether a color name is contained in those names, we turn it
;; into a `set` and use `contains?`. (A set is an unordered collection,
;; which is much easier to search through than an ordered collection
;; like a vector.)

(contains? (set (map first color-names)) "slate")

;; And let's double-check that it's not `false` for *every* name:

(contains? (set (map first color-names)) "gray")

;; OK. Maybe I was thinking of IKEA couch colors when I thought of
;; "slate"? Regardless, we've solved our question. But our code isn't
;; super clear, is it? If we saved this somewhere, without a name,
;; would we know what it does? Would it be easy to figure out why we
;; wrote it? Not really. We can make our life easier by making it a
;; function and giving that function a name. Here's that same code as
;; a function:

((fn [color] (contains? (set (map first color-names)) color)) "orange")

((fn [color] (contains? (set (map first color-names)) color)) "moonblue")

;; ...and now let's name it:

(let [color-name? (fn [color] (contains? (set (map first color-names)) color))]
  (color-name? "slate"))

;; We should be suspicious of this code. Do we really have to say all
;; that every time we want to check if something is a valid color
;; name?

(let [color-name? (fn [color] (contains? (set (map first color-names)) color))]
  (color-name? "purple"))

;; 😩

(let [color-name? (fn [color] (contains? (set (map first color-names)) color))]
  (color-name? "reallydarkgrey"))

;; Ugh! 😫 Let's not repeat ourselves. `let` is good when we'll use a
;; name only in one spot, but this is a function we want to be able to
;; call from anywhere. And for that, we have `def`.

;; You can _define_ names that work all across your program using `def`:

(def rainbow-colors ["red" "orange" "yellow" "green" "blue" "indigo" "violet"])

rainbow-colors

;; Nice! 🏳️‍🌈 Now you fill in this `def` to do the same for our
;; color-name-checking function:

(def color-name?
  ;; your code goes here

  )

(color-name? "burntochre")

(color-name? "charredogre")

;; Pretty cool, right? There's even `defn`, a special shorthand for
;; defining functions:

(defn color-name? [color]
  (contains? (set (map first color-names)) color))

(color-name? "blue")

(color-name? "blau")

;; Unfortunately Maria doesn't understand German color names 🇩🇪 🙁





;; Programming like this is like building a LEGO spaceship, except we
;; can invent whatever blocks we need, and use them as many times as
;; we like. For instance, `rainbow-colors` is in our toolbox, ready
;; whenever we need it. Here's another way to show off our rainbow,
;; using the power of `map` over more than one vector at a time:

(map colorize
     rainbow-colors
	 (repeat (rectangle 20 20)))

;; How it works is that we're mapping over `colorize`, which takes two
;; arguments. For each step it takes the first argument from the first
;; collection and the second argument from the second collection–and
;; so on if there are more.

;; Evaluate sub-expressions to see what that means. Each step
;; colorizes one color from `rainbow-colors`, and one shape to get
;; colorized, which comes from the output of `repeat`. What comes out
;; of `repeat`?

;; ...???

;; Congratulations! Now that we've created a function, you are a True
;; Programmer. Give yourself a high-five. I am right now giving you a
;; high-five. Maria is giving you a high-five.

;; You've been introduced to the essence of code: writing expressions,
;; asking the computer questions, and creating functions. Where to go
;; with that power is up to you: the next step is finding interesting
;; ways to put functions together to create cool stuff.
