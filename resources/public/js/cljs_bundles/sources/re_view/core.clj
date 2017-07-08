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
                                  "view" :class-keys
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

(clojure.core/defn parse-view-args
  "Parse args for optional docstring and method map"
  [args]
  (let [out [(if (string? (first args)) (first args) nil)]
        args (cond-> args (string? (first args)) (next))
        out (conj out (if (map? (first args)) (first args) nil))
        args (cond-> args (map? (first args)) (next))]
    (conj out (first args) (next args))))

(clojure.core/defn display-name
  "Generate a meaningful name to identify React components while debugging"
  ([ns] (str (gensym (display-name ns "view"))))
  ([ns given-name]
   (str (last (string/split (name (ns-name ns)) #"\.")) "/" given-name)))

(clojure.core/defn wrap-body
  "Wrap body in anonymous function form."
  [args body]
  `(~'fn ~args (~'re-view-hiccup.core/element (do ~@body))))

(defmacro defview
  "Define a view function.

   Expects optional docstring and methods map, followed by
    the argslist and body for the render function, which should
    return a Hiccup vector or React element."
  [view-name & args]
  (let [[docstring methods args body] (parse-view-args args)
        methods (-> methods
                    (merge {:react/docstring docstring
                            :display-name    (display-name *ns* view-name)
                            :life/render     (wrap-body args body)})
                    (group-methods))]
    `(def ~view-name ~docstring (~'re-view.core/view* ~methods))))

(defmacro view
  "Returns anonymous view, given the same args as `defview`."
  [& args]
  (let [[docstring methods args body] (parse-view-args args)
        methods (-> methods
                    (merge {:react/docstring docstring
                            :display-name    (display-name *ns*)
                            :life/render     (wrap-body args body)})
                    (group-methods))]
    `(~'re-view.core/view* ~methods)))

(defmacro defn
  "Defines a stateless view function"
  [name & args]
  (let [[docstring methods args body] (parse-view-args args)]
    `(def ~name ~@[docstring]
       (~'fn [& args#]
         (let [~args (if (map? (first args#)) args# (cons {} args#))]
           (~'re-view-hiccup.core/element (do ~@body)))))))

(comment
  (assert (= (parse-view-args '("a" {:b 1} [c] 1 2))
             '["a" {:b 1} [c] (1 2)]))

  (assert (= (parse-view-args '({} [] 1 2))
             '[nil {} [] (1 2)]))

  (assert (= (parse-view-args '("a" [] 1 2))
             '["a" nil [] (1 2)]))

  (assert (= (parse-view-args '([] 1 2))
             '[nil nil [] (1 2)]))

  (assert (= (parse-view-args '([]))
             '[nil nil [] nil])))
