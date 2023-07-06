(ns maria.editor.core
  (:require ["prosemirror-view" :refer [EditorView]]
            ["prosemirror-state" :refer [EditorState]]
            ["prosemirror-history" :refer [history]]
            ["prosemirror-dropcursor" :refer [dropCursor]]
            ["prosemirror-gapcursor" :refer [gapCursor]]
            ["prosemirror-schema-list" :as cmd-list]
            ["react" :as react]
            ["react-dom" :as react-dom]
            [applied-science.js-interop :as j]
            [applied-science.js-interop.alpha :refer [js]]
            [maria.editor.code-blocks.NodeView :as node-view]
            [maria.editor.code-blocks.commands :as commands]
            [maria.editor.code-blocks.parse-clj :as parse-clj :refer [clj->md]]
            [maria.editor.code-blocks.sci :as sci]
            [maria.editor.keymaps :as keymaps]
            [maria.editor.prosemirror.input-rules :as input-rules]
            [maria.editor.prosemirror.links :as links]
            [maria.editor.prosemirror.schema :as markdown]))

(js
  (defn plugins []
    [keymaps/prose-keymap
     keymaps/default-keys
     input-rules/maria-rules
     (dropCursor)
     (gapCursor)
     (history)
     ~@(links/plugins)]))

(defonce !mounted-view (atom nil))

(defn init [{:as opts :keys [initial-value
                             make-sci-ctx]
             :or {make-sci-ctx sci/initial-context}}
            ^js element]
  (let [state (js (.create EditorState {:doc     (-> initial-value
                                                     parse-clj/clj->md
                                                     markdown/md->doc)
                                        :plugins (plugins)}))
        view (-> (js (EditorView. {:mount element}
                                  {:state     state
                                   :nodeViews {:code_block node-view/editor}
                                   #_#_:handleDOMEvents {:blur #(js/console.log "blur" %1 %2)}
                                   ;; no-op tx for debugging
                                   #_#_:dispatchTransaction (fn [tx]
                                                              (this-as ^js view
                                                                (let [state (.apply (.-state view) tx)]
                                                                  (.updateState view state))))}))
                 (j/assoc! :!sci-ctx (atom (make-sci-ctx))))]
    (reset! !mounted-view view)
    (commands/prose:eval-doc! view)
    #(do (when (identical? @!mounted-view view) (reset! !mounted-view nil))
         (j/call view :destroy))))

#_(defn ^:dev/before-load clear-console []
    (.clear js/console))
