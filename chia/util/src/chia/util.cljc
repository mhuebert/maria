(ns chia.util
  (:require [chia.util.macros :as m]
            [chia.util.string :as string]
            [clojure.string :as str]
            #?(:cljs [applied-science.js-interop :as j]))
  #?(:cljs (:require-macros [chia.util])))

(def some-str string/some-str)
(def ensure-prefix string/ensure-prefix)
(def strip-prefix string/trim-prefix)

(defn guard [x f]
  (when (f x)
    x))

(defn guard->> [f x]
  (when (f x)
    x))

(defn nilable [pred]
  (fn [x]
    (or (nil? x) (pred x))))

;; from https://github.com/clojure/core.incubator/blob/master/src/main/clojure/clojure/core/incubator.clj
(defn dissoc-in
  "Dissociates an entry from a nested associative structure returning a new
  nested structure. keys is a sequence of keys. Any empty maps that result
  will not be present in the new structure."
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (dissoc m k)))

;; modified from https://github.com/clojure/core.incubator/blob/master/src/main/clojure/clojure/core/incubator.clj
(defn disj-in
  "Dis[join]'s `value` from set at `path` returning a new nested structure.
   The set, if empty, and any empty maps that result, will not be present in the new structure."
  [m [k & ks :as path] value]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (disj-in nextmap ks value)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (let [new-set (disj (get m k) value)]
      (if (empty? new-set)
        (dissoc m k)
        (assoc m k new-set)))))

(defn update-map [m key-f val-f]
  (persistent!
   (reduce-kv (fn [m k v] (assoc! m (key-f k) (val-f v))) (transient (empty m)) m)))

(defn update-keys [m f]
  (update-map m f identity))

(defn update-vals [m f]
  (update-map m identity f))

(defn update-some-keys
  [m ks f]
  (reduce (fn update-k [m k]
            (cond-> m
                    (contains? m k) (assoc k (f (get m k))))) m ks))

(defn update-some [m updaters]
  (reduce-kv (fn [m k update-f]
               (cond-> m
                       (contains? m k) (update k update-f))) m updaters))

(defn first-when [pred coll]
  (reduce (fn [_ x]
            (when (pred x) (reduced x))) nil coll))

(defn last-while [pred coll]
  (reduce (fn [found x]
            (if (pred x) x (reduced found))) nil coll))

(defn apply-if-fn [f & args]
  (if (fn? f)
    (apply f args)
    f))

(m/defmacro for-map [& body]
  `(->> (for ~@body)
        (apply merge)))

(m/defmacro doto->>
  "Like `doto`, but threads the value of `x` through the end of each expression."
  {:added "1.0"}
  [x & forms]
  (let [gx (gensym)]
    `(let [~gx ~x]
       ~@(map (fn [f]
                (with-meta
                 (if (seq? f)
                   `(~(first f) ~@(next f) ~gx)
                   `(~f ~gx))
                 (meta f)))
              forms)
       ~gx)))

(m/macro-time
 (defn munged-key [k]
   (munge (str (when (keyword? k)
                 (some-> (namespace k)
                         (str "__")))
               (name k)))))

(defn memoize-by
  "Like memoize, but uses `key-f` to compute the memoization key from passed-in args."
  [f key-f]
  (let [mem (atom {})
        lookup-sentinel #?(:cljs #js{} :clj ::not-found)]
    (fn [& args]
      (let [args-key (key-f args)]
        #?(:clj  (if-let [e (find @mem args-key)]
                   (val e)
                   (let [ret (apply f args)]
                     (swap! mem assoc args-key ret)
                     ret))
           :cljs (let [v (get @mem args-key lookup-sentinel)]
                   (if (identical? v lookup-sentinel)
                     (let [ret (apply f args)]
                       (swap! mem assoc args-key ret)
                       ret)
                     v)))))))

