;; # Hi!

;; This environment is called Maria. We're going to learn to program with Maria. 😃 Don't worry if anything's new or a little confusing at first–we'll explain everything as we go.

;; First, let's ask the computer some questions. We'll ask our questions in a programming language called Clojure. Here we go!

;; Put your cursor at the end of the following code block with `(circle 25)` and press `Control-Enter` (`Command-Enter` on Mac):

(circle 25)

;; You just drew a circle by *evaluating* that code. In Clojure, the word evaluate means to tell the computer to run some code and tell you the result.

;; Clojure is a language full of things called expressions, and `(circle 25)` is one of them. All expressions can be evaluated, as you just did. Let's take a look a the pieces of that expression:

(what-is circle)

;; `circle` is a *function*. By putting the function right after an open-parenthesis, we *call* that function. This makes the expression in parentheses kind of like a sentence. In Clojure, sentences start with an open-parenthesis: `(`. Next in the Clojure sentence is the verb, in this case, `circle`. The verb is usually a function. Whatever comes after the function "verb" are the *arguments* we want to give to the function. When we evaluate an expression in parens it *calls* the function at the beginning of the expression on the arguments that follow. (These arguments don't have anything to do with arguing, it's just a word from maths for what's given to a function).

;; What do you think evaluating this next function might do? Say your guess out loud before trying it–it's fun and helpful to try to predict what the computer will do.

(square 50)

;; We get a square with 50-pixel sides. Ask yourself: what's the name of the function we're calling here? What arguments does it take?

;; I wonder what giving `circle` two arguments would do.

(circle 20 10)

;; 😱 Don't worry, errors happens to every programmer. Stay positive ✌️ and track down the misunderstanding between you and the computer. In this case, we gave `circle` one too many arguments. Notice that when we wrote code that Clojure couldn’t understand, Maria told us. Maria will even try to tell us how to fix our code. Thanks, invisible robot helper.

;; What if we hadn't already known that `circle` needed to be given just one radius argument? How would we find out the arguments to a function? Well, our friend Maria knows, and will tell us. Put your cursor on the `circle` expression. At the bottom left of your browser you should see a short description of the `circle` function. The part in square brackets, "`[radius]`", tells us what arguments the `circle` function takes: one argument called `radius`. The last part of Maria’s hints are a description of what the function does.

;; Sometimes the description of what a function does is quite long, so you’ll want to take a better look at it than the quick hint that Maria shows. You can ask the computer to describe a function with this expression:

(doc circle)

;; `doc` is short for "documentation". The `doc` function tells us what a function does and what arguments it needs. We gave `doc` the argument `circle`, so it gives us documentation for the `circle` function. The computer knows things and wants to share what it knows with us.

;; Remember, we can also examine the different pieces of that expression by asking `what-is`:

(what-is 25)

(what-is circle)

(what-is (circle 25))

;; We can even apply the `what-is` function to the `what-is` function to find out what `what-is` is! 😹

(what-is what-is)

;; OK. Let's review our **Code Inspection Toolbox**:
;; - `what-is`
;; - `doc`
;; - bottom-of-the-screen documentation hints
;; Whenever you're not sure what some code does, grab one of these to inspect its parts.

;; This is a good time to take a break. Stretch your legs. Are you working on this alongside other people? This is a good time to chat with them about how things are going.


;; ## Shapes 🔺 and Colors 🌈

;; Now that you've seen the basics of evaluating expressions and calling functions, let's learn some more through toying with some shapes and colors.

;; Try creating your own expression to use some other numbers with `circle`.

;; ⬇ your code goes here 😀 (press "return" or "enter" twice with the
;; cursor there)

;; ⬆ your code goes here

;; In fact, you should feel free to experiment with this document at any time. Try absurd numbers! Move things around! This is *your* playground. In fact, if you feel inspired to go off script with lots of your own code blocks, you should consider opening a fresh page with the "New" button at the top left. And if you don't mind new and complex things being thrown at you without explanation, you might get some inspiration for your wanderings in the [Gallery](https://maria.cloud/gallery).

;; As you might guess, there's a `rectangle` function much like `circle`. Let's find out how it works:

(doc rectangle)

;; OK, so it takes two arguments: width and height. Let's take it for a spin. You fill in the arguments:

(rectangle )

;; You can also nest expressions inside other expressions to create bigger ones. For example, these black shapes are a little boring. Let's add some color!

(colorize "blue" (rectangle 250 100))

;; Now try putting your cursor inside the nested expression and evaluating just the inner sub-expression. For instance, with your cursor on `100`, pressing the evaluation key command will evaluate just that value: 100. With your cursor one level "outwards", after the closing paren `)` of `(rectangle 250 100)`, you'll evaluate just that sub-expression.

;; Another trick: you can always evaluate the whole expression with `Control-Shift-Enter`; `Shift-Command-Enter` on Mac. That is, even with your cursor at `rectangle` in `(colorize "blue" (rectangle 250 100))`, adding `Shift` to your evaluation key command will evaluate not `rectangle` but the top-level expression of that code block: `(colorize "blue" (rectangle 250 100))`. This is particularly useful when making changes.

;; Now, try evaluating each inner part of this expression:

(colorize "turquoise" (rectangle 300 (* 2 25)))

;; Do you now know what `*` does? Evaluating individual sub-expressions like this is an important tool in anyone's **Code Inspection Toolbox**.

;; Maybe, if you feel like it, change "blue" to "purple" or another color. What color names does Maria recognize? Explore this:

color-names

;; You can expand that list by clicking the ellipsis ("..."). But that's a lot of colors. To filter just the ones you want, explore this:

(colors-named "yellow")

;; Try other partial color names, like "slate" or "light". 🔭

;; Before moving ahead, depending on how comfortable you feel with exploring on your own how Maria commands work, you might want to check out the [Editor Quickstart](https://maria.cloud/quickstart). That page explains how to use this environment to its full potential. Once you've gone through that, come on back here and continue.

;; In addition to nesting expressions, we can also combine expressions to create groups of shapes. We do this by using the `layer` function. Let's reach into our **Code Inspection Toolbox** to ask about it:

(doc layer)

;; By default, things in layers all share the same top/left corner, and thus often overlap:

(layer
 (colorize "aqua" (square 50))
 (colorize "magenta" (circle 25)))

;; We can also position them within a layer using the `position` function, which takes an `x` and a `y` to tell it where to put a shape, counting down and to the right from the top left corner.

(layer
 (colorize "springgreen" (circle 25))
 (position 50 25 (colorize "pink" (circle 25))))

;; Why not try evaluating each of these sub-expressions too?

;; Now that you are experienced with evaluating individual expressions inside a big nested expression, let's draw a face with an expression:

(layer
 (colorize "aqua" (circle 40))
 (position 10 10 (colorize "magenta" (triangle 24)))
 (position 45 10 (colorize "magenta" (triangle 24)))
 (position 40 55 (colorize "white" (circle 10))))

;; Take a minute and play around a little. See what you can make by combining shapes. 🏠️ 🕸️ Remember to use your **Code Inspection Toolbox**:
;; - `doc`
;; - bottom-of-the-screen documentation hints
;; - `what-is`
;; - evaluating sub-expressions

;; ⬇ your code goes here (or anywhere, really!)

;; ⬆ your code goes here


;; ## ⚡️ Computing Superpowers 💪🏽

;; We can draw a whole bunch of shapes using two new things: "vectors" and the `map` function. Try it first:

(map square [2 4 8 16 32 64 128])

;; What's going on here? The part in square brackets (`[` and `]`) is a vector, which is how we store a bunch of things in order. We use a vector of numbers here to make a bunch of squares with that size. We use `map` to evaluate the function `square` on each thing in our vector. The result is a square for every number in our collection.

;; Try using `map` with `circle`. Replace "_" with your code:

(map _)

;; If we want to `map` a function that takes two arguments, we need to give it two arguments. We can do that by giving `map` one vector for the first argument and a second vector for the second argument.

;; We can do that for `rectangle`. The first vector (with two `10`s and two `50`s) is used for the width. The second vector (with different values) is used for the heights:

(map rectangle
     [10 10 50 50]
     [10 25 50 75])

;; Play around with the values in those vectors a bit, to get a sense for how `map` uses both vectors.

;; There's a second way to control multiple arguments in a `map`, and that is to create a function on the fly to do what we want. We use this in situations where only one argument will change. For instance, say we want to create some shapes with particular colors.

;; 1. We start with a vector of color names, `["red" "blue" "green"]`
;; 2. We use `fn` to make up a _totally new function_--we call these "anonymous functions", because they don't need names
;; 3. We use `map` to apply that anonymous function to each color name in our vector

;; The technique looks like this:

(map (fn [color] (colorize color (circle 25)))
     ["red" "blue" "green"])

;; The short way to read this out loud is "we map an anonymous function, which takes a 'color' argument, over a vector of color names".

;; Let's look closer at our anonymous function.

;;    ![](https://i.imgur.com/U1KuIIf.png)

;; We create this function-with-no-name using `fn`, which we give two arguments: first a vector and then an expression.
;; 1. The vector declares what arguments our new function-with-no-name will receive. In this case it takes just one, which we call `color`. This name is a placeholder that will be filled in when someone calls this function later.
;; 2. The expression is code that our function-with-no-name will run when _it_ gets evaluated. The `color` placeholder will be replaced by whatever value our anonymous function is given as an argument.

;; Making up functions on the fly is super useful. Let's use this technique in another scenario. Say instead of creating shapes with particular colors, we want the same shape but with different degrees of transparency:

(map (fn [o] (opacity o (square 40)))
     [0, 1/4, 1/2, 3/4, 1])

;; What happens if you evaluate the `fn` sub-expression? It doesn’t return a shape or a value. It returns a funny "f" that shows _our code_ when you click on it. 😲

;; Let’s inspect with one of our **Code Inspection Tools**:

(what-is (fn [o] (opacity o (square 40))))

;; ... it's a function, just like `circle` or `colorize`. What's going on here? `fn` is an unusual kind of function called a "special form", which means it is one of the building blocks of Clojure. The `fn` special form returns a brand new function. Whenever you see an expression that starts with `fn`, that's what it's doing: creating a function. Evaluating a `fn` expression gives us back a function itself, not the result of calling a function.

;; Now you try it. Edit the code below to map an anonymous function over one (or more!) vectors. 🏘️ Consider using `triangle`, `ellipse`, `opacity`, or `colorize`:

(map (fn [] ) 
     [])

;; The power to create our own functions is the heart of the power of programming. We entrust this power now to you. ⚡️ ✨


;; ## 👨🏾‍🚀 Names 👩🏻‍🚀

;; To use a particular shape many times, it's simpler to give it a name. For instance, we can turn one triangle into a mountain range by naming it with `let` and stamping it onto our canvas several times.

;; `let` takes two things:
;;  - a vector of name/value pairs
;;  - an expression using those names

(let [t (triangle 100)] ;; <-- this vector holds our name/value pair
  ;; now we write an expression using that name:
  (layer t
         (position 50 0 t)
         (position 88 0 t)
         (position 113 0 t)
         (position 185 0 t)
         (position 195 0 t)
         (position 238 0 t)))

;; We `let` the letter `t` have the value `(triangle 100)` so we can use the name `t` inside our expression to make a triangle.

;; If you need to reuse some value across multiple expressions, you can give it a "global" name with `def`. For instance, we're going to use a rainbow palette for a couple different sketches, so we'll create the name `rainbow`.

(def rainbow
  ["red" "orange" "yellow" "green" "blue" "indigo" "violet"])

;; Now we can use our new name `rainbow` anywhere:

(map (fn [c] (colorize c (circle 25))) rainbow)

;; 🌈 😺

;; Maybe that little anonymous function there could come in handy. Let's give it a name, too!

(def colorized-circle
  (fn [c] (colorize c (circle 25))))

;; Then we can:

(colorized-circle "slategray")

;; or:

(map colorized-circle ["green" "seagreen" "limegreen" "mediumturquoise"])

;; Nice! That's not all: Programmers like you define functions so often that Clojure has a special shorthand, `defn`, that is like a combined `def` and `fn`. Take a look at how we use `defn` to build `colorized-circle`:

(defn colorized-circle [c]
  (colorize c (circle 25)))

;; Let's look closer at `defn`'s parts:

;;    ![](https://i.imgur.com/gnssmpJ.png)

;; Just like with `fn`, evaluating `defn` doesn't return a shape or a value. It returns the function itself, which we can use in an expression somewhere else.

;; Cool! Now that we're naming functions, we're just about finished.


;; ## 🔬 Putting Your Programmer Tools to Work 🔨

;; The last part of this whirlwind introduction is to show you how to _think about_ defining functions. How do we start to tackle the problem?

;; The problem-solving template goes like this:
;;  1. Explore the problem in a conversation with the computer
;;  2. Gradually turn your explorations into a solution
;;  3. (maybe) Turn your solution into a named function for later

;; Let's try it with a slightly more complex shape-drawing challenge: drawing a flag with a vector of colors we give it. For instance, suppose we want to turn our `rainbow` vector into a rainbow flag, or a vector of three colors into a vertical tricolor like the flag of Germany or the Netherlands. So our goal will be a function that stacks colored bands to make a flag out of whatever color names we give it.

;; First, we explore. We know we want our code to accept a vector of colors, turning each one into a shape that makes up our flag. From that we can expect that our code might use `map`. Our first attempt might look something like:

(map (fn [c] (colorize c (rectangle 300 20))) 
     rainbow)

;; But that's not quite right, because `map` returns a collection of shapes, and we want the shapes stacked next to each other. To get the rectangles adjacent we need the `apply` function. Like `map`, `apply` takes a function and a collection, but instead of doing the function to each value in the collection, `apply` gives all of the arguments at once to the function. This is especially useful with functions that take any number of arguments, like `above` and `beside`. 

(apply above
       (map (fn [c]
              (colorize c (rectangle 300 20))) 
            rainbow))

;; The `above` function needs its arguments to be shapes, and it is willing to accept any number of them. We used `apply` as a bridge between between a function that wants many arguments and a collection of those arguments as a single value.

;; We've explored the problem (step 1) and now we're ready to turn our exploration into a solution (step 2). To make a solution, we need to make our flag have the right proportions not just for `rainbow` colors, but for other numbers of colors as well. Right now, using fewer colors just looks thin and off-balance compared to the familiar 3:2 ratio we had earlier:

(apply above
       (map (fn [c]
              (colorize c (rectangle 300 20))) 
            ["orange" "red" "lightgreen" "brown"]))

;; The challenge here is to generalize our flag-drawing code. The next code block has a solution. Try to wrestle with the problem yourself before scrolling down to see how Maria solves it. Use the code block right there. Experiment with different numbers of colors in the vector, and different ways of calculating the size of the rectangle.

;; ...

;; ...

;; (Hint: you'll want to `count` the vector of colors.)

;; ...

;; ...

;; ...

;; ...

;; ...

;; ...

;; ...

;; Here's the solution Maria got:

(let [colors ["blue" "green"]]
  (apply above
         (map (fn [c]
                (colorize c (rectangle 300 (/ 200 (count colors))))) 
              colors)))

;; To preserve the 3:2 ratio, we divide the total height (200) by the number of colors we have. The `let` helps in two ways: first it allows us to try out different vectors of colors without repeating ourselves, and then when we're done it tells us what the arguments to our function should be.

;; Now that we have a general vertically-stacked-colors flag solution, we're ready for step 3: turning our solution into a function. What should we call our function? If you're stumped, you're in good company: naming things is one of the truly hard problems in programming. One serviceable name is `vertical-flag`, which we can define like this:

(defn vertical-flag [colors] 
  (apply above
         (map (fn [c]
                (colorize c (rectangle 300 (/ 200 (count colors))))) 
              colors)))

;; See how the `let` for `colors` turns into the argument list for our function? Let's try it out!

;; Rainbow flag!
(vertical-flag rainbow)

;; Germany!
(vertical-flag ["black" "red" "gold"])

;; Indonesia!
(vertical-flag ["red" "white"])

;; Catalonia!
(vertical-flag ["gold" "red" "gold" "red" "gold" "red" "gold" "red" "gold"])

;; We could keep going (Ukraine! Hungary! Holland!), and if you're hungry for more, you could write a similar function for horizontal flags, but let's take a moment to congratulate you.

;; Nice work! 🎉 💯 👏🏾 You created a function. That’s the only requirement to being a True Programmer. Give yourself a high-five. I am right now giving you a high-five. Maria is giving you a high-five.


;; # Where to Go From Here

;; You've been introduced to the essence of code: writing expressions, asking the computer questions, and creating functions. Where to go with that power is up to you. Your next step is to find interesting ways to put functions together to create cool stuff. I look forward to seeing what you make. ✨

;; This introduction to programming through Clojure intentionally avoids getting into the benefits of Lisp syntax, language features like immutability or laziness, hosting and tooling concerns, and the full span of basic data types. This document is a consciously incomplete introduction. While all of those topics are valuable material, they are secondary for the programming beginner. Instead, since Clojure programmers typically spend the majority of their time juggling expressions, names, and functions, we try to offer with Maria a playground where the beginner is introduced to those fundamental tools.

;; To explore other things one can do with Clojure and Maria, check out the [Gallery](https://maria.cloud/gallery), explore other [curriculum modules](https://maria.cloud/curriculum), or write your own code on a fresh page by using the New button at the top left.

;; If you'd like to work through a textbook, consider [Clojure for the Brave and True](https://www.braveclojure.com/clojure-for-the-brave-and-true/) or [Living Clojure](http://shop.oreilly.com/product/0636920034292.do).

;; If you’re not sure which of these is best, it might help to consider what you would find rewarding to build. What is your purpose for learning programming? Your answer might be to build mobile apps, websites, or games, or to make art, design graphics, or explore science or math or statistics or [information theory](https://www.maria.cloud/shannons-entropy) or linguistics, or...?
