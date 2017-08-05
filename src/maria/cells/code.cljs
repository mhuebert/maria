(ns maria.cells.code
  (:require [re-view.core :as v :refer [defview]]
            [maria.commands.exec :as exec]
            [maria.views.repl-values :as repl-values]
            [maria.cells.codemirror :as codemirror]
            [re-db.d :as d]
            [maria.cells.core :as Cell]))

(defview code-view
  {:key                :id
   :view/did-mount     #(Cell/mount (:cell %) %)
   :view/will-unmount  #(Cell/unmount (:cell %))
   :view/should-update #(do false)
   :focus              (fn [this coords]
                         (.focus (:editor-view @(:view/state this)) coords))
   :view/initial-state {:eval-log []}}
  [{:keys [view/state on-ast splice-self! cells id] :as this}]
  [:.flex.pv3.cursor-text
   {:on-click #(when (= (.-target %) (.-currentTarget %))
                 (.focus this))}
   [:.w-50.flex-none
    (codemirror/editor {:class               "pa3 bg-white"
                        :keymap              {"Backspace" (fn [cm e]
                                                            (if (= "" (.getValue cm))
                                                              (let [{:keys [before after]} (meta (splice-self! []))]
                                                                (Cell/focus! (or before after) :end))
                                                              js/CodeMirror.Pass))
                                              ";"         (fn [cm e]
                                                            (let [line (.-line (.getCursor cm))]
                                                              (prn :line line)
                                                              (if (= "" (.getLine cm line))
                                                                (let [before-cell (when-let [before-line (when (> line 0) (dec line))]
                                                                                    (Cell/from-source (.getRange cm
                                                                                                                 #js {:line 0 :ch 0}
                                                                                                                 #js {:line before-line
                                                                                                                      :ch   (count (.getLine cm before-line))})))
                                                                      new-cell (Cell/->ProseCell (d/unique-id) "")
                                                                      after-cell (when-let [after-line (when (> (.lineCount cm) line)
                                                                                                         (inc line))]
                                                                                   (Cell/from-source (.getRange cm
                                                                                                                #js {:line after-line
                                                                                                                     :ch   0}
                                                                                                                #js {:line (.lineCount cm)
                                                                                                                     :ch   (count (.getLine cm (.lineCount cm)))})))]
                                                                  (splice-self! (cond-> []
                                                                                        before-cell (into before-cell)
                                                                                        true (conj new-cell)
                                                                                        after-cell (into after-cell)))
                                                                  (Cell/focus! new-cell))
                                                                js/CodeMirror.Pass)))
                                              "Up"        (fn [cm e]
                                                            (let [cursor (.getCursor cm)]
                                                              (if (= [0 0] [(.-line cursor) (.-ch cursor)])
                                                                (some-> (Cell/before (:cells this) id)
                                                                        (Cell/focus! :end))
                                                                js/CodeMirror.Pass)))
                                              "Down"      (fn [cm e]
                                                            (let [cursor (.getCursor cm)]
                                                              (if (= [(.lastLine cm)
                                                                      (count (.getLine cm (.lastLine cm)))]
                                                                     [(.-line cursor) (.-ch cursor)])
                                                                (some-> (Cell/after (:cells this) id)
                                                                        (Cell/focus! :start))
                                                                js/CodeMirror.Pass)))
                                              "Enter"     (fn [cm e]
                                                            (let [last-line (.lastLine cm)
                                                                  cursor (.getCursor cm)]
                                                              (if (and (= (.-line cursor) last-line) (= "" (.getLine cm last-line)))
                                                                (let [doc-empty? (re-find #"^\s*$" (.getValue cm))
                                                                      next-cell (Cell/after cells id)
                                                                      new-cell (when-not (instance? Cell/ProseCell next-cell)
                                                                                 (Cell/->ProseCell (d/unique-id) ""))]
                                                                  (when-not doc-empty?
                                                                    (.replaceRange cm ""
                                                                                   #js {:line (dec last-line)
                                                                                        :ch   (count (.getLine cm (dec last-line)))}
                                                                                   #js {:line last-line
                                                                                        :ch   0}))
                                                                  (splice-self! (vec (keep identity [(when-not doc-empty? (:cell this))
                                                                                                     new-cell])))
                                                                  (Cell/focus! (or new-cell next-cell) :start))
                                                                js/CodeMirror.Pass)))}
                        :ref                 #(v/swap-silently! state assoc :editor-view %)
                        :default-value       (Cell/emit (:cell this))
                        :on-ast              on-ast
                        :on-eval-result      #(swap! state update :eval-log conj %)
                        :capture-event/focus #(set! exec/current-editor (.getEditor %2))
                        :capture-event/blur  #(set! exec/current-editor nil)})]

   [:.w-50.flex-none.code.overflow-hidden (some-> (last (:eval-log @state)) (repl-values/display-result))]])