(ns maria.trusted.routes
  (:require [maria.trusted.frame-views :as frame-view]
            [re-view.core :as v :refer [defview]]
            [cljs.core.match :refer-macros [match]]
            [maria.curriculum :as curriculum]
            [maria.persistence.github :as github]))

(defn match-route-segments [segments]
  (match segments
         []
         (match-route-segments ["new"])
         #_(match-route-segments ["modules" "intro"])

         ["modules"]
         (match-route-segments ["gists" "modules"])

         ["modules" module-name]
         (match-route-segments ["gist" (curriculum/modules-by-path module-name)])

         ["new"]
         (frame-view/editor-frame-view {:entity-id       "new"
                                        :db/transactions [[:db/add :layout 1 segments]]})

         ["gists" username]
         (do (github/load-user-gists username)
             (frame-view/editor-frame-view {:entity-id       username
                                            :db/transactions [[:db/add :layout 1 segments]]}))

         ["gist" id]
         (match-route-segments ["gist" id false])

         ["gist" id filename]
         (do (github/load-gist id)
             (frame-view/editor-frame-view {:entity-id       id
                                            :db/transactions [[:db/add :layout 1 segments]]}))))

