(ns maria.eval
  (:require [cljs.js :as cljs]
            [cljs-live.compiler :as c]
            [cljs.tools.reader :as r]
            [cljs.tools.reader.reader-types :as rt]))

(defonce _ (c/preloads!))

(def c-state (cljs/empty-state))
(def c-env (atom {}))
(defn c-opts
  []
  {:load          c/load-fn
   :eval          cljs/js-eval
   :ns            (:ns @c-env)
   :context       :expr
   :source-map    true
   :def-emits-var true})

(defn read-string [s]
  (when (and s (not= "" s))
    (r/read {} (rt/indexing-push-back-reader s))))

(defn eval
  "Eval a single form, keeping track of current ns in fire-env."
  [form]
  (let [result (atom)
        ns? (and (seq? form) (#{'ns} (first form)))
        macros-ns? (and (seq? form) (= 'defmacro (first form)))]
    (cljs/eval c-state form (cond-> (c-opts)
                                    macros-ns?
                                    (-> (update :ns #(symbol (str % "$macros")))
                                        (assoc :macros-ns true))) (partial reset! result))
    (when (and ns? (contains? @result :value))
      (swap! c-env assoc :ns (second form)))
    @result))

(defn eval-str
  "Eval string by first reading all top-level forms, then eval'ing them one at a time."
  [src]
  (let [forms (try (read-string (str "[\n" src "]"))
                   (catch js/Error e
                     (set! (.-data e) (clj->js (update (.-data e) :line dec)))
                     {:error e}))]
    (if (contains? forms :error)
      forms
      (loop [forms forms]
        (let [{:keys [error] :as result} (eval (first forms))
              remaining (rest forms)]
          (if (or error (empty? remaining))
            result
            (recur remaining)))))))