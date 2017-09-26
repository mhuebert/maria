(ns maria.messages
  (:require [clojure.string :as string]
            [cljs.analyzer :as ana]
            [shapes.core :as shapes]))

(defprotocol IDoc
  (doc [this] "Return a docstring for type"))

(extend-protocol
  IDoc
  shapes/Shape
  (doc [this] "a shape: some geometry that Maria can draw"))

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

;; TODO possibly add references to https://clojure.org/reference/reader and/or https://clojure.org/reference/data_structures
(defn what-is
  "Returns a string describing what kind of thing `thing` is."
  [thing]
  (if (satisfies? IDoc thing)
    (doc thing)
    (get-in kinds [(kind thing) :doc] (type thing))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; error message prettifier

(defn match-in-tokens
  "Recursively walk down the search `trie` matching `tokens` along `path`, returning the matching message template and match context."
  ([trie tokens] (match-in-tokens trie tokens []))
  ([trie [token & remaining-tokens] context]
   (let [capture (first (filter (partial = "%") (keys trie)))
         context (if capture
                   (conj context token)
                   context)]
     (if-let [trie-match (or (trie token) (trie capture))]
       (let [next-match (match-in-tokens trie-match remaining-tokens context)]
         (if (:message trie-match)
           (assoc trie-match :context context)
           next-match))
       context))))

(defn tokenize
  "Returns lowercase tokens from `s`, limited to the letters [a-z] and numbers [0-9]."
  [s]
  (->> (string/split (string/lower-case s) #"[^a-z%0-9]")
       (remove empty?)
       (into [])))

(defn build-error-message-trie
  "Convert a sequence of pattern/output `templates` into a search trie."
  [templates]
  (reduce (fn [trie [pattern output]]
            (assoc-in trie (conj (tokenize pattern) :message) output))
          {}
          templates))

(def error-message-trie
  "A search trie for matching error messages to templates."
  (build-error-message-trie
    [["cannot read property call of %"
      "It looks like you're trying to call a function that has not been defined yet. 🙀"]
     ["invalid arity %"
      "%1 is too many arguments!"]
     ["no protocol method icollection conj defined for type % %"
      "The %1 `%2` can't be used as a collection. Sorry!"]
     ["% is not iseqable"
      "The value `%1` can't be used as a sequence or collection."]
     ["% call is not a function"
      "The value `%1` isn't a function, but it's being called like one."]
     ["Parameter declaration missing"
      "This function is missing its 'parameter declaration', the vector of arguments that comes after its name or docstring."]
     ["Could not compile"
      "Compile error: we were unable to turn this code into JavaScript. 😰"] ;; FIXME
     ["let requires an even number"
      "`let` requires an even number of forms in its binding vector."] ;; FIXME improve
     ["Index out of bounds"
      "Somehow you're trying to get a non-existent part of a collection.\n\nThis is like trying to make an appointment on the fortieth day of November. 📆 There is no fortieth day, so we get what's called an \"index out of bounds\" error."]
     ["nth not supported on this type %"
      "It looks like you're trying to iterate over something that isn't a sequence. Perhaps you're trying to destructure something that is not a sequence? 🤔"]]))
;;"It looks like you're declaring a function, but something isn't right. Most of the time a function declaration looks like this, for the function named \"foo\":\n\n(defn foo [a b c]\n  (* a b c))\n\nOr like this, with a docstring:\n\n(defn foo \"Returns the product of its three arguments.\"\n  [a b c]\n  (* a b c))"

(defn prettify-error-message
  "Take an error `message` string and return a prettified version."
  [message]
  (let [match (match-in-tokens error-message-trie (tokenize message))]
    (if (some-> match (contains? :message))
      (reduce
        (fn [message [i replacement]]
          (string/replace message (str "%" (inc i)) replacement))
        (:message match)
        (map vector (range) (:context match)))
      message)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn reformat-error
  "Takes the exception text `e` and tries to make it a bit more human friendly."
  [{:keys [source error error/position]}]
  (let [error-message (ex-message error)
        cause-message (ex-message (ex-cause error))]
    (list
      (some-> cause-message (prettify-error-message))
      (some-> error-message (prettify-error-message))
      (when-let [stack (some-> (ex-cause error) (aget "stack"))]
        [:pre stack]))))

(defn type-to-name
  "Return a string representation of the type indicated by the symbol `thing`."
  [thing]
  (cond
    (= 'string thing) "a string"
    (= 'number thing) "a number"
    (string/includes? (name thing) "Vector") "a vector"
    (string/includes? (name thing) "List") "a list"
    (string/includes? (name thing) "Keyword") "a keyword"
    (string/includes? (name thing) "PersistentArrayMap") "a map"
    :else thing))

(defn humanize-sequence
  "Given a sequence of strings, collects them together into a comma separated list with grammatically correct use of `or`."
  [sq]
  (case (count sq)
    1 (first sq)
    2 (str (first sq) " or " (second sq))
    (let [chunks (interpose ", " sq)]
      (str (apply str (butlast chunks)) "or " (last chunks)))))

;;(humanize-sequence (map what-is [1 :a]))
;;=>"a number or a keyword"
;;(humanize-sequence (map what-is [1 2 'a :b "c"]))
;;=> "a number, a number, a symbol, a keyword, or a string"




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Warning messages can be set by overriding default values in the
;; cljs.analyzer/error-message multimethod.

(defn bad-types [info]
  (map type-to-name (remove (partial = 'number) (:types info))))

(defmethod ana/error-message :fn-arity
  [type info]
  (str "The function `"
       (or (:ctor info)
           (:name info))
       "` needs "
       (if (= 0 (-> info :argc))   ;; TODO get arity from meta
         "more"
         "a different number of")
       " arguments."))

(defmethod ana/error-message :invalid-arithmetic
  [type info]
  (str "The arithmetic operator `"
       (name (:js-op info))
       "` can't be used on non-numbers, like "
       (humanize-sequence (bad-types info)) "."))

(defmethod ana/error-message :undeclared-var
  [type info]
  (str "`" (:suffix info) "` hasn't been defined! Perhaps there is a misspelling, or this expression depends on a name that has not yet been evaluated?"))

(defmethod ana/error-message :overload-arity
  [type info]
  "This is a 'multiple-arity' function, which has more than one expression accepting same number of arguments.")

(def ignored-warning-types #{:infer-warning})

(defn reformat-warning [warning]
  (if (ignored-warning-types (:type warning))
    nil
    [:div
     (ana/error-message (:type warning) (:extra warning))
     [:.o-50.i "(" (str (:type warning)) ")"]]))

(comment

  (what-is true)                                            ;"the Boolean value true"
  (what-is 1)                                               ;"a number"
  (what-is "bob")                                           ;"a string"
  (what-is 'a)                                              ;"a symbol"
  (what-is :b)                                              ;"a keyword"
  (what-is (fn [_]))                                        ;"a function"

  (first (reformat-error "Error: No protocol method ICollection.-conj defined for type number: 5"))
  ;;=> "The number `5` can't be used as a collection."

  (first (reformat-error "Error: 1 is not ISeqable"))
  ;;=> "The value `1` can't be used as a sequence or collection."

  (first (reformat-error "TypeError: 1.call is not a function"))
  ;;=> "The value `1` isn't a function, but it's being called like one."

  (reformat-warning '{:type  :invalid-arithmetic,
                      :extra {:js-op cljs.core/+, :types [cljs.core/IVector number]},
                      :form  (+ [] 5)})
  ;;=>"In the expression `(+ [] 5)`, the arithmetic operaror `+` can't be used on non-numbers, like a vector."
  )
