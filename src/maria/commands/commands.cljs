(ns maria.commands.commands
  (:require [maria.commands.registry :refer-macros [defcommand]]
            [re-db.d :as d]
            [maria.eval :as eval]
            [magic-tree.core :as tree]
            [magic-tree-codemirror.util :as cm]
            [magic-tree-codemirror.edit :as edit]))

(defcommand :copy/form
            ["Cmd-C"]
            ""
            edit/copy-form)

(defcommand :cut/line
            ["Ctrl-K"]
            "Cut to end of line / node"
            edit/kill)

(defcommand :cut/form
            ["Cmd-X"]
            "Cuts current highlight"
            edit/cut-form)

(defcommand :delete/form
            ["Cmd-Backspace"]
            "Deletes current highlight"
            edit/delete-form)

(defcommand :cursor/hop-left
            ["Alt-Left"]
            "Move cursor left one form"
            edit/hop-left)

(defcommand :cursor/hop-right
            ["Alt-Right"]
            "Move cursor right one form"
            edit/hop-right)

(defcommand :selection/expand
            ["Cmd-]" "Cmd-1"]
            "Select parent form, or form under cursor"
            edit/expand-selection)

(defcommand :selection/shrink
            ["Cmd-[" "Cmd-2"]
            "Select child of current form (remembers :expand-selection history)"
            edit/shrink-selection)

(defcommand :comment/line
            ["Cmd-/"]
            "Comment the current line"
            edit/comment-line)

(defcommand :comment/uneval-form
            ["Cmd-;"]
            ""
            edit/uneval-form)

(defcommand :comment/uneval-top-level-form
            ["Cmd-Shift-;"]
            ""
            edit/uneval-top-level-form)

(defcommand :form/slurp
            ["Shift-Cmd-K"]
            ""
            edit/slurp)

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
            ["Cmd-Enter"]
            "Evaluate the current form"
            (fn [editor] (eval-scope editor :bracket)))

(defcommand :eval/top-level
            ["Shift-Cmd-Enter"]
            "Evaluate the top-level form"
            (fn [editor] (eval-scope editor :top-level)))

(defcommand :eval/doc
            ["Option-Cmd-Enter"]
            "Evaluate whole doc"
            (fn [editor] (eval-to-repl (.getValue editor))))

(defcommand :eval/on-click
            ["Option-Click"]
            "Evaluate the clicked form"
            (fn [editor]
              (eval-scope editor :bracket)
              true))