(ns chia.view
  (:require [applied-science.js-interop :as j]
            [cljs.analyzer :as ana]
            [clojure.core :as core]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [clojure.walk :as walk]))

(defn- find-all [pred body]
  (let [sym-list (atom [])]
    (walk/postwalk
      (fn w [x]
        (if (pred x)
          (do (swap! sym-list conj x)
              x)
          x))
      body)
    @sym-list))

(comment
  ;; future use
  (defn- find-locals [{:as env :keys [locals]} body]
    (into []
          (comp (distinct)
                (keep #(:name (locals %))))
          (find-all symbol? body))))

(defn- maybe-hook?
  ;; it's ok to be liberal in what we accept - the signature only changes when
  ;; editing source code.
  [sym]
  (let [sym-name (name sym)]
    (or
      (identical? "deref" sym-name)
      (str/starts-with? sym-name "use")
      (some->> (ana/resolve-symbol sym)
               (namespace)
               (str/includes? "hook")))))

(defn- hook-signature [body]
  ;; identifies lexical occurrences of symbols that begin with `use`
  ;; followed by - or any capital letter. Whenever this "hook signature"
  ;; changes
  (->> (find-all (every-pred list? (comp (every-pred symbol? maybe-hook?) first)) body)
       (map (fn [form] (str (first form) "-" (hash form))))
       (str/join "|")))

(core/defn parse-functional-view-args [args]
  (let [view-map (s/conform (s/cat :name (s/? symbol?)
                                   :doc (s/? string?)
                                   :view/options (s/? map?)
                                   :body (s/+ any?))
                            args)]
    (assoc view-map :view/name
                    (symbol (name (ns-name *ns*))
                            (name (:name view-map))))))

(defmacro defview [& args]
  (let [{:keys [name
                doc
                view/options
                body]
         view-name :view/name} (parse-functional-view-args args)
        {:view/keys [forward-ref?]} options
        f-sym (symbol (str "-" name))
        keyf-sym (gensym "key")
        key-fn (:key options)
        args-sym (gensym "args")
        view-meta (reduce-kv (fn [m k v] (cond-> m (not (= "view" (name k)))
                                                   (assoc k v))) {} options)
        signature (gensym (str name "-signature"))
        qualified-name (str *ns* "/" name)]
    `(let [~keyf-sym ~key-fn
           ~f-sym (~'chia.view/-functional-render
                    #:view{:view/name ~(str view-name)
                           :view/fn (fn ~name ~@body)
                           :view/should-update? ~(:view/should-update? options `not=)
                           :view/forward-ref? ~(:view/forward-ref? options false)})]

       (def ~signature (~'chia.view/signature-fn))

       (def ~name
         (-> (fn ~name [& ~args-sym]
               (when ~'chia.view/refresh-enabled? (~signature))
               (let [props# (when ~(or keyf-sym forward-ref?)
                              (j/obj
                                ~@(when key-fn
                                    [:key `(apply ~keyf-sym ~args-sym)])
                                ~@(when forward-ref?
                                    [:ref `(:ref (first ~args-sym))])))]
                 (~'chia.view/-create-element ~f-sym props# ~args-sym)))
             (~'applied-science.js-interop/!set
               :chia$meta ~view-meta
               :displayName ~qualified-name)))

       (when ~'chia.view/refresh-enabled?
         ;; type, key, forceReset, getCustomHooks
         (~signature ~f-sym ~(hook-signature body) nil nil)
         (~'chia.view/register! ~f-sym ~qualified-name))
       ~name)))

(defmacro defclass [& body]
  `(~'chia.view.legacy/defclass ~@body))
