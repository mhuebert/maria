(ns maria.messages)

;; TODO use context to show where the error happened

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
    (true? x)     "Boolean true"
    (false? x)    "Boolean false"
    (nil? x)      "nil (nothing)"
    :else (type x)))

(def exception-fns
  {"is not a function" #(str "It looks like the first thing in a list, “"
                            (.replace % ".call" "")
                            "”, isn't a function!")
   "is not ISeqable"   #(str "It looks like the computer was expecting a sequence where it found “" % "”!")})

(def exception-regex
  (re-pattern (str "(Error|TypeError): ([^ ]*) ("
                   (clojure.string/join "|" (keys exception-fns))
                   ")")))

(defn reformat-exception [e]
  (let [[_ kind thing error] (re-find exception-regex e)]
    (if-let [ex-fn (exception-fns error)]
      (ex-fn thing)
      [kind thing error])))

(comment

  (what-is 1)       ;"a number"
  (what-is "bob")   ;"a string"
  (what-is 'a)      ;"a symbol"
  (what-is :b)      ;"a keyword"
  (what-is (fn [_])) ;"a function"

  (reformat-exception "Error: 1 is not ISeqable")
  ;;=>"It looks like the computer was expecting a sequence where it found “1”!"

  (reformat-exception "TypeError: 1.call is not a function")
  ;;=> "It looks like the first thing in a list, “1”, isn't a function!"
  )
