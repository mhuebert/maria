(ns maria.keymaps
  (:require ["@codemirror/commands" :as cm.commands]
            ["@codemirror/view" :as cm.view]
            ["prosemirror-state" :refer [NodeSelection TextSelection Selection]]
            ["prosemirror-commands" :as pm.cmd :refer [baseKeymap]]
            ["prosemirror-keymap" :as pm.keymap]
            ["prosemirror-schema-list" :as pm.schema-list]
            ["prosemirror-history" :as pm.history]
            [applied-science.js-interop :as j]
            [maria.code.commands :as commands]
            [maria.prose.links :as links]
            [maria.prose.schema :refer [schema]]))

(def mac? (and (exists? js/navigator)
               (.test #"Mac|iPhone|iPad|iPod" js/navigator.platform)))

(def default-keys (pm.keymap/keymap baseKeymap))

(def chain pm.cmd/chainCommands)


(def palette-commands:prose
  ;; prose commands + metadata accessible to command-palette
  (j/let [^js {{:keys [strong em code]} :marks
               {:keys [bullet_list ordered_list blockquote
                       hard_break list_item paragraph
                       code_block heading horizontal_rule]} :nodes} schema]
    {:font/bold {:doc "Toggle bold"
                 :when :prose
                 :bindings [:Mod-b]
                 :f (pm.cmd/toggleMark strong)}
     :font/italic {:doc "Toggle italic"
                   :when :prose
                   :bindings [:Mod-i]
                   :f (pm.cmd/toggleMark em)}
     :font/code {:doc "Toggle inline code"
                 :when :prose
                 :bindings ["Mod-`"]
                 :f (pm.cmd/toggleMark code)}

     :block/paragraph {:bindings [:Shift-Ctrl-0]
                       :f (pm.cmd/setBlockType paragraph)}
     :block/code {:bindings ["Shift-Ctrl-\\\\"]
                  :when :prose
                  :f (pm.cmd/setBlockType code_block)}
     :block/h1 {:bindings [:Shift-Ctrl-1]
                :when :prose
                :f (pm.cmd/setBlockType heading #js{:level 1})}
     :block/h2 {:bindings [:Shift-Ctrl-2]
                :when :prose
                :f (pm.cmd/setBlockType heading #js{:level 2})}
     :block/h3 {:bindings [:Shift-Ctrl-3]
                :when :prose
                :f (pm.cmd/setBlockType heading #js{:level 3})}
     :block/h4 {:bindings [:Shift-Ctrl-4]
                :when :prose
                :f (pm.cmd/setBlockType heading #js{:level 4})}
     :block/h5 {:bindings [:Shift-Ctrl-5]
                :when :prose
                :f (pm.cmd/setBlockType heading #js{:level 5})}
     :block/h6 {:bindings [:Shift-Ctrl-6]
                :when :prose
                :f (pm.cmd/setBlockType heading #js{:level 6})}
     :block/bullet-list {:bindings [:Shift-Ctrl-8]
                         :when :prose
                         :f (pm.schema-list/wrapInList bullet_list)}
     :block/blockquote {:bindings [:Ctrl->]
                        :when :prose
                        :f (pm.cmd/wrapIn blockquote)}
     ;; does not work
     #_#_:block/ordered-list {:bindings [:Shift-Ctrl-9]
                              :f (pm.schema-list/wrapInList ordered_list)}
     :history/undo {:bindings [:Mod-z]
                    :f pm.history/undo}
     :history/redo {:bindings (cond-> [:Shift-Mod-z]
                                      (not mac?)
                                      (conj :Mod-y))
                    :f pm.history/redo}
     :list/outdent {:bindings ["Mod-["
                               :Shift-Tab]
                    :when :prose
                    :f (pm.schema-list/liftListItem list_item)}
     :list/indent {:bindings ["Mod-]"
                              :Tab]
                   :when :prose
                   :f (pm.schema-list/sinkListItem list_item)}
     :block/horizontal-rule {:bindings [:Mod-_]
                             :when :prose
                             :f (fn [^js state dispatch]
                                  (when dispatch
                                    (dispatch (.. state -tr
                                                  (replaceSelectionWith (.create horizontal_rule))
                                                  (scrollIntoView))))
                                  true)}
     :eval/doc {:bindings [:Mod-Alt-Enter]
                :f (fn [state dispatch view]
                     (commands/prose:eval-doc! view)
                     true)}}))

(defn ->pm [out commands]
  (doseq [[_ {:keys [bindings f]}] commands
          binding bindings]
    (j/!set out (name binding) f))
  out)

(j/js
  (def prose-keymap
    (let [{{:keys [hard_break list_item]} :nodes} schema
          hard-break-cmd (chain
                          pm.cmd/exitCode
                          (fn [state dispatch]
                            (when dispatch
                              (dispatch (.. state -tr
                                            (replaceSelectionWith (.create hard_break))
                                            (pm.cmd/scrollIntoView))))
                            true))]
      (pm.keymap/keymap
       (-> {#_#_:Shift-Tab (fn [_ _ proseView] (commands/prose:next-code-cell proseView))
            :Backspace (chain links/open-link-on-backspace
                              pm.cmd/selectNodeBackward
                              pm.cmd/deleteSelection
                              pm.cmd/joinBackward)
            :Alt-ArrowUp pm.cmd/joinUp
            :Alt-ArrowDown pm.cmd/joinDown
            :Mod-BracketLeft pm.cmd/lift
            :Escape pm.cmd/selectParentNode

            :Enter (chain (pm.schema-list/splitListItem list_item)
                          commands/prose:convert-to-code)
            :ArrowLeft (commands/prose:arrow-handler -1)
            :ArrowUp (commands/prose:arrow-handler -1)
            :ArrowRight (commands/prose:arrow-handler 1)
            :ArrowDown (commands/prose:arrow-handler 1)}
           (->pm palette-commands:prose)
           (j/extend!
             {[:Mod-Enter
               :Shift-Enter] hard-break-cmd}
             (when mac?
               {:Ctrl-Enter hard-break-cmd})))))))

(def palette-commands:code
  ;; code commands + metadata accessible to command-palette
  {:eval/block {:bindings [:Shift-Enter]
                ;; TODO :f
                :when :code}
   :eval/region {:bindings [:Mod-Enter]
                 ;; TODO :f
                 :when :code}
   :nav/next-code-cell {:bindings [:Shift-Tab]
                        :when :code
                        ;; TODO fix :f, its quoted and references `this`
                        :f '#(commands/prose:next-code-cell (j/get this :proseView))}

   })

(defn ->cm [out commands]
  (doseq [[_ {:keys [bindings f]}] commands
          binding bindings]
    (j/push! out #js{:key (name binding)
                     :run f}))
  out)

(j/js
  (def code-keymap
    (.of cm.view/keymap
         (-> [{:key :ArrowUp
               :run (commands/code:arrow-handler :line -1)}
              {:key :ArrowLeft
               :run (commands/code:arrow-handler :char -1)}
              {:key :ArrowDown
               :run (commands/code:arrow-handler :line 1)}
              {:key :ArrowRight
               :run (commands/code:arrow-handler :char 1)}
              {:key :Enter
               :doc "Convert empty code block to paragraph"
               :run commands/code:convert-to-paragraph}
              {:key :Enter
               :doc "New code block after code block"
               :run commands/code:insert-another-code-block}
              {:key :Enter
               :doc "Split code block"
               :run commands/code:split}
              {:key :Backspace
               :doc "Remove empty code block"
               :run commands/code:remove-on-backspace}
              {:key :Mod-c
               :doc "Copy code"
               :run commands/code:copy-current-region}]
             (->cm palette-commands:code)))))


