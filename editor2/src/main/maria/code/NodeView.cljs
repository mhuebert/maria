(ns maria.code.NodeView
  (:require [applied-science.js-interop :as j]
            [maria.prose.schema :as prose-schema]
            ["lezer-clojure" :as lezer-clojure]
            ["prosemirror-state" :refer [TextSelection Selection]]
            ["@codemirror/language" :as lang]
            ["@codemirror/state" :refer [EditorState]]
            ["@codemirror/view" :as cm.view :refer [EditorView]]
            ["prosemirror-history" :as history]
            ["@codemirror/commands" :as cmd]
            ["react-dom/client" :as react.client]
            ["react" :as react]
            [nextjournal.clojure-mode.util :as u]
            [nextjournal.clojure-mode :as clj-mode]
            [maria.style :as style]
            [maria.keymap :as keys]
            [maria.eval.sci :as sci]
            [maria.code.commands :as commands]
            [maria.code.views :as views]
            [yawn.view.dom :as dom]))


(j/js
  (defn focus! [{:keys [codeView on-mount]}]
    (on-mount #(.focus codeView))))

(j/js
  (defn set-initial-focus!
    "If ProseMirror cursor is within code view, focus it."
    [{:as this :keys [getPos proseView codeView]}]
    (let [cursor (dec (.. proseView -state -selection -$anchor -pos))
          start (getPos)
          length (.. codeView -state -doc -length)
          end (+ (getPos) length)]
      (when (and (>= cursor start)
                 (< cursor end))
        (focus! this)))))

(j/js

  (defn code:forward-update
    "When the code-editor is focused, forward events from it to ProseMirror."
    [{:keys [codeView proseView getPos code-updating?]} code-update]
    (let [{prose-state :state} proseView]
      (when (and (.-hasFocus codeView) (not code-updating?))
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
            (.dispatch proseView tr))))))

  (defn- controlled-update [this f]
    (j/!set this :code-updating? true)
    (f)
    (j/!set this :code-updating? false))

  (defn code-text [codeView] (.. codeView -state -doc (toString)))

  (defn prose:set-selection
    "Called when ProseMirror tries to put the selection inside the node."
    [{:as this :keys [codeView]} anchor head]
    (controlled-update this
      #(do (.dispatch codeView {:selection {:anchor anchor
                                            :head head}})
           (focus! this))))

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
           :insert (.slice new-text start new-end)}))))

  (defn prose:forward-update [{:as this :keys [codeView]
                               prev-node :proseNode} new-node]
    (boolean
     (when (= (.-type prev-node)
              (.-type new-node))
       (j/!set this :proseNode new-node)
       (let [new-text (.-textContent new-node)
             old-text (code-text codeView)]
         (when (not= new-text old-text)
           (controlled-update this
             (fn []
               (.dispatch codeView {:changes (text-diff old-text new-text)
                                    :annotations [(u/user-event-annotation "noformat")]})))))
       true)))

  (defn prose:select-node [{:keys [codeView]}]
    (.focus codeView))


  (defn editor [{:as proseNode :keys [textContent]} proseView getPos]
    (let [eval-modifier "Alt"
          dom (js/document.createElement "div")
          this (j/obj)]
      (dom/mount dom #(#'views/code-row this))
      (j/extend! this
        {:getPos getPos
         :proseView proseView
         :proseNode proseNode
         :mounted! (fn [el]
                     (.appendChild (.-firstChild el) (.. this -codeView -dom))
                     (set-initial-focus! this)
                     (when-not (j/get this :mounted?)
                       (j/!set this :mounted? true)
                       ^:clj (doseq [f (j/get this :on-mounts)] (f))
                       (j/delete! this :on-mounts)
                       ))
         :codeView (new EditorView
                        {:state
                         (.create EditorState
                                  {:doc textContent
                                   :extensions [(.define lang/LRLanguage
                                                         {:parser (.configure lezer-clojure/parser
                                                                              {:props [clj-mode/format-props
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
                                                                             :on-enter (partial commands/code:eval-string! this)})
                                                (keys/code-keys this)
                                                (.of cm.view/keymap clj-mode/complete-keymap)
                                                (.of cm.view/keymap cmd/historyKeymap)

                                                (.. EditorView
                                                    -updateListener
                                                    (of #(code:forward-update this %)))]})})
         :!result (atom nil)

         :on-mounts []
         :mounted? false
         :on-mount (fn [f]
                     (if (j/get this :mounted?)
                       (f)
                       (j/update! this :on-mounts j/push! f)))

         :code-updating? false

         ;; NodeView API
         :dom dom
         :update (fn [node]
                   #_(j/log :prose:update)
                   (prose:forward-update this node))
         :selectNode (fn [this]
                       #_(j/log :selectNode this)
                       (prose:select-node this))
         :deselectNode (fn []
                         #_(j/log :deselectNode this))
         :setSelection (fn [anchor head]
                         #_(j/log :setSelection this)
                         (prose:set-selection this anchor head))
         :stopEvent (fn []
                      #_(j/log :stopEvent)
                      true)
         :destroy #(let [{:keys [codeView]} this]
                     (.destroy codeView)
                     ;; setTimeout => avoids trying to unmount during a re-render caused by react-refresh
                     (js/setTimeout (partial dom/unmount dom) 0))}))))