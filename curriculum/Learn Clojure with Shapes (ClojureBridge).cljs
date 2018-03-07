;; # Welcome to ClojureBridge!

;; Put your cursor at the end of the following code block with `(circle 25)` and press `Control-Enter` (`Command-Enter` on Mac):

(circle 25)

;; You just drew a circle by *evaluating* that code.

;; Look at the pieces of that expression:

(what-is circle)

;; What do you think evaluating this next function might do?

(square 50)

;; Ask yourself: what's the name of the function we're calling here? What arguments does it take?

;; Try:

(circle 20 10)

;; 😱 Don't worry, errors happens to every programmer. Stay positive ✌️ and track down the misunderstanding between you and the computer. In this case, we gave `circle` one too many arguments.

;; What if we hadn't already known that `circle` needed to be given just one radius argument? Put your cursor on the `circle` expression. Look at the bottom left of your browser for an explanation of the function.

;; You can ask the computer to describe a function with this expression:

(doc circle)

;; Remember `what-is`:

(what-is 25)

(what-is circle)

(what-is (circle 25))

;; We can even apply the `what-is` function to the `what-is` function to find out what `what-is` is! 😹

(what-is what-is)

;; Let's review our **Code Inspection Toolbox**:
;; - `what-is`
;; - `doc`
;; - bottom-of-the-screen documentation hints

;; This is a good time to take a break to stretch your legs. If you're working on this alongside other people, this is a good time to chat with them about how things are going.


;; ## Shapes 🔺 and Colors 🌈

;; Let's learn more by toying with some shapes and colors.

;; Try creating your own expression to use some other numbers with `circle`.

;; ⬇ your code goes here 😀 (press "return" or "enter" twice with the
;; cursor there)

;; ⬆ your code goes here

