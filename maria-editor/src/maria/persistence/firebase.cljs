(ns maria.persistence.firebase
  (:require [re-view.core :as v :refer [defview]]
            [maria.persistence.tokens :as tokens]
            [re-db.d :as d]
            [goog.object :as gobj]
            [maria.persistence.local :as local]
            [maria.persistence.github :as github]))

(def CONNECT_ERR_MESSAGE "Could not initialize Firebase auth")

(def firebase-auth (try (.auth js/firebase)
                        (catch js/Error e
                          (prn CONNECT_ERR_MESSAGE))))


(def providers (try {"github.com" (doto (js/firebase.auth.GithubAuthProvider.)
                                    (.addScope "gist"))}
                    (catch js/Error e
                      (prn CONNECT_ERR_MESSAGE))))

(defn sign-in [provider]
  (->
    (.signInWithPopup firebase-auth (get providers provider))
    (.then (fn [result]
             (tokens/put-token provider (-> result
                                            (gobj/get "credential")
                                            (gobj/get "accessToken")))))))

(defn sign-out []
  (local/local-put! "auth/accessToken" nil)
  (tokens/clear-tokens)
  (.signOut firebase-auth))


(try
  (.onAuthStateChanged firebase-auth (fn [user]
                                       (d/transact! (if-let [{:keys [displayName uid providerData]} (some-> user (.toJSON) (js->clj :keywordize-keys true))]
                                                      (let [github-id (get-in providerData [0 :uid])]
                                                        (github/get-username github-id (fn [{:keys [value error]}]
                                                                                         (if value (d/transact! [{:db/id     :auth-public
                                                                                                                  :username  value
                                                                                                                  :maria-url (str "/gists/" value)}])
                                                                                                   (if (re-find #"40" error)
                                                                                                     (sign-in (:providerId (first providerData)))
                                                                                                     (.error js/console "Unable to retrieve GitHub username" error)))))
                                                        [{:db/id        :auth-public
                                                          :display-name displayName
                                                          :id           github-id
                                                          :signed-in?   true}
                                                         (merge {:db/id         :auth-secret
                                                                 :uid           uid
                                                                 :provider-data providerData})])
                                                      [[:db/retract-entity :auth-public]
                                                       [:db/retract-entity :auth-secret]]))))
  (catch js/Error e
    (prn CONNECT_ERR_MESSAGE)))

