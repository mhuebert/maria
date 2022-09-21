(ns maria.sci)

(defmacro require-namespaces [& namespaces]
  (->> namespaces
       (reduce (fn [m ns]
                 (let [[ns opts] (if (symbol? ns)
                                   [ns {}]
                                   [(first ns) (apply hash-map (rest ns))])]
                   (assoc m `'~ns `(~'sci.core/copy-ns ~ns (~'sci.core/create-ns '~ns) ~opts)))) {})))