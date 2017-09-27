;; # Hi!

;; This environment is called Maria. We're going to learn to program
;; with Maria. 😃 Don't worry if anything's new or a little confusing at
;; first–we'll explain everything as we go.

;; First, let's ask the computer some questions. We'll ask our
;; questions in a programming language called Clojure. Here we go!

;; Put your cursor at the end of the line with "puppy" and press
;; `control-enter` (`command-enter` on Mac):

"puppy"

;; You just "evaluated" a puppy! 🐶 In Clojure, the word evaluate
;; means to tell the computer to "run" your code and tell you the
;; result.

;; Clojure is a language full of things called expressions, and
;; "puppy" is one of them. All expressions can be evaluated, as you
;; just did. What kind of expression is "puppy"? Evaluate this
;; expression to find out:

(what-is "puppy")

;; It's a *string*, which is a funny way of saying "some text". You
;; can tell it's a string because it's wrapped in double-quotes. There
;; are several other kinds of things in Clojure that we'll get to know
;; later, but in the meantime let's talk about those parentheses.

;; Whenever you see an expression in parentheses, it's kind of like a
;; sentence. In Clojure, sentences start with an open-parenthesis:
;; `(`. Next in the Clojure sentence is the verb, in this case,
;; `what-is`. We call these verbs "functions". Whatever comes after
;; the function "verb" are the *arguments* we want to give to the
;; function. When we evaluate an expression in parens it *calls* the
;; function at the beginning of the expression on the arguments that
;; follow. (These arguments don't have anything to do with arguing,
;; it's just a word from maths for what's given to a function).

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
;; arguments. Notice that when we wrote code that Clojure couldn’t
;; understand, Maria told us. Maria will even try to tell us how to
;; fix our code. Thanks, invisible robot helper.

;; What if we hadn't already known that `circle` needed to be given
;; just one radius argument? How would we find out the arguments to a
;; function? Well, our friend Maria knows, and will tell us. Put your
;; cursor on the `circle` expression. At the bottom left of your
;; browser you should see a short description of the `circle`
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

;; ⬇ your code goes here 😀 (press "return" or "enter" twice with the cursor there)

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
;; `color-names`, without parentheses around it.)

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
;; and we want to call a function on each thing, we can use the great
;; `map` function. Watch:

(map circle [16 32 64 128])

;; We "mapped" the function `circle` over a vector of all the sizes we
;; want our circles to be.

;; Have a look at both of those two expressions: one spelling out four
;; calls to `circle`, the other using `map` to call `circle` on every
;; value in a vector. As far as the computer is concerned, both
;; expressions do the same thing. That makes `map` powerful and
;; useful. Lots of `map`’s power comes alive when you pass it a
;; function tailor-made for the problem you’re solving. This probably
;; isn’t obvious yet, so to see what we mean, let's make a new
;; function right now! To start with, here's a really simple example
;; of a function:

(fn [radius] (circle radius))

;; Evaluating that doesn't return anything that's like what we've seen
;; before. This expression doesn’t return a shape or value. It returns
;; a funny "f" that shows our code when you click on it. Let’s inspect
;; it by wrapping the expression in a `what-is` call. Go ahead.

;; ... it's a function! What does this mean?

;; `fn` is a special kind of function that returns a brand new
;; function. Whenever you see an expression that starts with `fn`,
;; that's what it's doing: creating a function. Evaluating a `fn`
;; expression gives us back a function itself, not the result of
;; calling a function.

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

;; Try to guess what this expression will return 🤔. Once you have an
;; idea, evaluate the expression to test your hypothesis.

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

;; But sometimes you'll need to use a particular value or function
;; call over and over. That's when you should consider naming it, so
;; it's easier to change, so we know all the occurrences are supposed
;; to be the same thing, and so we don't unnecessarily repeat any
;; computations. For instance, if we're going to use a specific set of
;; colors for some shapes, we might want to create a named color
;; palette so we don't have to repeat the whole list of colors over
;; and over. For this, we can use `let`, like the color-shifting
;; picture we draw using this rainbow palette:

