(ns chia.db.core
  (:refer-clojure :exclude [peek]))

(defmacro peek
  "Evaluates body without tracking read patterns."
  [& body]
  `(binding [~'chia.db.core/*access-log* nil]
     (do ~@body)))

(defmacro get-in*
  "Compiled version of get-in, small performance boost over the `get-in` function."
  ([m ks]
   (if-not (vector? ks)
     `(clojure.core/get-in ~m ~ks)
     `(-> ~m
          ~@(for [k ks]
              (list 'clojure.core/get k)))))
  ([m ks not-found]
   (if-not (vector? ks)
     `(clojure.core/get-in ~m ~ks ~not-found)
     `(-> ~m
          ~@(for [k (drop-last ks)]
              (list 'clojure.core/get k))
          (clojure.core/get ~(last ks) ~not-found)))))

#_(defmacro db-log [& body]
    `(binding [~'chia.db.core/*db-log* (atom {})
               ~'chia.db.core/*prevent-notify* true]
       (let [value# (do ~@body)]
         {:tx-report @~'chia.db.core/*db-log*
          :value value#})))