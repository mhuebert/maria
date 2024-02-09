(ns maria.editor.code.sci
  (:refer-clojure :exclude [eval])
  (:require-macros [maria.editor.code.sci])
  (:require ["@codemirror/view" :as view]
            [applied-science.js-interop]
            [cells.core]
            [cells.hooks]
            [cells.impl]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as str]
            [maria.editor.code.docs]
            [maria.editor.code.repl]
            [maria.editor.views]
            [promesa.core :as p]
            [re-db.reactive]
            [re-db.sci-config]
            [sci.async :as a]
            [sci.configs.applied-science.js-interop :as js-interop.sci]
            [sci.configs.funcool.promesa :as promesa.sci]
            [sci.core :as sci]
            [sci.ctx-store :as ctx]
            [sci.impl.namespaces :as sci.ns]
            [sci.impl.resolve]
            [sci.lang]
            [sci.pprint]
            [shapes.core]
            [yawn.sci-config]
            [maria.editor.extensions.config :as config]))

(defn async-load-fn [opts]
  (or (config/load-lib+ opts)
      (throw (js/Error. (str "Module not found for lib: " (:libname opts))))))

(defn await? [x] (and (instance? js/Promise x) (a/await? x)))

(defn eval-form-sync [ctx ns form]
  (ctx/with-ctx ctx
    (sci/binding [sci/ns (sci.ns/sci-the-ns ctx ns)]
                 (sci/eval-form ctx form))))

(defn eval-string*
  [{:as ctx :keys [last-ns]} opts s]
  (let [rdr (sci/reader s)
        eval-next (fn eval-next [res]
                    (let [continue #(ctx/with-ctx ctx
                                      (sci/binding [sci/ns @last-ns
                                                    sci/file (:clojure.core/eval-file opts)]
                                                   (let [form (sci/parse-next ctx rdr)]
                                                     (if (= :sci.core/eof form)
                                                       res
                                                       (if (seq? form)
                                                         (if (= 'ns (first form))
                                                           (eval-next (a/await (p/do (a/eval-ns-form ctx form)
                                                                                     {:form form
                                                                                      :value @last-ns})))
                                                           (eval-next {:form form
                                                                       :value (sci/eval-form ctx form)}))
                                                         (eval-next {:form form
                                                                     :value (sci/eval-form ctx form)}))))))]
                      (if (await? res)
                        (a/await (-> res
                                     (.then continue)
                                     (.catch (fn [e] e))))
                        (continue))))]
    (eval-next nil)))

(defn eval-string
  "Same as eval-string* but returns map with `:value`, the evaluation
  result, and `:ns`, the last active namespace. The return value can
  be passed back into `opts` to preserve the namespace state."
  ([ctx s] (eval-string ctx nil s))
  ([ctx opts s]
   (let [last-ns (if (:ns opts)
                   (volatile! (sci.ns/sci-the-ns ctx (:ns opts)))
                   (:last-ns ctx))
         result (eval-string* (assoc ctx :last-ns last-ns) opts s)
         return (fn [result]
                  (when-not (:ns opts)
                    (vreset! (:last-ns ctx) @last-ns))
                  result)]
     (if (await? result)
       (a/await
         (p/-> result return))
       (return result)))))

(sci/alter-var-root sci/print-fn (constantly *print-fn*))

(defn guard [x f] (when (f x) x))

(defn intern-core [ctx to syms]
  (let [ns (sci/find-ns ctx to)]
    (reduce (fn [ctx sym]
              (let [resolved (sci.impl.resolve/resolve-symbol ctx sym)]
                (doto ctx (sci/intern ns
                                      (with-meta
                                        (symbol (name sym))
                                        (meta resolved)) @resolved)))) ctx syms)))

(def re-db-reactive-ns (sci/create-ns 're-db.reactive nil))
(def re-db-reactive-namespace (sci/copy-ns re-db.reactive re-db-reactive-ns))

(def sci-opts
  (-> {:async-load-fn async-load-fn
       :bindings {'prn prn
                  'println println
                  'pprint pprint}
       :classes {'js goog/global :allow [js/TextEncoder js/Promise]}
       :aliases {'p 'promesa.core
                 'j 'applied-science.js-interop
                 'shapes 'shapes.core
                 'sci 'sci.core}
       :namespaces (merge {'clojure.core {'require a/require}
                           'user {'println println
                                  'prn prn
                                  'pr pr}}
                          js-interop.sci/namespaces
                          promesa.sci/namespaces
                          yawn.sci-config/namespaces
                          re-db.sci-config/namespaces)}
      (maria.editor.code.sci/require-namespaces [shapes.core
                                                 maria.editor.code.repl
                                                 sci.core
                                                 sci.async
                                                 cells.hooks
                                                 cells.impl
                                                 cells.core
                                                 maria.editor.code.docs
                                                 maria.editor.views])))

(defn refer-all! [{:as ctx :keys [env]} targets]
  (doseq [[from to] targets]
    (swap! env
           update-in
           [:namespaces to :refers]
           merge
           (sci.ns/sci-ns-publics ctx from)))
  ctx)

(defn initial-context []
  (-> (sci/init sci-opts)
      (intern-core 'clojure.core '[maria.editor.code.repl/doc
                                   maria.editor.code.repl/dir
                                   maria.editor.code.repl/await
                                   maria.editor.code.repl/what-is
                                   maria.editor.code.repl/html])
      (refer-all! '{cells.core user
                    maria.editor.code.repl user
                    shapes.core user})
      (sci/add-class! 'Math js/Math)
      (assoc :last-ns (volatile! @sci/ns)
             :!viewers (atom nil))))
