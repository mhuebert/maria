(ns maria.friendly.kinds)

(defprotocol IDoc
  (doc [this] "Return a docstring for type"))

(def kinds
  {:maria.kinds/character    {:doc "a character: a unit of writing (letter, emoji, and so on)"}
   :maria.kinds/false        {:doc "false: the Boolean value 'false'"}
   :maria.kinds/function     {:doc "a function: something you call with input that returns output"}
   :maria.kinds/macro        {:doc "a macro: a function that transforms source code before it is evaluated."}
   :maria.kinds/keyword      {:doc "a keyword: a special symbolic identifier"}
   :maria.kinds/list         {:doc "a list: a sequence, possibly 'lazy'"}
   :maria.kinds/map          {:doc "a map: a collection of key/value pairs, where each key 'maps' to its corresponding value"}
   :maria.kinds/nil          {:doc "nil: a special value meaning nothing"}
   :maria.kinds/number       {:doc "a number: it can be whole, a decimal, or even a ratio"}
   :maria.kinds/sequence     {:doc "a sequence: a sequence of values, each followed by the next"}
   :maria.kinds/set          {:doc "a set: a collection of unique values"}
   :maria.kinds/string       {:doc "a string: a run of characters that can make up a text"}
   :maria.kinds/symbol       {:doc "a symbol: a name that usually refers to something"}
   :maria.kinds/true         {:doc "true: the Boolean value 'true'"}
   :maria.kinds/vector       {:doc "a vector: a collection of values, indexable by number"}
   :maria.kinds/object       {:doc "a javascript object: a collection of key/value pairs"}
   :maria.kinds/special-form {:doc "a special form: a primitive which is evaluated in a special way"}
   :maria.kinds/atom         {:doc "an Clojure atom, a way to manage data that can change"}
   :maria.kinds/var          {:doc "a Clojure var"}
   :maria.kinds/comment      {:doc "a comment: any text beginning with `;` is ignored by the computer, useful for explaining or annotating code."}
   :maria.kinds/uneval       {:doc "uneval: adding `#_` in front of any expression will cause it to be completely ignored by the computer, like a comment."}})

(defn kind [thing]
  (if (and (keyword? thing) (contains? kinds thing))
    thing
    (cond
      (char? thing) :maria.kinds/character
      (false? thing) :maria.kinds/false
      (keyword? thing) :maria.kinds/keyword
      (seq? thing) :maria.kinds/sequence
      (list? thing) :maria.kinds/list
      (map? thing) :maria.kinds/map
      (var? thing) :maria.kinds/var
      (fn? thing) :maria.kinds/function
      (nil? thing) :maria.kinds/nil
      (number? thing) :maria.kinds/number
      (set? thing) :maria.kinds/set
      (string? thing) :maria.kinds/string
      (symbol? thing) :maria.kinds/symbol
      (true? thing) :maria.kinds/true
      (vector? thing) :maria.kinds/vector
      (object? thing) :maria.kinds/object
      (instance? Atom thing) :maria.kinds/atom
      :else nil)))

(defn what-is
  "Returns a string describing what kind of thing `thing` is."
  [thing]
  (if (satisfies? IDoc thing)
    (doc thing)
    (get-in kinds [(kind thing) :doc] (type thing))))