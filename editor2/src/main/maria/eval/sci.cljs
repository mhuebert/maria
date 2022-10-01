(ns maria.eval.sci
  (:refer-clojure :exclude [eval])
  (:require ["@codemirror/view" :as view]
            [clojure.string :as str]
            [sci.core :as sci]
            [shadow.resource :as resource]
            shapes.core
            maria.friendly.kinds
            [maria.eval.repl :refer [*context*]]
            sci.impl.resolve
            [promesa.core :as p])
  (:require-macros [maria.eval.sci :refer [require-namespaces]]))

(defn eval-string
  ([source] (eval-string @*context* source))
  ([ctx source]
   (when-some [code (not-empty (str/trim source))]
     (try
       (p/catch (p/let [value (sci/eval-string* ctx code)]
                       {:value value})
                (fn [e] {:error e}))
       (catch js/Error e {:error e})))))

(def sci-opts
  {:namespaces (merge (require-namespaces 'shapes.core
                                          'maria.friendly.kinds
                                          'maria.friendly.messages
                                          'maria.eval.repl
                                          '[sci.impl.resolve :include [resolve-symbol]]
                                          )
                      {'user {'println println
                              'prn prn
                              'pr pr}})})

(reset! *context*  (sci/init sci-opts))

(eval-string (resource/inline "user.cljs")) ;; sets up scope for repl