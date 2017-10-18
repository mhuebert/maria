(ns maria.pages.block_list
  (:require [re-view.core :as v :refer [defview]]
            [maria.blocks.history :as history]
            [re-db.d :as d]
            [maria.blocks.code]
            [maria.blocks.prose]
            [maria.blocks.blocks :as Block]
            [lark.commands.exec :as exec]
            [maria.commands.blocks]
            [maria.eval :as e]))

(exec/add-context-augmenter! :auth #(assoc % :signed-in? (d/get :auth-public :signed-in?)))

(exec/add-context-augmenter! :block-meta (fn [{:keys [block-view] :as context}]
                                           (cond-> context
                                                   block-view (merge {:editor (.getEditor block-view)}
                                                                     (select-keys block-view [:block :blocks])))))

(defview BlockList
  {:key                     :source-id
   :view/initial-state      (fn [{value :value}] (history/initial-state value))
   :view/did-mount          (fn [this]
                              (history/after-mount (:view/state this))
                              (exec/set-context! {:block-list this})
                              (when (= (str (d/get-in :router/location [:query :eval])) "true")
                                (e/on-load #(exec/exec-command-name :eval/doc))))
   :view/will-unmount       #(exec/set-context! {:block-list nil})
   :view/will-receive-props (fn [{value                       :value
                                  source-id                   :source-id
                                  {prev-source-id :source-id} :view/prev-props
                                  state                       :view/state
                                  :as                         this}]
                              (when (not= source-id prev-source-id)
                                ;; clear history when editing a new doc
                                (history/clear! state))
                              (when (not= value (:last-update @state))
                                ;; re-parse blocks when new value doesn't match last emitted value
                                (history/add! state (Block/ensure-blocks (Block/from-source value)))))
   :view/should-update      (fn [{:keys                   [view/state
                                                           on-update]
                                  {prev-history :history} :view/prev-state}]
                              (let [{:keys [history]} @state
                                    blocks-changed? (not= (first history)
                                                          (first prev-history))]
                                (when blocks-changed?
                                  (let [updated-source (Block/emit-list (first history))]
                                    (v/swap-silently! state assoc :last-update updated-source)
                                    (on-update updated-source)))
                                blocks-changed?))
   :get-blocks              #(first (:history @(:view/state %)))
   :splice                  (fn [this & args]
                              (apply history/splice (:view/state this) args))}
  [{:keys [view/state] :as this}]
  (let [{:keys [history]} @state
        blocks (first history)]
    (into [:.w-100.flex-none.pv3]
          (mapv (fn [block]
                  (Block/render block {:blocks        blocks
                                       :block-list    this
                                       :before-change history/before-change})) blocks))))

