(ns maria.eval.repl
  (:refer-clojure :exclude [eval])
  (:require [sci.impl.resolve :as sci.resolve]
            [sci.core :as sci]
            [clojure.string :as str]
            ["react" :as react]
            [sci.async :as a]
            [promesa.core :as p]
            [yawn.view :as v]))

(defonce ^:dynamic *context* (atom nil))

(defn eval-string
  "Evaluates string `s` as one or multiple Clojure expressions"
  [source]
  (sci/eval-string* @*context* source))

(defn eval
  "Evaluates form as a Clojure expression"
  [form]
  (eval-string (pr-str form)))


(defn ^:macro doc
  "Show documentation for given symbol"
  [&form &env sym]
  (let [{:keys [name ns doc arglists]} (-> (sci.impl.resolve/resolve-symbol @maria.eval.repl/*context* sym)
                                           meta)]
    `^:hiccup [:div.mx-2
               [:div.mb-1
                [:span.text-slate-500 ~(str ns)]
                [:span.text-slate-500 "/"]
                [:span.text-slate-800 ~(str name)]]
               (into [:div.text-blue-500]
                     ~(mapv #(vector :div.mb-1 (str %)) arglists))
               [:div.mb-1 ~doc]]))

(defn ^:macro dir
  "Display public vars in namespace (symbol)"
  [&form &env ns]
  #_`(with-out-str (clojure.repl/dir ~ns))
  `'~(some->> @maria.eval.repl/*context*
              :env
              deref
              :namespaces
              (#(% ns))
              keys
              (filter symbol?)
              sort))

(def ^function is-valid-element? react/isValidElement)

(defn promise? [x] (instance? js/Promise x))

(defn await? [x] (and (promise? x) (a/await? x)))

(defn await [x]
  (a/await
   (if (instance? sci.lang/Var x)
     (p/let [v @x]
       (sci/alter-var-root x (constantly v))
       x)
     (js/Promise.resolve x))))

(defn catch [^js p f]
  (if (promise? p)
    (cond-> (.catch p f) (a/await? p) a/await)
    p))
(defn then [^js p f]
  (if (promise? p)
    (cond-> (.then p f) (a/await? p) a/await)
    p))

