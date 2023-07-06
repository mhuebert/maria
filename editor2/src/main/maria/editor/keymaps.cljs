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
            [clerkify.maria :as clerkify]
            [maria.editor.code-blocks.commands :as commands]
            [maria.ui :as ui]
            [maria.editor.doc :as editor.doc]
            [maria.editor.prosemirror.links :as links]
            [maria.editor.prosemirror.schema :refer [schema]]
            [nextjournal.clojure-mode :as clj-mode]
            [nextjournal.clojure-mode.commands :refer [paredit-index]]))

(def mac? (and (exists? js/navigator)
               (.test #"Mac|iPhone|iPad|iPod" js/navigator.platform)))

(def default-keys (pm.keymap/keymap baseKeymap))

(def chain pm.cmd/chainCommands)

(def commands:prose
  ;; prose commands + metadata accessible to command-palette
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
    {:font/bold             {:doc      "Toggle bold"
                             :kind     :prose
                             :bindings [:Mod-b]
                             :f        (pm.cmd/toggleMark strong)}
     :font/italic           {:doc      "Toggle italic"
                             :kind     :prose
                             :bindings [:Mod-i]
                             :f        (pm.cmd/toggleMark em)}
     :font/code             {:doc      "Toggle inline code"
                             :kind     :prose
                             :bindings ["Mod-`"]
                             :f        (pm.cmd/toggleMark code)}

     :block/paragraph       {:doc      "Convert block to paragraph"
                             :bindings [:Shift-Ctrl-0]
                             :kind     :prose
                             :f        (pm.cmd/setBlockType paragraph)}
     :block/code            {:doc      "Convert block to code"
                             :bindings ["Shift-Ctrl-\\\\"]
                             :kind     :prose
                             :f        (pm.cmd/setBlockType code_block)}
     :block/h1              {:doc      "Convert block to heading 1"
                             :bindings [:Shift-Ctrl-1]
                             :kind     :prose
                             :f        (pm.cmd/setBlockType heading #js{:level 1})}
     :block/h2              {:doc      "Convert block to heading 2"
                             :bindings [:Shift-Ctrl-2]
                             :kind     :prose
                             :f        (pm.cmd/setBlockType heading #js{:level 2})}
     :block/h3              {:doc      "Convert block to heading 3"
                             :bindings [:Shift-Ctrl-3]
                             :kind     :prose
                             :f        (pm.cmd/setBlockType heading #js{:level 3})}
     :block/h4              {:doc      "Convert block to heading 4"
                             :bindings [:Shift-Ctrl-4]
                             :kind     :prose
                             :f        (pm.cmd/setBlockType heading #js{:level 4})}
     :block/h5              {:doc      "Convert block to heading 5"
                             :bindings [:Shift-Ctrl-5]
                             :kind     :prose
                             :f        (pm.cmd/setBlockType heading #js{:level 5})}
     :block/h6              {:doc      "Convert block to heading 6"
                             :bindings [:Shift-Ctrl-6]
                             :kind     :prose
                             :f        (pm.cmd/setBlockType heading #js{:level 6})}
     :block/bullet-list     {:doc      "Convert block to bullet list"
                             :bindings [:Shift-Ctrl-8]
                             :kind     :prose
                             :f        (pm.schema-list/wrapInList bullet_list)}
     :block/blockquote      {:doc      "Convert block to blockquote"
                             :bindings [:Ctrl->]
                             :kind     :prose
                             :f        (pm.cmd/wrapIn blockquote)}
     ;; does not work
     #_#_:block/ordered-list {:bindings [:Shift-Ctrl-9]
                              :f        (pm.schema-list/wrapInList ordered_list)}
     :history/undo          {:bindings [:Mod-z]
                             :kind     :prose
                             :f        pm.history/undo}
     :history/redo          {:bindings (cond-> [:Shift-Mod-z]
                                               (not mac?)
                                               (conj :Mod-y))
                             :kind     :prose
                             :f        pm.history/redo}
     :list/outdent          {:doc      "Outdent list item"
                             :bindings ["Mod-["
                                        :Shift-Tab]
                             :kind     :prose
                             :f        (pm.schema-list/liftListItem list_item)}
     :list/indent           {:doc      "Indent list item"
                             :bindings ["Mod-]"
                                        :Tab]
                             :kind     :prose
                             :f        (pm.schema-list/sinkListItem list_item)}
     :block/horizontal-rule {:doc      "Insert horizontal rule"
                             :bindings [:Mod-_]
                             :kind     :prose
                             :f        (fn [^js state dispatch]
                                         (when dispatch
                                           (dispatch (.. state -tr
                                                         (replaceSelectionWith (.create horizontal_rule))
                                                         (scrollIntoView))))
                                         true)}
     :eval/doc              {:doc      "Evaluate document"
                             :kind     :prose
                             :bindings [:Mod-Alt-Enter]
                             :f        (fn [state dispatch view]
                                         (commands/prose:eval-doc! view)
                                         true)}
     :clerkify              {:doc  "Save as Clerk project"
                             :kind :prose
                             :f    (fn [state dispatch view]
                                     (clerkify/download-clerkified-zip
                                       (-> (j/get-in view [:state :doc])
                                           editor.doc/doc->clj)))}
     #_#_:Shift-Tab (fn [_ _ proseView] (commands/prose:next-code-cell proseView))
     :prose/backspace       {:bindings [:Backspace]
                             :hidden?  true
                             :f        (chain links/open-link-on-backspace
                                              pm.cmd/selectNodeBackward
                                              pm.cmd/deleteSelection
                                              pm.cmd/joinBackward)
                             :kind     :prose}
     :prose/join-up         {:bindings     [:Alt-ArrowUp]
                             :hidden?      true
                             :command-bar? false
                             :f            pm.cmd/joinUp
                             :kind         :prose}
     :prose/join-down       {:bindings     [:Alt-ArrowDown]
                             :hidden?      true
                             :command-bar? false
                             :f            pm.cmd/joinDown
                             :kind         :prose}
     :prose/lift            {:bindings     [:Mod-BracketLeft]
                             :hidden?      true
                             :command-bar? false
                             :f            pm.cmd/lift
                             :kind         :prose}
     :prose/select-parent   {:bindings     [:Escape]
                             :hidden?      true
                             :command-bar? false
                             :f            pm.cmd/selectParentNode
                             :kind         :prose}

     :prose/enter           {:bindings     [:Enter]
                             :hidden?      true
                             :command-bar? false
                             :f            (chain (pm.schema-list/splitListItem list_item)
                                                  commands/prose:convert-to-code)
                             :kind         :prose}
     :prose/arrow-left      {:bindings     [:ArrowLeft]
                             :hidden?      true
                             :command-bar? false
                             :f            (commands/prose:arrow-handler -1)
                             :kind         :prose}
     :prose/arrow-up        {:bindings     [:ArrowUp]
                             :hidden?      true
                             :command-bar? false
                             :f            (commands/prose:arrow-handler -1)
                             :kind         :prose}
     :prose/arrow-right     {:bindings     [:ArrowRight]
                             :hidden?      true
                             :command-bar? false
                             :f            (commands/prose:arrow-handler 1)
                             :kind         :prose}
     :prose/arrow-down      {:bindings     [:ArrowDown]
                             :hidden?      true
                             :command-bar? false
                             :f            (commands/prose:arrow-handler 1)
                             :kind         :prose}

     :prose/hard-break      {:bindings (cond-> [:Mod-Enter
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

(def commands:code
  ;; code commands + metadata accessible to command-palette
  {:eval/block              {:bindings [:Shift-Enter]
                             ;; TODO :f
                             :kind     :code}
   :eval/region             {:bindings [:Mod-Enter]
                             ;; TODO :f
                             :kind     :code}
   :navigate/prev-code-cell {:bindings [:Shift-Tab]
                             :kind     :code
                             :f        (fn [codeView]
                                         (commands/prose:prev-code-cell (j/get-in codeView [:node-view :proseView])))}
   :code/format             {:bindings [:Alt-Tab]
                             :doc      "Indent document (or selection)"
                             :kind     :code
                             :f        (:indent paredit-index)}
   :code/unwrap             {:bindings [:Alt-s]
                             :doc      "Lift contents of collection into parent"
                             :kind     :code
                             :f        (:unwrap paredit-index)}
   :code/slurp              {:bindings [:Ctrl-ArrowRight
                                        :Mod-Shift-ArrowRight
                                        :Mod-Shift-k]
                             :doc      "Expand collection to include form to the right"
                             :kind     :code
                             :f        (:slurp-forward paredit-index)}
   :code/barf-last          {:bindings [:Ctrl-ArrowLeft
                                        :Mod-Shift-ArrowLeft]
                             :doc      "Push last element of collection out to the right"
                             :kind     :code
                             :f        (:barf-forward paredit-index)}
   :code/slurp-backward     {:bindings [:Shift-Ctrl-ArrowLeft]
                             :doc      "Expand collection to include form to the right"
                             :kind     :code
                             :f        (:slurp-backward paredit-index)}
   :code/barf-first         {:bindings [:Shift-Ctrl-ArrowRight]
                             :doc      "Push first element of collection out to the left"
                             :kind     :code
                             :f        (:barf-backward paredit-index)}
   :kill                    {:bindings [:Ctrl-K]
                             :doc      "Remove all forms from cursor to end of line"
                             :kind     :code
                             :f        (:kill paredit-index)}
   :navigate/hop-right      {:bindings [:Alt-ArrowRight]
                             :doc      "Move cursor one form to the right"
                             :kind     :code
                             :f        (:nav-right paredit-index)}
   :navigate/hop-left       {:bindings [:Alt-ArrowLeft]
                             :doc      "Move cursor one form to the left"
                             :kind     :code
                             :f        (:nav-left paredit-index)}
   :select/hop-right        {:bindings [:Shift-Alt-ArrowRight]
                             :doc      "Expand selection one form to the left"
                             :kind     :code
                             :f        (:nav-select-right paredit-index)}
   :select/hop-left         {:bindings [:Shift-Alt-ArrowLeft]
                             :doc      "Expand selection one form to the right"
                             :kind     :code
                             :f        (:nav-select-left paredit-index)}
   :select/grow             {:bindings [:Mod-1
                                        :Alt-ArrowUp
                                        :Mod-ArrowUp]
                             :doc      "Grow selection"
                             :kind     :code
                             :f        (:selection-grow paredit-index)}
   :select/shrink           {:bindings [:Mod-2
                                        :Alt-ArrowDown
                                        :Mod-ArrowDown]
                             :doc      "Shrink selection"
                             :kind     :code
                             :f        (:selection-return paredit-index)}
   :code/arrow-up           {:bindings [:ArrowUp]
                             :kind     :code
                             :hidden?  true
                             :f        (commands/code:arrow-handler :line -1)}
   :code/arrow-left         {:bindings [:ArrowLeft]
                             :kind     :code
                             :hidden?  true
                             :f        (commands/code:arrow-handler :char -1)}
   :code/arrow-down         {:bindings [:ArrowDown]
                             :kind     :code
                             :hidden?  true
                             :f        (commands/code:arrow-handler :line 1)}
   :code/arrow-right        {:bindings [:ArrowRight]
                             :kind     :code
                             :hidden?  true
                             :f        (commands/code:arrow-handler :char 1)}
   :code/code->paragraph    {:bindings [:Enter]
                             :kind     :code
                             :hidden?  true
                             :doc      "Convert empty code block to paragraph"
                             :f        commands/code:convert-to-paragraph}
   :code/new-block          {:bindings [:Enter]
                             :kind     :code
                             :hidden?  true
                             :doc      "New code block after code block"
                             :f        commands/code:insert-another-code-block}
   :code/split-block        {:bindings [:Enter]
                             :kind     :code
                             :hidden?  true
                             :doc      "Split code block"
                             :f        commands/code:split}
   :code/insert-indented    {:bindings [:Enter]
                             :kind     :code
                             :hidden?  true
                             :doc      "Insert newline and indent"
                             :f        ^:clj (:enter-and-indent paredit-index)}
   :code/remove-empty       {:bindings [:Backspace]
                             :kind     :code
                             :hidden?  true
                             :doc      "Remove empty code block"
                             :f        commands/code:remove-on-backspace}
   :code/copy               {:bindings [:Mod-c]
                             :kind     :code
                             :hidden?  true
                             :doc      "Copy code"
                             :f        commands/code:copy-current-region}})

(def code-keymap
  (.of cm.view/keymap
       (-> (let [out #js []]
             (doseq [[_ {:keys [doc bindings f]}] commands:code
                     binding bindings]
               (j/push! out #js{:key (name binding)
                                :run f}))
             out)
           (.concat clj-mode/builtin-keymap))))

(def commands:global
  {:toggle-sidebar     {:bindings [:Shift-Mod-k]
                        :kind     :global
                        :f        (fn [_]
                                    (swap! ui/!state update :sidebar/visible? not))}
   :toggle-command-bar {:bindings [:Mod-k]
                        :kind     :global
                        :f        (fn [_]
                                    (prn :ho "toggle command bar")
                                    (when-let [el (:command-bar/element @ui/!state)]
                                      (if (identical? el (.-activeElement js/document))
                                        (.blur el)
                                        (.focus el))))}})

(def commands:all
  (merge commands:prose
         commands:code
         commands:global))