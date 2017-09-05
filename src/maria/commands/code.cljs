(ns maria.commands.code
  (:require [maria-commands.registry :as registry :refer-macros [defcommand]]
            [maria.repl-specials :as repl-specials]
            [maria.eval :as e]
            [magic-tree.core :as tree]
            [magic-tree-editor.edit :as edit :include-macros true]
            [magic-tree-editor.codemirror :as cm]
            [fast-zip.core :as z]
            [clojure.set :as set]
            [maria.blocks.blocks :as Block]
            [maria.blocks.prose :as Prose]
            [maria.block-views.editor :as Editor]
            [maria.util :as util]
            [maria.live.ns-utils :as ns-utils]
            [maria.views.floating-hint :as hint]))

(def pass #(.-Pass js/CodeMirror))

(def selection? #(and (:block/code %)
                      (some-> (:editor %) (.somethingSelected))))
(def no-selection? #(and (:block/code %)
                         (some-> (:editor %) (.somethingSelected) (not))))

(defcommand :copy/selection
  {:bindings ["M1-C"
              "M1-Shift-C"]
   :when     selection?})

(defcommand :selection/paste
  {:bindings       ["M1-v"]
   :intercept-when false}
  [{:keys [editor]}]
  (when-let [pos (cm/cursor-root editor)]
    (.setCursor editor pos)
    false))

(defcommand :selection/expand-left
  {:bindings ["M1-Left"]
   :when     :block/code}
  [context]
  (edit/expand-selection-left (:editor context)))

(defcommand :selection/cursor-left-edge
  {:bindings ["M1-Shift-Left"]
   :when     :block/code}
  [{:keys [editor]}]
  (edit/cursor-selection-edge editor :left))

(defcommand :selection/cursor-right-edge
  {:bindings ["M1-Shift-Right"]
   :when     :block/code}
  [{:keys [editor]}]
  (edit/cursor-selection-edge editor :right))

(defcommand :selection/expand-right
  {:bindings ["M1-Right"]
   :when     :block/code}
  [context]
  (edit/expand-selection-right (:editor context)))

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
                              :end Block/right
                              (:empty :start) Block/left) blocks block)
            adjacent-prose (when (= :prose (some-> adjacent-block (Block/kind)))
                             adjacent-block)
            new-block (when-not adjacent-prose
                        (Block/create :prose))]
        (case edge-position
          :end (do
                 (.splice block-list block adjacent-prose
                          (cond-> []
                                  (not (Block/empty? block)) (conj block)
                                  true (conj (or new-block
                                                 adjacent-prose))))

                 (some-> adjacent-prose (Prose/prepend-paragraph))
                 (-> (or new-block adjacent-prose)
                     (Editor/of-block)
                     (Editor/focus! :start)))

          (:empty :start)
          (do (.splice block-list block (cond-> []
                                                new-block (conj new-block)
                                                (not empty-block) (conj block)))
              (when empty-block
                (some-> (or new-block adjacent-prose)
                        (Editor/of-block)
                        (Editor/focus! :end))))))
      js/CodeMirror.Pass)))

(defcommand :delete/selection
  "Deletes current selection"
  {:bindings ["M1-Backspace"
              "M1-Shift-Backspace"]
   :when     :block/code}
  [context]
  (let [editor (:editor context)]
    (when (.somethingSelected editor)
      (.replaceSelections editor (to-array (repeat (count (.getSelections editor)) ""))))))

(defn clear-empty-code-block [block-list blocks block]
  (let [before (Block/left blocks block)
        replacement (when-not before (Block/create :prose))]
    (.splice block-list block (if replacement [replacement] []))
    (-> (or before replacement)
        (Editor/of-block)
        (Editor/focus! :end))))

