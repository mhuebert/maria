(ns maria.friendly.kinds)

(defprotocol IDoc
  (doc [this] "Return a docstring for type"))

(def kinds
  #::{:character    {:doc "a character: a unit of writing (letter, emoji, and so on)"}
      :false        {:doc "false: the Boolean value 'false'"}
      :function     {:doc "a function: something you call with input that returns output"}
      :macro        {:doc "a macro: a function that transforms source code before it is evaluated."}
      :keyword      {:doc "a keyword: a special symbolic identifier"}
      :list         {:doc "a list: a sequence, possibly 'lazy'"}
      :map          {:doc "a map: a collection of key/value pairs, where each key 'maps' to its corresponding value"}
      :nil          {:doc "nil: a special value meaning nothing"}
      :number       {:doc "a number: it can be whole, a decimal, or even a ratio"}
      :sequence     {:doc "a sequence: a sequence of values, each followed by the next"}
      :set          {:doc "a set: a collection of unique values"}
      :string       {:doc "a string: a run of characters that can make up a text"}
      :symbol       {:doc "a symbol: a name that usually refers to something"}
      :true         {:doc "true: the Boolean value 'true'"}
      :vector       {:doc "a vector: a collection of values, indexable by number"}
      :object       {:doc "a javascript object: a collection of key/value pairs"}
      :special-form {:doc "a special form: a primitive which is evaluated in a special way"}
      :atom         {:doc "an Clojure atom, a way to manage data that can change"}
      :var          {:doc "a Clojure var"}
      :comment      {:doc "a comment: any text beginning with `;` is ignored by the computer, useful for explaining or annotating code."}
      :uneval       {:doc "uneval: adding `#_` in front of any expression will cause it to be completely ignored by the computer, like a comment."}})

(defn kind [thing]
  (if (and (keyword? thing) (contains? kinds thing))
    thing
    (cond
      (char? thing) ::character
      (false? thing) ::false
      (keyword? thing) ::keyword
      (seq? thing) ::sequence
      (list? thing) ::list
      (map? thing) ::map
      (var? thing) ::var
      (fn? thing) ::function
      (nil? thing) ::nil
      (number? thing) ::number
      (set? thing) ::set
      (string? thing) ::string
      (symbol? thing) ::symbol
      (true? thing) ::true
      (vector? thing) ::vector
      (object? thing) ::object
      (instance? Atom thing) ::atom
      :else nil)))

(defn what-is
  "Returns a string describing what kind of thing `thing` is."
  [thing]
  (if (satisfies? IDoc thing)
    (doc thing)
    (get-in kinds [(kind thing) :doc] (type thing))))