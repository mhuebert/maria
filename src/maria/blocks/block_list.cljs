(ns maria.blocks.block-list
  (:require [re-view.core :as v :refer [defview]]
            [cljsjs.markdown-it]
            [re-db.d :as d]
            [maria.blocks.code]
            [maria.blocks.prose]
            [maria.blocks.blocks :as Block]
            [maria-commands.exec :as exec]
            [maria.commands.blocks]
            [maria.eval :as e]
            [clojure.set :as set]
            [cells.eval-context :as eval-context]))

(d/transact! [[:db/add :feature :in-place-eval true]])

(defn selected-block []
  (get-in @exec/context [:block-view :block]))

(defn apply-selections [key blocks]
  (when-let [[block selections] (get (meta blocks) key)]
      (when block
        (Block/focus! block)
        (when selections
          (Block/put-selections! block selections)))))

(defn get-selections []
  (when-let [block (selected-block)]
    [block (Block/get-history-selections block)]))

(defn merge-version? [prev-blocks]
  (let [{[block _] :selections-after
         timestamp :timestamp} (meta prev-blocks)]
    (and (< (- (.now js/Date) timestamp)
            300)
         (= (:id block) (:id (selected-block))))))

(defn update-first-meta [coll f & args]
  (cons (with-meta (first coll) (apply f (meta (first coll)) args))
        (rest coll)))

(def ^:dynamic *undo-op* false)
(def -prior-selection (volatile! nil))

(defn dispose-removed
  "Compares prev- and next- block lists and 'disposes' of blocks which have been removed."
  ;; the history system works on frozen states, but blocks have a lifecycle.
  ;; so we have to manually 'dispose' of blocks that are removed due to
  ;; undo/redo operations.
  [prev-blocks next-blocks]
  (let [ids (set/difference (set (map :id prev-blocks))
                            (set (map :id next-blocks)))
        blocks (filter (comp ids :id) prev-blocks)]
    (doseq [block blocks]
      (eval-context/dispose! block))))

(defview block-list
  {:key                     :source-id
   :view/initial-state      (fn [{value :value}]
                              {:last-update value
                               :history     (list (Block/ensure-blocks (Block/from-source value)))})
   :view/did-mount          (fn [this]
                              (Block/focus! (first (.getBlocks this)) :start)
                              (exec/set-context! {:block-list this})
                              (when (= (str (d/get-in :router/location [:query :eval])) "true")
                                (e/on-load #(exec/exec-command-name :eval/doc)))
                              (js/setTimeout
                                #(let [selections (get-selections)]
                                   (v/swap-silently! (:view/state this) update :history update-first-meta merge
                                                     {:selections-before selections
                                                      :selections-after  selections
                                                      :timestamp         (.now js/Date)})
                                   (vreset! -prior-selection selections)) 0))

   :undo                    (fn [{:keys [view/state]}]
                              (let [history (:history @state)]
                                (when (second history)
                                  (let [prev-blocks (first history)
                                        next-blocks (second history)]
                                    (dispose-removed prev-blocks next-blocks)
                                    (binding [*undo-op* true]
                                      (reset! state (-> @state
                                                        (update :history rest)
                                                        (update :history/redo-stack conj prev-blocks)))
                                      (v/flush!)
                                      (apply-selections :selections-before prev-blocks))))))
   :redo                    (fn [{:keys [view/state]}]
                              (when-let [next-blocks (first (:history/redo-stack @state))]
                                (binding [*undo-op* true]
                                  (dispose-removed (first (:history @state)) next-blocks)
                                  (reset! state (-> @state
                                                    (update :history/redo-stack rest)
                                                    (update :history conj next-blocks)))
                                  (v/flush!)
                                  (apply-selections :selections-after next-blocks))))
   :clear-history           (fn [{:keys [view/state]}]
                              (v/swap-silently! state dissoc :history/redo-stack :history))
   :add-to-history          (fn [{:keys [view/state]} blocks]
                              (when-not *undo-op*
                                (binding [*undo-op* true]
                                  (let [{:keys [history history/redo-stack]} @state
                                        prev-blocks (first history)
                                        merge? (merge-version? prev-blocks)
                                        next-blocks (with-meta blocks {:selections-before (if merge? (:selections-before (meta (first history)))
                                                                                                     @-prior-selection)
                                                                       :selections-after  (get-selections)
                                                                       :timestamp         (.now js/Date)})]
                                    (v/flush!)
                                    (reset! state (-> @state
                                                      (cond-> merge? (update :history rest))
                                                      (update :history conj next-blocks)
                                                      (cond-> (seq redo-stack) (update :history/redo-stack empty))))))))
   :view/will-unmount       #(exec/set-context! {:block-list nil})
   :view/will-receive-props (fn [{value                       :value
                                  source-id                   :source-id
                                  {prev-source-id :source-id} :view/prev-props
                                  state                       :view/state
                                  :as                         this}]
                              (when (not= source-id prev-source-id)
                                ;; clear history when editing a new doc
                                (.clearHistory this))
                              (when (not= value (:last-update @state))
                                ;; re-parse blocks when new value doesn't match last emitted value
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
                  (Block/render block {:blocks        blocks
                                       :block-list    this
                                       :before-change #(vreset! -prior-selection (get-selections))})) blocks))))

