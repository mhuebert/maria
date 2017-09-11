
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
