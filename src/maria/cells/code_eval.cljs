(ns maria.cells.code-eval
  (:require [maria.eval :as eval]
            [re-db.d :as d]
            [cljs-live.compiler :as c]))


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
                       (eval/eval '(require '[cljs.core :include-macros true]))
                       (eval/eval '(require '[maria.user :include-macros true]))
                       (eval/eval '(inject 'cljs.core '{what-is   maria.messages/what-is
                                                        load-gist maria.user.loaders/load-gist
                                                        load-js   maria.user.loaders/load-js
                                                        load-npm  maria.user.loaders/load-npm
                                                        html      re-view-hiccup.core/element}))
                       (eval/eval '(in-ns maria.user))

                       (loaded!)))))


(defn log-eval-result! [id result]
  (let [result (assoc result :id (d/unique-id))]
    (d/transact! [[:db/update-attr :repl/state :eval-log (fnil conj []) result]
                  (when id [:db/update-attr id :eval-log (fnil conj []) result])])))

(defn eval-str [id source]
  (log-eval-result! id (eval/eval-str source)))

(defn eval-form [id form]
  (log-eval-result! id (eval/eval form)))