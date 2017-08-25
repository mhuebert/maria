(ns maria.blocks.list-view
  (:require [re-view.core :as v :refer [defview]]
            [cljsjs.markdown-it]
            [re-db.d :as d]
            [maria.blocks.code :as code]
            [maria.blocks.prose]
            [maria.blocks.blocks :as Block]
            [maria-commands.exec :as exec]
            [maria.commands.blocks]
            [maria.eval :as e]))


(d/transact! [[:db/add :feature :in-place-eval true]])

(defn apply-selections [blocks]
  (when-let [{:keys [selections selections/block]} (meta blocks)]
    (Block/put-selections! block selections)
    #_(try
      (Block/put-selections! block selections)
      (catch js/Error e nil))))

(defn current-selections []
  (let [block (get-in @exec/context [:block-view :block])]
    {:selections/block block
     :selections       (Block/get-selections block)}))

(def ^:dynamic *undo-op* false)

(defview block-list
  {:key                     :source-id
   :view/initial-state      (fn [{value :value}]
                              {:last-update value
                               :history     (list (Block/ensure-blocks (Block/from-source value)))})
   :view/did-mount          (fn [this]
                              (Block/focus! (first (.getBlocks this)) :start)
                              (exec/set-context! {:block-list this})
                              (when (= (str (d/get-in :router/location [:query :eval])) "true")
                                (e/on-load #(exec/exec-command-name :eval/doc))))


   ;; TODO
   ;; with each `undo` step, also save the current selection(s),
   ;; ie. [{:start [block data] :end [block data]}],
   ;; so that we can reset the selections during undo/redo.
   ;;
   ;; this is also not a bad thing to work through because we want
   ;; to be able to select across blocks, which means tracking this
   ;; info anyway.
   ;;
   ;; in fact we ultimately want to be able to click and drag to
   ;; select any range of any blocks.
   ;;



   :undo                    (fn [{:keys [view/state]}]
                              (when-let [blocks (some-> (seq (:history @state)) (first))]
                                (binding [*undo-op* true]
                                  (reset! state (-> @state
                                                    (update :history rest)
                                                    (update :history/redo-stack conj blocks)))
                                  (v/flush!)
                                  (apply-selections (first (:history @state))))))
   :redo                    (fn [{:keys [view/state]}]
                              (when-let [blocks (first (:history/redo-stack @state))]
                                (binding [*undo-op* true]
                                  (reset! state (-> @state
                                                    (update :history/redo-stack rest)
                                                    (update :history conj blocks)))
                                  (v/flush!)
                                  (apply-selections blocks))))
   :add-to-history          (fn [{:keys [view/state]} blocks]
                              (when-not *undo-op*
                                (binding [*undo-op* true]
                                  (reset! state (-> @state
                                                    (update :history conj (with-meta blocks (current-selections)))
                                                    (update :history/redo-stack empty)))
                                  (v/flush!))))
   :view/will-unmount       #(exec/set-context! {:block-list nil})
   :view/will-receive-props (fn [{value :value
                                  state :view/state
                                  :as   this}]
                              ;; normally, the source we are passed from above during editing
                              ;; is the source we last passed upwards in :will-receive-state.
                              ;; if these are different, the document has changed and we
                              ;; should re-parse from scratch.
                              (when (not= value (:last-update @state))
                                (.addToHistory this (Block/ensure-blocks (Block/from-source value)))))
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
   :splice                  (fn splice
                              ([this block value] (splice this block 0 value))
                              ([{:keys [view/state] :as this} block n value]
                               (let [blocks (-> (first (:history @state))
                                                (Block/splice-block block n value)
                                                (Block/join-blocks)
                                                (Block/ensure-blocks))]
                                 (.addToHistory this blocks)
                                 blocks)))}
  [{:keys [view/state] :as this}]
  (let [{:keys [history]} @state
        blocks (first history)]
    (into [:.w-100.flex-none.pv3]
          (mapv (fn [block]
                  (Block/render block {:blocks     blocks
                                       :block-list this})) blocks))))

