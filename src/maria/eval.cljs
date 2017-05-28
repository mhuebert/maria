(ns maria.eval
  (:require [cljs.js :as cljs]
            [cljs-live.compiler :as c]
            [cljs-live.eval :as e :refer [defspecial]]
            [maria.views.repl-values :refer [repl-card]]

            [maria.repl-specials]))

(defonce c-state (cljs/empty-state))
(defonce c-env (atom {:ns (symbol "cljs.user")}))

(def eval (partial e/eval c-state c-env))
(def eval-str (partial e/eval-str c-state c-env))

(defonce _
         (do (set! cljs-live.compiler/debug? true)
             (c/load-bundles! ["/js/cljs_bundles/cljs.core.json"
                               "/js/cljs_bundles/maria.user.json"
                               #_"/js/cljs_bundles/cljs.spec.alpha.json"]
                              (fn []
                                (eval '(require '[cljs.core :include-macros true]))
                                (eval '(require '[maria.user :include-macros true]))
                                (eval '(in-ns maria.user))))))







