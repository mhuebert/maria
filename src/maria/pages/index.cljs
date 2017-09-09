(ns maria.pages.index
  (:require [re-view.core :as v :refer [defview]]
            [re-db.d :as d]
            [commands.which-key :as which-key]
            [maria.eval :as e]
            [maria.repl-specials]
            [maria.views.cards :as repl-ui]
            [cljs.core.match :refer-macros [match]]
            [maria.views.floating.float-ui :as hint]
            [maria.views.bottom-bar :as dock]
            [maria.pages.docs :as docs]
            [maria.persistence.local :as local]
            [maria.views.top-bar :as toolbar]))

(defonce _
         (e/on-load #(d/transact! [[:db/add :repl/state :eval-log [{:id    (d/unique-id)
                                                                    :value (repl-ui/plain [:span.gray "Ready."])}]]])))

(defn last-n [n v]
  (subvec v (max 0 (- (count v) n))))

(defview home
  [this]
  (let [username (d/get :auth-public :username)]
    [:div
     (toolbar/doc-toolbar {})
     (when username
       (list
         [:.b.sans-serif.mh3.mv2 "My gists"]
         [:.bg-white.ma3
          (docs/doc-list (d/get username :gists))]))
     (when-let [recent-docs (local/local-get (str username "/recent-docs"))]
       [:div
        [:.b.sans-serif.mh3.mv2 "Recent"]
        [:.bg-white.ma3
         (docs/doc-list recent-docs)]])]))

(defview layout
  [{:keys []}]
  [:div
   {:class "bg-light-gray"
    :style {:min-height     "100%"
            :padding-bottom 40}}
   (hint/display-hint)
   [:.relative.border-box.flex.flex-column.w-100
    (when-let [segments (d/get :router/location :segments)]

      (match segments
             ["home"] (home)
             ["new"] (docs/file-edit {:id "new"})
             ["gist" id filename] (docs/file-edit {:id       id
                                                   :filename filename})
             ["gists" username] (docs/gists-list username)))]

   (which-key/show-commands)
   (dock/BottomBar)
   ])