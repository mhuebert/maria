(ns maria.messages
  (:require [clojure.string :as cs]
            [cljs.pprint :refer [pprint]]
    ;; core.match not yet supported in self-hosted clojurescript
    ;; see: http://blog.klipse.tech/clojure/2016/10/25/core-match.html
    #_[clojure.core.match :refer-macros [match]]

            ))

(defn what-is
  "Returns a string describing what kind of thing `thing` is."
  [thing]
  (cond
    (vector? thing) "a vector"
    (list? thing) "a list"
    (string? thing) "a string"
    (char? thing) "a character"
    (number? thing) "a number"
    (keyword? thing) "a keyword"
    (symbol? thing) "a symbol"
    (fn? thing) "a function"
    (map? thing) "a map"
    (seq? thing) "a sequence"
    (true? thing) "the Boolean value true"
    (false? thing) "the Boolean value false"
    (nil? thing) "the special value nil (nothing)"
    :else (type thing)))

(defn tokenize
  "Returns lowercase tokens from `s`, limited to the letters [a-z] and numbers [0-9]."
  [s]
  (->> (cs/split (cs/lower-case s) #"[^a-z0-9]")
       (remove empty?)
       rest
       (into [])))

(defn reformat-error
  "Takes the exception text `e` and tries to make it a bit more human friendly."
  [e]
  "<must implement reformat-error>"
  #_(match [(tokenize e)]
           [["cannot" "read" "property" "call" "of" the-value]] ;; TODO warning is better
           (str "It looks like you're trying to call a function that hasn't been defined.")
           [["invalid" "arity" the-value]]                  ;; TODO warning is better
           (str the-value " is too many arguments!")
           [["no" "protocol" "method" "icollection" "conj" "defined" "for" "type" the-type the-value]]
           (str "The " the-type " `" the-value "` can't be used as a collection.")
           [[the-value "is" "not" "iseqable"]]
           (str "The value `" the-value "` can't be used as a sequence or collection.")
           [[the-value "call" "is" "not" "a" "function"]]
           (str "The value `" the-value "` isn't a function, but it's being called like one.")
           :else e))

(defn type-to-name
  "Return a string representation of the type indicated by the symbol `thing`."
  [thing]
  (cond
    (= 'string thing) "a string"
    (= 'number thing) "a number"
    (cs/includes? (name thing) "Vector") "a vector"
    (cs/includes? (name thing) "List") "a list"
    (cs/includes? (name thing) "Keyword") "a keyword"
    (cs/includes? (name thing) "PersistentArrayMap") "a map"
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

(defn reformat-warning [w]
  (let [bad-types (map type-to-name
                       (remove (partial = 'number)
                               (:types (:extra w))))]
    (case (:type w)
      :fn-arity (str "The function `"
                     (name (-> w :extra :name))
                     "` in the expression `"
                     (:source-form w)
                     "` needs "
                     (if (= 0 (-> w :extra :argc))          ;; TODO get arity from meta
                       "more"
                       "a different number of")
                     " arguments.")
      :invalid-arithmetic (str "In the expression `"
                               (:source-form w)
                               "`, the arithmetic operaror `"
                               (name (-> w :extra :js-op))
                               "` can't be used on non-numbers, like "
                               (humanize-sequence bad-types) ".")
      :undeclared-var (str "The expression `"
                           (:source-form w)
                           "` contains `"
                           (-> w :extra :suffix)
                           "`, but it hasn't been defined!")
      (with-out-str (pprint (dissoc w :env))))))

;;{:type :undeclared-var, :extra {:prefix maria.user, :suffix what-is, :macro-present? false}, :source-form (what-is "foo")}

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

  (reformat-warning '{:type        :invalid-arithmetic,
                      :extra       {:js-op cljs.core/+, :types [cljs.core/IVector number]},
                      :source-form (+ [] 5)})
  ;;=>"In the expression `(+ [] 5)`, the arithmetic operaror `+` can't be used on non-numbers, like a vector."
  )
