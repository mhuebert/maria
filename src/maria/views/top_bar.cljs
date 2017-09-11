(ns maria.views.top-bar
  (:require [re-view.core :as v :refer [defview]]
            [commands.registry :refer-macros [defcommand]]
            [commands.exec :as exec]
            [maria.views.text :as text]
            [maria.frames.frame-communication :as frame]
            [maria.views.icons :as icons]
            [re-db.d :as d]
            [maria.commands.doc :as doc]
            [maria.util :as util]
            [commands.registry :as registry]
            [maria.persistence.local :as local]))

(defn toolbar-icon [icon]
  (icons/size icon 20))

(defn toolbar-button [[action icon text title]]
  [(if (:href action) :a :div)
   (cond-> {:class (str "pa2 flex items-center gray "
                        (when action "pointer hover-black hover-bg-near-white"))}
           title (assoc :data-tooltip title)
           (fn? action) (assoc :on-click action)
           (map? action) (merge action))
   (some-> icon (toolbar-icon))
   text])

(defn command-button
  ([context command-name icon]
   (command-button context command-name icon nil))
  ([context command-name icon else-icon]
   (let [tooltip (registry/spaced-name (name command-name))]
     (if (:exec? (exec/get-command context command-name))
       (toolbar-button [#(exec/exec-command-name command-name) icon nil tooltip])
       (when else-icon
         (toolbar-button [nil else-icon nil tooltip]))))))

(defn push-recents! [id filename]
  (when (and filename (not= "new" filename))
    (when-let [username (and filename (d/get :auth-public :username))]
      (local/local-update! (str username "/recent-docs") #(->> (remove (comp (partial = id) :id) %)
                                                               (cons {:id id :filename filename}))))))

(defview doc-toolbar
  {:view/did-mount          (fn [this]
                              (.updateWindowTitle this)
                              (exec/set-context! {:current-doc this})
                              (push-recents! (:id this) (:filename this)))
   :view/will-unmount       #(exec/set-context! {:current-doc nil})
   :view/will-receive-props (fn [{filename                                 :filename
                                  props                                    :view/props
                                  {prev-filename :filename :as prev-props} :view/prev-props
                                  :as                                      this}]
                              (when-not (= filename prev-filename)
                                (.updateWindowTitle this))
                              (when (not= props prev-props)
                                (push-recents! (:id this) filename)))
   :update-window-title     (fn [{:keys [filename]}]
                              (frame/send frame/trusted-frame [:window/set-title (util/some-str filename)]))}
  [{{:keys [persisted local]} :project
    :keys                     [filename id view/state left-content] :as this}]
  (let [signed-in? (d/get :auth-public :signed-in?)
        {parent-url :maria-url parent-username :username} (or (:parent this) (:owner persisted) (d/entity :auth-public))
        current-filename (or (get-in local [:files filename :filename]) filename)
        {local-content :content} (get-in local [:files filename])
        {persisted-content :content} (get-in persisted [:files filename])
        update-filename #(d/transact! [[:db/update-attr id :local (fn [local]
                                                                    (assoc-in local [:files filename] {:filename %
                                                                                                       :content  (or local-content persisted-content)}))]])
        command-context (exec/get-context)]
    [:.bb.flex.sans-serif.items-stretch.br.b--light-gray.f7.flex-none.b--light-gray
     {:class (when (d/get :feature :in-place-eval) "bg-white")}
     [:.ph2.flex-auto.flex.items-center
      (toolbar-button [{:href "/home"} icons/Home nil "Home"])

      (command-button command-context :doc/new icons/Add)

      (or left-content
          (when filename
            [:.flex.items-center.bg-darken-lightly

             [:a.hover-underline.gray.no-underline.pl2 {:href parent-url} parent-username]
             [:.ph1.gray "/"]
             (text/autosize-text {:auto-focus  true
                                  :class       "mr2"
                                  :ref         #(when % (swap! state assoc :title-input %))
                                  :value       (doc/strip-clj-ext current-filename)
                                  :on-key-down #(cond (and (= 13 (.-which %))
                                                           (not (or (.-metaKey %)
                                                                    (.-ctrlKey %))))
                                                      (doc/persist! this)
                                                      (= 40 (.-which %))
                                                      (exec/exec-command-name :navigate/focus-start)
                                                      :else nil)
                                  :placeholder "Enter a title..."
                                  :on-change   #(update-filename (doc/add-clj-ext (.-value (.-target %))))})

             (command-button command-context :doc/save icons/Backup (when signed-in? (icons/class icons/Backup "o-30")))
             (command-button command-context :doc/save-a-copy icons/ContentDuplicate)

             (command-button command-context :doc/revert icons/Undo)]))]
     [:.flex-auto]
     (toolbar-button [{:on-click #(do (util/stop! %)
                                      (exec/exec-command-name :commands/search command-context))} icons/Help nil "Command Search"])

     (toolbar-button [{:href   "https://www.github.com/mhuebert/maria/issues"
                       :target "_blank"} icons/Bug nil "Report a Bug"])
     (if signed-in? (toolbar-button [#(doc/send [:auth/sign-out]) nil "Sign out"])
                    (toolbar-button [#(frame/send frame/trusted-frame [:auth/sign-in]) nil "Sign in with GitHub"]))
     [:.ph1]]))

;; (str "/gists/" (d/get :auth-public :username))