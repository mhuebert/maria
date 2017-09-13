(ns maria.live.ns-utils
  (:require [maria.eval :as e]
            [cljs-live.eval :as c-e]
            [clojure.string :as string]
            [magic-tree.core :as tree]
            [cljs.analyzer :as ana]))


(defn builtin-ns? [s]
  (and (not= s 'maria.user)
       (re-find #"^(?:re-view|maria|cljs|re-db|clojure)" (name s))))

(defn analyzer-ns [c-state ns]
  (get-in @c-state [:cljs.analyzer/namespaces ns]))

(defn user-namespaces [c-state]
  (->> (keys (:cljs.analyzer/namespaces c-state))
       (filter (complement builtin-ns?))))

(defn add-$macros-suffix [sym]
  (symbol (str sym "$macros")))

(defn elide-quote [x]
  (cond-> x
          (and (seq? x) (= 'quote (first x))) (second)))

(defn resolve-sym
  "Resolve a symbol into fully qualified name. Returns vector of [namespace, name] as symbols."
  [c-state c-env sym]
  (let [n (:name (e/resolve-var c-state c-env sym))]
    (mapv symbol [(namespace n) (name n)])))

(defn resolve-var
  "Simplified resolve-var fn, looks up `def` in compiler state."
  ([sym] (resolve-var e/c-state e/c-env sym))
  ([c-state c-env sym]
   (let [[namespace name] (resolve-sym c-state c-env sym)]
     (or (get-in @c-state [:cljs.analyzer/namespaces namespace :defs name])
         (get-in @c-state [:cljs.analyzer/namespaces (add-$macros-suffix namespace) :defs name])))))

(defn get-ns
  ([] (get-ns (:ns @e/c-env)))
  ([ns-name] (get-in @e/c-state [:cljs.analyzer/namespaces ns-name])))

(defn ns-publics*
  ([the-ns only-doc?] (ns-publics* the-ns only-doc? nil))
  ([the-ns only-doc? {:keys [include]}]
   (reduce (fn [names {:keys    [doc anonymous private]
                       the-name :name}]
             (cond-> names
                     (and (not anonymous)
                          (not private)
                          (or (not only-doc?)
                              doc
                              (and include (include the-name))))
                     (assoc (name the-name) the-name))) {} (into (vals (:defs the-ns))
                                                                 (vals (:macros the-ns))))))




(defn full-name [kind [the-name the-target]]
  (if (= the-name the-target)
    the-target
    (case kind
      (:uses) (symbol the-target the-name)
      (:use-macros) (symbol (str the-target "$macros") the-name)
      (:requires
        :require-macros
        :imports) the-target)))

(defn update-kvs [m key-f val-f]
  (reduce (fn [m pair]
            (assoc m (key-f pair) (val-f pair))) {} m))

(defn ns-aliases* [the-ns]
  (reduce (fn [names k]
            (merge names (update-kvs (get the-ns k)
                                     (comp str first)
                                     #(full-name k %)))) {} [:rename-macros
                                                             :renames
                                                             :use-macros
                                                             :imports
                                                             :requires
                                                             :uses]))




;; lifted from https://github.com/clojure/clojure/blob/42a7fd42cfae973d2af16d4bed40c7594574b58b/src/clj/clojure/repl.clj#L19
(def special-doc-map
  '{.             {:url   "java_interop#dot"
                   :forms [(.instanceMember instance args*)
                           (.instanceMember Classname args*)
                           (Classname/staticMethod args*)
                           Classname/staticField]
                   :doc   "The instance member form works for both fields and methods.
  They all expand into calls to the dot operator at macroexpansion time."}
    def           {:forms [(def symbol doc-string? init?)]
                   :doc   "Creates and interns a global var with the name
  of symbol in the current namespace (*ns*) or locates such a var if
  it already exists.  If init is supplied, it is evaluated, and the
  root binding of the var is set to the resulting value.  If init is
  not supplied, the root binding of the var is unaffected."}
    do            {:forms [(do exprs*)]
                   :doc   "Evaluates the expressions in order and returns the value of
  the last. If no expressions are supplied, returns nil."}
    if            {:forms [(if test then else?)]
                   :doc   "Evaluates test. If not the singular values nil or false,
  evaluates and yields then, otherwise, evaluates and yields else. If
  else is not supplied it defaults to nil."}
    monitor-enter {:forms [(monitor-enter x)]
                   :doc   "Synchronization primitive that should be avoided
  in user code. Use the 'locking' macro."}
    monitor-exit  {:forms [(monitor-exit x)]
                   :doc   "Synchronization primitive that should be avoided
  in user code. Use the 'locking' macro."}
    new           {:forms [(Classname. args*) (new Classname args*)]
                   :url   "java_interop#new"
                   :doc   "The args, if any, are evaluated from left to right, and
  passed to the constructor of the class named by Classname. The
  constructed object is returned."}
    quote         {:forms [(quote form)]
                   :doc   "Yields the unevaluated form."}
    recur         {:forms [(recur exprs*)]
                   :doc   "Evaluates the exprs in order, then, in parallel, rebinds
  the bindings of the recursion point to the values of the exprs.
  Execution then jumps back to the recursion point, a loop or fn method."}
    set!          {:forms [(set! var-symbol expr)
                           (set! (. instance-expr instanceFieldName-symbol) expr)
                           (set! (. Classname-symbol staticFieldName-symbol) expr)]
                   :url   "vars#set"
                   :doc   "Used to set thread-local-bound vars, Java object instance
fields, and Java class static fields."}
    throw         {:forms [(throw expr)]
                   :doc   "The expr is evaluated and thrown, therefore it should
  yield an instance of some derivee of Throwable."}
    try           {:forms [(try expr* catch-clause* finally-clause?)]
                   :doc   "catch-clause => (catch classname name expr*)
  finally-clause => (finally expr*)
  Catches and handles Java exceptions."}
    var           {:forms [(var symbol)]
                   :doc   "The symbol must resolve to a var, and the Var object
itself (not its value) is returned. The reader macro #'x expands to (var x)."}})

(defn special-doc [name-symbol]
  (some-> (special-doc-map name-symbol)
          (assoc :name (symbol "clojure.core" (name name-symbol))
                 :special-form true)))

(defn resolve-var-or-special
  ([name] (resolve-var-or-special e/c-state e/c-env name))
  ([c-state c-env name]
   (when (symbol? name)
     (or (resolve-var c-state c-env name)
         (when-let [repl-special (get c-e/repl-specials name)]
           (merge (meta repl-special)
                  (resolve-var c-state c-env (:name (meta repl-special)))))
         (special-doc name)))))


(def core-publics
  (memoize (fn [] (merge (ns-publics* (get-ns 'cljs.core) true {:include '#{cljs.core/munge}})
                         (ns-publics* (get-ns 'cljs.core$macros) true #{})))))

(defn ana-env []
  (assoc @e/c-state :ns (ana/get-namespace e/c-state (:ns @e/c-env))))

(defn ns-completions
  ([token] (ns-completions (:ns @e/c-env) token))
  ([ns-name node]
   (let [the-symbol (tree/sexp node)
         root-ns (some->> (namespace the-symbol)
                          (ana/resolve-ns-alias (ana-env))
                          (get-ns))
         sym-string (if root-ns (name the-symbol) (str the-symbol))]
     (sort (for [[completion full-name] (if root-ns
                                          (ns-publics* root-ns false)
                                          (merge (ns-aliases* (get-ns ns-name))
                                                 (ns-publics* (get-ns ns-name) false)
                                                 (core-publics)))
                 :when (and (string/starts-with? completion sym-string)
                            (not= completion the-symbol))]
             [completion full-name])))))
