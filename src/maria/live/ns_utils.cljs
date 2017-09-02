(ns maria.live.ns-utils
  (:require [maria.eval :as e]
            [clojure.string :as string]))

(defn builtin-ns? [s]
  (and (not= s 'maria.user)
       (re-find #"^(?:re-view|maria|cljs|re-db|clojure)" (name s))))

(defn analyzer-ns [c-state ns]
  (get-in c-state [:cljs.analyzer/namespaces ns]))

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
  [c-state c-env sym]
  (let [[namespace name] (resolve-sym c-state c-env sym)]
    (or (get-in @c-state [:cljs.analyzer/namespaces namespace :defs name])
        (get-in @c-state [:cljs.analyzer/namespaces (add-$macros-suffix namespace) :defs name]))))

(defn get-ns
  ([] (get-ns (:ns @e/c-env)))
  ([ns-name] (get-in @e/c-state [:cljs.analyzer/namespaces ns-name])))

(defn ns-defs*
  ([the-ns] (ns-defs* the-ns false))
  ([the-ns include-private?]
   (let [ns-name (str (:name the-ns))]
     (reduce (fn [names {:keys    [anonymous private]
                         the-name :name}]
               (cond-> names
                       (and (not anonymous)
                            (if include-private? true
                                                 (not private)))
                       (assoc the-name ns-name))) {} (vals (:defs the-ns))))))

(def core-publics (ns-defs* (get-ns 'cljs.core)))

(defn ns-aliases* [the-ns]
  (reduce (fn [names k]
            (merge names (get the-ns k))) {} [:rename-macros
                                              :renames
                                              :use-macros
                                              :imports
                                              :requires
                                              :uses]))

(defn ns-completions
  ([token] (ns-completions (:ns @e/c-env) token))
  ([ns-name token]
   (let [token (str token)]
     (sort (for [[completion namespace] (merge (ns-aliases* (get-ns ns-name))
                                               core-publics)
                 :when (string/starts-with? (str completion) token)]
             [completion namespace])))))