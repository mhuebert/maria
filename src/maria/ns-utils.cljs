(ns maria.ns-utils)


(defn builtin-ns? [s]
  (and (not= s 'maria.user)
       (re-find #"^(?:re-view|maria|cljs|re-db|clojure)" (name s))))

(defn ns-map [c-state ns]
  (get-in c-state [:cljs.analyzer/namespaces ns]))

(defn usable-names [c-state ns]
  (->> (ns-map c-state ns)
       (dissoc :defs)
       (vals)
       (filter map?)
       (map #(dissoc % :order :seen))
       (apply merge)))

(defn user-namespaces [c-state]
  (->> (keys (:cljs.analyzer/namespaces c-state))
       (filter (complement builtin-ns?))))