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

(defn read-string-indexed
  "Read string using indexing-push-back-reader, for errors with location information."
  [s]
  (when (and s (not= "" s))
    (r/read {} (rt/indexing-push-back-reader s))))

(defn eval
  "Eval a single form, keeping track of current ns in c-env"
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

(defn wrap-source
  "Clojure reader only returns the last top-level form in a string,
  so we wrap user source strings."
  [src]
  (str "[\n" src "]"))

(defn read-src
  "Read src using default tools.reader. If an error is encountered,
  re-read an unwrapped version of src using indexed reader to return
  a correct error location."
  [src]
  (try (r/read-string (wrap-source src))
       (catch js/Error e1
         (try (read-string-indexed src)
              ;; if no error thrown by indexed reader, return original error
              {:error e1}
              (catch js/Error e2
                {:error e2})))))

(defn eval-src
  "Eval string by first reading all top-level forms, then eval'ing them one at a time."
  [src]
  (let [{:keys [error] :as result} (read-src src)]
    (if error result
              (loop [forms result]
                (let [{:keys [error] :as result} (eval (first forms))
                      remaining (rest forms)]
                  (if (or error (empty? remaining))
                    result
                    (recur remaining)))))))