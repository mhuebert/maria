(ns maria.cells.code
  (:require [re-view.core :as v :refer [defview]]
            [maria.commands.exec :as exec]
            [maria.views.repl-values :as repl-values]
            [maria.cells.codemirror :as codemirror]
            [re-view-prosemirror.core :as pm]
            [re-view-prosemirror.commands :as pm-commands]
            [re-db.d :as d]
            [maria.cells.core :as Cell]
            [maria.eval :as eval]))


(defn edge-left [this id cm e]
  (let [cursor (.getCursor cm)]
    (if (= [0 0] [(.-line cursor) (.-ch cursor)])
      (some-> (Cell/before (:cells this) id)
              (Cell/focus! :end))
      js/CodeMirror.Pass)))

(defn edge-right [this id cm e]
  (let [cursor (.getCursor cm)]
    (if (= [(.lastLine cm)
            (count (.getLine cm (.lastLine cm)))]
           [(.-line cursor) (.-ch cursor)])
      (some-> (Cell/after (:cells this) id)
              (Cell/focus! :start))
      js/CodeMirror.Pass)))

(defview code-view
  {:key                :id
   :view/did-mount     #(Cell/mount (:id %) %)
   :view/will-unmount  #(Cell/unmount (:id %))
   :view/should-update #(do false)
   :focus              (fn [this coords]
                         (.focus (:editor-view @(:view/state this)) coords))
   :view/initial-state {:eval-log []}}
  [{:keys [view/state on-ast splice-self! id] :as this}]
  [:.flex.pv3.cursor-text
   {:on-click #(when (= (.-target %) (.-currentTarget %))
                 (.focus this))}
   [:.w-50.flex-none
    (codemirror/editor {:class               "pa3 bg-white"
                        :keymap              {"Backspace" (fn [cm e]
                                                            (let [before (Cell/before (:cells this) id)]
                                                              (cond
                                                                (Cell/empty? before)
                                                                (splice-self! -1 [(:cell this)])

                                                                (Cell/empty? (:cell this))
                                                                (let [new-cell (when-not before (Cell/->ProseCell (d/unique-id) ""))]
                                                                  (splice-self! (if new-cell [new-cell] []))
                                                                  (Cell/focus! (or before new-cell) :end))
                                                                :else js/CodeMirror.Pass)))
                                              "Up"        (partial edge-left this id)
                                              "Left"      (partial edge-left this id)
                                              "Down"      (partial edge-right this id)
                                              "Right"     (partial edge-right this id)
                                              "Enter"     (fn [cm e]
                                                            (let [last-line (.lastLine cm)
                                                                  cursor (.getCursor cm)]
                                                              (if-let [edge-position (cond (and (= (.-line cursor) last-line)
                                                                                                (= (.-ch cursor) (count (.getLine cm last-line))))
                                                                                           :end
                                                                                           (and (= (.-line cursor) 0)
                                                                                                (= (.-ch cursor) 0))
                                                                                           :start
                                                                                           :else nil)]
                                                                (let [{:keys [cell cells]} this
                                                                      adjacent-cell ((case edge-position
                                                                                       :end Cell/after
                                                                                       :start Cell/before) cells id)
                                                                      adjacent-prose (when (satisfies? Cell/IText adjacent-cell)
                                                                                       adjacent-cell)
                                                                      new-cell (when-not adjacent-prose
                                                                                 (Cell/->ProseCell (d/unique-id) ""))]
                                                                  (case edge-position
                                                                    :end (do
                                                                           (splice-self! (if adjacent-prose 1 0)
                                                                                         (cond-> []
                                                                                                 (not (Cell/empty? cell)) (conj cell)
                                                                                                 true (conj (or new-cell
                                                                                                                adjacent-prose))))

                                                                           (when (satisfies? Cell/IText adjacent-prose)
                                                                             (Cell/prepend-paragraph adjacent-prose))

                                                                           (Cell/focus! (or new-cell adjacent-prose) :start))
                                                                    :start (splice-self! (cond-> []
                                                                                                 new-cell (conj new-cell)
                                                                                                 true (conj cell)))))
                                                                js/CodeMirror.Pass)))}
                        :ref                 #(v/swap-silently! state assoc :editor-view %)
                        :default-value       (Cell/emit (:cell this))
                        :on-ast              on-ast
                        :on-eval-result      #(swap! state update :eval-log conj %)
                        :capture-event/focus #(exec/set-context! :cell/code {:editor    (.getEditor %2)
                                                                             :cell-view this})
                        :capture-event/blur  #(exec/set-context! :cell/code nil)})]

   [:.w-50.flex-none.code.overflow-hidden (some-> (peek (d/get id :eval-log))
                                                  (repl-values/display-result))]])