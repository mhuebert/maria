(ns maria.pages.live-layout
  (:require [re-view.core :as v :refer [defview]]
            [maria.views.floating.tooltip :as tooltip]
            [re-db.d :as d]
            [maria.commands.which-key :as which-key]
            [maria.repl-specials]
            [cljs.core.match :refer-macros [match]]
            [maria.views.floating.float-ui :as hint]
            [maria.views.bottom-bar :as dock]
            [maria.pages.docs :as docs]

            [maria.views.top-bar :as toolbar]
            [lark.commands.exec :as exec]
            [maria.commands.doc :as doc]
            [maria.views.icons :as icons]
            [bidi.bidi :as bidi]
            [clojure.string :as str]
            [maria.util :as util]))

(d/transact! [[:db/add :ui/globals :sidebar-width 250]])

(defn last-n [n v]
  (subvec v (max 0 (- (count v) n))))

(defn doc-list-section [docs {:keys [title] :as options}]
  (when-let [docs (seq (filter :local-url docs))]
    [:div
     [:.sans-serif.ph3.pt3.pb2.f7.b title]
     (docs/doc-list options docs)]))

(defn landing []
  [:.w-100
   (toolbar/doc-toolbar {})
   [:.tc.serif.center
    {:style {:max-width 600}}
    [:.f1.mb3.pt5 "Welcome to Maria,"]
    [:.f3.mv3 "a coding environment for beginners."]

    [:.flex.items-center.f3.mv4.br3.bg-darken.pa3
     [:.flex-auto]
     "Your journey begins here: "
     [:a.br2.bg-white.shadow-4.pa3.ml3.sans-serif.black.no-underline.f5.b.pointer.hover-underline.hover-shadow-5 {:href "/intro"} "Learn Clojure with Shapes"]
     [:.flex-auto]]

    [:.tc.i.f3.mv3 "More reading:"]

    [:ul.f-body.tl.lh-copy
     [:li "The " [:a {:href "/quickstart"} "Editor Quickstart"] ", if you're already familiar with Clojure."]
     [:li "An " [:a {:href "/gallery?eval=true"} "Example Gallery"] " of user creations."]
     [:li "Understand the " [:a {:target "_blank"
                                 :href   "https://github.com/mhuebert/maria/blob/master/curriculum/pedagogy.md"} "Pedagogy"] " behind Maria's curriculum."]
     [:li "Discover the " [:a {:target "_blank"
                               :href   "https://github.com/mhuebert/maria/wiki/Background-reading"} "Sources of Inspiration"] " for the project."]]]
   ])

(defview sidebar
  [{:keys [visible? id]}]
  (let [width (d/get :ui/globals :sidebar-width)]
    [:.fixed.f7.z-5.top-0.bottom-0.flex.flex-column.bg-white.b--moon-gray.bw1.br
     {:style {:width      width
              :transition "all ease 0.2s"
              :left       (if visible? 0 (- width))}}
     [:.flex.items-stretch.pl2.flex-none
      #_(toolbar/toolbar-button [{:on-click #(d/transact! [[:db/add :ui/globals :sidebar? nil]])}
                                 icons/Docs
                                 nil
                                 "Docs"])

      #_(toolbar/toolbar-button [{:on-click #(exec/exec-command-name :doc/new)}
                                 nil
                                 "New"
                                 "New Doc"])

      (toolbar/toolbar-button [{:href "/"}
                               icons/Home
                               nil
                               "Home"])
      [:.flex-auto]
      (toolbar/toolbar-button [{:on-click #(d/transact! [[:db/add :ui/globals :sidebar? nil]])}
                               (-> icons/ExpandMore
                                   (icons/style {:transform "rotate(90deg)"})
                                   (icons/class "o-60"))
                               nil
                               "Close"])]

     [:.overflow-y-auto.bg-white.pb4
      (some-> (doc/locals-docs :local/recents)
              (doc-list-section {:context :recents
                                 :id      id
                                 :title   "Recent"}))

      (-> doc/curriculum
          (doc-list-section {:context :curriculum
                             :id      id
                             :title   "Learning Modules"}))



      (when-let [username (d/get :auth-public :username)]
        (some-> (seq (doc/user-gists username))
                (doc-list-section {:context :gists
                                   :id      id
                                   :title   "My gists"})))

      (some-> (doc/locals-docs :local/trash)
              (doc-list-section {:context :trash
                                 :id      id
                                 :title   "Trash"
                                 :limit   0}))]]))

(def routes ["/" {""                           landing
                  "home"                       landing
                  ["doc/" [#".*" :id]]         docs/file-edit
                  ["gists/" [#".*" :username]] docs/gists-list
                  ["local/" [#".*" :id]]       (v/partial docs/file-edit {:local? true})
                  "local"                      (v/partial docs/gists-list {:username "local"})}])

(defview layout
  [{:keys []}]
  (let [sidebar? (d/get :ui/globals :sidebar?)
        path (str "/" (str/join "/" (d/get :router/location :segments)))
        {:keys [route-params handler]} (bidi/match-route routes path)

        ;; normalize encoding... bidi leaves route params in a partially decoded state
        route-params (util/map-vals (comp js/encodeURIComponent js/decodeURIComponent) route-params)]
    [:.w-100.relative.sans-serif.cursor-text
     {:on-click #(when (= (.-target %) (.-currentTarget %))
                   (exec/exec-command-name :navigate/focus-end))
      :style    {:min-height     "100%"
                 :padding-bottom 40
                 :transition     "padding-left ease 0.2s"
                 :padding-left   (when sidebar? (d/get :ui/globals :sidebar-width))}}
     (hint/show-floating-view)
     (sidebar {:visible? sidebar?
               :id       (:id route-params)})
     [:.relative.w-100
      (when handler
        (handler route-params))]

     (which-key/show-commands)
     (dock/BottomBar {})
     (tooltip/Tooltip)]))