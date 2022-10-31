(ns maria.repl.api
  (:refer-clojure :exclude [atom])
  (:require [sci.impl.resolve]
            [sci.core :as sci]
            #?(:cljs ["react" :as react])
            #?(:cljs [sci.async :as a])
            [promesa.core :as p]
            [yawn.view :as v]
            [re-db.reactive :as r]
            [sci.impl.vars :as vars]
            [sci.impl.utils :as utils]
            [sci.impl.resolve :as resolve]
            [maria.ui :as ui]
            [maria.helpful :as helpful]))

(defonce ^:dynamic *context* (clojure.core/atom nil))

(defn resolve-symbol
  "Resolves `sym` to var, optionally evaluated within `ns`"
  ([sym] (resolve-symbol nil sym))
  ([ns sym]
   (vars/with-bindings
    {utils/current-ns (or ns @utils/current-ns)}
    (try (resolve/resolve-symbol @*context* sym)
         (catch js/Error e nil)))))

(defn html
  "Renders hiccup forms to html (via underlying view layer, eg. React)"
  [hiccup-form]
  (v/x hiccup-form))

(defn ^:macro doc
  "Show documentation for given symbol"
  [&form &env sym]
  `(ui/show-doc (merge (meta (resolve-symbol '~sym))
                       '~(helpful/doc-map sym))))

(defn ^:macro dir
  "Display public vars in namespace (symbol)"
  [&form &env ns]
  #_`(with-out-str (clojure.repl/dir ~ns))
  `'~(some->> @maria.repl.api/*context*
              :env
              deref
              :namespaces
              (#(% ns))
              keys
              (filter symbol?)
              sort))

(def atom r/atom)

#?(:cljs
   (def ^function is-valid-element? react/isValidElement))

#?(:cljs
   (defn promise? [x] (instance? js/Promise x)))

#?(:cljs
   (defn await? [x] (and (promise? x) (a/await? x))))

#?(:cljs
   (defn await [x]
     (a/await
      (if (instance? sci.lang/Var x)
        (p/let [v @x]
          (sci/alter-var-root x (constantly v))
          x)
        (js/Promise.resolve x))))
   :clj
   (defn await [x] x))

(defn what-is
  "Returns a string describing what kind of thing `thing` is."
  [thing]
  (cond (vector? thing) "a vector: a collection of values, indexable by number",
        (keyword? thing) "a keyword: a special symbolic identifier",
        (boolean? thing) (str "the Boolean value '" thing "'"),
        (var? thing) "a Clojure var",
        (seq? thing) "a sequence: a sequence of values, each followed by the next",
        (set? thing) "a set: a collection of unique values",
        (record? thing) (str "an instance of " (pr-str (type thing)) " (a record)")
        (map? thing) "a map: a collection of key/value pairs, where each key 'maps' to its corresponding value",
        (list? thing) "a list: a sequence, possibly 'lazy'",
        (char? thing) "a character: a unit of writing (letter, emoji, and so on)",
        (nil? thing) "nil: a special value meaning nothing",
        (number? thing) "a number: it can be whole, a decimal, or even a ratio",
        (string? thing) "a string: a run of characters that can make up a text",
        (symbol? thing) "a symbol: a name that usually refers to something",
        #?@(:cljs [(object? thing) "a javascript object: a collection of key/value pairs",]),
        (instance? #?(:cljs Atom :clj clojure.lang.Atom) thing) "an Clojure atom, a way to manage data that can change"
        (fn? thing) "a function: something you call with input that returns output"))