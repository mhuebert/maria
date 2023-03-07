(ns maria.repl.api
  (:refer-clojure :exclude [atom])
  (:require #?(:cljs ["react" :as react])
            #?(:cljs [sci.async :as a])
            [maria.helpful :as helpful]
            [maria.ui :as ui]
            [promesa.core :as p]
            [re-db.reactive :as r]
            [sci.core :as sci]
            [sci.impl.namespaces :as sci.ns]
            [sci.impl.resolve]
            [sci.impl.resolve :as resolve]
            [sci.impl.utils :as utils]
            [sci.impl.vars :as vars]
            [yawn.view :as v]
            [sci.ctx-store :as store]))

(defn current-ns [ctx]
  @(:last-ns ctx))

(def current-ns-name (comp sci.ns/sci-ns-name current-ns))

(defn resolve-symbol
  "Resolves `sym` to var, optionally evaluated within `ns`"
  ([sym] (resolve-symbol nil sym))
  ([ns sym] (resolve-symbol (store/get-ctx) ns sym))
  ([ctx ns sym]
   (vars/with-bindings
    {utils/current-ns (or ns @utils/current-ns)}
    (try (resolve/resolve-symbol ctx sym)
         (catch js/Error e nil)))))

(defn doc-map
  ([sym] (doc-map (store/get-ctx) nil sym))
  ([ctx ns sym]
   (merge (meta (resolve-symbol ctx ns sym))
          (helpful/doc-map sym)
          (when-let [sci-ns (sci.ns/sci-find-ns ctx sym)]
            {:doc (:doc (meta sci-ns))
             :name sym}))))

(defn html
  "Renders hiccup forms to html (via underlying view layer, eg. React)"
  [hiccup-form]
  (v/x hiccup-form))

(defn ^:macro doc
  "Show documentation for given symbol"
  [&form &env sym]
  `(ui/show-doc '~(doc-map sym)))

(defn ^:macro dir
  "Display public vars in namespace (symbol)"
  [&form &env ns]
  #_`(with-out-str (clojure.repl/dir ~ns))
  `'~(some->> (store/get-ctx)
              :env
              deref
              :namespaces
              (#(% ns))
              keys
              (filter symbol?)
              sort))

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