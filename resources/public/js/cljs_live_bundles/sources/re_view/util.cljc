(ns re-view.util
  (:require [clojure.string :as string]))

(defn camelCase
  "Return camelCased string, eg. hello-there to helloThere. Does not modify existing case."
  [s]
  (string/replace (name s) #"-(.)" (fn [[_ match]] (string/upper-case match))))


(defn update-attrs [el f & args]
  (if-not (vector? el)
    el
    (let [attrs? (map? (second el))]
      (into [(el 0) (apply f (if attrs? (el 1) {}) args)]
            (subvec el (if attrs? 2 1))))))

(defn ensure-keys [forms]
  (let [seen #{}]
    (map-indexed #(update-attrs %2 update :key (fn [k]
                                                       (if (or (nil? k) (contains? seen k))
                                                         %1
                                                         (do (swap! seen conj k)
                                                             k)))) forms)))

(defn map-with-keys [& args]
  (ensure-keys (apply clojure.core/map args)))

(defn any-pred
  "Evaluate fns sequentially, stopping if any return true."
  [& fns]
  (fn [this]
    (loop [fns fns]
      (if (empty? fns)
        false
        (or ((first fns) this)
            (recur (rest fns)))))))

#?(:cljs (defn is-react-element? [x]
           (and x
                (or (boolean (aget x "re$view"))
                    (.isValidElement js/React x)))))

(defn flatten-seqs
  "Flatten collection, only unwrap sequences"
  [children]
  (filter #(not (seq? %))
          (rest (tree-seq seq? seq children))))