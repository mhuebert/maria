(ns maria.commands.commands
  (:require [maria.commands.registry :refer-macros [defcommand]]
            [maria.views.pages.repl :as repl]
            [maria.repl-specials :as repl-specials]
            [maria.eval :as eval]
            [magic-tree.core :as tree]
            [magic-tree-codemirror.util :as cm]
            [magic-tree-codemirror.edit :as edit]
            [fast-zip.core :as z]
            [clojure.set :as set]))

(def pass #(.-Pass js/CodeMirror))

(def selection? #(some-> (:cell/code %) (:editor) (.somethingSelected)))
(def no-selection? #(some-> (:cell/code %) (:editor) (.somethingSelected) (not)))

(defcommand :copy/form
  {:bindings ["M1-C"
              "M1-Shift-C"]
   :when     no-selection?}
  [{:keys [cell/code]}]
  (edit/copy-form (:editor code)))

(defcommand :copy/selection
  {:bindings ["M1-C"
              "M1-Shift-C"]
   :when     selection?})

(defcommand :cut/form
  "Cuts current highlight"
  {:bindings ["M1-X"
              "M1-Shift-X"]
   :when     :cell/code}
  [{:keys [cell/code]}]
  (edit/cut-form (:editor code)))

(defcommand :delete/form
  "Deletes current highlight"
  {:bindings ["M1-Backspace"
              "M1-Shift-Backspace"]
   :when     :cell/code}
  [{:keys [cell/code]}]
  (edit/delete-form (:editor code)))

(defcommand :navigate/hop-left
  "Move cursor left one form"
  {:bindings ["M2-Left"]
   :when     :cell/code}
  [{:keys [cell/code]}]
  (edit/hop-left (:editor code)))

(defcommand :navigate/hop-right
  "Move cursor right one form"
  {:bindings ["M2-Right"]
   :when     :cell/code}
  [{:keys [cell/code]}]
  (edit/hop-right (:editor code)))

(defcommand :navigate/jump-to-top
  "Move cursor to top of current doc"
  {:bindings "M1-Up"
   :when     :cell/code})

(defcommand :navigate/jump-to-bottom
  "Move cursor to bottom of current doc"
  {:bindings "M1-Down"
   :when     :cell/code})

(defcommand :selection/expand
  "Select parent form, or form under cursor"
  {:bindings ["M1-]" "M1-1"]
   :when     :cell/code}
  [{:keys [cell/code]}]
  (edit/expand-selection (:editor code)))

(defcommand :selection/shrink
  "Select child of current form (remembers :expand-selection history)"
  {:bindings ["M1-[" "M1-2"]
   :when     :cell/code}
  [{:keys [cell/code]}]
  (edit/shrink-selection (:editor code)))

(defcommand :comment/line
  "Comment the current line"
  {:bindings ["M1-/"]
   :when     :cell/code}
  [{:keys [cell/code]}]
  (edit/comment-line (:editor code)))

(defcommand :comment/uneval-form
  {:bindings ["M1-;"
              "M1-Shift-;"]
   :when     :cell/code}
  [{:keys [cell/code]}]
  (edit/uneval-form (:editor code)))

(defcommand :edit/slurp
  {:bindings ["M1-Shift-K"]
   :when     :cell/code}
  [{:keys [cell/code]}]
  (edit/slurp (:editor code)))

(defcommand :edit/kill
  "Cuts to end of line"
  {:bindings ["M1-K"]
   :when     :cell/code}
  [{:keys [cell/code]}]
  (edit/kill (:editor code)))

(defcommand :eval/form
  "Evaluate the current form"
  {:bindings ["M1-Enter"
              "M1-Shift-Enter"]
   :when     :cell/code}
  [{:keys [cell/code]}]
  (when-let [source (or (cm/selection-text (:editor code))
                        (->> (:editor code)
                             :magic/cursor
                             :bracket-loc
                             (tree/string (:ns @eval/c-env))))]

    (repl/eval-str-to-repl source)))

(defcommand :eval/doc
  "Evaluate whole doc"
  {:bindings ["M1-M2-Enter"]
   :when     :cell/code}
  [{:keys [cell/code]}]
  (repl/eval-str-to-repl (.getValue (:editor code))))

#_(defcommand :eval/on-click
    "Evaluate the clicked form"
    {:bindings ["Option-Click"]
     :when     :cell/code}
    [{:keys [cell/code]}]
    (eval-scope (:editor code) :bracket))

(defn get-info [node]
  (let [sexp (some-> node tree/sexp)]
    (if (and (symbol? sexp) (repl-specials/resolve-var-or-special eval/c-state eval/c-env sexp))
      (repl/eval-to-repl (list 'doc sexp))
      (repl/eval-to-repl (list 'maria.messages/what-is (list 'quote sexp))))))

(defcommand :meta/info
  "Show documentation for current form"
  {:bindings ["M1-I"
              "M1-Shift-I"]
   :when     #(some-> % :cell/code :magic/cursor :bracket-loc z/node)}
  [{:keys [cell/code]}]
  (get-info (some-> (:editor code) :magic/cursor :bracket-loc z/node)))

(defcommand :meta/source
  "Show source code for the current var"
  {:bindings ["M1-Shift-S"]
   :when     #(some->> % :cell/code :magic/cursor :bracket-loc z/node tree/sexp symbol?)}
  [{:keys [cell/code]}]
  (repl/eval-to-repl (list 'source (some-> (:editor code) :magic/cursor :bracket-loc z/node tree/sexp))))

(defcommand :meta/javascript-source
  "Show compiled javascript for current form"
  {:bindings ["M1-Shift-J"]}
  [{:keys [cell/code]}]
  (repl/add-to-repl-out! (some-> (:editor code) :magic/cursor :bracket-loc z/node tree/string eval/compile-str (set/rename-keys {:compiled-js :value}))))

