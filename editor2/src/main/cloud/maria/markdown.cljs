(ns cloud.maria.markdown
  (:require [applied-science.js-interop :as j]
            [tools.maria.hooks :as hooks]
            ["react" :as re]
            ["react-dom/client" :refer [createRoot]]
            ["prosemirror-view" :refer [EditorView]]
            ["prosemirror-state" :refer [EditorState]]
            ["prosemirror-markdown" :as md]
            ["prosemirror-example-setup" :refer [exampleSetup]]))

(defn parse [source] (.parse md/defaultMarkdownParser source))
(defn serialize [doc] (.serialize md/defaultMarkdownSerializer doc))

(defn init-prosemirror-view! [{:keys [element
                                      source]}]
  (let [plugins (exampleSetup #js{:schema md/schema})
        state (.create EditorState #js{:doc (parse source)
                                       :plugins plugins})
        view (EditorView. element #js{:state state})]
    view))

(defn editor [{:keys [source]}]
  (j/let [!view (hooks/ref nil)
          ref-fn (re/useCallback
                  (fn [el]
                    (some-> @!view (j/call :destroy))
                    (when el
                      (reset! !view (init-prosemirror-view! {:element el
                                                             :source source})))
                    nil))]
    [:div {:ref ref-fn}]))

