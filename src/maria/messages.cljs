(ns maria.messages
  (:require [clojure.string :as string]

    ;; core.match not yet supported in self-hosted clojurescript
    ;; see: http://blog.klipse.tech/clojure/2016/10/25/core-match.html
    #_[clojure.core.match :refer-macros [match]]
            ))

;; TODO possibly add references to https://clojure.org/reference/reader and/or https://clojure.org/reference/data_structures
(defn what-is
  "Returns a string describing what kind of thing `thing` is."
  [thing]
  (case thing
    :maria.kinds/macro "a macro: a function that transforms source code before it is evaluated."
    :maria.kinds/function (what-is (fn []))
    (cond
      (vector? thing) "a vector: a collection of values, indexed by contiguous integers"
      (list? thing) "a list: a collection and sequence, possibly lazy"
      (set? thing) "a set: a collection of unique values"
      (string? thing) "a string: text characters"
      (char? thing) "a character: a single literal unit of text"
      (number? thing) "a number: literal digits or a ratio"
      (keyword? thing) "a keyword: a symbolic identifier"
      (symbol? thing) "a symbol: a name that refers to something else"
      (fn? thing) "a function: an object you call with input, that returns output"
      (map? thing) "a map: a collection of key/value pairs, where each key is a 'map' to its corresponding value"
      (seq? thing) "a sequence: a logical list of values, each one followed by the next"
      (true? thing) "the Boolean value true"
      (false? thing) "the Boolean value false"
      (nil? thing) "the special value nil (nothing)"
      :else (type thing))))

(defn tokenize
  "Returns lowercase tokens from `s`, limited to the letters [a-z] and numbers [0-9]."
  [s]
  (->> (string/split (string/lower-case s) #"[^a-z0-9]")
       (remove empty?)
       rest
       (into [])))

(defn reformat-error
  "Takes the exception text `e` and tries to make it a bit more human friendly."
  [{:keys [source error error-location]}]
  [:div
   [:p (ex-message error)]
   [:p (ex-message (ex-cause error))]
   [:pre (some-> (ex-cause error) (aget "stack"))]]
  #_(match [(tokenize error)]
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
           :else error))

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
                      "` in the expression `"
                      (:form w)
                      "` needs "
                      (if (= 0 (-> w :extra :argc))         ;; TODO get arity from meta
                        "more"
                        "a different number of")
                      " arguments.")
       :invalid-arithmetic (str "In the expression `"
                                (:form w)
                                "`, the arithmetic operaror `"
                                (name (-> w :extra :js-op))
                                "` can't be used on non-numbers, like "
                                (humanize-sequence bad-types) ".")
       :undeclared-var (str "The expression `"
                            (:form w)
                            "` contains `"
                            (-> w :extra :suffix)
                            "`, but it hasn't been defined!")
       (with-out-str (println (dissoc w :env))))]))

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
