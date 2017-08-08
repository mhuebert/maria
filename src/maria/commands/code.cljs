(ns maria.commands.code
  (:require [maria-commands.registry :refer-macros [defcommand]]
            [maria.repl-specials :as repl-specials]
            [maria.eval :as e]
            [magic-tree.core :as tree]
            [magic-tree-codemirror.util :as cm]
            [magic-tree-codemirror.edit :as edit]
            [fast-zip.core :as z]
            [clojure.set :as set]
            [maria.cells.core :as Cell]
            [re-db.d :as d]
            [maria.util :as util]))

(def pass #(.-Pass js/CodeMirror))

(def selection? #(and (:cell/code %)
                      (some-> (:editor %) (.somethingSelected))))
(def no-selection? #(and (:cell/code %)
                         (some-> (:editor %) (.somethingSelected) (not))))

(defcommand :copy/form
  {:bindings ["M1-C"
              "M1-Shift-C"]
   :when     no-selection?}
  [context]
  (edit/copy-form (:editor context)))

(defcommand :copy/selection
  {:bindings ["M1-C"
              "M1-Shift-C"]
   :when     selection?})

(defcommand :cut/form
  "Cuts current highlight"
  {:bindings ["M1-X"
              "M1-Shift-X"]
   :when     :cell/code}
  [context]
  (edit/cut-form (:editor context)))


(defcommand :code-cell/enter
  {:bindings "Enter"
   :when     :cell/code}
  [{:keys [cell-view editor cell-list cell cells]}]
  (let [last-line (.lastLine editor)
        {cursor-line :line
         cursor-ch   :ch} (util/js-lookup (.getCursor editor))]
    (if-let [edge-position (cond (and (= cursor-line last-line)
                                      (= cursor-ch (count (.getLine editor last-line))))
                                 :end
                                 (and (= cursor-line 0)
                                      (= cursor-ch 0))
                                 :start
                                 :else nil)]
      (let [adjacent-cell ((case edge-position
                             :end Cell/after
                             :start Cell/before) cells cell)
            adjacent-prose (when (satisfies? Cell/IText adjacent-cell)
                             adjacent-cell)
            new-cell (when-not adjacent-prose
                       (Cell/->ProseCell (d/unique-id) ""))]
        (case edge-position
          :end (do
                 (.splice cell-list cell (if adjacent-prose 1 0)
                          (cond-> []
                                  (not (Cell/empty? cell)) (conj cell)
                                  true (conj (or new-cell
                                                 adjacent-prose))))

                 (when (satisfies? Cell/IText adjacent-prose)
                   (Cell/prepend-paragraph adjacent-prose))
                 (Cell/focus! (or new-cell adjacent-prose) :start))
          :start (.splice cell-list cell (cond-> []
                                                 new-cell (conj new-cell)
                                                 true (conj cell)))))
      js/CodeMirror.Pass)))

(defcommand :delete/form
  "Deletes current highlight"
  {:bindings ["M1-Backspace"
              "M1-Shift-Backspace"]
   :when     :cell/code}
  [context]
  (edit/delete-form (:editor context)))

(defcommand :delete/code
  "Delete"
  {:bindings ["Backspace"]
   :when     :cell/code}
  [{:keys [cell-list cell cells]}]
  (let [before (Cell/before cells cell)]
    (cond
      (and (Cell/at-start? cell) (and before (Cell/empty? before)))
      (.splice cell-list cell -1 [cell])


      (Cell/empty? cell)
      (let [new-cell (when-not before (Cell/->ProseCell (d/unique-id) ""))]
        (.splice cell-list cell (if new-cell [new-cell] []))

        (Cell/focus! (or before new-cell) :end))
      :else js/CodeMirror.Pass)))

(defcommand :navigate/hop-left
  "Move cursor left one form"
  {:bindings ["M2-Left"]
   :when     :cell/code}
  [context]
  (edit/hop-left (:editor context)))

(defcommand :navigate/hop-right
  "Move cursor right one form"
  {:bindings ["M2-Right"]
   :when     :cell/code}
  [context]
  (edit/hop-right (:editor context)))

(defcommand :navigate/jump-to-top
  "Move cursor to top of current doc"
  {:bindings "M1-Up"
   :when     :cell/code})

(defcommand :navigate/jump-to-bottom
  "Move cursor to bottom of current doc"
  {:bindings "M1-Down"
   :when     :cell/code})

(defcommand :comment/line
  "Comment the current line"
  {:bindings ["M1-/"]
   :when     :cell/code}
  [context]
  (edit/comment-line (:editor context)))

(defcommand :comment/uneval-form
  {:bindings ["M1-;"
              "M1-Shift-;"]
   :when     :cell/code}
  [context]
  (edit/uneval-form (:editor context)))

(defcommand :edit/slurp
  {:bindings ["M1-Shift-K"]
   :when     :cell/code}
  [context]
  (edit/slurp (:editor context)))

(defcommand :edit/kill
  "Cuts to end of line"
  {:bindings ["M1-K"]
   :when     :cell/code}
  [context]
  (edit/kill (:editor context)))

(defcommand :eval/form
  "Evaluate the current form"
  {:bindings ["M1-Enter"
              "M1-Shift-Enter"]
   :when     :cell/code}
  [{:keys [editor cell-view]}]
  (when-let [source (or (cm/selection-text editor)
                        (->> editor
                             :magic/cursor
                             :bracket-loc
                             (tree/string (:ns @e/c-env))))]

    (e/logged-eval-str (:id cell-view) source)))

#_(defcommand :eval/on-click
    "Evaluate the clicked form"
    {:bindings ["Option-Click"]
     :when     :cell/code}
    [{:keys [cell-view]}]
    (eval-scope (.getEditor cell-view) :bracket))

(defcommand :meta/info
  "Show documentation for current form"
  {:bindings ["M1-I"
              "M1-Shift-I"]
   :when     #(and (:cell/code %)
                   (some-> % :editor :magic/cursor :bracket-loc z/node))}
  [{:keys [cell-view editor cell]}]
  (let [form (some-> editor :magic/cursor :bracket-loc z/node tree/sexp)]
    (if (and (symbol? form) (repl-specials/resolve-var-or-special e/c-state e/c-env form))
      (e/logged-eval-form (:id cell) (list 'doc form))
      (e/logged-eval-form (:id cell) (list 'maria.messages/what-is (list 'quote form))))))

(defcommand :meta/source
  "Show source code for the current var"
  {:bindings ["M1-Shift-S"]
   :when     #(and (:cell/code %)
                   (some->> (:editor %) :magic/cursor :bracket-loc z/node tree/sexp symbol?))}
  [{:keys [cell editor]}]
  (e/logged-eval-form (:id cell) (list 'source
                                       (some-> editor :magic/cursor :bracket-loc z/node tree/sexp))))

(defcommand :meta/javascript-source
  "Show compiled javascript for current form"
  {:bindings ["M1-Shift-J"]}
  [{:keys [cell editor]}]
  (e/log-eval-result! (:id cell)
                      (some-> editor :magic/cursor :bracket-loc z/node tree/string e/compile-str (set/rename-keys {:compiled-js :value}))))

