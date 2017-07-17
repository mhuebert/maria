(ns maria.commands.commands
  (:require [maria.commands.registry :refer-macros [defcommand]]
            [maria.views.pages.repl :as repl]
            [maria.repl-specials :as repl-specials]
            [maria.eval :as eval]
            [magic-tree.core :as tree]
            [magic-tree-codemirror.util :as cm]
            [magic-tree-codemirror.edit :as edit]
            [fast-zip.core :as z]
            [maria.live.ns-utils :as ns-utils]
            [clojure.set :as set]))

(def pass #(.-Pass js/CodeMirror))

(def selection? #(some-> (:editor %) (.somethingSelected)))
(def no-selection? #(some-> (:editor %) (.somethingSelected) (not)))

(defcommand :copy/form
  {:bindings ["Command-C"
              "Control-C"]
   :when     no-selection?}
  [{:keys [editor]}]
  (edit/copy-form editor))

(defcommand :copy/selection
  {:bindings ["Command-C"
              "Control-C"]
   :when     selection?})

(defcommand :cut/line
  "Kill: cuts to end of line, preserving bracket structure"
  {:bindings ["Command-K"]
   :when     :editor}
  [{:keys [editor]}]
  (edit/kill editor))

(defcommand :cut/selection
  {:bindings ["Command-X"
              "Control-X"]
   :when     selection?})

(defcommand :cut/form
  "Cuts current highlight"
  {:bindings ["Command-X"
              "Control-X"]
   :when     no-selection?}
  [{:keys [editor]}]
  (edit/cut-form editor))

(defcommand :delete/form
  "Deletes current highlight"
  {:bindings ["Command-Backspace"]
   :when     no-selection?}
  [{:keys [editor]}]
  (edit/delete-form editor))

(defcommand :delete/selection
  {:bindings ["Command-Backspace"]
   :when     selection?})

(defcommand :cursor/hop-left
  "Move cursor left one form"
  {:bindings ["Option-Left"]
   :when     :editor}
  [{:keys [editor]}]
  (edit/hop-left editor))

(defcommand :cursor/hop-right
  "Move cursor right one form"
  {:bindings ["Option-Right"]
   :when     :editor}
  [{:keys [editor]}]
  (edit/hop-right editor))

(defcommand :cursor/jump-to-top
  "Move cursor to top of current doc"
  {:bindings "Command-Up"
   :when     :editor})

(defcommand :cursor/jump-to-bottom
  "Move cursor to bottom of current doc"
  {:bindings "Command-Down"
   :when     :editor})

(defcommand :selection/expand
  "Select parent form, or form under cursor"
  {:bindings ["Command-]" "Command-1"]
   :when     :editor}
  [{:keys [editor]}]
  (edit/expand-selection editor))

(defcommand :selection/shrink
  "Select child of current form (remembers :expand-selection history)"
  {:bindings ["Command-[" "Command-2"]
   :when     :editor}
  [{:keys [editor]}]
  (edit/shrink-selection editor))

(defcommand :comment/line
  "Comment the current line"
  {:bindings ["Command-/"]
   :when     :editor}
  [{:keys [editor]}]
  (edit/comment-line editor))

(defcommand :comment/uneval-form
  {:bindings ["Command-;"]
   :when     :editor}
  [{:keys [editor]}]
  (edit/uneval-form editor))

(defcommand :comment/uneval-top-level-form
  {:bindings ["Command-Shift-;"]
   :when     :editor}
  [{:keys [editor]}]
  (edit/uneval-top-level-form editor))

(defcommand :form/slurp
  {:bindings ["Command-Shift-K"]
   :when     :editor}
  [{:keys [editor]}]
  (edit/slurp editor))

(defn eval-scope [cm scope]
  (let [traverse (case scope :top-level tree/top-loc
                             :bracket identity)]
    (when-let [source (or (cm/selection-text cm)
                          (->> cm
                               :magic/cursor
                               :bracket-loc
                               (traverse)
                               (tree/string (:ns @eval/c-env))))]

      (repl/eval-str-to-repl source))))

(defcommand :eval/form
  "Evaluate the current form"
  {:bindings ["Command-Enter"]
   :when     :editor}
  [{:keys [editor]}]
  (eval-scope editor :bracket))

(defcommand :eval/top-level
  "Evaluate the top-level form"
  {:bindings ["Command-Shift-Enter"]
   :when     :editor}
  [{:keys [editor]}]
  (eval-scope editor :top-level))

(defcommand :eval/doc
  "Evaluate whole doc"
  {:bindings ["Command-Option-Enter"]
   :when     :editor}
  [{:keys [editor]}]
  (repl/eval-str-to-repl (.getValue editor)))

(defcommand :eval/on-click
  "Evaluate the clicked form"
  {:bindings ["Option-Click"]
   :when     :editor}
  [{:keys [editor]}]
  (eval-scope editor :bracket))

(defcommand :form/doc
  "Show documentation for current form"
  {:bindings ["Command-Shift-D"]
   :when     #(some-> % :editor :magic/cursor :bracket-loc z/node)}
  [{editor :editor}]
  (let [node (some-> editor :magic/cursor :bracket-loc z/node)
        sexp (some-> node tree/sexp)]
    (if (and (symbol? sexp) (repl-specials/resolve-var-or-special eval/c-state eval/c-env sexp))
      (repl/eval-to-repl (list 'doc sexp))
      (repl/eval-to-repl (list 'maria.messages/what-is (list 'quote sexp))))))

(defcommand :form/source
  "Show source code for the current var"
  {:bindings ["Command-Shift-S"]
   :when     #(some->> % :editor :magic/cursor :bracket-loc z/node tree/sexp symbol?)}
  [{editor :editor}]
  (repl/eval-to-repl (list 'source (some-> editor :magic/cursor :bracket-loc z/node tree/sexp))))

(defcommand :form/javascript-source
  "Show compiled javascript for current form"
  {:bindings ["Command-Shift-J"]}
  [{editor :editor}]
  (repl/add-to-repl-out! (some-> editor :magic/cursor :bracket-loc z/node tree/string eval/compile-str (set/rename-keys {:compiled-js :value}))))
