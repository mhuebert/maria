(ns lark.eval
  (:refer-clojure :exclude [eval])
  (:require [cljs.js :as cljs]
            [cljs.tagged-literals :as tagged-literals]
            [cljs.tools.reader :as r]
            [cljs.tools.reader.reader-types :as rt]
            [cljs.analyzer :as ana :refer [*cljs-warning-handlers*]]
            [shadow.cljs.bootstrap.browser :as boot]
            [goog.object :as gobj]
            [clojure.string :as string]
            [goog.crypt.base64 :as base64]
            [cljs.source-map :as sm]
            [clojure.string :as str]
            [cljs.env :as env]
            [clojure.set :as set]
            [applied-science.js-interop :as j])
  (:require-macros [lark.eval :refer [defspecial]]))

(def ^:dynamic *cljs-warnings* nil)

(defonce c-state (cljs/empty-state))
(defonce c-env (atom {:ns (symbol "cljs.user")}))

(defonce repl-specials {})

(defn swap-repl-specials!
  "Mutate repl specials available to the eval fns in this namespace."
  [f & args]
  (set! repl-specials (apply f repl-specials args)))

(defn c-opts
  [c-state env]
  {:load          (partial boot/load c-state)
   :eval          cljs/js-eval
   :ns            (:ns @env)
   :context       :expr
   :source-map    true
   :def-emits-var true})

(defn get-ns [c-state ns] (get-in @c-state [:cljs.analyzer/namespaces ns]))

(defn toggle-macros-ns [sym]
  (let [s (str sym)]
    (symbol
     (if (string/ends-with? s "$macros")
       (string/replace s "$macros" "")
       (str s "$macros")))))

