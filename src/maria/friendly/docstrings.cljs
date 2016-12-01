(ns maria.friendly.docstrings)

(def docstrings
  '{cljs.core

    {js->clj
     "Recursively transforms JavaScript arrays into ClojureScript
    vectors, and JavaScript objects into ClojureScript maps.  With
    option ':keywordize-keys true' will convert object fields from
    strings to keywords."

     sort-by
     "Returns a sorted sequence of the items in coll, where the sort
     order is determined by comparing (keyfn item).  Comp can be
     boolean-valued comparison funcion, or a -/0/+ valued comparator.
     Comp defaults to compare."

     ITransientAssociative
     "Protocol for adding associativity to transient collections."

     pr-str*
     "Support so that collections can implement toString without
     loading all the printing machinery."

     eduction
     "Returns a reducible/iterable application of the transducers
    to the items in coll. Transducers are applied in order as if
    combined with comp. Note that these applications will be
    performed every time reduce/iterator is called."

     tree-seq
     "Returns a lazy sequence of the nodes in a tree, via a depth-first walk.
    branch? must be a fn of one arg that returns true if passed a node
    that can have children (but may not).  children must be a fn of one
    arg that returns a sequence of the children. Will only be called on
    nodes for which branch? returns true. Root is the root node of the
    tree."

     seq
     "Returns a seq on the collection. If the collection is
    empty, returns nil.  (seq nil) returns nil. seq also works on
    Strings."

     reduce
     "f should be a function of 2 arguments. If val is not supplied,
    returns the result of applying f to the first 2 items in coll, then
    applying f to that result and the 3rd item, etc. If coll contains no
    items, f must accept no arguments as well, and reduce returns the
    result of calling f with no arguments.  If coll has only 1 item, it
    is returned and f is not called.  If val is supplied, returns the
    result of applying f to val and the first item in coll, then
    applying f to that result and the 2nd item, etc. If coll contains no
    items, returns val and f is not called."

     IUUID
     "A marker protocol for UUIDs"

     find-ns
     "Bootstrap only."

     contains?
     "Returns true if key is present in the given collection, otherwise
    returns false.  Note that for numerically indexed collections like
    vectors and arrays, this tests if the numeric key is within the
    range of indexes. 'contains?' operates constant or logarithmic time;
    it will not perform a linear search for a value.  See also 'some'."

     every?
     "Returns true if (pred x) is logical true for every x in coll, else
    false."

     keep-indexed
     "Returns a lazy sequence of the non-nil results of (f index item). Note,
    this means false return values will be included.  f must be free of
    side-effects.  Returns a stateful transducer when no collection is
    provided."

     subs
     "Returns the substring of s beginning at start inclusive, and ending
    at end (defaults to length of string), exclusive."

     set
     "Returns a set of the distinct elements of coll."

     compare-indexed
     "Compare indexed collection."

     take-last
     "Returns a seq of the last n items in coll.  Depending on the type
    of coll may be no better than linear time.  For vectors, see also subvec."

     bit-set
     "Set bit at index n"

     qualified-keyword?
     "Return true if x is a keyword with a namespace"

     -with-meta
     "Returns a new object with value of o and metadata meta added to it."

     butlast
     "Return a seq of all but the last item in coll, in linear time"

     unchecked-subtract-int
     "If no ys are supplied, returns the negation of x, else subtracts
    the ys from x and returns the result."

     -iterator
     "Returns an iterator for coll."

     take-nth
     "Returns a lazy seq of every nth item in coll.  Returns a stateful
    transducer when no collection is provided."

     first
     "Returns the first item in the collection. Calls seq on its
    argument. If coll is nil, returns nil."

     native-satisfies?
     "Internal - do not use!"

     seq?
     "Return true if s satisfies ISeq"

     -sorted-seq-from
     "Returns a sorted seq from coll in either ascending or descending order.
       If ascending is true, the result should contain all items which are > or >=
       than k. If ascending is false, the result should contain all items which
       are < or <= than k, e.g.
       (-sorted-seq-from (sorted-set 1 2 3 4 5) 3 true) => (3 4 5)
       (-sorted-seq-from (sorted-set 1 2 3 4 5) 3 false) => (3 2 1)"

     println-str
     "println to a string, returning it"

     inst-ms
     "Return the number of milliseconds since January 1, 1970, 00:00:00 GMT"

     iterate
     "Returns a lazy sequence of x, (f x), (f (f x)) etc. f must be free of side-effects"

     -empty
     "Returns an empty collection of the same category as coll. Used
       by cljs.core/empty."

     newline
     "Prints a newline using *print-fn*"

     ILookup
     "Protocol for looking up a value in a data structure."

     -chunked-rest
     "Return a new collection of coll with the first chunk removed."

     fn?
     "Return true if f is a JavaScript function or satisfies the Fn protocol."

     -assoc
     "Returns a new collection of coll with a mapping from key k to
       value v added to it."

     doall
     "When lazy sequences are produced via functions that have side
    effects, any effects other than those needed to produce the first
    element in the seq do not occur until the seq is consumed. doall can
    be used to force any effects. Walks through the successive nexts of
    the seq, retains the head and returns it, thus causing the entire
    seq to reside in memory at one time."

     keyword-identical?
     "Efficient test to determine that two keywords are identical."

     *print-err-fn*
     "Each runtime environment provides a different way to print error output.
    Whatever function *print-err-fn* is bound to will be passed any
    Strings which should be printed."

     prefers
     "Given a multimethod, returns a map of preferred value -> set of other values"

     -js->clj
     "Transforms JavaScript values to Clojure"

     dedupe
     "Returns a lazy sequence removing consecutive duplicates in coll.
    Returns a transducer when no collection is provided."

     dissoc
     "dissoc[iate]. Returns a new map of the same (hashed/sorted) type,
    that does not contain a mapping for key(s)."

     bit-shift-right
     "Bitwise shift right"

     -first
     "Returns the first item in the collection coll. Used by cljs.core/first."

     peek
     "For a list or queue, same as first, for a vector, same as, but much
    more efficient than, last. If the collection is empty, returns nil."

     IKVReduce
     "Protocol for associative types that can reduce themselves
    via a function of key and val. Called by cljs.core/reduce-kv."

     aget
     "Returns the value at the index."

     -write
     "Writes s with writer and returns the result."

     last
     "Return the last item in coll, in linear time"

     pr
     "Prints the object(s) using string-print.  Prints the
    object(s), separated by spaces if there is more than one.
    By default, pr and prn print in a way that objects can be
    read by the reader"

     namespace
     "Returns the namespace String of a symbol or keyword, or nil if not present."

     obj-map
     "keyval => key val
    Returns a new object map with supplied mappings."

     -conj
     "Returns a new collection of coll with o added to it. The new item
       should be added to the most efficient place, e.g.
       (conj [1 2 3 4] 5) => [1 2 3 4 5]
       (conj '(2 3 4 5) 1) => '(1 2 3 4 5)"

     =
     "Equality. Returns true if x equals y, false if not. Compares
    numbers and collections in a type-independent manner.  Clojure's immutable data
    structures define -equiv (and thus =) as a value, not an identity,
    comparison."

     ITransientMap
     "Protocol for adding mapping functionality to transient collections."

     take
     "Returns a lazy sequence of the first n items in coll, or all items if
    there are fewer than n.  Returns a stateful transducer when
    no collection is provided."

     vector?
     "Return true if x satisfies IVector"

     boolean
     "Coerce to boolean"

     IChunk
     "Protocol for accessing the items of a chunk."

     bit-shift-left
     "Bitwise shift left"

     any?
     "Returns true if given any argument."

     rand-int
     "Returns a random whole number, from 0 up to (but not including) n."

     aclone
     "Returns a javascript array, cloned from the passed in array"

     vreset!
     "Sets the value of volatile to newval without regard for the
     current value. Returns newval."

     dec
     "Returns a number one less than num."

     map
     "Returns a lazy sequence consisting of the result of applying f to
    the set of first items of each coll, followed by applying f to the
    set of second items in each coll, until any one of the colls is
    exhausted.  Any remaining items in other colls are ignored. Function
    f should accept number-of-colls arguments. Returns a transducer when
    no collection is provided."

     juxt
     "Takes a set of functions and returns a fn that is the juxtaposition
    of those fns.  The returned fn takes a variable number of args, and
    returns a vector containing the result of applying each fn to the
    args (left-to-right).
    ((juxt a b c) x) => [(a x) (b x) (c x)]"

     <
     "Returns non-nil if nums are in monotonically increasing order,
    otherwise false."

     test
     "test [v] finds fn at key :test in var metadata and calls it,
    presuming failure will throw exception"

     rest
     "Returns a possibly empty seq of the items after the first. Calls seq on its
    argument."

     ex-data
     "Returns exception data (a map) if ex is an ExceptionInfo.
    Otherwise returns nil."

     -drop-first
     "Return a new chunk of coll with the first item removed."

     isa?
     "Returns true if (= child parent), or child is directly or indirectly derived from
    parent, either via a JavaScript type inheritance relationship or a
    relationship established via derive. h must be a hierarchy obtained
    from make-hierarchy, if not supplied defaults to the global
    hierarchy"

     boolean?
     "Return true if x is a Boolean"

     -clone
     "Creates a clone of value."

     re-seq
     "Returns a lazy sequence of successive matches of re in s."

     char?
     "Returns true if x is a JavaScript string of length one."

     make-hierarchy
     "Creates a hierarchy object for use with derive, isa? etc."

     -reduce
     "f should be a function of 2 arguments. If start is not supplied,
       returns the result of applying f to the first 2 items in coll, then
       applying f to that result and the 3rd item, etc."

     -count
     "Calculates the count of coll in constant time. Used by cljs.core/count."

     keep
     "Returns a lazy sequence of the non-nil results of (f item). Note,
    this means false return values will be included.  f must be free of
    side-effects.  Returns a transducer when no collection is provided."

     char
     "Coerce to char"

     mapcat
     "Returns the result of applying concat to the result of applying map
    to f and colls.  Thus function f should return a collection. Returns
    a transducer when no collections are provided"

     unchecked-long
     "Coerce to long by stripping decimal places. Identical to `int'."

     some?
     "Returns true if x is not nil, false otherwise."

     symbol-identical?
     "Efficient test to determine that two symbols are identical."

     reverse
     "Returns a seq of the items in coll in reverse order. Not lazy."

     inst?
     "Return true if x satisfies Inst"

     range
     "Returns a lazy seq of nums from start (inclusive) to end
     (exclusive), by step, where start defaults to 0, step to 1,
     and end to infinity."

     bit-count
     "Counts the number of bits set in n"

     sort
     "Returns a sorted sequence of the items in coll. Comp can be
     boolean-valued comparison function, or a -/0/+ valued comparator.
     Comp defaults to compare."

     -compare
     "Returns a negative number, zero, or a positive number when x is logically
       'less than', 'equal to', or 'greater than' y."

     map-indexed
     "Returns a lazy sequence consisting of the result of applying f to 0
    and the first item of coll, followed by applying f to 1 and the second
    item in coll, etc, until coll is exhausted. Thus function f should
    accept 2 arguments, index and item. Returns a stateful transducer when
    no collection is provided."

     rand-nth
     "Return a random element of the (sequential) collection. Will have
    the same performance characteristics as nth for the given
    collection."

     comp
     "Takes a set of functions and returns a fn that is the composition
    of those fns.  The returned fn takes a variable number of args,
    applies the rightmost of fns to the args, the next
    fn (right-to-left) to the result, etc."

     dispatch-fn
     "Given a multimethod, return it's dispatch-fn."

     bit-shift-right-zero-fill
     "DEPRECATED: Bitwise shift right with zero fill"

     -as-transient
     "Returns a new, transient version of the collection, in constant time."

     dorun
     "When lazy sequences are produced via functions that have side
    effects, any effects other than those needed to produce the first
    element in the seq do not occur until the seq is consumed. dorun can
    be used to force any effects. Walks through the successive nexts of
    the seq, does not retain the head and returns nil."

     simple-symbol?
     "Return true if x is a symbol without a namespace"

     IIndexed
     "Protocol for collections to provide indexed-based access to their items."

     disj
     "disj[oin]. Returns a new set of the same (hashed/sorted) type, that
    does not contain key(s)."

     IPrintWithWriter
     "The old IPrintable protocol's implementation consisted of building a giant
     list of strings to concatenate.  This involved lots of concat calls,
     intermediate vectors, and lazy-seqs, and was very slow in some older JS
     engines.  IPrintWithWriter implements printing via the IWriter protocol, so it
     be implemented efficiently in terms of e.g. a StringBuffer append."

     IVector
     "Protocol for adding vector functionality to collections."

     IIterable
     "Protocol for iterating over a collection."

     *2
     "bound in a repl thread to the second most recent value printed"

     cons
     "Returns a new seq where x is the first element and coll is the rest."

     ns-lookup
     "Bootstrap only."

     pos?
     "Returns true if num is greater than zero, else false"

     fnil
     "Takes a function f, and returns a function that calls f, replacing
    a nil first argument to f with the supplied value x. Higher arity
    versions can replace arguments in the second and third
    positions (y, z). Note that the function f can take any number of
    arguments, not just the one(s) being nil-patched."

     merge-with
     "Returns a map that consists of the rest of the maps conj-ed onto
    the first.  If a key occurs in more than one map, the mapping(s)
    from the latter (left-to-right) will be combined with the mapping in
    the result by calling (f val-in-result val-in-latter)."

     nthrest
     "Returns the nth rest of coll, coll when n is 0."

     sequential?
     "Returns true if coll satisfies ISequential"

     prim-seq
     "Create seq from a primitive JavaScript Array-like."

     *print-level*
     "*print-level* controls how many levels deep the printer will
    print nested objects. If it is bound to logical false, there is no
    limit. Otherwise, it must be bound to an integer indicating the maximum
    level to print. Each argument to print is at level 0; if an argument is a
    collection, its items are at level 1; and so on. If an object is a
    collection and is at a level greater than or equal to the value bound to
    *print-level*, the printer prints '#' to represent it. The root binding
    is nil indicating no limit."

     shuffle
     "Return a random permutation of coll"

     find
     "Returns the map entry for key, or nil if key not present."

     alength
     "Returns the length of the array. Works on arrays of all types."

     bit-xor
     "Bitwise exclusive or"

     unsigned-bit-shift-right
     "Bitwise shift right with zero fill"

     neg?
     "Returns true if num is less than zero, else false"

     js-invoke
     "Invoke JavaScript object method via string. Needed when the
    string is not a valid unquoted property name."

     undefined?
     "Returns true if x identical to the JavaScript undefined value."

     IMeta
     "Protocol for accessing the metadata of an object."

     reduced?
     "Returns true if x is the result of a call to reduced"

     disj!
     "disj[oin]. Returns a transient set of the same (hashed/sorted) type, that
    does not contain key(s)."

     -lookup
     "Use k to look up a value in o. If not-found is supplied and k is not
       a valid value that can be used for look up, not-found is returned."

     float?
     "Returns true for JavaScript numbers, false otherwise."

     ICloneable
     "Protocol for cloning a value."

     int-array
     "Creates an array of ints. Does not coerce array, provided for compatibility
    with Clojure."

     set?
     "Returns true if x satisfies ISet"

     iterable?
     "Return true if x implements IIterable protocol."

     cat
     "A transducer which concatenates the contents of each input, which must be a
    collection, into the reduction."

     take-while
     "Returns a lazy sequence of successive items from coll while
    (pred item) returns true. pred must be free of side-effects.
    Returns a transducer when no collection is provided."

     vary-meta
     "Returns an object of the same type and value as obj, with
    (apply f (meta obj) args) as its metadata."

     INext
     "Protocol for accessing the next items of a collection."

     ICounted
     "Protocol for adding the ability to count a collection in constant time."

     IMapEntry
     "Protocol for examining a map entry."

     <=
     "Returns non-nil if nums are in monotonically non-decreasing order,
    otherwise false."

     conj!
     "Adds val to the transient collection, and return tcoll. The 'addition'
    may happen at different 'places' depending on the concrete type."

     -pop
     "Returns a new stack without the item on top of the stack. Is used
       by cljs.core/pop."

     repeatedly
     "Takes a function of no args, presumably with side effects, and
    returns an infinite (or length n if supplied) lazy sequence of calls
    to it"

     zipmap
     "Returns a map with the keys mapped to the corresponding vals."

     IStack
     "Protocol for collections to provide access to their items as stacks. The top
    of the stack should be accessed in the most efficient way for the different
    data structures."

     -remove-watch
     "Removes watcher that corresponds to key from this."

     IVolatile
     "Protocol for adding volatile functionality."

     remove
     "Returns a lazy sequence of the items in coll for which
    (pred item) returns false. pred must be free of side-effects.
    Returns a transducer when no collection is provided."

     *
     "Returns the product of nums. (*) returns 1."

     re-pattern
     "Returns an instance of RegExp which has compiled the provided string."

     min
     "Returns the least of the nums."

     -persistent!
     "Creates a persistent data structure from tcoll and returns it."

     -nth
     "Returns the value at the index n in the collection coll.
       Returns not-found if index n is out of bounds and not-found is supplied."

     pop!
     "Removes the last item from a transient vector. If
    the collection is empty, throws an exception. Returns tcoll"

     prn-str
     "Same as pr-str followed by (newline)"

     IReversible
     "Protocol for reversing a seq."

     reversible?
     "Returns true if coll satisfies? IReversible."

     -realized?
     "Returns true if a value for x has been produced, false otherwise."

     -add-watch
     "Adds a watcher function f to this. Keys must be unique per reference,
       and can be used to remove the watch with -remove-watch."

     conj
     "conj[oin]. Returns a new collection with the xs
    'added'. (conj nil item) returns (item).  The 'addition' may
    happen at different 'places' depending on the concrete type."

     -sorted-seq
     "Returns a sorted seq from coll in either ascending or descending order."

     flatten1
     "Take a collection of collections, and return a lazy seq
    of items from the inner collection"

     transduce
     "reduce with a transformation of f (xf). If init is not
    supplied, (f) will be called to produce it. f should be a reducing
    step function that accepts both 1 and 2 arguments, if it accepts
    only 2 you can add the arity-1 with 'completing'. Returns the result
    of applying (the transformed) xf to init and the first item in coll,
    then applying xf to that result and the 2nd item, etc. If coll
    contains no items, returns init and f is not called. Note that
    certain transforms may inject or skip items."

     -swap!
     "Swaps the value of o to be (apply f current-value-of-atom args)."

     *print-length*
     "*print-length* controls how many items of each collection the
    printer will print. If it is bound to logical false, there is no
    limit. Otherwise, it must be bound to an integer indicating the maximum
    number of items of each collection to print. If a collection contains
    more items, the printer will print items up to the limit followed by
    '...' to represent the remaining items. The root binding is nil
    indicating no limit."

     js-delete
     "Delete a property from a JavaScript object."

     truth_
     "Internal - do not use!"

     compare-and-set!
     "Atomically sets the value of atom to newval if and only if the
    current value of the atom is equal to oldval. Returns true if
    set happened, else false."

     array-seq
     "Create a seq from a JavaScript array."

     interleave
     "Returns a lazy seq of the first item in each coll, then the second etc."

     map?
     "Return true if x satisfies IMap"

     get
     "Returns the value mapped to key, not-found or nil if key not present."

     identity
     "Returns its argument."

     into
     "Returns a new coll consisting of to-coll with all of the items of
    from-coll conjoined. A transducer may be supplied."

     long
     "Coerce to long by stripping decimal places. Identical to `int'."

     volatile?
     "Returns true if x is a volatile."

     -key
     "Returns the key of the map entry."

     nfirst
     "Same as (next (first x))"

     meta
     "Returns the metadata of obj, returns nil if there is no metadata."

     -kv-reduce
     "Reduces an associative collection and returns the result. f should be
       a function that takes three arguments."

     IHash
     "Protocol for adding hashing functionality to a type."

     bit-and-not
     "Bitwise and with complement"

     var?
     "Returns true if v is of type cljs.core.Var"

     -comparator
     "Returns the comparator for coll."

     unchecked-add-int
     "Returns the sum of nums. (+) returns 0."

     hash-ordered-coll
     "Returns the hash code, consistent with =, for an external ordered
     collection implementing Iterable.
     See http://clojure.org/data_structures#hash for full algorithms."

     extend-object!
     "Takes a JavaScript object and a map of names to functions and
    attaches said functions as methods on the object.  Any references to
    JavaScript's implicit this (via the this-as macro) will resolve to the
    object that the function is attached."

     reset-meta!
     "Atomically resets the metadata for an atom"

     IEquiv
     "Protocol for adding value comparison functionality to a type."

     cycle
     "Returns a lazy (infinite!) sequence of repetitions of the items in coll."

     -deref
     "Returns the value of the reference o."

     empty?
     "Returns true if coll has no items - same as (not (seq coll)).
    Please use the idiom (seq x) rather than (not (empty? x))"

     -clj->js
     "Recursively transforms clj values to JavaScript"

     -chunked-first
     "Returns the first chunk in coll."

     filterv
     "Returns a vector of the items in coll for which
    (pred item) returns true. pred must be free of side-effects."

     hash
     "Returns the hash code of its argument. Note this is the hash code
     consistent with =."

     quot
     "quot[ient] of dividing numerator by denominator."

     ns-interns*
     "Bootstrap only."

     *target*
     "Var bound to the name value of the compiler build :target option.
    For example, if the compiler build :target is :nodejs, *target* will be bound
    to \"nodejs\". *target* is a Google Closure define and can be set by compiler
    :closure-defines option."

     ITransientVector
     "Protocol for adding vector functionality to transient collections."

     key
     "Returns the key of the map entry."

     not=
     "Same as (not (= obj1 obj2))"

     set-print-err-fn!
     "Set *print-err-fn* to f."

     string?
     "Returns true if x is a JavaScript string."

     es6-iterator
     "EXPERIMENTAL: Return a ES2015 compatible iterator for coll."

     pr-str-with-opts
     "Prints a sequence of objects to a string, observing all the
    options given in opts"

     *print-newline*
     "When set to logical false will drop newlines from printing calls.
    This is to work around the implicit newlines emitted by standard JavaScript
    console objects."

     unchecked-multiply-int
     "Returns the product of nums. (*) returns 1."

     remove-all-methods
     "Removes all of the methods of multimethod."

     trampoline
     "trampoline can be used to convert algorithms requiring mutual
    recursion without stack consumption. Calls f with supplied args, if
    any. If f returns a fn, calls that fn with no arguments, and
    continues to repeat, until the return value is not a fn, then
    returns that non-fn value. Note that if you want to return a fn as a
    final value, you must wrap it in some data structure and unpack it
    after trampoline returns."

     double?
     "Returns true for JavaScript numbers, false otherwise."

     *1
     "bound in a repl thread to the most recent value printed"

     vec
     "Creates a new vector containing the contents of coll. JavaScript arrays
    will be aliased and should not be modified."

     *print-meta*
     "If set to logical true, when printing an object, its metadata will also
    be printed in a form that can be read back by the reader.

    Defaults to false."

     -notify-watches
     "Calls all watchers with this, oldval and newval."

     int
     "Coerce to int by stripping decimal places."

     rand
     "Returns a random floating point number between 0 (inclusive) and
    n (default 1) (exclusive)."

     second
     "Same as (first (next x))"

     find-ns-obj
     "Bootstrap only."

     IEditableCollection
     "Protocol for collections which can transformed to transients."

     >
     "Returns non-nil if nums are in monotonically decreasing order,
    otherwise false."

     -name
     "Returns the name String of x."

     replace
     "Given a map of replacement pairs and a vector/collection, returns a
    vector/seq with any elements = a key in smap replaced with the
    corresponding val in smap.  Returns a transducer when no collection
    is provided."

     int?
     "Return true if x satisfies integer? or is an instance of goog.math.Integer
     or goog.math.Long."

     associative?
     "Returns true if coll implements Associative"

     unchecked-int
     "Coerce to int by stripping decimal places."

     js-keys
     "Return the JavaScript keys for an object."

     keyword?
     "Return true if x is a Keyword"

     force
     "If x is a Delay, returns the (possibly cached) value of its expression, else returns x"

     group-by
     "Returns a map of the elements of coll keyed by the result of
    f on each element. The value at each key will be a vector of the
    corresponding elements, in the order they appeared in coll."

     -rseq
     "Returns a seq of the items in coll in reversed order."

     prn
     "Same as pr followed by (newline)."

     default-dispatch-val
     "Given a multimethod, return it's default-dispatch-val."

     unchecked-multiply
     "Returns the product of nums. (*) returns 1."

     even?
     "Returns true if n is even, throws an exception if n is not an integer"

     es6-iterator-seq
     "EXPERIMENTAL: Given an ES2015 compatible iterator return a seq."

     unchecked-dec
     "Returns a number one less than x, an int."

     tagged-literal?
     "Return true if the value is the data representation of a tagged literal"

     double-array
     "Creates an array of doubles. Does not coerce array, provided for compatibility
    with Clojure."

     create-ns
     "Bootstrap only."

     rseq
     "Returns, in constant time, a seq of the items in rev (which
    can be a vector or sorted-map), in reverse order. If rev is empty returns nil"

     ex-cause
     "Returns exception cause (an Error / ExceptionInfo) if ex is an
    ExceptionInfo.
    Otherwise returns nil."

     IReset
     "Protocol for adding resetting functionality."

     IEmptyableCollection
     "Protocol for creating an empty collection."

     ex-message
     "Returns the message attached to the given Error / ExceptionInfo object.
    For non-Errors returns nil."

     IRecord
     "Marker interface indicating a record object"

     pr-str
     "pr to a string, returning it. Fundamental entrypoint to IPrintWithWriter."

     concat
     "Returns a lazy seq representing the concatenation of the elements in the supplied colls."

     symbol
     "Returns a Symbol with the given namespace and name."

     to-array-2d
     "Returns a (potentially-ragged) 2-dimensional array
    containing the contents of coll."

     mod
     "Modulus of num and div. Truncates toward negative infinity."

     ISet
     "Protocol for adding set functionality to a collection."

     pop
     "For a list or queue, returns a new list/queue without the first
    item, for a vector, returns a new vector without the last item.
    Note - not the same as next/butlast."

     IPending
     "Protocol for types which can have a deferred realization. Currently only
    implemented by Delay and LazySeq."

     -entry-key
     "Returns the key for entry."

     dissoc!
     "Returns a transient map that doesn't contain a mapping for key(s)."

     reductions
     "Returns a lazy seq of the intermediate values of the reduction (as
    per reduce) of coll by f, starting with init."

     indexed?
     "Returns true if coll implements nth in constant time"

     -
     "If no ys are supplied, returns the negation of x, else subtracts
    the ys from x and returns the result."

     -equiv
     "Returns true if o and other are equal, false otherwise."

     assoc!
     "When applied to a transient map, adds mapping of key(s) to
    val(s). When applied to a transient vector, sets the val at index.
    Note - index must be <= (count vector). Returns coll."

     hash-set
     "Returns a new hash set with supplied keys.  Any equal keys are
    handled as if by repeated uses of conj."

     reduce-kv
     "Reduces an associative collection. f should be a function of 3
    arguments. Returns the result of applying f to init, the first key
    and the first value in coll, then applying f to that result and the
    2nd key and value, etc. If coll contains no entries, returns init
    and f is not called. Note that reduce-kv is supported on vectors,
    where the keys will be the ordinals."

     name
     "Returns the name String of a string, symbol or keyword."

     Fn
     "Marker protocol"

     ffirst
     "Same as (first (first x))"

     sorted-set
     "Returns a new sorted set with supplied keys."

     pr-with-opts
     "Prints a sequence of objects using string-print, observing all
    the options given in opts"

     counted?
     "Returns true if coll implements count in constant time"

     tagged-literal
     "Construct a data representation of a tagged literal from a
    tag symbol and a form."

     println
     "Same as print followed by (newline)"

     assoc-in
     "Associates a value in a nested associative structure, where ks is a
    sequence of keys and v is the new value and returns a new nested structure.
    If any levels do not exist, hash-maps will be created."

     bit-test
     "Test bit at index n"

     ISwap
     "Protocol for adding swapping functionality."

     memoize
     "Returns a memoized version of a referentially transparent function. The
    memoized version of the function keeps a cache of the mapping from arguments
    to results and, when calls with the same arguments are repeated often, has
    higher performance at the expense of higher memory use."

     alter-meta!
     "Atomically sets the metadata for a namespace/var/ref/agent/atom to be:

    (apply f its-current-meta args)

    f must be free of side-effects"

     zero?
     "Returns true if num is zero, else false"

     simple-keyword?
     "Return true if x is a keyword without a namespace"

     *main-cli-fn*
     "When compiled for a command-line target, whatever function
    *main-cli-fn* is set to will be called with the command-line
    argv as arguments"

     -assoc-n
     "Returns a new vector with value val added at position n."

     unchecked-dec-int
     "Returns a number one less than x, an int."

     persistent!
     "Returns a new, persistent version of the transient collection, in
    constant time. The transient collection cannot be used after this
    call, any such use will throw an exception."

     set-print-fn!
     "Set *print-fn* to f."

     nnext
     "Same as (next (next x))"

     add-watch
     "Adds a watch function to an atom reference. The watch fn must be a
    fn of 4 args: a key, the reference, its old-state, its
    new-state. Whenever the reference's state might have been changed,
    any registered watches will have their functions called. The watch
    fn will be called synchronously. Note that an atom's state
    may have changed again prior to the fn call, so use old/new-state
    rather than derefing the reference. Keys must be unique per
    reference, and can be used to remove the watch with remove-watch,
    but are otherwise considered opaque by the watch mechanism.  Bear in
    mind that regardless of the result or action of the watch fns the
    atom's value will change.  Example:

        (def a (atom 0))
        (add-watch a :inc (fn [k r o n] (assert (== 0 n))))
        (swap! a inc)
        ;; Assertion Error
        (deref a)
        ;=> 1"

     not-every?
     "Returns false if (pred x) is logical true for every x in
    coll, else true."

     rem
     "remainder of dividing numerator by denominator."

     some
     "Returns the first logical true value of (pred x) for any x in coll,
    else nil.  One common idiom is to use a set as pred, for example
    this will return :fred if :fred is in the sequence, otherwise nil:
    (some #{:fred} coll)"

     INamed
     "Protocol for adding a name."

     IReduce
     "Protocol for seq types that can reduce themselves.
    Called by cljs.core/reduce."

     neg-int?
     "Return true if x satisfies int? and is positive."

     drop
     "Returns a lazy sequence of all but the first n items in coll.
    Returns a stateful transducer when no collection is provided."

     js-obj
     "Create JavaSript object from an even number arguments representing
    interleaved keys and values."

     ITransientCollection
     "Protocol for adding basic functionality to transient collections."

     nth
     "Returns the value at the index. get returns nil if index out of
    bounds, nth throws an exception unless not-found is supplied.  nth
    also works for strings, arrays, regex Matchers and Lists, and,
    in O(n) time, for sequences."

     sorted?
     "Returns true if coll satisfies ISorted"

     nil?
     "Returns true if x is nil, false otherwise."

     split-at
     "Returns a vector of [(take n coll) (drop n coll)]"

     *e
     "bound in a repl thread to the most recent exception caught by the repl"

     prn-str-with-opts
     "Same as pr-str-with-opts followed by (newline)"

     random-sample
     "Returns items from coll with random probability of prob (0.0 -
    1.0).  Returns a transducer when no collection is provided."

     select-keys
     "Returns a map containing only those entries in map whose key is in keys"

     bit-and
     "Bitwise and"

     bounded-count
     "If coll is counted? returns its count, else will count at most the first n
     elements of coll using its seq"

     update
     "'Updates' a value in an associative structure, where k is a
    key and f is a function that will take the old value
    and any supplied args and return the new value, and returns a new
    structure.  If the key does not exist, nil is passed as the old value."

     find-macros-ns
     "Bootstrap only."

     list*
     "Creates a new list containing the items prepended to the rest, the
    last of which will be treated as a sequence."

     update-in
     "'Updates' a value in a nested associative structure, where ks is a
    sequence of keys and f is a function that will take the old value
    and any supplied args and return the new value, and returns a new
    nested structure.  If any levels do not exist, hash-maps will be
    created."

     prefer-method
     "Causes the multimethod to prefer matches of dispatch-val-x over dispatch-val-y
     when there is a conflict"

     ensure-reduced
     "If x is already reduced?, returns it, else returns (reduced x)"

     instance?
     "Evaluates x and tests if it is an instance of the type
    c. Returns true or false"

     mix-collection-hash
     "Mix final collection hash for ordered or unordered collections.
     hash-basis is the combined collection hash, count is the number
     of elements included in the basis. Note this is the hash code
     consistent with =, different from .hashCode.
     See http://clojure.org/data_structures#hash for full algorithms."

     re-find
     "Returns the first regex match, if any, of s to re, using
    re.exec(s). Returns a vector, containing first the matching
    substring, then any capturing groups if the regular expression contains
    capturing groups."

     run!
     "Runs the supplied procedure (via reduce), for purposes of side
    effects, on successive items in the collection. Returns nil"

     val
     "Returns the value in the map entry."

     unchecked-add
     "Returns the sum of nums. (+) returns 0."

     not
     "Returns true if x is logical false, false otherwise."

     -vreset!
     "Sets the value of volatile o to new-value without regard for the
       current value. Returns new-value."

     fn->comparator
     "Given a fn that might be boolean valued or a comparator,
     return a fn that is a comparator."

     with-meta
     "Returns an object of the same type and value as obj, with
    map m as its metadata."

     unreduced
     "If x is reduced?, returns (deref x), else returns x"

     record?
     "Return true if x satisfies IRecord"

     type
     "Return x's constructor."

     identical?
     "Tests if 2 arguments are the same object"

     -namespace
     "Returns the namespace String of x."

     unchecked-divide-int
     "If no denominators are supplied, returns 1/numerator,
    else returns numerator divided by all of the denominators."

     ns-name
     "Bootstrap only."

     max-key
     "Returns the x for which (k x), a number, is greatest."

     set-validator!
     "Sets the validator-fn for an atom. validator-fn must be nil or a
    side-effect-free fn of one argument, which will be passed the intended
    new state on any state change. If the new state is unacceptable, the
    validator-fn should return false or throw an Error. If the current state
    is not acceptable to the new validator, an Error will be thrown and the
    validator will not be changed."

     ident?
     "Return true if x is a symbol or keyword"

     -meta
     "Returns the metadata of object o."

     swap!
     "Atomically swaps the value of atom to be:
    (apply f current-value-of-atom args). Note that f may be called
    multiple times, and thus should be free of side effects.  Returns
    the value that was swapped in."

     vals
     "Returns a sequence of the map's values."

     -chunked-next
     "Returns a new collection of coll without the first chunk."

     unchecked-subtract
     "If no ys are supplied, returns the negation of x, else subtracts
    the ys from x and returns the result."

     IMap
     "Protocol for adding mapping functionality to collections."

     sorted-set-by
     "Returns a new sorted set with supplied keys, using the supplied comparator."

     cloneable?
     "Return true if x implements ICloneable protocol."

     qualified-ident?
     "Return true if x is a symbol or keyword with a namespace"

     true?
     "Returns true if x is the value true, false otherwise."

     find-ns-obj*
     "Bootstrap only."

     array
     "Creates a new javascript array.
  @param {...*} var_args"

     print
     "Prints the object(s) using string-print.
    print and println produce output for human consumption."

     -peek
     "Returns the item from the top of the stack. Is used by cljs.core/peek."

     ISeq
     "Protocol for collections to provide access to their items as sequences."

     empty
     "Returns an empty collection of the same category as coll, or nil"

     remove-method
     "Removes the method of multimethod associated with dispatch-value."

     volatile!
     "Creates and returns a Volatile with an initial value of val."

     /
     "If no denominators are supplied, returns 1/numerator,
    else returns numerator divided by all of the denominators."

     bit-or
     "Bitwise or"

     vector
     "Creates a new vector containing the args."

     >=
     "Returns non-nil if nums are in monotonically non-increasing order,
    otherwise false."

     drop-last
     "Return a lazy sequence of all but the last n (default 1) items in coll"

     object?
     "Returns true if x's constructor is Object"

     not-empty
     "If coll is empty, returns nil, else coll"

     distinct
     "Returns a lazy sequence of the elements of coll with duplicates removed.
    Returns a stateful transducer when no collection is provided."

     partition
     "Returns a lazy sequence of lists of n items each, at offsets step
    apart. If step is not supplied, defaults to n, i.e. the partitions
    do not overlap. If a pad collection is supplied, use its elements as
    necessary to complete last partition up to n items. In case there are
    not enough padding elements, return a partition with less than n items."

     IAssociative
     "Protocol for adding associativity to collections."

     bit-flip
     "Flip bit at index n"

     long-array
     "Creates an array of longs. Does not coerce array, provided for compatibility
    with Clojure."

     descendants
     "Returns the immediate and indirect children of tag, through a
    relationship established via derive. h must be a hierarchy obtained
    from make-hierarchy, if not supplied defaults to the global
    hierarchy. Note: does not work on JavaScript type inheritance
    relationships."

     merge
     "Returns a map that consists of the rest of the maps conj-ed onto
    the first.  If a key occurs in more than one map, the mapping from
    the latter (left-to-right) will be the mapping in the result."

     ISeqable
     "Protocol for adding the ability to a type to be transformed into a sequence."

     js-mod
     "Modulus of num and div with original javascript behavior. i.e. bug for negative numbers"

     integer?
     "Returns true if n is a JavaScript number with no decimal part."

     NS_CACHE
     "Bootstrap only."

     mapv
     "Returns a vector consisting of the result of applying f to the
    set of first items of each coll, followed by applying f to the set
    of second items in each coll, until any one of the colls is
    exhausted.  Any remaining items in other colls are ignored. Function
    f should accept number-of-colls arguments."

     infinite?
     "Returns true for Infinity and -Infinity values."

     partition-all
     "Returns a lazy sequence of lists like partition, but may include
    partitions with fewer than n items at the end.  Returns a stateful
    transducer when no collection is provided."

     partition-by
     "Applies f to each value in coll, splitting it each time f returns a
     new value.  Returns a lazy seq of partitions.  Returns a stateful
     transducer when no collection is provided."

     ISequential
     "Marker interface indicating a persistent collection of sequential items"

     equiv-map
     "Assumes y is a map. Returns true if x equals y, otherwise returns
    false."

     object-array
     "Creates an array of objects. Does not coerce array, provided for compatibility
    with Clojure."

     derive
     "Establishes a parent/child relationship between parent and
    tag. Parent must be a namespace-qualified symbol or keyword and
    child can be either a namespace-qualified symbol or keyword or a
    class. h must be a hierarchy obtained from make-hierarchy, if not
    supplied defaults to, and modifies, the global hierarchy."

     IChunkedSeq
     "Protocol for accessing a collection as sequential chunks."

     special-symbol?
     "Returns true if x names a special form"

     ancestors
     "Returns the immediate and indirect parents of tag, either via a JavaScript type
    inheritance relationship or a relationship established via derive. h
    must be a hierarchy obtained from make-hierarchy, if not supplied
    defaults to the global hierarchy"

     subseq
     "sc must be a sorted collection, test(s) one of <, <=, > or
    >=. Returns a seq of those entries with keys ek for
    which (test (.. sc comparator (compare ek key)) 0) is true"

     gensym
     "Returns a new symbol with a unique name. If a prefix string is
    supplied, the name is prefix# where # is some unique number. If
    prefix is not supplied, the prefix is 'G__'."

     -next
     "Returns a new collection of coll without the first item. In contrast to
       rest, it should return nil if there are no more items, e.g.
       (next []) => nil
       (next nil) => nil"

     delay?
     "returns true if x is a Delay created with delay"

     flatten
     "Takes any nested combination of sequential things (lists, vectors,
    etc.) and returns their contents as a single, flat sequence.
    (flatten nil) returns nil."

     -dissoc
     "Returns a new collection of coll without the mapping for key k."

     -contains-key?
     "Returns true if k is a key in coll."

     remove-watch
     "Removes a watch (set by add-watch) from a reference"

     ex-info
     "Create an instance of ExceptionInfo, an Error type that carries a
    map of additional data."

     ifn?
     "Returns true if f returns true for fn? or satisfies IFn."

     IAtom
     "Marker protocol indicating an atom."

     nat-int?
     "Return true if x satisfies int? and is a natural integer value."

     IWatchable
     "Protocol for types that can be watched. Currently only implemented by Atom."

     subvec
     "Returns a persistent vector of the items in vector from
    start (inclusive) to end (exclusive).  If end is not supplied,
    defaults to (count vector). This operation is O(1) and very fast, as
    the resulting vector shares structure with the original and no
    trimming is done."

     -pop!
     "Returns tcoll with the last item removed from it."

     partial
     "Takes a function f and fewer than the normal arguments to f, and
    returns a fn that takes a variable number of additional args. When
    called, the returned function calls f with args + additional args."

     chunked-seq?
     "Return true if x is satisfies IChunkedSeq."

     replicate
     "DEPRECATED: Use 'repeat' instead.
    Returns a lazy seq of n xs."

     min-key
     "Returns the x for which (k x), a number, is least."

     reduced
     "Wraps x in a way such that a reduce will terminate with the value x"

     re-matches
     "Returns the result of (re-find re s) if re fully matches s."

     array-map
     "keyval => key val
    Returns a new array map with supplied mappings."

     ITransientSet
     "Protocol for adding set functionality to a transient collection."

     every-pred
     "Takes a set of predicates and returns a function f that returns true if all of its
    composing predicates return a logical true value against all of its arguments, else it returns
    false. Note that f is short-circuiting in that it will stop execution on the first
    argument that triggers a logical false result against the original predicates."

     keys
     "Returns a sequence of the map's keys."

     distinct?
     "Returns true if no two of the arguments are ="

     pos-int?
     "Return true if x satisfies int? and is positive."

     methods
     "Given a multimethod, returns a map of dispatch values -> dispatch fns"

     odd?
     "Returns true if n is odd, throws an exception if n is not an integer"

     ci-reduce
     "Accepts any collection which satisfies the ICount and IIndexed protocols and
  reduces them without incurring seq initialization"

     *3
     "bound in a repl thread to the third most recent value printed"

     frequencies
     "Returns a map from distinct items in coll to the number of times
    they appear."

     reduceable?
     "Returns true if coll satisfies IReduce"

     rsubseq
     "sc must be a sorted collection, test(s) one of <, <=, > or
    >=. Returns a reverse seq of those entries with keys ek for
    which (test (.. sc comparator (compare ek key)) 0) is true"

     inc
     "Returns a number one greater than num."

     get-method
     "Given a multimethod and a dispatch value, returns the dispatch fn
    that would apply to that value, or nil if none apply and no default"

     bit-clear
     "Clear bit at index n"

     filter
     "Returns a lazy sequence of the items in coll for which
    (pred item) returns true. pred must be free of side-effects.
    Returns a transducer when no collection is provided."

     -assoc-n!
     "Returns tcoll with value val added at position n."

     IWithMeta
     "Protocol for adding metadata to an object."

     list
     "Creates a new list containing the items."

     +
     "Returns the sum of nums. (+) returns 0."

     split-with
     "Returns a vector of [(take-while pred coll) (drop-while pred coll)]"

     aset
     "Sets the value at the index."

     keyword
     "Returns a Keyword with the given namespace and name.  Do not use :
    in the keyword strings, it will be added automatically."

     *ns*
     "Var bound to the current namespace. Only used for bootstrapping."

     ICollection
     "Protocol for adding to a collection."

     str
     "With no args, returns the empty string. With one arg x, returns
    x.toString().  (str nil) returns the empty string. With more than
    one arg, returns the concatenation of the str values of the args."

     next
     "Returns a seq of the items after the first. Calls seq on its
    argument.  If there are no more items, returns nil"

     ASeq
     "Marker protocol indicating an array sequence."

     IFn
     "Protocol for adding the ability to invoke an object as a function.
    For example, a vector can also be used to look up a value:
    ([1 2 3 4] 1) => 2"

     regexp?
     "Returns true if x is a JavaScript RegExp instance."

     hash-map
     "keyval => key val
    Returns a new hash map with supplied mappings."

     underive
     "Removes a parent/child relationship between parent and
    tag. h must be a hierarchy obtained from make-hierarchy, if not
    supplied defaults to, and modifies, the global hierarchy."

     -reset!
     "Sets the value of o to new-value."

     -rest
     "Returns a new collection of coll without the first item. It should
       always return a seq, e.g.
       (rest []) => ()
       (rest nil) => ()"

     pr-writer
     "Prefer this to pr-seq, because it makes the printing function
     configurable, allowing efficient implementations such as appending
     to a StringBuffer."

     false?
     "Returns true if x is the value false, false otherwise."

     *print-readably*
     "When set to logical false, strings and characters will be printed with
    non-alphanumeric characters converted to the appropriate escape sequences.

    Defaults to true"

     some-fn
     "Takes a set of predicates and returns a function f that returns the first logical true value
    returned by one of its composing predicates against any of its arguments, else it returns
    logical false. Note that f is short-circuiting in that it will stop execution on the first
    argument that triggers a logical true result against the original predicates."

     *flush-on-newline*
     "When set to true, output will be flushed whenever a newline is printed.

    Defaults to true."

     to-array
     "Naive impl of to-array as a start."

     list?
     "Returns true if x implements IList"

     array?
     "Returns true if x is a JavaScript array."

     simple-ident?
     "Return true if x is a symbol or keyword without a namespace"

     clone
     "Clone the supplied value which must implement ICloneable."

     bit-not
     "Bitwise complement"

     max
     "Returns the greatest of the nums."

     IComparable
     "Protocol for values that can be compared."

     ==
     "Returns non-nil if nums all have the equivalent
    value, otherwise false. Behavior on non nums is
    undefined."

     parents
     "Returns the immediate parents of tag, either via a JavaScript type
    inheritance relationship or a relationship established via derive. h
    must be a hierarchy obtained from make-hierarchy, if not supplied
    defaults to the global hierarchy"

     count
     "Returns the number of items in the collection. (count nil) returns
    0.  Also works on strings, arrays, and Maps"

     -disjoin!
     "Returns tcoll without v."

     sorted-map-by
     "keyval => key val
    Returns a new sorted map with supplied mappings, using the supplied comparator."

     apply
     "Applies fn f to the argument list formed by prepending intervening arguments to args."

     clj->js
     "Recursively transforms ClojureScript values to JavaScript.
    sets/vectors/lists become Arrays, Keywords and Symbol become Strings,
    Maps become Objects. Arbitrary keys are encoded to by key->js."

     IChunkedNext
     "Protocol for accessing the chunks of a collection."

     interpose
     "Returns a lazy seq of the elements of coll separated by sep.
    Returns a stateful transducer when no collection is provided."

     assoc
     "assoc[iate]. When applied to a map, returns a new map of the
     same (hashed/sorted) type, that contains the mapping of key(s) to
     val(s). When applied to a vector, returns a new vector that
     contains val at index."

     transient
     "Returns a new, transient version of the collection, in constant time."

     -disjoin
     "Returns a new collection of coll that does not contain v."

     comparator
     "Returns an JavaScript compatible comparator based upon pred."

     sorted-map
     "keyval => key val
    Returns a new sorted map with supplied mappings."

     drop-while
     "Returns a lazy sequence of the items in coll starting from the
    first item for which (pred item) returns logical false.  Returns a
    stateful transducer when no collection is provided."

     IWriter
     "Protocol for writing. Currently only implemented by StringBufferWriter."

     realized?
     "Returns true if a value has been produced for a delay or lazy sequence."

     *print-fn*
     "Each runtime environment provides a different way to print output.
    Whatever function *print-fn* is bound to will be passed any
    Strings which should be printed."

     compare
     "Comparator. Returns a negative number, zero, or a positive number
    when x is logically 'less than', 'equal to', or 'greater than'
    y. Uses IComparable if available and google.array.defaultCompare for objects
   of the same type and special-cases nil to be less than any other object."

     complement
     "Takes a fn f and returns a fn that takes the same arguments as f,
    has the same effects, if any, and returns the opposite truth value."

     -assoc!
     "Returns a new transient collection of tcoll with a mapping from key to
       val added to it."

     *print-dup*
     "When set to logical true, objects will be printed in a way that preserves
    their type when read in later.

    Defaults to false."

     -key->js
     "Transforms map keys to valid JavaScript keys. Arbitrary keys are
    encoded to their string representation via (pr-str x)"

     IDeref
     "Protocol for adding dereference functionality to a reference."

     sequence
     "Coerces coll to a (possibly empty) sequence, if it is not already
    one. Will not force a lazy seq. (sequence nil) yields (), When a
    transducer is supplied, returns a lazy sequence of applications of
    the transform to the items in coll(s), i.e. to the set of first
    items of each coll, followed by the set of second
    items in each coll, until any one of the colls is exhausted.  Any
    remaining items in other colls are ignored. The transform should accept
    number-of-colls arguments"

     constantly
     "Returns a function that takes any number of arguments and returns x."

     ISorted
     "Protocol for a collection which can represent their items
    in a sorted manner.
  "

     make-array
     "Construct a JavaScript array of the specified dimensions. Accepts ignored
    type argument for compatibility with Clojure. Note that there is no efficient
    way to allocate multi-dimensional arrays in JavaScript; as such, this function
    will run in polynomial time when called with 3 or more arguments."

     enable-console-print!
     "Set *print-fn* to console.log"

     -flush
     "Flush writer."

     completing
     "Takes a reducing function f of 2 args and returns a fn suitable for
    transduce by adding an arity-1 signature that calls cf (default -
    identity) on the result argument."

     equiv-sequential
     "Assumes x is sequential. Returns true if x equals y, otherwise
    returns false."

     hash-unordered-coll
     "Returns the hash code, consistent with =, for an external unordered
     collection implementing Iterable. For maps, the iterator should
     return map entries whose hash is computed as
       (hash-ordered-coll [k v]).
     See http://clojure.org/data_structures#hash for full algorithms."

     repeat
     "Returns a lazy (infinite!, or length n if supplied) sequence of xs."

     nthnext
     "Returns the nth next of coll, (seq coll) when n is 0."

     get-validator
     "Gets the validator-fn for a var/ref/agent/atom."

     number?
     "Returns true if x is a JavaScript number."

     -conj!
     "Adds value val to tcoll and returns tcoll."

     print-str
     "print to a string, returning it"

     not-any?
     "Returns false if (pred x) is logical true for any x in coll,
    else true."

     into-array
     "Returns an array with components set to the values in aseq. Optional type
    argument accepted for compatibility with Clojure."

     -hash
     "Returns the hash code of o."

     qualified-symbol?
     "Return true if x is a symbol with a namespace"

     -dissoc!
     "Returns a new transient collection of tcoll without the mapping for key."

     seqable?
     "Return true if s satisfies ISeqable"

     symbol?
     "Return true if x is a Symbol"

     system-time
     "Returns highest resolution time offered by host in milliseconds."

     coll?
     "Returns true if x satisfies ICollection"

     get-in
     "Returns the value in a nested associative structure,
    where ks is a sequence of keys. Returns nil if the key is not present,
    or the not-found value if supplied."

     fnext
     "Same as (first (next x))"

     IList
     "Marker interface indicating a persistent list"

     -val
     "Returns the value of the map entry."

     -seq
     "Returns a seq of o, or nil if o is empty."}
    cljs.core$macros

    {macroexpand
     "Repeatedly calls macroexpand-1 on form until it no longer
    represents a macro form, then returns it.  Note neither
    macroexpand-1 nor macroexpand expand macros in subforms."

     when-first
     "bindings => x xs

       Roughly the same as (when (seq xs) (let [x (first xs)] body)) but xs is evaluated only once"

     cond->>
     "Takes an expression and a set of test/form pairs. Threads expr (via ->>)
       through each form for which the corresponding test expression
       is true.  Note that, unlike cond branching, cond->> threading does not short circuit
       after the first true test expression."

     while
     "Repeatedly executes body while test expression is true. Presumes
       some side-effect will cause test to become false/nil. Returns nil"

     satisfies?
     "Returns true if x satisfies the protocol"

     ns-unmap
     "Removes the mappings for the symbol from the namespace."

     import
     "import-list => (closure-namespace constructor-name-symbols*)

    For each name in constructor-name-symbols, adds a mapping from name to the
    constructor named by closure-namespace to the current namespace. Use :import in the ns
    macro in preference to calling this directly."

     specify
     "Identical to specify! but does not mutate its first argument. The first
    argument must be an ICloneable instance."

     vswap!
     "Non-atomically swaps the value of the volatile as if:
     (apply f current-value-of-vol args). Returns the value that
     was swapped in."

     this-as
     "Defines a scope where JavaScript's implicit \"this\" is bound to the name provided."

     ..
     "form => fieldName-symbol or (instanceMethodName-symbol args*)

       Expands into a member access (.) of the first member on the first
       argument, followed by the next member on the result, etc. For
       instance:

       (.. System (getProperties) (get \"os.name\"))

       expands to:

       (. (. System (getProperties)) (get \"os.name\"))

       but is easier to write, read, and understand."

     delay
     "Takes a body of expressions and yields a Delay object that will
    invoke the body only the first time it is forced (with force or deref/@), and
    will cache the result and return it on all subsequent force
    calls."

     simple-benchmark
     "Runs expr iterations times in the context of a let expression with
    the given bindings, then prints out the bindings and the expr
    followed by number of iterations and total time. The optional
    argument print-fn, defaulting to println, sets function used to
    print the result. expr's string representation will be produced
    using pr-str in any case."

     implements?
     "EXPERIMENTAL"

     assert-valid-fdecl
     "A good fdecl looks like (([a] ...) ([a b] ...)) near the end of defn."

     goog-define
     "Defines a var using `goog.define`. Passed default value must be
    string, number or boolean.

    Default value can be overridden at compile time using the
    compiler option `:closure-defines`.

    Example:
      (ns your-app.core)
      (goog-define DEBUG! false)
      ;; can be overridden with
      :closure-defines {\"your_app.core.DEBUG_BANG_\" true}
      or
      :closure-defines {'your-app.core/DEBUG! true}"

     specify!
     "Identical to reify but mutates its first argument."

     if-not
     "Evaluates test. If logical false, evaluates and returns then expr,
       otherwise else expr, if supplied, else nil."

     doseq
     "Repeatedly executes body (presumably for side-effects) with
    bindings and filtering as provided by \"for\".  Does not retain
    the head of the sequence. Returns nil."

     undefined?
     "Return true if argument is identical to the JavaScript undefined value."

     deftype
     "(deftype name [fields*]  options* specs*)

    Currently there are no options.

    Each spec consists of a protocol or interface name followed by zero
    or more method bodies:

    protocol-or-Object
    (methodName [args*] body)*

    The type will have the (by default, immutable) fields named by
    fields, which can have type hints. Protocols and methods
    are optional. The only methods that can be supplied are those
    declared in the protocols/interfaces.  Note that method bodies are
    not closures, the local environment includes only the named fields,
    and those fields can be accessed directly. Fields can be qualified
    with the metadata :mutable true at which point (set! afield aval) will be
    supported in method bodies. Note well that mutable fields are extremely
    difficult to use correctly, and are present only to facilitate the building
    of higherlevel constructs, such as ClojureScript's reference types, in
    ClojureScript itself. They are for experts only - if the semantics and
    implications of :mutable are not immediately apparent to you, you should not
    be using them.

    Method definitions take the form:

    (methodname [args*] body)

    The argument and return types can be hinted on the arg and
    methodname symbols. If not supplied, they will be inferred, so type
    hints should be reserved for disambiguation.

    Methods should be supplied for all methods of the desired
    protocol(s). You can also define overrides for methods of Object. Note that
    a parameter must be supplied to correspond to the target object
    ('this' in JavaScript parlance). Note also that recur calls to the method
    head should *not* pass the target object, it will be supplied
    automatically and can not be substituted.

    In the method bodies, the (unqualified) name can be used to name the
    class (for calls to new, instance? etc).

    One constructor will be defined, taking the designated fields.  Note
    that the field names __meta and __extmap are currently reserved and
    should not be used when defining your own types.

    Given (deftype TypeName ...), a factory function called ->TypeName
    will be defined, taking positional parameters for the fields"

     when-let
     "bindings => binding-form test

       When test is true, evaluates body with binding-form bound to the value of test"

     if-some
     "bindings => binding-form test

        If test is not nil, evaluates then with binding-form bound to the
        value of test, if not, yields else"

     lazy-seq
     "Takes a body of expressions that returns an ISeq or nil, and yields
    a ISeqable object that will invoke the body only the first time seq
    is called, and will cache the result and return it on all subsequent
    seq calls."

     defcurried
     "Builds another arity of the fn that returns a fn awaiting the last
    param"

     js-debugger
     "Emit JavaScript \"debugger;\" statement"

     let
     "binding => binding-form init-expr

    Evaluates the exprs in a lexical context in which the symbols in
    the binding-forms are bound to their respective init-exprs or parts
    therein."

     ->
     "Threads the expr through the forms. Inserts x as the
       second item in the first form, making a list of it if it is not a
       list already. If there are more forms, inserts the first form as the
       second item in second form, etc."

     doto
     "Evaluates x then calls all of the methods and functions with the
       value of x supplied at the front of the given arguments.  The forms
       are evaluated in order.  Returns x.

       (doto (new java.util.HashMap) (.put \"a\" 1) (.put \"b\" 2))"

     areduce
     "Reduces an expression across an array a, using an index named idx,
    and return value named ret, initialized to init, setting ret to the
    evaluation of expr at each step, returning ret."

     fn
     "params => positional-params* , or positional-params* & next-param
       positional-param => binding-form
       next-param => binding-form
       name => symbol

       Defines a function"

     fast-path-protocols
     "protocol fqn -> [partition number, bit]"

     emit-defrecord
     "Do not use this directly - use defrecord"

     as->
     "Binds name to expr, evaluates the first form in the lexical context
       of that binding, then binds name to that result, repeating for each
       successive form, returning the result of the last form."

     when-not
     "Evaluates test. If logical false, evaluates body in an implicit do."

     when
     "Evaluates test. If logical true, evaluates body in an implicit do."

     use-macros
     "Similar to use but only for macros."

     some->>
     "When expr is not nil, threads it into the first form (via ->>),
       and when that result is not nil, through the next etc"

     defn
     "Same as (def name (core/fn [params* ] exprs*)) or (def
      name (core/fn ([params* ] exprs*)+)) with any doc-string or attrs added
      to the var metadata. prepost-map defines a map with optional keys
      :pre and :post that contain collections of pre or post conditions."

     amap
     "Maps an expression across an array a, using an index named idx, and
    return value named ret, initialized to a clone of a, then setting
    each element of ret to the evaluation of expr, returning the new
    array ret."

     use
     "Like require, but referring vars specified by the mandatory
    :only option.

    Example:

    The following would load the library clojure.set while referring
    the intersection var.

    (use '[clojure.set :only [intersection]])"

     declare
     "defs the supplied var names with no bindings, useful for making forward declarations."

     fast-path-protocol-partitions-count
     "total number of partitions"

     or
     "Evaluates exprs one at a time, from left to right. If a form
    returns a logical true value, or returns that value and doesn't
    evaluate any of the other expressions, otherwise it returns the
    value of the last expression. (or) returns nil."

     extend-type
     "Extend a type to a series of protocols. Useful when you are
    supplying the definitions explicitly inline. Propagates the
    type as a type hint on the first argument of all fns.

    type-sym may be

     * default, meaning the definitions will apply for any value,
       unless an extend-type exists for one of the more specific
       cases below.
     * nil, meaning the definitions will apply for the nil value.
     * any of object, boolean, number, string, array, or function,
       indicating the definitions will apply for values of the
       associated base JavaScript types. Note that, for example,
       string should be used instead of js/String.
     * a JavaScript type not covered by the previous list, such
       as js/RegExp.
     * a type defined by deftype or defrecord.

    (extend-type MyType
      ICounted
      (-count [c] ...)
      Foo
      (bar [x y] ...)
      (baz ([x] ...) ([x y & zs] ...))"

     macroexpand-1
     "If form represents a macro form, returns its expansion,
    else returns form."

     defmethod
     "Creates and installs a new method of multimethod associated with dispatch-value.
  "

     time
     "Evaluates expr and prints the time it took. Returns the value of expr."

     require
     "Loads libs, skipping any that are already loaded. Each argument is
    either a libspec that identifies a lib or a flag that modifies how all the identified
    libs are loaded. Use :require in the ns macro in preference to calling this
    directly.

    Libs

    A 'lib' is a named set of resources in classpath whose contents define a
    library of ClojureScript code. Lib names are symbols and each lib is associated
    with a ClojureScript namespace. A lib's name also locates its root directory
    within classpath using Java's package name to classpath-relative path mapping.
    All resources in a lib should be contained in the directory structure under its
    root directory. All definitions a lib makes should be in its associated namespace.

    'require loads a lib by loading its root resource. The root resource path
    is derived from the lib name in the following manner:
    Consider a lib named by the symbol 'x.y.z; it has the root directory
    <classpath>/x/y/, and its root resource is <classpath>/x/y/z.clj. The root
    resource should contain code to create the lib's namespace (usually by using
    the ns macro) and load any additional lib resources.

    Libspecs

    A libspec is a lib name or a vector containing a lib name followed by
    options expressed as sequential keywords and arguments.

    Recognized options:
    :as takes a symbol as its argument and makes that symbol an alias to the
      lib's namespace in the current namespace.
    :refer takes a list of symbols to refer from the namespace..
    :refer-macros takes a list of macro symbols to refer from the namespace.
    :include-macros true causes macros from the namespace to be required.

    Flags

    A flag is a keyword.
    Recognized flags: :reload, :reload-all, :verbose
    :reload forces loading of all the identified libs even if they are
      already loaded
    :reload-all implies :reload and also forces loading of all libs that the
      identified libs directly or indirectly load via require or use
    :verbose triggers printing information about each load, alias, and refer

    Example:

    The following would load the library clojure.string :as string.

    (require '[clojure/string :as string])"

     memfn
     "Expands into code that creates a fn that expects to be passed an
       object and any args and calls the named instance method on the
       object passing the args. Use when you want to treat a Java method as
       a first-class fn. name may be type-hinted with the method receiver's
       type in order to avoid reflective calls."

     extend-protocol
     "Useful when you want to provide several implementations of the same
       protocol all at once. Takes a single protocol and the implementation
       of that protocol for one or more types. Expands into calls to
       extend-type:

       (extend-protocol Protocol
         AType
           (foo [x] ...)
           (bar [x y] ...)
         BType
           (foo [x] ...)
           (bar [x y] ...)
         AClass
           (foo [x] ...)
           (bar [x y] ...)
         nil
           (foo [x] ...)
           (bar [x y] ...))

       expands into:

       (do
        (clojure.core/extend-type AType Protocol
          (foo [x] ...)
          (bar [x y] ...))
        (clojure.core/extend-type BType Protocol
          (foo [x] ...)
          (bar [x y] ...))
        (clojure.core/extend-type AClass Protocol
          (foo [x] ...)
          (bar [x y] ...))
        (clojure.core/extend-type nil Protocol
          (foo [x] ...)
          (bar [x y] ...)))"

     cond->
     "Takes an expression and a set of test/form pairs. Threads expr (via ->)
       through each form for which the corresponding test
       expression is true. Note that, unlike cond branching, cond-> threading does
       not short circuit after the first true test expression."

     dotimes
     "bindings => name n

    Repeatedly executes body (presumably for side-effects) with name
    bound to integers from 0 through n-1."

     reify
     "reify is a macro with the following structure:

   (reify options* specs*)

    Currently there are no options.

    Each spec consists of the protocol name followed by zero
    or more method bodies:

    protocol
    (methodName [args+] body)*

    Methods should be supplied for all methods of the desired
    protocol(s). You can also define overrides for Object methods. Note that
    the first parameter must be supplied to correspond to the target object
    ('this' in JavaScript parlance). Note also that recur calls
    to the method head should *not* pass the target object, it will be supplied
    automatically and can not be substituted.

    recur works to method heads The method bodies of reify are lexical
    closures, and can refer to the surrounding local scope:

    (str (let [f \"foo\"]
         (reify Object
           (toString [this] f))))
    == \"foo\"

    (seq (let [f \"foo\"]
         (reify ISeqable
           (-seq [this] (-seq f)))))
    == (\\f \\o \\o))

    reify always implements IMeta and IWithMeta and transfers meta
    data of the form to the created object.

    (meta ^{:k :v} (reify Object (toString [this] \"foo\")))
    == {:k :v}"

     defonce
     "defs name to have the root value of init iff the named var has no root value,
    else init is unevaluated"

     rfn
     "Builds 3-arity reducing fn given names of wrapped fn and key, and k/v impl."

     defn-
     "same as defn, yielding non-public def"

     defprotocol
     "A protocol is a named set of named methods and their signatures:

    (defprotocol AProtocolName
      ;optional doc string
      \"A doc string for AProtocol abstraction\"

    ;method signatures
      (bar [this a b] \"bar docs\")
      (baz [this a] [this a b] [this a b c] \"baz docs\"))

    No implementations are provided. Docs can be specified for the
    protocol overall and for each method. The above yields a set of
    polymorphic functions and a protocol object. All are
    namespace-qualified by the ns enclosing the definition The resulting
    functions dispatch on the type of their first argument, which is
    required and corresponds to the implicit target object ('this' in
    JavaScript parlance). defprotocol is dynamic, has no special compile-time
    effect, and defines no new types.

    (defprotocol P
      (foo [this])
      (bar-me [this] [this y]))

    (deftype Foo [a b c]
      P
      (foo [this] a)
      (bar-me [this] b)
      (bar-me [this y] (+ c y)))

    (bar-me (Foo. 1 2 3) 42)
    => 45

    (foo
      (let [x 42]
        (reify P
          (foo [this] 17)
          (bar-me [this] x)
          (bar-me [this y] x))))
    => 17"

     assert
     "Evaluates expr and throws an exception if it does not evaluate to
    logical true."

     letfn
     "fnspec ==> (fname [params*] exprs) or (fname ([params*] exprs)+)

       Takes a vector of function specs and a body, and generates a set of
       bindings of functions to their names. All of the names are available
       in all of the definitions of the functions, as well as the body."

     loop
     "Evaluates the exprs in a lexical context in which the symbols in
    the binding-forms are bound to their respective init-exprs or parts
    therein. Acts as a recur target."

     with-out-str
     "Evaluates exprs in a context in which *print-fn* is bound to .append
    on a fresh StringBuffer.  Returns the string created by any nested
    printing calls."

     condp
     "Takes a binary predicate, an expression, and a set of clauses.
    Each clause can take the form of either:

    test-expr result-expr

    test-expr :>> result-fn

    Note :>> is an ordinary keyword.

    For each clause, (pred test-expr expr) is evaluated. If it returns
    logical true, the clause is a match. If a binary clause matches, the
    result-expr is returned, if a ternary clause matches, its result-fn,
    which must be a unary function, is called with the result of the
    predicate as its argument, the result of that call being the return
    value of condp. A single default expression can follow the clauses,
    and its value will be returned if no clause matches. If no default
    expression is provided and no clause matches, an
    IllegalArgumentException is thrown."

     cond
     "Takes a set of test/expr pairs. It evaluates each test one at a
       time.  If a test returns logical true, cond evaluates and returns
       the value of the corresponding expr and doesn't evaluate any of the
       other tests or exprs. (cond) returns nil."

     check-valid-options
     "Throws an exception if the given option map contains keys not listed
    as valid, else returns nil."

     some->
     "When expr is not nil, threads it into the first form (via ->),
       and when that result is not nil, through the next etc"

     ns-interns
     "Returns a map of the intern mappings for the namespace."

     for
     "List comprehension. Takes a vector of one or more
     binding-form/collection-expr pairs, each followed by zero or more
     modifiers, and yields a lazy sequence of evaluations of expr.
     Collections are iterated in a nested fashion, rightmost fastest,
     and nested coll-exprs can refer to bindings created in prior
     binding-forms.  Supported modifiers are: :let [binding-form expr ...],
     :while test, :when test.

    (take 100 (for [x (range 100000000) y (range 1000000) :while (< y x)]  [x y]))"

     binding
     "binding => var-symbol init-expr

    Creates new bindings for the (already-existing) vars, with the
    supplied initial values, executes the exprs in an implicit do, then
    re-establishes the bindings that existed before.  The new bindings
    are made in parallel (unlike let); all init-exprs are evaluated
    before the vars are bound to their new values."

     defmacro
     "Like defn, but the resulting function name is declared as a
    macro and will be used as a macro by the compiler when it is
    called."

     with-redefs
     "binding => var-symbol temp-value-expr

    Temporarily redefines vars while executing the body.  The
    temp-value-exprs will be evaluated and each resulting value will
    replace in parallel the root value of its var.  After the body is
    executed, the root values of all the vars will be set back to their
    old values. Useful for mocking out functions during testing."

     defmulti
     "Creates a new multimethod with the associated dispatch function.
    The docstring and attribute-map are optional.

    Options are key-value pairs and may be one of:
      :default    the default dispatch value, defaults to :default
      :hierarchy  the isa? hierarchy to use for dispatching
                  defaults to the global hierarchy"

     if-let
     "bindings => binding-form test

       If test is true, evaluates then with binding-form bound to the value of
       test, if not, yields else"

     case
     "Takes an expression, and a set of clauses.

    Each clause can take the form of either:

    test-constant result-expr

    (test-constant1 ... test-constantN)  result-expr

    The test-constants are not evaluated. They must be compile-time
    literals, and need not be quoted.  If the expression is equal to a
    test-constant, the corresponding result-expr is returned. A single
    default expression can follow the clauses, and its value will be
    returned if no clause matches. If no default expression is provided
    and no clause matches, an Error is thrown.

    Unlike cond and condp, case does a constant-time dispatch, the
    clauses are not considered sequentially.  All manner of constant
    expressions are acceptable in case, including numbers, strings,
    symbols, keywords, and (ClojureScript) composites thereof. Note that since
    lists are used to group multiple constants that map to the same
    expression, a vector can be used to match a list if needed. The
    test-constants need not be all of the same type."

     exists?
     "Return true if argument exists, analogous to usage of typeof operator
     in JavaScript."

     lazy-cat
     "Expands to code which yields a lazy sequence of the concatenation
    of the supplied colls.  Each coll expr is not evaluated until it is
    needed.

    (lazy-cat xs ys zs) === (concat (lazy-seq xs) (lazy-seq ys) (lazy-seq zs))"

     comment
     "Ignores body, yields nil"

     unsafe-cast
     "EXPERIMENTAL: Subject to change. Unsafely cast a value to a different type."

     defrecord
     "(defrecord name [fields*]  options* specs*)

    Currently there are no options.

    Each spec consists of a protocol or interface name followed by zero
    or more method bodies:

    protocol-or-Object
    (methodName [args*] body)*

    The record will have the (immutable) fields named by
    fields, which can have type hints. Protocols and methods
    are optional. The only methods that can be supplied are those
    declared in the protocols.  Note that method bodies are
    not closures, the local environment includes only the named fields,
    and those fields can be accessed directly.

    Method definitions take the form:

    (methodname [args*] body)

    The argument and return types can be hinted on the arg and
    methodname symbols. If not supplied, they will be inferred, so type
    hints should be reserved for disambiguation.

    Methods should be supplied for all methods of the desired
    protocol(s). You can also define overrides for
    methods of Object. Note that a parameter must be supplied to
    correspond to the target object ('this' in JavaScript parlance). Note also
    that recur calls to the method head should *not* pass the target object, it
    will be supplied automatically and can not be substituted.

    In the method bodies, the (unqualified) name can be used to name the
    class (for calls to new, instance? etc).

    The type will have implementations of several ClojureScript
    protocol generated automatically: IMeta/IWithMeta (metadata support) and
    IMap, etc.

    In addition, defrecord will define type-and-value-based =,
    and will define ClojureScript IHash and IEquiv.

    Two constructors will be defined, one taking the designated fields
    followed by a metadata map (nil for none) and an extension field
    map (nil for none), and one taking only the fields (using nil for
    meta and extension fields). Note that the field names __meta
    and __extmap are currently reserved and should not be used when
    defining your own records.

    Given (defrecord TypeName ...), two factory functions will be
    defined: ->TypeName, taking positional parameters for the fields,
    and map->TypeName, taking a map of keywords to field values."

     and
     "Evaluates exprs one at a time, from left to right. If a form
    returns logical false (nil or false), and returns that value and
    doesn't evaluate any of the other expressions, otherwise it returns
    the value of the last expr. (and) returns true."

     js-comment
     "Emit a top-level JavaScript multi-line comment. New lines will create a
    new comment line. Comment block will be preceded and followed by a newline"

     when-some
     "bindings => binding-form test

        When test is not nil, evaluates body with binding-form bound to the
        value of test"

     require-macros
     "Similar to require but only for macros."

     ->>
     "Threads the expr through the forms. Inserts x as the
       last item in the first form, making a list of it if it is not a
       list already. If there are more forms, inserts the first form as the
       last item in second form, etc."

     js-inline-comment
     "Emit an inline JavaScript comment."

     refer-clojure
     "Refers to all the public vars of `cljs.core`, subject to
    filters.
    Filters can include at most one each of:

    :exclude list-of-symbols
    :rename map-of-fromsymbol-tosymbol

    Filters can be used to select a subset, via exclusion, or to provide a mapping
    to a symbol different from the var's name, in order to prevent clashes."}})