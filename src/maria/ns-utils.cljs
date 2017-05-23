(ns maria.ns-utils
  (:require [maria.eval :as eval]))


(defn builtin-ns? [s]
  (and (not= s 'maria.user)
       (re-find #"^(?:re-view|maria|cljs|re-db|clojure)" (name s))))

(defn ns-map [ns]
  (get-in @eval/c-state [:cljs.analyzer/namespaces ns]))

(defn usable-names [ns]
  (->> (ns-map ns)
       (dissoc :defs)
       (vals)
       (filter map?)
       (map #(dissoc % :order :seen))
       (apply merge)))

(defn user-namespaces []
  (->> (keys (:cljs.analyzer/namespaces @eval/c-state))
       (filter (complement builtin-ns?))))