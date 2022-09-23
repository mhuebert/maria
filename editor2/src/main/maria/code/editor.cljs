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
            [maria.style :as style]
            [maria.eval.sci :as sci]
            [maria.keymap :as keys]))

(defn eval-string! [!result source]
  ;; TODO - handle errors
  (reset! !result (sci/eval-string source)))

(j/js

  (defn editor [{:as prose-node :keys [textContent]} prose-view get-node-pos]
    (let [on-mounts []
          {:as this :keys [!result]} {:get-node-pos get-node-pos
                                      :prose-view prose-view
                                      :prose-node prose-node
                                      :eval-value (atom nil)
                                      :!result (atom nil)}
          _ (j/extend! this {:mounted? false
                             :mounted! (fn []
                                         (j/!set this :mounted? true)
                                         ^:clj (doseq [f on-mounts] (f)))
                             :on-mount (fn [f] (if (j/get this :mounted?)
                                                 (f)
                                                 (j/push! on-mounts f)))})
          eval-modifier "Alt"
          cm (new EditorView
                  {:state
                   (.create EditorState
                            {:doc textContent
                             :extensions [(.define lang/LRLanguage
                                                   {:parser (.configure lezer-clj/parser
                                                                        {:props #js [clj-mode/format-props
                                                                                     (.add lang/foldNodeProp clj-mode/fold-node-props)
                                                                                     style/code-styles]})})
                                          (clj-mode/match-brackets)
                                          (clj-mode/close-brackets)
                                          (clj-mode/selection-history)

                                          (clj-mode/format-changed-lines)
                                          (.theme EditorView style/code-theme)
                                          (cmd/history)
                                          (.. EditorState -allowMultipleSelections (of true))
                                          (lang/syntaxHighlighting style/code-highlight-style)
                                          (lang/syntaxHighlighting lang/defaultHighlightStyle)

                                          (clj-mode/eval-region ^:clj {:modifier eval-modifier
                                                                       :eval-string! (partial eval-string! !result)})
                                          (keys/code-keys this)
                                          (.of view/keymap clj-mode/complete-keymap)
                                          (.of view/keymap cmd/historyKeymap)

                                          (.. EditorView
                                              -updateListener
                                              (of #(node-view/code:forward-update this %)))]})})]
      (node-view/init this cm))))
