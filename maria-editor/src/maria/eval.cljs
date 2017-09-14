(ns maria.eval
  (:require [cljs.js :as cljs]
            [cljs-live.compiler :as c]
            [cljs-live.eval :as e :refer [defspecial]]
            [re-db.d :as d]))

(defonce c-state (cljs/empty-state))
(defonce c-env (atom {:ns (symbol "cljs.user")}))

(def -eval-logs (volatile! {}))

(def add-error-position e/add-error-position)

(defn handle-block-error [block-id error]
  (.log js/console "error" error)
  (let [eval-log (d/get block-id :eval-log)
        result (-> (first eval-log)
                   (assoc :error (or error (js/Error. "Unknown error"))
                          :error/kind :eval)
                   (e/add-error-position))]
    (vswap! -eval-logs update block-id #(cons result (rest %)))
    nil))

(defn result-value [x]
  (if (var? x) @x x))

(def var-value e/var-value)

(defn resolve-var
  ([sym] (e/resolve-var c-state c-env sym))
  ([c-state c-env sym] (e/resolve-var c-state c-env sym)))

(defonce eval-log (atom (list)))

(defn eval-log-wrap [f]
  (fn [& args]
    (let [result (apply f args)]
      (swap! eval-log conj result)
      result)))

(def eval-form* (eval-log-wrap (partial e/eval c-state c-env)))
(def eval-str* (eval-log-wrap (partial e/eval-str c-state c-env)))
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
  ;(set! cljs-live.compiler/debug? true)
  (let [bundles ["cljs.core"
                 "maria.user"
                 "cljs.spec.alpha"
                 #_"reagent.core"
                 #_"bach-leipzig"]]
    (c/load-bundles! (map #(str "/js/cljs_live_bundles/" % ".json") bundles)
                     (fn []
                       (eval-form* '(require '[cljs.core :include-macros true]))
                       (eval-form* '(require '[maria.user :include-macros true]))
                       (eval-form* '(inject 'cljs.core '{what-is   maria.messages/what-is
                                                         load-gist maria.user.loaders/load-gist
                                                         load-js   maria.user.loaders/load-js
                                                         load-npm  maria.user.loaders/load-npm
                                                         html      re-view-hiccup.core/element}))
                       (eval-form* '(in-ns maria.user))

                       (loaded!)))))


(defn log-eval-result! [result]
  (let [result (assoc result :id (d/unique-id))]
    (d/transact! [[:db/update-attr :repl/state :eval-log (fnil conj []) result]])
    result))

(defn eval-str [source]
  (log-eval-result! (eval-str* source)))

(defn eval-form [form]
  (log-eval-result! (eval-form* form))) 