(ns maria.eval
  (:require [cljs.js :as cljs]
            [cljs-live.compiler :as c]
            [cljs-live.eval :as e]
            [maria.ns-utils :as ns-utils]
            [maria.views.repl-values :refer [repl-card]]
            [maria.views.repl-ns :as repl-ns]))

(defonce c-state (cljs/empty-state))
(defonce c-env (atom {:ns (symbol "cljs.user")}))

(defn doc*
  "Show doc for symbol"
  [c-state c-env [_ n]]
  (let [[namespace name] (let [n (e/resolve-symbol c-state c-env n)]
                           (map symbol [(namespace n) (name n)]))]
    {:value (repl-ns/doc (merge {:expanded?   true
                                 :standalone? true}
                                (get-in (ns-utils/ns-map @c-state namespace) [:defs name])))}))

(defn dir*
  "Display public vars in namespace"
  [c-state c-env [_ ns]]
  (let [ns (or ns (:ns @c-env))]
    {:value (repl-ns/dir c-state ns)}))

;; mutate cljs-live's default repl-specials
(e/swap-repl-specials! merge {'doc doc*
                              'dir dir*})

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







