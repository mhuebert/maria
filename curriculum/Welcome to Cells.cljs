;; # Say Hello to Cells

;; Welcome! On this page, we are going to introduce something called a ‘cell’, which will help you make code come alive.

;; What is a cell, you may ask? Well, at the most basic level, a `cell` is a thing that:

;; * has a value,
;; * can change over time,
;; * wraps a bit of code, which tells the cell what to do every time it runs.

;; ## Your First Cell 🤠

;; Consider `interval`, a special function that resets the value of a cell repeatedly, on a timer. Given `interval`, here is a 'cell' that counts upward to infinity, one second at a time:

(cell (interval 1000 inc))

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

;; ## Interconnected Cells 🌎️🌍️🌏️

;; Let's make a more tangled web of cells, to better see this interconnectedness in action.

;; We'll start with a cell that is a random number generator:
(defcell random-number []
  (interval 200 #(rand-int 20)))

;; The `#` ("hash" or "pound sign") immediately before an open-parenthesis might be new to you. Go ahead an evaluate that subform, `#(rand-int 20)`, and you'll see it returns a function. That's all the `#` does: it's a quick way to define an anonymous function. This [shorthand](https://clojure.org/guides/weird_characters#__code_code_anonymous_function) is no different from `(fn [] ...)` except the arguments get automatic names like %1 and %2.

;; Now we have a cell that updates itself every fifth of a second. Let's tinker with it a bit. Those numbers go by so fast--change the cell to slow it down. (Or, if you're a jet-pilot kind of programmer, speed it up! ⚡️ 😀)

;; That `random-number` cell is the first part of our cell chain. Next we'll create a cell that keeps track of the last 10 random numbers generated, using the `random-number` cell:
(defcell last-ten [self]
  (take 10 (cons @random-number @self)))

;; The `last-ten` cell works by building up a list one at a time with `cons`. (If you're not familiar with `cons`, take a minute using `doc` (press `Command-i`) to get to know it.) The value that `cons` adds to the list comes from looking at the *current* value of the `random-number` cell, which we get by dereferencing it.

;; (Bonus 'how it works' puzzle: what would happen if we `take` a different number from the list, or if we didn't `take 10` at all? Think about it, then experiment.)

;; Often it's easier to think about numbers if we can make them more real. Let's do that by visualizing our last ten random numbers into shapes:
(defcell squares []
  (map square @last-ten))

;; Notice how you can see each number-as-a-square move across the list as it grows old. Notice also how `squares` uses one cell, which uses another cell, and that all those dependencies are handled *automatically* by your friend the computer.

;; Play around with those squares to see how else to visualize our random numbers. Might different shapes look cool? How could you add color? What would you have to do to assign each number a color that sticks with its shape? Experiment.

;; ## Talking to Cells 📢 🗣️

;; We can also **interact** with cells. Just like we used cells to track time using `interval`, we can use cells to detect user activity. We do this by "listening" for specific browser "events", such as a mouse click.

;; Let's build some cells that we can make do things. How about this: one cell will act as our "light switch", controlling whether or not the other cells get "power" (which for us will be color). The other cells will check that "light switch" cell and do different things based on its value. Then we'll make our "light switch" cell *clickable* so we can interact with it.

;; To do all that, we first need to go over how to evaluate different code based on certain conditions. We’ll start with `if`, which is [special](https://clojure.org/reference/special_forms#if) and weird but fairly simple to use. What you do is give `if` three things, in this order:

;; 1. a test
;; 2. what to do if the test evaluates to "logical true"
;; 3. what to do if the test evaluates to "logical false"

;; That’s it. For instance:
(if true
  "a"
  "b")

(if false
  "a"
  "b")

;; (`true` and `false` are special values in Clojure, used when we need a clear [Boolean](https://en.wikipedia.org/wiki/Boolean_data_type) truth value. Both `true` and `false` are [literal values](https://clojure.org/reference/reader#_literals), and don't need to be wrapped in double-quotes.)

;; Here’s a diagram to explain the pieces of those `if` expressions:

(layer
 (position 40 60 (text "(if (tired? you)"))
 (position 80 80 (text "(nap \"20 minutes\" you)"))
 (position 80 100 (text "(code-something-fun you))"))
 (colorize "grey" (rotate -90 (position 310 70 (triangle 10))))
 (position 330 80 (text "if test passes, evaluate this"))
 (position 130 25 (rotate 60 (colorize "grey" (triangle 10))))
 (position 115 20 (text "test"))
 (colorize "grey" (position 200 110 (triangle 10)))
 (position 50 140 (text "if test is false or nil, evaluate this")))

;; Now let’s get a feel for `if` by evaluating some examples. We’ll use some new functions, so if you’re not sure what something is, use your Utility Belt (`what-is`, `doc` (and `Command-i`), and experimentation) to find out.

(if (= 1 1)
  "equal"
  "not equal")

(if (= 1 2)
  "equal"
  "not equal")

(if (vector? ["Bert" "Ernie"])
  "vector (of Sesame Street characters)"
  "not a vector")

(if (string? "Catherine the Great")
  "yes the royal is a string"
  "not a string")

;; Notice that nearly every value for the `test` in `if` is considered "logically true". That means `if` considers collections and other values "truthy", even if they’re empty:

(if 1968
  "any number counts as true"
  "try another number, but trust me, I won't get evaluated!")

(if "Fela Kuti"
  "strings are truthy too"
  "logical false")

(if []
  "empty vectors count as logical true"
  "I never get evaluated :(")

;; So what do we have to do to get `if` to evaluate the `else` expression? What’s NOT truthy? It’s very specific: the *only* thing that is considered "logical false" is `false` and one other special value called `nil`, which means "nothing". Everything else `if` evaluates the `then` expression.

(if nil
  "logical true"
  "logical false")

;; Everything else–strings, numbers, any sort of collection–are all considered "truthy". This might seem weird (OK, it is weird! 🤡) but this broad definition of "truthiness" is handy.

;; Anyway, we brought up `if` to make some cells that talk to each other. The idea was to make "light switch" cell, and some other cells that do different things based on that "light switch" cell’s value. With `if` in our toolbox that will be simple. First, we make the "light switch" cell, which we’ll call `toggle` because it toggles back and forth between two values, `true` and `false`, for "off" and "on". It will start "off":

(defcell toggle false)

;; This cell doesn't do much: it's just a container for `false`. But because it's a cell, we can use it for so much more than if we defined it as a plain old `true` value on its own. As a cell, when it changes it notifies other cells that depend on it.

;; Now we build an `if` expression that draws a circle if our cell is true–actually, if it’s "truthy"–and a square if it’s not. First, let's see how works **without** cells talking to each other. What we'll do is check the value of `toggle`, and draw a circle if it's positive, and a square if it's negative.
(if @toggle
  (circle 40)
  (square 80))

;; We defined `toggle` as `false`, our `if` makes a square when `toggle` is false, so we get a square. Fine enough.

;; But here’s the thing: go back and change `toggle` to `false` and re-evaluate it. Our square stays a square! Our `if` stopped paying attention to `toggle`. We want our `if` to draw a circle whenever `toggle` is `true`, but for that to happen we have to re-evaluate our `if`. (Try it.) Our `if` has no idea what’s going on with `toggle` because it was evaluated in the past. That’s a bummer. ☹️

;; Let’s try the same thing, but now inside a cell. Remember, cells keep track of each other.
(cell (if @toggle
        (circle 40)
        (square 80)))

;; Whatever `toggle` is right now, we should get the right shape. Here’s the difference: go and change `toggle` again. Our shape changes in the *other* cell! 🤗 😎 This is powerful. All we need to do now is add some interaction with the user–that’s you! This requires three new ideas at once: event listeners, [keywords](https://clojure.org/reference/data_structures#Keywords), and swapping values. These three are all super helpful tools, and we’ll explain each one in turn, so get ready.

;; The way we detect mouse clicks and other users actions in a browser like Safari, Firefox, Internet Explorer, or Chrome is to listen for them as [events](https://developer.mozilla.org/en-US/docs/Web/Events). We have a ready-made function to do that called `listen`:

(doc listen)

;; The documentation for `listen` tells us it takes three arguments: the `event` to listen for, a `listener` function that gets called when the event is "heard", and a `shape` to draw.

;; The first argument to `listen`, the event to listen for, needs to be a "keyword" that matches a [known event](https://reactjs.org/docs/events.html#supported-events) like FIXME "onClick". Keywords are symbolic identifiers that we use in our programs to represent something. They always start with a colon, like `:i-am-a-keyword`, and they’re a great data type to use for cross-referencing things. We’ll make a listener called `light-switch` that will listen for the `:click` event.

;; This part is cool enough that we should define a new `toggle` so you can see them side-by-side.

(defcell toggle2 false)

(defcell light-switch
  (listen :click
          (fn [] (swap! toggle2 not))
          (if @toggle2
            (circle 40)
            (square 80))))

;; Here's the magic: click on the result. Look at the value of `toggle2`. Now click again. They're connected! Let's dive into how it works. There's a bunch going on.

;; Notice the second argument to `light-switch` is an anonymous function with no arguments. (If you want some extra practice, take a minute and change this line from the `(fn [] ...)` style to the `#(...)` style for writing anonymous functions.)

;; This function argument uses a function we haven’t seen before: `swap!`. The `swap!` function is how we update the value of a cell from outside its original definition. (If you’ve worked with Clojure atoms, it’s the same thing here. Cells aren’t atoms but they work the same in this respect.) The oh-so-energetic exclamation point is a warning. Normally, functions are very direct: you give them something, they give you something back, that’s it. The only thing that happens is you get a return value based on the function you called and the arguments you called it with. Everything happens in plain sight.

;; But functions with exclamation points–they break that pattern. They’re not so simple. The exclamation point is a warning from whoever wrote the function that you call it with arguments, and you don’t just get a return value, *something changes somewhere else*. 😱 These are called "side effects". Things get harder to think about with side effects. When something happens in a program with side effects, you’ll have to ask, "How did this value get this way?", or "What changed? Where?", and it can be hard to track down the answer. Side effects are powerful, but they require great care, and they should be used only when absolutely necessary. I entrust you now with their power.

;; In this case, the side effect is cell interaction itself. Each cell has a value, and when another cell needs to change that value, `swap!` is how they do it. The `swap!` function takes a cell name and a function, and instead of merely returning something based on those values, it reaches over into the named cell, gets its value, applies the given function to that value, and does two things with the result: sets the value of the cell to that, and returns it too.

;; Now, we have cells that aren't just connected to each other, but that we can click and change whenever we want. 👍🏼



;; ## Making Web Pages


(-> % (.-currentTarget) (.-value) int)


;; TODO

(defcell circle-size 16)

;; XXX `html`
;; XXX hiccup
;; XXX JS interop
;; XXX threading
(cell (html [:div
             [:h2 "Adjustable Circle"]
             [:input {:type "range"
                      :on-input #(reset! circle-size (-> % (.-currentTarget) (.-value) int))}]
             [:div (colorize "aqua" (circle @circle-size))]]))

;; Note that both the `circle-size` cell's reported value and the circle itself change when you move the slider. This happens because the function attached to on-input is `reset!`ing the `circle-size`, which then communicates that change to every cell that depends on it.

;; We can use this same mechanism to store reactive state for an application, for example by placing a map with multiple entries in a cell:


(defcell some-state
  {:size 20
   :colors ["magenta" "cyan" "yellow"]})

;; ... then assigning some behavior that updates what's in the state map. For example, when you click on this circle it will change its size and color:

(cell (->> (colorize (first (@some-state :colors))
                     (circle (@some-state :size)))
           (listen :click #(swap! some-state assoc :size (+ 20 (rand-int 10))
                                  :colors (shuffle (@some-state :colors))))))







;; FIXME ## Data From Space 🚀
;;
;; Just like `interval`, there is another special function called `fetch` which only works inside cells. Given a URL, `fetch` can download data from the internet:

(defcell location (geo-location))

(defcell birds
  "An options map including :query params may be passed
  as the second arg to fetch."
  (fetch "https://ebird.org/ws1.1/data/obs/geo/recent"
         {:query {:lat (:latitude @location "52.4821146")
                  :lng (:longitude @location "13.4121388")
                  :maxResults 10
                  :fmt "json"}}))

(cell (map :comName @birds))

(defn find-image [term]
  (let [result (cell term (fetch "https://commons.wikimedia.org/w/api.php"
                                 {:query {:action "query"
                                          :origin "*"
                                          :generator "images"
                                          :prop "imageinfo"
                                          :iiprop "url"
                                          :gimlimit 5
                                          :format "json"
                                          :redirects 1
                                          :titles term}}))]
    (some->> @result
             :query
             :pages
             vals
             (keep (comp :url first :imageinfo))
             first)))

(defcell bird-pics
  (doall (for [bird @birds]
           (some->> (:sciName bird)
                    (find-image)
                    (image 100)))))

(cell (image (find-image "berlin")))

;; XXX for more on fetching, see https://maria.cloud/gist/f958a24f0ece6d673bce574ec2d3cd71


;; TODO Loop example


;; TODO wrap up with conclusion
