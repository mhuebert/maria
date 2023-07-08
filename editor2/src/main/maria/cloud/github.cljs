(ns maria.cloud.github
  (:require ["firebase/app" :as firebase]
            ["firebase/auth" :as auth :refer [getAuth
                                              GithubAuthProvider
                                              signInWithPopup
                                              getAdditionalUserInfo]]
            [applied-science.js-interop :as j]
            [maria.cloud.config.public :refer [env]]
            [maria.cloud.local :as local]
            [maria.editor.keymaps :as keymaps]
            [maria.editor.util :as u]
            [promesa.core :as p]
            [re-db.reactive :as r]))

;; TODO ...
;; wait to load gist page until we have an auth result (in/out)
;; to avoid load failure while waiting for auth to complete?

(defonce !token (local/ratom nil))
(defonce !user (r/atom nil)) ;; `nil` means not loaded, `false` means signed out

(defn token [] (when @!user @!token))

(defn auth-headers []
  (when-let [t (:token (token))]
    {:Authorization (str "Bearer " t)}))

(defonce App (firebase/initializeApp (clj->js (:firebase env))))

(def provider (doto (new GithubAuthProvider)
                (.addScope "gist")))

(j/defn handle-user! [^js {:as user :keys [photoURL email displayName uid]
                           [{github-uid :uid}] :providerData}]
  (if (and user
           (= uid (:uid @!token)))
    (do (reset! !user
                {:photo-url photoURL
                 :email email
                 :display-name displayName})
        (p/-> (u/fetch (str "https://api.github.com/user/" github-uid) :headers (auth-headers))
              (j/call :json)
              (j/get :login)
              (->> (swap! !user assoc :username))))
    (do (reset! !token nil)
        (reset! !user false))))

(defn handle-token! [uid token]
  (assert token)
  (reset! !token {:uid uid :token token}))

(defn sign-in-with-popup! []
  (p/let [result (signInWithPopup (getAuth) provider)]
    (handle-token! (j/get-in result [:user :uid])
                   (-> (.credentialFromResult GithubAuthProvider result)
                       (j/get :accessToken)))
    (handle-user! (j/get result :user))))


(defn sign-out []
  (.signOut (getAuth)))

(defonce _auth_callback
  (try
    (.onAuthStateChanged (getAuth) handle-user!)))

(j/defn parse-gist [^js {:as gist
                         :keys [id
                                description
                                files
                                html_url
                                updated_at]
                         {:keys [login]} :owner}]
  (when-let [files (some->> files
                            (js/Object.values)
                            (keep (j/fn [^js {:keys [filename language content]}]
                                    (when (= language "Clojure")
                                      #:gist{:filename filename
                                             :language language
                                             :content content})))
                            seq)]
    #:gist{:id id
           :owner login
           :description description
           :clojure-files files
           :html_url html_url
           :updated_at updated_at}))

(keymaps/register-commands! {:account/sign-in {:f (fn [_] (sign-in-with-popup!))
                                               :when (fn [_] (not @!user))}
                             :account/sign-out {:f    (fn [_] (sign-out))
                                                :when (fn [_] (boolean @!user))}})