(ns maria.code.editor
  (:require ["@codemirror/state" :as state :refer [EditorState]]
            ["@codemirror/view" :as view :refer [EditorView]]
            ["@codemirror/commands" :as cmd]
            ["@codemirror/language" :as lang]
            ["lezer-clojure" :as lezer-clj]
            ["@lezer/highlight" :as highlight :refer [tags]]
            ["react" :as re]
            [applied-science.js-interop :as j]
            [nextjournal.clojure-mode :as clj-mode]
            [maria.code.node-view :as node-view]
            [maria.style :as style]))

(j/js

  (def default-extensions
    [(.define lang/LRLanguage
              {:parser (.configure lezer-clj/parser
                                   {:props #js [clj-mode/format-props
                                                (.add lang/foldNodeProp clj-mode/fold-node-props)
                                                style/code-styles]})})
     (clj-mode/match-brackets)
     (clj-mode/close-brackets)
     (clj-mode/selection-history)
     (clj-mode/format-changed-lines)
     (clj-mode/eval-region {:modifier "Alt"})])

  (def extensions
    [default-extensions
     (.theme EditorView style/code-theme)
     (cmd/history)
     (.. EditorState -allowMultipleSelections (of true))
     (lang/syntaxHighlighting style/code-highlight-style)
     (lang/syntaxHighlighting lang/defaultHighlightStyle)

     (.of view/keymap clj-mode/complete-keymap)
     (.of view/keymap cmd/historyKeymap)

     ])

  (defn editor [{:as prose-node :keys [textContent]} prose-view prose-pos]
    (let [this {:prose-pos prose-pos
                :prose-view prose-view
                :prose-node prose-node
                :eval-value (atom nil)}
          cm (new EditorView
                  {:state
                   (.create EditorState
                            {:doc textContent
                             :extensions [extensions
                                          (node-view/extensions this)]})})]
      (node-view/init this cm))))
