(ns maria.friendly.docstrings)

;; Currently using Markdowny strings

;; Similar work (but complected with nREPL and having only one example) at https://github.com/tatut/nrepl-doc-inject
;; Similar work (using adoc format and with longer/more extensive explanations) at https://github.com/Odie/sidedocs-clj-api-docs
;; Similar work (started but only 2 examples) at https://github.com/ericnormand/ultra-docstrings/blob/master/src/ultra_docstrings/core.clj

(def clojure-core
  {"map" {:docstring "Applies the given function `f` to each element of the collection `coll`.

If given more than one collection (e.g. `(map f c1 c2)`), applies the given function `f` to the set of first items of each coll, followed by applying f to the set of second items in each coll, until any one of the colls is exhausted. (Any remaining items in other collections are ignored.)

Returns a lazy sequence, regardless of the type of the input collections. See `mapv` if you want a similar function that returns a vector.

The function `f` should accept the same number of arguments as the number of collections provided.

Returns a [transducer](https://clojure.org/reference/transducers) when no collection is provided."}
   "fn" {:docstring "Defines a function that will evaluate its arguments `params*` (a vector of zero or more names) according to the expressions `exprs*`."}})
