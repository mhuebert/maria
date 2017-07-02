(ns maria.repl-specials
  "Special forms that exist only in the REPL."
  (:require [cljs-live.eval :as e :refer [defspecial]]
            [maria.views.repl-specials :as special-views]
            [maria.messages :as messages]
            [maria.ns-utils :as ns-utils]
            [clojure.string :as string]
            [maria.source-lookups :as source-lookups]))

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

(defn- special-doc [name-symbol]
  (some-> (special-doc-map name-symbol)
          (assoc :name (symbol "clojure.core" (name name-symbol))
                 :special-form true)))

(defspecial dir
  "Display public vars in namespace"
  [c-state c-env ns]
  (let [ns (or ns (:ns @c-env))]
    {:value (special-views/dir c-state ns)}))

(defspecial what-is
  "Defers to maria.messages/what-is; this is only here to handle the edge case of repl-special functions."
  [c-state c-env thing]
  (e/eval-str c-state c-env (str `(maria.messages/what-is ~(cond (and (symbol? thing) (:macro (ns-utils/resolve-var c-state c-env thing)))
                                                                 :maria.kinds/macro

                                                                 (and (symbol? thing) (special-doc-map thing))
                                                                 :maria.kinds/special-form

                                                                 (contains? e/repl-specials thing)
                                                                 :maria.kinds/function

                                                                 :else thing)))))

(defn resolve-var-or-special [c-state c-env name]
  (when (symbol? name)
    (or (ns-utils/resolve-var c-state c-env name)
        (when-let [repl-special (get e/repl-specials name)]
          (meta repl-special)
          #_(ns-utils/resolve-var c-state c-env (:name (meta repl-special))))
        (special-doc name))))

(defspecial doc
  "Show documentation for given symbol"
  [c-state c-env name]
  (if-let [the-var (resolve-var-or-special c-state c-env name)]
    {:value (special-views/doc (merge {:expanded?   true
                                       :standalone? true}
                                      the-var))}
    {:error (js/Error. (if (symbol? name) (str "Could not resolve the symbol `" (string/trim-newline (with-out-str (prn name))) "`. Maybe it has not been defined?")
                                          (str (str "`doc` requires a symbol, but a " (cljs.core/name (messages/kind name)) " was passed."))))}))

(defspecial source
  "Show source code for given symbol"
  [c-state c-env name]
  (let [{:keys [error value] :as val} (e/resolve-var c-state c-env name)]
    (if error
      val
      (if-let [the-var (or (and (symbol? name) (resolve-var-or-special c-state c-env name))
                           (source-lookups/fn-var value))]
        {:value (special-views/var-source the-var)}
        ;; try getting value as function
        (if-let [fn-source (and (fn? value) (source-lookups/fn-source-sync value))]
          {:value fn-source}
          {:error (js/Error. (str "Could not resolve the symbol `" (string/trim-newline (with-out-str (prn name))) "`"))})))))

(defspecial inject
  "Inject vars into a namespace, preserving all metadata (inc. name)"
  [c-state c-env ns mappings]
  (let [ns (ns-utils/elide-quote ns)]
    (doseq [[inject-as sym] (seq (ns-utils/elide-quote mappings))]
      (e/eval c-state c-env `(def ~inject-as ~sym) {:ns ns})
      (swap! c-state update-in [:cljs.analyzer/namespaces ns :defs inject-as] merge (e/resolve-var c-state c-env sym)))))
