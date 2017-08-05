(ns maria.cells.prose
  (:require [re-view.core :as v :refer [defview]]
            [re-view-prosemirror.markdown :as prose]
            [re-view-prosemirror.core :as pm]

            [re-view-prosemirror.commands :as commands :refer [apply-command]]

            [maria.commands.exec :as exec]
            [maria.commands.registry :refer-macros [defcommand]]
            [re-db.d :as d]
            [maria.cells.core :as Cell]
            [maria.util :as util]))

(defn log-ret [x]
  (println x)
  x)

(defn split-with-code-cell [splice-self! state {:keys [content cursor-coords]}]
  (let [$head (.. state -selection -$head)
        $end (.-$head (.atEnd pm/Selection (.. state -doc)))
        $start (.-$head (.atStart pm/Selection (.. state -doc)))
        markdown-before (prose/serialize-selection (.between pm/TextSelection $start $head))
        markdown-after (prose/serialize-selection (.between pm/TextSelection $head $end))
        new-cell (if content (first (Cell/from-source content)) (Cell/->CodeCell (d/unique-id) []))]
    (splice-self! (cond-> []
                          (not (util/whitespace-string? markdown-before))
                          (conj (Cell/->ProseCell (d/unique-id) markdown-before))

                          true (conj new-cell)
                          (not (util/whitespace-string? markdown-after))
                          (conj (Cell/->ProseCell (d/unique-id) markdown-after))))
    (Cell/focus! new-cell cursor-coords)))


(defview prose-view
  {:key                :id
   :pm-view            #(.pmView (:pm-view @(:view/state %)))
   :focus              (fn [this coords]
                         (let [pm-view (.pmView this)
                               state (.-state pm-view)]
                           (when coords
                             (.dispatch pm-view (.setSelection (.-tr state)
                                                               (case coords :start (.atStart pm/Selection (.-doc state))
                                                                            :end (.atEnd pm/Selection (.-doc state)))))
                             )
                           (.focus pm-view)))
   :view/did-mount     #(Cell/mount (:id %) %)
   :view/should-update #(do false)
   :view/will-unmount  (fn [this] (Cell/unmount (:id this))
                         ;; prosemirror doesn't fire `blur` command when unmounted
                         (when (= (:cell-view exec/text-cell) this)
                           (set! exec/text-cell nil)))}
  [{:keys [view/state on-update splice-self! cell id] :as this}]
  (prose/Editor {:default-value (:value cell)
                 :class         " serif f4 ph3 w-50 cf"
                 :input-rules   [commands/rule-blockquote-start
                                 commands/rule-block-list-bullet-start
                                 commands/rule-block-list-numbered-start
                                 commands/rule-block-code-start
                                 commands/rule-toggle-code
                                 commands/rule-block-heading-start
                                 commands/rule-paragraph-start
                                 (pm/InputRule.
                                   #"^[\(\[\{]$"
                                   (fn [state [bracket] & _]
                                     (let [other-bracket ({\( \) \[ \] \{ \}} bracket)]
                                       (js/setTimeout
                                         #(split-with-code-cell splice-self! state {:content       (str bracket other-bracket)
                                                                                    :cursor-coords #js {:line 0
                                                                                                        :ch   1}}) 0))
                                     (.-tr state)))]
                 :keymap        {"ArrowUp"   (fn [state dispatch]
                                               (let [pos (.-pos (.-$head (.-selection state)))
                                                     beginning-of-doc? (= pos (.-pos (.-$head (.atStart pm/Selection (.. state -doc)))))]
                                                 (when beginning-of-doc?
                                                   (some-> (Cell/before (:cells this) id)
                                                           (Cell/focus! :end)))))
                                 "ArrowDown" (fn [state dispatch]
                                               (let [pos (.-pos (.-$head (.-selection state)))
                                                     end-of-doc? (= pos (.-pos (.-$head (.atEnd pm/Selection (.. state -doc)))))]
                                                 (when end-of-doc?
                                                   (some-> (Cell/after (:cells this) id)
                                                           (Cell/focus! :start)))))
                                 "Enter"     (commands/chain
                                               (fn [state dispatch]
                                                 (let [root? (= 1 (pm/cursor-depth state))
                                                       paragraph? (pm/is-node-type? state :paragraph)
                                                       empty-node? (commands/empty-node? (pm/cursor-node state))]
                                                   (when (and root? paragraph? empty-node?)
                                                     (split-with-code-cell splice-self! state nil)
                                                     true)))
                                               commands/enter)
                                 "Tab"       commands/indent
                                 "Backspace" (commands/chain
                                               (fn [state dispatch]
                                                 (when (Cell/empty? (:cell this))
                                                   (let [result (splice-self! [])
                                                         {:keys [before after]} (meta result)]
                                                     (if before (Cell/focus! before :end)
                                                                (Cell/focus! after :start)))
                                                   true))
                                               commands/backspace)}
                 :on-focus      #(set! exec/text-cell {:prosemirror (.pmView (:pm-view @state))
                                                       :cell-view   this})
                 :on-blur       #(set! exec/text-cell nil)
                 :ref           #(v/swap-silently! state assoc :pm-view %)
                 :on-dispatch   #(on-update (.serialize (:pm-view @state)))}))



