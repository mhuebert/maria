(ns maria.repl-specials
  "Special forms that exist only in the REPL."
  (:require [cljs-live.eval :as e :refer [defspecial]]
            [maria.views.repl-specials :as repl-ns]
            [maria.ns-utils :as ns-utils]))

(defspecial doc
  "Show doc for symbol"
  [c-state c-env name]
  (let [[namespace name] (let [n (e/resolve-symbol c-state c-env name)]
                           (map symbol [(namespace n) (clojure.core/name n)]))]
    {:value (repl-ns/doc (merge {:expanded?   true
                                 :standalone? true}
                                (or (some-> (get e/repl-specials name) (meta))
                                    (get-in (ns-utils/ns-map @c-state namespace) [:defs name]))))}))

(defspecial dir
  "Display public vars in namespace"
  [c-state c-env ns]
  (let [ns (or ns (:ns @c-env))]
    {:value (repl-ns/dir c-state ns)}))

