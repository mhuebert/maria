(ns maria.cells.list-view
  (:require [re-view.core :as v :refer [defview]]
            [maria.cells.code :as code]
            [clojure.core.match :refer-macros [match]]
            [cljsjs.markdown-it]
            [maria.cells.prose :as prose]
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
                                  (let [updated-source (Cell/emit-list (:cells @state))]
                                    (v/swap-silently! state assoc :last-update updated-source)
                                    (on-update updated-source)))
                                cells-changed?))
   :get-cells               #(:cells @(:view/state %))
   :splice                  (fn splice
                              ([this cell value] (splice this cell 0 value))
                              ([{:keys [view/state]} cell n value]
                               (let [cells (Cell/splice-by-id (:cells @state) (:id cell) n value)]
                                 (js/setTimeout #(swap! state update :cells Cell/join-cells) 0)
                                 (:cells (swap! state assoc :cells cells)))))}
  [{:keys [view/state] :as this}]
  (let [{:keys [cells]} @state]
    [:.w-100.flex-none.pv3
     (map (fn [cell]
            (Cell/render cell {:cells     cells
                               :cell-list this})) cells)]))

