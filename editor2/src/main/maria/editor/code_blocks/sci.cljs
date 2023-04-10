(ns maria.editor.code-blocks.sci
  (:refer-clojure :exclude [eval])
  (:require-macros [maria.editor.code-blocks.sci])
  (:require ["@codemirror/view" :as view]
            [applied-science.js-interop]
            [cells.api]
            [cells.hooks]
            [cells.impl]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as str]
            [maria.editor.code-blocks.docs]
            [maria.editor.code-blocks.repl]
            [maria.editor.views]
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
            [shadow.lazy :as lazy]
            [shapes.core]
            [yawn.sci-config]))

;; Extension point for additional lazy-loaded libraries
(def lazy-libs {})

(defn lazy-load [{:keys [libname ctx]}]
  (when-let [loadable (lazy-libs (first (str/split (str libname) #"\.")))]
    (p/do (if false #_(lazy/ready? loadable)
            (@loadable ctx)
            (p/let [init (lazy/load loadable)]
              (init ctx)))
          {})))

(defn async-load-fn [{:as opts :keys [libname ctx]}]
  (or (lazy-load opts)
      (throw (js/Error. (str "Module not found for lib: " libname)))))


(defn await? [x] (and (instance? js/Promise x) (a/await? x)))

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
                                                                                     @last-ns)))
                                                           (eval-next (sci/eval-form ctx form)))
                                                         (eval-next (sci/eval-form ctx form)))))))]
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
         value (eval-string* (assoc ctx :last-ns last-ns) opts s)
         return (fn [v]
                  (when-not (:ns opts)
                    (vreset! (:last-ns ctx) @last-ns))
                  {:value v})]
     (if (await? value)
       (a/await
        (p/-> value return))
       (return value)))))

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
       :classes {#_#_'js goog/global
                 'js/Promise js/Promise
                 'js/TextEncoder js/TextEncoder
                 'Math js/Math}
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
      (maria.editor.code-blocks.sci/require-namespaces [shapes.core
                                                        maria.editor.code-blocks.repl
                                                        sci.core
                                                        sci.async
                                                        cells.hooks
                                                        cells.impl
                                                        cells.api
                                                        maria.editor.code-blocks.docs
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
      (intern-core 'clojure.core '[maria.editor.code-blocks.repl/doc
                                   maria.editor.code-blocks.repl/dir
                                   maria.editor.code-blocks.repl/await])
      (refer-all! '{cells.api user
                    maria.editor.code-blocks.repl user
                    shapes.core user})
      (assoc :last-ns (volatile! @sci/ns))))
