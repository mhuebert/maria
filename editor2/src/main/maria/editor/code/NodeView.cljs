(ns maria.editor.code.NodeView
  (:require ["@codemirror/commands" :as cmd]
            ["@codemirror/language" :as lang]
            ["@codemirror/state" :refer [EditorState]]
            ["@codemirror/view" :as cm.view :refer [EditorView]]
            ["@nextjournal/lezer-clojure" :as lezer-clojure]
            ["prosemirror-history" :as history]
            ["prosemirror-state" :as pm.state :refer [TextSelection Selection]]
            ["react" :as react]
            [maria.editor.code.show-values :refer [show]]
            [maria.editor.icons :as icons]
            [shadow.cljs.modern :refer [defclass]]
            [applied-science.js-interop :as j]
            [applied-science.js-interop.alpha :refer [js]]
            [clojure.string :as str]
            [maria.editor.code.commands :as commands]
            [maria.editor.code.completions :as completions]
            [maria.editor.code.docbar :as eldoc]
            [maria.editor.code.error-marks :as error-marks]
            [maria.editor.code.eval-region :as eval-region]
            [maria.editor.code.styles :as styles]
            [maria.editor.keymaps :as keymaps]
            [shapes.core :as shapes]
            [maria.editor.prosemirror.schema :as prose-schema]
            [nextjournal.clojure-mode :as clj-mode]
            [nextjournal.clojure-mode.extensions.close-brackets :as close-brackets]
            [nextjournal.clojure-mode.extensions.formatting :as formatting]
            [nextjournal.clojure-mode.extensions.match-brackets :as match-brackets]
            [nextjournal.clojure-mode.extensions.selection-history :as sel-history]
            [nextjournal.clojure-mode.util :as u]
            [re-db.reactive :as r]
            [yawn.hooks :as h]
            [yawn.root :as root]
            [yawn.view :as v]))

(def ^:dynamic *selecting-node* false)

(defonce !focused-view (atom nil))

(js
  (defn focus! [{:as this :keys [CodeView]}]
    (if (j/get this :mounted?)
      (.focus CodeView)
      (j/assoc! this :initially-focused? true))))

(js
  (defn set-initial-focus!
    "If ProseMirror cursor is within code view, focus it."
    [{:as this :keys [getPos ProseView CodeView initially-focused?]}]
    (if initially-focused?
      (.focus CodeView)
      (let [cursor (dec (.. ProseView -state -selection -$anchor -pos))
            start (getPos)
            length (.. CodeView -state -doc -length)
            end (+ (getPos) length)]
        (when (and (>= cursor start)
                   (< cursor end))
          (.focus CodeView))))
    this))

