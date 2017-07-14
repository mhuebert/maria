(ns ^:figwheel-always re-view.core
  (:refer-clojure :exclude [defn])
  (:require [clojure.string :as string]
            [re-view.util :refer [camelCase]]))

(clojure.core/defn- js-obj-with-set!
  "Convert a Clojure map to javascript object using `set!`, to play well with Closure Compiler.
  Keys are converted to camelCase. Shallow."
  [m]
  (when-let [m (seq m)]
    (let [sym (gensym)
          exprs (map (fn [[k v]]
                       `(~'set! (~(symbol (str ".-" (camelCase (name k)))) ~sym) ~v)) m)]
      `(let [~sym (~'js-obj)]
         ~@exprs
         ~sym))))

#_(clojure.core/defn- js-obj-camelCase
    "Return a javascript object for m with keys as camelCase strings (keys will not be recognized by Closure compiler)."
    [m]
    (when-let [m (seq m)]
      `(~'js-obj ~@(mapcat (fn [[k v]] (list (camelCase (name k)) v)) m))))

(clojure.core/defn group-methods
  "Groups methods by role in a React component."
  [methods]
  (-> (reduce-kv (fn [m k v]
                   (assoc-in m [(case (namespace k)
                                  "life" :lifecycle-keys
                                  "react" :react-keys
                                  ("spec" "view") :class-keys
                                  (case k
                                    (:key :display-name)
                                    :react-keys
                                    :instance-keys)) k] v)) {} methods)
      ;; instance keys are accessed via dot notation.
      ;; must use set! for the keys, otherwise they will
      ;; be modified in advanced compilation.
      (update :instance-keys js-obj-with-set!)

      ;; this won't last - currently building :view/default-props
      ;; in the macro so there's no way to reuse specs.
      ))

(clojure.core/defn parse-opt-args [preds args]
  (loop [preds preds
         args args
         out []]
    (if (empty? preds)
      (conj out args)
      (let [match? ((first preds) (first args))]
        (recur (rest preds)
               (cond-> args match? (rest))
               (conj out (if match? (first args) nil)))))))

(def parse-view-args (partial parse-opt-args [symbol? string? map?]))

(clojure.core/defn display-name
  "Generate a meaningful name to identify React components while debugging"
  [ns given-name]
  (str (last (string/split (name (ns-name ns)) #"\.")) "/" given-name))

(clojure.core/defn wrap-body
  "Wrap body in anonymous function form."
  [name [args & body]]
  (assert (vector? args))
  `(~'fn ~name ~args
     (do ~@(drop-last body)
         (~'re-view-hiccup.core/element ~(last body)))))

(defmacro defview
  "Define a view function.

   Expects optional docstring and methods map, followed by
    the argslist and body for the render function, which should
    return a Hiccup vector or React element."
  [& args]
  (let [[view-name docstring methods body] (parse-view-args args)
        _ (assert (symbol? view-name))
        methods (-> methods
                    (merge {:react/docstring docstring
                            :display-name    (display-name *ns* view-name)
                            :life/render     (wrap-body view-name body)})
                    (group-methods))]
    `(def ~view-name ~@(some-> docstring (list)) (~'re-view.core/view* ~methods))))

(defmacro view
  "Returns anonymous view, given the same args as `defview`."
  [& args]
  (let [[view-name docstring methods body] (parse-view-args args)
        methods (-> methods
                    (merge {:react/docstring docstring
                            :display-name    (display-name *ns* view-name)
                            :life/render     (wrap-body view-name body)})
                    (group-methods))]
    `(~'re-view.core/view* ~methods)))

(defmacro defn
  "Defines a stateless view function"
  [& args]
  (let [[view-name docstring methods body] (parse-view-args args)]
    `(clojure.core/defn ~view-name ~@(if docstring [docstring] [])
       [& args#]
       (apply ~(wrap-body view-name body) (if (map? (first args#)) args# (cons {} args#))))))

(comment
  (assert (= (parse-view-args '(name "a" {:b 1} [c] 1 2))
             '[name "a" {:b 1} ([c] 1 2)]))

  (assert (= (parse-view-args '(name {} [] 1 2))
             '[name nil {} ([] 1 2)]))

  (assert (= (parse-view-args '(name "a" [] 1 2))
             '[name "a" nil ([] 1 2)]))

  (assert (= (parse-view-args '(name [] 1 2))
             '[name nil nil ([] 1 2)]))

  (assert (= (parse-view-args '(name []))
             '[name nil nil ([])])))