(m/defmacro memoized-on [o k & body]
  `(or (~'applied-science.js-interop/get ~o ~k)
       (doto->> (do ~@body)
                (~'applied-science.js-interop/assoc! ~o ~k))))

(defn user-bindings
  "Returns all user-assigned bindings resulting from a let binding."
  [let-bindings]
  (let [;; set of symbols that will be bound via clojure.core/destructure
        bound-sym? (m/case :clj (->> (clojure.core/destructure let-bindings) ;; not selfhost-compatible
                                     (partition 2)
                                     (map first)
                                     (set))
                           :cljs symbol?)
        ;; all keywords & symbols that appear in the user destructuring side
        user-syms-keywords (->> (partition 2 let-bindings)
                                (map first)
                                (tree-seq coll? seq)
                                (group-by #(cond (symbol? %) :symbols
                                                 (keyword? %) :keywords
                                                 :else nil))
                                (filter #(or (symbol? %)
                                             (keyword? %)))
                                (distinct))]
    (->> user-syms-keywords
         ;; only keep symbols/keywords which correspond to bound names
         ;; (ie. ignore generated symbols)
         (filter (fn [x]
                   (-> (name x)
                       (symbol)
                       (bound-sym?)))))))

(comment
 (= (user-bindings '[a 4
                     {:as   m
                      :keys [b :c ::d x/e]
                      [f]   :n
                      g     :o} {}])
    '[a m b :c ::d x/e f g]))

(defmacro log-let [bindings & body]
  (let [{:keys [file line]} (meta bindings)]
    `(let ~bindings
       (~'js/console.groupCollapsed ~(str file "#" line))
       ~@(for [user-binding (user-bindings bindings)]
           `(~'js/console.log (quote ~user-binding) ~(symbol (name user-binding))))
       (~'js/console.groupEnd)
       ~@body)))

(defmacro log-sym [sym]
  `(do (~'js/console.log ~(str sym ":") ~sym)))

(defmacro as-promise [expr]
  `(~'js/Promise.
    (fn [resolve# reject#]
      (~@expr (fn [err# result#]
                (if err# (reject# err#)
                         (resolve# result#)))))))

(defn promise? [x]
  #?(:cljs (= (js/Promise.resolve x) x)
     :clj  false))

(defn simplify-keyword [k]
  (keyword (name k)))

(defn merge-maps [x y]
  (if (and (map? y)
           (or (map? x)
               (nil? x)))
    (merge x y)
    y))

(defn deep-merge-maps [& ms]
  (apply merge-with merge-maps ms))

(defn update-first [coll pred update-f]
  (let [end (count coll)]
    (loop [i 0]
      (cond (>= i end) (do (prn [:update-first/not-found pred])
                           coll)

            (pred (nth coll i))
            (assoc coll i (update-f (nth coll i)))

            :else
            (recur (inc i))))))

(defn dissoc-ns [m ns-key]
  (let [ns (name ns-key)]
    (->> (keys m)
         (reduce (fn [m k]
                   (cond-> m
                           (= ns (namespace k)) (dissoc k))) m))))

(defn group-ns [m & {:keys [lift?]}]
  (reduce-kv (fn [m k v]
               (let [path (if (keyword? k)
                            (if-let [ns (namespace k)]
                              [ns (if lift?
                                    (keyword (name k))
                                    k)]
                              [:_ k])
                            [:_ k])]
                 (assoc-in m path v))) {} m))

(defn select-ns
  [m ns-key & {:keys [lift?]}]
  (let [ns (case ns-key
             :_ nil
             ns-key)]
    (->> m
         (reduce-kv (fn [m k v]
                      (cond-> m
                              (and (keyword? k)
                                   (= ns (namespace k))) (assoc (if lift?
                                                                  (keyword (name k))
                                                                  k) v))) {}))))

(defn lift-nses [m nses]
  (reduce-kv (fn [m k v]
               (if (and (keyword? k)
                        (contains? nses (namespace k)))
                 (-> m
                     (dissoc k)
                     (assoc (keyword (name k)) v))
                 m)) m m))

(defn memoize-str
  [f]
  (let [mem #?(:cljs #js{} :clj (atom {}))]
    (fn [x]
      #?(:cljs (j/get mem x
                      (let [v (f x)]
                        (unchecked-set mem x v)
                        v))
         :clj  (let [v (get @mem x ::not-found)]
                 (if (identical? v ::not-found)
                   (let [ret (f x)]
                     (swap! mem assoc x ret)
                     ret)
                   v))))))

(defn camel-case* [s]
  (str/replace s #"-(.)" (fn [[_ s]] (str/upper-case s))))

(def camel-case (memoize-str camel-case*))

(defn count-by [f s]
  (reduce (fn [acc x]
            (if #?(:cljs ^boolean (f x)
                   :clj  (f x)) (inc acc) acc)) 0 s))

