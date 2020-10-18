(ns chia.db.patterns
  (:require [chia.reactive :as r]
            [clojure.set :as set]))

(def ^:private empty-patterns
  "Map for building sets of patterns."
  {:e__ #{}                                                 ;; <entity id>
   :_a_ #{}                                                 ;; <attribute>
   :_av #{}                                                 ;; [<attribute>, <value>]
   :ea_ #{}})                                               ;; [<entity id>, <attribute>]

(defn added-patterns [prev-p next-p]
  (cond->> next-p
           (some? prev-p) (reduce-kv (fn [patterns k next-v]
                                       (if-let [prev-v (get prev-p k)]
                                         (assoc patterns k (set/difference next-v prev-v))
                                         patterns)) next-p)))

(defn removed-patterns [prev-p next-p]
  (cond->> prev-p
           (some? next-p)
           (reduce-kv (fn [patterns k prev-v]
                        (if-let [next-v (get next-p k)]
                          (assoc patterns k (set/difference prev-v next-v))
                          patterns)) prev-p)))

(def conj-set (fnil conj #{}))
(def into-set (fnil into #{}))

(defn log-read
  "Record pattern to *pattern-log*."
  ([db kind pattern]
   (log-read db kind pattern false))
  ([db kind pattern multiple?]
   (r/observe-pattern! db
                       update
                       kind
                       (if multiple? into-set conj-set)
                       pattern)))

(defn- add-listener
  "Associates listener with pattern in value-map."
  [value-map pattern-key pattern value]
  (update-in value-map [pattern-key pattern] conj-set value))

(defn- remove-listener
  "Removes listener associated with pattern in value-map."
  [value-map pattern-key pattern value]
  (update-in value-map [pattern-key pattern] disj value))

(declare listen unlisten)

(defn- resolve-id
  "Copied from chia.db.core."
  ([db db-snap attr val]
   (log-read db :_av [attr val])
   (first (get-in db-snap [:ave attr val]))))

(defn- lookup-ref?
  "Returns true if pattern has lookup ref in id position."
  [kind pattern]
  (or (and (keyword-identical? :e__ kind)
           (vector? pattern)
           pattern)
      (and (keyword-identical? :ea_ kind)
           (vector? (first pattern))
           (first pattern))))

(defn listen
  "Adds pattern listener."
  [db patterns listener]
  (swap! db assoc :listeners
         (reduce-kv (fn [listeners kind patterns]
                      (reduce (fn [listeners pattern]
                                (add-listener listeners kind pattern listener)) listeners patterns))
                    (get @db :listeners)
                    patterns)))

(defn unlisten
  "Removes pattern listener."
  [db patterns listener]
  (swap! db assoc :listeners
         (reduce-kv (fn [listeners kind patterns]
                      (reduce (fn [listeners pattern]
                                (remove-listener listeners kind pattern listener)) listeners patterns)) (get @db :listeners) patterns)))

(defn- non-empty-keys
  "Returns list of keys for which map contains a non-empty value."
  [m]
  (reduce-kv (fn [ks k v]
               (cond-> ks
                       (not (empty? v)) (conj k))) #{} m))

(defn- datom-patterns
  "Returns a map of patterns matched by a list of datoms.
  Limits patterns to those listed in pattern-keys.
  many? should return true for attributes which have schema value :db.cardinality/many?."
  ([datoms many?]
   (datom-patterns datoms many? [:e__ :ea_ :_av :_a_]))
  ([datoms many? pattern-keys]
   (->> datoms
        (reduce (fn [pattern-map [e a v pv]]
                  (cond-> pattern-map
                          (contains? pattern-keys :e__) (update :e__ conj e)
                          (contains? pattern-keys :ea_) (update :ea_ conj [e a])
                          (contains? pattern-keys :_av) (update :_av into (if (many? a)
                                                                            (reduce
                                                                             (fn [patterns v] (conj patterns [a v])) [] (into v pv))
                                                                            [[a v]
                                                                             [a pv]]))
                          (contains? pattern-keys :_a_) (update :_a_ conj a)))
                (select-keys empty-patterns pattern-keys)))))

(defn pattern-listeners
  "Returns values associated with patterns.

  value-map is of form {<pattern-key> {<pattern> #{...set of values...}}}.
  pattern-map is of form {<pattern-key> #{...set of patterns...}}"
  [patterns listeners]
  (reduce-kv (fn [out pattern-key patterns]
               (reduce (fn [out pattern]
                         (into out (get-in listeners [pattern-key pattern]))) out patterns)) #{} patterns))

(defn datom-values
  "Returns the set of values in value-map associated with patterns matched by datoms."
  [value-map datoms many?]
  (let [active-keys (non-empty-keys value-map)]
    (-> (datom-patterns datoms many? active-keys)
        (pattern-listeners value-map))))

(comment
 (assert (= (datom-patterns [["e" "a" "v" "prev-v"]]
                            #{}
                            (keys empty-patterns))
            {:e__ #{"e"}
             :ea_ #{["e" "a"]}
             :_av #{["e" "v"] ["e" "prev-v"]}
             :_a_ #{"a"}})))
