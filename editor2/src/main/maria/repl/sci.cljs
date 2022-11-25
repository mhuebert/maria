(ns maria.repl.sci
  (:refer-clojure :exclude [eval])
  (:require-macros [maria.repl.sci])
  (:require [applied-science.js-interop]
            [cells.api]
            [cells.hooks]
            [cells.impl]
            [clojure.string :as str]
            [maria.helpful]
            [maria.repl.api]
            ["@codemirror/view" :as view]
            [maria.ui]
            [maria.ui]
            [promesa.core :as p]
            [re-db.reactive]
            [sci.async :as a]
            [sci.configs.applied-science.js-interop :as js-interop.sci]
            [sci.configs.funcool.promesa :as promesa.sci]
            [sci.core :as sci]
            [sci.impl.namespaces :as sci.ns]
            [sci.impl.resolve]
            [sci.lang]
            [shapes.core]
            [yawn.sci-config :as yawn.sci]
            [sci.ctx-store :as ctx]))

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
                                     (.catch (fn [e] {:error e}))))
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

(defn load-fn [{:keys [namespace]}]
  (when (str/starts-with? (str namespace)
                          "sicmutils.")
    {:file (str namespace ".cljs")
     :source ":source"}
    ))

(def sci-opts
  (-> {:bindings {'prn prn
                  'println println}
       :classes {'js goog/global
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
                          yawn.sci/namespaces)}
      (maria.repl.sci/require-namespaces [shapes.core
                                          maria.repl.api
                                          sci.core
                                          sci.async
                                          cells.hooks
                                          cells.impl
                                          cells.api
                                          re-db.reactive
                                          maria.helpful
                                          maria.ui])))

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
      (intern-core 'clojure.core '[maria.repl.api/doc
                                   maria.repl.api/dir
                                   maria.repl.api/await])
      (refer-all! '{cells.api user
                    maria.repl.api user
                    shapes.core user})
      (assoc :last-ns (volatile! @sci/ns))))
