(ns maria.friendly.messages
  (:require [maria.friendly.kinds :as kinds]
            [clojure.string :as string]
            [cljs.analyzer :as ana]))



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
     "It looks like you're trying to iterate over something that isn't sequential. This could be a couple things, but probably involves trying to treat something like a map, set, or keyword as if it were a vector, list, or string. Double-check that you're passing the arguments you think you are. 🤔 Are you passing a map or set parameter to a function expecting a vector or list? Or perhaps you're trying to destructure something that is not a sequence?"]
    ["illegal character"
     "One of the names in that expression contains an 'illegal character'. Often this is because the name contains an emoji, which JavaScript (the programming language your browser uses) doesn't allow."]]))
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
      (list (some-> (ex-message (ex-cause error)) (prettify-error-message))
            (some-> (ex-message error) (prettify-error-message))))

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
  [type {missing-name :suffix
         :keys [macro-present?]}]
           (if macro-present?
             (str "`" missing-name "` is a macro, which can only be used in the first position of a list. A macro doesn't have a value on its own, so it doesn't make sense in this position.")
             (str "`" missing-name "` hasn't been defined! Perhaps there is a misspelling, or this expression depends on a name that has not yet been evaluated?")))

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
