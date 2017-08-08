(ns maria.commands.prose
  (:require [maria-commands.registry :refer-macros [defcommand]]
            [re-view-prosemirror.commands :as commands :refer [apply-command]]
            [re-view-prosemirror.markdown :as prose]
            [re-view-prosemirror.core :as pm]
            [maria.cells.core :as Cell]
            [re-db.d :as d]
            [maria.util :as util]))

(defn empty-root-paragraph? [state]
  (and (= 1 (pm/cursor-depth state))
       (pm/is-node-type? state :paragraph)
       (commands/empty-node? (pm/cursor-node state))))

(defn split-with-code-cell [splice-self! state {:keys [content cursor-coords]}]
  (when (empty-root-paragraph? state)
    (let [$head (.. state -selection -$head)
          markdown-before (prose/serialize-selection (.between pm/TextSelection (pm/start-$pos state) $head))
          markdown-after (prose/serialize-selection (.between pm/TextSelection $head (pm/end-$pos state)))
          new-cell (if content (first (Cell/from-source content)) (Cell/->CodeCell (d/unique-id) []))]
      (splice-self! (cond-> []
                            (not (util/whitespace-string? markdown-before))
                            (conj (Cell/->ProseCell (d/unique-id) markdown-before))

                            true (conj new-cell)
                            (not (util/whitespace-string? markdown-after))
                            (conj (Cell/->ProseCell (d/unique-id) markdown-after))))
      (Cell/focus! new-cell cursor-coords)
      true)))

(defcommand :prose-cell/enter
  {:bindings ["Enter"]
   :when :cell/prose}
  [{{:keys [splice-self!] :as cell-view} :cell-view}]
  (commands/apply-command (.getEditor cell-view)
                          (commands/chain
                            (fn [state dispatch]
                              (split-with-code-cell splice-self! state nil))
                            commands/enter)))

(defcommand :prose/shrink-selection
  {:bindings ["M1-2"
              "M1-["]
   :when     :cell/prose}
  [{:keys [cell-view]}]
  (apply-command (.getEditor cell-view) commands/shrink-selection))

(defcommand :prose/expand-selection
  {:bindings ["M1-1"
              "M1-]"]
   :when     :cell/prose}
  [{:keys [cell-view]}]
  (apply-command (.getEditor cell-view) commands/expand-selection))

(defcommand :prose/newline
  {:bindings       ["Shift-Enter"
                    "M2-Enter"]
   :when           :cell/prose
   :intercept-when true}
  [{:keys [cell-view]}]
  (apply-command (.getEditor cell-view) commands/newline-in-code))

(defcommand :prose/undo
  {:bindings ["M1-z"]
   :when     :cell/prose}
  [{:keys [cell-view]}]
  (apply-command (.getEditor cell-view) commands/undo))


(defcommand :prose/redo
  {:bindings ["M1-y"]
   :when     :cell/prose}
  [{:keys [cell-view]}]
  (apply-command (.getEditor cell-view) commands/redo))


(defcommand :prose/redo
  {:bindings ["Shift-M1-z"]
   :when     :cell/prose}
  [{:keys [cell-view]}]
  (apply-command (.getEditor cell-view) commands/redo))


(defcommand :prose/inline-bold
  {:bindings ["M1-b"]
   :when     :cell/prose}
  [{:keys [cell-view]}]
  (apply-command (.getEditor cell-view) commands/inline-bold))


(defcommand :prose/inline-italic
  {:bindings ["M1-I"]
   :when     :cell/prose}
  [{:keys [cell-view]}]
  (apply-command (.getEditor cell-view) commands/inline-italic))


(defcommand :prose/inline-code
  {:bindings ["M3-`"]
   :when     :cell/prose}
  [{:keys [cell-view]}]
  (apply-command (.getEditor cell-view) commands/inline-code))


(defcommand :prose/block-list-bullet
  {:bindings ["Shift-Ctrl-8"]
   :when     :cell/prose}
  [{:keys [cell-view]}]
  (apply-command (.getEditor cell-view) commands/block-list-bullet))


(defcommand :prose/block-list-ordered
  {:bindings ["Shift-Ctrl-9"]
   :when     :cell/prose}
  [{:keys [cell-view]}]
  (apply-command (.getEditor cell-view) commands/block-list-ordered))


(defcommand :prose/block-paragraph
  {:bindings ["Shift-Ctrl-0"]
   :when     :cell/prose}
  [{:keys [cell-view]}]
  (apply-command (.getEditor cell-view) commands/block-paragraph))


(defcommand :prose/outdent
  {:bindings ["Shift-Tab"]
   :when     :cell/prose}
  [{:keys [cell-view]}]
  (apply-command (.getEditor cell-view) (commands/chain commands/heading->paragraph
                                                        commands/outdent)))


(defcommand :prose/indent
  {:bindings ["Tab"]
   :when     :cell/prose}
  [{:keys [cell-view]}]
  (apply-command (.getEditor cell-view) commands/indent))

(defcommand :prose/backspace
  {:bindings ["Backspace"]
   :when     :cell/prose}
  [{{:keys [id cell cells splice-self!] :as cell-view} :cell-view}]
  (apply-command (.getEditor cell-view)
                 (commands/chain
                   (fn [state dispatch]
                     (cond (Cell/empty? cell)
                           (let [result (splice-self! [])
                                 {:keys [before after]} (meta result)]
                             (if before (Cell/focus! before :end)
                                        (Cell/focus! after :start))
                             true)
                           (and (Cell/at-start? cell)
                                (some-> (Cell/before cells id) (Cell/empty?)))
                           (let [self (Cell/trim-paragraph-left cell)]
                             (splice-self! -1 (if (Cell/empty? self)
                                                []
                                                [self]))
                             true)
                           :else false))
                   commands/backspace)))

(defcommand :prose/join-up
  {:bindings ["M2-Up"]
   :when     :cell/prose}
  [{:keys [cell-view]}]
  (apply-command (.getEditor cell-view) commands/join-up))

(defcommand :prose/join-down
  {:bindings ["M2-Down"]
   :when     :cell/prose}
  [{:keys [cell-view]}]
  (apply-command (.getEditor cell-view) commands/join-down))

(defcommand :prose/select-all
  {:bindings ["M1-A"]
   :when     :cell/prose}
  [{:keys [cell-view]}]
  (apply-command (.getEditor cell-view) commands/select-all))

(comment
  (defcommand :prose/increase-size
    {:bindings ["M1-="]
     :when     :cell/prose}
    [{:keys [cell-view]}]
    (apply-command (.getEditor cell-view) (partial commands/adjust-font-size dec)))

  (defcommand :prose/decrease-size
    {:bindings ["M1-DASH"]
     :when     :cell/prose}
    [{:keys [cell-view]}]
    (apply-command (.getEditor cell-view) (partial commands/adjust-font-size inc))))

(defn input-rules [cell-view]
  [commands/rule-blockquote-start
   commands/rule-block-list-bullet-start
   commands/rule-block-list-numbered-start
   commands/rule-block-code-start
   commands/rule-toggle-code
   commands/rule-block-heading-start
   commands/rule-paragraph-start
   (pm/InputRule.
     #"^[\(\[\{]$"
     (fn [state [bracket] & _]
       (when (empty-root-paragraph? state)
         (let [other-bracket ({\( \) \[ \] \{ \}} bracket)]
           (js/setTimeout
             #(split-with-code-cell (:splice-self! cell-view) state {:content       (str bracket other-bracket)
                                                                     :cursor-coords #js {:line 0
                                                                                         :ch   1}}) 0))
         (.-tr state))))])