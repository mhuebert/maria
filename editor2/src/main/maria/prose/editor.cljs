(ns maria.prose.editor
  (:require [applied-science.js-interop :as j]
            [tools.maria.component :refer [with-element]]
            [tools.maria.hooks :as hooks]
            [maria.prose.input-rules :as input-rules]
            [maria.keymap :as keys]
            [maria.code.editor :as code-editor]
            [maria.style :as style]
            [maria.code.parse-clj :as parse-clj]
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

(defn md->doc [source] (.parse md/defaultMarkdownParser source))
(defn doc->md [doc] (.serialize md/defaultMarkdownSerializer doc))

(defn plugins []
  #js[keys/default-keys
      keys/prose-keys
      input-rules/maria-rules
      (dropCursor)
      (gapCursor)
      (history)])

(defn editor [{:keys [source]}]
  (with-element {:el style/prose-element}
    (fn [element]
      (j/js
        (let [state (.create EditorState {:doc (-> source
                                                   parse-clj/source->md
                                                   md->doc)
                                          :plugins (plugins)})
              view (EditorView. element {:state state
                                         :nodeViews {:code_block code-editor/editor}})]
          #(j/call view :destroy))))))
