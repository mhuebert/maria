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
            [maria.eval.repl :refer [*context*]]
            sci.impl.resolve
            sci.lang
            [sci.async :as a]
            [promesa.core :as p]
            [sci.configs.applied-science.js-interop :as sci.j]
            [sci.configs.funcool.promesa :as sci.p]
            [re-db.reactive]
            [re-db.macros :as r.macros])
  (:require-macros [maria.eval.sci :refer [require-namespaces]]))

(sci/alter-var-root sci/print-fn (constantly *print-fn*))

(defn await? [x]
  (and (instance? js/Promise x)
       (a/await? x)))

(defn guard [x f] (when (f x) x))

(defn eval-string
  ([source] (eval-string @*context* source))
  ([ctx source]
   (try
     (let [value (sci/eval-string* ctx source)
           ;; can use this to track ns;
           ;; then have to handle promise flattening and await detection.
           #_#_{value :val ns :ns} (a/eval-string+ ctx source)
           ]
       (if (await? value)
         (a/await
          (-> (p/let [value value]
                {:value value})
              (p/catch (fn [e] {:error e}))))
         {:value value}))
     (catch js/Error e {:error e}))))

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