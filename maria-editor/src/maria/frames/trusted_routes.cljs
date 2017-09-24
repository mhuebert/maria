(ns maria.frames.trusted-routes
  (:require [maria.frames.trusted-views :as trusted-views]
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
                   :segments segments)
        curriculum? (and (= 1 (count segments))
                         (contains? curriculum/slugs (first segments)))
        segments (if curriculum?
                   ["curriculum" (first segments)]
                   segments)]
    (match segments
           []
           (do (some-> current-username (github/load-user-gists))
               (trusted-views/editor-frame-view {:db/transactions [route-tx]}))

           ["curriculum" slug]
           (match-route-segments ["http-text" (first (for [[module-slug _ id] curriculum/modules
                                                         :when (= slug module-slug)]
                                                     id))])
           ["quickstart"]
           (match-route-segments ["http-text" (:db/id (second curriculum/docs))])

           ["modules"]
           (match-route-segments ["gists" "modules"])

           ["http-text" id]
           (let [url (js/decodeURIComponent id)]
             (github/load-url-text url)
             (trusted-views/editor-frame-view {:db/transactions [(assoc route-tx :segments ["doc" id])]
                                               :current-entity  id}))


           ;; re/ :current-entity
           ;; We keep track of :current-entity from our trusted frame,
           ;; and only support edit actions on that entity. This is to
           ;; prevent malicious code from attempting to overwrite
           ;; documents other than what the user is currently editing.

           ["gists" username]
           (do (some-> username (github/load-user-gists))
               (trusted-views/editor-frame-view {:db/transactions [route-tx]
                                                 :db/queries      (when username
                                                                    [[:doc.owner/username username]])}))


           (:or
             ["local" id]
             ["local"])
           (trusted-views/editor-frame-view {:db/transactions [route-tx]})

           ["gist" id]
           (match-route-segments ["gist" id false])

           ["home"] (do
                      (some-> current-username (github/load-user-gists))
                      (trusted-views/editor-frame-view {:db/transactions [route-tx]}))

           ["gist" id filename]
           (do (github/load-gist id)
               (trusted-views/editor-frame-view {:current-entity  id
                                                 :db/transactions [(assoc route-tx :segments ["doc" id])]})))))

