(ns chia.view.impl
  (:require ["react" :as react]
            ["react-dom" :as react-dom]
            [applied-science.js-interop :as j]
            [chia.view.util :as u]
            [chia.reactive :as r]))


;; Hook utils

(defn- wrap-effect [f]
  (fn []
    (let [destroy (f)]
      (if (fn? destroy)
        destroy
        js/undefined))))

;; ReactDOM

(def -render react-dom/render)
(def -unmount-component-at-node react-dom/unmountComponentAtNode)

(defn resolve-node [node-or-id]
  (cond->> node-or-id
           (string? node-or-id)
           (u/find-or-append-element)))


;; Context

(def ^:private kw-context-cache
  (memoize (fn [k] (react/createContext))))

(defn lookup-context [k]
  {:pre [(or (object? k) (qualified-keyword? k))]}
  (if (object? k) k (kw-context-cache k)))


;; View memoization

(defn- args-not= [x y]
  (not= (j/get x :children)
        (j/get y :children)))

(defn memoize-view
  "Returns a memoized version of view `f` with optional `should-update?` function.

  - By default, arguments are compared with cljs equality.
  - During dev reload, all components re-render.
  - A no-op in node.js"
  ([f]
   (memoize-view f args-not=))
  ([f should-update?]
   (react/memo f (fn equal? [x y]
                   (not (should-update? (j/get x :children)
                                        (j/get y :children)))))))