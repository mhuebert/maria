(ns maria.pages.live-layout
  (:require [chia.view :as v]
            [maria.views.floating.tooltip :as tooltip]
            [chia.db :as d]
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
            [maria.util :as util]
            [chia.view.props :as props]))

(defonce _
         (d/transact! [[:db/add :ui/globals :sidebar-width 250]
                       #_[:db/add :remote/status :in-progress true]]))

(defn last-n [n v]
  (subvec v (max 0 (- (count v) n))))

(defn landing []
  [:.w-100
   [toolbar/doc-toolbar {}]
   [:.h2]
   [:.tc.serif.center.ph3
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
                                 :href   "https://github.com/mhuebert/maria/wiki/Curriculum"} "Pedagogy"] " behind Maria's curriculum."]
     [:li "Discover the " [:a {:target "_blank"
                               :href   "https://github.com/mhuebert/maria/wiki/Background-reading"} "Sources of Inspiration"] " for the project."]]]
   ])

(v/defn sidebar
  [{:keys [visible? id]}]
  (let [width (d/get :ui/globals :sidebar-width)]
    [:.fixed.f7.z-5.top-0.bottom-0.flex.flex-column.bg-white.b--moon-gray.bw1.br
     {:style {:width      width
              :transition "all ease 0.2s"
              :left       (if visible? 0 (- width))}}

     [:.flex.items-stretch.pl1.flex-none.bg-darken-lightly
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
      (some->> (doc/locals-docs :local/recents)
               (docs/doc-list {:context :recents
                               :id      id
                               :title   "Recent"}))

      (some->> doc/curriculum
               (docs/doc-list {:context :curriculum
                               :id      id
                               :title   "Learning Modules"
                               :limit   0}))

      (when-let [username (d/get :auth-public :username)]
        (some->> (seq (doc/user-gists username))
                 (docs/doc-list {:context :gists
                                 :id      id
                                 :title   (str username "'s Gists")
                                 :limit   0})))

      (some->> (doc/locals-docs :local/trash)
               (docs/doc-list {:context :trash
                               :id      id
                               :title   "Trash"
                               :limit   0}))]]))

(def routes ["/" {""                           landing
                  "home"                       landing
                  ["doc/" [#".*" :id]]         docs/file-edit
                  ["gists/" [#".*" :username]] docs/gists-list
                  ["local/" [#".*" :id]]       (props/partial docs/file-edit {:local? true})
                  "local"                      (props/partial docs/gists-list {:username "local"})}])

(v/defclass remote-progress []
  (let [active? (> (d/get :remote/status :in-progress) 0)]
    [:div {:style {:height   (if active? 10 0)
                   :left     0
                   :right    0
                   :top      0
                   :position "absolute"}}

     (when active?
       [:.progress-indeterminate])]))

(v/defclass layout
  [{:keys []}]
  (let [sidebar? (d/get :ui/globals :sidebar?)
        path (str "/" (str/join "/" (d/get :router/location :segments)))
        {:keys [route-params handler]} (when (d/contains? :router/location)
                                         (-> (bidi/match-route routes path)
                                             ;; normalize encoding... bidi leaves route params in a partially decoded state
                                             (update :route-params
                                                     (partial util/map-vals
                                                       (comp js/encodeURIComponent js/decodeURIComponent)))))]
    [:.w-100.relative.sans-serif.cursor-text
     {:on-click #(when (= (.-target %) (.-currentTarget %))
                   (exec/exec-command-name :navigate/focus-end))
      :style    {:min-height     "100%"
                 :padding-bottom 140
                 :transition     "padding-left ease 0.2s"
                 :padding-left   (when sidebar? (d/get :ui/globals :sidebar-width))}}
     [remote-progress]
     [hint/show-floating-view]
     [sidebar {:visible? sidebar?
               :id (:id route-params)}]
     [:.relative.w-100
      (when handler
        (handler route-params))]

     [which-key/show-commands]
     [dock/BottomBar {}]
     [tooltip/Tooltip]]))