(defn resolve-var
  ([sym]
   (resolve-var c-state c-env sym))
  ([c-state c-env sym]
   (binding [cljs.env/*compiler* c-state]
     (ana/resolve-var (assoc @cljs.env/*compiler* :ns (get-ns c-state (or (:ns @c-env) 'cljs.user))) sym))))

(defn var-value [the-var]
  (->> (string/split (:name the-var) #"[\./]")
       (map munge)
       (to-array)
       (apply gobj/getValueByKeys js/window)))

(defn resolve-symbol
  ([sym] (resolve-symbol c-state c-env sym))
  ([c-state c-env sym]
   (:name (resolve-var c-state c-env sym))))

(declare eval eval-forms)

(defn ensure-ns!
  "Create namespace if it doesn't exist"
  [c-state c-env ns]
  (when-not (contains? (get @c-state :cljs.analyzer/namespaces) ns)
    (eval c-state c-env `(~'ns ~ns))))

(defn ->macro-sym [sym]
  (let [ns? (namespace sym)
        ns (or ns? (name sym))
        name (if ns? (name ns) nil)]
    (if (string/ends-with? ns "$macros")
      ns
      (if ns? (symbol (str ns "$macros") name)
              (symbol (str ns "$macros"))))))

(defn elide-quote [x]
  (cond-> x
          (and (seq? x) (= 'quote (first x))) (second)))

(defspecial in-ns
  "Switch to namespace"
  [c-state c-env namespace]
  (let [namespace (elide-quote namespace)]
    (when-not (symbol? namespace) (throw (js/Error. "`in-ns` must be passed a symbol.")))
    (if (contains? (get @c-state :cljs.analyzer/namespaces) namespace)
      {:ns namespace}
      (eval c-state c-env `(~'ns ~namespace)))))

(defspecial ns
  "Wraps `ns` to return :ns in result map"
  [c-state c-env & body]
  (-> (eval c-state c-env (with-meta (cons 'ns body) {::skip-repl-special true}))
      (assoc :ns (first body))))

(defn repl-special [c-state c-env body]
  (let [special-sym (first body)
        f (get repl-specials special-sym)
        special-source (when-let [source (:source (meta body))]
                         (as-> source source
                               (str/replace-first source (re-pattern (str "[^]+" (str special-sym))) "")
                               (subs source 0 (dec (count source)))))]
    (try (f c-state c-env special-source body)
         (catch js/Error e
           (prn "repl-special error" body)
           (.error js/console e)
           {:error e}))))

(defn dec-pos
  "Position information from the ClojureScript reader is 1-indexed - decrement line and column."
  [{:keys [line column] :as pos}]
  (assoc pos
    :line (dec line)
    :column (dec column)))

(defn relative-pos [{target-line   :line
                     target-column :column
                     :as           target}
                    {start-line :line
                     start-col  :column}]
  (if-not start-line
    target
    (cond-> (update target :line + start-line)
            (= target-line start-line) (update :column + start-col))))

(defn warning-handler
  "Collect warnings in a dynamic var"
  [form source warning-type env extra]
  (when (ana/*cljs-warnings* warning-type)
    ;; note - not including `env` in warnings maps, because it is so large and can't be printed.
    ;;        also unsure of memory implications.
    (some-> *cljs-warnings*
            (swap! conj {:type             warning-type
                         :warning-position (relative-pos (-> (select-keys env [:line :column])
                                                             (dec-pos))
                                                         (when (satisfies? IMeta form) (some-> (meta form)
                                                                                               (dec-pos))))
                         :extra            extra
                         :source           source
                         :form             form}))))

(defn stack-error-position [error]
  (let [[line column] (some->> (j/get error :stack)
                               (re-find #"<anonymous>:(\d+)(?::(\d+))")
                               (rest)
                               (map js/parseInt))]
    {:line   line
     :column column}))

(defn mapped-cljs-position [{:keys [line column]} source-map]
  (when-let [source-map (try (some-> (base64/decodeString source-map)
                                     (js/JSON.parse)
                                     (sm/decode))
                             (catch :default e nil))]
    (some-> (get source-map (dec line))
            (subseq <= column)
            (last)
            (second)
            (last)
            (select-keys [:line :col])
            (set/rename-keys {:col :column}))))

(defn add-error-position [{:keys [error error/position error/kind start-position source-map] :as result}]
  (cond-> result
          (and error (nil? position)) (assoc :error/position
                                             (case kind :compile (some-> (ex-cause error)
                                                                         (ex-data)
                                                                         (select-keys [:line :column])
                                                                         (dec-pos)
                                                                         (relative-pos start-position))
                                                        :eval (some-> (stack-error-position error)
                                                                      (mapped-cljs-position source-map)
                                                                      (relative-pos start-position))
                                                        nil))))
(defonce cljs-cache (atom {}))

(defn compile-str
  ([c-state c-env source] (compile-str c-state c-env source {}))
  ([c-state c-env source {:keys [form
                                 file-name
                                 opts
                                 start-position]}]
   (let [the-ns (:ns @c-env)
         opts (merge (c-opts c-state c-env) opts)
         file-name (or file-name (str (string/replace (str the-ns) "." "/") "/" (gensym "cljs_live_") (if (string/ends-with? (str the-ns) "$macros")
                                                                                                        ".clj"
                                                                                                        ".cljs")))
         result (atom nil)]
     (binding [*cljs-warning-handlers* [(partial warning-handler form source)]
               r/*data-readers* (merge r/*data-readers*
                                       tagged-literals/*cljs-data-readers*)]
       (swap! cljs-cache assoc file-name source)
       (cljs/compile-str c-state source file-name opts
                         (fn [{error                       :error
                               compiled-js-with-source-map :value}]
                           (let [[compiled-js source-map] (clojure.string/split compiled-js-with-source-map #"\n//#\ssourceURL[^;]+;base64,")]

                             (->> {:source         source
                                   :form           form
                                   :start-position start-position}
                                  (merge (if error
                                           {:error      error
                                            :error/kind :compile}
                                           {:compiled-js compiled-js
                                            :source-map  source-map
                                            :env         @c-env}))
                                  (add-error-position)
                                  (reset! result)))))
       @result))))

(defn eval
  "Eval a single form. Arguments:
   c-state - a cljs compiler state atom
   c-env   - an atom for tracking the compiler environment;
             must contain an :ns key with the current namespace (as a symbol)

  Updates the `c-env` atom if the current namespace changes during eval.

  Eval returns a map containing:

  :value or :error - depending on the result of evaluation
  :error/position  - the 0-indexed position of the error, if present
  :compiled-js     - the javascript source emitted by the compiler
  :source          - the source code string that was evaluated
  :source-map      - the base64-encoded source-map string
  :env             - the compile environment, a map containing :ns (current namespace)"
  ([form] (eval c-state c-env form))
  ([c-state c-env form] (eval c-state c-env form {}))
  ([c-state c-env form opts]
   (let [repl-special? (and (seq? form)
                            (contains? repl-specials (first form))
                            (not (::skip-repl-special (meta form))))
         opts (merge (c-opts c-state c-env) opts)
         start-ns (:ns opts)
         {:keys [source] :as start-position} (when (satisfies? IMeta form)
                                               (some-> (meta form) (dec-pos)))
         {:keys [ns] :as result}
         (if repl-special?
           (repl-special c-state c-env form)
           (binding [*cljs-warning-handlers* [(partial warning-handler form source)]
                     r/*data-readers* (merge r/*data-readers*
                                             tagged-literals/*cljs-data-readers*)]
             (if source
               (let [{:keys [compiled-js
                             error] :as result} (compile-str c-state c-env source {:form           form
                                                                                   :opts           opts
                                                                                   :start-position start-position})]
                 (cond-> result
                         (not error) (-> (merge (try {:value (binding [*ns* start-ns]
                                                               (js/eval compiled-js))}
                                                     (catch js/Error e {:error      e
                                                                        :error/kind :eval})))
                                         (add-error-position))))
               (let [result (atom nil)]

                 (cljs/eval c-state form opts #(reset! result %))
                 @result))))]
     (when (and (some? ns) (not= ns (:ns @c-env)))
       (swap! c-env assoc :ns ns))
     result)))

(defn read-string-indexed
  "Read string using indexing-push-back-reader, for errors with location information."
  [s]
  (when (and s (not= "" s))
    (let [reader (rt/source-logging-push-back-reader s)]
      (loop [forms []]
        (let [form (r/read {:eof ::eof} reader)]
          (if (= form ::eof)
            forms
            (recur (conj forms form))))))))

(defn eval-forms
  "Eval a list of forms. Stops at the first error.

  Returns the result of the last form. A vector of earlier results is returned in
  the :intermediate-values key."
  [c-state c-env forms opts]
  (binding [*cljs-warnings* (or *cljs-warnings* (atom []))]
    (loop [forms forms
           intermediate-values []]
      (let [{:keys [error] :as result} (eval c-state c-env (first forms) opts)
            remaining (rest forms)]
        (if (or error (empty? remaining))
          (assoc result :warnings @*cljs-warnings*
                        :intermediate-values intermediate-values)
          (recur remaining (conj intermediate-values result)))))))

(defn read-src
  "Read src using indexed reader."
  [c-state c-env src]
  (binding [r/resolve-symbol #(resolve-symbol c-state c-env %)
            r/*data-readers* (merge r/*data-readers*
                                    tagged-literals/*cljs-data-readers*)
            r/*alias-map* (get-in @c-state [:cljs.analyzer/namespaces (:ns @c-env) :requires])
            *ns* (:ns @c-env)]
    (try {:value (read-string-indexed src)}
         (catch js/Error e
           {:error      e
            :error/kind :reader}))))

(defn eval-str
  "Eval string by first reading all top-level forms, then eval'ing them one at a time.
  Stops at the first error."
  ([src] (eval-str c-state c-env src {}))
  ([c-state c-env src] (eval-str c-state c-env src {}))
  ([c-state c-env src opts]
   (let [{:keys [error value] :as result} (read-src c-state c-env src)]
     (merge (if error
              result
              (eval-forms c-state c-env value opts))
            {:source src}))))

