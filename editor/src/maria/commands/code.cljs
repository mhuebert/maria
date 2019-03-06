(ns maria.commands.code
  (:require [lark.commands.registry :as registry :refer-macros [defcommand]]
            [maria.eval :as e]
            [lark.tree.core :as tree]
            [lark.tree.format :as format]
            [lark.structure.edit :as edit :include-macros true]
            [lark.editors.codemirror :as cm]
            [fast-zip.core :as z]
            [clojure.set :as set]
            [maria.blocks.blocks :as Block]
            [lark.editor :as Editor]
            [maria.live.ns-utils :as ns-utils]
            [goog.events :as events]
            [lark.commands.exec :as exec]
            [maria.util :as util]
            ["codemirror" :as CM]
            [lark.tree.nav :as nav]
            [lark.tree.range :as range]
            [lark.tree.reader :as rd]
            [lark.tree.cursor :as cursor]
            [lark.tree.emit :as emit]
            [lark.tree.node :as n]
            [chia.util :as u]
            [cljs.pprint :as pp]
            [applied-science.js-interop :as j]))

(def pass #(do :lark.commands/Pass))

(def selection? #(and (:block/code %)
                      (some-> (:editor %) (.somethingSelected))))
(def no-selection? #(and (:block/code %)
                         (some-> (:editor %) (.somethingSelected) (not))))

(defcommand :clipboard/copy
  {:bindings ["M1-c"]
   :private true}
  [{:keys [editor block/code block/prose]}]
  (when code
    (edit/copy (.getSelection editor))
    true))

(defcommand :clipboard/cut
  {:bindings ["M1-x"]
   :private true
   :when :block/code}
  [{:keys [editor block/code block/prose]}]
  (edit/copy (.getSelection editor))
  (.replaceSelection editor "")
  true)

(defcommand :clipboard/paste
  {:bindings ["M1-v"]
   :private true}
  [{:keys [editor
           event
           block/code
           clipboard-data]}]
  (util/stop! event)

  )

(defn handle-paste [e]
  (when-let [clipboard-code (and (:block/code (exec/get-context))
                                 (util/clipboard-text e))]
    (exec/exec-command :clipboard/paste
                       (merge (exec/get-context)
                              {:clipboard-data clipboard-code
                               :event e}))
    false))

(defonce _ (.addEventListener js/document.body "paste" #(#'handle-paste %) true))

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
                                                                       (nav/navigate pos))
                                                           loc (some->> loc
                                                                        (cm/sexp-near pos))]
                                                       (when (and loc (not (= loc (:loc @last-sel))))

                                                         (swap! exec/WHICH_KEY_STATE exec/clear-which-key)

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
   :when :block/code}
  [{:keys [editor]}]
  (when-not (cm/selection? editor)
    (cm/select-at-cursor editor false))
  (init-select-by-click editor))


