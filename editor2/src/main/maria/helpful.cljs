(ns maria.helpful
  (:require [clojure.set :as set]))

;; copied from https://raw.githubusercontent.com/clojure/clojure/42a7fd42cfae973d2af16d4bed40c7594574b58b/src/clj/clojure/repl.clj

(def special-doc-map*
  '{. {:url "java_interop#dot"
       :forms [(.instanceMember instance args*)
               (.instanceMember Classname args*)
               (Classname/staticMethod args*)
               Classname/staticField]
       :doc "The instance member form works for both fields and methods.
  They all expand into calls to the dot operator at macroexpansion time."}
    def {:forms [(def symbol doc-string? init?)]
         :doc "Creates and interns a global var with the name
  of symbol in the current namespace (*ns*) or locates such a var if
  it already exists.  If init is supplied, it is evaluated, and the
  root binding of the var is set to the resulting value.  If init is
  not supplied, the root binding of the var is unaffected."}
    do {:forms [(do exprs*)]
        :doc "Evaluates the expressions in order and returns the value of
  the last. If no expressions are supplied, returns nil."}
    if {:forms [(if test then else?)]
        :doc "Evaluates test. If not the singular values nil or false,
  evaluates and yields then, otherwise, evaluates and yields else. If
  else is not supplied it defaults to nil."}
    monitor-enter {:forms [(monitor-enter x)]
                   :doc "Synchronization primitive that should be avoided
  in user code. Use the 'locking' macro."}
    monitor-exit {:forms [(monitor-exit x)]
                  :doc "Synchronization primitive that should be avoided
  in user code. Use the 'locking' macro."}
    new {:forms [(Classname. args*) (new Classname args*)]
         :url "java_interop#new"
         :doc "The args, if any, are evaluated from left to right, and
  passed to the constructor of the class named by Classname. The
  constructed object is returned."}
    quote {:forms [(quote form)]
           :doc "Yields the unevaluated form."}
    recur {:forms [(recur exprs*)]
           :doc "Evaluates the exprs in order, then, in parallel, rebinds
  the bindings of the recursion point to the values of the exprs.
  Execution then jumps back to the recursion point, a loop or fn method."}
    set! {:forms [(set! var-symbol expr)
                  (set! (. instance-expr instanceFieldName-symbol) expr)
                  (set! (. Classname-symbol staticFieldName-symbol) expr)]
          :url "vars#set"
          :doc "Used to set thread-local-bound vars, Java object instance
fields, and Java class static fields."}
    throw {:forms [(throw expr)]
           :doc "The expr is evaluated and thrown, therefore it should
  yield an instance of some derivee of Throwable."}
    try {:forms [(try expr* catch-clause* finally-clause?)]
         :doc "catch-clause => (catch classname name expr*)
  finally-clause => (finally expr*)
  Catches and handles Java exceptions."}
    var {:forms [(var symbol)]
         :doc "The symbol must resolve to a var, and the Var object
itself (not its value) is returned. The reader macro #'x expands to (var x)."}})

(defn- ->meta [m]
  (-> m
      (update :forms (partial mapv (comp vec rest)))
      (set/rename-keys {:forms :arglists})
      (assoc :name (ffirst (:forms m))
             :special-form true)))

(def core-doc-map*
  '{macroexpand {:doc "Repeatedly calls macroexpand-1 on form until it no longer
                    represents a macro form, then returns it.  Note neither
                    macroexpand-1 nor macroexpand expand macros in subforms.",
                 :arglists ([form])},
    when-first {:doc "bindings => x xs

                   Roughly the same as (when (seq xs) (let [x (first xs)] body)) but xs is evaluated only once",
                :arglists ([bindings & body])},
    find-ns {:doc "Returns the namespace named by the symbol or nil if it doesn't exist.", :arglists ([sym])},
    get-thread-bindings {},
    cond->> {:doc "Takes an expression and a set of test/form pairs. Threads expr (via ->>)
                through each form for which the corresponding test expression
                is true.  Note that, unlike cond branching, cond->> threading does not short circuit
                after the first true test expression.",
             :arglists ([expr & clauses])},
    while {:doc "Repeatedly executes body while test expression is true. Presumes
              some side-effect will cause test to become false/nil. Returns nil",
           :arglists ([test & body])},
    satisfies? {},
    *print-namespace-maps* {:doc "*print-namespace-maps* controls whether the printer will print
                               namespace map literal syntax. It defaults to false, but the REPL binds
                               to true."},
    ns-unmap {:doc "Removes the mappings for the symbol from the namespace.", :arglists ([ns sym])},
    newline {},
    bound-fn {:doc "Returns a function defined by the given fntail, which will install the
                 same bindings in effect as in the thread at the time bound-fn was called.
                 This may be used to define a helper function which runs on a different
                 thread, but needs the same bindings in place.",
              :arglists ([& fntail])},
    vswap! {:doc "Non-atomically swaps the value of the volatile as if:
               (apply f current-value-of-vol args). Returns the value that
               was swapped in.",
            :arglists ([vol f & args])},
    pr {},
    push-thread-bindings {},
    remove-ns {:doc "Removes the namespace named by the symbol. Use with caution.
                  Cannot be used to remove the clojure namespace.",
               :arglists ([sym])},
    thread-bound? {},
    find-var {:doc "Returns the global var named by the namespace-qualified symbol, or\nnil if no var with that name.",
              :arglists ([sym])},
    ns-publics {:doc "Returns a map of the public intern mappings for the namespace.", :arglists ([ns])},
    isa? {:doc "Returns true if (= child parent), or child is directly or indirectly derived from
             parent, either via a Java type inheritance relationship or a
             relationship established via derive. h must be a hierarchy obtained
             from make-hierarchy, if not supplied defaults to the global
             hierarchy",
          :arglists ([child parent] [h child parent])},
    .. {:doc "form => fieldName-symbol or (instanceMethodName-symbol args*)

           Expands into a member access (.) of the first member on the first
           argument, followed by the next member on the result, etc. For
           instance:

           (.. System (getProperties) (get \"os.name\"))

           expands to:

           (. (. System (getProperties)) (get \"os.name\"))

           but is easier to write, read, and understand.",
        :arglists ([x form] [x form & more])},
    delay {:doc "Takes a body of expressions and yields a Delay object that will
              invoke the body only the first time it is forced (with force or deref/@), and
              will cache the result and return it on all subsequent force
              calls. See also - realized?",
           :arglists ([& body])},
    swap-vals! {:doc "Atomically swaps the value of atom to be:
                   (apply f current-value-of-atom args). Note that f may be called
                   multiple times, and thus should be free of side effects.
                   Returns [old new], the value of the atom before and after the swap.",
                :arglists ([atom f] [atom f x] [atom f x y] [atom f x y & args])},
    with-bindings {:doc "Takes a map of Var/value pairs. Installs for the given Vars the associated
                      values as thread-local bindings. Then executes body. Pops the installed
                      bindings after body was evaluated. Returns the value of body.",
                   :arglists ([binding-map & body])},
    *read-eval* {:doc "Defaults to true (or value specified by system property, see below)
                    ***This setting implies that the full power of the reader is in play,
                    including syntax that can cause code to execute. It should never be
                    used with untrusted sources. See also: clojure.edn/read.***

                    When set to logical false in the thread-local binding,
                    the eval reader (#=) and record/type literal syntax are disabled in read/load.
                    Example (will fail): (binding [*read-eval* false] (read-string \"#=(* 2 21)\"))

                    The default binding can be controlled by the system property
                    'clojure.read.eval' System properties can be set on the command line
                    like this:

                    java -Dclojure.read.eval=false ...

                    The system property can also be set to 'unknown' via
                    -Dclojure.read.eval=unknown, in which case the default binding
                    is :unknown and all reads will fail in contexts where *read-eval*
                    has not been explicitly bound to either true or false. This setting
                    can be a useful diagnostic tool to ensure that all of your reads
                    occur in considered contexts. You can also accomplish this in a
                    particular scope by binding *read-eval* to :unknown
                    "},
    *2 {:doc "bound in a repl thread to the second most recent value printed"},
    eval {:doc "Evaluates the form data structure (not text!) and returns the result.", :arglists ([form])},
    refer {:doc "refers to all public vars of ns, subject to filters.
              filters can include at most one each of:

              :exclude list-of-symbols
              :only list-of-symbols
              :rename map-of-fromsymbol-tosymbol

              For each public interned var in the namespace named by the symbol,
              adds a mapping from the name of the var to the var to the current
              namespace.  Throws an exception if name is already mapped to
              something else in the current namespace. Filters can be used to
              select a subset, via inclusion or exclusion, or to provide a mapping
              to a symbol different from the var's name, in order to prevent
              clashes. Use :use in the ns macro in preference to calling this directly.",
           :arglists ([ns-sym & filters])},
    if-not {:doc "Evaluates test. If logical false, evaluates and returns then expr,
               otherwise else expr, if supplied, else nil.",
            :arglists ([test then] [test then else])},
    *print-level* {:doc "*print-level* controls how many levels deep the printer will
                      print nested objects. If it is bound to logical false, there is no
                      limit. Otherwise, it must be bound to an integer indicating the maximum
                      level to print. Each argument to print is at level 0; if an argument is a
                      collection, its items are at level 1; and so on. If an object is a
                      collection and is at a level greater than or equal to the value bound to
                      *print-level*, the printer prints '#' to represent it. The root binding
                      is nil indicating no limit."},
    alength {:doc "Returns the length of the Java array. Works on arrays of all\ntypes.", :arglists ([array])},
    doseq {:doc "Repeatedly executes body (presumably for side-effects) with
              bindings and filtering as provided by \"for\".  Does not retain
              the head of the sequence. Returns nil.",
           :arglists ([seq-exprs & body])},
    var-set {},
    deftype {:doc "(deftype name [fields*]  options* specs*)

                Options are expressed as sequential keywords and arguments (in any order).

                Supported options:
                :load-ns - if true, importing the type class will cause the
                           namespace in which the type was defined to be loaded.
                           Defaults to false.

                Each spec consists of a protocol or interface name followed by zero
                or more method bodies:

                protocol-or-interface-or-Object
                (methodName [args*] body)*

                Dynamically generates compiled bytecode for class with the given
                name, in a package with the same name as the current namespace, the
                given fields, and, optionally, methods for protocols and/or
                interfaces.

                The class will have the (by default, immutable) fields named by
                fields, which can have type hints. Protocols/interfaces and methods
                are optional. The only methods that can be supplied are those
                declared in the protocols/interfaces.  Note that method bodies are
                not closures, the local environment includes only the named fields,
                and those fields can be accessed directly. Fields can be qualified
                with the metadata :volatile-mutable true or :unsynchronized-mutable
                true, at which point (set! afield aval) will be supported in method
                bodies. Note well that mutable fields are extremely difficult to use
                correctly, and are present only to facilitate the building of higher
                level constructs, such as Clojure's reference types, in Clojure
                itself. They are for experts only - if the semantics and
                implications of :volatile-mutable or :unsynchronized-mutable are not
                immediately apparent to you, you should not be using them.

                Method definitions take the form:

                (methodname [args*] body)

                The argument and return types can be hinted on the arg and
                methodname symbols. If not supplied, they will be inferred, so type
                hints should be reserved for disambiguation.

                Methods should be supplied for all methods of the desired
                protocol(s) and interface(s). You can also define overrides for
                methods of Object. Note that a parameter must be supplied to
                correspond to the target object ('this' in Java parlance). Thus
                methods for interfaces will take one more argument than do the
                interface declarations. Note also that recur calls to the method
                head should *not* pass the target object, it will be supplied
                automatically and can not be substituted.

                In the method bodies, the (unqualified) name can be used to name the
                class (for calls to new, instance? etc).

                When AOT compiling, generates compiled bytecode for a class with the
                given name (a symbol), prepends the current ns as the package, and
                writes the .class file to the *compile-path* directory.

                One constructor will be defined, taking the designated fields.  Note
                that the field names __meta, __extmap, __hash and __hasheq are currently
                reserved and should not be used when defining your own types.

                Given (deftype TypeName ...), a factory function called ->TypeName
                will be defined, taking positional parameters for the fields",
             :arglists ([name [& fields] & opts+specs])},
    ns-unalias {:doc "Removes the alias for the symbol from the namespace.", :arglists ([ns sym])},
    when-let {:doc "bindings => binding-form test

                 When test is true, evaluates body with binding-form bound to the value of test",
              :arglists ([bindings & body])},
    flush {},
    if-some {:doc "bindings => binding-form test

                If test is not nil, evaluates then with binding-form bound to the
                value of test, if not, yields else",
             :arglists ([bindings then] [bindings then else & oldform])},
    reset-vals! {:doc "Sets the value of atom to newval. Returns [old new], the value of the
                    atom before and after the reset.",
                 :arglists ([atom newval])},
    alter-var-root {},
    bound? {},
    *print-length* {:doc "*print-length* controls how many items of each collection the
                       printer will print. If it is bound to logical false, there is no
                       limit. Otherwise, it must be bound to an integer indicating the maximum
                       number of items of each collection to print. If a collection contains
                       more items, the printer will print items up to the limit followed by
                       '...' to represent the remaining items. The root binding is nil
                       indicating no limit."},
    *file* {:doc "The path of the file being evaluated, as a String.

               When there is no file, e.g. in the REPL, the value is not defined."},
    compare-and-set! {:doc "Atomically sets the value of atom to newval if and only if the
                         current value of the atom is identical to oldval. Returns true if
                         set happened, else false",
                      :arglists ([atom oldval newval])},
    pop-thread-bindings {},
    printf {},
    -> {:doc "Threads the expr through the forms. Inserts x as the
           second item in the first form, making a list of it if it is not a
           list already. If there are more forms, inserts the first form as the
           second item in second form, etc.",
        :arglists ([x & forms])},
    *err* {:doc "A java.io.Writer object representing standard error for print operations.

              Defaults to System/err, wrapped in a PrintWriter"},
    doto {:doc "Evaluates x then calls all of the methods and functions with the
             value of x supplied at the front of the given arguments.  The forms
             are evaluated in order.  Returns x.

             (doto (new java.util.HashMap) (.put \"a\" 1) (.put \"b\" 2))",
          :arglists ([x & forms])},
    areduce {:doc "Reduces an expression across an array a, using an index named idx,
                and return value named ret, initialized to init, setting ret to the
                evaluation of expr at each step, returning ret.",
             :arglists ([a idx ret init expr])},
    *default-data-reader-fn* {:doc "When no data reader is found for a tag and *default-data-reader-fn*
                                 is non-nil, it will be called with two arguments,
                                 the tag and the value.  If *default-data-reader-fn* is nil (the
                                 default), an exception will be thrown for the unknown tag."},
    var? {},
    ns-aliases {:doc "Returns a map of the aliases for the namespace.", :arglists ([ns])},
    read {:doc "Reads the next object from stream, which must be an instance of
             java.io.PushbackReader or some derivee.  stream defaults to the
             current value of *in*.

             Opts is a persistent map with valid keys:
               :read-cond - :allow to process reader conditionals, or
                            :preserve to keep all branches
               :features - persistent set of feature keywords for reader conditionals
               :eof - on eof, return value unless :eofthrow, then throw.
                      if not specified, will throw

             Note that read can execute code (controlled by *read-eval*),
             and as such should be used only with trusted sources.

             For data structure interop use clojure.edn/read",
          :arglists ([] [stream] [stream eof-error? eof-value] [stream eof-error? eof-value recursive?] [opts stream])},
    ns-resolve {:doc "Returns the var or Class to which a symbol will be resolved in the
                   namespace (unless found in the environment), else nil.  Note that
                   if the symbol is fully qualified, the var/Class to which it resolves
                   need not be present in the namespace.",
                :arglists ([ns sym] [ns env sym])},
    as-> {:doc "Binds name to expr, evaluates the first form in the lexical context
             of that binding, then binds name to that result, repeating for each
             successive form, returning the result of the last form.",
          :arglists ([expr name & forms])},
    when-not {:doc "Evaluates test. If logical false, evaluates body in an implicit do.", :arglists ([test & body])},
    *1 {:doc "bound in a repl thread to the most recent value printed"},
    *print-meta* {:doc "If set to logical true, when printing an object, its metadata will also
                     be printed in a form that can be read back by the reader.

                     Defaults to false."},
    when {:doc "Evaluates test. If logical true, evaluates body in an implicit do.", :arglists ([test & body])},
    ns-refers {:doc "Returns a map of the refer mappings for the namespace.", :arglists ([ns])},
    prn {},
    extend {:doc "Implementations of protocol methods can be provided using the extend construct:

               (extend AType
                 AProtocol
                  {:foo an-existing-fn
                   :bar (fn [a b] ...)
                   :baz (fn ([a]...) ([a b] ...)...)}
                 BProtocol
                   {...}
                 ...)

               extend takes a type/class (or interface, see below), and one or more
               protocol + method map pairs. It will extend the polymorphism of the
               protocol's methods to call the supplied methods when an AType is
               provided as the first argument.

               Method maps are maps of the keyword-ized method names to ordinary
               fns. This facilitates easy reuse of existing fns and fn maps, for
               code reuse/mixins without derivation or composition. You can extend
               an interface to a protocol. This is primarily to facilitate interop
               with the host (e.g. Java) but opens the door to incidental multiple
               inheritance of implementation since a class can inherit from more
               than one interface, both of which extend the protocol. It is TBD how
               to specify which impl to use. You can extend a protocol on nil.

               If you are supplying the definitions explicitly (i.e. not reusing
               exsting functions or mixin maps), you may find it more convenient to
               use the extend-type or extend-protocol macros.

               Note that multiple independent extend clauses can exist for the same
               type, not all protocols need be defined in a single extend call.

               See also:
               extends?, satisfies?, extenders",
            :arglists ([atype & proto+mmaps])},
    some->> {:doc "When expr is not nil, threads it into the first form (via ->>),
                and when that result is not nil, through the next etc",
             :arglists ([expr & forms])},
    create-ns {:doc "Create a new namespace named by the symbol if one doesn't already
                  exist, returns it or the already-existing namespace of the same
                  name.",
               :arglists ([sym])},
    ex-cause {},
    ex-message {},
    amap {:doc "Maps an expression across an array a, using an index named idx, and
             return value named ret, initialized to a clone of a, then setting
             each element of ret to the evaluation of expr, returning the new
             array ret.",
          :arglists ([a idx ret expr])},
    use {:doc "Like 'require, but also refers to each lib's namespace using
            clojure.core/refer. Use :use in the ns macro in preference to calling
            this directly.

            'use accepts additional options in libspecs: :exclude, :only, :rename.
            The arguments and semantics for :exclude, :only, and :rename are the same
            as those documented for clojure.core/refer.",
         :arglists ([& args])},
    declare {:doc "defs the supplied var names with no bindings, useful for making forward declarations.",
             :arglists ([& names])},
    reset! {:doc "Sets the value of atom to newval without regard for the\ncurrent value. Returns newval.",
            :arglists ([atom newval])},
    println {},
    extend-type {:doc "A macro that expands into an extend call. Useful when you are
                    supplying the definitions explicitly inline, extend-type
                    automatically creates the maps required by extend.  Propagates the
                    class as a type hint on the first argument of all fns.

                    (extend-type MyType
                      Countable
                        (cnt [c] ...)
                      Foo
                        (bar [x y] ...)
                        (baz ([x] ...) ([x y & zs] ...)))

                    expands into:

                    (extend MyType
                     Countable
                       {:cnt (fn [c] ...)}
                     Foo
                       {:baz (fn ([x] ...) ([x y & zs] ...))
                        :bar (fn [x y] ...)})",
                 :arglists ([t & specs])},
    macroexpand-1 {:doc "If form represents a macro form, returns its expansion,\nelse returns form.", :arglists ([form])},
    defmethod {:doc "Creates and installs a new method of multimethod associated with dispatch-value. ",
               :arglists ([multifn dispatch-val & fn-tail])},
    requiring-resolve {:doc "Resolves namespace-qualified sym per 'resolve'. If initial resolve
                          fails, attempts to require sym's namespace and retries.",
                       :arglists ([sym])},
    require {:doc "Loads libs, skipping any that are already loaded. Each argument is
                either a libspec that identifies a lib, a prefix list that identifies
                multiple libs whose names share a common prefix, or a flag that modifies
                how all the identified libs are loaded. Use :require in the ns macro
                in preference to calling this directly.

                Libs

                A 'lib' is a named set of resources in classpath whose contents define a
                library of Clojure code. Lib names are symbols and each lib is associated
                with a Clojure namespace and a Java package that share its name. A lib's
                name also locates its root directory within classpath using Java's
                package name to classpath-relative path mapping. All resources in a lib
                should be contained in the directory structure under its root directory.
                All definitions a lib makes should be in its associated namespace.

                'require loads a lib by loading its root resource. The root resource path
                is derived from the lib name in the following manner:
                Consider a lib named by the symbol 'x.y.z; it has the root directory
                <classpath>/x/y/, and its root resource is <classpath>/x/y/z.clj, or
                <classpath>/x/y/z.cljc if <classpath>/x/y/z.clj does not exist. The
                root resource should contain code to create the lib's
                namespace (usually by using the ns macro) and load any additional
                lib resources.

                Libspecs

                A libspec is a lib name or a vector containing a lib name followed by
                options expressed as sequential keywords and arguments.

                Recognized options:
                :as takes a symbol as its argument and makes that symbol an alias to the
                  lib's namespace in the current namespace.
                :as-alias takes a symbol as its argument and aliases like :as, however
                  the lib will not be loaded. If the lib has not been loaded, a new
                  empty namespace will be created (as with create-ns).
                :refer takes a list of symbols to refer from the namespace or the :all
                  keyword to bring in all public vars.

                Prefix Lists

                It's common for Clojure code to depend on several libs whose names have
                the same prefix. When specifying libs, prefix lists can be used to reduce
                repetition. A prefix list contains the shared prefix followed by libspecs
                with the shared prefix removed from the lib names. After removing the
                prefix, the names that remain must not contain any periods.

                Flags

                A flag is a keyword.
                Recognized flags: :reload, :reload-all, :verbose
                :reload forces loading of all the identified libs even if they are
                  already loaded (has no effect on libspecs using :as-alias)
                :reload-all implies :reload and also forces loading of all libs that the
                  identified libs directly or indirectly load via require or use
                  (has no effect on libspecs using :as-alias)
                :verbose triggers printing information about each load, alias, and refer

                Example:

                The following would load the libraries clojure.zip and clojure.set
                abbreviated as 's'.

                (require '(clojure zip [set :as s]))",
             :arglists ([& args])},
    *data-readers* {:doc "Map from reader tag symbols to data reader Vars.

                       When Clojure starts, it searches for files named 'data_readers.clj'
                       and 'data_readers.cljc' at the root of the classpath. Each such file
                       must contain a literal map of symbols, like this:

                           {foo/bar my.project.foo/bar
                            foo/baz my.project/baz}

                       The first symbol in each pair is a tag that will be recognized by
                       the Clojure reader. The second symbol in the pair is the
                       fully-qualified name of a Var which will be invoked by the reader to
                       parse the form following the tag. For example, given the
                       data_readers.clj file above, the Clojure reader would parse this
                       form:

                           #foo/bar [1 2 3]

                       by invoking the Var #'my.project.foo/bar on the vector [1 2 3]. The
                       data reader function is invoked on the form AFTER it has been read
                       as a normal Clojure data structure by the reader.

                       Reader tags without namespace qualifiers are reserved for
                       Clojure. Default reader tags are defined in
                       clojure.core/default-data-readers but may be overridden in
                       data_readers.clj, data_readers.cljc, or by rebinding this Var."},
    extend-protocol {:doc "Useful when you want to provide several implementations of the same
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
                           (bar [x y] ...)))",
                     :arglists ([p & specs])},
    *e {:doc "bound in a repl thread to the most recent exception caught by the repl"},
    cond-> {:doc "Takes an expression and a set of test/form pairs. Threads expr (via ->)
               through each form for which the corresponding test
               expression is true. Note that, unlike cond branching, cond-> threading does
               not short circuit after the first true test expression.",
            :arglists ([expr & clauses])},
    dotimes {:doc "bindings => name n

                Repeatedly executes body (presumably for side-effects) with name
                bound to integers from 0 through n-1.",
             :arglists ([bindings & body])},
    reify {:doc "reify creates an object implementing a protocol or interface.
               reify is a macro with the following structure:

              (reify options* specs*)

               Currently there are no options.

               Each spec consists of the protocol or interface name followed by zero
               or more method bodies:

               protocol-or-interface-or-Object
               (methodName [args+] body)*

               Methods should be supplied for all methods of the desired
               protocol(s) and interface(s). You can also define overrides for
               methods of Object. Note that the first parameter must be supplied to
               correspond to the target object ('this' in Java parlance). Thus
               methods for interfaces will take one more argument than do the
               interface declarations.  Note also that recur calls to the method
               head should *not* pass the target object, it will be supplied
               automatically and can not be substituted.

               The return type can be indicated by a type hint on the method name,
               and arg types can be indicated by a type hint on arg names. If you
               leave out all hints, reify will try to match on same name/arity
               method in the protocol(s)/interface(s) - this is preferred. If you
               supply any hints at all, no inference is done, so all hints (or
               default of Object) must be correct, for both arguments and return
               type. If a method is overloaded in a protocol/interface, multiple
               independent method definitions must be supplied.  If overloaded with
               same arity in an interface you must specify complete hints to
               disambiguate - a missing hint implies Object.

               recur works to method heads The method bodies of reify are lexical
               closures, and can refer to the surrounding local scope:

               (str (let [f \"foo\"]
                    (reify Object
                      (toString [this] f))))
               == \"foo\"

               (seq (let [f \"foo\"]
                    (reify clojure.lang.Seqable
                      (seq [this] (seq f)))))
               == (\\f \\o \\o))

               reify always implements clojure.lang.IObj and transfers meta
               data of the form to the created object.

               (meta ^{:k :v} (reify Object (toString [this] \"foo\")))
               == {:k :v}",
           :arglists ([& opts+specs])},
    *clojure-version* {:doc "The version info for Clojure core, as a map containing :major :minor
                          :incremental and :qualifier keys. Feature releases may increment
                          :minor and/or :major, bugfix releases will increment :incremental.
                          Possible values of :qualifier include \"GA\", \"SNAPSHOT\", \"RC-x\" \"BETA-x\""},
    instance? {:doc "Evaluates x and tests if it is an instance of the class\nc. Returns true or false", :arglists ([c x])},
    with-open {:doc "bindings => [name init ...]

                  Evaluates body in a try expression with names bound to the values
                  of the inits, and a finally clause that calls (.close name) on each
                  name in reverse order.",
               :arglists ([bindings & body])},
    defonce {:doc "defs name to have the root value of the expr iff the named var has no root value,
                else expr is unevaluated",
             :arglists ([name expr])},
    the-ns {:doc "If passed a namespace, returns it. Else, when passed a symbol,
               returns the namespace named by it, throwing an exception if not
               found.",
            :arglists ([x])},
    record? {:doc "Returns true if x is a record", :arglists ([x])},
    ns-name {:doc "Returns the name of the namespace, a symbol.", :arglists ([ns])},
    defn- {:doc "same as defn, yielding non-public def", :arglists ([name & decls])},
    *out* {:doc "A java.io.Writer object representing standard output for print operations.

              Defaults to System/out, wrapped in an OutputStreamWriter"},
    ns-map {:doc "Returns a map of all the mappings for the namespace.", :arglists ([ns])},
    defprotocol {:doc "A protocol is a named set of named methods and their signatures:
                    (defprotocol AProtocolName

                      ;optional doc string
                      \"A doc string for AProtocol abstraction\"

                     ;options
                     :extend-via-metadata true

                    ;method signatures
                      (bar [this a b] \"bar docs\")
                      (baz [this a] [this a b] [this a b c] \"baz docs\"))

                    No implementations are provided. Docs can be specified for the
                    protocol overall and for each method. The above yields a set of
                    polymorphic functions and a protocol object. All are
                    namespace-qualified by the ns enclosing the definition The resulting
                    functions dispatch on the type of their first argument, which is
                    required and corresponds to the implicit target object ('this' in
                    Java parlance). defprotocol is dynamic, has no special compile-time
                    effect, and defines no new types or classes. Implementations of
                    the protocol methods can be provided using extend.

                    When :extend-via-metadata is true, values can extend protocols by
                    adding metadata where keys are fully-qualified protocol function
                    symbols and values are function implementations. Protocol
                    implementations are checked first for direct definitions (defrecord,
                    deftype, reify), then metadata definitions, then external
                    extensions (extend, extend-type, extend-protocol)

                    defprotocol will automatically generate a corresponding interface,
                    with the same name as the protocol, i.e. given a protocol:
                    my.ns/Protocol, an interface: my.ns.Protocol. The interface will
                    have methods corresponding to the protocol functions, and the
                    protocol will automatically work with instances of the interface.

                    Note that you should not use this interface with deftype or
                    reify, as they support the protocol directly:

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
                    => 17",
                 :arglists ([name & opts+sigs])},
    swap! {:doc "Atomically swaps the value of atom to be:
              (apply f current-value-of-atom args). Note that f may be called
              multiple times, and thus should be free of side effects.  Returns
              the value that was swapped in.",
           :arglists ([atom f] [atom f x] [atom f x y] [atom f x y & args])},
    assert {:doc "Evaluates expr and throws an exception if it does not evaluate to\nlogical true.",
            :arglists ([x] [x message])},
    print {},
    *in* {:doc "A java.io.Reader object representing standard input for read operations.

             Defaults to System/in, wrapped in a LineNumberingPushbackReader"},
    letfn {:doc "fnspec ==> (fname [params*] exprs) or (fname ([params*] exprs)+)

              Takes a vector of function specs and a body, and generates a set of
              bindings of functions to their names. All of the names are available
              in all of the definitions of the functions, as well as the body.",
           :arglists ([fnspecs & body]),
           :special-form true},
    read-line {},
    descendants {:doc "Returns the immediate and indirect children of tag, through a
                    relationship established via derive. h must be a hierarchy obtained
                    from make-hierarchy, if not supplied defaults to the global
                    hierarchy. Note: does not work on Java type inheritance
                    relationships.",
                 :arglists ([tag] [h tag])},
    with-out-str {:doc "Evaluates exprs in a context in which *out* is bound to a fresh
                     StringWriter.  Returns the string created by any nested printing
                     calls.",
                  :arglists ([& body])},
    condp {:doc "Takes a binary predicate, an expression, and a set of clauses.
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
              IllegalArgumentException is thrown.",
           :arglists ([pred expr & clauses])},
    derive {:doc "Establishes a parent/child relationship between parent and
               tag. Parent must be a namespace-qualified symbol or keyword and
               child can be either a namespace-qualified symbol or keyword or a
               class. h must be a hierarchy obtained from make-hierarchy, if not
               supplied defaults to, and modifies, the global hierarchy.",
            :arglists ([tag parent] [h tag parent])},
    load-string {:doc "Sequentially read and evaluate the set of forms contained in the\nstring", :arglists ([s])},
    ancestors {:doc "Returns the immediate and indirect parents of tag, either via a Java type
                  inheritance relationship or a relationship established via derive. h
                  must be a hierarchy obtained from make-hierarchy, if not supplied
                  defaults to the global hierarchy",
               :arglists ([tag] [h tag])},
    cond {:doc "Takes a set of test/expr pairs. It evaluates each test one at a
             time.  If a test returns logical true, cond evaluates and returns
             the value of the corresponding expr and doesn't evaluate any of the
             other tests or exprs. (cond) returns nil.",
          :arglists ([& clauses])},
    intern {:doc "Finds or creates a var named by the symbol name in the namespace
               ns (which can be a symbol or a namespace), setting its root binding
               to val if supplied. The namespace must exist. The var will adopt any
               metadata from the name symbol.  Returns the var.",
            :arglists ([ns name] [ns name val])},
    with-in-str {:doc "Evaluates body in a context in which *in* is bound to a fresh
                    StringReader initialized with the string s.",
                 :arglists ([s & body])},
    some-> {:doc "When expr is not nil, threads it into the first form (via ->),
               and when that result is not nil, through the next etc",
            :arglists ([expr & forms])},
    ns-interns {:doc "Returns a map of the intern mappings for the namespace.", :arglists ([ns])},
    all-ns {:doc "Returns a sequence of all namespaces.", :arglists ([])},
    for {:doc "List comprehension. Takes a vector of one or more
             binding-form/collection-expr pairs, each followed by zero or more
             modifiers, and yields a lazy sequence of evaluations of expr.
             Collections are iterated in a nested fashion, rightmost fastest,
             and nested coll-exprs can refer to bindings created in prior
             binding-forms.  Supported modifiers are: :let [binding-form expr ...],
             :while test, :when test.

            (take 100 (for [x (range 100000000) y (range 1000000) :while (< y x)] [x y]))",
         :arglists ([seq-exprs body-expr])},
    binding {:doc "binding => var-symbol init-expr

                Creates new bindings for the (already-existing) vars, with the
                supplied initial values, executes the exprs in an implicit do, then
                re-establishes the bindings that existed before.  The new bindings
                are made in parallel (unlike let); all init-exprs are evaluated
                before the vars are bound to their new values.",
             :arglists ([bindings & body])},
    with-local-vars {:doc "varbinding=> symbol init-expr

                        Executes the exprs in a context in which the symbols are bound to
                        vars with per-thread bindings to the init-exprs.  The symbols refer
                        to the var objects themselves, and must be accessed with var-get and
                        var-set",
                     :arglists ([name-vals-vec & body])},
    ns-imports {:doc "Returns a map of the import mappings for the namespace.", :arglists ([ns])},
    *3 {:doc "bound in a repl thread to the third most recent value printed"},
    alias {:doc "Add an alias in the current namespace to another
              namespace. Arguments are two symbols: the alias to be used, and
              the symbolic name of the target namespace. Use :as in the ns macro in preference
              to calling this directly.",
           :arglists ([alias namespace-sym])},
    read-string {:doc "Reads one object from the string s. Optionally include reader
                    options, as specified in read.

                    Note that read-string can execute code (controlled by *read-eval*),
                    and as such should be used only with trusted sources.

                    For data structure interop use clojure.edn/read-string",
                 :arglists ([s] [opts s])},
    proxy {:doc "class-and-interfaces - a vector of class names

              args - a (possibly empty) vector of arguments to the superclass
              constructor.

              f => (name [params*] body) or
              (name ([params*] body) ([params+] body) ...)

              Expands to code which creates a instance of a proxy class that
              implements the named class/interface(s) by calling the supplied
              fns. A single class, if provided, must be first. If not provided it
              defaults to Object.

              The interfaces names must be valid interface types. If a method fn
              is not provided for a class method, the superclass method will be
              called. If a method fn is not provided for an interface method, an
              UnsupportedOperationException will be thrown should it be
              called. Method fns are closures and can capture the environment in
              which proxy is called. Each method fn takes an additional implicit
              first arg, which is bound to 'this. Note that while method fns can
              be provided to override protected methods, they have no other access
              to protected members, nor to super, as these capabilities cannot be
              proxied.",
           :arglists ([class-and-interfaces args & fs])},
    with-redefs {:doc "binding => var-symbol temp-value-expr

                    Temporarily redefines Vars while executing the body.  The
                    temp-value-exprs will be evaluated and each resulting value will
                    replace in parallel the root value of its Var.  After the body is
                    executed, the root values of all the Vars will be set back to their
                    old values.  These temporary changes will be visible in all threads.
                    Useful for mocking out functions during testing.",
                 :arglists ([bindings & body])},
    *ns* {:doc "A clojure.lang.Namespace object representing the current namespace."},
    defmulti {:doc "Creates a new multimethod with the associated dispatch function.
                 The docstring and attr-map are optional.

                 Options are key-value pairs and may be one of:

                 :default

                 The default dispatch value, defaults to :default

                 :hierarchy

                 The value used for hierarchical dispatch (e.g. ::square is-a ::shape)

                 Hierarchies are type-like relationships that do not depend upon type
                 inheritance. By default Clojure's multimethods dispatch off of a
                 global hierarchy map.  However, a hierarchy relationship can be
                 created with the derive function used to augment the root ancestor
                 created with make-hierarchy.

                 Multimethods expect the value of the hierarchy option to be supplied as
                 a reference type e.g. a var (i.e. via the Var-quote dispatch macro #'
                 or the var special form).",
              :arglists ([name docstring? attr-map? dispatch-fn & options])},
    if-let {:doc "bindings => binding-form test

               If test is true, evaluates then with binding-form bound to the value of
               test, if not, yields else",
            :arglists ([bindings then] [bindings then else & oldform])},
    underive {:doc "Removes a parent/child relationship between parent and
                 tag. h must be a hierarchy obtained from make-hierarchy, if not
                 supplied defaults to, and modifies, the global hierarchy.",
              :arglists ([tag parent] [h tag parent])},
    *print-readably* {:doc "When set to logical false, strings and characters will be printed with
                         non-alphanumeric characters converted to the appropriate escape sequences.

                         Defaults to true"},
    *flush-on-newline* {:doc "When set to true, output will be flushed whenever a newline is printed.\n\nDefaults to true."},
    lazy-cat {:doc "Expands to code which yields a lazy sequence of the concatenation
                 of the supplied colls.  Each coll expr is not evaluated until it is
                 needed.

                 (lazy-cat xs ys zs) === (concat (lazy-seq xs) (lazy-seq ys) (lazy-seq zs))",
              :arglists ([& colls])},
    comment {:doc "Ignores body, yields nil", :arglists ([& body])},
    parents {:doc "Returns the immediate parents of tag, either via a Java type
                inheritance relationship or a relationship established via derive. h
                must be a hierarchy obtained from make-hierarchy, if not supplied
                defaults to the global hierarchy",
             :arglists ([tag] [h tag])},
    deref {:doc "Also reader macro: @ref/@agent/@var/@atom/@delay/@future/@promise. Within a transaction,
              returns the in-transaction-value of ref, else returns the
              most-recently-committed value of ref. When applied to a var, agent
              or atom, returns its current state. When applied to a delay, forces
              it if not already forced. When applied to a future, will block if
              computation not complete. When applied to a promise, will block
              until a value is delivered.  The variant taking a timeout can be
              used for blocking references (futures and promises), and will return
              timeout-val if the timeout (in milliseconds) is reached before a
              value is available. See also - realized?.",
           :arglists ([ref] [ref timeout-ms timeout-val])},
    resolve {:doc "same as (ns-resolve *ns* symbol) or (ns-resolve *ns* &env symbol)", :arglists ([sym] [env sym])},
    *print-dup* {:doc "When set to logical true, objects will be printed in a way that preserves
                    their type when read in later.

                    Defaults to false."},
    defrecord {:doc "(defrecord name [fields*]  options* specs*)

                  Options are expressed as sequential keywords and arguments (in any order).

                  Supported options:
                  :load-ns - if true, importing the record class will cause the
                             namespace in which the record was defined to be loaded.
                             Defaults to false.

                  Each spec consists of a protocol or interface name followed by zero
                  or more method bodies:

                  protocol-or-interface-or-Object
                  (methodName [args*] body)*

                  Dynamically generates compiled bytecode for class with the given
                  name, in a package with the same name as the current namespace, the
                  given fields, and, optionally, methods for protocols and/or
                  interfaces.

                  The class will have the (immutable) fields named by
                  fields, which can have type hints. Protocols/interfaces and methods
                  are optional. The only methods that can be supplied are those
                  declared in the protocols/interfaces.  Note that method bodies are
                  not closures, the local environment includes only the named fields,
                  and those fields can be accessed directly.

                  Method definitions take the form:

                  (methodname [args*] body)

                  The argument and return types can be hinted on the arg and
                  methodname symbols. If not supplied, they will be inferred, so type
                  hints should be reserved for disambiguation.

                  Methods should be supplied for all methods of the desired
                  protocol(s) and interface(s). You can also define overrides for
                  methods of Object. Note that a parameter must be supplied to
                  correspond to the target object ('this' in Java parlance). Thus
                  methods for interfaces will take one more argument than do the
                  interface declarations. Note also that recur calls to the method
                  head should *not* pass the target object, it will be supplied
                  automatically and can not be substituted.

                  In the method bodies, the (unqualified) name can be used to name the
                  class (for calls to new, instance? etc).

                  The class will have implementations of several (clojure.lang)
                  interfaces generated automatically: IObj (metadata support) and
                  IPersistentMap, and all of their superinterfaces.

                  In addition, defrecord will define type-and-value-based =,
                  and will defined Java .hashCode and .equals consistent with the
                  contract for java.util.Map.

                  When AOT compiling, generates compiled bytecode for a class with the
                  given name (a symbol), prepends the current ns as the package, and
                  writes the .class file to the *compile-path* directory.

                  Two constructors will be defined, one taking the designated fields
                  followed by a metadata map (nil for none) and an extension field
                  map (nil for none), and one taking only the fields (using nil for
                  meta and extension fields). Note that the field names __meta,
                  __extmap, __hash and __hasheq are currently reserved and should not
                  be used when defining your own records.

                  Given (defrecord TypeName ...), two factory functions will be
                  defined: ->TypeName, taking positional parameters for the fields,
                  and map->TypeName, taking a map of keywords to field values.",
               :arglists ([name [& fields] & opts+specs])},
    with-redefs-fn {:doc "Temporarily redefines Vars during a call to func.  Each val of
                       binding-map will replace the root value of its key which must be
                       a Var.  After func is called with no args, the root values of all
                       the Vars will be set back to their old values.  These temporary
                       changes will be visible in all threads.  Useful for mocking out
                       functions during testing.",
                    :arglists ([binding-map func])},
    when-some {:doc "bindings => binding-form test

                  When test is not nil, evaluates body with binding-form bound to the
                  value of test",
               :arglists ([bindings & body])},
    ->> {:doc "Threads the expr through the forms. Inserts x as the
            last item in the first form, making a list of it if it is not a
            list already. If there are more forms, inserts the first form as the
            last item in second form, etc.",
         :arglists ([x & forms])},
    var-get {},
    refer-clojure {:doc "Same as (refer 'clojure.core <filters>)", :arglists ([& filters])}})

(defn doc-map [sym]
  (some-> (special-doc-map* sym)
          ->meta))