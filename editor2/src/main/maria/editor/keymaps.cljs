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
            [maria.editor.code-blocks.commands :as commands]
            [maria.editor.prosemirror.links :as links]
            [maria.editor.prosemirror.schema :refer [schema]]
            [nextjournal.clojure-mode :as clj-mode]
            [nextjournal.clojure-mode.commands :refer [paredit-index]]))

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

(js
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
   :navigate/next-code-cell {:bindings [:Shift-Tab]
                             :when :code
                             ;; TODO fix :f, its quoted and references `this`
                             :f '#(commands/prose:next-code-cell (j/get this :proseView))}
   :code/format {:bindings [:Alt-Tab]
                 :doc "Indent document (or selection)"
                 :when :code
                 :f (:indent paredit-index)}
   :code/unwrap {:bindings [:Alt-S]
                 :doc "Lift contents of collection into parent"
                 :when :code
                 :f (:unwrap paredit-index)}
   :code/slurp {:bindings [:Ctrl-ArrowRight
                           :Mod-Shift-ArrowRight
                           :Mod-Shift-K]
                :doc "Expand collection to include form to the right"
                :when :code
                :f (:slurp-forward paredit-index)}
   :code/barf-last {:bindings [:Ctrl-ArrowLeft
                               :Mod-Shift-ArrowLeft]
                    :doc "Push last element of collection out to the right"
                    :when :code
                    :f (:barf-forward paredit-index)}
   :code/slurp-backward {:bindings [:Shift-Ctrl-ArrowLeft]
                         :doc "Expand collection to include form to the right"
                         :when :code
                         :f (:slurp-backward paredit-index)}
   :code/barf-first {:bindings [:Shift-Ctrl-ArrowRight]
                     :doc "Push first element of collection out to the left"
                     :when :code
                     :f (:barf-backward paredit-index)}
   :kill {:bindings [:Ctrl-K]
          :doc "Remove all forms from cursor to end of line"
          :when :code
          :f (:kill paredit-index)}
   :navigate/hop-right {:bindings [:Alt-ArrowRight]
                        :doc "Move cursor one form to the right"
                        :when :code
                        :f (:nav-right paredit-index)}
   :navigate/hop-left {:bindings [:Alt-ArrowLeft]
                       :doc "Move cursor one form to the left"
                       :when :code
                       :f (:nav-left paredit-index)}
   :select/hop-right {:bindings [:Shift-Alt-ArrowRight]
                      :doc "Expand selection one form to the left"
                      :when :code
                      :f (:nav-select-right paredit-index)}
   :select/hop-left {:bindings [:Shift-Alt-ArrowLeft]
                     :doc "Expand selection one form to the right"
                     :when :code
                     :f (:nav-select-left paredit-index)}
   :select/grow {:bindings [:Mod-1
                            :Alt-ArrowUp
                            :Mod-ArrowUp]
                 :doc "Grow selection"
                 :when :code
                 :f (:selection-grow paredit-index)}
   :select/shrink {:bindings [:Mod-2
                              :Alt-ArrowDown
                              :Mod-ArrowDown]
                   :doc "Shrink selection"
                   :when :code
                   :f (:selection-return paredit-index)}})

(defn ->cm [out commands]
  (doseq [[_ {:keys [doc bindings f]}] commands
          binding bindings]
    (j/push! out #js{:key (name binding)
                     :run f}))
  out)

(js
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
              {:key :Enter
               :doc "Insert newline and indent"
               :run ^:clj (:enter-and-indent paredit-index)}
              {:key :Backspace
               :doc "Remove empty code block"
               :run commands/code:remove-on-backspace}
              {:key :Mod-c
               :doc "Copy code"
               :run commands/code:copy-current-region}]
             (->cm palette-commands:code)
             (.concat clj-mode/builtin-keymap)))))


