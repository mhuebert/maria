(ns maria.pages.live-layout
  (:require [re-view.core :as v :refer [defview]]
            [maria.views.floating.tooltip :as tooltip]
            [re-db.d :as d]
            [maria.commands.which-key :as which-key]
            [maria.eval :as e]
            [maria.repl-specials]
            [maria.views.cards :as repl-ui]
            [cljs.core.match :refer-macros [match]]
            [maria.views.floating.float-ui :as hint]
            [maria.views.bottom-bar :as dock]
            [maria.pages.docs :as docs]

            [maria.persistence.local :as local]
            [maria.views.top-bar :as toolbar]
            [maria.curriculum :as curriculum]
            [lark.commands.exec :as exec]
            [maria.commands.doc :as doc]))

(defonce _
         (e/on-load #(d/transact! [[:db/add :repl/state :eval-log [{:id    (d/unique-id)
                                                                    :value (repl-ui/plain [:span.gray "Ready."])}]]])))

(defn last-n [n v]
  (subvec v (max 0 (- (count v) n))))

(defn doc-list-section [docs title]
  [:div
   [:.sans-serif.mh3.mv2 title]
   [:.bg-white.ma3
    (docs/doc-list docs)]])



(defn landing []
  [:div
   (toolbar/doc-toolbar {})
   [:.tc.serif.center
    {:style {:max-width 550}}
    [:.f1.mb3.pt5 "Welcome to Maria,"]
    [:.f3.mv3 "a coding environment for beginners."]

    [:.flex.items-center.f3.mv4.br3.bg-darken.pa3
     [:.flex-auto]
     "Your journey begins here: "
     [:a.br2.bg-white.shadow-4.pa3.ml3.sans-serif.black.no-underline.f5.b.pointer.hover-underline.hover-shadow-5 {:href "/intro"} "Learn Clojure with Shapes"]
     [:.flex-auto]]

    [:.tc.i.f3.mv3 "More reading:"]

    [:ul.f4.tl.lh-copy
     [:li "The " [:a {:href "/quickstart"} "Editor Quickstart"] ", if you're already familiar with Clojure."]
     [:li "An " [:a {:href "/gallery?eval=true"} "Example Gallery"] " of user creations."]
     [:li "Understand the " [:a {:target "_blank"
                                 :href   "https://github.com/mhuebert/maria/blob/master/curriculum/pedagogy.md"} "Pedagogy"] " behind Maria's curriculum."]
     [:li "Discover the " [:a {:target "_blank"
                               :href   "https://github.com/mhuebert/maria/wiki/Background-reading"} "Sources of Inspiration"] " for the project."]]]
   ])

(defview home
  [this]
  [:div
   (toolbar/doc-toolbar {})

   (-> doc/curriculum
       (doc-list-section "Learning Modules"))

   (some-> (doc/locals-docs :local/recents)
           (doc-list-section "Recent"))

   (when-let [username (d/get :auth-public :username)]
     (some-> (seq (doc/user-gists username))
             (doc-list-section "My gists")))

   (some-> (doc/locals-docs :local/trash)
           (doc-list-section "Trash"))

   ])

(defview layout
  [{:keys []}]
  [:.bg-light-gray.overflow-hidden.w-100.relative.sans-serif
   {:on-click #(when (= (.-target %) (.-currentTarget %))
                 (exec/exec-command-name :navigate/focus-end))
    :style    {:min-height     "100%"
               :padding-bottom 40}}
   (hint/show-floating-view)
   [:.relative.border-box.flex.flex-column.w-100
    (when-let [segments (d/get :router/location :segments)]
      (match segments
             [] (if (d/get :auth-public :signed-in?)
                  (home)
                  (landing))
             ["home"] (home)
             ["landing"] (landing)
             ["doc" id] (docs/file-edit {:id id})
             ["gists" username] (docs/gists-list username)

             ["local"] (docs/gists-list "local")
             ["local" id] (docs/file-edit {:id     id
                                           :local? true})))]

   (which-key/show-commands)
   (dock/BottomBar {})
   (tooltip/Tooltip)])