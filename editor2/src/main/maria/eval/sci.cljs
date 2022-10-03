(ns maria.eval.sci
  (:refer-clojure :exclude [eval])
  (:require [applied-science.js-interop :as j]
            ["@codemirror/view" :as view]
            [clojure.string :as str]
            [sci.core :as sci]
            [shadow.resource :as resource]
            shapes.core
            maria.friendly.kinds
            [maria.eval.repl :refer [*context*]]
            sci.impl.resolve
            sci.lang
            [sci.async :as a]
            [promesa.core :as p]
            [sci.configs.applied-science.js-interop :as sci.j]
            [sci.configs.funcool.promesa :as sci.p])
  (:require-macros [maria.eval.sci :refer [require-namespaces]]))

(defn flatten-var [x]
  (cond-> x (instance? sci.lang/Var x) deref))

(defn await? [x]
  (let [x (flatten-var x)]
    (and (instance? js/Promise x) (a/await? x))))

(defn pass-await [p0 p1]
  (cond-> p1
          (a/await? (flatten-var p0))
          a/await))

(defn eval-string
  ([source] (eval-string @*context* source))
  ([ctx source]
   (try
     (let [value (sci/eval-string* ctx (str/trim source))]
       (if (await? value)
         (-> (p/let [awaited-value (flatten-var value)]
               (if (instance? sci.lang/Var value)
                 (do (sci/alter-var-root value (constantly awaited-value))
                     {:value value})
                 {:value awaited-value}))
             (p/catch (fn [e] {:error e}))
             a/await)
         {:value value}))
     (catch js/Error e {:error e}))))

(def sci-opts
  {:bindings {}
   :namespaces (merge (require-namespaces 'shapes.core
                                          'maria.friendly.kinds
                                          'maria.friendly.messages
                                          'maria.eval.repl
                                          'sci.async
                                          '[sci.impl.resolve :include [resolve-symbol]]
                                          )
                      {'applied-science.js-interop sci.j/js-interop-namespace
                       'promesa.core sci.p/promesa-namespace
                       'promesa.protocols sci.p/promesa-protocols-namespace
                       'user {'println println
                              'prn prn
                              'pr pr}})})

(reset! *context* (sci/init sci-opts))

(eval-string (resource/inline "user.cljs")) ;; sets up scope for repl