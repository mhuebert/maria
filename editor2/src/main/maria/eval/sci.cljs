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
            maria.friendly.kinds
            [maria.eval.repl :refer [*context* promise? await?]]
            sci.impl.resolve
            sci.lang
            [sci.async :as a]
            [promesa.core :as p]
            [sci.configs.applied-science.js-interop :as sci.j]
            [sci.configs.funcool.promesa :as sci.p]
            [re-db.reactive])
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
                                                         (eval-next (a/await (a/eval-ns-form ctx form)))
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
  ([ctx s] (eval-string ctx s nil))
  ([ctx s opts]
   (let [last-ns (volatile! (or (:ns opts)
                                (:last-ns ctx)
                                @sci/ns))
         ctx (assoc ctx :last-ns last-ns)
         value (eval-string* ctx s)]
     (if (await? value)
       (a/await
        (p/let [value value]
          (let [ns @last-ns]
            (swap! *context* assoc :last-ns ns)
            {:value value :ns ns})))
       (let [ns @last-ns]
         (swap! *context* assoc :last-ns ns)
         {:value value :ns ns})))))

(sci/alter-var-root sci/print-fn (constantly *print-fn*))

(defn guard [x f] (when (f x) x))

(defn init-cells [sci-opts]
  (-> (require-namespaces sci-opts [cells.lib])
      (assoc-in [:namespaces 'cells.cell] (let [ns (sci/create-ns 'cells.cell)]
                                            (merge (sci/copy-ns cells.cell ns)
                                                   #_{'defcell (sci/copy-var cells.macros/defcell:impl ns)
                                                      'cell (sci/copy-var cells.macros/cell:impl ns)
                                                      'bound-fn (sci/copy-var cells.macros/bound-fn:impl ns)
                                                      'memoized-on (sci/copy-var cells.macros/memoized-on:impl ns)})))))


(def sci-opts
  (-> {:bindings {'prn prn
                  'println println}
       :classes {'js goog/global}
       :namespaces {'applied-science.js-interop sci.j/js-interop-namespace
                    'clojure.core {'require a/require}
                    'promesa.core sci.p/promesa-namespace
                    'promesa.protocols sci.p/promesa-protocols-namespace
                    'user {'println println
                           'prn prn
                           'pr pr}}}
      (init-cells)
      (require-namespaces '[[shapes.core :as shapes]
                            maria.friendly.kinds
                            maria.friendly.messages
                            maria.eval.repl
                            [sci.async :refer [await]]
                            [sci.impl.resolve :include [resolve-symbol]]
                            cells.cell
                            cells.lib
                            re-db.reactive])))

(reset! *context* (sci/init sci-opts))

(eval-string (resource/inline "user.cljs")) ;; sets up scope for repl