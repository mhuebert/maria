(ns maria.commands.code
  (:require [lark.commands.registry :as registry :refer-macros [defcommand]]
            [maria.eval :as e]
            [lark.tree.core :as tree]
            [lark.tree.emit :as emit]
            [lark.structure.edit :as edit :include-macros true]
            [lark.structure.codemirror :as cm]
            [fast-zip.core :as z]
            [clojure.set :as set]
            [maria.blocks.blocks :as Block]
            [maria.blocks.prose :as Prose]
            [lark.editor :as Editor]
            [maria.live.ns-utils :as ns-utils]
            [goog.events :as events]
            [lark.commands.exec :as exec]
            [maria.util :as util]
            ["codemirror" :as CM]))

(def pass #(do :lark.commands/Pass))

(def selection? #(and (:block/code %)
                      (some-> (:editor %) (.somethingSelected))))
(def no-selection? #(and (:block/code %)
                         (some-> (:editor %) (.somethingSelected) (not))))

(defn format-code [editor]
  (binding [lark.tree.emit/*prettify* true]
    (cm/set-preserve-cursor! editor (-> (.getValue editor)
                                        (tree/ast)
                                        (tree/string)))
    true))

(defcommand :clipboard/copy
  {:bindings ["M1-c"]
   :private  true}
  [{:keys [editor block/code block/prose]}]
  (when code
    (edit/copy (.getSelection editor))
    true))

(defcommand :clipboard/cut
  {:bindings ["M1-x"]
   :private  true
   :when     :block/code}
  [{:keys [editor block/code block/prose]}]
  (edit/copy (.getSelection editor))
  (.replaceSelection editor "")
  true)

(defcommand :clipboard/paste-insert
  {:bindings ["M1-v"]
   :private  true}
  [{:keys [editor block/code]}]
  (when-let [pos (and code (cm/temp-marker-cursor-pos editor))]
    (.setCursor editor pos))
  false)

(defcommand :clipboard/paste-replace
  {:bindings ["M1-Shift-v"]}
  [{:keys [editor block/code]}]
  false)

(defn init-select-by-click [editor]
  (let [in-progress? (volatile! true)
        last-sel (volatile! {:pos nil
                             :loc nil})
        listeners (volatile! [])
        clear-listeners! #(do (doseq [key @listeners]
                                (events/unlistenByKey key))
                              (when @in-progress?
                                (vreset! in-progress? false)))]
    (vswap! listeners into [(events/listen js/window "mousemove"
                                           (fn [e]
                                             (let [cm-pos (Editor/coords-cursor editor (.-clientX e) (.-clientY e))]
                                               (when-not (.-outside cm-pos)
                                                 (let [pos (cm/pos->boundary cm-pos)]
                                                   (when-not (= pos (:pos @last-sel))
                                                     (let [loc (some-> (:zipper editor)
                                                                       (tree/node-at pos))
                                                           loc (some->> loc
                                                                        (cm/cursor-loc pos))]
                                                       (when (and loc (not (= loc (:loc @last-sel))))

                                                         (swap! exec/state exec/clear-which-key)

                                                         (cm/temp-select-node! editor (z/node loc))
                                                         (vreset! last-sel {:pos pos
                                                                            :loc loc})))))))))
                            (events/listen js/window "mouseup" #(when @in-progress?
                                                                  (when-let [node (some-> (:loc @last-sel)
                                                                                          (z/node))]

                                                                    (cm/unset-temp-marker! editor)
                                                                    (.preventDefault %)
                                                                    (.stopPropagation %)
                                                                    (cm/select-range editor node))) true)
                            (events/listen js/window "keyup" #(when-not (registry/M1-down? %)
                                                                (clear-listeners!)))
                            (events/listen js/window "blur" #(clear-listeners!))
                            (events/listen js/window "focus" #(clear-listeners!))])

    (events/listenOnce js/window "keydown" clear-listeners! true)))

(defcommand :select/form-at-cursor
  {:bindings ["M1"]
   :when     :block/code}
  [{:keys [editor]}]
  (when-not (:errors editor)
    (when-not (cm/selection? editor)
      (cm/select-at-cursor editor false))
    (init-select-by-click editor)))


#_(defcommand :select/top-level-form
    {:bindings ["M1-Shift"]
     :when     :block/code}
    [context]
    (cm/select-at-cursor (:editor context) true))

(defcommand :select/left
  "Expand selection to include form to the left."
  {:bindings ["M1-Left"]
   :when     :block/code}
  [{:keys [editor]}]
  (edit/expand-selection-x editor :left))

(defcommand :select/right
  "Expand selection to include form to the right."
  {:bindings ["M1-Right"]
   :when     :block/code}
  [{:keys [editor]}]
  (edit/expand-selection-x editor :right))


(defcommand :navigate/form-start
  "Move cursor to beginning of top-level form."
  {:bindings ["M1-Shift-Up"]
   :when     :block/code}
  [{:keys [editor]}]
  (edit/cursor-selection-edge editor :left))

(defcommand :navigate/form-end
  "Move cursor to end of top-level form."
  {:bindings ["M1-Shift-Down"]
   :when     :block/code}
  [{:keys [editor]}]
  (edit/cursor-selection-edge editor :right))

(defcommand :navigate/line-start
  "Move cursor to beginning of line."
  {:bindings ["M2-Shift-Left"]
   :when     :block/code}
  [{:keys [editor]}]
  (edit/cursor-line-edge editor :left))

(defcommand :navigate/line-end
  "Move cursor to end of line."
  {:bindings ["M2-Shift-Right"]
   :when     :block/code}
  [{:keys [editor]}]
  (edit/cursor-line-edge editor :right))

(defn enter [{:keys [block-view editor block-list block blocks]}]
  (let [last-line (.lastLine editor)
        cursor (.getCursor editor)
        cursor-line (.-line cursor)
        cursor-ch (.-ch cursor)
        empty-block (Block/empty? block)]
    (when-let [edge-position (cond empty-block :empty
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
                     (Editor/focus! :start))
                 true)

          (:empty :start)
          (do (.splice block-list block (cond-> []
                                                new-block (conj new-block)
                                                (not empty-block) (conj block)))
              (when empty-block
                (some-> (or new-block adjacent-prose)
                        (Editor/of-block)
                        (Editor/focus! :end)))
              true))))))

(defcommand :edit/delete-selection
  "Remove the current selection."
  {:bindings ["M1-Backspace"
              "M1-Shift-Backspace"]

   :when     :block/code}
  [context]
  (let [editor (:editor context)]
    (when (.somethingSelected editor)
      (.replaceSelections editor (to-array (repeat (count (.getSelections editor)) ""))))
    true))

(defn clear-empty-code-block [block-list blocks block]
  (let [before (Block/left blocks block)
        replacement (when-not before (Block/create :prose))]
    (.splice block-list block (if replacement [replacement] []))
    (-> (or before replacement)
        (Editor/of-block)
        (Editor/focus! :end)))
  true)

(defcommand :edit/auto-close
  {:bindings ["["
              "Shift-9"                                     ;; (
              "Shift-'"                                     ;; "
              "Shift-["]                                    ;; {
   :private  true
   :when     :block/code}
  [{:keys [editor binding] :as args}]
  (when-not (:errors editor)
    (edit/operation editor
                    (when-let [brackets (get {"["       "[]"
                                              "Shift-9" "()"
                                              "Shift-'" "\"\""
                                              "Shift-[" "{}"} binding)]
                      (let [in-string? (let [{:keys [node pos]} (:magic/cursor editor)]
                                         (and (= :string (:tag node))
                                              (tree/inside? node pos)))
                            ;; if in a string, escape quotes and do not autoclose
                            [insertion-text forward] (if in-string?
                                                       (if (= \" (first brackets)) ["\\\"" 2] [(first brackets) 1])
                                                       [brackets 1])]
                        (when (cm/selection? editor)
                          (cm/replace-range! editor "" (cm/current-selection-bounds editor)))
                        (-> (edit/pointer editor)
                            (edit/insert! insertion-text)
                            (edit/move forward)
                            (edit/set-editor-cursor!)))))))

(defcommand :edit/type-close-bracket
    {:bindings ["]"
                "Shift-0"
                "Shift-]"]
     :private  true
     :when     :block/code}
    [{:keys [editor]}]
    (when-not (:errors editor)
      (edit/cursor-skip! editor :right)))

(defcommand :edit/backspace
  {:bindings ["Backspace"]
   :private  true
   :when     :block/code}
  [{:keys [block-list block blocks editor]}]
  (let [before (Block/left blocks block)
        pointer (edit/pointer editor)
        prev-char (edit/get-range pointer -1)]
    (cond
      (and before
           (Editor/at-start? editor)
           (Block/empty? before)) (.splice block-list before block [block])

      (Block/empty? block) (clear-empty-code-block block-list blocks block)
      (.somethingSelected editor) false

      (= \" prev-char) (do (if (= \" (edit/get-range pointer 1))
                             (-> pointer
                                 (edit/move -1)
                                 (edit/insert! 2 ""))
                             (-> pointer
                                 (edit/move -1)
                                 (edit/set-editor-cursor!)))
                           true)

      (#{:comment :string} (get-in editor [:magic/cursor :node :tag])) false

      (#{")" "]" "}"} prev-char) (do (-> pointer
                                         (edit/move -1)
                                         (edit/set-editor-cursor!))
                                     true)
      (#{\( \[ \{} prev-char) (do (edit/operation editor
                                                  (edit/unwrap! editor)
                                                  (edit/backspace! editor)) true)

      (util/whitespace-string? (edit/get-range pointer (- (.-ch (:pos pointer)))))
      (edit/operation editor
                      (.replaceRange editor " "
                                     (:pos pointer)
                                     (:pos (edit/move-while pointer -1 #(#{\space \newline \tab} %)))))

      :else false)))


(defcommand :navigate/hop-left
  "Move cursor left one form"
  {:bindings ["M2-Left"]
   :when     :block/code}
  [context]
  (edit/cursor-skip! (:editor context) :left))

(defcommand :navigate/hop-right
  "Move cursor right one form"
  {:bindings ["M2-Right"]
   :when     :block/code}
  [context]
  (edit/cursor-skip! (:editor context) :right))

(defcommand :navigate/jump-to-top
  "Move cursor to top of current block"
  {:bindings ["M2-Up"]
   :when     :block/code}
  [{:keys [editor]}]
  (Editor/set-cursor editor
                     (Editor/start editor)))

(defcommand :navigate/jump-to-bottom
  "Move cursor to bottom of current block"
  {:bindings ["M2-Down"]
   :when     :block/code}
  [{:keys [editor]}]
  (Editor/set-cursor editor
                     (Editor/end editor)))

(defcommand :edit/comment-line
  "Comment the current line"
  {:bindings ["M1-/"]
   :when     :block/code}
  [context]
  (edit/comment-line (:editor context)))

(defcommand :edit/ignore-form
  "Ignore the current form using the `#_` reader macro ('uneval')"
  {:bindings ["M1-;"]
   :when     :block/code}
  [context]
  (edit/uneval! (:editor context)))

(defcommand :edit/slurp-forward
  "Expand current form to include the form to the right."
  {:bindings ["M1-Shift-Right"]
   :when     :block/code}
  [{:keys [editor] :as context}]
  (cm/return-to-temp-marker! editor)
  (edit/slurp-forward editor)
  (format-code editor))

(defcommand :edit/barf-forward
  "Pushes last child of current form out to the right."
  {:bindings ["M1-Shift-Left"]
   :when     :block/code}
  [{:keys [editor]}]
  (cm/return-to-temp-marker! editor)
  (edit/unslurp-forward editor))

(defcommand :edit/kill
  "Cuts to end of current form or line, whichever comes first."
  {:bindings ["M3-K"]
   :when     :block/code}
  [context]
  (edit/kill! (:editor context)))

(defcommand :edit/unwrap
  "Replaces the current form with its contents."
  {:bindings ["M2-S"]
   :when     :block/code}
  [context]
  (edit/unwrap! (:editor context)))

(defcommand :edit/raise
  "Replace the selected form's parent with the selected form."
  {:bindings ["M2-Shift-S"]
   :when     :block/code}
  [context]
  (edit/raise! (:editor context)))

(defcommand :eval/form
  "Evaluate the current form"
  {:bindings ["M1-Enter"]
   :when     :block/code}
  [{:keys [editor block-view block]}]
  (Block/eval! block))

(defcommand :eval/top-level
  "Evaluate the current top-level form"
  {:bindings ["Shift-Enter"
              "M1-Shift-Enter"]
   :when     :block/code}
  [{:keys [editor block-view block]}]
  (when-let [loc (some->> (:loc (:magic/cursor editor))
                          (tree/top-loc))]
    (cm/temp-select-node! editor (z/node loc))
    (js/setTimeout #(cm/return-to-temp-marker! editor) 210)
    (Block/eval! block)))

#_(defcommand :eval/on-click
    "Evaluate the clicked form"
    {:bindings ["Option-Click"]
     :when     :block/code}
    [{:keys [block-view]}]
    (eval-scope (.getEditor block-view) :bracket))

(defcommand :info/doc
  "Show documentation for current form."
  {:bindings ["M1-I"]
   :when     :block/code}
  [{:keys [block-view editor block]}]
  (when-let [form (some-> editor :magic/cursor :bracket-loc z/node tree/sexp)]
    (if (and (symbol? form) (ns-utils/resolve-var-or-special e/c-state e/c-env form))
      (Block/eval! block :form (list 'doc form))
      (Block/eval! block :form (list 'maria.friendly.kinds/what-is (list 'quote form))))))

(defcommand :info/source
  "Show source code for the current var"
  {:when #(and (:block/code %)
               (some->> (:editor %) :magic/cursor :bracket-loc z/node tree/sexp symbol?))}
  [{:keys [block editor]}]
  (Block/eval! block :form (list 'source
                                 (some-> editor :magic/cursor :bracket-loc z/node tree/sexp))))

(defcommand :info/javascript-source
  "Show compiled javascript for current form"
  {:bindings ["M1-Shift-J"]
   :when     :block/code}
  [{:keys [block editor]}]
  (when-let [node (some-> editor :magic/cursor :bracket-loc z/node)]
    (Block/eval-log! block (some-> node tree/string e/compile-str (set/rename-keys {:compiled-js :value})))))

(defcommand :edit/format-code
  {:bindings ["M2-Tab"]
   :when     :block/code}
  [{:keys [editor]}]
  (format-code editor))


