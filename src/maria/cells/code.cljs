(ns maria.cells.code
  (:require [re-view.core :as v :refer [defview]]
            [maria-commands.exec :as exec]
            [maria.views.repl-values :as repl-values]
            [maria.cells.codemirror :as codemirror]
            [re-db.d :as d]
            [maria.cells.core :as Cell]
            [maria.util :as util]))

(defview code-view
  {:key                :id
   :view/should-update #(do false)
   :get-editor         #(.getEditor (:editor-view @(:view/state %)))
   :scroll-into-view   #(util/scroll-to-cursor (.getEditor %))
   :focus              (fn [this coords]
                         (.focus (:editor-view @(:view/state this)) coords))}
  [{:keys [view/state on-ast id] :as this}]
  [:.flex.pv3.cursor-text
   {:on-click #(when (= (.-target %) (.-currentTarget %))
                 (.focus this))
    :on-focus #(.scrollIntoView this)}
   [:.w-50.flex-none
    (codemirror/editor {:class               "pa3 bg-white"
                        :ref                 #(v/swap-silently! state assoc :editor-view %)
                        :default-value       (Cell/emit (:cell this))
                        :on-ast              on-ast
                        :on-eval-result      #(swap! state update :eval-log conj %)
                        :capture-event/focus #(exec/set-context! {:cell/code true
                                                                  :cell-view this})
                        :capture-event/blur  #(exec/set-context! {:cell/code nil
                                                                  :cell-view nil})})]

   [:.w-50.flex-none.code.overflow-hidden (some-> (peek (d/get id :eval-log))
                                                  (repl-values/display-result))]])