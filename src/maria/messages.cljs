(ns maria.messages
  (:require [clojure.string :as cs]
            [cljs.core.match :refer-macros [match]]))

(defn what-is
  "This is `what-is`, a function that takes any sort of element and
  returns a string describing that that element is."
  [x]
  (cond 
    (string? x)   "a string"
    (char? x)     "a character"
    (number? x)   "a number"
    (symbol? x)   "a symbol"
    (keyword? x)  "a keyword"
    (fn? x)       "a function"
    (vector? x)   "a vector"
    (list? x)     "a list"
    (map? x)      "a map"
    (seq? x)      "a sequence"
    (true? x)     "the Boolean value true"
    (false? x)    "the Boolean value false"
    (nil? x)      "the special value nil (nothing)"
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

;; TODO example warning:
;;
;; {:type :invalid-arithmetic,
;;  :extra {:js-op cljs.core/+, :types [cljs.core/IVector number]},
;;  :source-form (+ [] 5)}

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
  )
