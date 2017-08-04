;;;; Intro

(+ 3 4)

(max 8 17 2)

(print-str "Hello, World!")


;;;; Simple values

;; In order to do anything in a programming language, you need to have
;; values to do stuff with. In Clojure, simple values are numbers,
;; strings, booleans, nil and keywords.

;; Integers

;; First up are integers. Integers include zero, the positive whole
;; numbers, and the negative whole numbers, and you write them just
;; like we write them normally.

0

12

-42

;; Decimals

;; Then we have decimal numbers, which are also called floats. They
;; include any numbers that have a decimal point in them.

0.0000072725

10.5

-99.9

;; Ratios

;; Finally, we have fractions, which are also called ratios. Computers
;; cannot perfectly represent all floats, but ratios are always exact.
;; We write them with a slash, like so:

1/2

;; Note that, just like with pen-and-paper math, the [denominator](http://en.wikipedia.org/wiki/Fraction_%28mathematics%29) of your ratio cannot be equal to `0`.

-7/3


;; Arithmetic

;; You can add, subtract, multiply, and divide numbers. In Clojure,
;; arithmetic looks a little different than it does when you write it
;; out with pen and paper. Look at these examples:

(+ 1 1)  ;=> 1 + 1 = 2

(- 12 4) ;=> 12 - 4 = 8

(* 13 2) ;=> 13 * 2 = 26

(/ 27 9) ;=> 27 / 9 = 3


;; Prefix notation

;; In Clojure, `+`, `-`, `*` and `/` appear before two numbers. This
;; is called _prefix notation_. What you're used to seeing is called
;; _infix notation_, as the arithmetic operator is in-between the two
;; operands.

;; Languages such as **JavaScript** use **infix** notation, while
;; **Clojure** only uses **prefix** notation.  Prefix notation is
;; useful for many reasons. Look at this example of an infix
;; expression and the prefix equivalent:

(+ (- (+ (+ 1 (/ (* 2 3) 4)) 5) (/ (* 6 7) 8)) 9) ; compare to: 1 + 2 * 3 / 4 + 5 - 6 * 7 / 8 + 9

;; > Imagine both are unclear, but notice that in the prefix version,
;; > you do not have to ever think about the precedence of operators.
;; > Because each expression has the operator before all the operands and
;; > the entire expression is wrapped in parentheses, all precedence is
;; > explicit.

(+ 1 (/ 2 3)) ; compare to: 1 + 2 / 3

;; > Another reason prefix notation can be nice is that it can make long
;; > expressions less repetitive.
;; > With prefix notation, if we plan to use the same operator on many
;; > operands, we do not have to repeat the operator between them.

(+ 1 2 3 4 5 6 7 8 9) ; compare to: 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9


;; Arithmetic with all number types

;; > So far, we looked at arithmetic operations by integers only.
;; > However, we can use floats or ratios for those operations as well.
;; > See these examples:

(+ 4/3 7/8)   ;=> 53/24

(- 9 4.2 1/2) ;=> 4.3

(/ 27/2 1.5)  ;=> 9.0

;; Strings

;; What is a string? A string is just a piece of text. To make a
;; string, you enclose it in quotation marks.  Look at the last
;; example. A backslash is how we put a quotation mark inside a
;; string. Do not try using single quotes to make a string.

"Hello, World!"

"This is a longer string that I wrote for purposes of an example."

"Aubrey said, \"I think we should go to the Orange Julius.\""


;; Booleans

;; A boolean is a true or false value, and you type them just like
;; that, `true` and `false`. Often in programming, we need to ask a
;; true or false question, like "Is this class in the current
;; semester?" or "Is this person's birthday today?" When we ask those
;; questions, we get a boolean back.

true
false

;;  There is another value `nil`, which behaves like a boolean in
;;  terms of __truthiness__.  But, `nil` means no value at all and not
;;  a boolean

nil

;;  Keywords are the strangest of the basic value types. Some computer
;;  languages have similar one. However, keywords don’t have a real
;;  world analog like numbers, strings, or booleans.  You can think of
;;  them as a special type of string, one that’s used for labels. They
;;  are often used as keys of key-value pair for maps (data structure;
;;  will learn later).

:forename
:surname
:date-of-birth


;; Assignment

;;  If we had to type the same values over and over, it would be very
;;  hard to write a program. What we need are names for values, so we
;;  can refer to them in a way we can remember. This is called
;;  assignment.

;;  We can assign a name to value using `def`.  When a name is
;;  assigned a value, that name is called a *symbol*.

(def mangoes 3)

(def oranges 5)

(+ mangoes oranges)

;;  You can assign more than simple values to symbols. Try the
;;  following.  Look at the last line, and see how we can use symbols
;;  by themselves to refer to a value.

(def fruit (+ mangoes oranges))

(def average-fruit-amount (/ fruit 2))

average-fruit-amount


;;;; EXERCISE 1: Basic arithmetic

;; * How many minutes have elapsed since you arrived at the workshop today?
;; * Convert this value from minutes to seconds.


;;;; EXERCISE 2 [BONUS]: Minutes and seconds

;; Convert 1000 seconds to minutes and seconds.
;; The minutes and the seconds will be separate numbers.
;; `(quot x y)` will give you the whole number part of x divided by y.
;; `(rem x y)` will give you the remainder of x divided by y.



;;;; Data Structures

;; > So far, we've dealt with discrete pieces of data: one number, one
;; string, one value. When programming, it is more often the case that
;; you want to work with groups of data.

;; > Clojure has great facilities for working with these groups, or
;; *collections*, of data. Not only does it provide four different types
;; of collections, but it also provides a uniform way to use all of these
;; collections together.

;; Vectors

;; > A vector is a sequential collection of values. A vector may be
;; > empty. A vector may contain values of different types.
;; > Each value in a vector is numbered starting at 0, that number is
;; > called its index. The index is used to refer to each value when
;; > looking them up.

;; > To imagine a vector, imagine a box split into some number of
;; > equally-sized compartments. Each of those compartments has a number.
;; > You can put a piece of data inside each compartment and always know
;; > where to find it, as it has a number.


;; > Note that the numbers start with 0. That may seem strange, but we
;; > often count from zero when programming.

;; FIXME IMAGE
;;; ![Vector](img/vector.png)

;; >Vectors are written using square brackets with any number of pieces
;; >of data inside them, separated by spaces. Here are some examples of
;; >vectors:

[1 2 3 4 5]
[56.9 60.2 61.8 63.1 54.3 66.4 66.5 68.1 70.2 69.2 63.1 57.1]
[]

;; Creation

;; > Instead of writing a vector with square brackets, you can also use the vector
;; > function to create a vector. All arguments are collected and placed inside a new
;; > vector.

(vector 5 10 15)

;; > `conj` takes a vector and some other values, and returns a new vector with the
;; > extra value added. `conj` is short for conjoin, which means to join or combine.
;; > This is what we're doing: we're joining the extra value to the vector. `conj`
;; > can be used with any kind of collection. Right now the only kind of
;; > collection we've encountered is a vector.

(conj [5 10] 15)

;; Extraction

;; > Now, take a look at these four functions. `count` gives us a
;; count of the number of items in a vector. `nth` gives us the nth
;; item in the vector. Note that we start counting at 0, so in the
;; example, calling `nth` with the number 1 gives us what we'd call the
;; second element when we aren't programming. `first` returns the first
;; item in the collection. `rest` returns all except the first item.
;; Try not to think about that and `nth` at the same time, as they can
;; be confusing.

(count [5 10 15])

(nth [5 10 15] 1)

(first [5 10 15])

(rest [5 10 15])


;;;; EXERCISE: Make a vector

;; Make a vector of the high temperatures for the next 7 days in the
;; town where you live.  Then use the `nth` function to get the high
;; temperature for next Tuesday.


;;;; Maps

;; >Maps hold a set of keys and values associated with them. You can
;; >think of it like a dictionary: you look up things using a word (a
;; >keyword) and see the definition (its value). If you've programmed in
;; >another language, you might have seen something like maps--maybe
;; >called dictionaries, hashes, or associative arrays.

;; FIXME ![Map](img/map.png)

;; > We write maps by enclosing alternating keys and values in curly braces, like so.

;; > Maps are useful because they can hold data in a way we normally
;; > think about it. Take our made up example, Sally Brown. A map can
;; > hold her first name and last name, her address, her favorite food,
;; > or anything else. It's a simple way to collect that data and make it
;; > easy to look up. The last example is an empty map. It is a map that
;; > is ready to hold some things, but doesn't have anything in it yet.

;; {:first "Sally" :last "Brown"}
;; {:a 1 :b "two"}
;; {}

;; > `assoc` and `dissoc` are paired functions: they associate and disassociate items from a map. See how we add the last name "Brown" to the map with `assoc`, and then we remove it with `dissoc`. `merge` merges two maps together to make a new map.

(assoc {:first "Sally"} :last "Brown")

(dissoc {:first "Sally" :last "Brown"} :last)

(merge {:first "Sally"} {:last "Brown"})

;; > `count` every collection has this function. Why do you think the
;; > answer is two? `count` is returning the number of associations.

;; > Since map is a key-value pair, the key is used to get a value from a
;; > map. One of the ways often used in Clojure is the examples below.
;; > We can use a keyword like using a function in order to look
;; > up values in a map. In the last example, we supplied the key `:MISS`.
;; > This works when the key we asked for is not in the map.

(count {:first "Sally" :last "Brown"})

(get {:first "Sally" :last "Brown"} :first)

(get {:first "Sally"} :last)

(get {:first "Sally"} :last :MISS)

;; > Then we have `keys` and `vals`, which are pretty simple: they return
;; > the keys and values in the map. The order is not guaranteed, so we
;; > could have gotten `(:first :last)` or `(:last :first)`.

(keys {:first "Sally" :last "Brown"})

(vals {:first "Sally" :last "Brown"})

;; > After the creation, we want to save a new value associated to the
;; > key. The `assoc` function can be used by assigning a new value to
;; > the existing key.
;; > Also, there's handy function `update`. The function takes map and
;; > a key with a function. The value of specified key will be the first
;; > argument of the given function.
;; > The `update-in` function works like `update`, but takes a vector of keys
;; > to update at a path to a nested map.

(def hello {:count 1 :words "hello"})

(update hello :count inc)

(update hello :words str ", world")


(def mine {:pet {:age 5 :name "able"}})

(update-in mine [:pet :age] - 3)


;; Collections of Collections

;; > Simple values such as numbers, keywords, and strings are not the
;; > only types of things you can put into collections. You can also put
;; > other collections into collections, so you can have a vector of
;; > maps, or a list of vectors, or whatever combination fits your data.

;; Vector of Maps

(def characters
  [{:name "Snoopy"
    :species "dog"}
   {:name "Woodstock"
    :species "bird"}
   {:name "Charlie Brown"
    :species "human"}])

(:name (first characters))

(map :name characters)


;; EXERCISE: Modeling Yourself

;; * Make a map representing yourself
;; * Make sure it contains your first name and last name
;; * Then, add your hometown to the map using [assoc](http://grimoire.arrdem.com/1.6.0/clojure.core/assoc/) or [merge](http://grimoire.arrdem.com/1.6.0/clojure.core/merge/).

;; EXERCISE [BONUS]: Modeling your classmates

;; * First, take the map you made about yourself in previous exercise.
;; * Then, create a vector of maps containing the first name, last name and hometown of two or three other classmates around you.
;; * Lastly, add your map to their information using [conj](http://clojuredocs.org/clojure.core/conj).
