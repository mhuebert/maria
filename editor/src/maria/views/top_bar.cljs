(ns maria.views.top-bar
  (:require [chia.view :as v]
            [chia.view.legacy :as vlegacy]
            [lark.commands.registry :refer-macros [defcommand]]
            [lark.commands.exec :as exec]
            [maria.views.text :as text]
            [maria.frames.frame-communication :as frame]
            [maria.views.icons :as icons]
            [chia.db :as d]
            [maria.commands.doc :as doc]
            [maria.util :as util]
            [lark.commands.registry :as registry]
            [goog.events :as events]
            [goog.functions :as gf]
            [chia.reactive :as r]))

(defn toolbar-icon [icon]
  (icons/size icon 20))

(defn toolbar-button [[action icon text tooltip]]
  [(if (:href action) :a.no-underline :div)
   (cond-> {:class (str "pa2 flex items-center gray "
                        (when action "pointer hover-black"))}
           tooltip (assoc :data-tooltip (pr-str tooltip))
           (fn? action) (assoc :on-click action)
           (map? action) (merge action))
   (some-> icon (toolbar-icon))
   (when (and icon text) util/space)
   (cond->>
    [:.dib.truncate text]
    icon (conj [:.dn.dib-ns]))])

(defn command-button
  [context command-name {:keys [icon else-icon tooltip text]}]
  (let [tooltip (when-not (false? tooltip) (or tooltip (registry/spaced-name (name command-name))))
        {:keys [exec? bindings]} (exec/get-command context command-name)
        key-string (some-> (first bindings)
                           (registry/binding-string->vec)
                           (registry/keyset-string))]
    (if exec?
      (toolbar-button [#(do (util/stop! %)
                            (exec/exec-command-name command-name)) icon text (if key-string
                                                                               [:.items-center tooltip
                                                                                [:.o-80.nowrap key-string]]
                                                                               tooltip)])
      (when else-icon
        (toolbar-button [nil else-icon nil tooltip])))))


