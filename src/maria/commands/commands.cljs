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
              "Control-C"
              "Command-Shift-C"
              "Control-Shift-C"]
   :when     no-selection?}
  [{:keys [editor]}]
  (edit/copy-form editor))

(defcommand :copy/selection
  {:bindings ["Command-C"
              "Control-C"
              "Command-Shift-C"]
   :when     selection?})

(defcommand :cut/form
  "Cuts current highlight"
  {:bindings ["Command-X"
              "Control-X"
              "Command-Shift-X"]
   :when     :editor}
  [{:keys [editor]}]
  (edit/cut-form editor))

(defcommand :delete/form
  "Deletes current highlight"
  {:bindings ["Command-Backspace"
              "Command-Shift-Backspace"]
   :when     :editor}
  [{:keys [editor]}]
  (edit/delete-form editor))

(defcommand :navigate/hop-left
  "Move cursor left one form"
  {:bindings ["Option-Left"]
   :when     :editor}
  [{:keys [editor]}]
  (edit/hop-left editor))

(defcommand :navigate/hop-right
  "Move cursor right one form"
  {:bindings ["Option-Right"]
   :when     :editor}
  [{:keys [editor]}]
  (edit/hop-right editor))

(defcommand :navigate/jump-to-top
  "Move cursor to top of current doc"
  {:bindings "Command-Up"
   :when     :editor})

(defcommand :navigate/jump-to-bottom
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
  {:bindings ["Command-;"
              "Command-Shift-;"]
   :when     :editor}
  [{:keys [editor]}]
  (edit/uneval-form editor))

(defcommand :edit/slurp
  {:bindings ["Command-Shift-K"]
   :when     :editor}
  [{:keys [editor]}]
  (edit/slurp editor))

(defcommand :edit/kill
  "Cuts to end of line"
  {:bindings ["Command-K"
              "Command-Shift-K"]
   :when     :editor}
  [{:keys [editor]}]
  (edit/kill editor))

(defcommand :eval/form
  "Evaluate the current form"
  {:bindings ["Command-Enter"
              "Command-Shift-Enter"]
   :when     :editor}
  [{:keys [editor]}]
  (when-let [source (or (cm/selection-text editor)
                        (->> editor
                             :magic/cursor
                             :bracket-loc
                             (tree/string (:ns @eval/c-env))))]

    (repl/eval-str-to-repl source)))

(defcommand :eval/doc
  "Evaluate whole doc"
  {:bindings ["Command-Option-Enter"]
   :when     :editor}
  [{:keys [editor]}]
  (repl/eval-str-to-repl (.getValue editor)))

#_(defcommand :eval/on-click
    "Evaluate the clicked form"
    {:bindings ["Option-Click"]
     :when     :editor}
    [{:keys [editor]}]
    (eval-scope editor :bracket))

(defn get-info [node]
  (let [sexp (some-> node tree/sexp)]
    (if (and (symbol? sexp) (repl-specials/resolve-var-or-special eval/c-state eval/c-env sexp))
      (repl/eval-to-repl (list 'doc sexp))
      (repl/eval-to-repl (list 'maria.messages/what-is (list 'quote sexp))))))

(defcommand :meta/info
  "Show documentation for current form"
  {:bindings ["Command-I"
              "Command-Shift-I"]
   :when     #(some-> % :editor :magic/cursor :bracket-loc z/node)}
  [{editor :editor}]
  (get-info (some-> editor :magic/cursor :bracket-loc z/node)))

(defcommand :meta/source
  "Show source code for the current var"
  {:bindings ["Command-Shift-S"]
   :when     #(some->> % :editor :magic/cursor :bracket-loc z/node tree/sexp symbol?)}
  [{editor :editor}]
  (repl/eval-to-repl (list 'source (some-> editor :magic/cursor :bracket-loc z/node tree/sexp))))

(defcommand :meta/javascript-source
  "Show compiled javascript for current form"
  {:bindings ["Command-Shift-J"]}
  [{editor :editor}]
  (repl/add-to-repl-out! (some-> editor :magic/cursor :bracket-loc z/node tree/string eval/compile-str (set/rename-keys {:compiled-js :value}))))

