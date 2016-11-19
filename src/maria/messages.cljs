(ns maria.messages
  (:require [clojure.string :as cs]
            [cljs.pprint :refer [pprint]]
            [cljs.core.match :refer-macros [match]]))

(defn what-is
  "Returns a string describing what kind of thing `x` is."
  [x]
  (cond 
    (vector? x)  "a vector"
    (list? x)    "a list"
    (string? x)  "a string"
    (char? x)    "a character"
    (number? x)  "a number"
    (keyword? x) "a keyword"
    (symbol? x)  "a symbol"
    (fn? x)      "a function"
    (map? x)     "a map"
    (seq? x)     "a sequence"
    (true? x)    "the Boolean value true"
    (false? x)   "the Boolean value false"
    (nil? x)     "the special value nil (nothing)"
    :else (type x)))

(defn tokenize
  "Returns lowercase tokens from `s`, limited to the letters [a-z] and numbers [0-9]"
  [s]
  (->> (cs/split (cs/lower-case s) #"[^a-z0-9]")
       (remove empty?)
       rest
       (into [])))

(defn reformat-error
  "Takes the exception text `e` and tries to make it a bit more human friendly."
  [e]
  (match [(tokenize e)]
         [["no" "protocol" "method" "icollection" "conj" "defined" "for" "type" the-type the-value]]
         (str "The " the-type " `" the-value "` can't be used as a collection.")
         [[the-value "is" "not" "iseqable"]]
         (str "The value `" the-value "` can't be used as a sequence or collection.")
         [[the-value "call" "is" "not" "a" "function"]]
         (str "The value `" the-value "` isn't a function, but it's being called like one.")
         :else e))

(defn type-to-name [thing]
  (cond 
    (= 'string thing)                                "a string"
    (= 'number thing)                                "a number"
    (cs/includes? (name thing) "Vector")             "a vector"
    (cs/includes? (name thing) "List")               "a list"
    (cs/includes? (name thing) "Keyword")            "a keyword"
    (cs/includes? (name thing) "PersistentArrayMap") "a map"
    (symbol? thing)                             "a symbol"
    (fn? thing)                                 "a function"
    (string? thing)                             "a string"
    (char? thing)                               "a character"
    (seq? thing)                                "a sequence"
    (true? thing)                               "the Boolean value true"
    (false? thing)                              "the Boolean value false"
    (nil? thing)                                "the special value nil (nothing)"
    :else thing))

(defn humanize-sequence [sq]
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
  (str
   (case (:type w)
     :invalid-arithmetic (let [op-name (name (-> w :extra :js-op))
                               bad-types (map type-to-name
                                              (remove (partial = 'number)
                                                      (:types (:extra w))))]
                           (str "In the expression `" (:source-form w) "`, the arithmetic operaror `" op-name "` can't be used on non-numbers, like " (humanize-sequence bad-types) "."))
     "")
   "\n\n"
   (with-out-str (pprint (dissoc w :env)))))

(comment

  (what-is true)    ;"the Boolean value true"
  (what-is 1)       ;"a number"
  (what-is "bob")   ;"a string"
  (what-is 'a)      ;"a symbol"
  (what-is :b)      ;"a keyword"
  (what-is (fn [_])) ;"a function"

  (reformat-error "Error: No protocol method ICollection.-conj defined for type number: 5")
  ;;=> "The number `5` can't be used as a collection."  

  (reformat-error "Error: 1 is not ISeqable")
  ;;=> "The value `1` can't be used as a sequence or collection."

  (reformat-error "TypeError: 1.call is not a function")
  ;;=> "The value `1` isn't a function, but it's being called like one."  

  (reformat-warning '{:type :invalid-arithmetic,
                      :extra {:js-op cljs.core/+, :types [cljs.core/IVector number]},
                      :source-form (+ [] 5)})
  ;;=>"In the expression `(+ [] 5)`, the arithmetic operaror `+` can't be used on non-numbers, like a vector."
  )

;; (map type-to-name
;;      (-> '{:type :invalid-arithmetic,
;;            :extra {:js-op cljs.core$macros/+, :types [cljs.core/IVector number]},
;;            :source-form (+ [] "foo" 5)}
;;          :extra
;;          :types))
