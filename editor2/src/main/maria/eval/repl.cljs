(ns maria.eval.repl
  (:refer-clojure :exclude [eval])
  (:require [sci.impl.resolve :as sci.resolve]
            [sci.core :as sci]
            [clojure.string :as str]
            ["react" :as react]
            [sci.async :as a]
            [promesa.core :as p]))

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
  #_`(unescape (with-out-str (clojure.repl/doc ~sym)))
  (-> (sci.impl.resolve/resolve-symbol @maria.eval.repl/*context* sym)
      meta
      :doc))

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