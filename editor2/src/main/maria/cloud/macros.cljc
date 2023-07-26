(ns maria.cloud.macros
  (:require [shadow.lazy #?(:clj :as-alias :cljs :as) lazy]
            #?(:clj [cljs.analyzer :as ana]))
  #?(:cljs (:require-macros maria.cloud.macros)))

#?(:clj
   (defmacro parse-views
     ;; wraps ::view keys with lazy/loadable (and resolves aliases, with :as-alias support)
     [m]
     (when (:ns &env)
       (let [the-ns (ana/get-namespace (ns-name *ns*))
             aliases (apply merge
                            ((juxt :requires :require-macros :as-aliases :reader-aliases)
                             the-ns))
             resolve-sym (fn [quoted-sym]
                           (let [sym (second quoted-sym)]
                             (if-let [resolved (get aliases (symbol (namespace sym)))]
                               (symbol (str resolved) (name sym))
                               sym)))
             m (update-keys m resolve-sym)]
         (reduce-kv (fn [m k v]
                      (assoc m
                        `'~k
                        `(delay ~(assoc v :view `(shadow.lazy/loadable ~k)))))
                    {}
                    m)))))

(defmacro sci-macro [name & body]
  (if (:ns &env)
    (let [[doc body] (if (string? (first body))
                       [(first body) (rest body)]
                       [nil body])
          [options body] (if (map? (first body))
                           [(first body) (rest body)]
                           [nil body])
          arities (if (vector? (first body)) (list body) body)
          arities (map (fn [[argv & body]] (list* (into '[&form &env] argv) body)) arities)]
      `(defn ~(vary-meta name assoc :sci/macro true)
         ~@(when doc [doc])
         ~@(when options [options])
         ~@arities))
    `(~'clojure.core/defmacro ~name ~@body)))