(defcommand :prose/delete-cell
  {:when :cell/prose}
  [{:keys [cell/prose]}]
  (let [{:keys [splice-self!]} (:cell-view prose)]
    (let [{:keys [before after]} (meta (splice-self! []))]
      (Cell/focus! (if before (Cell/focus! before :end)
                              (Cell/focus! after :start))))))

(defcommand :prose/shrink-selection
  {:bindings ["M1-2"
              "M1-["]
   :when     :cell/prose}
  [{:keys [cell/prose]}]
  (apply-command (:prosemirror prose) commands/shrink-selection))

(defcommand :prose/expand-selection
  {:bindings ["M1-1"
              "M1-]"]
   :when     :cell/prose}
  [{:keys [cell/prose]}]
  (apply-command (:prosemirror prose) commands/expand-selection))

(defcommand :prose/newline
  {:bindings       ["Shift-Enter"
                    "M2-Enter"]
   :when           :prosemirror
   :intercept-when true}
  [{:keys [cell/prose]}]
  (apply-command (:prosemirror prose) commands/newline-in-code))

(defcommand :prose/undo
  {:bindings ["M1-z"]
   :when     :cell/prose}
  [{:keys [cell/prose]}]
  (apply-command (:prosemirror prose) commands/undo))


(defcommand :prose/redo
  {:bindings ["M1-y"]
   :when     :cell/prose}
  [{:keys [cell/prose]}]
  (apply-command (:prosemirror prose) commands/redo))


(defcommand :prose/redo
  {:bindings ["Shift-M1-z"]
   :when     :cell/prose}
  [{:keys [cell/prose]}]
  (apply-command (:prosemirror prose) commands/redo))


(defcommand :prose/inline-bold
  {:bindings ["M1-b"]
   :when     :cell/prose}
  [{:keys [cell/prose]}]
  (apply-command (:prosemirror prose) commands/inline-bold))


(defcommand :prose/inline-italic
  {:bindings ["M1-I"]
   :when     :cell/prose}
  [{:keys [cell/prose]}]
  (apply-command (:prosemirror prose) commands/inline-italic))


(defcommand :prose/inline-code
  {:bindings ["M3-`"]
   :when     :cell/prose}
  [{:keys [cell/prose]}]
  (apply-command (:prosemirror prose) commands/inline-code))


(defcommand :prose/block-list-bullet
  {:bindings ["Shift-Ctrl-8"]
   :when     :cell/prose}
  [{:keys [cell/prose]}]
  (apply-command (:prosemirror prose) commands/block-list-bullet))


(defcommand :prose/block-list-ordered
  {:bindings ["Shift-Ctrl-9"]
   :when     :cell/prose}
  [{:keys [cell/prose]}]
  (apply-command (:prosemirror prose) commands/block-list-ordered))


