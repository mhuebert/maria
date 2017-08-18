(ns maria.commands.prose
  (:require [maria-commands.registry :refer-macros [defcommand]]
            [re-view-prosemirror.commands :as commands :refer [apply-command]]
            [re-view-prosemirror.markdown :as prose]
            [re-view-prosemirror.core :as pm]
            [maria.blocks.core :as Block]
            [re-db.d :as d]
            [maria.util :as util]
            [re-view.core :as v]
            [clojure.string :as string]))

(defn empty-root-paragraph? [state]
  (and (= 1 (pm/cursor-depth state))
       (pm/is-node-type? state :paragraph)
       (commands/empty-node? (pm/cursor-node state))))

(defn split-with-code-block [block-list block state {:keys [content cursor-coords]}]
  (when (empty-root-paragraph? state)
    (let [$head (.. state -selection -$head)
          markdown-before (prose/serialize-selection (.between pm/TextSelection (pm/start-$pos state) $head))
          markdown-after (prose/serialize-selection (.between pm/TextSelection $head (pm/end-$pos state)))
          new-blocks (or (some-> content (Block/from-source))
                         [(Block/create :code)])]
      (.splice block-list block (cond-> []
                                        (not (util/whitespace-string? markdown-before))
                                        (conj (Block/create :prose markdown-before))

                                        true (into new-blocks)
                                        (not (util/whitespace-string? markdown-after))
                                        (conj (Block/create :prose markdown-after))))
      (Block/focus! (first new-blocks) cursor-coords)
      true)))

(defcommand :prose-block/enter
  {:bindings ["Enter"]
   :when     :block/prose}
  [{:keys [editor block block-list]}]
  (commands/apply-command editor
                          (commands/chain
                            (fn [state dispatch]
                              (split-with-code-block block-list block state nil))
                            commands/enter)))

(defcommand :prose/newline
  {:bindings       ["Shift-Enter"
                    "M2-Enter"]
   :when           :block/prose
   :intercept-when true}
  [context]
  (apply-command (:editor context) commands/newline-in-code))

(defcommand :prose/undo
  {:bindings ["M1-z"]
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/undo))


(defcommand :prose/redo
  {:bindings ["M1-y"]
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/redo))


(defcommand :prose/redo
  {:bindings ["Shift-M1-z"]
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/redo))


(defcommand :prose/inline-bold
  {:bindings ["M1-b"]
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/inline-bold))


(defcommand :prose/inline-italic
  {:bindings ["M1-i"]
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/inline-italic))


(defcommand :prose/inline-code
  {:bindings ["M3-`"
              "M1-`"]
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/inline-code))


(defcommand :prose/block-list-bullet
  {:bindings ["Shift-Ctrl-8"]
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/block-list-bullet))


(defcommand :prose/block-list-ordered
  {:bindings ["Shift-Ctrl-9"]
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/block-list-ordered))


(defcommand :prose/block-paragraph
  {:bindings ["Shift-Ctrl-0"]
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/block-paragraph))


(defcommand :prose/outdent
  {:bindings ["Shift-Tab"]
   :when     :block/prose}
  [context]
  (apply-command (:editor context) (commands/chain commands/heading->paragraph
                                                   commands/outdent)))


(defcommand :prose/indent
  {:bindings ["Tab"]
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/indent))

(defn remove-empty-block [{:keys [blocks block-list block]}]
  (fn [_ _]
    (when (and (Block/empty? block) (Block/before blocks block))
      (let [result (.splice block-list block [])
            {:keys [before after]} (meta result)]
        (if before (Block/focus! before :end)
                   (Block/focus! after :start))
        true))))

(defn clear-previous-empty [{:keys [blocks block-list block]}]
  (fn [_ _]
    (when (and (Block/at-start? block)
               (some-> (Block/before blocks block) (Block/empty?)))
      (.splice block-list block -1 [block])
      true)))

(defcommand :prose/backspace
  {:bindings ["Backspace"]
   :when     :block/prose}
  [{:keys [editor block blocks block-list] :as context}]
  (apply-command editor
                 (commands/chain
                   commands/open-link
                   commands/open-image
                   (remove-empty-block context)
                   (clear-previous-empty context)
                   commands/backspace)))

(defcommand :prose/space
  {:bindings       ["Space"]
   :when           :block/prose
   :intercept-when false}
  [context]
  (apply-command (:editor context) commands/end-link))

(defcommand :prose/join-up
  {:bindings ["M2-Up"]
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/join-up))

(defcommand :prose/join-down
  {:bindings ["M2-Down"]
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/join-down))

(defcommand :prose/select-all
  {:bindings ["M1-A"]
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/select-all))

(defcommand :prose/remove-all-marks
  {:bindings ["Esc"]
   :when     :block/prose}
  [context]
  (apply-command (:editor context) commands/clear-stored-marks))

(comment
  (defcommand :prose/increase-size
    {:bindings ["M1-="]
     :when     :block/prose}
    [context]
    (apply-command (:editor context) (partial commands/adjust-font-size dec)))

  (defcommand :prose/decrease-size
    {:bindings ["M1-DASH"]
     :when     :block/prose}
    [context]
    (apply-command (:editor context) (partial commands/adjust-font-size inc))))

(defn input-rules [block-view]
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
             #(split-with-code-block (:block-list block-view)
                                     (:block block-view)
                                     state {:content       (str bracket other-bracket)
                                            :cursor-coords #js {:line 0
                                                                :ch   1}}) 0))
         (.-tr state))))])