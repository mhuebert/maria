(ns maria.editor.core
  (:require ["prosemirror-view" :as p.view]
            ["prosemirror-state" :as p.state]
            ["prosemirror-history" :refer [history]]
            ["prosemirror-dropcursor" :refer [dropCursor]]
            ["prosemirror-gapcursor" :refer [gapCursor]]
            ["prosemirror-schema-list" :as cmd-list]
            ["react" :as react]
            ["react-dom" :as react-dom]
            [applied-science.js-interop :as j]
            [applied-science.js-interop.alpha :refer [js]]
            [maria.cloud.persistence :as persist]
            [maria.cloud.menubar :as menu]
            [maria.editor.code.NodeView :as NodeView]
            [maria.editor.code.commands :as commands]
            [maria.editor.code.parse-clj :as parse-clj :refer [clj->md]]
            [maria.editor.code.sci :as sci]
            [maria.editor.keymaps :as keymaps]
            [maria.editor.prosemirror.input-rules :as input-rules]
            [maria.editor.prosemirror.links :as links]
            [maria.editor.prosemirror.schema :as markdown]
            [maria.editor.util :as u]
            [maria.ui :as ui]
            [re-db.reactive :as r]
            [yawn.hooks :as h]
            [yawn.view :as v]))

(defonce focused-state-key (new p.state/PluginKey "focused-state"))

(defn focused-state-plugin []
  (js
    (new p.state/Plugin {:key focused-state-key
                         :state {:init (fn [] false)
                                 :apply (fn [transaction prev-focus]
                                          (if-some [new-focus (.getMeta transaction focused-state-key)]
                                            new-focus
                                            prev-focus))}
                         :props {:handleDOMEvents
                                 {:blur
                                  (fn [view]
                                    (.dispatch view (.. view -state -tr (setMeta focused-state-key false)))
                                    false)
                                  :focus
                                  (fn [view]
                                    (.dispatch view (.. view -state -tr (setMeta focused-state-key true)))
                                    false)}}})))


(defn selection-decoration []
  (new p.state/Plugin (js
                        {:props
                         {:decorations
                          (fn [{:as ProseState :keys [doc] {:keys [from to]} :selection}]
                            (if (.getState focused-state-key ProseState)
                              (.-empty p.view/DecorationSet)
                              (.create p.view/DecorationSet doc
                                       [(.inline p.view/Decoration from to {:class "bg-selection"})])))}})))

(js
  (defn plugins []
    [keymaps/prose-keymap
     keymaps/default-keys
     input-rules/maria-rules
     (dropCursor)
     (gapCursor)
     (history)
     ~@(links/plugins)
     (focused-state-plugin)
     (selection-decoration)]))

(defonce !mounted-view (atom nil))

(defn init [{:as opts :keys [id
                             on-doc
                             default-value
                             make-sci-ctx]
             :or {make-sci-ctx sci/initial-context}}
            ^js element]
  {:pre [default-value]}
  (let [ProseState (js (.create p.state/EditorState {:doc (-> default-value
                                                              parse-clj/clj->md
                                                              markdown/md->doc)
                                                     :plugins (plugins)}))
        autosave! (persist/autosave-local-fn)
        ProseView (js (-> (p.view/EditorView. {:mount element}
                                              {:state ProseState
                                               :nodeViews {:code_block NodeView/editor}
                                               #_#_:handleDOMEvents {:blur #(js/console.log "blur" %1 %2)}
                                               ;; no-op tx for debugging
                                               :dispatchTransaction (fn [tx]
                                                                      (this-as ^js view
                                                                        (let [prev-state (.-state view)
                                                                              next-state (.apply prev-state tx)]
                                                                          (.updateState view next-state)
                                                                          (autosave! id prev-state next-state))))})
                          (j/assoc! :!sci-ctx (atom (make-sci-ctx))
                                    "file/id" id)))]
    (swap! keymaps/!context assoc :ProseView ProseView)
    (commands/prose:eval-doc! ProseView)
    (j/call ProseView :focus)
    #(do (swap! keymaps/!context u/dissoc-value :ProseView ProseView)
         (j/call ProseView :destroy))))

#_(defn ^:dev/before-load clear-console []
    (.clear js/console))

(defn use-global-doc-id! [id]
  (let [!content (ui/use-context ::menu/!content)]
    (h/use-effect
      (fn []
        (let [content (v/x [menu/doc-menu id])]
          (reset! !content content)
          #(swap! !content (fn [x]
                             (if (identical? x content)
                               nil
                               x)))))
      [id])))

(ui/defview editor [options]
  "Returns a ref for the element where the editor is to be mounted."
  (let [!ref (h/use-state nil)]
    ;; mount the editor
    (h/use-effect
      #(when-let [element @!ref]
         (init options element))
      [@!ref (:id options)])
    ;; set global doc id
    (use-global-doc-id! (:id options))
    [:div.relative.notebook.my-4 {:ref #(when % (reset! !ref %))}]))