(defcommand :prose/block-paragraph
  {:bindings ["Shift-Ctrl-0"]
   :when     :cell/prose}
  [{:keys [cell/prose]}]
  (apply-command (:prosemirror prose) commands/block-paragraph))


(defcommand :prose/outdent
  {:bindings ["Shift-Tab"]
   :when     :cell/prose}
  [{:keys [cell/prose]}]
  (apply-command (:prosemirror prose) (commands/chain commands/heading->paragraph
                                                      commands/outdent)))


(defcommand :prose/indent
  {:bindings [
              ;; handled by ProseMirror keymap
              ;; TODO
              ;; let maria.commands handle single-press modifiers like Enter, Backspace, Tab, etc.
              #_"Tab"
              ]
   :when     :cell/prose}
  [{:keys [cell/prose]}]
  (apply-command (:prosemirror prose) commands/indent))


(defcommand :prose/join-up
  {:bindings ["M2-Up"]
   :when     :cell/prose}
  [{:keys [cell/prose]}]
  (apply-command (:prosemirror prose) commands/join-up))

(defcommand :prose/join-down
  {:bindings ["M2-Down"]
   :when     :cell/prose}
  [{:keys [cell/prose]}]
  (apply-command (:prosemirror prose) commands/join-down))

(defcommand :prose/select-all
  {:bindings ["M1-A"]
   :when     :cell/prose}
  [{:keys [cell/prose]}]
  (apply-command (:prosemirror prose) commands/select-all))

(comment
  (defcommand :prose/increase-size
    {:bindings ["M1-="]
     :when     :cell/prose}
    [{:keys [cell/prose]}]
    (apply-command (:prosemirror prose) (partial commands/adjust-font-size dec)))

  (defcommand :prose/decrease-size
    {:bindings ["M1-DASH"]
     :when     :cell/prose}
    [{:keys [cell/prose]}]
    (apply-command (:prosemirror prose) (partial commands/adjust-font-size inc))))







;; TODO

;; Commands
;; =====================
;; select word
;; split prose into prose/code/prose
;; split code into code/prose/code
;;
;; Structure
;; =====================
;;
;; give each form a unique id;
;; allows for inserting forms into AST
;; without re-parsing the whole damn thing.
;;
;;
;;
;; Edits inside a form don't propagate upwards until one of:
;; - `eval`
;; - `blur`
;; (warning: sometimes you have 2 top-level forms just because
;;  you are in the middle of editing & want to slurp, etc.)
;;
;; Shortcuts allow quick jumping between code and comment block
;;



;(def md (js/markdownit "default" #js {"html" false}))
;; attempt at rendering pure markdown and only instantiating ProseMirror when the user clicks on the field.
;; does not work due to probable bug in ProseMirror where .focus() sets the cursor position to zero.
#_(defn get-pm [editor-view]
    (:pm-view @(:view/state editor-view)))
#_(defview markdown
    {:view/initial-state {:editing? false
                          :pm-view  nil}}
    [{:keys [view/state view/prev-state]} s]
    (if (:editing? @state)
      (prose/Editor {:value   s
                     :ref     #(let [pm-view (some-> % (get-pm))]
                                 (v/swap-silently! state assoc :pm-view pm-view)

                                 (when (and pm-view (not (:pm-view prev-state)))
                                   (when-let [selection (some->> (:clicked-coords @state)
                                                                 (clj->js)
                                                                 (.posAtCoords pm-view)
                                                                 (.-pos)
                                                                 (.resolve (.. pm-view -state -doc))
                                                                 (.near js/pm.Selection))]
                                     (.dispatch pm-view (.. pm-view -state -tr (setSelection selection))))

                                   (some-> pm-view (.focus))))
                     :on-blur #(do (.log js/console "prose/Editor on-blur")
                                   (swap! state assoc
                                          :editing? false
                                          :clicked-coords nil))})
      [:.cf {:ref                     #(v/swap-silently! state assoc :md-view %)
             :on-click                #(swap! state assoc
                                              :editing? true
                                              :clicked-coords {:left (.-clientX %)
                                                               :top  (.-clientY %)})
             :dangerouslySetInnerHTML {:__html (.render md s)}}]))