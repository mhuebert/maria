(ns maria.persistence.firebase
  (:require [chia.view :as v]
            [maria.persistence.tokens :as tokens]
            [chia.triple-db :as d]
            [goog.object :as gobj]
            [maria.persistence.local :as local]
            [maria.persistence.github :as github]
            ["firebase/app" :as firebase]
            ["firebase/auth"]))

;(def firebase js/firebase)

(def CONNECT_ERR_MESSAGE "Could not initialize Firebase auth")

(defonce _init_firebase_

         (.initializeApp firebase #js {:apiKey "AIzaSyCu4fEYMcfW6vzQWB0TS--Jphv3hzBqUyo",
                                       :authDomain "maria-d04a7.firebaseapp.com",
                                       :databaseURL "https://maria-d04a7.firebaseio.com",
                                       :projectId "maria-d04a7",
                                       :storageBucket "maria-d04a7.appspot.com",
                                       :messagingSenderId "832199278239"}))

(defonce ^js firebase-auth
         (try (.auth firebase)
              (catch js/Error e
                (prn CONNECT_ERR_MESSAGE))))

(defonce providers (try {"github.com" (doto (new (.. firebase -auth -GithubAuthProvider))
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


(defonce _auth_callback_
         (try
           (.onAuthStateChanged
            ^js firebase-auth
            (fn [user]
              (d/transact! (if-let [{:keys [displayName uid providerData]} (some-> user (.toJSON) (js->clj :keywordize-keys true))]
                             (let [github-id (get-in providerData [0 :uid])]
                               (github/get-username github-id (fn [{:keys [value error]}]
                                                                (if value (d/transact! [{:db/id :auth-public
                                                                                         :username value
                                                                                         :local-url (str "/gists/" value)}])
                                                                          (if (re-find #"40" error)
                                                                            (sign-in (:providerId (first providerData)))
                                                                            (.error js/console "Unable to retrieve GitHub username" error)))))
                               [{:db/id :auth-public
                                 :display-name displayName
                                 :id github-id
                                 :signed-in? true}
                                (merge {:db/id :auth-secret
                                        :uid uid
                                        :provider-data providerData})])
                             [[:db/retract-entity :auth-public]
                              [:db/add :auth-public :signed-in? false]
                              [:db/retract-entity :auth-secret]]))))
           (catch js/Error e
             (.error js/console e)
             (prn CONNECT_ERR_MESSAGE))))

