(ns maria.commands.prose
  (:require [maria-commands.registry :refer-macros [defcommand]]
            [re-view-prosemirror.commands :as commands :refer [apply-command]]
            [re-view-prosemirror.markdown :as prose]
            [re-view-prosemirror.core :as pm]
            [maria.cells.core :as Cell]
            [re-db.d :as d]
            [maria.util :as util]
            [re-view.core :as v]
            [clojure.string :as string]))

(defn empty-root-paragraph? [state]
  (and (= 1 (pm/cursor-depth state))
       (pm/is-node-type? state :paragraph)
       (commands/empty-node? (pm/cursor-node state))))

(defn split-with-code-cell [cell-list cell state {:keys [content cursor-coords]}]
  (when (empty-root-paragraph? state)
    (let [$head (.. state -selection -$head)
          markdown-before (prose/serialize-selection (.between pm/TextSelection (pm/start-$pos state) $head))
          markdown-after (prose/serialize-selection (.between pm/TextSelection $head (pm/end-$pos state)))
          new-cells (or (some-> content (Cell/from-source))
                        [(Cell/create :code)])]
      (.splice cell-list cell (cond-> []
                                      (not (util/whitespace-string? markdown-before))
                                      (conj (Cell/create :prose markdown-before))

                                      true (into new-cells)
                                      (not (util/whitespace-string? markdown-after))
                                      (conj (Cell/create :prose markdown-after))))
      (Cell/focus! (first new-cells) cursor-coords)
      true)))

(defcommand :prose-cell/enter
  {:bindings ["Enter"]
   :when     :cell/prose}
  [{:keys [editor cell cell-list]}]
  (commands/apply-command editor
                          (commands/chain
                            (fn [state dispatch]
                              (split-with-code-cell cell-list cell state nil))
                            commands/enter)))

(defcommand :prose/newline
  {:bindings       ["Shift-Enter"
                    "M2-Enter"]
   :when           :cell/prose
   :intercept-when true}
  [context]
  (apply-command (:editor context) commands/newline-in-code))

(defcommand :prose/undo
  {:bindings ["M1-z"]
   :when     :cell/prose}
  [context]
  (apply-command (:editor context) commands/undo))


(defcommand :prose/redo
  {:bindings ["M1-y"]
   :when     :cell/prose}
  [context]
  (apply-command (:editor context) commands/redo))


(defcommand :prose/redo
  {:bindings ["Shift-M1-z"]
   :when     :cell/prose}
  [context]
  (apply-command (:editor context) commands/redo))


(defcommand :prose/inline-bold
  {:bindings ["M1-b"]
   :when     :cell/prose}
  [context]
  (apply-command (:editor context) commands/inline-bold))


(defcommand :prose/inline-italic
  {:bindings ["M1-i"]
   :when     :cell/prose}
  [context]
  (apply-command (:editor context) commands/inline-italic))


(defcommand :prose/inline-code
  {:bindings ["M3-`"
              "M1-`"]
   :when     :cell/prose}
  [context]
  (apply-command (:editor context) commands/inline-code))


(defcommand :prose/block-list-bullet
  {:bindings ["Shift-Ctrl-8"]
   :when     :cell/prose}
  [context]
  (apply-command (:editor context) commands/block-list-bullet))


(defcommand :prose/block-list-ordered
  {:bindings ["Shift-Ctrl-9"]
   :when     :cell/prose}
  [context]
  (apply-command (:editor context) commands/block-list-ordered))


(defcommand :prose/block-paragraph
  {:bindings ["Shift-Ctrl-0"]
   :when     :cell/prose}
  [context]
  (apply-command (:editor context) commands/block-paragraph))


(defcommand :prose/outdent
  {:bindings ["Shift-Tab"]
   :when     :cell/prose}
  [context]
  (apply-command (:editor context) (commands/chain commands/heading->paragraph
                                                   commands/outdent)))


(defcommand :prose/indent
  {:bindings ["Tab"]
   :when     :cell/prose}
  [context]
  (apply-command (:editor context) commands/indent))

(defn remove-empty-cell [{:keys [cells cell-list cell]}]
  (fn [_ _]
    (when (and (Cell/empty? cell) (Cell/before cells cell))
      (let [result (.splice cell-list cell [])
            {:keys [before after]} (meta result)]
        (if before (Cell/focus! before :end)
                   (Cell/focus! after :start))
        true))))

(defn clear-previous-empty [{:keys [cells cell-list cell]}]
  (fn [_ _]
    (when (and (Cell/at-start? cell)
               (some-> (Cell/before cells cell) (Cell/empty?)))
      (.splice cell-list cell -1 [cell])
      true)))

(defcommand :prose/backspace
  {:bindings ["Backspace"]
   :when     :cell/prose}
  [{:keys [editor cell cells cell-list] :as context}]
  (apply-command editor
                 (commands/chain
                   commands/open-link
                   commands/open-image
                   (remove-empty-cell context)
                   (clear-previous-empty context)
                   commands/backspace)))

(defcommand :prose/space
  {:bindings       ["Space"]
   :when           :cell/prose
   :intercept-when false}
  [context]
  (apply-command (:editor context) commands/end-link))

(defcommand :prose/join-up
  {:bindings ["M2-Up"]
   :when     :cell/prose}
  [context]
  (apply-command (:editor context) commands/join-up))

(defcommand :prose/join-down
  {:bindings ["M2-Down"]
   :when     :cell/prose}
  [context]
  (apply-command (:editor context) commands/join-down))

(defcommand :prose/select-all
  {:bindings ["M1-A"]
   :when     :cell/prose}
  [context]
  (apply-command (:editor context) commands/select-all))

(defcommand :prose/remove-all-marks
  {:bindings ["Esc"]
   :when     :cell/prose}
  [context]
  (apply-command (:editor context) commands/clear-stored-marks))

(comment
  (defcommand :prose/increase-size
    {:bindings ["M1-="]
     :when     :cell/prose}
    [context]
    (apply-command (:editor context) (partial commands/adjust-font-size dec)))

  (defcommand :prose/decrease-size
    {:bindings ["M1-DASH"]
     :when     :cell/prose}
    [context]
    (apply-command (:editor context) (partial commands/adjust-font-size inc))))

(defn input-rules [cell-view]
  [commands/rule-blockquote-start
   commands/rule-block-list-bullet-start
   commands/rule-block-list-numbered-start
   commands/rule-block-code-start
   commands/rule-toggle-code
   commands/rule-block-heading-start
   commands/rule-paragraph-start
   commands/rule-image-and-links
   (pm/InputRule.
     #"^\($"
     (fn [state [bracket] & _]
       (when (empty-root-paragraph? state)
         (let [other-bracket ({\( \) \[ \] \{ \}} bracket)]
           (js/setTimeout
             #(split-with-code-cell (:cell-list cell-view)
                                    (:cell cell-view)
                                    state {:content       (str bracket other-bracket)
                                           :cursor-coords #js {:line 0
                                                               :ch   1}}) 0))
         (.-tr state))))])