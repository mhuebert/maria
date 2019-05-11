(ns maria.eval
  (:refer-clojure :exclude [macroexpand eval])
  (:require [lark.eval :as e :refer [defspecial]]
            [shadow.cljs.bootstrap.browser :as boot]
            [chia.db :as d]
            [kitchen-async.promise :as p]))

(def bootstrap-path "/js/compiled/bootstrap")

;;;;;;;;;;;;;
;;
;; Compiler state

(defonce c-state e/c-state)
(defonce c-env e/c-env)
(defonce resolve-var e/resolve-var)

;;;;;;;;;;;;;
;;
;; Block error handling

(defonce -block-eval-log (volatile! {}))

(def add-error-position e/add-error-position)

(defn handle-block-error [block-id error]
  (let [eval-log (get @-block-eval-log block-id)
        result (-> (first eval-log)
                   (assoc :error (or error (js/Error. "Unknown error"))
                          :error/kind :eval)
                   (e/add-error-position))]
    (vswap! -block-eval-log update block-id #(cons result (rest %)))
    nil))

(defonce eval-log (atom (list)))

(defn eval-log-wrap [f]
  (fn [& args]
    (let [result (apply f args)]
      (swap! eval-log conj result)
      result)))

(def eval-form*
  (eval-log-wrap (partial e/eval c-state c-env)))
(def eval-str*
  (eval-log-wrap (partial e/eval-str c-state c-env)))
(def compile-str
  (partial e/compile-str c-state c-env))

#_(defn macroexpand-n
    ([form] (macroexpand-n 1000 form))
    ([depth-limit form]
     (loop [form form
            n 0]
       (if (>= n depth-limit)
         form
         (let [expanded (ana/macroexpand-1 c-state form)]
           (if (= form expanded)
             expanded
             (recur expanded (inc n))))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Logged eval fns

(defn log-eval-result! [result]
  (let [result (assoc result :id (d/unique-id))]
    (d/transact! [[:db/update-attr :repl/state :eval-log (fnil conj []) result]])
    result))

(defn eval-str [source]
  (log-eval-result! (eval-str* source)))

(defn eval-form [form]
  (log-eval-result! (eval-form* form)))

(defn eval* [form]
  (get (eval-form* form) :value))

(defonce compiler-ready
         (delay
          (p/promise [resolve reject]
            (boot/init
             c-state
             {:path         bootstrap-path
              :load-on-init '#{maria.user
                               cljs.spec.alpha
                               cljs.spec.test.alpha}}
             (fn []
               (eval-form* '(inject 'cljs.core '{what-is   maria.friendly.kinds/what-is
                                                 load-gist maria.user.loaders/load-gist
                                                 load-js   maria.user.loaders/load-js
                                                 load-npm  maria.user.loaders/load-npm
                                                 html      chia.view.hiccup/element
                                                 #_#_macroexpand-n maria.eval/macroexpand-n}))
               (doseq [form ['(in-ns cljs.spec.test.alpha$macros)
                             '(in-ns maria.user)]]
                 (eval-form* form))
               (resolve))))))

(set! cljs.core/*eval* eval*)
