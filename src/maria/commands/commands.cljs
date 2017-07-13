(ns maria.commands.commands
  (:require [maria.commands.registry :refer-macros [defcommand]]
            [re-db.d :as d]
            [maria.eval :as eval]
            [magic-tree.core :as tree]
            [magic-tree-codemirror.util :as cm]
            [magic-tree-codemirror.edit :as edit]))

(defcommand :copy/form
            {:bindings ["Cmd-C"]
             :when     :editor}
            [{:keys [editor]}]
            (edit/copy-form editor))

(defcommand :cut/line
            "Cut to end of line / node"
            {:bindings ["Ctrl-K"]
             :when     :editor}
            [{:keys [editor]}]
            (edit/kill editor))

(defcommand :cut/form
            "Cuts current highlight"
            {:bindings ["Cmd-X"]
             :when     :editor}
            [{:keys [editor]}]
            (edit/cut-form editor))

(defcommand :delete/form
            "Deletes current highlight"
            {:bindings ["Cmd-Backspace"]
             :when     :editor}
            [{:keys [editor]}]
            (edit/delete-form editor))

(defcommand :cursor/hop-left
            "Move cursor left one form"
            {:bindings ["Alt-Left"]
             :when     :editor}
            [{:keys [editor]}]
            (edit/hop-left editor))

(defcommand :cursor/hop-right
            "Move cursor right one form"
            {:bindings ["Alt-Right"]
             :when     :editor}
            [{:keys [editor]}]
            (edit/hop-right editor))

(defcommand :selection/expand
            "Select parent form, or form under cursor"
            {:bindings ["Cmd-]" "Cmd-1"]
             :when     :editor}
            [{:keys [editor]}]
            (edit/expand-selection editor))

(defcommand :selection/shrink
            "Select child of current form (remembers :expand-selection history)"
            {:bindings ["Cmd-[" "Cmd-2"]
             :when     :editor}
            [{:keys [editor]}]
            (edit/shrink-selection editor))

(defcommand :comment/line
            "Comment the current line"
            {:bindings ["Cmd-/"]
             :when     :editor}
            [{:keys [editor]}]
            (edit/comment-line editor))

(defcommand :comment/uneval-form
            {:bindings ["Cmd-;"]
             :when     :editor}
            [{:keys [editor]}]
            (edit/uneval-form editor))

(defcommand :comment/uneval-top-level-form
            {:bindings ["Cmd-Shift-;"]
             :when     :editor}
            [{:keys [editor]}]
            (edit/uneval-top-level-form editor))

(defcommand :form/slurp
            {:bindings ["Shift-Cmd-K"]
             :when     :editor}
            [{:keys [editor]}]
            (edit/slurp editor))

(defn eval-to-repl [source]
  (d/transact! [[:db/update-attr :repl/state :eval-log (fnil conj []) (assoc (eval/eval-str source)
                                                                        :id (d/unique-id)
                                                                        :source source)]]))

(defn eval-scope [cm scope]
  (let [traverse (case scope :top-level tree/top-loc
                             :bracket identity)]
    (when-let [source (or (cm/selection-text cm)
                          (->> cm
                               :magic/cursor
                               :bracket-loc
                               (traverse)
                               (tree/string (:ns @eval/c-env))))]

      (eval-to-repl source))))

(defcommand :eval/form
            "Evaluate the current form"
            {:bindings ["Cmd-Enter"]
             :when     :editor}
            [{:keys [editor]}]
            (eval-scope editor :bracket))

(defcommand :eval/top-level
            "Evaluate the top-level form"
            {:bindings ["Shift-Cmd-Enter"]
             :when     :editor}
            [{:keys [editor]}]
            (eval-scope editor :top-level))

(defcommand :eval/doc
            "Evaluate whole doc"
            {:bindings ["Option-Cmd-Enter"]
             :when     :editor}
            [{:keys [editor]}]
            (eval-to-repl (.getValue editor)))

(defcommand :eval/on-click
            "Evaluate the clicked form"
            {:bindings ["Option-Click"]
             :when     :editor}
            [{:keys [editor]}]
            (eval-scope editor :bracket)
            true)