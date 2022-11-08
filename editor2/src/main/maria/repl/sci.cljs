(ns maria.repl.sci
  (:refer-clojure :exclude [eval])
  (:require [applied-science.js-interop :as j]
            ["@codemirror/view" :as view]
            [clojure.string :as str]
            [sci.core :as sci]
            [shadow.resource :as resource]
            cells.api
            shapes.core
            [maria.repl.api :refer [*context* promise? await?]]
            sci.impl.resolve
            sci.lang
            [sci.async :as a]
            [promesa.core :as p]
            [sci.configs.applied-science.js-interop :as js-interop.sci]
            [sci.configs.funcool.promesa :as promesa.sci]
            [yawn.sci-config :as yawn.sci]
            [re-db.reactive]
            maria.friendly.kinds
            [sicmutils.env.sci :as sicm.sci])
  (:require-macros [maria.repl.sci :refer [require-namespaces]]))

(defn eval-string*
  [ctx s]
  (let [rdr (sci/reader s)
        last-ns (or (:last-ns ctx) (volatile! @sci/ns))
        ctx (assoc ctx :last-ns last-ns)
        eval-next (fn eval-next [res]
                    (let [continue #(sci/binding [sci/ns @last-ns]
                                                 (let [form (sci/parse-next ctx rdr)]
                                                   (if (= :sci.core/eof form)
                                                     res
                                                     (if (seq? form)
                                                       (if (= 'ns (first form))
                                                         (eval-next (a/await (p/do (a/eval-ns-form ctx form)
                                                                                   @last-ns)))
                                                         (eval-next (sci/eval-form ctx form)))
                                                       (eval-next (sci/eval-form ctx form))))))]
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
  ([s] (eval-string @*context* s))
  ([ctx s] (eval-string ctx nil s))
  ([ctx opts s]
   (let [last-ns (volatile! (or (:ns opts)
                                (:last-ns ctx)
                                @sci/ns))
         ctx (assoc ctx :last-ns last-ns)
         value (eval-string* ctx s)
         return (fn [v]
                  (swap! *context* assoc :last-ns @last-ns)
                  {:value v})]
     (if (await? value)
       (a/await
        (p/-> value return))
       (return value)))))

(sci/alter-var-root sci/print-fn (constantly *print-fn*))

(defn guard [x f] (when (f x) x))

(defn intern-core [ctx & syms]
  (let [ns (sci/find-ns ctx 'clojure.core)]
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
      (require-namespaces '[shapes.core
                            maria.repl.api
                            sci.core
                            sci.async
                            cells.hooks
                            cells.impl
                            cells.api
                            re-db.reactive
                            maria.friendly.messages
                            maria.friendly.kinds
                            maria.helpful
                            maria.ui])))
(defonce _
         (do
           (reset! *context* (-> (sci/init sci-opts)
                                 (intern-core 'maria.repl.api/doc
                                              'maria.repl.api/dir
                                              'maria.repl.api/await)
                                 (sci/merge-opts sicm.sci/context-opts)))

           (p/->> (eval-string (resource/inline "user.cljs"))
                  (prn "Evaluated user.cljs"))))