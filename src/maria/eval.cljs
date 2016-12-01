(ns maria.eval
  (:require [cljs.js :as cljs]
            [cljs.analyzer :refer [*cljs-warning-handlers*]]
            [cljs-live.compiler :as c]
            [cljs.tools.reader :as r :refer [resolve-symbol]]
            [cljs.repl :refer [print-doc]]
            [maria.html :refer [html]]
            [maria.friendly.docstrings :refer [docstrings]]
            [cljs.tools.reader.reader-types :as rt]))

(defonce c-state (cljs/empty-state))
(defonce c-env (atom {}))
(def ^:dynamic *cljs-warnings* nil)

(defn c-opts
  []
  {:load               c/load-fn
   :eval               cljs/js-eval
   :ns                 (:ns @c-env)
   :context            :expr
   :source-map         true
   :def-emits-var      true
   :warn-on-undeclared true})

(defn read-string-indexed
  "Read string using indexing-push-back-reader, for errors with location information."
  [s]
  (when (and s (not= "" s))
    (r/read {} (rt/indexing-push-back-reader s))))

(def repl-special*
  {'in-ns (fn [n]
            (when-not (symbol? n)
              (throw (js/Error. "`in-ns` must be passed a symbol.")))
            (swap! c-env assoc :ns n)
            {:value nil
             :ns    n})

   'doc   (fn [n]
            (let [[namespace name] (map symbol [(namespace n) (name n)])
                  friendly-doc (get-in docstrings [namespace name])]
              {:value
               (html [:div (with-out-str
                             (some-> (get-in @c-state [:cljs.analyzer/namespaces namespace :defs name])
                                     (select-keys [:name :doc])
                                     (cond->
                                       friendly-doc (assoc :doc friendly-doc))
                                     print-doc)
                             "Not found")])}))
   })

(defn repl-special [op & args]
  (when-let [f (get repl-special* op)]
    (try (apply f args)
         (catch js/Error e {:error e}))))

(defn eval
  "Eval a single form, keeping track of current ns in c-env"
  [form]
  (or (and (seq? form) (apply repl-special form))
      (let [result (atom)
            ns? (and (seq? form) (#{'ns} (first form)))
            macros-ns? (and (seq? form) (= 'defmacro (first form)))]
        (binding [*cljs-warning-handlers* [(fn [warning-type env extra]
                                             (some-> *cljs-warnings*
                                                     (swap! conj {:type        warning-type
                                                                  :env         env
                                                                  :extra       extra
                                                                  :source-form form})))]
                  r/*data-readers* (conj r/*data-readers* {'js identity})]
          (try (cljs/eval c-state form (cond-> (c-opts)
                                               macros-ns?
                                               (-> #_(update :ns #(symbol (str % "$macros")))
                                                 (assoc :macros-ns true))) (partial swap! result merge))
               (catch js/Error e
                 (.error js/console (or (.-cause e) e))
                 (swap! result assoc :error e))))
        (when (and ns? (contains? @result :value))
          (swap! c-env assoc :ns (second form)))
        @result)))

(defn wrap-source
  "Clojure reader only returns the last top-level form in a string,
  so we wrap user source strings."
  [src]
  (str "[\n" src "\n]"))

(defn read-src
  "Read src using default tools.reader. If an error is encountered,
  re-read an unwrapped version of src using indexed reader to return
  a correct error location."
  [src]
  (try (r/read-string (wrap-source src))
       (catch js/Error e1
         (try (read-string-indexed src)
              ;; if no error thrown by indexed reader, return original error
              {:error e1}
              (catch js/Error e2
                {:error e2})))))

(defn eval-src
  "Eval string by first reading all top-level forms, then eval'ing them one at a time."
  [src]
  (binding [*cljs-warnings* (atom [])]
    (let [{:keys [error] :as result} (read-src src)]
      (if error result
                (loop [forms result]
                  (let [{:keys [error] :as result} (eval (first forms))
                        remaining (rest forms)]
                    (if (or error (empty? remaining))
                      (assoc result :warnings @*cljs-warnings*)
                      (recur remaining))))))))

(defonce _
         (c/load-bundles! ["/js/cljs_bundles/cljs.core.json"
                           "/js/cljs_bundles/maria.user.json"
                           #_"/js/cljs_bundles/quil.json"]
                          (fn []
                            (eval '(require '[cljs.core :include-macros true]))
                            (eval '(require '[maria.user :include-macros true]))
                            (eval '(in-ns maria.user)))))
