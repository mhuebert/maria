(ns maria.keymap
  (:require [applied-science.js-interop :as j]
            [maria.prose.schema :refer [schema]]
            [maria.code.commands :as commands]
            ["prosemirror-state" :refer [NodeSelection TextSelection Selection]]
            ["prosemirror-commands" :as pm.cmd :refer [baseKeymap]]
            ["prosemirror-keymap" :as pm.keymap]
            ["prosemirror-schema-list" :as pm.schema-list]
            ["prosemirror-history" :as pm.history]
            ["@codemirror/commands" :as cm.commands]
            ["@codemirror/view" :as cm.view]
            [nextjournal.clojure-mode.node :as n]
            [clojure.string :as str]))

(def default-keys (pm.keymap/keymap baseKeymap))

(def chain pm.cmd/chainCommands)

(j/js
  (def prose-keys
    (let [mac? (and (exists? js/navigator)
                    (.test #"Mac|iPhone|iPad|iPod" js/navigator.platform))
          {{:keys [strong em code]} :marks
           {:keys [bullet_list ordered_list blockquote
                   hard_break list_item paragraph
                   code_block heading horizontal_rule]} :nodes} schema
          hard-break-cmd (chain
                          pm.cmd/exitCode
                          (fn [state dispatch]
                            (when dispatch
                              (dispatch (.. state -tr
                                            (replaceSelectionWith (.create hard_break))
                                            (pm.cmd/scrollIntoView))))
                            true))]
      (pm.keymap/keymap
       (j/extend! {:Shift-Tab (fn [_ _ proseView] (commands/prose:next-code-cell proseView))
                   :Mod-Alt-Enter
                   (fn [state dispatch view]
                     (commands/prose:eval-doc! view)
                     true)
                   :Backspace pm.cmd/undoInputRule
                   :Alt-ArrowUp pm.cmd/joinUp
                   :Alt-ArrowDown pm.cmd/joinDown
                   :Mod-BracketLeft pm.cmd/lift
                   :Escape pm.cmd/selectParentNode

                   [:Mod-b
                    :Mod-B] (pm.cmd/toggleMark strong)
                   [:Mod-i
                    :Mod-I] (pm.cmd/toggleMark em)
                   "Mod-`" (pm.cmd/toggleMark code)
                   :Shift-Ctrl-8 (pm.cmd/toggleMark bullet_list)
                   :Shift-Ctrl-9 (pm.cmd/toggleMark ordered_list)

                   :Ctrl-> (pm.cmd/wrapIn blockquote)

                   :Enter (chain (pm.schema-list/splitListItem list_item)
                                 commands/prose:convert-to-code)
                   "Mod-[" (pm.schema-list/liftListItem list_item)
                   "Mod-]" (pm.schema-list/sinkListItem list_item)

                   :Shift-Ctrl-0 (pm.cmd/setBlockType paragraph)
                   "Shift-Ctrl-\\\\" (pm.cmd/setBlockType code_block)
                   :Shift-Ctrl-1 (pm.cmd/setBlockType heading {:level 1})
                   :Shift-Ctrl-2 (pm.cmd/setBlockType heading {:level 2})
                   :Shift-Ctrl-3 (pm.cmd/setBlockType heading {:level 3})
                   :Shift-Ctrl-4 (pm.cmd/setBlockType heading {:level 4})
                   :Shift-Ctrl-5 (pm.cmd/setBlockType heading {:level 5})
                   :Shift-Ctrl-6 (pm.cmd/setBlockType heading {:level 6})

                   [:ArrowLeft
                    :ArrowUp] (commands/prose:arrow-handler -1)
                   [:ArrowRight
                    :ArrowDown] (commands/prose:arrow-handler 1)}

         {:Mod-z pm.cmd/undo
          :Shift-Mod-z pm.cmd/redo}
         (when-not mac?
           {:Mod-y pm.cmd/redo})

         {[:Mod-Enter
           :Shift-Enter] hard-break-cmd}
         (when mac?
           {:Ctrl-Enter hard-break-cmd})

         {:Mod-_ (fn [state dispatch]
                   (when dispatch
                     (dispatch (.. state -tr
                                   (replaceSelectionWith (.create horizontal_rule))
                                   (pm.cmd/scrollIntoView))))
                   true)})))))

(j/js
  (defn code-keys [this]
    (.of cm.view/keymap
         [{:key :ArrowUp
           :run #(commands/code:arrow-handler this :line -1)}
          {:key :ArrowLeft
           :run #(commands/code:arrow-handler this :char -1)}
          {:key :ArrowDown
           :run #(commands/code:arrow-handler this :line 1)}
          {:key :ArrowRight
           :run #(commands/code:arrow-handler this :char 1)}
          {:key :Ctrl-z :mac :Cmd-z
           :run (commands/bind-prose-command this pm.history/undo)}
          {:key :Shift-Ctrl-z :mac :Shift-Cmd-z
           :run (commands/bind-prose-command this pm.history/redo)}
          {:key :Ctrl-y :mac :Cmd-y
           :run (commands/bind-prose-command this pm.history/redo)}
          {:key :Enter
           :doc "Convert empty code block to paragraph"
           :run (partial commands/code:convert-to-paragraph this)}
          {:key :Enter
           :doc "New paragraph after code block"
           :run (partial commands/code:paragraph-after-code this)}
          {:key :Enter
           :doc "Split code block"
           :run (partial commands/code:split this)}
          {:key :Backspace
           :doc "Remove empty code block"
           :run (partial commands/code:remove-on-backspace this)}
          {:key :Mod-Alt-Enter
           :doc "Evaluate doc"
           :run (fn [_] (commands/prose:eval-doc! (.. this -proseView))
                  true)}
          {:key :Shift-Enter
           :doc "Evaluate block"
           :run #(commands/code:eval-block! this)}
          {:key :Alt-Enter
           :doc "Evaluate current region"
           :run #(commands/code:eval-current-region this)}
          {:key :Shift-Tab
           :doc "Next Code Cell"
           :run #(commands/prose:next-code-cell (j/get this :proseView))}
          {:key :Alt-c
           :doc "Copy code"
           :run commands/code:copy-current-region}])))


