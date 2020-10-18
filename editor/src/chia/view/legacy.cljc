(ns chia.view.legacy
  (:require [clojure.core :as core]
            [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [chia.view.legacy.util :as class-util]
            [chia.util :as u]))

(defmacro ^:private apply-fn [f this]
  `(if-let [children# (.. ~this -state -children)]
     (.apply ~f ~this (to-array (cons ~this children#)))
     (.call ~f ~this ~this)))

(core/defn- get-display-name
  "Generate a meaningful name to identify React components while debugging"
  [ns given-name]
  (let [segments (->> (str/split (name (ns-name ns)) #"\.")
                      (drop 1)
                      (take-last 2))]
    (str (str/join "." segments)
         "/"
         (or given-name
             (gensym "view")))))

(core/defn- wrap-render-body
  "Wrap body in anonymous function form."
  [name argv body]
  `(~'fn ~(symbol (str "__" name)) ~argv
    (~'chia.view.props/to-element (do ~@body))))

(core/defn- make-constructor [the-name initial-state]
  (let [this-name (gensym)
        fn-name (gensym the-name)
        props-sym (gensym "props")]
    `(fn ~fn-name [~props-sym]
       (core/this-as ~this-name
         ;; super()
         (~'.call ~'chia.view.legacy/Component ~this-name ~props-sym)
         ;; init internal state

         (~'applied-science.js-interop/assoc! ~this-name ~'.-state (~'js-obj))

         ~(when initial-state
            `(~'chia.view.legacy/populate-initial-state! ~this-name ~props-sym ~initial-state))

         ;; return component
         ~this-name))))

(core/defn- ->js-with-camelCase [renamable? m]
  (let [m (u/update-keys m (comp (if renamable?
                                   (fn [s] (symbol (str ".-" s)))
                                   keyword) u/camel-case name))]
    `(~'applied-science.js-interop/obj ~@(apply concat m))))

(core/defn- bind-vals [m]
  (reduce-kv (fn [m k v]
               (assoc m k `(let [v# ~v]
                             (if (fn? v#)
                               (fn [& args#]
                                 (~'this-as this#
                                  (apply v# this# args#)))
                               v#)))) {} m))

(def __deprecated-keys #{:view/will-receive-props
                         :view/will-update
                         :view/will-mount})


(core/defn- group-methods
  "Groups methods by role in a React component."
  [methods]
  (-> (reduce-kv (fn [m k v]
                   (when (__deprecated-keys k)
                     (throw (ex-info "Deprecated lifecycle key" {:key k})))
                   (let [[group-k v] (case (namespace k)
                                       "static" [:static-keys v]
                                       "spec" [:spec-keys `(when ~'js/goog.DEBUG ~v)]
                                       "view" (do (assert (class-util/lifecycle-keys k)
                                                          (str "Unknown chia/view key: " k))
                                                  [:lifecycle-keys v])
                                       nil (case (name k)
                                             ("key"
                                              "doc"
                                              "display-name") [:react-keys v]
                                             [:unqualified-keys v])
                                       [:qualified-keys v])]
                     (assoc-in m [group-k k] v))) {} methods)
      (update :react-keys (partial ->js-with-camelCase false))
      (update :unqualified-keys (comp (partial ->js-with-camelCase true) bind-vals))))

(defn parse-class-args [args]
  (let [view-map (s/conform (s/cat :name (s/? symbol?)
                                   :doc (s/? string?)
                                   :view/options (s/? map?)
                                   :view/arglist vector?
                                   :view/body (s/+ any?))
                            args)]
    (assoc view-map :view/name
                    (symbol (name (ns-name *ns*))
                            (name (:name view-map))))))

(core/defn make-class [{:keys [name
                               doc
                               view/options
                               view/arglist
                               view/body]}]
  (let [display-name (get-display-name *ns* name)
        methods (-> options
                    (dissoc :view/initial-state)
                    (merge (cond-> {;; TODO
                                    ;; keep track of dev- vs prod-time, elide display-name and docstring in prod
                                    :display-name display-name
                                    :view/render  (wrap-render-body name arglist body)}
                                   doc (assoc :doc doc)))
                    (group-methods))]
    (let [constructor (make-constructor name (:view/initial-state options))]
      `(~'chia.view.legacy/view* ~methods ~constructor))))

(defmacro view
  [& args]
  (make-class (parse-class-args args)))

(defmacro defclass
  "Define a view function.

   Expects optional docstring and methods map, followed by
    the argslist and body for the render function, which should
    return a Hiccup vector or React element."
  [& args]
  (let [{:as   view-map
         :keys [name]} (parse-class-args args)
        name (with-meta name (merge
                              (meta name)
                              (select-keys view-map [:doc
                                                     :view/arglist
                                                     :view/name])))]
    `(def ~name ~(make-class view-map))))

(defmacro extend-view [view & args]
  `(~'cljs.core/specify!
    (~'goog.object/getValueByKeys ~view "chia$constructor" "prototype")
    ~@args))

(defmacro once
  "Evaluates `body` once per component mount or, if :key is provided, once per unique key (per component mount).

  :on-unmount - will be called with [component, value] when component unmounts."
  ([body]
   `(once {} ~body))
  ([{:keys [key
            on-unmount]} body]
   (let [gname (name (gensym "once"))
         js-get 'applied-science.js-interop/get
         js-assoc! 'applied-science.js-interop/assoc!
         this-sym (gensym "this")
         key-sym (gensym "key")
         val-sym (gensym "val")]
     `(let [~key-sym ~(if key `(str ~gname "/" ~key)
                              gname)
            ~this-sym ~'chia.reactive/*reader*]
        (or (~js-get ~this-sym ~key-sym)
            (let [~val-sym ~body]
              (~js-assoc! ~this-sym ~key-sym ~val-sym)
              ~(when on-unmount
                 `(~'chia.view/on-unmount! ~this-sym ~key-sym
                   (fn [this#] (~on-unmount this# ~val-sym))))
              ~val-sym))))))

(defmacro defspec [kw doc & args]
  (let [[doc args] (if (string? doc)
                     [doc args]
                     [nil (cons doc args)])]
    `(when ~'js/goog.DEBUG
       (swap! ~'chia.view.legacy.view-specs/spec-meta assoc ~kw {:doc ~doc})
       (clojure.spec.alpha/def ~kw ~@args))))

(defmacro consume [bindings & body]
  (loop [bindings (partition 2 bindings)
         out (cons 'do body)]
    (if-let [[ctx-sym ctx-k] (first bindings)]
      (recur (rest bindings)
             `(~'chia.view.legacy/consume*
               (~'chia.view.impl/lookup-context ~ctx-k)
               (fn [~ctx-sym] ~out)))
      out)))