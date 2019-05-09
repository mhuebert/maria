(ns maria.friendly.messages
  (:require [clojure.string :as string]
            [clojure.set :as set]
            [cljs.analyzer :as ana]
            #_ [cljs.repl]
            [maria.live.ns-utils :as ns-utils]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; error message prettifier

(defn match-in-tokens
  "Recursively walk down the search `trie` matching `tokens` along `path`, returning the matching message template and match context."
  ([trie tokens] (match-in-tokens trie tokens []))
  ([trie [token & remaining-tokens] context]
   (let [capture (first (filter (partial = "%") (keys trie)))]
     (if-let [trie-match (or (trie token) (trie capture))]
       ;; if we calculate `context` without `(not (trie token))` then we get captures from other branches
       (let [context (if (and capture (not (trie token)))
                       (conj context token)
                       context)
             next-match (match-in-tokens trie-match remaining-tokens context)]
         (if (:message trie-match)
           (assoc trie-match :context context)
           next-match))
       context))))

(comment
  ;; Sample error message strings
  "Invalid arity: 3"
  "No protocol method ICollection.-conj defined for type cljs.core/Keyword: :a"
  "No protocol method ICollection.-conj defined for type number: 7"
  "5.call is not a function"
  "shapes.core.circle.call(...).call is not a function"

  (tokenize "shapes.core.circle.call(...).call is not a function")

  )

(defn str-sanitizer-fns [s]
  (-> (str s) ;; we ensure Stringiness here because we also want to handle raw vars
      (string/replace ".call" "")
      (string/replace "cljs.core/" "")
      (string/replace "shapes.core/" "")
      (string/replace "maria.user/" "")))

(defn tokenize
  "Returns lowercase tokens from `s`, limited to the letters [a-z] and numbers [0-9]."
  [s]
  (->> (-> s
           string/lower-case
           str-sanitizer-fns
           (string/split #"[^a-z%0-9]"))
       (map #(string/replace % "." ""))
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

(defn error-messages
  "Returns friendly variants of messages from `error` and its cause"
  [error]
  (if (instance? js/Error error)
    (->> [(some-> (ex-message (ex-cause error)) (prettify-error-message))
          (some-> (ex-message error) (prettify-error-message))]
         (keep identity))
    [(str "Error: " error)]))

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

;; from https://gist.github.com/vishnuvyas/958488
(defn- compute-next-row
  "computes the next row using the prev-row current-element and the other seq"
  [prev-row current-element other-seq pred]
  (reduce
   (fn [row [diagonal above other-element]]
     (conj row (if (pred other-element current-element)
	             ;; if the elements are deemed equivalent according to the predicate
	             ;; pred, then no change has taken place to the string, so we are
	             ;; going to set it the same value as diagonal (which is the previous edit-distance)
	             diagonal

	             ;; in the case where the elements are not considered equivalent, then we are going
	             ;; to figure out if its a substitution (then there is a change of 1 from the previous
	             ;; edit distance) thus the value is diagonal + 1 or if its a deletion, then the value
	             ;; is present in the columns, but not in the rows, the edit distance is the edit-distance
	             ;; of last of row + 1 (since we will be using vectors, peek is more efficient)
	             ;; or it could be a case of insertion, then the value is above+1, and we chose
	             ;; the minimum of the three
	             (inc (min diagonal above (peek row))))))
   ;; we need to initialize the reduce function with the value of a row, since we are
   ;; constructing this row from the previous one, the row is a vector of 1 element which
   ;; consists of 1 + the first element in the previous row (edit distance between the prefix so far
   ;; and an empty string)
   [(inc (first prev-row))]

   ;; for the reduction to go over, we need to provide it with three values, the diagonal
   ;; which is the same as prev-row because it starts from 0, the above, which is the next element
   ;; from the list and finally the element from the other sequence itself.
   (map vector prev-row (next prev-row) other-seq)))

(defn levenshtein-distance
  "Levenshtein Distance - http://en.wikipedia.org/wiki/Levenshtein_distance
  In information theory and computer science, the Levenshtein distance is a metric for measuring the amount of difference between two sequences. This is a functional implementation of the levenshtein edit
  distance with as little mutability as possible.
  Still maintains the O(n*m) guarantee."
  [a b & {p :predicate :or {p =}}]
  (peek (reduce
         ;; we use a simple reduction to convert the previous row into
         ;; the next-row using the compute-next-row which takes a
         ;; current element, the previous-row computed so far and the
         ;; predicate to compare for equality.
         (fn [prev-row current-element]
	       (compute-next-row prev-row current-element b p))

         ;; we need to initialize the prev-row with the edit distance
         ;; between the various prefixes of b and the empty string.
         (map #(identity %2) (cons nil b) (range))
         a)))

(defn similar-to-vars ;; TODO improve this name
  "Finds `n` (default 4) Maria-relevant Clojure vars (as Strings) named similarly to the given String `s`."
  ([s] (similar-to-vars s 4))
  ([s n]
   (->> (concat (keys (ns-utils/core-publics))
                (map name (keys (ns-publics 'shapes.core)))
                #_ (map name (keys cljs.repl/special))
                ;; Special forms that aren't in `core-publics`: (TODO would be nice to have these automatically enumerated)
                ["if" "def" "quote" "var" "do" "recur" "throw" "." "try"])
        (map name)
        (map (juxt (partial levenshtein-distance s) identity))
        (sort-by first)
        (take-while (comp (partial >= 3) first)) ;; we only want strong matches, defined as Levenshtein distance <= 3
        (map second) ;; we're done with Levenshtein calculation
        (take n))))

(comment
  (similar-to-vars "define" 20) ;; it would be nice to prioritize `def` higher
  (similar-to-vars "defun") 
  (similar-to-vars "def")
  (similar-to-vars "cirlce")  
  (similar-to-vars "whatis")
  (similar-to-vars "js-source1") ;; <-- TODO

  ;; It's hard to find the right match with short names:
  (similar-to-vars "tryy") ;; works fine
  (similar-to-vars "tyr") ;; we'd like to see `try` but it's tied with all the other candidates
  (similar-to-vars "tyr" 10)

  )

;; Maybe return to this idea later?
#_(def similar-concepts ;; TODO improve name
  "Map of possibly-commonly-reached-for functions that do not exist in Clojure, and some of their nearest Clojure counterparts."
  {"curry" #{"partial"}
   "function" #{"fn"}})

(def analyzer-messages
  {;; :preamble-missing ::pass
   ;; :invoke-ctor ::pass
   ;; :extending-base-js-type ::pass
   ;; :unsupported-preprocess-value ::pass
   ;; :redef ::pass
   ;; :js-shadowed-by-local ::pass
   ;; :unsupported-js-module-type ::pass
   ;; :private-var-access ::pass
   ;; :munged-namespace ::pass
   ;; :single-segment-namespace ::pass
   ;; :infer-warning ::pass
   ;; :invalid-array-access ::pass
   ;; :unprovided ::pass
   ;; :ns-var-clash ::pass
   ;; :undeclared-ns ::pass
   :non-dynamic-earmuffed-var ::pass ;; the out-of-the-box error message is pretty good!
   ;; :undeclared-ns-form ::pass
   ;; :fn-var ::pass
   ;; :redef-in-file ::pass
   ;; :extend-type-invalid-method-shape ::pass
   ;; :multiple-variadic-overloads ::pass
   ;; :protocol-with-variadic-method ::pass
   ;; :undeclared-protocol-symbol ::pass
   ;; :protocol-impl-recur-with-target ::pass
   ;; :protocol-multiple-impls ::pass
   ;; :protocol-invalid-method ::pass
   ;; :protocol-impl-with-variadic-method ::pass
   ;; :invalid-protocol-symbol ::pass

   ;; :variadic-max-arity ::pass
   
   :fn-arity
   (fn [type info]
     (str "The function `"
          (str-sanitizer-fns (or (:ctor info)
                                 (:name info)))
          "` needs "
          (if (= 0 (:argc info)) ; TODO get arity from meta
            "more"
            "a different number of")
          " arguments."))

   :invalid-arithmetic
   (fn [type info]
     (str "The arithmetic operator `"
          (name (:js-op info))
          "` can't be used on non-numbers, like "
          (humanize-sequence (bad-types info)) "."))

   :undeclared-var
   (fn [type {missing-name :suffix
             :keys        [macro-present?]}]
     (if macro-present?
       (str "`" missing-name "` is a macro, which can only be used in the first position of a list. A macro doesn't have a value on its own, so it doesn't make sense in this position.")
       (if-let [proposed (when (<= 3 (count (str missing-name)))
                           (seq (similar-to-vars (str missing-name))))]
         (str "`" missing-name "` hasn't been defined."
              " Did you mean `" (if (> (count proposed) 1)
                                 (str (string/join "`, `" (butlast proposed)) "`, or `" (last proposed) "`")
                                 (first proposed))
              "? Or maybe the definition of `" missing-name "` has not yet been evaluated.")
         (str "`" missing-name "` hasn't been defined. Perhaps there is a misspelling, or this expression depends on a name that has not yet been evaluated?"))))
   
   :overload-arity
   (fn [type info]
     "This is a 'multiple-arity' function, which has more than one expression accepting same number of arguments.")

   :dynamic
   (fn [_type info]
     (str "You're not allowed to dynamically change the value of `" (:name info) "`, because it is a static var. Consider redefining it to be `^:dynamic`."))

   :fn-deprecated
   (fn [_type info]
     (str "The function `" (or (-> info :fexpr :form)
                               (-> info :fexpr :name))
          "` is deprecated, meaning it is not intended to be used."))

   :protocol-deprecated
   (fn [_type info]
     (str "The protocol `" (:protocol info) "` is deprecated. That means you shouldn't use or implement it."))

   :protocol-duped-method
   (fn [_type info]
     (str "Your implementation of protocol `" (:protocol info) "` has two versions of the method `" (:fname info) "`. Consider deleting one."))

   :declared-arglists-mismatch
   (fn [_type info]
     (comment ;; Sample `info`:
       {:ns-name maria.user, :sym foo, :declared ([x y]), :defined ([x y z])})
     (str "`" (:sym info) "` cannot be defined with arglist `" (:defined info)
          "` because it was declared with arglist `" (:declared info) "`."
          "\n\nEarlier, you declared `" (:sym info) "` as a function with one arglist. This expression defines that function with an incompatible arglist, which is not allowed. Make the declaration match the definition."))})

(defn override-analyzer-messages! []
  (doseq [[k f] analyzer-messages]
    (when-not (= ::pass f)
      (-add-method ana/error-message k f))))

(comment
  (override-analyzer-messages!)

 ;; missing messages
 (set/difference (set (keys ana/*cljs-warnings*))
                 (set (keys analyzer-messages))))

(def ignored-warning-types #{:infer-warning})

(defn reformat-warning [warning]
  (when-not (ignored-warning-types (:type warning))
    [:div
     (ana/error-message (:type warning) (:extra warning))
     [:.o-50.i.mv2 "(" (str (:type warning)) ")"]]))
