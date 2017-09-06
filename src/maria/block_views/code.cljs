(ns maria.block-views.code
  (:require [re-view.core :as v :refer [defview]]
            [maria.block-views.editor :as Editor]
            [maria.views.codemirror :as codemirror]
            [maria.blocks.blocks :as Block]
            [maria-commands.exec :as exec]
            [maria.views.values :as repl-values]
            [magic-tree-editor.edit :as edit]
            [magic-tree-editor.codemirror :as cm]))

(defview CodeView
  {:key                :id
   :view/should-update #(not= (:block %) (:block (:view/prev-props %)))
   :view/did-mount     Editor/mount
   :view/will-unmount  Editor/unmount
   :get-editor         #(.getEditor (:editor-view @(:view/state %)))}
  [{:keys [view/state block-list block before-change on-selection-activity] :as this}]
  [:.flex.pv3.cursor-text
   {:on-click #(when (= (.-target %) (.-currentTarget %))
                 (Editor/focus! (.getEditor this)))}
   [:.w-50.flex-none
    (codemirror/editor {:class                 "pa3 bg-white"
                        :ref                   #(v/swap-silently! state assoc :editor-view %)
                        :value                 (Block/emit (:block this))
                        :on-ast                (fn [node]
                                                 (.splice block-list block [(assoc block :node node)]))
                        :before-change         before-change
                        :on-selection-activity on-selection-activity
                        :capture-event/focus   #(exec/set-context! {:block/code true
                                                                    :block-view this})
                        :capture-event/blur    #(exec/set-context! {:block/code nil
                                                                    :block-view nil})})]

   [:.w-50.flex-none.code.overflow-y-hidden.overflow-x-auto
    (some-> (first (Block/eval-log block))
            (assoc :block-id (:id block))
            (repl-values/display-result))]])


(specify! (.-prototype js/CodeMirror)

  Editor/IKind
  (kind [this] :code)

  Editor/IHistory

  (get-selections [cm]
    (if-let [root-cursor (cm/cursor-root cm)]
      #js [#js {:anchor root-cursor
                :head   root-cursor}]
      (.listSelections cm)))

  (put-selections! [cm selections]
    (v/flush!)
    (.setSelections cm selections))

  Editor/ICursor

  (-focus! [this coords]
    (let [coords (if (keyword? coords)
                   (case coords :end (cm/Pos (.lineCount this) (count (.getLine this (.lineCount this))))
                                :start (cm/Pos 0 0))
                   coords)]
      (doto this
        (.focus)
        (cond-> coords (.setCursor coords)))
      (Editor/scroll-into-view (Editor/cursor-coords this))))

  (get-cursor [this]
    (when-not (.somethingSelected this)
      (cm/get-cursor this)))

  (cursor-coords [this]
    (let [coords (.cursorCoords this)]
      ;; TODO
      ;; these coords don't seem to be correct when using them
      ;; to scroll the cursor into view.
      #_(.log js/console "cm" #js {:left   (- (.-left coords) (.-scrollX js/window))
                                 :right  (- (.-right coords) (.-scrollX js/window))
                                 :top    (- (.-top coords) (.-scrollY js/window))
                                 :bottom (- (.-bottom coords) (.-scrollY js/window))})
      #js {:left   (- (.-left coords) (.-scrollX js/window))
           :right  (- (.-right coords) (.-scrollX js/window))
           :top    (- (.-top coords) (.-scrollY js/window))
           :bottom (- (.-bottom coords) (.-scrollY js/window))}))

  (start [this] (cm/Pos 0 0))
  (end [this] (cm/Pos (.lastLine this) (count (.getLine this (.lastLine this)))))

  (selection-expand [cm]
    (edit/expand-selection cm))

  (selection-contract [cm]
    (edit/shrink-selection cm)))