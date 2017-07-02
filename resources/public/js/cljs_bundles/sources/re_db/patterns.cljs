(ns re-db.patterns)

(def ^:dynamic *pattern-log*
  "Dynamic var used in conjunction with re-db.patterns/capture-patterns macro to
  identify patterns read by a block of code."
  nil)

(def ^:private empty-pattern-map
  "Map for building sets of patterns."
  {:e__ #{}                                                 ;; <entity id>
   :_a_ #{}                                                 ;; <attribute>
   :_av #{}                                                 ;; [<attribute>, <value>]
   :ea_ #{}})                                               ;; [<entity id>, <attribute>]

(def supported-pattern-keys (set (keys empty-pattern-map)))

(def conj-set (fnil conj #{}))
(def into-set (fnil into #{}))

(defn log-read
  "Record pattern to *pattern-log*."
  ([kind pattern]
   (when-not (nil? *pattern-log*)
     (set! *pattern-log* (update *pattern-log* kind conj-set pattern))))
  ([kind pattern multiple?]
   (when-not (nil? *pattern-log*)
     (set! *pattern-log* (update *pattern-log* kind (if multiple? into-set conj-set) pattern)))))

(defn add-value
  "Associates value with pattern in value-map."
  [value-map pattern-key pattern value]
  (update-in value-map [pattern-key pattern] conj-set value))

(defn remove-value
  "Removes value associated with pattern in value-map."
  [value-map pattern-key pattern value]
  (update-in value-map [pattern-key pattern] disj value))

(declare listen unlisten)

(defn resolve-id
  "Copied from re-db.core."
  [db-snap attr val]
  (log-read :_av [attr val])
  (first (get-in db-snap [:ave attr val])))

(defn listen-lookup-ref
  "Adds lookup ref listener, which uses an intermediate listener to update when
  the target of a lookup ref changes."
  [[lookup-attr lookup-val :as lookup-ref] kind pattern listeners db f]
  (let [lookup-target (atom (resolve-id @db lookup-attr lookup-val))
        lookup-cb (fn [{:keys [db-after] :as tx-report}]
                    (let [next-lookup-target (resolve-id db-after lookup-attr lookup-val)]
                      (when @lookup-target
                        (unlisten db {kind (case kind :e__ [@lookup-target]
                                                      :ea_ [[lookup-target (second pattern)]])} f))
                      (when-not (nil? next-lookup-target)
                        (listen db {kind (case kind :e__ [next-lookup-target]
                                                    :ea_ [[next-lookup-target (second pattern)]])} f))
                      (reset! lookup-target next-lookup-target)
                      (f tx-report)))]
    (-> (cond-> listeners
                (not (nil? @lookup-target)) (add-value :e__ @lookup-target f))
        (add-value :_av lookup-ref lookup-cb)
        (assoc-in [:lookup-refs [pattern f]] {:lookup-cb     lookup-cb
                                              :lookup-target lookup-target}))))

(defn unlisten-lookup-ref
  "Removes lookup ref listener."
  [lookup-ref kind pattern listeners db f]
  (let [{:keys [lookup-cb lookup-target]} (get-in listeners [:lookup-refs [pattern f]])]
    (-> (cond-> listeners
                @lookup-target (remove-value kind pattern f))
        (remove-value :_av lookup-ref lookup-cb)
        (dissoc [:lookup-refs [pattern f]]))))

(defn lookup-ref?
  "Returns true if pattern has lookup ref in id position."
  [kind pattern]
  (or (and (keyword-identical? kind :e__)
           (vector? pattern)
           pattern)
      (and (keyword-identical? kind :ea_)
           (vector? (first pattern))
           (first pattern))))

(defn listen
  "Adds pattern listener."
  [db patterns value]
  (swap! db assoc :listeners
         (reduce-kv (fn [listeners kind patterns]
                      (reduce (fn [listeners pattern]
                                (if-let [lookup-ref (lookup-ref? kind pattern)]
                                  (listen-lookup-ref lookup-ref kind pattern listeners db value)
                                  (add-value listeners kind pattern value))) listeners patterns)) (get @db :listeners) patterns)))

(defn unlisten
  "Removes pattern listener."
  [db patterns value]
  (swap! db assoc :listeners
         (reduce-kv (fn [listeners kind patterns]
                      (reduce (fn [listeners pattern]
                                (if-let [lookup-ref (lookup-ref? kind pattern)]
                                  (unlisten-lookup-ref lookup-ref kind pattern listeners db value)
                                  (remove-value listeners kind pattern value))) listeners patterns)) (get @db :listeners) patterns)))

(defn non-empty-keys
  "Returns list of keys for which map contains a non-empty value."
  [m]
  (reduce-kv (fn [ks k v]
               (cond-> ks
                       (not (empty? v)) (conj k))) #{} m))

(defn datom-patterns
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
                (select-keys empty-pattern-map pattern-keys)))))

(defn pattern-values
  "Returns values associated with patterns.

  value-map is of form {<pattern-key> {<pattern> #{...set of values...}}}.
  pattern-map is of form {<pattern-key> #{...set of patterns...}}"
  [pattern-map value-map]
  (reduce-kv (fn [values pattern-key patterns]
               (reduce (fn [values pattern]
                         (into values (get-in value-map [pattern-key pattern]))) values patterns)) #{} pattern-map))

(defn datom-values
  "Returns the set of values in value-map associated with patterns matched by datoms."
  [value-map datoms many?]
  (let [active-keys (non-empty-keys value-map)]
    (-> (datom-patterns datoms many? active-keys)
        (pattern-values value-map))))


(comment
  (assert (= (datom-patterns [["e" "a" "v" "prev-v"]]
                             #{}
                             supported-pattern-keys)
             {:e__ #{"e"}
              :ea_ #{["e" "a"]}
              :_av #{["e" "v"] ["e" "prev-v"]}
              :_a_ #{"a"}})))