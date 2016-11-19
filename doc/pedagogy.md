# Pedagogy for *Maria* #

>Needless help is an actual hindrance to the development of natural forces.
–Maria Montessori

## Primary inspiration ##

 - [How to Design Programs, 2nd Ed.](http://www.ccs.neu.edu/home/matthias/HtDP2e/)
 - [An Introduction to Racket with Pictures](http://docs.racket-lang.org/quick/)
 - [The Little Schemer, 4th Ed.](https://mitpress.mit.edu/books/little-schemer)

## Secondary resources ##

 - [SICP](https://mitpress.mit.edu/sicp/full-text/book/book-Z-H-10.html), especially the presentation of its [introductory lecture](https://www.youtube.com/watch?v=2Op3QLzMgSY)
 - [Bootstrap Introduction to Programming](http://www.bootstrapworld.org/materials/spring2016/tutorial/)
 - Alan Kay, [A Personal Computer for Children of All Ages](http://www.vpri.org/pdf/hc_pers_comp_for_children.pdf)


## Guiding principles ##

The best way to learn a tool’s usefulness is to do the task without the tool. Feel the pain and hassle that the tool cuts through. Only then introduce the tool as one’s savior. (There’s a specific article that described this really well; if you have the URL I’d be very happy to re-receive it.)

>The practice of programming is a powerful way to develop a deeper understanding of any subject. Indeed, by the act of debugging we learn about our misconceptions, and by reflecting on our bugs and their resolutions we learn ways to learn more effectively."
– Functional Differential Geometry by Gerald Jay Sussman, Jack Wisdom, Will Farr, page xiii of the preface, concept attributed to Seymour Papert and Marvin Minsky

## Concepts to introduce ##

In rough order.

 - reading comments, evaluating expressions
 - asking Clojure questions (through a few core functions)
 - creating one’s own questions (`fn`)
 - `let` -> `def` -> `(def (fn ...))` -> `defn`
 - higher order functions: `reduce` -> `filter`/`remove`, `map`, `iterate`...
 - recursion
 - maps, sets, lists/sequences/collections/vectors
 - namespaces, requiring, external dependencies
