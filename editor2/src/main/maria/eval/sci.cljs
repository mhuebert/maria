(ns maria.eval.sci
  (:refer-clojure :exclude [eval])
  (:require [applied-science.js-interop :as j]
            ["@codemirror/view" :as view]
            [clojure.string :as str]
            [sci.core :as sci]
            [shadow.resource :as resource]
            cells.lib
            cells.cell
            cells.macros
            shapes.core
            [maria.eval.repl :refer [*context* promise? await?]]
            sci.impl.resolve
            sci.lang
            [sci.async :as a]
            [promesa.core :as p]
            [sci.configs.applied-science.js-interop :as sci.j]
            [sci.configs.funcool.promesa :as sci.p]
            [re-db.reactive]
            maria.friendly.kinds
            [sicmutils.env.sci :as sicm.sci])
  (:require-macros [maria.eval.sci :refer [require-namespaces]]))

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
                 'shapes 'shapes.core}
       :namespaces {'applied-science.js-interop sci.j/js-interop-namespace
                    'clojure.core {'require a/require}
                    'promesa.core sci.p/promesa-namespace
                    'promesa.protocols sci.p/promesa-protocols-namespace
                    'user {'println println
                           'prn prn
                           'pr pr}}}
      (require-namespaces '[applied-science.js-interop
                            promesa.core
                            shapes.core
                            maria.eval.repl
                            sci.async
                            cells.cell
                            cells.lib
                            re-db.reactive
                            maria.friendly.messages
                            maria.friendly.kinds])))
(defonce _
         (do
           (reset! *context* (-> (sci/init sci-opts)
                                 (intern-core 'maria.eval.repl/doc
                                              'maria.eval.repl/dir
                                              'maria.eval.repl/await
                                              'maria.eval.repl/html
                                              'maria.friendly.kinds/what-is)
                                 (sci/merge-opts sicm.sci/context-opts)))

           (p/->> (eval-string (resource/inline "user.cljs"))
                  (prn "Evaluated user.cljs"))))