(ns maria.eval
  (:require [cljs.js :as cljs]
            [cljs.analyzer :refer [*cljs-warning-handlers*]]
            [cljs-live.compiler :as c]
            [cljs.tools.reader :as r]
            [cljs.repl :refer [print-doc]]
            [maria.html :refer [html]]
            [maria.friendly.docstrings :refer [docstrings]]
            [cljs.tools.reader.reader-types :as rt]
            [clojure.string :as string]))

(defonce c-state (cljs/empty-state))
(defonce c-env (atom {:ns (symbol "cljs.user")}))
(def ^:dynamic *cljs-warnings* nil)

(defn get-ns [ns] (get-in @c-state [:cljs.analyzer/namespaces ns]))

(defn resolve-symbol [sym]
  (binding [cljs.env/*compiler* c-state]
    (:name (cljs.analyzer/resolve-var (assoc @cljs.env/*compiler* :ns (or (get-ns (:ns @c-env)) 'cljs.user)) sym))))

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
            (let [[namespace name] (let [n (resolve-symbol n)]
                                     (map symbol [(namespace n) (name n)]))
                  friendly-doc (get-in docstrings [namespace name])]
              {:value
               (html [:div (with-out-str
                             (some-> (get-in @c-state [:cljs.analyzer/namespaces namespace :defs name])
                                     (select-keys [:name :doc :arglists])
                                     (cond->
                                       friendly-doc (assoc :doc friendly-doc))
                                     print-doc)
                             "Not found")
                      (when (#{'cljs.core 'cljs.core$macros 'clojure.core} namespace)
                        (list [:.gray.di "view on "]
                              [:a {:href (str "https://clojuredocs.org/clojure.core/" name)} "clojuredocs.org"]))])}))
   })

(defn repl-special [op & args]
  (when-let [f (get repl-special* op)]
    (try (apply f args)
         (catch js/Error e {:error e}))))

(declare eval)

(defn ensure-macro-ns [sym]
  (if
    (string/ends-with? (name sym) "$macros")
    sym
    (symbol (namespace sym) (str (name sym) "$macros"))))

(defn eval
  "Eval a single form, keeping track of current ns in c-env"
  [form]
  (or (and (seq? form) (apply repl-special form))
      (let [result (atom)
            ns? (and (seq? form) (#{'ns} (first form)))
            macros-ns? (and (seq? form) (= 'defmacro (first form)))
            opts (cond-> (c-opts)
                         macros-ns?
                         (merge {:macros-ns true
                                 :ns        (ensure-macro-ns (:ns @c-env))}))]
        (binding [*cljs-warning-handlers* [(fn [warning-type env extra]
                                             (some-> *cljs-warnings*
                                                     (swap! conj {:type        warning-type
                                                                  :env         env
                                                                  :extra       extra
                                                                  :source-form form})))]
                  r/*data-readers* (conj r/*data-readers* {'js identity})]
          (try (cljs/eval c-state form opts (partial swap! result merge))
               (when (and macros-ns? (not= (:ns opts) (:ns @c-env)))
                 (eval `(require-macros '[~(:ns @c-env) :refer [~(second form)]])))
               (catch js/Error e
                 (throw e)
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
  (binding [cljs.tools.reader/resolve-symbol resolve-symbol]
    (try (r/read-string (wrap-source src))
         (catch js/Error e1
           (try (read-string-indexed src)
                ;; if no error thrown by indexed reader, return original error
                {:error e1}
                (catch js/Error e2
                  {:error e2}))))))

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