(let [palette ["red" "orange" "yellow" "green" "blue" "indigo" "violet"]]
  (layer (colorize (rand-nth palette) (square 100))
         (colorize (rand-nth palette) (triangle 100))
         (colorize (rand-nth palette) (circle 15))))

;; Make sure you evaluate this expression more than once 😇

;; How does it work? `let` takes two arguments. The first argument is
;; a vector with pairs: first a name, and then an expression that gets
;; that name. In the above expression, that's the `[palette ["red"
;; "orange" "yellow" "green" "blue" "indigo" "violet"]]` part, with
;; `palette` being the name (since it's the first of the pair) and the
;; vector of colors being the expression that `let` turns into that
;; name.

;; The second argument to `let` is an expression that uses the names
;; from the first argument. Here, we use the name `palette` to paint
;; some shapes using the `rand-nth` function. Use all the techniques
;; you've learned to explore what `rand-nth` does: Maria’s argument
;; list hints, `what-is`, `doc`, and evaluating the function with
;; different input.

;; ⬇ explore here (press "return" or "enter" twice to create a code block)
;; ⬆ explore here

;; (The "rand" in `rand-nth` comes from random; the "nth" comes from
;; maths, where it's common to write "1, 2, 3, and so on" as "1, 2, 3,
;; n". So instead of getting "1st" or "2nd" one from our vector, it's
;; a random "nth". 🤓)

;; Notice that the name we made up doesn't do anything outside the
;; `let`:

palette

;; Why? It's helpful to name things, but most of the time we only want
;; our names for a short time. One thing programmers have found out is
;; that having a lot of names that work everywhere gets hard to keep
;; track of. In the above expression, we needed the name `palette`
;; because we wanted to use it several times, and it makes no sense to
;; write out all those color names over and over. If we only needed
;; the list of colors once, we wouldn’t have named it at all. Fewer
;; names makes for simpler code that’s easier to read.

;; What else can we do with that rainbow palette? Well, let’s take a
;; look at the whole thing:

(let [palette ["red" "orange" "yellow" "green" "blue" "indigo" "violet"]]
  (map (fn [color] (colorize color (rectangle 20 20)))
       palette))

;; Wait. We’re repeating ourselves. If we’re going to use that rainbow
;; palette everywhere, it’s silly to make the same `let` every time.

;; We can *define* names that work all across your program using `def`:

(def rainbow ["red" "orange" "yellow" "green" "blue" "indigo" "violet"])

rainbow

(map (fn [color] (colorize color (rectangle 20 20)))
     rainbow)

;; 🏳️‍🌈 😺

;; Now we’ve got a `rainbow` in our toolbox, ready to use for
;; whatever. Programming like this is like building a LEGO spaceship,
;; except we can invent whatever blocks we need, and use them as many
;; times as we like.

(map (fn [color] (colorize color (rectangle 20 20)))
     (reverse rainbow))

;; Did you notice we’re repeating ourselves AGAIN with that function?
;; Let’s `def` it, too! We can call it "swatch", like the swatches of
;; color samples you get when choosing paint at the hardware store.

(def swatch (fn [color] (colorize color (rectangle 20 20))))

;; Now that the colors have been moved to `rainbow` and the
;; square-making function has been moved to `swatch`, our code to see
;; all our rainbow colors is shorter and more clear:

(map swatch rainbow)

;; Notice that this short three-word function call is the same as
;; this more verbose expression:

(map (fn [color] (colorize color (rectangle 20 20)))
     ["red" "orange" "yellow" "green" "blue" "indigo" "violet"])

;; It gets better. We programmers define functions so often that
;; Clojure has a special shorthand, `defn`, that is like a combined
;; `def` and `fn`. It takes a name (like `def`), then a vector of
;; arguments (like `fn`), then an expression using those arguments
;; (like both `def` and `fn`).

;; Here’s another giant expression. It explains `defn` with a diagram:

(layer
 (position 50 60 (text "(defn labeled-swatch [color] [color (colorize color (square 25))])"))
 (colorize "grey" (position 60 70 (triangle 10)))
 (position 0 102 (text "defines a function"))
 (position 105 25 (rotate 60 (colorize "grey" (triangle 10))))
 (position 90 20 (text "name"))
 (position 240 25 (rotate 60 (colorize "grey" (triangle 10))))
 (position 200 20 (text "argument(s)"))
 (colorize "grey" (position 310 70 (triangle 10)))
 (position 300 102 (text "expression")))

;; OK. Let’s try it. Let’s use `defn` to make labeled swatches (like
;; `color-names` uses) so we know what color we’re looking at. Our
;; function will take a color and return a vector that has the color
;; name (the label) and then a square showing the color (the swatch):

(defn labeled-swatch [color]
  [color (colorize color (square 25))])

;; Just like with `fn`, evaluating that `defn` doesn't return a shape
;; or a value or anything else tangible. It returns the function
;; itself. Now the name `labeled-swatch` is defined, so we can call
;; it:

(labeled-swatch "red")

;; We can use it to help make sense of what shades of blue there are.

(map labeled-swatch
     ["blue" "lightskyblue" "darkslateblue"
      "midnightblue" "powderblue" "steelblue"
      "cornflowerblue" "aliceblue" "deepskyblue"
      "skyblue" "dodgerblue" "mediumblue"
      "darkblue" "blueviolet" "cadetblue"
      "slateblue" "royalblue" "lightblue"
      "lightsteelblue" "mediumslateblue"])

;; Or:
(labeled-swatch (rand-nth rainbow))

;; We can even tell future programmers how to use our function by
;; documenting it. Right now `labeled-swatch` doesn’t have any
;; documentation:

(doc labeled-swatch)

;; ...but if we add a string to our `defn` between the function name
;; and the argument vector, that string will show up when you call
;; `doc`. We call those "docstrings", short for "documentation
;; string". Let’s write our own docstring describing what
;; `labeled-swatch` does, and then re-evaluate `(doc swatch)`.

(defn labeled-swatch
  "HI I’M A DOCSTRING PLEASE TURN ME INTO SOMETHING HELPFUL"
  [color]
  [color (colorize color (square 25))])

;; Hey–nice work! 🎉 💯 👏🏾 You created a function. That’s the only
;; requirement to being a True Programmer. Give yourself a
;; high-five. I am right now giving you a high-five. Maria is giving
;; you a high-five.

;; # Where to Go From Here

;; You've been introduced to the essence of code: writing expressions,
;; asking the computer questions, and creating functions. Where to go
;; with that power is up to you. Your next step is to find interesting
;; ways to put functions together to create cool stuff. I look forward
;; to seeing what you make.

;; This introduction to Clojure intentionally avoids getting into the
;; benefits of Lisp syntax, language features like immutability or
;; laziness, hosting and tooling concerns, and the full span of basic
;; data types. This document is a consciously incomplete
;; introduction. While all of those topics are valuable material, they
;; are secondary for the Clojure beginner. Instead, since Clojure
;; programmers typically spend the majority of their time juggling
;; expressions, names, and functions, we try to offer in Maria a
;; playground where the beginner is introduced to those fundamental
;; tools.

;; To explore other things one can do with Clojure and Maria, check
;; out the [Gallery](https://maria.cloud/gallery), explore other
;; [curriculum modules](https://maria.cloud/), or write your own
;; code on a fresh page by using the New button at the top left.

;; If you want to use full powers of the Maria environment, take a
;; look at the [Editor
;; Quickstart](https://maria.cloud/quickstart).

;; If you have the patience to work through a textbook, consider
;; [these Clojure books](https://clojure.org/community/books).

;; If you’re not sure which of these is best, it might help to
;; consider what you would find rewarding to build. What is your
;; purpose for learning programming? Your answer might be to build
;; mobile apps, websites, or games, or to make art, design graphics,
;; or explore science or math or statistics or [information
;; theory](https://maria.cloud/gist/888b354fe941866721370a91e181252c)
;; or linguistics, or...?
