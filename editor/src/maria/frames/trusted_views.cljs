(ns maria.frames.trusted-views
  (:require [chia.view :as v]
            [chia.triple-db :as d]
            [maria.frames.frame-communication :as frame]
            [cljs.core.match :refer-macros [match]]
            [maria.frames.trusted-actions :as actions]))

(v/defview frame-view
  {:view/initial-state #(do {:frame-id (str (gensym))})
   #_#_:spec/props {:id {:spec string?
                     :doc "unique ID for frame"}
                :on-message {:spec :Function
                             :doc "Function to be called with messages from iFrame."}}
   :view/did-mount (fn [{:keys [view/state on-message] :as this}]
                     (when on-message
                       (frame/listen (:frame-id @state) on-message))
                     (.sendTransactions this))
   :view/did-update (fn [{:keys [on-message view/state db/transactions] {prev-tx :db/transactions
                                                                         prev-on-message :on-message} :view/prev-props :as this}]
                      (let [{:keys [frame-id]} @state]
                        (when-not (= on-message prev-on-message)
                          (frame/unlisten frame-id prev-on-message)
                          (frame/listen frame-id on-message))
                        (when (not= transactions prev-tx)
                          (.sendTransactions this))))
   :view/will-unmount (fn [{:keys [view/state on-message]}]
                        (frame/unlisten (:frame-id @state) on-message))}
  [{:keys [view/state]}]
  [:iframe.maria-editor-frame
   {:allow "geolocation"
    :src (str frame/child-origin "/live.html#frame_" (:frame-id @state))}])

(v/extend-view frame-view
  Object
  (send [{:keys [view/state]} message]
    (frame/send (:frame-id @state) message))
  (sendTransactions [{:keys [db/transactions view/state]}]
    (frame/send (:frame-id @state) [:db/transactions transactions])))

(v/defview editor-frame-view
  {#_#_:spec/props {:default-value :String}}
  [{:keys [current-entity db/transactions db/queries]
    :or {queries []}}]
  (let [username (d/get :auth-public :username)
        queries (cond-> queries
                        username (conj [[:doc.owner/username username]]))]
    (frame-view
     {:db/transactions (cond-> (into [(d/entity :auth-public)
                                      [:db/add :window/location :origin (.. js/window -location -origin)]
                                      (d/entity :remote/status)
                                      (some-> current-entity (d/entity))
                                      (some-> username (d/entity))]
                                     transactions)
                               (seq queries) (into (mapcat d/entities queries)))
      :on-message (actions/handle-message current-entity)})))

