(ns maria.messages
  (:require [clojure.string :as string]))

(def kinds
  {:maria.kinds/character    {:doc "a character: a unit of writing (letter, emoji, and so on)"}
   :maria.kinds/false        {:doc "false: the Boolean value 'false'"}
   :maria.kinds/function     {:doc "a function: something you call with input that returns output"}
   :maria.kinds/macro        {:doc "a macro: a function that transforms source code before it is evaluated."}
   :maria.kinds/keyword      {:doc "a keyword: a special symbolic identifier"}
   :maria.kinds/list         {:doc "a list: a sequence, possibly 'lazy'"}
   :maria.kinds/shape        {:doc "a shape: some geometry that Maria can draw"}
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
   :maria.kinds/var          {:doc "a Clojure var"}})

(defn kind [thing]
  (if (contains? kinds thing)
    thing
    (cond
      (char? thing) :maria.kinds/character
      (false? thing) :maria.kinds/false
      (fn? thing) :maria.kinds/function
      (keyword? thing) :maria.kinds/keyword
      (list? thing) :maria.kinds/list
      (and (map? thing) (= :shape (:is-a thing))) :maria.kinds/shape
      (map? thing) :maria.kinds/map
      (nil? thing) :maria.kinds/nil
      (number? thing) :maria.kinds/number
      (seq? thing) :maria.kinds/sequence
      (set? thing) :maria.kinds/set
      (string? thing) :maria.kinds/string
      (symbol? thing) :maria.kinds/symbol
      (true? thing) :maria.kinds/true
      (vector? thing) :maria.kinds/vector
      (object? thing) :maria.kinds/object
      (var? thing) :maria.kinds/var
      :else nil)))

;; TODO possibly add references to https://clojure.org/reference/reader and/or https://clojure.org/reference/data_structures
(defn what-is
  "Returns a string describing what kind of thing `thing` is."
  [thing]
  (get-in kinds [(kind thing) :doc] (type thing)))

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
      "It looks like you're trying to call a function that has not been defined yet."]
     ["invalid arity %"
      "%1 is too many arguments!"]
     ["no protocol method icollection conj defined for type % %"
      "The %1 `%2` can't be used as a collection."]
     ["% is not iseqable" "The value `%1` can't be used as a sequence or collection."]
     ["% call is not a function"
      "The value `%1` isn't a function, but it's being called like one."]]))

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
  [{:keys [source error error-position]}]
  [:div
   [:p (prettify-error-message (ex-message error))]
   [:p (ex-message (ex-cause error))]
   [:pre (some-> (ex-cause error) (aget "stack"))]])

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

(defn reformat-warning [{:keys [source env] :as w}]
  (let [bad-types (map type-to-name
                       (remove (partial = 'number)
                               (:types (:extra w))))]
    [:div
     (case (:type w)
       :fn-arity (str "The function `"
                      (name (-> w :extra :name))
                      "` in the expression above needs "
                      (if (= 0 (-> w :extra :argc))         ;; TODO get arity from meta
                        "more"
                        "a different number of")
                      " arguments.")
       :invalid-arithmetic (str "In the above expression, the arithmetic operator `"
                                (name (-> w :extra :js-op))
                                "` can't be used on non-numbers, like "
                                (humanize-sequence bad-types) ".")
       :undeclared-var (str "The above expression contains a reference to `"
                            (-> w :extra :suffix)
                            "`, but it hasn't been defined!")
       (with-out-str (println (select-keys w [:type :extra]))))]))

;; NB took this out because we're already
;; printing the expression in a nicer way
;; above the messages. -jar
;;
;; (:form w) "` contains `"

;;{:type :undeclared-var, :extra {:prefix maria.user, :suffix what-is, :macro-present? false}, :form (what-is "foo")}

(comment

  (what-is true)                                            ;"the Boolean value true"
  (what-is 1)                                               ;"a number"
  (what-is "bob")                                           ;"a string"
  (what-is 'a)                                              ;"a symbol"
  (what-is :b)                                              ;"a keyword"
  (what-is (fn [_]))                                        ;"a function"

  (reformat-error "Error: No protocol method ICollection.-conj defined for type number: 5")
  ;;=> "The number `5` can't be used as a collection."

  (reformat-error "Error: 1 is not ISeqable")
  ;;=> "The value `1` can't be used as a sequence or collection."

  (reformat-error "TypeError: 1.call is not a function")
  ;;=> "The value `1` isn't a function, but it's being called like one."

  (reformat-warning '{:type  :invalid-arithmetic,
                      :extra {:js-op cljs.core/+, :types [cljs.core/IVector number]},
                      :form  (+ [] 5)})
  ;;=>"In the expression `(+ [] 5)`, the arithmetic operaror `+` can't be used on non-numbers, like a vector."
  )