(vlegacy/defview fixed-top
  {:view/did-mount (fn [{:keys [view/state]}]
                     (->> (events/listen js/window "scroll"
                                         (gf/throttle (fn [e]
                                                        (swap! state assoc :scrolled? (not= 0 (.-scrollY js/window)))) 300))
                          (r/silently
                           (swap! state assoc :listener-key))))
   :view/will-unmount #(events/unlistenByKey (:listener-key @(:view/state %)))}
  [{:keys [when-scrolled view/state]} child]
  [:.fixed.top-0.right-0.left-0.z-5.transition-all
   (-> (when (:scrolled? @state) when-scrolled)
       (cond-> (d/get :ui/globals :sidebar?)
               (assoc-in [:style :left] (d/get :ui/globals :sidebar-width))))
   child])

(vlegacy/defview doc-toolbar
  {:view/did-mount (fn [this]
                     (.updateWindowTitle this)
                     (some->> (:id this) (doc/locals-push! :local/recents))
                     (exec/set-context! {:current-doc this}))
   :view/will-unmount (fn [this]
                        (when (= (:current-doc @exec/context) this)
                          (exec/set-context! {:current-doc nil})))
   :view/did-update (fn [{filename :filename
                          props :view/props
                          {prev-filename :filename :as prev-props} :view/prev-props
                          :as this}]
                      (when-not (= filename prev-filename)
                        (.updateWindowTitle this))
                      (when (not= props prev-props)
                        (some->> (:id this)
                                 (doc/locals-push! :local/recents))))}
  [{{:keys [persisted local]} :project
    :keys [filename id view/state left-content] :as this}]
  (let [signed-in? (d/get :auth-public :signed-in?)
        {parent-url :local-url parent-username :username} (or (:owner persisted)
                                                              (:owner this))
        current-filename (.getFilename this)
        {local-content :content} (get-in local [:files filename])
        {persisted-content :content} (get-in persisted [:files filename])
        update-filename #(d/transact! [[:db/update-attr id :local (fn [local]
                                                                    (assoc-in local [:files filename] {:filename %
                                                                                                       :content (or local-content persisted-content)}))]])
        command-context (exec/get-context)
        {:keys [persistence/provider remote-url]} persisted
        persistence-mode (doc/persistence-mode this)
        sidebar? (d/get :ui/globals :sidebar?)]
    [fixed-top
     {:when-scrolled {:style {:background-color "#e7e7e7"
                              :border-bottom "2px solid #e2e2e2"}}}
     [:#top-bar.flex.sans-serif.items-stretch.f7.flex-none.overflow-hidden.pl2.mb2
      (when-not sidebar?
        [toolbar-button [{:on-click #(d/transact! [[:db/update-attr :ui/globals :sidebar? (comp not boolean)]])}
                         icons/Docs
                         nil
                         (if sidebar? "Hide Sidebar" "Docs")]])

      (command-button command-context :doc/new {:text "New"
                                                :tooltip false})

      (some->>
       (or left-content
           (when filename
             (list
              [:.ph2.flex.items-center
               (when (and parent-username parent-url)
                 [:a.hover-underline.gray.no-underline.dn.dib-ns {:href parent-url} parent-username])
               [:.ph1.gray.dn.dib-ns "/"]
               (text/autosize-text {:auto-focus true
                                    :class "mr2 half-b sans-serif"
                                    :ref #(when % (swap! state assoc :title-input %))
                                    :value (doc/strip-clj-ext current-filename)
                                    :on-key-down #(cond (and (= 13 (.-which %))
                                                             (not (or (.-metaKey %)
                                                                      (.-ctrlKey %))))
                                                        (doc/persist! this)
                                                        (= 40 (.-which %))
                                                        (exec/exec-command-name :navigate/focus-start)
                                                        :else nil)
                                    :placeholder "Enter a title..."
                                    :on-change #(update-filename (doc/add-clj-ext (.-value (.-target %))))})]
              )))
       (conj [:.flex.items-stretch.bg-darken-lightly]))

      (case persistence-mode
        (:save :create)
        (command-button command-context :doc/save {:text "Save"
                                                   :tooltip (str (if persisted false
                                                                               "Publish as new Gist"))})
        :saved
        (toolbar-button [nil nil [:span.o-60 "Saved"] nil])
        nil)
      (command-button command-context :doc/revert-to-saved-version {:text "Revert"})

      (command-button command-context :doc/duplicate {:text "Duplicate"
                                                      :tooltip false})

      (when (= provider :gist)
        (toolbar-button [{:href remote-url
                          :target "_blank"} icons/OpenInNew nil "View on GitHub"]))

      [:.flex-auto]

      (command-button command-context :commands/command-search {:text "Commands..."})

      (toolbar-button [{:href "https://www.github.com/mhuebert/maria/issues"
                        :tab-index -1
                        :target "_blank"} nil "Bug Report"])
      (if (d/get :auth-public :signed-in?)
        (toolbar-button [#(doc/send [:auth/sign-out]) icons/SignOut nil "Sign out"])
        (toolbar-button [#(frame/send frame/trusted-frame [:auth/sign-in]) nil "Sign in with GitHub"]))
      [:.ph1]]]))

(vlegacy/extend-view doc-toolbar
  Object
  (getFilename [{:keys [filename] :as this}]
    (get-in this [:project :local :files filename :filename] filename))
  (updateWindowTitle [{:keys [view/state] :as this}]
    (let [filename (.getFilename this)]

      #_(when (= filename "Untitled.cljs")
          (js/setTimeout #(some-> (:title-input @state)
                                  :view/state
                                  (deref)
                                  :input-element
                                  (.select)) 50))
      (frame/send frame/trusted-frame [:window/set-title (util/some-str filename)]))))

;; (str "/gists/" (d/get :auth-public :username))