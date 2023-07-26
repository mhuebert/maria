(ns maria.cloud.github
  (:require ["firebase/app" :as firebase]
            ["firebase/auth" :as auth :refer [getAuth
                                              GithubAuthProvider
                                              signInWithPopup
                                              getAdditionalUserInfo]]
            [applied-science.js-interop :as j]
            [clojure.string :as str]
            [maria.cloud.config.public :refer [env]]
            [maria.cloud.local-sync :as local-sync]
            [maria.editor.keymaps :as keymaps]
            [maria.editor.util :as u]
            [promesa.core :as p]
            [re-db.api :as db]
            [re-db.reactive :as r]))

;; TODO ...
;; wait to load gist page until we have an auth result (in/out)
;; to avoid load failure while waiting for auth to complete?

(defonce !initialized? (r/atom false))

(defn get-user [] (db/get ::user))

(defn set-user! [x]
  (db/transact! [(assoc x :db/id ::user)]))

(defn get-token
  ([] (get-token (:uid (get-user))))
  ([uid] (get (local-sync/get-entity ::token) uid)))

(defn any-tokens? []
  (some? (local-sync/get-entity ::token)))

(defn set-token! [uid->token]
  (local-sync/swap-entity ::token (constantly uid->token)))

(defn auth-headers []
  (when-let [t (get-token)]
    {:Authorization (str "Bearer " t)}))

(defonce App (firebase/initializeApp (clj->js (:firebase env))))

(def provider (doto (new GithubAuthProvider)
                (.addScope "gist")))

(j/defn handle-user! [^js {:as user :keys [photoURL email displayName uid]
                           [{github-uid :uid}] :providerData}]
  (if (and user (get-token uid))
    (do (set-user! {:uid uid
                    :photo-url photoURL
                    :email email
                    :display-name displayName})
        (p/let [username (p/-> (u/fetch (str "https://api.github.com/user/" github-uid) :headers (auth-headers))
                               (j/call :json)
                               (j/get :login))]
          (db/transact! [[:db/add ::user :username username]])
          (reset! !initialized? true)))
    (do (set-token! nil)
        (db/transact! [[:db/retractEntity ::user]])
        (reset! !initialized? true))))

(defn sign-in-with-popup! []
  (p/let [result (signInWithPopup (getAuth) provider)]
    (set-token! {(j/get-in result [:user :uid])
                 (-> (.credentialFromResult GithubAuthProvider result)
                     (j/get :accessToken))})
    (handle-user! (j/get result :user))))


(defn sign-out []
  (.signOut (getAuth)))

(defonce _auth_callback
  (try
    (.onAuthStateChanged (getAuth) handle-user!)))

(j/defn parse-gist [^js {:keys [id
                                description
                                files
                                html_url
                                updated_at]
                         {:keys [login]} :owner}]
  (when-let [[file] (some->> files
                             (js/Object.values)
                             (keep (j/fn [^js {:keys [filename language content]}]
                                     (when (= language "Clojure")
                                       {:file/id (str "gist:" id "/" filename)
                                        :file/title (some-> description
                                                            str/trim
                                                            (str/split-lines)
                                                            first
                                                            (u/guard (complement str/blank?)))
                                        :file/name filename
                                        :file/language language
                                        :file/source content
                                        :file/provider :file.provider/gist})))
                             seq)]
    (merge file
           {:gist/id id
            :gist/owner login
            :gist/description description
            :gist/html_url html_url
            :gist/updated_at updated_at})))

(keymaps/register-commands!
  {:account/sign-in {:f (fn [_] (sign-in-with-popup!))
                     :when (fn [_] (not (get-user)))}
   :account/sign-out {:f (fn [_] (sign-out))
                      :when (fn [_] (some? (get-user)))}})