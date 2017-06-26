(ns maria.eval
  (:require [cljs.js :as cljs]
            [cljs-live.eval :as e :refer [defspecial]]))

(defonce c-state (cljs/empty-state))
(defonce c-env (atom {:ns (symbol "cljs.user")}))
(defonce eval-log (atom (list)))

(defn eval-log-wrap [f]
  (fn [& args]
    (let [result (apply f args)]
      (swap! eval-log conj result)
      result)))

(def eval (eval-log-wrap (partial e/eval c-state c-env)))
(def eval-str (eval-log-wrap (partial e/eval-str c-state c-env)))


