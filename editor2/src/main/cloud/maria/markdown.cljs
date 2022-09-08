(ns cloud.maria.markdown
  (:require [applied-science.js-interop :as j]
            [tools.maria.hooks :as hooks]
            [cloud.maria.input-rules :as input-rules]
            [cloud.maria.keymap :as keys]
            ["react" :as re]
            ["react-dom/client" :refer [createRoot]]
            ["prosemirror-view" :refer [EditorView]]
            ["prosemirror-state" :refer [EditorState]]
            ["prosemirror-markdown" :as md]
            ["prosemirror-example-setup" :refer [exampleSetup]]
            ["prosemirror-history" :refer [history]]
            ["prosemirror-dropcursor" :refer [dropCursor]]
            ["prosemirror-gapcursor" :refer [gapCursor]]
            ["prosemirror-schema-list" :as cmd-list]))

(defn parse [source] (.parse md/defaultMarkdownParser source))
(defn serialize [doc] (.serialize md/defaultMarkdownSerializer doc))

(defn plugins []
  (array
   keys/default-keys
   keys/maria-keys
   input-rules/maria-rules
   (dropCursor)
   (gapCursor)
   (history)))

(j/lit∞
 (defn init-prosemirror-view! [{:keys [element
                                       source]}]
   (let [state (.create EditorState {:doc (parse source)
                                     :plugins (plugins)})
         view (EditorView. element {:state state})]
     view)))

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
