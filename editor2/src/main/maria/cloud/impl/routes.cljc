(ns maria.cloud.impl.routes
  (:require [bidi.bidi :as bidi]
            [clojure.walk :as walk]
            [shadow.lazy :as lazy]
            #?(:clj [cljs.analyzer :as ana]))
  #?(:cljs (:require-macros maria.cloud.impl.routes)))

#?(:cljs
   (extend-protocol bidi/Matched
     lazy/Loadable
     (resolve-handler [this m] (bidi/succeed this m))
     (unresolve-handler [this m] (when (= this (:handler m)) ""))
     Delay
     (resolve-handler [this m] (bidi/succeed this m))
     (unresolve-handler [this m] (when (= this (:handler m)) ""))))

#?(:clj
   (defn resolve-syms
     "Fix for https://clojure.atlassian.net/browse/CLJS-3399"
     [x]
     (let [the-ns (ana/get-namespace (ns-name *ns*))
           aliases (apply merge
                          ((juxt :requires :require-macros :as-aliases :reader-aliases)
                           the-ns))
           resolve-sym (fn [sym]
                         (if-let [resolved (and (qualified-symbol? sym)
                                                (get aliases (symbol (namespace sym))))]
                           (symbol (str resolved) (name sym))
                           sym))]
       (walk/postwalk resolve-sym x))))

#?(:clj
   (defmacro resolve-views
     "Replaces quoted qualified symbols with a bidi-tagged <shadow.lazy/loadable>."
     [m]
     (if (:ns &env)
       (walk/postwalk (fn [x]
                        (if (and (list? x)
                                 (= 'quote (first x))
                                 (qualified-symbol? (second x)))
                          `(~'bidi.bidi/tag (~'shadow.lazy/loadable ~(second x)) ~x)
                          x))
                      (resolve-syms m))
       m)))