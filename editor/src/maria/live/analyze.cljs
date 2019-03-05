(ns maria.live.analyze
  (:require [cljs.js :as cljs]
            [cljs.analyzer :as ana]
            [cljs.env :as env]
            [cljs.tools.reader :as r]
            [cljs.tagged-literals :as tags]
            [clojure.walk :as walk]
            [maria.eval :as e])
  (:require-macros [cljs.analyzer.macros :refer [no-warn]]))


#_(defn analyze-form
  "Returns the ClojureScript ast for a form."
  ([form]
   (analyze-form e/c-state e/c-env form nil))
  ([form opts]
   (analyze-form e/c-state e/c-env form opts))
  ([c-state c-env form opts]
   (let [aenv (ana/empty-env)
         the-ns (:ns @c-env)]
     (binding [env/*compiler* c-state
               ana/*cljs-ns* the-ns
               ana/*cljs-static-fns* (:static-fns opts)
               *ns* (create-ns the-ns)
               ana/*passes* (or (:passes opts) ana/*passes*)
               ;r/*alias-map* (cljs/current-alias-map)
               r/*data-readers* tags/*cljs-data-readers*
               r/resolve-symbol cljs/resolve-symbol
               ana/*cljs-file* (:cljs-file opts)]
       (let [aenv (cond-> (assoc aenv :ns (ana/get-namespace ana/*cljs-ns*))
                          (:context opts) (assoc :context (:context opts))
                          (:def-emits-var opts) (assoc :def-emits-var true))]
         (try
           (no-warn (ana/analyze aenv form nil opts))
           (catch :default cause
             (ana/error aenv
                        (str "Could not analyze " name) cause))))))))

(defn some-namespaced-symbol
  "Returns s (the symbol) if it has a namespace."
  [s]
  (when (qualified-symbol? s)
    (namespace s)))

(defn printable-ast
  "Recursively remove :env keys from an ast - useful for printing."
  [x]
  (walk/prewalk #(cond-> % (map? %) (dissoc :env)) x))

(defn ast-vars
  "Returns a list of names of vars contained in an ast."
  [ast]
  (->> ast
       (tree-seq :children :children)
       (keep #(when (and (map? %)
                         (= (:op %) :var))
                (-> % :info :name (some-namespaced-symbol))))))