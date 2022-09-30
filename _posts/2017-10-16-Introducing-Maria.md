## Maria: a beginner-friendly coding environment for Clojure

*Matt Huebert, Dave Liepmann, and Jack Rusher*

We'd like to introduce a tool we built for Clojure beginners. To understand why we built it, join us for a moment on a mental exercise.

Take a deep breath, close your eyes, and imagine that you're new to Clojure. In fact, imagine you're entirely new to programming, but you heard Clojure is cool. You of course want to program with the cool stuff, so off you go to learn Clojure. Have you got that picture painted in your mind's eye?

Open your eyes.

![Leiningen install instructions](http://daveliepmann.com/maria/lein.png)

Welcome to Clojure! I hope you know about `$PATH` and `chmod`. Remember that for lots of folks even the *existence* of the command line is new.


## Emacs & CIDER
![Emacs CIDER install instructions](http://daveliepmann.com/maria/cider.png)
Then you need an editor, and emacs is the best, so...good luck figuring that out.

I love emacs, but it's a long road to a running REPL. So maybe you use something with a gentler on-ramp...

## Nightcode
![Emacs CIDER install instructions](http://daveliepmann.com/maria/nightcode.png)

Nightcode is good plug-and-play software (like Cursive), but it's an IDE.

Where do you even start to make sense of this?

Say we persevere, and actually get to write some code. Like everyone, we make a mistake...

## Stack traces
![Emacs CIDER install instructions](http://daveliepmann.com/maria/stacktrace.png)

...that's not the clearest error message!

* When do we start /programming?/

Remember, we came to play with Clojure code! To make things! When do we get to do _that_?

## Learning Clojure is a _lot_
  - the command line
  - build tooling
  - a new editor
  - the host platform
  - dependencies and packages
  - stack traces!
  - lispy syntax
  - functional programming
  - immutability
  - and...
  - and...
  - and...

We must respect the number of topics involved in learning Clojure, just like any programming language.

All of these are necessary for professional work, of course. The problem is, they have to learn these /all at once/!

The time will come when they need to learn to read a stack trace, but that time is not their first day.

How are these new topics arranged?

_Must_ learn now

  - the command line
  - project templates
  - build tooling
  - a strange editor
  - the host platform
  - stack traces
  - lisp syntax

*Maybe* do later

  - play
  - build
  - explore
  - share

We have two very distinct columns.

The programming comes _last_ and the setup comes first.

\<wax poetic about what we truly want\>

What if we could swap these columns?

What if we could play with shapes first, build with code first, explore ideas first? And if something comes up so we need to talk about lisp syntax or SVGs in JS, we can learn it then.

But pull those topics piecemeal!

What if we could get out of their way and let them _experience programming_?

## Maria Montessori
![Maria Montessori](http://daveliepmann.com/maria/maria-montessori-1933.jpg)

>Our work is not to teach, but to help the absorbent mind in its work of development.
*–Maria Montessori*

Putting play and exploration first was a central idea of Maria Montessori.

She was a pioneer in educational philosophy.

Her idea was that learners are eager to learn, and so the goal of the educator is to create a playground and then *get out of the way*.

For us, that means instead of memorizing command-line incantations that we don't understand, learn things because they naturally arise in the course of our activity. That implies that as educators, as tool-builders, we should create an environment where learners aren't drowned beneath a tsunami of incidental complexity.

Through exploration, people will learn incredibly complex topics, if they are in an environment suitable to explore.

## Quick: An Introduction to Racket with Pictures
*by Matthew Flat*

![Screenshot from quick intro to Racket](http://daveliepmann.com/maria/quick-racket.png)

This kind of approach can work! It's been shown to work, for instance, in the world of Racket (a lispy cousin of ours).

The Racket community ruthlessly minimized anything in the way of a total newcomer running code to draw pictures.

Matthew Flat's introduction to Racket is a walkthrough that takes the learner through how to interact with the REPL and call functions, how to deal with errors, how to name things and use higher-order functions to assemble complex shapes out of simple building blocks.

And it does all that with zero syntax--zero theory. Just let the learner do things appropriate to their level.

## *Intro to Racket*'s Process
1. Run installer
2. One-liner to draw a circle! 🎉 🎉 🎉 🎉

## Giants' Shoulders
Racket's approach now possible in Clojure, thanks to hard work across Clojure community.

 - *spec* = foundational step towards better error messages & debugging, we are already seeing early results of that
 - *self-hosted compiler* = real live programming of ClojureScript possible in a browser
 - CLJS team has a lot of *improvements in the pipeline* which will make working in JS ecosystem way better -- we're happy to build on that


``` clojure
{:clj/spec                  friendly-error-messages
 :cljs/self-hosted-compiler live-environment-in-browser
 :cljs/active-development   [:cljs/module-consumption
                             :cljs/externs-inference
                             (future (get-improvements)]}
```

## What might this look like in Clojure?
``` clojure
(do (demo tool)
    (demo curriculum))
 ```

## ggg
``` clojure
{:website "maria.cloud"
 :github  {:maria                  "mhuebert/maria"
           :structural-editing     "mhuebert/magic-tree"
           :self-host-dependencies "mhuebert/cljs-live"}}
```

## Vielen Dank!

``` clojure
{:website "maria.cloud"
 :github  {:maria                  "mhuebert/maria"
           :structural-editing     "mhuebert/magic-tree"
           :self-host-dependencies "mhuebert/cljs-live"}}
```
