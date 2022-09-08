(ns cloud.maria
  (:require ["react-dom/client" :as react.client]
            [applied-science.js-interop :as j]
            [cloud.maria.markdown :as markdown]
            cloud.maria.parse-clojure
            [tools.maria.dom :as dom]
            [reagent.core :as reagent]))


(defonce !root (delay (react.client/createRoot
                       (dom/find-or-create-element :prosemirror-markdown-demo))))

(defonce fn-compiler (doto (reagent/create-compiler {:function-components true})
                       (reagent/set-default-compiler!)))

(defn ^:dev/after-load render []
  (j/call @!root :render
    (reagent/as-element
     [markdown/editor {:source "# Hello, world..."}])))