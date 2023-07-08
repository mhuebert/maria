(ns maria.editor.code.NodeView
  (:require ["@codemirror/commands" :as cmd]
            ["@codemirror/language" :as lang]
            ["@codemirror/state" :refer [EditorState]]
            ["@codemirror/view" :as cm.view :refer [EditorView]]
            ["@nextjournal/lezer-clojure" :as lezer-clojure]
            ["prosemirror-history" :as history]
            ["prosemirror-state" :refer [TextSelection Selection]]
            ["react" :as react]
            [applied-science.js-interop :as j]
            [applied-science.js-interop.alpha :refer [js]]
            [clojure.string :as str]
            [maria.editor.code.commands :as commands]
            [maria.editor.code.completions :as completions]
            [maria.editor.code.docbar :as eldoc]
            [maria.editor.code.error-marks :as error-marks]
            [maria.editor.code.eval-region :as eval-region]
            [maria.editor.code.styles :as styles]
            [maria.editor.code.views :as views]
            [maria.editor.keymaps :as keymaps]
            [maria.editor.prosemirror.schema :as prose-schema]
            [nextjournal.clojure-mode :as clj-mode]
            [nextjournal.clojure-mode.extensions.close-brackets :as close-brackets]
            [nextjournal.clojure-mode.extensions.formatting :as formatting]
            [nextjournal.clojure-mode.extensions.match-brackets :as match-brackets]
            [nextjournal.clojure-mode.extensions.selection-history :as sel-history]
            [nextjournal.clojure-mode.util :as u]
            [re-db.reactive :as r]
            [yawn.root :as root]))

(defonce !focused-view (atom nil))

(defn set-focus! [^js view _]
  (if (.-hasFocus view)
    (reset! !focused-view view)
    (when (identical? view @!focused-view)
      (reset! !focused-view nil))))

(js
  (defn focus! [{:keys [CodeView on-mount]}]
    (on-mount #(.focus CodeView))))

(js
  (defn set-initial-focus!
    "If ProseMirror cursor is within code view, focus it."
    [{:as this :keys [getPos ProseView CodeView]}]
    (let [cursor (dec (.. ProseView -state -selection -$anchor -pos))
          start (getPos)
          length (.. CodeView -state -doc -length)
          end (+ (getPos) length)]
      (when (and (>= cursor start)
                 (< cursor end))
        (focus! this)))))

(js
  (defn code:forward-update
    "When the code-editor is focused, forward events from it to ProseMirror."
    [{:keys [CodeView ProseView getPos code-updating?]} code-update]
    (let [{prose-state :state} ProseView]
      (when (.-focusChanged code-update)
        (set-focus! CodeView (.-hasFocus CodeView)))
      (when (and (.-hasFocus CodeView) (not code-updating?))
        (let [start-pos (inc (getPos))
              {from' :from to' :to} (.. code-update -state -selection -main)
              {code-changed? :docChanged
               code-changes :changes} code-update
              {:keys [tr doc]} prose-state]
          (when (or code-changed? (not (.eq (.. prose-state -selection)
                                            (.create TextSelection
                                                     doc
                                                     (+ start-pos from')
                                                     (+ start-pos to')))))

            ;; handle code changes
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
                                                   (- to-a from-a))))))

            ;; handle selection changes
            (.setSelection tr (.create TextSelection
                                       (.-doc tr)
                                       (+ start-pos from')
                                       (+ start-pos to')))
            (.dispatch ProseView tr)))))))

(defn- controlled-update [this f]
  (j/!set this :code-updating? true)
  (f)
  (j/!set this :code-updating? false))

(defn code-text [^js CodeView] (.. CodeView -state -doc (toString)))

(js
  (defn prose:set-selection
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
       true))))

(defn prose:select-node [^js this]
  (.. this -CodeView focus))

(def language
  (.define lang/LRLanguage
           (js
             {:parser (.configure lezer-clojure/parser
                                  {:props [formatting/props
                                           (.add lang/foldNodeProp clj-mode/fold-node-props)
                                           styles/code-styles]})})))

(js
  (defn editor [{:as proseNode :keys [textContent]} ProseView getPos]
    (let [el (doto (js/document.createElement "div")
               (.. -classList (add "my-4" "md:flex" "NodeView")))
          this (j/obj :id (str (gensym "code-view-")))
          root (root/create el (views/code-row this))]
      (j/extend! this
        {:initialNs (str/starts-with? textContent "(ns ")
         :getPos getPos
         :ProseView ProseView
         :proseNode proseNode
         :mounted! (fn [el]
                     (.appendChild el (.. this -CodeView -dom))
                     (doto (.. this -CodeView -dom -classList)
                       (.add "rounded-r")
                       (.add "overflow-hidden"))
                     (set-initial-focus! this)
                     (when-not (j/get this :mounted?)
                       (j/!set this :mounted? true)
                       ^:clj (doseq [f (j/get this :on-mounts)] (f))
                       (j/delete! this :on-mounts)))
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

                                                    (eval-region/extension ^:clj {:on-enter #(do (commands/code:eval-string! this %)
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
         :!result   (atom nil)

         :on-mounts []
         :mounted?  false
         :on-mount  (fn [f]
                      (if (j/get this :mounted?)
                        (f)
                        (j/update! this :on-mounts j/push! f)))

         :code-updating? false

         ;; NodeView API
         :dom el
         :update (partial prose:forward-update this)
         :selectNode prose:select-node
         :deselectNode (fn []
                         #_(j/log :deselectNode this))
         :setSelection (partial prose:set-selection this)
         :stopEvent (fn [e]
                      ;; keyboard events that are handled by a keymap are already stopped;
                      ;; not sure what events should be stopped here.
                      (instance? js/MouseEvent e))
         :destroy #(let [{:keys [CodeView !result]} this]
                     (.destroy CodeView)
                     (root/unmount-soon root)
                     ^:clj (let [value (:value @!result)]
                             (when (satisfies? r/IReactiveValue value)
                               (r/dispose! value))))}))))