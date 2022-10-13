(ns maria.eval.sci)

(defn dequote [x]
  (if (and (seq? x) (= 'quote (first x)))
    (second x)
    x))

(defmacro require-namespaces [sci-opts namespaces]
  (reduce (fn [sci-opts ns]
            (let [the-ns (dequote ns)
                  [the-ns opts] (if (symbol? the-ns)
                              [the-ns {}]
                              [(first the-ns) (apply hash-map (rest the-ns))])
                  ns-map (if (:only opts)
                           (let [ns-sym (gensym "ns")]
                             `(let [~ns-sym (~'sci.core/create-ns '~the-ns)]
                                ~(reduce (fn [out sym]
                                           (assoc out `'~sym `(~'sci.core/copy-var ~(symbol (str the-ns) (str sym)) ~ns-sym)))
                                         {}
                                         (:only opts))))
                           `(~'sci.core/copy-ns ~the-ns (~'sci.core/create-ns '~the-ns) ~opts))
                  as (:as opts)]
              `(-> ~sci-opts
                   (assoc-in [:namespaces '~the-ns] ~ns-map)
                   ~@(when as
                       `[(assoc-in [:aliases '~the-ns] '~as)]))))
          sci-opts
          (dequote namespaces)))

(comment
 (require-namespaces {}
                     '[[cells.lib :as y]
                       shapes.core])
 (macroexpand '(require-namespaces {}
                                   '[shapes.core])))