(ns maria.cells.code
  (:require [re-view.core :as v :refer [defview]]
            [maria-commands.exec :as exec]
            [maria.views.repl-values :as repl-values]
            [maria.views.codemirror :as codemirror]
            [re-db.d :as d]
            [maria.cells.core :as Cell]
            [maria.util :as util]
            [magic-tree.core :as tree]
            [magic-tree-codemirror.edit :as edit]
            [maria.eval :as e]))

(defview code-view
  {:key                :id
   :view/should-update #(do false)
   :view/did-mount     #(Cell/mount (:cell %) %)
   :view/will-unmount  #(Cell/unmount (:cell %))
   :get-editor         #(.getEditor (:editor-view @(:view/state %)))
   :scroll-into-view   #(util/scroll-to-cursor (.getEditor %))
   :focus              (fn [this coords]
                         (.focus (:editor-view @(:view/state this)) coords))}
  [{:keys [view/state cell-list cell] :as this}]
  [:.flex.pv3.cursor-text
   {:on-click #(when (= (.-target %) (.-currentTarget %))
                 (.focus this))
    :on-focus #(.scrollIntoView this)}
   [:.w-50.flex-none
    (codemirror/editor {:class               "pa3 bg-white"
                        :ref                 #(v/swap-silently! state assoc :editor-view %)
                        :default-value       (Cell/emit (:cell this))
                        :on-ast              (fn [node]
                                               (when (not= (tree/string node)
                                                           (tree/string (:node (:cell this))))
                                                 (.splice cell-list cell [(assoc cell :node node)])))
                        :capture-event/focus #(exec/set-context! {:cell/code true
                                                                  :cell-view this})
                        :capture-event/blur  #(exec/set-context! {:cell/code nil
                                                                  :cell-view nil})})]

   [:.w-50.flex-none.code.overflow-hidden (some-> (peek (d/get (:id cell) :eval-log))
                                                  (assoc :cell-id (:id cell))
                                                  (repl-values/display-result))]])

(extend-type Cell/CodeCell

  Cell/ICursor
  (cursor-edge [this]
    (let [editor (Cell/editor this)
          last-line (.lastLine editor)
          {cursor-line :line
           cursor-ch   :ch} (util/js-lookup (.getCursor editor))]
      (cond (Cell/empty? this) :empty
            (and (= cursor-line last-line)
                 (= cursor-ch (count (.getLine editor last-line))))
            :end
            (and (= cursor-line 0)
                 (= cursor-ch 0))
            :start
            :else nil)))

  (cursor-coords [this]
    (.cursorCoords (Cell/editor this)))

  (at-end? [this]
    (let [cm (Cell/editor this)
          cursor (.getCursor cm)]
      (= [(.lastLine cm)
          (count (.getLine cm (.lastLine cm)))]
         [(.-line cursor) (.-ch cursor)])))

  (at-start? [this]
    (let [cursor (.getCursor (Cell/editor this))]
      (= [0 0] [(.-line cursor) (.-ch cursor)])))

  (selection-expand [this]
    (edit/expand-selection (Cell/editor this)))

  (selection-contract [this]
    (edit/shrink-selection (Cell/editor this)))

  Cell/ICell

  (kind [this] :code)

  (append? [this other-cell] false)

  (empty? [this]
    (let [{:keys [tag] :as node} (:node this)]
      (= 0 (count (filter (complement tree/whitespace?) (if (= :base tag)
                                                          (:value node)
                                                          [node]))))))



  (emit [this] (tree/string (:node this)))



  (render [this props]
    (code-view (assoc props
                 :cell this
                 :id (:id this))))

  Cell/IEval
  (eval [this]
    (e/logged-eval-str (:id this) (Cell/emit this))))