(ns maria.editor.core
  (:require ["prosemirror-view" :as p.view]
            ["prosemirror-state" :as p.state]
            ["prosemirror-history" :refer [history]]
            ["prosemirror-dropcursor" :refer [dropCursor]]
            ["prosemirror-gapcursor" :refer [gapCursor]]
            ["prosemirror-schema-list" :as cmd-list]
            ["react" :as react]
            ["react-dom" :as react-dom]
            ["lodash.debounce" :as debounce]
            [applied-science.js-interop :as j]
            [applied-science.js-interop.alpha :refer [js]]
            [clojure.string :as str]
            [maria.cloud.menubar :as menu]
            [maria.cloud.persistence :as persist]
            [maria.cloud.routes :as routes]
            [maria.editor.code.NodeView :as NodeView]
            [maria.editor.code.commands :as commands]
            [maria.editor.code.parse-clj :as parse-clj :refer [clojure->markdown]]
            [maria.editor.code.sci :as sci]
            [maria.editor.keymaps :as keymaps]
            [maria.editor.prosemirror.input-rules :as input-rules]
            [maria.editor.prosemirror.links :as links]
            [maria.editor.prosemirror.schema :as schema]
            [maria.editor.util :as u]
            [maria.ui :as ui]
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

(def clj->doc
  "Returns a ProseMirror Markdown doc representing Clojure source code."
  (comp schema/markdown->doc parse-clj/clojure->markdown))

(defn doc->cells
  "Returns vector of code cells in a doc."
  [^js doc]
  (into []
        (comp (filter #(some->> (j/get-in % [:attrs :params])
                                (re-find #"^clj")))
              (map (j/get :textContent)))
        (j/get-in doc [:content :content])))


(defn use-doc-menu-content [doc-id]
  (let [!content (ui/use-context ::menu/!content)]
    (h/use-effect
      (fn []
        (let [content (v/x
                        [:<>
                         [menu/doc-menu doc-id]
                         [:div.flex-auto]])]
          (reset! !content content)
          #(swap! !content (fn [x]
                             (if (identical? x content)
                               nil
                               x)))))
      [doc-id])))

(defn autosave-local
  "Returns a callback that will save the current doc to local storage"
  [id ^js prev-state ^js next-state]
  (when-not (.eq (.-doc prev-state) (.-doc next-state))
    (swap! (persist/local-ratom id) assoc
           :file/source
           (persist/state-source next-state))
    ;; maybe: store the hash of the original source to detect changes
    #_(when-let [source (:file/source @(persist/$doc id))]
        {:file/source-hash (md5/hash source)})
    ))

(defn use-prose-view [{:keys [file/id
                              default-value]} deps]
  (let [!ref (h/use-state nil)
        ref-fn (h/use-callback #(when % (reset! !ref %)))
        !prose-view (h/use-state nil)
        autosave! (h/use-memo #(debounce autosave-local 1000 #js{:leading true :trailing true}))
        make-prose-view (fn [element]
                          (js (-> (p.view/EditorView. {:mount element}
                                                      {:state (js (.create p.state/EditorState
                                                                           {:doc (clj->doc default-value)
                                                                            :plugins (plugins)}))
                                                       :nodeViews {:code_block NodeView/editor}
                                                       :dispatchTransaction (fn [tx]
                                                                              (this-as ^js view
                                                                                (let [prev-state (.-state view)
                                                                                      next-state (.apply prev-state tx)]
                                                                                  (.updateState view next-state)
                                                                                  (autosave! id prev-state next-state))))})
                                  (j/assoc! :!sci-ctx (atom (sci/initial-context))))))]

    (h/use-effect
      ;; save local state immediately when tab is hidden
      (fn []
        (let [cb (fn []
                   (when (j/get js/document :hidden)
                     (j/call autosave! :flush)))]
          (when @!prose-view
            (js/document.addEventListener "visibilitychange" cb)
            #(do (j/call autosave! :flush)
                 (js/document.removeEventListener "visibilitychange" cb)))))
      [@!prose-view])

    (h/use-effect
      (fn []
        (if-let [element (and default-value @!ref)]
          (let [ProseView (make-prose-view element)]
            (reset! !prose-view ProseView)
            #(j/call ProseView :destroy))
          (reset! !prose-view nil)))
      (conj deps @!ref))
    [@!prose-view ref-fn]))

(ui/defview editor* [params {:as file :keys [file/id]}]
  "Returns a ref for the element where the editor is to be mounted."

  (persist/use-persisted-file file)
  (use-doc-menu-content id)
  (persist/use-recents! (::routes/path params) file)

  (let [[ProseView ref-fn] (use-prose-view {:file/id id
                                            :default-value (or (:file/source @(persist/local-ratom id))
                                                               (:file/source file)
                                                               "")}
                                           [])]

    ;; initialize new editors
    (h/use-effect
      (fn []
        (when (some-> ProseView (u/guard (complement (j/get :isDestroyed))))
          (keymaps/add-context :ProseView ProseView)
          (when-not (str/includes? js/window.location.href "eval=false")
            (commands/prose:eval-prose-view! ProseView))
          (j/call ProseView :focus)))
      [ProseView])

    ;; set file/id context
    (h/use-effect (fn []
                    (keymaps/add-context :file/id id)
                    #(keymaps/remove-context :file/id id)))
    [:div.relative.notebook.my-4 {:ref ref-fn}]))

(ui/defview editor
  {:key (fn [params file] (:file/id file))}
  [params file]
  (if file
    [editor* params file]
    [:div.circle-loading.m-2 [:div] [:div]]))