(js
  (defn code:forward-update
    "Forward events from CodeMirror to ProseMirror (when the code-editor is focused)"
    [{:keys [CodeView ProseView getPos code-updating?]} code-update]
    (let [{prose-state :state} ProseView
          focus-changed? (.-focusChanged code-update)
          has-focus? (.-hasFocus CodeView)]

      (when focus-changed?
        (if (.-hasFocus CodeView)
          (reset! !focused-view CodeView)
          (when (identical? CodeView @!focused-view)
            (reset! !focused-view nil))))

      (when (and has-focus? (not code-updating?))
        (let [start-pos (inc (getPos))
              {from' :from to' :to} (.. code-update -state -selection -main)
              {code-changed? :docChanged
               code-changes :changes} code-update
              {:keys [tr doc]} prose-state
              selection-changed? (not (.eq (.. code-update -startState -selection)
                                           (.. code-update -state -selection)))
              #_(not (.eq (.. prose-state -selection)
                          (.create TextSelection
                                   doc
                                   (+ start-pos from')
                                   (+ start-pos to'))))]
          (comment
            ;; or we could compare the selection inside codemirror?
            (not (.eq (.. code-update -startState -selection)
                      (.. code-update -state -selection))))

          (when (or code-changed? selection-changed?)

            (when code-changed?
              (let [!offset (volatile! start-pos)]
                (.iterChanges code-changes
                              (fn [from-a to-a from-b to-b {:as text :keys [length]}]
                                (let [offset @!offset]
                                  (if (pos-int? length)
                                    (.replaceWith tr
                                                  (+ offset from-a)
                                                  (+ offset to-a)
                                                  (.text prose-schema/schema (.toString text)))
                                    (.delete tr
                                             (+ offset from-a)
                                             (+ offset to-a))))
                                ;; adjust offset for changes in length caused by the change,
                                ;; so further steps are in correct position
                                (vswap! !offset + (- (- to-b from-b)
                                                     (- to-a from-a)))))))

            (when selection-changed?
              (.setSelection tr (.create TextSelection
                                         (.-doc tr)
                                         (+ start-pos from')
                                         (+ start-pos to'))))
            (.dispatch ProseView tr)))))))

(defn- controlled-update [this f]
  (j/!set this :code-updating? true)
  (f)
  (j/!set this :code-updating? false))

(defn code-text [^js CodeView] (.. CodeView -state -doc (toString)))

(js
  (defn prose:set-CodeView-selection
    "Called when ProseMirror tries to put the selection inside the node."
    [{:as this :keys [CodeView dom]} anchor head]
    (controlled-update this
                       #(do (.dispatch CodeView {:selection {:anchor anchor
                                                             :head head}})
                            (when-not (.contains dom (.. js/document (getSelection) -focusNode))
                              (focus! this))))))
(js
  (defn text-diff [old-text new-text]
    (let [old-end (.-length old-text)
          new-end (.-length new-text)
          start (loop [start 0]
                  (if (and (< start old-end)
                           (== (.charCodeAt old-text start)
                               (.charCodeAt new-text start)))
                    (recur (inc start))
                    start))]
      (loop [old-end old-end
             new-end new-end]
        (if (and (> old-end start)
                 (> new-end start)
                 (== (.charCodeAt old-text (dec old-end))
                     (.charCodeAt new-text (dec new-end))))
          (recur (dec old-end)
                 (dec new-end))
          {:from start
           :to old-end
           :insert (.slice new-text start new-end)})))))

(js
  (defn prose:forward-update [{:as this :keys [CodeView]
                               prev-node :proseNode} new-node]
    (if *selecting-node*
      true
      (boolean
        (when (= (.-type prev-node)
                 (.-type new-node))
          (j/!set this :proseNode new-node)
          (let [new-text (.-textContent new-node)
                old-text (code-text CodeView)]
            (when (not= new-text old-text)
              (controlled-update this
                                 (fn []
                                   (.dispatch CodeView {:changes (text-diff old-text new-text)
                                                        :annotations [(u/user-event-annotation "noformat")]})))))
          true)))))

(def language
  (.define lang/LRLanguage
           (js
             {:parser (.configure lezer-clojure/parser
                                  {:props [formatting/props
                                           (.add lang/foldNodeProp clj-mode/fold-node-props)
                                           styles/code-styles]})})))

(js
  (defn mount-code-view! [el {:as this
                              :keys [mounted?]
                              {cm-dom :dom} :CodeView}]
    (.appendChild el cm-dom)
    (doto (.-classList cm-dom)
      (.add "rounded-r")
      (.add "overflow-hidden"))
    (-> this
        (j/assoc! :mounted? true)
        set-initial-focus!)))


(defclass ErrorBoundary
  (extends react/Component)
  (constructor [this] (super))
  Object
  (render [^js this]
          (j/let [^js {{:keys [error]} :state
                       {:keys [render value]} :props} this]
            (try (render (or error value))
                 (catch js/Error e
                   (js/console.debug (.-stack e))
                   (render e))))))

(j/!set ErrorBoundary :getDerivedStateFromError (fn [error] #js{:error error}))

(defn shape? [x] (instance? shapes/Shape x))

(v/defview value-viewer [this]
  (let [{:keys [value error key]} (h/use-deref (j/get this :!result))
        opts {:NodeView this
              :sci/context @(j/get-in this [:ProseView :!sci-ctx])
              :sci/get-ns #(commands/code:ns this)}]
    [:... {:key key}
     (if error
       (show opts error)
       (j/lit [ErrorBoundary {:key key
                              :render (fn [x]
                                        (show opts x))
                              :value value}]))]))

(defn temporarily-disable-contenteditable [start-event end-event]
  ;; Ugly hack: temporarily disable contenteditable when users interact with ".value-viewer" regions,
  ;; to prevent iOS keyboard from showing up & scrolling to unwanted regions of the document.
  ;; (I tried using `stopPropagation` on value-viewer nodes and that did not seem to work;
  ;;  maybe there's still a better way.)
  (.addEventListener js/window start-event
                     (fn [^js e]
                       (when-let [value-viewer (.. e -target (closest ".value-viewer"))]
                         (let [notebook (.closest value-viewer ".notebook")]
                           (j/!set notebook :contentEditable "false")
                           (.addEventListener js/window end-event
                                              (fn listener [e]
                                                (j/!set notebook :contentEditable "true")
                                                (.removeEventListener js/window start-event listener))
                                              true))))
                     true))

(defonce _
         (do
           (temporarily-disable-contenteditable "touchstart" "touchend")
           (temporarily-disable-contenteditable "mousedown" "mouseup")))

(v/defview code-row [^js {:as this :keys [!result !ui-state id CodeView]}]
  (let [ref (h/use-callback (fn [el]
                              (when el (mount-code-view! el this))))
        {:keys [hide-source node-selected?]} (h/use-deref !ui-state)
        hide-source? (if (some? hide-source)
                       hide-source
                       (str/includes? (.. CodeView -state -doc (line 1) -text) "^:hide-source"))
        toggle-classes (v/classes ["z-10 w-6 h-6"
                                   "inline-flex items-center justify-center"
                                   "text-zinc-400 hover:text-zinc-700"
                                   "cursor-pointer"
                                   "rounded-full bg-white"
                                   "focus:ring"])]
    [:<>
     [:div {:class ["w-full md:w-1/2 relative text-base flex rounded-none sm:rounded"
                    (when-not hide-source? "bg-white")]}
      (when-not hide-source?
        [:div {:class ["flex-auto text-base relative text-brackets"
                       (when node-selected? "ring ring-2 ring-selection rounded-r ring-l-none")]
               :ref ref
               :id id}])
      [:div.flex.flex-col.w-6.items-center.gap-1.ml-auto
       (if hide-source?
         [:button
          {:on-click #(swap! !ui-state assoc :hide-source false)
           :class [toggle-classes "mr-[6px] shadow"]}
          (icons/code-bracket:mini "w-4 h-4")]
         [:div
          {:on-click #(swap! !ui-state assoc :hide-source true)
           :class [toggle-classes "opacity-70 focus:opacity-100 hover:opacity-100 transition-opacity"]}
          (icons/minus-small:mini "w-5 h-5")])]]
     [:div.value-viewer
      [value-viewer this]
      [:div.ml-auto.flex.flex-col.items-center
       [:div.text-slate-300.hover:text-slate-600.z-5.mr-1
        {:on-click #(commands/code:eval-NodeView! this)}
        [icons/play-circle "w-8 h-8 sm:hidden sm:hidden"]]]]]))

(j/defn select-node [^js {:keys [!ui-state ProseView CodeView]}]
  (when-not *selecting-node*
    (binding [*selecting-node* true]
      (swap! !ui-state assoc :node-selected? true)
      (.focus ProseView))))

(js
  (defn editor [{:as proseNode :keys [textContent]} ProseView getPos]
    (let [el (doto (js/document.createElement "div")
               (.. -classList (add "my-4" "md:flex" "NodeView")))
          this (j/obj :id (str (gensym "code-view-")))
          root (root/create el (code-row this))
          !ui-state (atom ^:clj {})]
      (j/extend! this
                 {:initialNs (str/starts-with? textContent "(ns ")
                  :getPos getPos
                  :ProseView ProseView
                  :proseNode proseNode
                  :CodeView (-> (new EditorView
                                     {:state
                                      (.create EditorState
                                               {:doc textContent
                                                :extensions [language
                                                             (.. language -data (of {:autocomplete (fn [context]
                                                                                                     (#'completions/completions this context))}))
                                                             (completions/plugin)

                                                             (close-brackets/extension)
                                                             (match-brackets/extension)
                                                             (sel-history/extension)
                                                             (formatting/ext-format-changed-lines)

                                                             (.theme EditorView styles/code-theme)
                                                             (cmd/history)

                                                             (lang/syntaxHighlighting styles/code-highlight-style)
                                                             (lang/syntaxHighlighting lang/defaultHighlightStyle)

                                                             (eval-region/extension ^:clj {:on-enter #(do (commands/code:eval-NodeView! this)
                                                                                                          true)})
                                                             keymaps/code-keymap
                                                             (.of cm.view/keymap cmd/historyKeymap)

                                                             (.. EditorState -allowMultipleSelections (of true))

                                                             #_(cm.view/drawSelection)

                                                             (.. EditorView
                                                                 -updateListener
                                                                 (of #(code:forward-update this %)))
                                                             (eldoc/extension this)
                                                             (error-marks/extension)
                                                             ]})})
                                (j/!set :NodeView this))
                  :!result (atom nil)
                  :!ui-state !ui-state

                  :code-updating? false

                  ;; NodeView API
                  :dom el
                  :update (partial prose:forward-update this)
                  :selectNode (partial select-node this)
                  :deselectNode (fn []
                                  (swap! !ui-state assoc :node-selected? false))
                  :setSelection (partial prose:set-CodeView-selection this)
                  :stopEvent (fn [e]
                               ;; keyboard events that are handled by a keymap are already stopped
                               (or #_(instance? js/MouseEvent e)
                                 (.. e -target (closest ".value-viewer"))))
                  :destroy #(let [{:keys [CodeView !result]} this]
                              (.destroy CodeView)
                              (root/unmount-soon root)
                              ^:clj (let [value (:value @!result)]
                                      (when (satisfies? r/IReactiveValue value)
                                        (r/dispose! value))))}))))