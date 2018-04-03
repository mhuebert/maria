(ns maria.eval
  (:refer-clojure :exclude [macroexpand eval])
  (:require [cljs.js :as cljs]
            [lark.eval :as e :refer [defspecial]]
            [shadow.cljs.bootstrap.browser :as boot]
            [re-db.d :as d]
            [cljs.compiler :as comp]
            [cljs.analyzer :as ana]))

(defonce c-state (cljs/empty-state))
(defonce c-env (atom {:ns (symbol "cljs.user")}))
(defonce -eval-logs (volatile! {}))
(def bootstrap-path "/js/compiled/bootstrap")

(def add-error-position e/add-error-position)

(defn handle-block-error [block-id error]
  (js/console.error "handle-block-error/error" error)
  (let [eval-log (get @-eval-logs block-id)
        result (-> (first eval-log)
                   (assoc :error (or error (js/Error. "Unknown error"))
                          :error/kind :eval)
                   (e/add-error-position))]
    (vswap! -eval-logs update block-id #(cons result (rest %)))
    nil))

#_(set! boot/script-eval (fn script-eval
                           [code]
                           (let [node (doto (js/document.createElement "script")
                                        #_(.setAttribute "src" uri)
                                        (.setAttribute "type" "text/javascript")
                                        (.setAttribute "text" code))]
                             (js/document.body.appendChild node)
                             (js/document.body.removeChild node))))

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
(defonce queue [])
(defn on-load [f]
  (if loaded? (f)
              (set! queue (conj queue f))))
(defn loaded! []
  (set! loaded? true)
  (doseq [f queue]
    (f)))



(defn init []
  (boot/init c-state
             {:path         bootstrap-path
              :load-on-init '#{maria.user cljs.spec.alpha cljs.spec.test.alpha}}
             (fn []
               (eval-form* '(inject 'cljs.core '{what-is       maria.friendly.kinds/what-is
                                                 load-gist     maria.user.loaders/load-gist
                                                 load-js       maria.user.loaders/load-js
                                                 load-npm      maria.user.loaders/load-npm
                                                 html          re-view.hiccup.core/element
                                                 macroexpand-n maria.eval/macroexpand-n
                                                 eval          maria.eval/eval}))
               (doseq [form ['(in-ns cljs.spec.test.alpha$macros)
                             '(def eval maria.eval/eval)
                             '(in-ns maria.user)]]
                 (eval-form* form))
               (loaded!)))
  #_(let [bundles ["cljs.core"
                   "maria.user"
                   "cljs.spec.alpha"
                   #_"reagent.core"
                   #_"bach-leipzig"]]
      (c/load-bundles! (map #(str "/js/cljs_live_bundles/" % ".json") bundles)
                       (fn []
                         (eval-form* '(require '[cljs.core :include-macros true]))
                         (eval-form* '(require '[maria.user :include-macros true]))
                         (eval-form* '(inject 'cljs.core '{what-is   maria.friendly.kinds/what-is
                                                           load-gist maria.user.loaders/load-gist
                                                           load-js   maria.user.loaders/load-js
                                                           load-npm  maria.user.loaders/load-npm
                                                           html      re-view.hiccup.core/element}))
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

(defn eval [form]
  (get (eval-form* form) :value))

(defn macroexpand-n
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

;; Taken from planck eval implementation
;; The following atoms and fns set up a scheme to
;; emit function values into JavaScript as numeric
;; references that are looked up.

(defonce ^:private fn-index (volatile! 0))
(defonce ^:private fn-refs (volatile! {}))

(defn- clear-fns!
  "Clears saved functions."
  []
  (vreset! fn-refs {}))

(defn- put-fn
  "Saves a function, returning a numeric representation."
  [f]
  (let [n (vswap! fn-index inc)]
    (vswap! fn-refs assoc n f)
    n))

(defn- get-fn
  "Gets a function, given its numeric representation."
  [n]
  (get @fn-refs n))

(defn- emit-fn [f]
  (print "maria.eval.get_fn(" (put-fn f) ")"))

(defmethod comp/emit-constant js/Function
  [f]
  (emit-fn f))

(defmethod comp/emit-constant cljs.core/Var
  [f]
  (emit-fn f))
