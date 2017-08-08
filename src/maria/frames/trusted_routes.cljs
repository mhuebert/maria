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
  (match segments
         []
         (match-route-segments ["modules" "intro"])
         #_(match-route-segments ["modules" "intro"])

         ["modules"]
         (match-route-segments ["gists" "modules"])

         ["modules" module-name]
         (match-route-segments ["gist" (curriculum/modules-by-path module-name)])


         ;; re/ :current-entity
         ;; We keep track of :current-entity from our trusted frame,
         ;; and only support edit actions on that entity. This is to
         ;; prevent malicious code from attempting to overwrite
         ;; documents other than what the user is currently editing.


         ["new"]
         (frame-view/editor-frame-view {:current-entity  "new"
                                        :db/transactions [(assoc (sanitized-location)
                                                            :segments segments)]})

         ["gists" username]
         (do (github/load-user-gists username)
             (frame-view/editor-frame-view {:current-entity  username
                                            :db/transactions [(assoc (sanitized-location)
                                                                :segments segments)]}))

         ["gist" id]
         (match-route-segments ["gist" id false])

         ["gist" id filename]
         (do (github/load-gist id)
             (frame-view/editor-frame-view {:current-entity  id
                                            :db/transactions [(assoc (sanitized-location)
                                                                :segments segments)]}))))

