(ns maria.live.ns-utils
  (:require [maria.eval :as e]
            [lark.eval :as c-e]
            [clojure.string :as string]
            [lark.tree.core :as tree]
            [cljs.analyzer :as ana]
            [clojure.string :as str]
            [maria.util :as util]
            [cljs.tools.reader.edn :as edn]))

(defn builtin-ns? [s]
  (and (not= s 'maria.user)
       (re-find #"^(?:chia|maria|cljs|re-db|clojure)" (name s))))

(defn analyzer-ns [c-state ns]
  (get-in @c-state [:cljs.analyzer/namespaces ns]))

(defn user-namespaces [c-state]
  (->> (keys (:cljs.analyzer/namespaces c-state))
       (filter (complement builtin-ns?))))

(defn add-$macros-suffix [sym]
  (if-not (string/ends-with? (str sym) "$macros")
    (symbol (str sym "$macros"))
    sym)
  (symbol (str sym "$macros")))

(defn elide-quote [x]
  (cond-> x
          (and (seq? x) (= 'quote (first x))) (second)))

(defn resolve-sym
  "Resolve a symbol into fully qualified name. Returns vector of [namespace, name] as symbols."
  [c-state c-env sym]
  (let [n (:name (e/resolve-var c-state c-env sym))]
    (mapv symbol [(namespace n) (name n)])))

(defn get-ns
  ([] (get-ns (:ns @e/c-env)))
  ([ns-name] (get-in @e/c-state [:cljs.analyzer/namespaces ns-name])))

;; copied from cljs.analyzer
(defn resolve-macro-var
  "Given env, an analysis environment, and sym, a symbol, resolve a macro."
  [c-state c-env sym]
  (binding [cljs.env/*compiler* c-state]
    (let [ns (:ns @c-env)
          namespaces (get @c-state :cljs.analyzer/namespaces)
          env {:ns (get-ns)}]
      (cond
        (some? (namespace sym))
        (let [ns (namespace sym)
              ns (if (= "clojure.core" ns) "cljs.core" ns)
              full-ns (add-$macros-suffix (ana/resolve-macro-ns-alias env ns))]
          (get-in namespaces [full-ns :defs (symbol (name sym))]))

        (some? (get-in namespaces [ns :use-macros sym]))
        (let [full-ns (add-$macros-suffix (get-in namespaces [ns :use-macros sym]))]
          (get-in namespaces [full-ns :defs sym]))

        (some? (get-in namespaces [ns :rename-macros sym]))
        (let [qualified-symbol (get-in namespaces [ns :rename-macros sym])
              full-ns (add-$macros-suffix (symbol (namespace qualified-symbol)))
              sym (symbol (name qualified-symbol))]
          (get-in namespaces [full-ns :defs sym]))

        :else
        (let [ns (cond
                   (some? (get-in namespaces [ns :macros sym])) ns
                   (ana/core-name? env sym) ana/CLJS_CORE_MACROS_SYM)]
          (when (some? ns)
            (get-in namespaces [(add-$macros-suffix ns) :defs sym])))))))

(defn resolve-var
  "Simplified resolve-var fn, looks up `def` in compiler state."
  ([sym] (resolve-var e/c-state e/c-env sym))
  ([c-state c-env sym]
   (let [[namespace name] (resolve-sym c-state c-env sym)]
     (or (get-in @c-state [:cljs.analyzer/namespaces namespace :defs name])
         (get-in @c-state [:cljs.analyzer/namespaces (add-$macros-suffix namespace) :defs name])))))

(defn ns-publics*
  ([the-ns only-doc?] (ns-publics* the-ns only-doc? nil))
  ([the-ns only-doc? {:keys [include]}]
   (reduce (fn [names {:keys [doc anonymous private]
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
    fn {:forms [(fn & sigs)]
        :doc "params => positional-params* , or positional-params* & next-param
positional-param => binding-form
next-param => binding-form
name => symbol

Defines a function"}
    if {:forms [(if test then else?)]
        :doc "Evaluates test. If not the singular values nil or false,
  evaluates and yields then, otherwise, evaluates and yields else. If
  else is not supplied it defaults to nil."}
    let {:forms [(let bindings & body)]
         :doc "Evaluates the exprs in a lexical context in which the symbols in
the binding-forms are bound to their respective init-exprs or parts
therein."}
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
         (resolve-macro-var c-state c-env name)
         (special-doc name)))))


(def core-publics
  (memoize (fn [] (merge (ns-publics* (get-ns 'cljs.core) true {:include '#{cljs.core/munge}})
                         (ns-publics* (get-ns 'cljs.core$macros) true #{})))))

(defn ana-env []
  (assoc @e/c-state :ns (ana/get-namespace e/c-state (:ns @e/c-env))))

(defn completion-data [node]
  (when-let [value (some-> node (.-value))]
    (try (let [val (edn/read-string value)]
           (when (symbol? val)
             [(namespace val) (name val)]))
         (catch js/Error e
           (when (str/ends-with? value "/")
             [(subs value 0 (dec (.-length value)))
              ""])))))

(defn ns-completions
  ([completion-data] (ns-completions (:ns @e/c-env) completion-data))
  ([ns-name [the-ns the-name]]
   (let [resolved-ns (some->> (or the-ns the-name)
                              (ana/resolve-ns-alias (ana-env))
                              (get-ns))
         printable-ns (or the-ns (when resolved-ns the-name))
         the-name (if (and resolved-ns (not the-ns))
                    ""
                    the-name)]
     (sort (for [[alias full-name] (if (or the-ns resolved-ns)
                                     (some-> resolved-ns (ns-publics* false))
                                     (merge (ns-aliases* (get-ns ns-name))
                                            (ns-publics* (get-ns ns-name) false)
                                            (core-publics)))
                 :when (and (string/starts-with? alias the-name)
                            (not= alias the-name))
                 :let [completion (cond->> alias
                                           printable-ns (str printable-ns "/"))]]
             [alias completion full-name])))))

(defn cd-encode [s]
  ;; COPIED from clojuredocs.util: https://github.com/zk/clojuredocs/blob/master/src/cljc/clojuredocs/util.cljc
  ;; Copyright © 2010-present Zachary Kim
  ;; Distributed under the Eclipse Public License version 1.0
  (when s
    (cond
      (= "." s) "_."
      (= ".." s) "_.."
      :else (-> s
                (string/replace #"/" "_fs")
                (string/replace #"\\" "_bs")
                (string/replace #"\?" "_q")))))
