(ns maria.frames.trusted-routes
  (:require [maria.frames.trusted-views :as frame-view]
            [re-view.core :as v :refer [defview]]
            [cljs.core.match :refer-macros [match]]
            [maria.curriculum :as curriculum]
            [maria.persistence.github :as github]
            [re-db.d :as d]))

(defn sanitized-location
  "Do not share more information than necessary with live frame."
  []
  (-> (d/entity :router/location)
      (dissoc :path)
      (update :query #(select-keys % [:eval]))))

(defn match-route-segments [segments]
  (let [current-username (d/get :auth-public :username)
        route-tx (assoc (sanitized-location)
                   :segments segments)]
    (match segments
           []
           (match-route-segments ["modules" (:db/id (first curriculum/as-gists))])
           #_(match-route-segments ["modules" "intro"])

           ["modules"]
           (match-route-segments ["gists" "modules"])
           ["modules" url]
           (match-route-segments ["http-text" url])

           ["http-text" id]
           (let [url (js/decodeURIComponent id)]
             (github/load-url-text url)
             (frame-view/editor-frame-view {:db/transactions [(assoc route-tx :segments ["doc" id])]
                                            :current-entity  id}))


           ;; re/ :current-entity
           ;; We keep track of :current-entity from our trusted frame,
           ;; and only support edit actions on that entity. This is to
           ;; prevent malicious code from attempting to overwrite
           ;; documents other than what the user is currently editing.

           ["gists" username]
           (do (some-> username (github/load-user-gists))
               (frame-view/editor-frame-view {:db/transactions [route-tx]
                                              :db/queries      (when username
                                                                 [[:doc.owner/username username]])}))


           (:or
             ["local" id]
             ["local"])
           (frame-view/editor-frame-view {:db/transactions [route-tx]})

           ["gist" id]
           (match-route-segments ["gist" id false])

           ["home"] (do
                      (some-> current-username (github/load-user-gists))
                      (frame-view/editor-frame-view {:db/transactions [route-tx]
                                                     :db/queries      (when current-username
                                                                        [[:doc.owner/username current-username]])}))

           ["gist" id filename]
           (do (github/load-gist id)
               (frame-view/editor-frame-view {:current-entity  id
                                              :db/transactions [(assoc route-tx :segments ["doc" id])]})))))