;; (Feel free to experiment all over this document. Need a blank canvas? Click the "New" button in the top left. Need inspiration? Check out the [Gallery](https://maria.cloud/gallery).)

;; There's a `rectangle` function much like `circle`. Use your **Code Inspection Toolbox** to see how it works:

(doc rectangle)

;; OK, it takes two arguments: width and height. Let's try it. You fill in the arguments:

(rectangle )

;; You can also nest expressions inside other expressions to create bigger ones:

(colorize "blue" (rectangle 250 100))

;; Put your cursor inside the nested expression and evaluate just the inner sub-expression. 

;; You can always evaluate the whole expression with `Control-Shift-Enter`; `Shift-Command-Enter` on Mac.

;; Evaluate each inner part of this expression:

(colorize "turquoise" (rectangle 300 (* 2 25)))

;; What color names can we use?

color-names

;; Before moving ahead, read the [Editor Quickstart](https://maria.cloud/quickstart). Then come back here and continue.

;; We can also create groups of shapes with `layer`. Replace the `???` with a function from your **Code Inspection Toolbox** to see how it works:

(??? layer)

;; By default, things in layers all share the same top/left corner, and thus often overlap:

(layer
 (colorize "aqua" (square 50))
 (colorize "magenta" (circle 25)))

;; We can also position them within a layer using the `position` function, which takes an `x` and a `y` to tell it where to put a shape:

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

;;  1. The part in square brackets (`[` and `]`) is a vector
;;  2. `map` evaluates the function `square` on each thing in our vector
;;  3. The result is a square for every number in our collection

;; Try using `map` with `circle`. Replace "_" with your code:

(map _)

;; If we want to `map` a function that takes two arguments, we need to give it two arguments. We can do that by giving `map` one vector for the first argument and a second vector for the second argument.

;; We can do that for `rectangle`. The first vector (with all `10`s) is used for the width. The second vector (with different values) is used for the heights:

(map rectangle
     [10 10 50 50]
     [10 25 50 75])

;; Play around with the values in those vectors a bit, to get a sense for how `map` uses both vectors.

;; There's a second way to control multiple arguments in a `map`, and that is to create a function on the fly to do what we want. We use this in situations where only one argument will change. For instance, say we want to create some shapes with particular colors.

;; 1. We start with a vector of color names, `["red" "blue" "green"]`
;; 2. We use `fn` to make up a _totally new function_--we call these "anonymous functions", because they don't need names)
;; 3. We use `map` to apply that anonymous function to each color name in our vector

;; The technique looks like this:

(map (fn [color] (colorize color (circle 25)))
     ["red" "blue" "green"])

;; Let's look closer at our anonymous function.

;;    ![](https://i.imgur.com/U1KuIIf.png)

;; We create this function-with-no-name using `fn`, which we give two arguments: first a vector and then an expression.
;; 1. The vector declares what arguments our new function-with-no-name will receive. In this case it takes just one, which we call `color`. This name is like a placeholder that will be filled in when someone calls this function later.
;; 2. The expression is code that our function-with-no-name will run when _it_ gets evaluated. The `color` placeholder will then get replaced by whatever value our anonymous function is given as an argument.

;; Making up functions on the fly is super useful. Let's use this technique in another scenario. Say instead of creating shapes with particular colors, we want the same shape but with different degrees of transparency:

(map (fn [o] (opacity o (square 40)))
     [0, 1/4, 1/2, 3/4, 1])

;; What happens if you evaluate the `fn` sub-expression? It doesn’t return a shape or a value. It returns a funny "f" that shows _our code_ when you click on it. 😲

;; Let’s inspect with one of our **Code Inspection Tools**:

(what-is (fn [o] (opacity o (square 40))))

;; What's going on here? `fn` is a "macro", a special kind of function that takes code and transforms it before the code gets evaluated. The `fn` macro returns a brand new function. Whenever you see an expression that starts with `fn`, that's what it's doing: creating a function. Evaluating a `fn` expression gives us back a function itself, not the result of calling a function.

;; Now you try. Edit the code below to map an anonymous function over one (or more!) vectors. 🏘️ Consider using `triangle`, `ellipse`, `opacity`, or `colorize`:

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

;; Programmers like you define functions so often that Clojure has a special shorthand, `defn`, that is like a combined `def` and `fn`. Look at how we can rewrite `colorized-circle` using `defn`:

(defn colorized-circle [c]
  (colorize c (circle 25)))

;; Let's look closer at `defn`'s parts:

;;    ![](https://i.imgur.com/gnssmpJ.png)

;; Just like with `fn`, evaluating `defn` doesn't return a shape or a value. It returns the function itself, which we can use in an expression somewhere else.

;; Cool! Now that we're naming functions, we're just about finished.


;; ## 🔬 Putting Your Programmer Tools to Work 🔨

;; The last thing is to show you how to _think about_ defining functions. The problem-solving template goes like this:

;;  1. explore the problem
;;  2. gradually turn your explorations into a solution
;;  3. (maybe) turn your solution into a named function

;; Let's try it by drawing a flag based on a vector of colors. For instance, suppose we want to turn our `rainbow` vector into a rainbow flag. Our goal is a function that makes a flag out of whatever color names we give it.

;; First, we explore. Our first attempt might look something like:

(map (fn [c] (colorize c (rectangle 300 20))) 
     rainbow)

;; But that's not quite right, because `map` returns a collection of shapes, and we want the shapes stacked next to each other. To get the rectangles adjacent we need the `apply` function. Like `map`, `apply` takes a function and a collection, but instead of doing the function to each value in the collection, `apply` gives all of the arguments at once to the function.

(apply above
       (map (fn [c]
              (colorize c (rectangle 300 20))) 
            rainbow))

;; We've explored the problem (step 1) and now we're ready to turn our exploration into a solution (step 2). Right now, using fewer colors just looks thin and off-balance compared to the familiar 3:2 ratio we had earlier:

(apply above
       (map (fn [c]
              (colorize c (rectangle 300 20))) 
            ["orange" "red" "lightgreen" "brown"]))

;; The challenge here is to generalize our flag-drawing code. The next code block has a solution. Try to wrestle with the problem yourself before scrolling down to see the solution. Use the code block right there. Experiment with different numbers of colors in the vector, and different ways of calculating the size of the rectangle.

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

(let [colors ["blue" "green"]]
  (apply above
         (map (fn [c]
                (colorize c (rectangle 300 (/ 200 (count colors))))) 
              colors)))

;; To preserve the 3:2 ratio, we divide the total height (200) by the number of colors we have. The `let` helps in two ways: first it allows us to try out different vectors of colors without repeating ourselves, and then when we're done it tells us what the arguments to our function should be.

;; Now that we have a general vertical-stacked-colors flag solution, we're ready for step 3: turning our solution into a function:

(defn vertical-flag [colors] 
  (apply above
         (map (fn [c]
                (colorize c (rectangle 300 (/ 200 (count colors))))) 
              colors)))

;; Notice how the `let` for `colors` turns into the argument list for our function.

;; Try it out:

;; Rainbow flag!
(vertical-flag rainbow)

;; Germany!
(vertical-flag ["black" "red" "gold"])

;; Indonesia!
(vertical-flag ["red" "white"])

;; Catalonia!
(vertical-flag ["gold" "red" "gold" "red" "gold" "red" "gold" "red" "gold"])

;; If you're hungry for more, you could write a similar function for horizontal flags, but let's take a moment to congratulate you.

;; Nice work! 🎉 💯 👏🏾 You created a function. That’s the only requirement to being a True Programmer. Give yourself a high-five. I am right now giving you a high-five. ClojureBridge is giving you a high-five.


;; # Where to Go From Here

;; You've been introduced to the essence of code: writing expressions, asking the computer questions, and creating functions. Your next step is to find interesting ways to put functions together to create cool stuff.

;; To explore other things one can do with Clojure and Maria, check out the [Gallery](https://maria.cloud/gallery) or explore more of our curriculum modules:

;; - cover conditional logic in [What If?](https://www.maria.cloud/what-if)
;; - explore math, statistics, and information theory through [Shannon's Entropy](https://www.maria.cloud/shannons-entropy)
;; - learn how to make animations and user interfaces with [Cells](https://www.maria.cloud/cells) (or skip the "learning" part with the [Animation Quickstart](https://dev.maria.cloud/animation-quickstart)
;; - poke around open data APIs with [Data Flow](https://www.maria.cloud/data-flow)

;; If you’re not sure which of these is best, it might help to consider what you would find rewarding to build. What would you like to with through programming?
