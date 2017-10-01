;; # What if?

;; What if we want to call one function for handling dogs 🐶 and another function for handling fish 🐟️? Enter the `if` statement.

;; We use `if` to choose between two different paths for our programs to go. It's amazing. What you do is give `if` three things, in this order:

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

;; Before we go any further, there's a trick about `if` that you should know: it's not like those other expressions you use. It's special. That's not just bravado: it's *literally* a "[special form](https://cljs.github.io/api/cljs.core/if)", not a function like almost everything else we use. See for yourself:

(what-is if)

;; Functions are the building blocks of using Clojure. Special forms are the building blocks used to make Clojure itself. This doesn't change how we use special forms–we still call them like functions–but it’s important to at least be aware of how our tools work "under the hood". (If you want some advanced reading, you can check out the actual source where `if` is built in [Clojure](https://github.com/clojure/clojure/blob/master/src/jvm/clojure/lang/Compiler.java#L2703) and [ClojureScript](https://github.com/clojure/clojurescript/blob/r1.9.908-12-g998933f/src/main/clojure/cljs/analyzer.cljc#L1347-L1359).)

;; ## `if` Sandbox

;; Now let’s get a feel for `if` by evaluating some examples. We’ll use some functions that might be new, so if you’re not sure what something is, use your Utility Belt (`what-is`, `doc` (and `Command-i`), experimentation, and slow careful research) to find out.

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

;; ## "Logical Truth"

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

;; ## Other species of `if`

;; There are lots of ways we might want to choose different paths for our code. `if` is the most basic, and everything else is based on it, but the others are useful specializations. They can be more concise, cover more situations, and they’re especially helpful for clearly communicating what you expect and what you want to happen.

;; (There's one trick to be aware of with these: none of them are functions. They're not special forms, either. See, special forms are fundamental building blocks for creating Clojure from scratch. These don't need to be made from scratch because they're all based on `if`. But they can't be functions, either, because they *rearrange your code* behind the scenes, and functions can't work like that. Because these tools provide ways to branch your code that only evaluates functions under certain conditions, they need to be [macros](https://clojure.org/reference/macros), which are an topic for another day. But, like the `if` special form, the way we use them still looks like calling a function.)

;; #### `when`

;; Sometimes we have situations where we want an `if`, but nothing needs to happen in the `else` condition. Technically it’s possible to write that as an `if` without the `else`, but it’s not considered good form:

(if (number? 1.8)
  "do some stuff with this number")

;; The accepted practice of writing Clojure for this situation is to use `when`, which is like a single-branch `if`. This makes it more clear that you don’t expect to do anything if the test is falsey.

(when (number? 1.8)
  "do some stuff with this number, and don’t worry if it's not a number")

;; There are other reasons to use `when` covered in the [Clojure Style Guide](https://github.com/bbatsov/clojure-style-guide#when-instead-of-single-branch-if).

;; Notice

;; #### "*Twenty* roads diverged in a yellow wood..."

;; Sometimes you need to choose between more than two different paths for your code. Again, it's technically possible to handle that with a bunch of nested, intermingled `if`s, but that is inelegant. Clojure provides us with ways to cleanly handle multiple possibilities: `cond`, `case`, and `condp`.

;; The most general is `cond`. It takes a bunch of pairs. Each pair has a test expression and a result expression. This handles the most complex hairballs of branching possibilities.

;; FIXME
(cond
  (raining? my-city))


;; If all the branches of your code depend on just checking if an expression is equal to some known values, `case` is perfect. For example, say we wanted to write a generic where-to-put-your-new-pet function. Before we would define it `defn`, we would play around with the code first:

(let [pet :fish]
  (case pet
    :fish    "tank with water"
    :dog     "big yard"
    :cat     "foot of your bed"
    :hamster "tank with woodchips"))

;; What happens when we put in a value that we don’t handle, like `:spider`? How could we prevent that? Research and experiment.

;; If all your outcomes depend on comparing a value to some possibilities, and the comparison isn’t just equality, use `condp`. For instance, play with the years-ago value here, and read the docs for `condp` to figure out how it works:

(let [years-ago 600]
  (condp < years-ago
    15000000000 "Our galaxy didn't exist yet"
    5000000000  "Our planet didn't exist yet"
    5000000     "Humans didn't exist yet"
    5000        "Writing didn't exist yet"
    500         "Computers didn't exist yet"
    50          "Clojure didn't exist yet"

    "Sounds pretty recent"))

;; Don’t forget to check out the `:>>` technique, described in the documentation.
