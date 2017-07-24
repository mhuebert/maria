(ns maria.frames.views
  (:require [re-view.core :as v :refer [defview]]
            [re-db.d :as d]
            [maria.frames.communication :as frame]
            [maria.persistence.local :as local]
            [cljs.core.match :refer-macros [match]]
            [maria.frames.trusted-actions :as actions]))

(defview frame-view
  {:view/initial-state      #(do {:frame-id (str (gensym))})
   :spec/props              {:id         {:spec :String
                                          :doc  "unique ID for frame"}
                             :on-message {:spec :Function
                                          :doc  "Function to be called with messages from iFrame."}
                             #_:on-change  #_{:spec :Function
                                              :doc  "Function to be called with source, whenever it changes."}
                             #_:on-save    #_{:spec :Function
                                              :doc  "Function to be called with source, whenever 'save' command is fired"}}
   :send                    (fn [{:keys [view/state]} message]
                              (frame/send (:frame-id @state) message))
   :send-transactions       (fn [{:keys [db/transactions view/state]}]
                              (frame/send (:frame-id @state) [:db/transactions transactions]))
   :view/did-mount          (fn [{:keys [view/state on-message] :as this}]
                              (when on-message
                                (frame/listen (:frame-id @state) on-message))
                              (.sendTransactions this))
   :view/will-receive-props (fn [{:keys [on-message view/state db/transactions] {prev-tx         :db/transactions
                                                                                 prev-on-message :on-message} :view/prev-props :as this}]
                              (let [{:keys [frame-id]} @state]
                                (when-not (= on-message prev-on-message)
                                  (frame/unlisten frame-id prev-on-message)
                                  (frame/listen frame-id on-message))
                                (when (not= transactions prev-tx)
                                  (.sendTransactions this))))
   :view/will-unmount       (fn [{:keys [view/state on-message]}]
                              (frame/unlisten (:frame-id @state) on-message))}
  [{:keys [view/state]}]
  [:iframe.maria-editor-frame
   {:src (str frame/child-origin "/live.html#frame_" (:frame-id @state))}])

(defview editor-frame-view
  {:spec/props {:default-value :String}}
  [{:keys [entity-id db/transactions]}]
  (frame-view {:db/transactions (into [(or (d/entity :auth-public)
                                           [:db/retract-entity :auth-public])
                                       (some-> entity-id (d/entity))]
                                      transactions)
               :on-message      (actions/handle-message entity-id)}))