(defcommand :highlight/parent-collection
  {:bindings ["M1-Shift"]
   :when :block/code}
  [context]

  #_(cm/select-at-cursor (:editor context) true))

(defcommand :select/left
  "Expand selection to include form to the left."
  {:bindings ["M1-Left"]
   :when :block/code}
  [{:keys [editor]}]
  (edit/expand-selection-x editor :left))

(defcommand :select/right
  "Expand selection to include form to the right."
  {:bindings ["M1-Right"]
   :when :block/code}
  [{:keys [editor]}]
  (edit/expand-selection-x editor :right))


(defcommand :navigate/form-start
  "Move cursor to beginning of top-level form."
  {:bindings ["M1-Shift-Up"]
   :when :block/code}
  [{:keys [editor]}]
  (edit/cursor-selection-edge editor :left))

(defcommand :navigate/form-end
  "Move cursor to end of top-level form."
  {:bindings ["M1-Shift-Down"]
   :when :block/code}
  [{:keys [editor]}]
  (edit/cursor-selection-edge editor :right))

(defcommand :navigate/line-start
  "Move cursor to beginning of line."
  {:bindings ["M2-Shift-Left"]
   :when :block/code}
  [{:keys [editor]}]
  (edit/cursor-line-edge editor :left))

(defcommand :navigate/line-end
  "Move cursor to end of line."
  {:bindings ["M2-Shift-Right"]
   :when :block/code}
  [{:keys [editor]}]
  (edit/cursor-line-edge editor :right))

(defn split-block [{:as context
                    :keys [editor block-list block]} pos]
  (let [pointer (edit/pointer editor pos)
        end (Editor/end editor)
        content (.getRange editor (:pos pointer) end)
        new-block (Block/from-ast (tree/ast content))]
    (.splice block-list block [block new-block])
    (.replaceRange editor
                   ""
                   (:pos (edit/move-while! pointer -1 #(re-matches #"\s" %)))
                   (update end :column inc))
    (-> new-block
        (Editor/of-block)
        (Editor/focus! :start))
    true))

(defn enter [{:keys [editor block-list block blocks] :as context
              {{:keys [pos loc node]} :magic/cursor} :editor}]
  (let [pointer (edit/pointer editor)
        adjacent-prose (some-> (Block/left blocks block)
                               (util/guard-> #(= :prose (Block/kind %))))]
    (cond (Block/empty? block)
          (let [new-block (when-not adjacent-prose (Block/create :prose))]
            (.splice block-list block (if adjacent-prose [] [new-block]))
            (some-> (or new-block adjacent-prose)
                    (Editor/of-block)
                    (Editor/focus! :end)))

          (or (Editor/at-end? editor)
              (and (= \newline (edit/get-range pointer -1))
                   (= :base (some-> (z/up loc)
                                    (z/node)
                                    (:tag)))))
          (split-block context (.getCursor editor))

          (Editor/at-start? editor)
          (.splice block-list block (-> (if adjacent-prose [] [(Block/create :prose)])
                                        (conj block)))

          :else
          (do
            ;; TODO
            ;; this will cause two separate .setValue CodeMirror firings.
            ;; can be vastly improved.
            (edit/with-formatting editor
              (edit/insert! pointer \newline))))))

(defcommand :edit/delete-selection
  "Remove the current selection."
  {:bindings ["M1-Backspace"
              "M1-Shift-Backspace"]

   :when :block/code}
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

(defn expected-brackets [loc cursor-pos expected-direction]
  (some->> (case expected-direction
             ;; TODO
             ;; edge cases where we find a matching bracket on the wrong side?
             :forward (cons loc (nav/left-locs loc))
             :backward (cons loc (nav/right-locs loc)))
           (keep (fn [loc]
                   (let [{:keys [tag]
                          {:keys [direction expects]} :info} (z/node loc)]
                     (when (and (= tag :unmatched-delimiter)
                                (= direction expected-direction))
                       expects))))))

(defn in-string? [loc cursor-pos]
  (let [node (z/node loc)]
    (and (= :string (:tag node))
         (range/within-inner? node cursor-pos))))

(defcommand :edit/auto-close
  {:bindings ["Shift-'"
              "Shift-["
              "Shift-9"
              "["]
   :private true
   :when :block/code}
  [{:keys [editor binding e event] :as args}]
  (let [brackets (get {"[" "[]"
                       "Shift-9" "()"
                       "Shift-'" "\"\""
                       "Shift-[" "{}"} binding)
        {:keys [loc pos]} (get editor :magic/cursor)]
    (when brackets
      (let [pointer (edit/pointer editor)
            [insertion-text forward] (or (when (in-string? loc pos)
                                           (if (= \" (first brackets))
                                             ["\\\"" 2]
                                             [(first brackets) 1]))
                                         (when-let [expected-bracket (first (expected-brackets loc pos :backward))]
                                           [expected-bracket 1])
                                         (let [[ch-before ch-after] (edit/chars-around pointer)
                                               pad-before (when (format/pad-chars? ch-before (first brackets))
                                                            " ")
                                               pad-after (when (format/pad-chars? (second brackets) ch-after)
                                                           " ")

                                               brackets (str pad-before
                                                             brackets
                                                             pad-after)]
                                           [brackets (cond-> 1
                                                             pad-before (inc))]))]
        (edit/with-formatting editor
          (when (cm/selection? editor)
            (cm/replace-range! editor "" (cm/current-selection-bounds editor)))
          (-> pointer
              (edit/insert! insertion-text)
              (edit/move forward)
              (edit/set-editor-cursor!))
          true)))))

(defcommand :edit/type-close-bracket
  {:bindings ["]"
              "Shift-0"
              "Shift-]"]
   :private true
   :when :block/code}
  [{:keys [editor e]}]
  (let [{:keys [loc pos]} (get editor :magic/cursor)]
    (if (in-string? loc pos)
      false
      (if-let [expected-close-brackets (seq (expected-brackets loc pos :forward))]
        (-> (edit/pointer editor)
            (edit/insert! (first expected-close-brackets)))
        (edit/cursor-skip! editor :right)))))

(defn replace-with-whitespace [loc]
  (let [{:as node
         :keys [end-line end-column]} (z/node loc)]
    (-> loc
        (z/replace (-> (rd/ValueNode :space " ")
                       (assoc :range [end-line end-column end-line (inc end-column)])))
        (z/insert-right (rd/EmptyNode :cursor)))))

(defcommand :edit/backspace
  {:bindings ["Backspace"]
   :private true
   :when :block/code}
  [{:keys [block-list block blocks editor]}]
  (let [before (Block/left blocks block)
        pointer (edit/pointer editor)
        prev-char (edit/get-range pointer -1)
        {:keys [node pos loc]} (get editor :magic/cursor)

        prev-node (some-> (get editor :zipper)
                          (nav/navigate (update pos :column (comp (partial max 0) dec)))
                          (z/node))]

    (cond
      (.somethingSelected editor) false
      (get prev-node :invalid?) false



      (Block/empty? block) (clear-empty-code-block block-list blocks block)

      ;; splice
      (some->> [loc (z/up loc)]
               (sequence (comp (keep identity)
                               (map z/node)
                               (filter n/may-contain-children?)
                               (map range/inner-range)
                               (filter #(range/at-start? pos %))))
               (seq)) (edit/with-formatting editor
                        (edit/unwrap! editor))

      (and before
           (Editor/at-start? editor)) (if (Block/empty? before)
                                        (.splice block-list before block [block])
                                        (when (= (type before) (type block))
                                          (let [editor (Editor/of-block before)
                                                end (Editor/end editor)
                                                pointer (edit/pointer editor end)
                                                pad? (let [ch (edit/get-range pointer -1)]
                                                       (not (re-matches #"\s" ch)))]
                                            (.splice block-list block [])
                                            (-> pointer
                                                (edit/insert! (str (when pad? " ") block))
                                                (cond-> pad? (edit/move 1))
                                                (edit/set-editor-cursor!))
                                            (Editor/focus! editor))
                                          true))

      (= \" prev-char) (do (if (= "" (.-value node))
                             (-> pointer
                                 (edit/move -1)
                                 (edit/insert! 2 ""))
                             (-> pointer
                                 (edit/move -1)
                                 (edit/set-editor-cursor!)))
                           true)

      (contains? #{:comment :string} (get node :tag)) false

      (#{")" "]" "}"} prev-char) (do (-> pointer
                                         (edit/move -1)
                                         (edit/set-editor-cursor!))
                                     true)

      (nav/prev-whitespace-node pos loc)
      (let [ws-loc (nav/prev-whitespace-loc pos loc)]
        (case (:tag (z/node ws-loc))
          :space false
          :newline
          (let [loc (loop [loc ws-loc]
                      (if (some-> (z/left loc)
                                  z/node
                                  n/whitespace?)
                        (recur (z/remove loc))
                        (replace-with-whitespace loc)))]

            (edit/apply-ast! editor (z/root loc))
            true)
          #_(edit/with-formatting editor
              (let [space-begins (edit/move-while pointer -1 #{\space \newline \tab})]
                (if (> (- (:line (:pos pointer))
                          (:line (:pos space-begins))) 1)
                  (cm/replace-range! editor "" {:line (- (:line (:pos pointer)) 2)
                                                :end-line (dec (:line (:pos pointer)))})
                  (let [pad? (and (not= (:pos space-begins) (update (:pos pointer) :column dec))
                                  (format/pad-chars? (edit/get-range space-begins -1)
                                                     (edit/get-range pointer 1)))]
                    (.replaceRange editor (if pad? " " "") (:pos space-begins) (:pos pointer)))))
              true)))
      :else false)))

(defcommand :navigate/hop-left
  "Move cursor left one form"
  {:bindings ["M2-Left"]
   :when :block/code}
  [context]
  (edit/cursor-skip! (:editor context) :left))

(defcommand :navigate/hop-right
  "Move cursor right one form"
  {:bindings ["M2-Right"]
   :when :block/code}
  [context]
  (edit/cursor-skip! (:editor context) :right))

(defcommand :navigate/jump-to-top
  "Move cursor to top of current block"
  {:bindings ["M2-Up"]
   :when :block/code}
  [{:keys [editor]}]
  (Editor/set-cursor editor
                     (Editor/start editor)))

(defcommand :navigate/jump-to-bottom
  "Move cursor to bottom of current block"
  {:bindings ["M2-Down"]
   :when :block/code}
  [{:keys [editor]}]
  (Editor/set-cursor editor
                     (Editor/end editor)))

(defcommand :edit/comment-line
  "Comment the current line"
  {:bindings ["M1-/"]
   :when :block/code}
  [context]
  (edit/comment-line (:editor context)))

(defcommand :edit/ignore-form
  "Ignore the current form using the `#_` reader macro ('uneval')"
  {:bindings ["M1-;"]
   :when :block/code}
  [context]
  (edit/uneval! (:editor context)))

(defcommand :edit/slurp-forward
  "Expand current form to include the form to the right."
  {:bindings ["M1-Shift-Right"]
   :when :block/code}
  [{:keys [editor] :as context}]
  (cm/return-to-temp-marker! editor)
  (edit/with-formatting editor
    (edit/slurp-forward editor))
  true)

(defcommand :edit/barf-forward
  "Pushes last child of current form out to the right."
  {:bindings ["M1-Shift-Left"]
   :when :block/code}
  [{:keys [editor]}]
  (cm/return-to-temp-marker! editor)
  (edit/unslurp-forward editor)
  true)

(defcommand :edit/kill
  "Cuts to end of current form or line, whichever comes first."
  {:bindings ["M3-K"]
   :when :block/code}
  [context]
  (edit/kill! (:editor context)))

(defcommand :edit/unwrap
  "Replaces the current form with its contents."
  {:bindings ["M2-S"]
   :when :block/code}
  [context]
  (edit/unwrap! (:editor context)))

(defcommand :edit/raise
  "Replace the selected form's parent with the selected form."
  {:bindings ["M2-Shift-S"]
   :when :block/code}
  [context]
  (edit/raise! (:editor context)))

(defcommand :eval/form
  "Evaluate the current form"
  {:bindings ["M1-Enter"]
   :when :block/code}
  [{:keys [editor block-view block]}]
  (Block/eval! block))

(defcommand :eval/top-level
  "Evaluate the current top-level form"
  {:bindings ["Shift-Enter"
              "M1-Shift-Enter"]
   :when :block/code}
  [{:keys [editor block-view block]}]
  (when-let [loc (some->> (:loc (:magic/cursor editor))
                          (nav/top-loc))]
    (cm/temp-select-node! editor (z/node loc))
    (js/setTimeout #(cm/return-to-temp-marker! editor) 210)
    (Block/eval! block)))

#_(defcommand :eval/on-click
    "Evaluate the clicked form"
    {:bindings ["Option-Click"]
     :when :block/code}
    [{:keys [block-view]}]
    (eval-scope (.getEditor block-view) :bracket))

(defcommand :info/doc
  "Show documentation for current form."
  {:bindings ["M1-I"]
   :when :block/code}
  [{:keys [block-view editor block]}]
  (when-let [form (some-> editor :magic/cursor :bracket-loc z/node emit/sexp)]
    (if (and (symbol? form) (ns-utils/resolve-var-or-special e/c-state e/c-env form))
      (Block/eval! block :form (list 'doc form))
      (Block/eval! block :form (list 'maria.friendly.kinds/what-is (list 'quote form))))))

(defcommand :info/source
  "Show source code for the current var"
  {:when #(and (:block/code %)
               (some->> (:editor %) :magic/cursor :bracket-loc z/node emit/sexp symbol?))}
  [{:keys [block editor]}]
  (Block/eval! block :form (list 'source
                                 (some-> editor :magic/cursor :bracket-loc z/node emit/sexp))))

(defcommand :info/javascript-source
  "Show compiled javascript for current form"
  {:bindings ["M1-Shift-J"]
   :when :block/code}
  [{:keys [block editor]}]
  (when-let [node (some-> editor :magic/cursor :bracket-loc z/node)]
    (Block/eval-log! block (some-> node emit/string e/compile-str (set/rename-keys {:compiled-js :value})))))

(defcommand :edit/format
  {:bindings ["M2-Tab"]
   :when :block/code}
  [{:keys [editor]}]
  (edit/format! editor)
  true)

(defcommand :edit/space
  {:bindings ["Space" "Tab"]
   :when :block/code}
  [{:keys [editor]}]
  (when-not (.somethingSelected editor)
    (edit/with-formatting editor
      {:preserve-cursor-space? true}
      (-> (edit/pointer editor)
          (edit/insert! " "))
      true)))