(ns cloud.maria
  (:require ["react-dom/client" :as react.client]
            [tools.maria.dom :as dom]
            [cloud.maria.markdown :as markdown]
            [reagent.core :as reagent]
            [applied-science.js-interop :as j]))


(defonce !root (delay (react.client/createRoot
                       (dom/find-or-create-element :prosemirror-markdown-demo))))

(defonce fn-compiler (doto (reagent/create-compiler {:function-components true})
                       (reagent/set-default-compiler!)))

(defn ^:dev/after-load render []
  (j/call @!root :render
    (reagent/as-element
     [markdown/editor {:source "# Hello, world..."}])))