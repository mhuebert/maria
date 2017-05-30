(ns maria.repl-specials
  "Special forms that exist only in the REPL."
  (:require [cljs-live.eval :as e :refer [defspecial]]
            [maria.views.repl-specials :as repl-ns]
            [maria.ns-utils :as ns-utils]))

(defspecial doc
  "Show documentation for given symbol"
  [c-state c-env name]
  (let [[namespace name] (let [n (e/resolve-symbol c-state c-env name)]
                           (map symbol [(namespace n) (clojure.core/name n)]))]
    {:value (repl-ns/doc (merge {:expanded?   true
                                 :standalone? true}
                                (or (get-in (ns-utils/ns-map @c-state namespace) [:defs name])
                                    (some-> (get e/repl-specials name) (meta)))))}))

(defspecial dir
  "Display public vars in namespace"
  [c-state c-env ns]
  (let [ns (or ns (:ns @c-env))]
    {:value (repl-ns/dir c-state ns)}))

(defspecial what-is
  "Defers to maria.messages/what-is; this is only here to handle the edge case of repl-special functions."
  [c-state c-env thing]
  (e/eval-str c-state c-env (str `(maria.messages/what-is ~(if (and (symbol? thing)
                                                                    (contains? e/repl-specials thing))
                                                             '(fn []) thing)))))

(defspecial inject
  "Inject vars into a namespace, preserving all metadata (inc. name)"
  [c-state c-env ns mappings]
  (let [ns (ns-utils/elide-quote ns)]
    (doseq [[inject-as sym] (seq (ns-utils/elide-quote mappings))]
      (e/eval c-state c-env `(def ~inject-as ~sym) {:ns ns})
      (swap! c-state update-in [:cljs.analyzer/namespaces ns :defs inject-as] merge (e/resolve-var c-state c-env sym)))))