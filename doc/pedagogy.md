# Pedagogy for *Maria* #

>Needless help is an actual hindrance to the development of natural forces.

–Maria Montessori


## Guiding principles ##

The best way to learn a tool’s usefulness is to do the task without the tool. Feel the pain and hassle that the tool cuts through. Only then introduce the tool as one’s savior. (There’s a specific article that described this really well; if you have the URL I’d be very happy to re-receive it.)

>The practice of programming is a powerful way to develop a deeper understanding of any subject. Indeed, by the act of debugging we learn about our misconceptions, and by reflecting on our bugs and their resolutions we learn ways to learn more effectively."
– Functional Differential Geometry by Gerald Jay Sussman, Jack Wisdom, Will Farr, page xiii of the preface, concept attributed to Seymour Papert and Marvin Minsky


## Roadmap ##

 1. A quick introduction, modeled after a blatant loving rewrite of [Quick: An Introduction to Racket with Pictures](http://docs.racket-lang.org/quick/). The goal is to walk the learner through the basics of working with Maria using a *minimum* of syntax and programming concepts. Someone familiar with modern computers but who has never programmed (or used a command shell, or learned advanced maths, etc.) should be able to complete this definitely in less than an hour, probably in less than half an hour, ideally within twenty minutes with coaching.
 2. Allow the user to select from a library of "modules" according to their interests. Possibilities include:
    - more extensive drawing explorations, with Quil or native Maria shapes
    - introduction to basics of computer science (recursion, higher order functions)
    - do some basic web stuff--this may be complicated by the fact that Maria is a self-enclosed tool, so building a web app may not be within scope. Instead, perhaps it would be possible to make a remote API call?
    - an exploration of Clojure features, e.g. laziness, infinite sequences, data types (this is the closest analogue to the existing ClojureBridge curriculum)
    - a fun wander through mathematics or statistics (Fibonnacci, Shannon's entropy, Markov chains)
    - word or programming puzzles
    - ??? your idea goes here


## Primary inspiration ##

 - [An Introduction to Racket with Pictures](http://docs.racket-lang.org/quick/), for how fast the on-ramp is, and for the simple LEGO-like fun of learning language features (e.g. define, functions as first-class objects) through simple pictures
 - [The Little Schemer, 4th Ed.](https://mitpress.mit.edu/books/little-schemer), for its minimal Socratic approach towards its single-minded goal of teaching recursion; for its emphasis on direct manipulation and forming one’s own mental computer; and for how its considered omission of theory, explanation, and instruction


## Secondary resources ##

 - [Bootstrap Introduction to Programming](http://www.bootstrapworld.org/materials/spring2016/tutorial/)
 - [SICP](https://mitpress.mit.edu/sicp/full-text/book/book-Z-H-10.html), especially the presentation of its [introductory lecture](https://www.youtube.com/watch?v=2Op3QLzMgSY) and its principled step-by-step syntax-ignorant progression from idea to idea
 - [How to Design Programs, 2nd Ed.](http://www.ccs.neu.edu/home/matthias/HtDP2e/)
 - Alan Kay, [A Personal Computer for Children of All Ages](http://www.vpri.org/pdf/hc_pers_comp_for_children.pdf)


## Differences from Orthodox Clojure ##

Some of these are only plans for the future.

 - side-by-side interface (normally Clojurists use a REPL or in-buffer evaluation, e.g. with CIDER)
 - nice error messages
 - the `what-is` function is specific to this environment
 - all drawings and shape manipulation (`circle`, `line-up`, `colorize`) are specific to this environment
 - we say "Clojure" which many people hear as "Clojure on the JVM" whereas we are using "Clojure in JavaScript" a.k.a. ClojureScript--but it's all just Clojure on different hosts
 - comments, sections, and headings may stray from plain Clojure to enhanced syntax for a literate-programming-ish approach
