(ns chia.view.server
  (:require ["react-dom/server" :as ssr]
            [chia.reactive :as r]))

(defn non-reactive-render [f]
  (fn [element]
    (binding [r/*non-reactive* true]
      (f element))))

(def html (non-reactive-render ssr/renderToString))
(def static-html (non-reactive-render ssr/renderToStaticMarkup))

(def node-stream (non-reactive-render ssr/renderToNodeStream))
(def static-node-stream (non-reactive-render ssr/renderToStaticNodeStream))