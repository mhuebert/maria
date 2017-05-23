(ns maria.eval
  (:require [cljs.js :as cljs]
            [cljs.analyzer :refer [*cljs-warning-handlers*]]
            [cljs-live.compiler :as c]
            [cljs-live.eval :as e]
            [cljs.tools.reader :as r]
            [cljs.repl :refer [print-doc]]
            [re-view-hiccup.core :as hiccup :refer [element]]
            [maria.friendly.docstrings :refer [docstrings]]
            [cljs.tools.reader.reader-types :as rt]
            [clojure.string :as string]))

(defonce c-state (cljs/empty-state))
(defonce c-env (atom {:ns (symbol "cljs.user")}))

(defn doc
  "Show doc for symbol"
  [c-state c-env [_ n]]
  (let [[namespace name] (let [n (e/resolve-symbol c-state c-env n)]
                           (map symbol [(namespace n) (name n)]))
        friendly-doc (get-in docstrings [namespace name])]
    {:value
     (element [:div (with-out-str
                      (some-> (get-in @c-state [:cljs.analyzer/namespaces namespace :defs name])
                              (select-keys [:name :doc :arglists])
                              (cond->
                                friendly-doc (assoc :doc friendly-doc))
                              print-doc)
                      "Not found")
               (when (#{'cljs.core 'cljs.core$macros 'clojure.core} namespace)
                 (list [:.gray.di "view on "]
                       [:a {:href   (str "https://clojuredocs.org/clojure.core/" name)
                            :target "_blank"
                            :rel    "noopener noreferrer"} "clojuredocs.org"]))])}))

;; mutate cljs-live's default repl-specials
(e/swap-repl-specials! assoc 'doc doc)

(def eval (partial e/eval c-state c-env))
(def eval-str (partial e/eval-str c-state c-env))

(defonce _
         (do (set! cljs-live.compiler/debug? true)
             (c/load-bundles! ["/js/cljs_bundles/cljs.core.json"
                               "/js/cljs_bundles/maria.user.json"
                               #_"/js/cljs_bundles/quil.json"]
                              (fn []
                                (eval '(require '[cljs.core :include-macros true]))
                                (eval '(require '[maria.user :include-macros true]))
                                (eval '(in-ns maria.user))))))







