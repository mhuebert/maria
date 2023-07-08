(ns maria.editor.keymaps
  (:require ["@codemirror/commands" :as cm.commands]
            ["@codemirror/view" :as cm.view]
            ["prosemirror-state" :refer [NodeSelection TextSelection Selection]]
            ["prosemirror-commands" :as pm.cmd :refer [baseKeymap]]
            ["prosemirror-keymap" :as pm.keymap]
            ["prosemirror-schema-list" :as pm.schema-list]
            ["prosemirror-history" :as pm.history]
            [applied-science.js-interop :as j]
            [applied-science.js-interop.alpha :refer [js]]
            [clojure.string :as str]
            [maria.editor.code.commands :as code.commands]
            [maria.editor.prosemirror.links :as links]
            [maria.editor.prosemirror.schema :refer [schema]]
            [maria.ui :as ui]
            [nextjournal.clojure-mode :as clj-mode]
            [nextjournal.clojure-mode.commands :refer [paredit-index]]
            [re-db.hooks :as h]
            [re-db.reactive :as r]
            [yawn.view :as v]))

(def mac? (and (exists? js/navigator)
               (.test #"Mac|iPhone|iPad|iPod" js/navigator.platform)))

(def default-keys (pm.keymap/keymap baseKeymap))

(def chain pm.cmd/chainCommands)

(defn spaced-name [the-name]
  (str/capitalize (str/replace (name the-name) "-" " ")))

(defn format-commands [m]
  (reduce-kv (fn [out id cmd]
               (assoc out id
                          (assoc cmd :id id
                                     :ns (some-> (namespace id) spaced-name)
                                     :title (or (:title cmd)
                                                (spaced-name (name id))))))
             {}
             m))

(def commands:prose
  (j/let [^js {{:keys [strong em code]}                     :marks
               {:keys [bullet_list ordered_list blockquote
                       hard_break list_item paragraph
                       code_block heading horizontal_rule]} :nodes} schema
          hard-break-cmd (chain
                           pm.cmd/exitCode
                           (fn [^js state dispatch]
                             (when dispatch
                               (dispatch (.. state -tr
                                             (replaceSelectionWith (.create hard_break))
                                             (pm.cmd/scrollIntoView))))
                             true))]
    {:text/bold              {:kind     :prose
                              :when     :focus/prose
                              :bindings [:Mod-b]
                              :f        (pm.cmd/toggleMark strong)}
     :text/italic            {:kind     :prose
                              :when     :focus/prose
                              :bindings [:Mod-i]
                              :f        (pm.cmd/toggleMark em)}
     :text/inline-code       {:kind     :prose
                              :when     :focus/prose
                              :bindings ["Mod-`"]
                              :f        (pm.cmd/toggleMark code)}
     :block/paragraph        {:bindings [:Shift-Ctrl-0]
                              :kind     :prose
                              :f        (pm.cmd/setBlockType paragraph)}
     :block/code             {:bindings ["Shift-Ctrl-\\"]
                              :kind     :prose
                              :when     :focus/prose
                              :f        (pm.cmd/setBlockType code_block)}
     :block/h1               {:bindings [:Shift-Ctrl-1]
                              :kind     :prose
                              :when     :focus/prose
                              :f        (pm.cmd/setBlockType heading #js{:level 1})}
     :block/h2               {:bindings [:Shift-Ctrl-2]
                              :kind     :prose
                              :when     :focus/prose
                              :f        (pm.cmd/setBlockType heading #js{:level 2})}
     :block/h3               {:bindings [:Shift-Ctrl-3]
                              :kind     :prose
                              :when     :focus/prose
                              :f        (pm.cmd/setBlockType heading #js{:level 3})}
     :block/h4               {:bindings [:Shift-Ctrl-4]
                              :kind     :prose
                              :when     :focus/prose
                              :f        (pm.cmd/setBlockType heading #js{:level 4})}
     :block/h5               {:bindings [:Shift-Ctrl-5]
                              :kind     :prose
                              :when     :focus/prose
                              :f        (pm.cmd/setBlockType heading #js{:level 5})}
     :block/h6               {:bindings [:Shift-Ctrl-6]
                              :kind     :prose
                              :when     :focus/prose
                              :f        (pm.cmd/setBlockType heading #js{:level 6})}
     :block/bullet-list      {:title    "Convert block to bullet list"
                              :bindings [:Shift-Ctrl-8]
                              :kind     :prose
                              :when     :focus/prose
                              :f        (pm.schema-list/wrapInList bullet_list)}
     :block/blockquote       {:title    "Convert block to blockquote"
                              :bindings [:Ctrl->]
                              :kind     :prose
                              :when     :focus/prose
                              :f        (pm.cmd/wrapIn blockquote)}
     ;; does not work
     #_#_:block/ordered-list {:bindings [:Shift-Ctrl-9]
                              :f        (pm.schema-list/wrapInList ordered_list)}
     :history/undo           {:bindings [:Mod-z]
                              :kind     :prose
                              :f        pm.history/undo}
     :history/redo           {:bindings (cond-> [:Mod-Shift-z]
                                                (not mac?)
                                                (conj :Mod-y))
                              :kind     :prose
                              :f        pm.history/redo}
     :list/outdent           {:title    "Outdent list item"
                              :bindings ["Mod-["
                                         :Shift-Tab]
                              :kind     :prose
                              :when     :focus/prose
                              :f        (pm.schema-list/liftListItem list_item)}
     :list/indent            {:title    "Indent list item"
                              :bindings ["Mod-]"
                                         :Tab]
                              :kind     :prose
                              :when     :focus/prose
                              :f        (pm.schema-list/sinkListItem list_item)}
     :insert/horizontal-rule {:bindings [:Mod-_]
                              :kind     :prose
                              :when     :focus/prose
                              :f        (fn [^js state dispatch]
                                          (when dispatch
                                            (dispatch (.. state -tr
                                                          (replaceSelectionWith (.create horizontal_rule))
                                                          (scrollIntoView))))
                                          true)}
     :code/eval-doc          {:kind     :prose
                              :bindings [:Mod-Alt-Enter]
                              :f        (fn [state dispatch view]
                                          (code.commands/prose:eval-doc! view)
                                          true)}
     :code/hide-source       {:when :NodeView
                              :f    (fn [{:keys [NodeView]}]
                                      (swap! (j/get NodeView :!ui-state) update :hide-source? (fnil not false)))}
     :prose/backspace        {:bindings [:Backspace]
                              :hidden?  true
                              :f        (chain links/open-link-on-backspace
                                               pm.cmd/selectNodeBackward
                                               pm.cmd/deleteSelection
                                               pm.cmd/joinBackward)
                              :kind     :prose}
     :prose/join-up          {:bindings     [:Alt-ArrowUp]
                              :hidden?      true
                              :command-bar? false
                              :f            pm.cmd/joinUp
                              :kind         :prose}
     :prose/join-down        {:bindings     [:Alt-ArrowDown]
                              :hidden?      true
                              :command-bar? false
                              :f            pm.cmd/joinDown
                              :kind         :prose}
     :prose/lift             {:bindings     [:Mod-BracketLeft]
                              :hidden?      true
                              :command-bar? false
                              :f            pm.cmd/lift
                              :kind         :prose}
     :prose/select-parent    {:bindings     [:Escape]
                              :hidden?      true
                              :command-bar? false
                              :f            pm.cmd/selectParentNode
                              :kind         :prose}

     :prose/enter            {:bindings     [:Enter]
                              :hidden?      true
                              :command-bar? false
                              :f            (chain (pm.schema-list/splitListItem list_item)
                                                   code.commands/prose:convert-to-code)
                              :kind         :prose}
     :prose/arrow-left       {:bindings     [:ArrowLeft]
                              :hidden?      true
                              :command-bar? false
                              :f            (code.commands/prose:arrow-handler -1)
                              :kind         :prose}
     :prose/arrow-up         {:bindings     [:ArrowUp]
                              :hidden?      true
                              :command-bar? false
                              :f            (code.commands/prose:arrow-handler -1)
                              :kind         :prose}
     :prose/arrow-right      {:bindings     [:ArrowRight]
                              :hidden?      true
                              :command-bar? false
                              :f            (code.commands/prose:arrow-handler 1)
                              :kind         :prose}
     :prose/arrow-down       {:bindings     [:ArrowDown]
                              :hidden?      true
                              :command-bar? false
                              :f            (code.commands/prose:arrow-handler 1)
                              :kind         :prose}

     :prose/hard-break       {:bindings (cond-> [:Mod-Enter
                                                 :Shift-Enter]
                                                mac?
                                                (conj :Ctrl-Enter))
                              :hidden?  true
                              :kind     :prose
                              :f        hard-break-cmd}}))


(def prose-keymap
  (let [out #js{}]
    (doseq [[_ {:keys [bindings f]}] commands:prose
            binding bindings]
      (j/!set out (name binding) f))
    (pm.keymap/keymap out)))

(defn something-selected [cm-state]
  (.. cm-state -selection -ranges (some #(not (.-empty ^js %)))))

(def commands:code
  {:code/eval-block                {:bindings [:Shift-Enter]
                                    ;; TODO :f
                                    :kind     :code}
   :code/eval-region               {:bindings [:Mod-Enter]
                                    ;; TODO :f
                                    :kind     :code}
   :code/format                    {:bindings [:Alt-Tab]
                                    :kind     :code
                                    ;; set title based on whether selection is empty
                                    :prepare  (fn [cmd {:keys [NodeView]}]
                                                (cond-> cmd
                                                        NodeView
                                                        (assoc :title
                                                               (if (something-selected (.-state (j/get NodeView :CodeView)))
                                                                 "Format selection"
                                                                 "Format cell"))))
                                    :f        (:indent paredit-index)}
   :code/unwrap                    {:bindings [:Alt-s]
                                    :title    "Splice/unwrap form into parent"
                                    :kind     :code
                                    :f        (:unwrap paredit-index)}
   :code/slurp-right               {:bindings [:Ctrl-ArrowRight
                                               :Mod-Shift-ArrowRight
                                               :Mod-Shift-k]
                                    :doc      "Expand collection to include form to the right"
                                    :kind     :code
                                    :f        (:slurp-forward paredit-index)}
   :code/barf-right                {:bindings [:Ctrl-ArrowLeft
                                               :Mod-Shift-ArrowLeft]
                                    :doc      "Push last element of collection out to the right"
                                    :kind     :code
                                    :f        (:barf-forward paredit-index)}
   :code/slurp-backward            {:bindings [:Shift-Ctrl-ArrowLeft]
                                    :doc      "Expand collection to include form to the left"
                                    :kind     :code
                                    :f        (:slurp-backward paredit-index)}
   :code/barf-left                 {:bindings [:Shift-Ctrl-ArrowRight]
                                    :doc      "Push first element of collection out to the left"
                                    :kind     :code
                                    :f        (:barf-backward paredit-index)}
   :code/kill                      {:bindings [:Ctrl-k]
                                    :doc      "Remove all forms from cursor to end of line"
                                    :kind     :code
                                    :f        (:kill paredit-index)}
   :code/hop-cursor-right          {:bindings [:Alt-ArrowRight]
                                    :doc      "Move cursor one form to the right"
                                    :kind     :code
                                    :f        (:nav-right paredit-index)}
   :code/hop-cursor-left           {:bindings [:Alt-ArrowLeft]
                                    :doc      "Move cursor one form to the left"
                                    :kind     :code
                                    :f        (:nav-left paredit-index)}
   :code/expand-selection-right    {:bindings [:Shift-Alt-ArrowRight]
                                    :doc      "Expand selection one form to the left"
                                    :kind     :code
                                    :f        (:nav-select-right paredit-index)}
   :code/expand-selection-left     {:bindings [:Shift-Alt-ArrowLeft]
                                    :doc      "Expand selection one form to the right"
                                    :kind     :code
                                    :f        (:nav-select-left paredit-index)}
   :code/grow-selection            {:bindings [:Mod-1
                                               :Alt-ArrowUp
                                               :Mod-ArrowUp]
                                    :kind     :code
                                    :f        (:selection-grow paredit-index)}
   :code/shrink-selection          {:bindings [:Mod-2
                                               :Alt-ArrowDown
                                               :Mod-ArrowDown]
                                    :kind     :code
                                    :f        (:selection-return paredit-index)}
   :code/arrow-up                  {:bindings [:ArrowUp]
                                    :kind     :code
                                    :hidden?  true
                                    :f        (code.commands/code:arrow-handler :line -1)}
   :code/arrow-left                {:bindings [:ArrowLeft]
                                    :kind     :code
                                    :hidden?  true
                                    :f        (code.commands/code:arrow-handler :char -1)}
   :code/arrow-down                {:bindings [:ArrowDown]
                                    :kind     :code
                                    :hidden?  true
                                    :f        (code.commands/code:arrow-handler :line 1)}
   :code/arrow-right               {:bindings [:ArrowRight]
                                    :kind     :code
                                    :hidden?  true
                                    :f        (code.commands/code:arrow-handler :char 1)}
   :code/code->paragraph           {:bindings [:Enter]
                                    :kind     :code
                                    :hidden?  true
                                    :title    "Convert empty code block to paragraph"
                                    :f        code.commands/code:convert-to-paragraph}
   :code/new-code-block            {:bindings [:Enter]
                                    :kind     :code
                                    :hidden?  true
                                    :f        code.commands/code:insert-another-code-block}
   :code/split-block               {:bindings [:Enter]
                                    :kind     :code
                                    :hidden?  true
                                    :f        code.commands/code:split}
   :code/insert-newline-and-indent {:bindings [:Enter]
                                    :kind     :code
                                    :hidden?  true
                                    :f        ^:clj (:enter-and-indent paredit-index)}
   :code/remove-empty-code-block   {:bindings [:Backspace]
                                    :kind     :code
                                    :hidden?  true
                                    :f        code.commands/code:remove-on-backspace}
   :code/copy                      {:bindings [:Mod-c]
                                    :kind     :code
                                    :hidden?  true
                                    :title    "Copy code"
                                    :f        code.commands/code:copy-current-region}})

(def code-keymap
  (.of cm.view/keymap
       (-> (let [out #js []]
             (doseq [[_ {:keys [doc bindings f]}] commands:code
                     binding bindings]
               (j/push! out #js{:key (name binding)
                                :run f}))
             out)
           (.concat clj-mode/builtin-keymap))))

(def !prev-selected-element (atom nil))

(defn hide-command-bar! []
  (some-> (:command-bar/element @ui/!state) (j/call :blur))
  (when-let [^js prev @!prev-selected-element]
    (reset! !prev-selected-element nil)
    (.focus prev)))

(defn show-command-bar! []
  (when-let [el (:command-bar/element @ui/!state)]
    (when-not (= @!prev-selected-element (.-activeElement js/document))
      (reset! !prev-selected-element (.-activeElement js/document))
      (.focus el))))

(defn command-bar-open? []
  (when-let [el (:command-bar/element @ui/!state)]
    (identical? el (.-activeElement js/document))))

(def commands:global
  {:editor/toggle-sidebar     {:bindings [:Shift-Mod-k]
                               :kind     :global
                               :prepare  (fn [cmd _]
                                           (assoc cmd
                                             :title (if (:sidebar/visible? @ui/!state)
                                                      "Hide sidebar"
                                                      "Show sidebar")))
                               :f        (fn [_]
                                           (swap! ui/!state update :sidebar/visible? not)
                                           false)}
   :editor/toggle-command-bar {:bindings [:Mod-k]
                               :kind     :global
                               :prepare  (fn [cmd _]
                                           (merge cmd (if (command-bar-open?)
                                                        {:title "Hide command bar"
                                                         :f     hide-command-bar!}
                                                        {:title "Show command bar"
                                                         :f     show-command-bar!})))}})

(defonce !command-registry (atom {}))
(defonce !binding-overrides (atom {}))
(defonce !commands
         (r/reaction
           (reduce-kv (fn [registry id bindings]
                        (if-some [cmd (registry id)]
                          (assoc registry id (assoc cmd :bindings bindings))
                          registry))
                      (h/use-deref !command-registry)
                      (h/use-deref !binding-overrides))))

(defn register-commands! [commands]
  (swap! !command-registry merge (format-commands commands)))

(register-commands! commands:prose)
(register-commands! commands:code)
(register-commands! commands:global)

(defn show-binding [binding]
  (v/x [:div.inline-flex
        {:class "gap-[2px]"}
        (map-indexed (fn [i segment]
                       [:span {:key i}
                        (case segment
                          "Mod" (if mac? "⌘" "Ctrl")
                          "Alt" (if mac? "⌥" "Alt")
                          "Ctrl" (if mac? "⌃" "Ctrl")
                          ;"m3" (if mac? "Ctrl" "Meta")
                          "ArrowLeft" "◄"
                          "ArrowRight" "►"
                          "ArrowUp" "▲"
                          "ArrowDown" "▼"
                          "Backspace" "⌫"
                          "Shift" "⇧"
                          "Tab" "⇥"
                          "Enter" "⏎"
                          "\\" "\\"
                          ;; ⌫
                          (str/capitalize segment))])
                     (str/split (name binding) #"\-"))]))

