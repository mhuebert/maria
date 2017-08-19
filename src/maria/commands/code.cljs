(ns maria.commands.code
  (:require [maria-commands.registry :refer-macros [defcommand]]
            [maria.repl-specials :as repl-specials]
            [maria.eval :as e]
            [magic-tree.core :as tree]
            [magic-tree-codemirror.util :as cm]
            [magic-tree-codemirror.edit :as edit]
            [fast-zip.core :as z]
            [clojure.set :as set]
            [maria.blocks.blocks :as Block]
            [maria.util :as util]))

(def pass #(.-Pass js/CodeMirror))

(def selection? #(and (:block/code %)
                      (some-> (:editor %) (.somethingSelected))))
(def no-selection? #(and (:block/code %)
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
   :when     :block/code}
  [context]
  (edit/cut-form (:editor context)))



(defcommand :code-block/enter
  {:bindings "Enter"
   :when     :block/code}
  [{:keys [block-view editor block-list block blocks]}]
  (let [last-line (.lastLine editor)
        {cursor-line :line
         cursor-ch   :ch} (util/js-lookup (.getCursor editor))
        empty-block (Block/empty? block)]
    (if-let [edge-position (cond empty-block :empty
                                 (and (= cursor-line last-line)
                                      (= cursor-ch (count (.getLine editor last-line))))
                                 :end
                                 (and (= cursor-line 0)
                                      (= cursor-ch 0))
                                 :start
                                 :else nil)]
      (let [adjacent-block ((case edge-position
                              :end Block/after
                              (:empty :start) Block/before) blocks block)
            prev-prose (when (satisfies? Block/IParagraph adjacent-block)
                         adjacent-block)
            new-block (when-not prev-prose
                        (Block/create :prose))]
        (case edge-position
          :end (do
                 (.splice block-list block (if prev-prose 1 0)
                          (cond-> []
                                  (not (Block/empty? block)) (conj block)
                                  true (conj (or new-block
                                                 prev-prose))))

                 (when (satisfies? Block/IParagraph prev-prose)
                   (Block/prepend-paragraph prev-prose))
                 (Block/focus! (or new-block prev-prose) :start))

          (:empty :start)
          (do (.splice block-list block (cond-> []
                                                new-block (conj new-block)
                                                (not empty-block) (conj block)))
              (when empty-block
                (Block/focus! (or new-block prev-prose) :end)))))
      js/CodeMirror.Pass)))

(defcommand :delete/form
  "Deletes current highlight"
  {:bindings ["M1-Backspace"
              "M1-Shift-Backspace"]
   :when     :block/code}
  [context]
  (edit/delete-form (:editor context)))

(defn clear-empty-code-block [block-list blocks block]
  (let [before (Block/before blocks block)
        replacement (when-not before (Block/create :prose))]
    (.splice block-list block (if replacement [replacement] []))
    (Block/focus! (or before replacement) :end)))

(defcommand :delete/code
  "Delete"
  {:bindings ["Backspace"]
   :when     :block/code}
  [{:keys [block-list block blocks]}]
  (let [before (Block/before blocks block)]
    (cond
      (and (Block/at-start? block) (and before (Block/empty? before)))
      (.splice block-list block -1 [block])

      (Block/empty? block)
      (clear-empty-code-block block-list blocks block)

      :else false)))

(defcommand :navigate/hop-left
  "Move cursor left one form"
  {:bindings ["M2-Left"]
   :when     :block/code}
  [context]
  (edit/hop-left (:editor context)))

(defcommand :navigate/hop-right
  "Move cursor right one form"
  {:bindings ["M2-Right"]
   :when     :block/code}
  [context]
  (edit/hop-right (:editor context)))

(defcommand :navigate/jump-to-top
  "Move cursor to top of current doc"
  {:bindings "M1-Up"
   :when     :block/code})

(defcommand :navigate/jump-to-bottom
  "Move cursor to bottom of current doc"
  {:bindings "M1-Down"
   :when     :block/code})

(defcommand :comment/line
  "Comment the current line"
  {:bindings ["M1-/"]
   :when     :block/code}
  [context]
  (edit/comment-line (:editor context)))

(defcommand :comment/uneval-form
  {:bindings ["M1-;"
              "M1-Shift-;"]
   :when     :block/code}
  [context]
  (edit/uneval-form (:editor context)))

(defcommand :edit/slurp
  {:bindings ["M1-Shift-K"]
   :when     :block/code}
  [context]
  (edit/slurp (:editor context)))

(defcommand :edit/kill
  "Cuts to end of line"
  {:bindings ["M1-K"]
   :when     :block/code}
  [context]
  (edit/kill (:editor context)))

(defcommand :eval/form
  "Evaluate the current form"
  {:bindings ["M1-Enter"
              "M1-Shift-Enter"]
   :when     :block/code}
  [{:keys [editor block-view block]}]
  (Block/eval block))

#_(defcommand :eval/on-click
    "Evaluate the clicked form"
    {:bindings ["Option-Click"]
     :when     :block/code}
    [{:keys [block-view]}]
    (eval-scope (.getEditor block-view) :bracket))

(defcommand :meta/info
  "Show documentation for current form"
  {:bindings ["M1-I"
              "M1-Shift-I"]
   :when     #(and (:block/code %)
                   (some-> % :editor :magic/cursor :bracket-loc z/node))}
  [{:keys [block-view editor block]}]
  (let [form (some-> editor :magic/cursor :bracket-loc z/node tree/sexp)]
    (if (and (symbol? form) (repl-specials/resolve-var-or-special e/c-state e/c-env form))
      (Block/eval block :form (list 'doc form))
      (Block/eval block :form (list 'maria.messages/what-is (list 'quote form))))))

(defcommand :meta/source
  "Show source code for the current var"
  {:bindings ["M1-Shift-S"]
   :when     #(and (:block/code %)
                   (some->> (:editor %) :magic/cursor :bracket-loc z/node tree/sexp symbol?))}
  [{:keys [block editor]}]
  (Block/eval block :form (list 'source
                                (some-> editor :magic/cursor :bracket-loc z/node tree/sexp))))

(defcommand :meta/javascript-source
  "Show compiled javascript for current form"
  {:bindings ["M1-Shift-J"]}
  [{:keys [block editor]}]
  (e/log-eval-result! (some-> editor :magic/cursor :bracket-loc z/node tree/string e/compile-str (set/rename-keys {:compiled-js :value}))))

