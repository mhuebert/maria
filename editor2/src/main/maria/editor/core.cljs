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
            [maria.editor.code.NodeView :as NodeView]
            [maria.editor.code.commands :as commands]
            [maria.editor.code.parse-clj :as parse-clj :refer [clj->md]]
            [maria.editor.code.sci :as sci]
            [maria.editor.keymaps :as keymaps]
            [maria.cloud.persistence :as persist]
            [maria.editor.prosemirror.input-rules :as input-rules]
            [maria.editor.prosemirror.links :as links]
            [maria.editor.prosemirror.schema :as markdown]
            [yawn.hooks :as h]
            [maria.cloud.local :as local]))

(defonce focused-state-key (new p.state/PluginKey "focused-state"))

(defn focused-state-plugin []
  (js
    (new p.state/Plugin {:key   focused-state-key
                         :state {:init  (fn [] false)
                                 :apply (fn [transaction prev-focus]
                                          (if-some ^:clj [new-focus (.getMeta transaction focused-state-key)]
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
                             persisted-value
                             make-sci-ctx]
             :or {make-sci-ctx sci/initial-context}}
            ^js element]
  ;; TODO
  ;; think through how we access local/persisted values for a doc
  (let [ProseState (js (.create p.state/EditorState {:doc     (-> (or (local/get id) persisted-value)
                                                                  parse-clj/clj->md
                                                                  markdown/md->doc)
                                                     :plugins (plugins)}))
        autosave! (persist/autosave-fn)
        ProseView (js (-> (p.view/EditorView. {:mount element}
                                              {:state               ProseState
                                               :nodeViews           {:code_block NodeView/editor}
                                               #_#_:handleDOMEvents {:blur #(js/console.log "blur" %1 %2)}
                                               ;; no-op tx for debugging
                                               :dispatchTransaction (fn [tx]
                                                                      (this-as ^js view
                                                                        (let [prev-state (.-state view)
                                                                              next-state (.apply prev-state tx)]
                                                                          (.updateState view next-state)
                                                                          (autosave! id prev-state next-state))))})
                          (j/assoc! :!sci-ctx (atom (make-sci-ctx)))))]
    (reset! !mounted-view ProseView)
    (commands/prose:eval-doc! ProseView)
    #(do (when (identical? @!mounted-view ProseView) (reset! !mounted-view nil))
         (j/call ProseView :destroy))))

(defn use-editor
  "Returns a ref for the element where the editor is to be mounted."
  [options]
  (let [!ref (h/use-ref nil)]
    (h/use-effect
      (fn []
        (when-let [element @!ref]
          (init options element)))
      [@!ref (:id options)])
    !ref))

#_(defn ^:dev/before-load clear-console []
    (.clear js/console))
