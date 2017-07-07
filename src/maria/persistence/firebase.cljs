(ns maria.persistence.firebase
  (:require [re-db.d :as d]))

(def auth (.auth js/firebase))

(.onAuthStateChanged auth (fn [user]
                            (d/transact! (if-let [{{:keys [accessToken]} :stsTokenManager
                                                   :keys                 [providerData providerId uid]} (some-> user (.toJSON) (js->clj :keywordize-keys true))]
                                           [{:db/id      :auth-public
                                             :provider   providerId
                                             :signed-in? true}
                                            (merge {:db/id :auth-secret
                                                    :uid   uid
                                                    :token accessToken}
                                                   (first providerData))]
                                           [[:db/retract-entity :auth-public]
                                            [:db/retract-entity :auth-secret]]))))


(def providers {:github (doto (js/firebase.auth.GithubAuthProvider.)
                          (.addScope "gist"))})

(defn sign-in [provider]
  (-> (.signInWithPopup auth (get providers provider))
      (.then (fn [result]
               ))))

(defn sign-out []
  (.signOut auth))