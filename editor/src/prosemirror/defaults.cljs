(ns prosemirror.defaults
  (:require  ["prosemirror-inputrules" :refer [allInputRules]]
             [prosemirror.commands :as commands]
            [prosemirror.core :as pm]))

(def keymap
  {"Mod-z"        commands/undo
   "Mod-y"        commands/redo
   "Shift-Mod-z"  commands/redo
   "Backspace"    commands/backspace

   "Mod-b"        commands/inline-bold
   "Mod-i"        commands/inline-italic
   "Mod-`"        commands/inline-code

   "Shift-Ctrl-8" commands/block-list-bullet
   "Shift-Ctrl-9" commands/block-list-ordered
   "Shift-Ctrl-0" commands/block-paragraph
   "Shift-Ctrl-1" (commands/block-heading "1")
   "Shift-Ctrl-2" (commands/block-heading "2")
   "Shift-Ctrl-3" (commands/block-heading "3")
   "Shift-Ctrl-4" (commands/block-heading "4")
   "Shift-Ctrl-5" (commands/block-heading "5")
   "Shift-Ctrl-6" (commands/block-heading "6")

   "Mod-["        commands/outdent
   "Shift-Tab"    commands/outdent
   "Mod-]"        commands/indent
   "Tab"          commands/indent

   "Mod-Enter"    commands/hard-break
   "Shift-Enter"  commands/hard-break
   "Ctrl-Enter"   commands/hard-break
   "Enter"        commands/enter
   })

(def input-rules (into [commands/rule-blockquote-start
                        commands/rule-block-list-bullet-start
                        commands/rule-block-list-numbered-start
                        commands/rule-block-code-start
                        commands/rule-block-heading-start
                        commands/rule-paragraph-start]
                       allInputRules))