(defcommand :edit/auto-close
  {:bindings ["["
              "Shift-["
              "Shift-9"
              "Shift-'"]
   :when     :block/code}
  [{:keys [editor key]}]
  (edit/operation editor
                  (when-let [other-bracket (edit/other-bracket key)]
                    (let [in-string? (let [{:keys [node pos]} (:magic/cursor editor)]
                                       (and (= :string (:tag node))
                                            (tree/inside? node pos)))
                          ;; if in a string, escape quotes and do not autoclose
                          [insertion-text forward] (if in-string?
                                                     (if (= key \") ["\\\"" 2] [key 1])
                                                     [(str key other-bracket) 1])]
                      (-> (edit/pointer editor)
                          (edit/insert! insertion-text)
                          (edit/move forward)
                          (edit/set-editor-cursor!))))))

(defcommand :delete/code
  "Delete"
  {:bindings ["Backspace"]
   :when     :block/code}
  [{:keys [block-list block blocks editor]}]
  (let [before (Block/left blocks block)
        pointer (edit/pointer editor)
        prev-char (edit/get-range pointer -1)]
    (cond
      (and (Editor/at-start? editor) (and before (Block/empty? before)))
      (.splice block-list before block [block])

      (Block/empty? block)
      (clear-empty-code-block block-list blocks block)

      (.somethingSelected editor) false

      (#{")" "]" "}"} prev-char) (-> pointer
                                     (edit/move -1)
                                     (edit/set-editor-cursor!))
      (#{\( \[ \{ \"} prev-char) (edit/operation editor
                                                 (edit/unwrap editor)
                                                 (edit/set-editor-cursor! (edit/move pointer -1)))

      :else false)))

(defcommand :hint/completions
  {:bindings ["M1-J"]
   :when     :block/code}
  [{:keys [editor block]}]
  (let [node (get-in editor [:magic/cursor :bracket-node])]
    (when (and (#{:symbol :token} (:tag node))
               (= (tree/bounds node :right)
                  (cm/pos->boundary (cm/get-cursor editor) :left)))
      (hint/floating-hint! {:element (for [[completion namespace] (ns-utils/ns-completions (tree/string node))]
                                       [:.flex.items-center (str completion) [:.flex-auto] [:.gray (str namespace)]])
                            :rect    (Editor/cursor-coords editor)}))))

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

#_(do
    (defcommand :navigate/jump-to-top
      "Move cursor to top of current doc"
      {:bindings "M1-Up"
       :when     :block/code})

    (defcommand :navigate/jump-to-bottom
      "Move cursor to bottom of current doc"
      {:bindings "M1-Down"
       :when     :block/code}))

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
  (edit/uneval (:editor context)))

(defcommand :edit/slurp
  {:bindings ["M1-Shift-K"]
   :when     :block/code}
  [context]
  (edit/slurp (:editor context)))

(defcommand :edit/kill
  "Cuts to end of line"
  {:bindings ["M3-K"]
   :when     :block/code}
  [context]
  (edit/kill (:editor context)))

(defcommand :edit/unwrap
  "Splice form"
  {:bindings ["M2-S"]
   :when     :block/code}
  [context]
  (edit/unwrap (:editor context)))

(defcommand :edit/raise
  "Splice form"
  {:bindings ["M2-Shift-S"]
   :when     :block/code}
  [context]
  (edit/raise (:editor context)))

(defcommand :eval/form
  "Evaluate the current form"
  {:bindings ["M1-Enter"
              "M1-Shift-Enter"]
   :when     :block/code}
  [{:keys [editor block-view block]}]
  (Block/eval! block))

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
      (Block/eval! block :form (list 'doc form))
      (Block/eval! block :form (list 'maria.messages/what-is (list 'quote form))))))

(defcommand :meta/source
  "Show source code for the current var"
  {:bindings ["M1-Shift-S"]
   :when     #(and (:block/code %)
                   (some->> (:editor %) :magic/cursor :bracket-loc z/node tree/sexp symbol?))}
  [{:keys [block editor]}]
  (Block/eval! block :form (list 'source
                                 (some-> editor :magic/cursor :bracket-loc z/node tree/sexp))))

(defcommand :meta/javascript-source
  "Show compiled javascript for current form"
  {:bindings ["M1-Shift-J"]}
  [{:keys [block editor]}]
  (Block/eval-log! block (some-> editor :magic/cursor :bracket-loc z/node tree/string e/compile-str (set/rename-keys {:compiled-js :value}))))

