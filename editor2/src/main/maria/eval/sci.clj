(ns maria.eval.sci)

(defn dequote [x]
  (if (and (list? x) (= 'quote (first x)))
    (second x)
    x))

(defmacro require-namespaces [& namespaces]
  (->> namespaces
       (reduce (fn [m ns]
                 (let [ns (dequote ns)
                       [ns opts] (if (symbol? ns)
                                   [ns {}]
                                   [(first ns) (apply hash-map (rest ns))])]
                   (if (:include opts)
                     (let [ns-sym (gensym "ns")]
                       (assoc m `'~ns `(let [~ns-sym (~'sci.core/create-ns '~ns)]
                                         ~(reduce (fn [out sym]

                                                    (assoc out `'~sym `(~'sci.core/copy-var ~(symbol (str ns) (str sym)) ~ns-sym)))
                                                  {}
                                                  (:include opts)))))
                     (assoc m `'~ns `(~'sci.core/copy-ns ~ns (~'sci.core/create-ns '~ns) ~opts))))) {})))