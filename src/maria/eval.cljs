(ns maria.eval
  (:require [cljs.js :as cljs]
            [cljs-live.compiler :as c]
            [cljs-live.eval :as e :refer [defspecial]]
            [re-db.d :as d]))

(defonce c-state (cljs/empty-state))
(defonce c-env (atom {:ns (symbol "cljs.user")}))

(defn resolve-var
  ([sym] (e/resolve-var c-state c-env sym))
  ([c-state c-env sym] (e/resolve-var c-state c-env sym)))

(def var-value e/var-value)

(defonce eval-log (atom (list)))

(defn eval-log-wrap [f]
  (fn [& args]
    (let [result (apply f args)]
      (swap! eval-log conj result)
      result)))

(def eval (eval-log-wrap (partial e/eval c-state c-env)))
(def eval-str (eval-log-wrap (partial e/eval-str c-state c-env)))
(def compile-str (partial e/compile-str c-state c-env))

;; Simple queue for functions to be executed
;; after self-host environment is ready.
(def loaded? false)
(def queue [])
(defn on-load [f]
  (if loaded? (f)
              (set! queue (conj queue f))))
(defn loaded! []
  (set! loaded? true)
  (doseq [f queue]
    (f)))

(defn init []
  #_(set! cljs-live.compiler/debug? true)
  (let [bundles ["cljs.core"
                 "maria.user"
                 "cljs.spec.alpha"
                 #_"reagent.core"
                 #_"bach-leipzig"]]
    (c/load-bundles! (map #(str "/js/cljs_live_bundles/" % ".json") bundles)
                     (fn []
                       (eval '(require '[cljs.core :include-macros true]))
                       (eval '(require '[maria.user :include-macros true]))
                       (eval '(inject 'cljs.core '{what-is   maria.messages/what-is
                                                        load-gist maria.user.loaders/load-gist
                                                        load-js   maria.user.loaders/load-js
                                                        load-npm  maria.user.loaders/load-npm
                                                        html      re-view-hiccup.core/element}))
                       (eval '(in-ns maria.user))

                       (loaded!)))))


(defn log-eval-result! [id result]
  (let [result (assoc result :id (d/unique-id))]
    (d/transact! [[:db/update-attr :repl/state :eval-log (fnil conj []) result]
                  (when id [:db/update-attr id :eval-log (fnil conj []) result])])))

(defn logged-eval-str [id source]
  (log-eval-result! id (eval-str source)))

(defn logged-eval-form [id form]
  (log-eval-result! id (eval form)))