(ns maria.eval.sci
  (:refer-clojure :exclude [eval])
  (:require ["@codemirror/view" :as view]
            [clojure.string :as str]
            [sci.core :as sci]
            [shadow.resource :as resource]
            shapes.core
            maria.friendly.kinds
            [maria.eval.repl :refer [*context*]]
            sci.impl.resolve)
  (:require-macros [maria.eval.sci :refer [require-namespaces]]))

(defn eval-string
  ([source] (eval-string @*context* source))
  ([ctx source]
   (when-some [code (not-empty (str/trim source))]
     (try {:value (sci/eval-string* ctx code)}
          (catch js/Error ^js e
            {:error (str (.-message e))})))))

(def eval (comp eval-string pr-str))

(def sci-opts
  {:namespaces (merge (require-namespaces 'shapes.core
                                          'maria.friendly.kinds
                                          'maria.friendly.messages
                                          'maria.eval.repl
                                          '[sci.impl.resolve :include [resolve-symbol]])
                      {'user {'println println
                              'prn prn
                              'pr pr
                              'eval eval}})})

(reset! *context*  (sci/init sci-opts))

(eval-string (resource/inline "user.cljs")) ;; sets up scope for repl