(ns maria.live.analyzer
  (:require [cljs.js :as cljs]
            [cljs.analyzer :as ana]
            [cljs.env :as env]
            [cljs.tools.reader :as r]
            [cljs.tagged-literals :as tags]
            [clojure.walk :as walk])
  (:require-macros [cljs.analyzer.macros :refer [no-warn]]))


(defn analyze-form [c-state c-env form opts]
  (let [aenv (ana/empty-env)
        the-ns (:ns @c-env)]
    (binding [env/*compiler* c-state
              ana/*cljs-ns* the-ns
              ana/*cljs-static-fns* (:static-fns opts)
              *ns* (create-ns the-ns)
              ana/*passes* (or (:passes opts) ana/*passes*)
              r/*alias-map* (cljs/current-alias-map)
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
                       (str "Could not analyze " name) cause)))))))

(defn some-namespaced-symbol [s]
  (when (namespace s) s))

(defn printable-ast [x]
  (walk/prewalk #(cond-> % (map? %) (dissoc :env)) x))

(defn ast-vars [ast]
  (->> ast
       (tree-seq :children :children)
       (keep #(when (and (map? %)
                         (= (:op %) :var))
                (-> % :info :name (some-namespaced-symbol))))))