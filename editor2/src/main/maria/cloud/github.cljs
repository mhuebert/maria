(ns maria.cloud.github
  (:require ["firebase/app" :as firebase]
            ["firebase/auth" :as auth :refer [getAuth
                                              GithubAuthProvider
                                              signInWithPopup
                                              getAdditionalUserInfo]]
            [applied-science.js-interop :as j]
            [maria.cloud.config.public :refer [env]]
            [maria.cloud.local :as local]
            [promesa.core :as p]
            [re-db.reactive :as r]))

;; TODO ...
;; wait to load gist page until we have an auth result (in/out)
;; to avoid load failure while waiting for auth to complete?

(defonce !token (local/ratom nil))
(defonce !user (r/atom nil)) ;; `nil` means not loaded, `false` means signed out

(defn token [] (when @!user @!token))

(defonce App (firebase/initializeApp (clj->js (:firebase env))))

(def provider (doto (new GithubAuthProvider)
                (.addScope "gist")))

(j/defn handle-user! [^js {:as user :keys [photoURL email displayName uid]}]
  (j/log user)
  (if (and user
           (= uid (:uid @!token)))
    (reset! !user
            {:photo-url photoURL
             :email email
             :display-name displayName})
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