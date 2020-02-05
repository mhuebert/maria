;; # Colours and Shapes in Clojure

;; This web page allows you to ask your computer to do tasks for you, specifically draw one or more shapes and change the colour of those shapes. We ask the computer to do these tasks in a programming language called Clojure.

;; Clojure is a functional programming language. The behaviour of your code is expressed by calling one or more functions.

;; Each function has a particular thing it does when you call it.  We will discover functions that:

;; * draw particular shapes

;; * add a colour to a shape

;; * help you combine shapes

;; You will also discover several common functions used for functional programming, `map`, `filter` and `reduce`.

;; ## Create a shape by evaluating a function

;; A function is called by putting its name within a `list`.  A `list` is represented by an open and close round bracket: `()`

;; We have create several functions that will draw a shape for you in this web page.  You just need to call the function and tell it how big a shape you want.

;; For example, when we call the `circle` function, we need to tell it what size of circle to draw.

;; Put the cursor at the end of the code below (in the white box) and press `Ctrl+Enter` on a PC or `Cmd+Enter` on a Mac

(circle 25)



;; #### Exercise: Create a circle of size 50 (or any other size you like)

()



;; ### Visualising the Clojure function call

;; Here is a visualisation of writing a function call in Clojure.

;; ![Clojure function call](https://clojure.org/images/content/guides/learn/syntax/structure-and-semantics.png "Clojure function call")


;; ## Arguments

;; Arguments can be any value in Clojure, for example numbers, strings, collections and functions too!

;; A function can take zero or more arguments.

;; Arguments affect the behaviour of a function.

;; So if you give a bigger number as an argument to the `circle` function, then a bigger circle is drawn.  The argument represents the radius of the circle.

;; What happens if we give `circle` more that one argument

(circle)

(circle 20 30)


;; > Arguments must match the same number as the argument takes
;; >
;; > Different functions can take a different number of arguments

;; ### Understanding what a function does and what arguments it takes

;; Functions in Clojure are documented, so you can ask them what they do.  The documentation should also tell you what arguments that function takes.

(doc rectangle)


;; #### Exercise: Create other shapes by evaluating different functions

;; There are other shapes you can create, using `rectangle`, `square` and `triangle`.

;; Use the `doc` function to understand what each of those functions does and what arguments to provide

(doc )


;; #### Exercise: write some code to draw one of those shapes, giving the right number of arguments

()


;; # Combining functions

;; We can combine functions in several ways.

;; * Using a function as an argument

;; * Composing functions together (more details about this when we use `layer` function)

;; ## A Function always returns a value, so a function can be an argument too

;; When you evaluate a function, it always returns a value.

;; For example, if we evaluate the add function, the total of adding all those numbers together is returned.

(+ 1 2 3 4 5)


;; As functions take values as arguments, then you can use function as an argument to another function.

;; For example: We can calculate the size of our shape.

(circle (* 5 10))

;; Lets make a rectangle that is half high as it is wide

(rectangle 40 (/ 40 2))


;; ## Adding Colours

;; We can give our shapes colours by using the `colorize` function

;; The simplest way to use colours is by their name.  The name of a colour should be in a string, represented by double quotes, `" "`

;; Example colours are `"red"`, `"green"`, `"blue"`.

;; #### Exercise: create a shape and give it a colour

()


;; > We will see other ways to use colours later on in the course, such as using [Hue, Saturation, Lightness values](https://www.w3schools.com/colors/colors_hsl.asp).

;; # Making interesting pictures with position

;; We can also combine function calls to create groups of shapes. We do this by using the `layer` function.

(doc layer)


;; Shapes in layers all share the same coordinates, starting in the top/left corner and the shapes will overlap

(layer
  (colorize "blue" (square 50))
  (colorize "green" (circle 25)))


;; The `position` function allow you to draw a shape in a specific position, using co-ordinates.

(doc position)

(layer
  (colorize "springgreen" (circle 25))
  (position 50 25 (colorize "pink" (circle 25))))


;; Lets create a very simple [emoji](https://en.wikipedia.org/wiki/Emoji), in this case a surprised emoji.

(layer
  (colorize "yellow" (circle 40))
  (position 10 10 (colorize "black" (triangle 24)))
  (position 45 10 (colorize "black" (triangle 24)))
  (position 40 55 (colorize "black" (circle 10))))


;; #### Exercise: what kinds of shapes can you create with layer and position?  Can you create a simple house or pet?

(layer )


;; ####

;; ### Example: A halloween pumpkin

(layer
  (position 40 60 (colorize "orange" (circle 40)))
  (position 10 30 (colorize "black"  (triangle 24)))
  (position 45 30 (colorize "black"  (triangle 24)))
  (position 20 75 (colorize "black"  (rectangle 40 10)))
  (position 25 74 (colorize "orange" (rectangle 10 5)))
  (position 45 74 (colorize "orange" (rectangle 10 5)))
  (position 33 82 (colorize "orange" (rectangle 10 5)))
  (position 35 2  (colorize "black"  (rectangle 10 20))))



;; # Collections

;; So far we have seen simple values, such as  numbers like `1` and strings like `"blue"`.

;; We can have groups of values and use them to generate multiple shapes.

;; We are going to use a \*\*Vector\*\* to hold our collection.  A vector is defined with square brackets `[ ]`

;; We can put the size of squares we want in a collection and then create them

(map square [2 4 8 16 32 64 128])


;; > The `map` function helps us work with functions that do not take collections as arguments.  We will see more of `map` soon.

;; Here we have a collection of colours, represented as strings.  A string is defined using a pair of double quotes `" "`

["red" "orange" "yellow" "green" "blue" "indigo" "violet"]


;; > We can put any kind of value into a collection (even functions, but we will see that a little later).

;; ### Give a collection a name with `def`

;; We can give our collection a name.  This name represents the collection and we can use that name rather than type all the values of the collection everywhere we use it.

(def rainbow ["red" "orange" "yellow" "green" "blue" "indigo" "violet"])

;; A name can be evaluated and it will return the collection it is assigned to.

rainbow



;; ### Nested Collections

;; We can group simple values together in collections.

;; We can group collections of collections in a collection.

;; #### Example: a collection of elements from the periodic table

;; In the [periodic table of elements](http://www.rsc.org/periodic-table/) each element has a name and a symbol and a number.  We can create a vector for each element.

;; We created a collection for just some the elements

(def periodic-table-of-elements
  [["gold" "Au" 79]
   ["silver" "Ag" 47]
   ["platinum" "Pt" 78]
   ["palladium" "Pd" 46]
   ["Tennessine" "Ts" 117]])


;; > Clojure has many functions that are very good at working with different shapes of data.

;; ## Generating Shapes from nested collections

;; Using collections of values is very useful where a shape needs more than one argument

;; First we will create a little helper function for you

(defn draw-rectangle [[width height]]
  (rectangle width height))


;; > The above code is defining a function.  We will talk about defining your own functions soon
;; >
;; > In the function definition, we used [destructuring](https://clojure.org/guides/destructuring) to get the width and height values from each nested collection

(map draw-rectangle [[2 3] [4 6] [8 12] [16 24] [32 48] [64 92]])


;; ## What colours are available

;; We have already created a collection of colours for you called `color-names`.

;; Evaluating `color-names` shows you the details of all the colours we added for you.

color-names


;; How did we get the coloured shapes in the collection of colours?  Lets look at that and build our own collection.


;; ## Building our own collection

;; > Remember: Calling a function returns a value, so we can also include a function call as an element of a collection.

;; Lets see this by defining our favourite colours

;; Here is a very simple representation with two colours

(def favourite-colors-simple ["blue" "green"])


;; We can now refer to this collection by its name (don't forget to evaluate the code above first)

favourite-colors-simple


;; > Note:  We can add more colours to our collection and re-evaluate it with `Ctrl+Enter` or `Cmd+Enter` to update the value that `my-favourite-colours` refers to.


;; We also want to see what the colour looks like, so we can include a function that will draw a shape with that particular colour

(def favourite-colors
  [["blue" (colorize "blue" (square 25))]
   ["green" (colorize "green" (square 25))]])

favourite-colors


;; #### Exercise: Add your own favourite colours to the collection

;; ## Avoid duplicate code using Iteration

;; In the `favourite-colors`  collection we used two lines of code to draw a coloured shape that were very similar.  If we had a large number of colours in our collection, that would mean a lot of extra code.

;; If we needed to change the code that created the coloured shape then we would need to change a lot of our code

;; We can use a function to  [iterate](https://dictionary.cambridge.org/dictionary/english/iterate) over a collection and avoid duplication.  If we needed to make a change, then we would only need to change the function we used.

;; > The `map`, `filter` and `reduce` functions are often used to iterate over collections

;; ### The `map` function

;; The map function will take each value in turn from the collection.

;; The map gives that value to a function, evaluates the function with the value as an argument and keeps the result.

;; The map function keeps going until it has a new value for each of the existing values in the collection.

;; ![Clojure map function visualised](https://www.braveclojure.com/assets/images/cftbat/core-functions-in-depth/mapping.png "Clojure map function visualised")

;; Here is a simple example of `map` function.  Evaluate the code and see what result you get.

(map inc [1 2 3 4 5])


;; > Feel free to experiment with different numbers if it helps


;; #### A little helper function

;; Here is a function that we have written for you.

;; This function creates a new collection with a coloured square added to each value from the original collection, using the colour from each value for the square.

(defn color-visualised
  [color-name]
  [color-name (colorize color-name (square 25))])



;; > We will talk about defining functions soon, for now just think of them as a way to wrap up some behaviour that you want to call more than once.

;; Now we can use `map` with this helper function to create a new collection with a coloured shape as well as our original collection of colour names.

(mapv color-visualised
      favourite-colors-simple)


;; > We are using `mapv` rather than `map` as we specifically want to return our colours in a vector.

;; ## Any colour so long as its blue

;; We created another function for you, called `colors-named`, that shows you all the shades of a colour from the `color-names`.

;; For example, if you want all the shades of blue:

(colors-named "blue")



;; > How would you write some code to show just the shades of a colour?
;; >
;; > The `map` function could be helpful, however, there is a more specific function called `filter` that will make things simpler.

;; ## `filter` - its not just for coffee

;; The `filter` function is similar to the `map` function as it also iterates over a collection.

;; `filter` only returns values that match a certain criteria.  That criteria is defined by the function used with `filter`.

;; > The `filter` function uses a [predicate function](https://en.wikipedia.org/wiki/Predicate_(mathematical_logic)), one that returns true or false when given an argument.  Its a convention to put a question mark, `?`, at the end of the name of predicate functions because they are asking if something is true or false.

;; For example, `odd?` will return true if its argument is odd and false if its argument is even.

(odd? 1)


;; `odd?` only takes one argument.

;; So if we want to have just the odd numbers from a collection, we need help from another function that iterates over collections.

;; We have seen that `map` iterates over a collection, so we could try that

(map odd? [1 2 3 4 5 6 7 8 9])


;; Using `odd?` with `map` creates a collection of true or false values.  We wanted just the odd values themselves.

;; Lets use `odd?` with `filter`

(filter odd? [1 2 3 4 5 6 7 8 9])

(filter even? [1 2 3 4 5 6 7 8 9])


;; > Find more predicate functions on the [Clojure CheetSheet](https://clojure.org/api/cheatsheet).

;; ## Filtering our favourite colours

;; With help from `filter` we can now get specific shades of blue.

;; First, lets add some more colours (feel free to add your own colours)

(def favourite-color-shades
  ["blue"
   "lightskyblue"
   "darkslateblue"
   "midnightblue"
   "green"
   "springgreen"
   "lawngreen"
   "forestgreen"])


;; > Use the `color-names` or `color-named` functions to find the names of existing colour we have added.


;; Here is a helper function that will determine if a colour is a kind of blue, if you evaluate this function, then we can use it in our filter

(defn is-blue? [color]
  (clojure.string/includes? color "blue"))

(filter is-blue? favourite-color-shades)


;; But we do not have any coloured shapes in our result!

;; #### Adding coloured shapes into our filtered favourite colours

;; We have our new collection, that contains shades of the colour blue.

;; We can use map to create a new collection that also contains the coloured shapes, just like before.

(mapv color-visualised
      (filter is-blue? favourite-color-shades))


;; # Defining functions

;; [Defining functions](https://clojure.org/guides/learn/functions) allows you to create your own specific behaviour.

;; We have seen three function definition already

;; * `draw-rectangle`

;; * `color-visualised`

;; * `is-blue?`

;; We define a function using the `defn` function

(doc defn)



;; ![Clojure function definition structure](https://github.com/ClojureBridgeLondon/workshop-content-gitbook/blob/master/images/clojure-function-definition-structure.png?raw=true "Clojure function definition structure")

;; ## Define a function that draws a new shape

;; We can define a new function that creates a new shape using a combination of existing shapes.

;; We can create a [rhombus](https://en.wikipedia.org/wiki/Rhombus) shape by combining 2 triangle shapes.

(defn rhombus
  "Draws a shape of a star of a given size"
  [size]
  (layer
    (triangle size)
    (position (/ size 2) 0 (rotate 180 (triangle size)))))



;; > `rotate` will rotate a shape by the given number of degrees

;; Lets evaluate our `rhombus` function to see the results (don't forget to evaluate your `rhombus` function definition first).

(rhombus 100)



;; #### Exercise: create different colours of rhombus

;; > Hint: change the function definition to also have a `color` as an argument and add a `fill` or `colorize` functions to the body

(defn )



;; # Building more complex shapes

;; Lets have some more fun with shapes.

;; The `beside` function will put shapes beside each other

(doc beside)



;; If we give `beside` several shapes, it will draw them beside each other.

(beside (circle 50) (circle 25))


;; Rather than have lots of calls to the `circle` function, we can create a collection of sizes of circles we want to draw next to each other.

(def my-circle-sizes [50 25 42 64 18 21 47])



;; We can just `map` the `circle` function over this collection as we have before, but this draws the circles on different lines

(map circle my-circle-sizes)


;; If we want all the circles in a straight line, we can use `beside`.

(beside (map circle my-circle-sizes))



;; ### Rotating shapes

;; We can use `beside` and a helper function to draw a rotating shape.

;; The shapes will change the 'hue' (colour) of each shape drawn as well as rotate the shape a little.

;; Here is a simple function to rotate a shape by a given [degree](https://en.wikipedia.org/wiki/Degree_(angle)).

;; A complete rotation would be 360 [degrees](https://en.wikipedia.org/wiki/Degree_(angle)).

(defn rotating-shapes [change-value]
  (rotate change-value (rectangle 30 50)))



;; We now use `map` with our `rotating-shapes` function to create a series of rotated shapes.

;; Those shapes are given to the `beside` function to space them out evenly.

(beside
  (map rotating-shapes (range 0 360 15)))


;; Oh, the shapes do blur into each other, so lets create another function to change the colour as we rotate.

;; We will use `fill` to add the hue, saturation and light colour value to each shape.

(defn rotating-shapes-and-colors [change-value]
  (fill (hsl change-value 90 45)
        (rotate change-value (rectangle 30 50))))


;; Lets create the sequence of shapes again.

(beside
  (map rotating-shapes-and-colors (range 0 360 15)))


;; The colours are still blurring into each other when they are a similar shade.  If we make the shapes partially transparent, then we can see all the shapes

(defn rotating-shapes-and-colors-semi-opaque [change-value]
  (fill (hsl change-value 90 45)
        (opacity 0.5
                 (rotate change-value (rectangle 30 50)))))


;; Draw the sequence of shapes one more time...

(beside
  (map rotating-shapes-and-colors-semi-opaque
       (range 0 360 15)))



;; # Additional common functions

;; ## Narrowing down results with `reduce`

;; We have used `map` and `reduce` functions to work with collections.

;; What if we want a total (aggregate) of all the information in a collection? Or find the biggest or smallest number in a collection.

;; We can use `reduce` to narrow down an answer, using another function.

;; ### Finding the smallest / largest circle

;; If we draw a range of circles, how do we know which is the biggest or smallest?

;; Using `map` and `circle` functions we can create a collection of circles, but can we easily tell which is the biggest and which is the smallest by looking at the results?

(map circle [30 20 42 60 22 42 38 62 24])



;; Hmm, seems a little tricky to know just by looking at the circles.

;; If we have a lot more circles, then it would also take time to work it out.

;; If we just look at the circle sizes in the collection we can read through them all and see which is the biggest and smallest.

;; Or we can use another function to help us.

;; Lets use `min` to find the smallest number

(min [30 20 42 60 22 42 38 62 24])



;; `min` does not give us the correct answer if we give it a collection as an argument, it needs individual values

;; Here is how `min` likes to work

(min 30 20 42 60 22 42 38 62 24)


;; Is there a way to use `min` with the collection though?

(map min [30 20 42 60 22 42 38 62 24])


;; `map` returns a collection, so in this case its not the function we want.

;; `reduce` will aggregate all the numbers in the collection, using the function we use with it.  So if we use `reduce` with `min` we get a single answer

(reduce min [30 20 42 60 22 42 38 62 24])


;; And the same for a maximum value

(reduce max [30 20 42 60 22 42 38 62 24])


;; Now we can draw just the biggest circle

(circle (reduce max [30 20 42 60 22 42 38 62 24]))



;; # Animation with Cells

;; ## Introducing `cell`

;; What is a cell, you may ask? Well, at the most basic level, a `cell` is a thing that:

;; * has a value,

;; * can change over time,

;; * wraps a bit of code, which tells the cell what to do every time it runs.

;; ## Your First Cell 🤠

;; Consider `interval`, a special function that resets the value of a cell repeatedly, on a timer. Given `interval`, here is a 'cell' that counts upward to infinity, one second at a time:

(cell (interval 1000 inc))



;; > An interval of `1000` is equal to `1` second.
;; >
;; > An interval of `250` is equal to 1/4 of a second.

;; Cells can have names, which we define using `defcell`. Let's re-make that timer cell, but this time we'll define a name for it.

(defcell counter (interval 1000 inc))



;; By giving a cell a name, we can re-use it in other cells. This gives us tremendous power. Watch:

(defcell bigger-counter (* @counter 10))



;; Do you see how `bigger-counter` is changing every second, even though it doesn't use `interval`? That's because `bigger-counter` automatically refreshes whenever `@counter` changes, multiplying that value by 10 to create its own value.

;; This kind of cell-to-cell awareness makes cells special. It means they can be formed into a giant web, each cell ready to act together with the others. Each cell in the web stays in sync with whatever other cells it is connected to.

;; You may be wondering what that **@** (the "at sign") is doing. We use the @ symbol to ‘dereference’ a cell, so that we can read its value. Evaluate this, wait a few seconds, then evaluate it again. (Make sure you evaluate the *whole* expression by using `Shift-Command-Enter`.)

@counter



;; Note how `@counter` doesn’t change: it is frozen in time. When you @dereference a cell, you don’t have the whole ‘cell’ anymore - you get a snapshot of its value, at one point in time.

;; If we wrap `@counter` in another cell, we’ll see it change again:

(cell @counter)



;; Every cell in your program dutifully keeps track of which other cells it depends on, and updates when they change. Only *cells* know how to react to other cells automatically: ordinary Clojure code doesn’t update like that.

;; ##

;; # Create some simple animations

;; We can use the `cell` function to animate our shapes, by calling a function that

;; ## Animate a simple square

(let [palette ["red" "orange" "yellow" "green" "blue" "indigo" "violet"]]

  (cell
    (interval 250 #(colorize (rand-nth palette)
                             (square 50)))))



;; > `let` function is similar to `def` in that it associates a name with a value (including a collection or even a function call)

;; ## Animated flower petals

;; First we define a function that draws a flower.

;; This function chooses a random colour each time it creates a petal for the flower.

(defn flower []
  (let [leaves ["hotpink" "blue" "red" "darkturquoise" "mediumvioletred"]]
    (layer
      (position 300 60
                (colorize (rand-nth leaves) (circle 60)))
      (position 150 200
                (colorize (rand-nth leaves) (circle 60)))
      (position 450 200
                (colorize (rand-nth leaves) (circle 60)))
      (position 300 350
                (colorize (rand-nth leaves) (circle 60)))
      (position 200 100
                (colorize (rand-nth leaves) (circle 60)))
      (position 400 100
                (colorize (rand-nth leaves) (circle 60)))
      (position 200 300
                (colorize (rand-nth leaves) (circle 60)))
      (position 400 300
                (colorize (rand-nth leaves) (circle 60)))
      (position 290 410
                (colorize "forestgreen" (rectangle 25 270)))
      (position 215 500
                (colorize "forestgreen" (circle 80)))
      (position 390 500
                (colorize "forestgreen" (circle 80)))
      (position 300 200
                (colorize "yellow" (circle 115))))))


;; If we call the flower by itself, it is not animated as it only draws once.

;; > If you press `Ctrl+Enter` or `Cmd+Enter` repeatedly on `flower` you can manually animate the flower.

(flower)


;; If we put the call to `flower` in a `cell` function and set an `interval` you can animate the flower.

(cell (interval 250 flower))



;; # Challenge: create your own animation

;; Think of picture or shape you can draw and animate.

;; Some ideas are

;; * A picture with a house, animating the curtains or if there is a sky in the picture you can animate a sun / moon rising and falling.

;; * A bee with an animated body

;; ### Example: My House

;; Draw a house using the shapes, colours and layer functions we have created for you.  Your house may be in the country side, so their could also be trees and grass.  You could also create sky with sun or clouds.

;; You could try animate the house by

;; * showing doors or curtains opening and closing

;; * the sun and sky changing colour, the sun sinking and being replaced by the moon

;; ### Example: Create a bee - some ideas (not complete)

;; Create a bee from the shapes we have

(circle 50)

(colorize "yellow" (circle 50))


;; We will create three layers for the bee

;; * `bee-head` is a circle with a face

;; * `bee-tail` is a circle with a triangle as a stinger

;; * `bee-body` is a series of rectangles that will be alternative colours, using cycle to alternate between black and yellow, taking enough colours each time for the body (starting with the opposite colour than last time)

;; Each layer is defined with a name using the `let` function.

;; Then `layer` is used to combine all the layers into one drawing

(beside (map (fn [color] (colorize color (rectangle 30 100)))
             (take 5 (cycle ["black" "yellow"]))))

(defn bee-part []
  (let [body-size 5
        bee-body-colors
        (take body-size
              (cycle ["black" "yellow"]))
        bee-body  (layer
                    (beside
                      (map
                        (fn [color]
                          (colorize color
                                    (rectangle 30 100)))
                        bee-body-colors)))
        ] ;; end of bee components
    ;; assemble all the bee components
    (layer
      bee-body
      )))

(bee-part)

(defn bee []
  (let
      [bee-tail (layer
                  (position 50 50
                            (colorize "yellow" (circle 50)))
                  (position 0 10 (triangle 15)))
       bee-head (layer
                  (position 250 50
                            (colorize "yellow" (circle 50))))
       bee-face (layer
                  ;; eyes
                  (position 235 40
                            (circle 10))
                  (position 265 40
                            (circle 10))
                  ;; mouth
                  (position 220 60
                            (rectangle 20 10))
                  (position 230 70
                            (rectangle 40 10))
                  (position 260 60
                            (rectangle 20 10))
                  )

       body-size       5
       bee-body-colors (take body-size (cycle ["black" "yellow"]))

       bee-body (layer
                  (beside
                    (map
                      (fn [color]
                        (colorize color
                                  (rectangle 30 100)))
                      bee-body-colors)))

       ] ;; end of bee components

    ;; assemble all the bee components
    (layer
      bee-tail
      bee-head
      bee-face
      bee-body

      ;; fix bee head being cut in half by drawing an invisible bee head shape
      (position 250 50
                (opacity 0 (circle 50)))
      )))



;; Cells to animate

(cell (interval 250 bee))



;; Use layers to help.  The order in which you add layers will help

;; Add some randomness to the bee colours to make a simple animation.  For example, if you bee has black and yellow striped body, then change your function to iterate through black an yellow for each stripe to animate the bees body.

;; Or if you bee has wings, change the position of the wings to animate the bee as if it were flying

;; To animate, you can put the bee function inside a cell and set an interval to redraw the bee.
