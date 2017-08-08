(ns maria.cells.list-view
  (:require [re-view.core :as v :refer [defview]]
            [maria.cells.code-view :as code]
            [clojure.core.match :refer-macros [match]]
            [cljsjs.markdown-it]
            [maria.cells.prose-view :as prose]
            [re-db.d :as d]
            [maria.cells.core :as Cell]
            [maria-commands.exec :as exec]
            [maria.commands.cells]
            [maria.eval :as e]
            [maria.util :as util]
            [cljs.pprint :as pp]))


(d/transact! [[:db/add :feature :in-place-eval true]])

(defview cell-list
  {:key                     :source-id
   :view/initial-state      (fn [{value :value}]
                              {:last-update value
                               :cells       (Cell/ensure-cells (Cell/from-source value))})
   :view/did-mount          (fn [{:keys [view/state] :as this}]
                              (Cell/focus! (first (:cells @state)) :start)
                              (exec/set-context! {:cell-list this})
                              (when (= (str (d/get-in :router/location [:query :eval])) "true")
                                (e/on-load #(exec/exec-command-name :eval/doc))))
   :view/will-unmount       #(exec/set-context! {:cell-list nil})
   :view/will-receive-props (fn [{value :value
                                  state :view/state
                                  :as   this}]
                              ;; normally, the source we are passed from above during editing
                              ;; is the source we last passed upwards in :will-receive-state.
                              ;; if these are different, the document has changed and we
                              ;; should re-parse from scratch.
                              (when (not= value (:last-update @state))
                                (swap! state assoc :cells (Cell/ensure-cells (Cell/from-source value)))))
   :view/should-update      (fn [{:keys [view/state
                                         view/prev-state
                                         on-update]}]
                              (let [cells-changed? (not= (:cells @state) (:cells prev-state))]
                                (when cells-changed?
                                  (let [updated-source (Cell/emit-many (:cells @state))]
                                    (v/swap-silently! state assoc :last-update updated-source)
                                    (on-update updated-source)))
                                cells-changed?))}
  [{:keys [view/state]}]
  (let [{:keys [cells]} @state]
    [:.w-100.flex-none.pv3
     (->> cells
          (map
            (fn [{:keys [id] :as cell}]
              (let [props {:id        id
                           :cell      cell
                           :cells     cells
                           :ref       #(if % (Cell/mount id %)
                                             (Cell/unmount id))
                           :splice!   (fn splice
                                        ([id value]
                                         (splice id 0 value))
                                        ([id n value]
                                         (:cells (swap! state update :cells Cell/splice-by-id id n value))))
                           :on-update #(swap! state update :cells Cell/splice-by-id id [(Cell/->ProseCell id %)])}]
                (condp instance? cell
                  Cell/ProseCell (prose/prose-cell-view (assoc props
                                                          :on-update #(swap! state update :cells Cell/splice-by-id id [(Cell/->ProseCell id %)])))
                  Cell/CodeCell (code/code-view (assoc props
                                                  :on-ast #(swap! state update :cells Cell/splice-by-id id [(Cell/->CodeCell id (:value %))]))))))))]))

