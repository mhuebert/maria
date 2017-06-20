(ns maria.repl-specials
  "Special forms that exist only in the REPL."
  (:require [cljs-live.eval :as e :refer [defspecial]]
            [maria.views.repl-specials :as special-views]
            [maria.ns-utils :as ns-utils]))

(defspecial dir
  "Display public vars in namespace"
  [c-state c-env ns]
  (let [ns (or ns (:ns @c-env))]
    {:value (special-views/dir c-state ns)}))

(defspecial what-is
  "Defers to maria.messages/what-is; this is only here to handle the edge case of repl-special functions."
  [c-state c-env thing]
  (let [macro (when (symbol? thing) (:macro (ns-utils/resolve-var c-state c-env thing)))]
    (e/eval-str c-state c-env (str `(maria.messages/what-is ~(cond macro :maria.kinds/macro
                                                                   (and (symbol? thing)
                                                                        (contains? e/repl-specials thing)) :maria.kinds/function
                                                                   :else thing))))))

(defspecial doc
  "Show documentation for given symbol"
  [c-state c-env name]
  (if (fn? name)
    {:value (special-views/doc (merge {:expanded?   true
                                       :standalone? true}
                                      (or (ns-utils/resolve-var c-state c-env name)
                                          (some-> (get e/repl-specials name) (meta)))))}
    ;; XXX ugly copy/paste because macros are weird in cljs
    (let [macro (when (symbol? name) (:macro (ns-utils/resolve-var c-state c-env name)))]
      (e/eval-str c-state c-env (str `(maria.messages/what-is ~(cond macro :maria.kinds/macro
                                                                     (and (symbol? name)
                                                                          (contains? e/repl-specials name)) :maria.kinds/function
                                                                     :else name)))))))

(defspecial inject
  "Inject vars into a namespace, preserving all metadata (inc. name)"
  [c-state c-env ns mappings]
  (let [ns (ns-utils/elide-quote ns)]
    (doseq [[inject-as sym] (seq (ns-utils/elide-quote mappings))]
      (e/eval c-state c-env `(def ~inject-as ~sym) {:ns ns})
      (swap! c-state update-in [:cljs.analyzer/namespaces ns :defs inject-as] merge (e/resolve-var c-state c-env sym)))))
