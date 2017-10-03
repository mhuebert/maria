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

;; Functions are the building blocks of using Clojure, but special forms are the building blocks used to make *Clojure itself*. This doesn't change how we use special forms–we still call them like functions–but it’s important to at least be aware of how our tools work "under the hood". (If you want some advanced reading, you can check out the actual source where `if` is built in [Clojure](https://github.com/clojure/clojure/blob/master/src/jvm/clojure/lang/Compiler.java#L2703) and [ClojureScript](https://github.com/clojure/clojurescript/blob/998933f5090254611b46a2b86626fb17cabc994a/src/main/clojure/cljs/compiler.cljc#L491).)

;; ## Playing in the Sandbox

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

;; Try building one or two of your own `if` statements.

;; ## Truthiness

;; One funny thing you might notice about `if` is that it treats nearly every value for `test` as logically true:

(if 1968
  "logical true"
  "logical false")

(if "Fela Kuti"
  "logical true"
  "logical false")

(if []
  "logical true"
  "logical false")

(if 0
  "logical true"
  "logical false")

;; Zero, empty collections, strings–they all count as "logical true"! So what do we have to do to get `if` to evaluate the `else` expression? What’s *not* logical true?

;; It’s very specific: the *only* thing considered "logical false" is `false` and one other special value `nil`, which means "nothing":

(if false
  "logical true"
  "logical false")

(if nil
  "logical true"
  "logical false")

;; Everything else evaluates to "logical true". The way we Clojure programmers usually talk about this is best explained by [The Joy of Clojure](http://www.joyofclojure.com/), a book about how to think "the Clojure way":

;; >In Clojure `nil` and `false` are considered "false" and therefore we say they are both "falsey"... All non-falsey values are considered "truthy" and evaluate as such.

;; Truthy, falsey, almost everything being logically true–this might all seem weird (OK, it is weird! 🤡) but it’s handy for things to work this way. A broad definition of "truthiness" makes it simple to build tests, and calling things "truthy" and "falsey" is super helpful. They’re a clear, quick way to make sure we remember the difference between the *value* `true` and how `if` and its friends treat values. This comes up when we read code to ourselves or other people: we’ll say something like "then we check such-and-such expression, and if it’s truthy, we do so-and-so". This is better than saying "we check if such-and-such expression is true", because the expression isn’t just checking for `true`, but all sorts of other "truthy" values.

;; Give it some practice by reading these expressions. Really read them out loud, with your voice, to get used to truthy and falsey:

(if (+ 10 -10)
  "truthy"
  "falsey")

(if ""
  "truthy"
  "falsey")

(if (number? "5")
  "truthy"
  "falsey")

(if (- 5 6)
  "truthy"
  "falsey")

(if "false"
  "truthy"
  "falsey")

;; ## Other species of `if`

;; There are lots of ways we might want to choose different paths for our code. `if` is the most basic, and everything else is based on it, but the others are useful specializations. They can be more concise, cover more situations, and they’re especially helpful for clearly communicating what you expect and what you want to happen.

;; (You should be aware of one strange property of these tools we’re about to cover: none of them are functions. They're not special forms, either. They're all [macros](https://clojure.org/reference/macros), which are a topic for another day. The way we use macros for now isn’t any different from calling a function.)

;; #### Only evaluate in case of fire: `when`

;; Sometimes we have situations where we want an `if`, but nothing needs to happen if the `test` evaluates to falsey. Technically it’s possible to write that as an `if` without an `else` expression, but it’s not considered good form:

(if (number? 1.8)
  "do some stuff with this number")

;; The accepted practice for writing Clojure in these situations is to use `when`, which is like a single-branch `if`. This makes it more clear that you don’t expect to do anything if the test evaluates to falsey:

(when (number? 1.8)
  "do some stuff with this number, and don’t worry if it's not a number")

;; There are other reasons to use `when` covered in the [Clojure Style Guide](https://github.com/bbatsov/clojure-style-guide#when-instead-of-single-branch-if).

;; ### "*Twenty* roads diverged in a yellow wood..."

;; Sometimes you need to choose between more than two different paths for your code. Again, it's technically possible to handle that with a bunch of nested, intermingled `if`s, but that is inelegant. Clojure provides us with ways to cleanly handle multiple possibilities: `cond`, `case`, and `condp`.

;; #### Infinite possibilities with `cond`

;; The most general is `cond`, the conditional expression. which takes a bunch of pairs. Each pair has a test expression and a result expression that gets evaluated if the test is truthy.

(doc cond)

;; `cond` handles the most complex hairballs of branching possibilities, like this decision-maker that returns a [keyword](https://clojure.org/reference/data_structures#Keywords) describing how to spend your day depending on four separate conditions that interact. Play with the `true`/`false` values in the `let` to make sure the logic is correct:

(let [raining? true
      weekday? true
      holiday? false
      sick?    true]
  (cond
    (and weekday?
         (not holiday?)
         (not sick?))              :go-to-work
    (and (or (not weekday?)
             holiday?)
         (not sick?)
         (not raining?))           :play-outside
    (or sick?
        (and raining?
             (or holiday?
                 (not weekday?)))) :read-a-book))

;; (If you haven’t seen them before, keywords are symbolic identifiers that we use in our programs to represent things. They always start with a colon, like `:i-am-a-keyword`, and they’re a great data type to use for cross-referencing things.)

;; #### Different strokes for different folks with `case`

;; If all the branches of your code depend on just checking if an expression is equal to some known values, `case` is perfect. For example, say we wanted to write a generic where-to-put-your-new-pet function. Before we would define it `defn`, we would play around with the code first:

(let [pet :fish]
  (case pet
    :fish    "tank with water"
    :dog     "big yard"
    :cat     "foot of your bed"
    :hamster "tank with woodchips"))

;; What happens when we put in a value that we don’t handle, like `:spider`? How could we prevent that? Research and experiment.

;; #### Check yourself with `condp`

;; If all your outcomes depend on comparing a value to some possibilities, and the comparison isn’t just equality, use `condp` (named for "conditional predicate"). For instance, play with the years-ago value here, and read the docs for `condp` to figure out how it works:

(let [years-ago 600]
  (condp < years-ago
    15000000000 "Our galaxy didn't exist yet"
    5000000000  "Our planet didn't exist yet"
    5000000     "Humans didn't exist yet"
    5000        "Writing didn't exist yet"
    500         "Computers didn't exist yet"
    50          "Clojure didn't exist yet"
    "Sounds pretty recent"))

;; Don’t forget to check out the `:>>` technique, described in the `condp` documentation.
