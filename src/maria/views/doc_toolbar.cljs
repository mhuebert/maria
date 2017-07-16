(ns maria.views.doc-toolbar
  (:require [re-view.core :as v :refer [defview]]
            [maria.commands.registry :refer-macros [defcommand]]
            [maria.commands.exec :as exec]
            [maria.views.text :as text]
            [maria.frames.communication :as frame]
            [re-view-material.icons :as icons]
            [maria.persistence.github :as github]
            [re-db.d :as d]
            [re-view-material.core :as ui]
            [clojure.string :as string]
            [cljs.core.match :refer-macros [match]]))

(def send (partial frame/send frame/trusted-frame))

(defn add-clj-ext [s]
  (when s
    (cond-> s
            (not (re-find #"\.clj[cs]?$" s)) (str ".cljs"))))

(defn strip-clj-ext [s]
  (some-> s
          (string/replace #"\.clj[cs]?$" "")))

(defn user-menu []
  (ui/SimpleMenuWithTrigger
    {:open-from         :top-left
     :container-classes ["mdc-menu-anchor" "flex" "items-stretch"]}
    [:.flex.items-center.b.pointer.ph2.hover-bg-near-white (d/get :auth-public :username)]
    (ui/SimpleMenuItem {:text-primary "My gists"
                        :href         (str "/gists/" (d/get :auth-public :username))
                        :dense        true})
    (ui/SimpleMenuItem {:text-primary "Sign out"
                        :on-click     #(send [:auth/sign-out])
                        :dense        true})))


(defn toolbar-button [[action icon]]
  [:.pa2.flex.items-center {:on-click action
                            :class    "pointer hover-bg-near-white"} (-> icon
                                                                         (icons/size 20)
                                                                         (icons/class "gold"))])

(defn command-button
  ([context command-name icon]
   (command-button context command-name icon nil))
  ([context command-name icon else-icon]
   (if (exec/some-command context command-name)
     (toolbar-button [#(exec/exec-command command-name) icon])
     (when else-icon
       (toolbar-button [#() else-icon])))))

(def toolbar-text :.f7.gray.no-underline.pa2.pointer.hover-underline.flex.items-center)

(defn persistence-mode [{{:keys [persisted]} :project}]
  (let [owned-by-current-user? (or (nil? persisted)
                                   (= (str (:id (:owner persisted))) (d/get :auth-public :id)))]
    (match [(boolean persisted) owned-by-current-user?]
           [false _] :create
           [true true] :publish
           [true false] :fork)))

(defn unsaved-changes? [{{{local-files :files}     :local
                          {persisted-files :files} :persisted
                          :as                      project} :project
                         filename                           :filename}]
  (let [{local-content  :content
         local-filename :filename
         :as            local-file} (get local-files filename)
        {persisted-content :content} (get persisted-files filename)]
    (and filename
         local-file
         (or (and local-content (not= local-content persisted-content))
             (and local-filename (not= local-filename filename))))))

(defn valid-content? [{:keys [project filename]}]
  (let [{local-content  :content
         local-filename :filename} (get-in project [:local :files filename])
        persisted-content (get-in project [:persisted :files filename :content])
        content-to-persist (or local-content persisted-content)
        filename-to-persist (some-> (or local-filename filename)
                                    (strip-clj-ext))]
    (and (not (empty? content-to-persist))
         (not (re-find #"^\s*$" content-to-persist))
         (not (empty? filename-to-persist))
         (not (re-find #"^\s*$" filename-to-persist)))))

(defn fork! [{:keys [project]}]
  (let [persisted-id (get-in project [:persisted :id])]
    (send [:project/fork persisted-id])))

(defn create! []
  (send [:project/create (d/get "new" :local)]))

(defn publish! [{{:keys [local persisted] :as project} :project
                 filename                              :filename
                 :as                                   toolbar}]
  (when (and (unsaved-changes? toolbar)
             (valid-content? toolbar))
    (let [persist-mode (persistence-mode toolbar)
          {local-content  :content
           local-filename :filename} (get-in local [:files filename])]
      (case persist-mode
        :publish (send [:project/publish (:id persisted) {:files {filename {:filename (or local-filename filename)
                                                                            :content  (or local-content (get-in persisted [:files filename :content]))}}}])
        :create (create!)
        :fork (fork! toolbar)))))

(defn new-file! [{:keys [view/state get-editor id] :as toolbar}]
  (let [{:keys [title-input]} @state]
    (github/clear-new!)
    ;(some-> title-input (.focus))
    (when (and (= id "new") get-editor)
      ;; if we are already in the document with the id "new", clear the editor.
      ;; otherwise, we will get a blank editor by switching ids.
      (some-> (get-editor)
              (.setValue "")))
    (frame/send frame/trusted-frame [:window/navigate "/new" {}])))

(defcommand :doc/new
  "Create a blank doc"
  {:bindings ["Command-B"]
   :when     :doc-toolbar}
  [{:keys [doc-toolbar]}]
  (new-file! doc-toolbar))

(defcommand :doc/publish
  "Publish the current doc"
  {:bindings ["Command-Shift-P"]
   :when     #(and (:signed-in? %)
                   (let [doc-toolbar (:doc-toolbar %)]
                     (and (#{:create :publish} (persistence-mode doc-toolbar))
                          (unsaved-changes? doc-toolbar)
                          (valid-content? doc-toolbar))))}
  [{:keys [doc-toolbar]}]
  (publish! doc-toolbar))

(defcommand :doc/fork
  "Make your own copy of another user's project"
  {:bindings ["Command-Shift-P"]
   :when     #(and (:signed-in? %)
                   (= :fork (persistence-mode (:doc-toolbar %))))}
  [{:keys [doc-toolbar]}]
  (fork! doc-toolbar))

(defcommand :doc/revert
  {:when (fn [{:keys [doc-toolbar]}]
           (and (get-in doc-toolbar [:project :persisted])
                (unsaved-changes? doc-toolbar)))}
  [{{{persisted :persisted} :project
     filename               :filename
     get-editor             :get-editor} :doc-toolbar}]
  (d/transact! [[:db/add (:id persisted) :local persisted]])
  (some-> (get-editor)
          (.setValueAndRefresh (get-in persisted [:files filename :content]))))

(defview doc-toolbar
  {:view/did-mount          (fn [this]
                              (.updateWindowTitle this)
                              (set! exec/current-doc-toolbar this))
   :view/will-unmount       #(set! exec/current-doc-toolbar nil)
   :view/will-receive-props (fn [{filename                  :filename
                                  {prev-filename :filename} :view/prev-props
                                  :as                       this}]
                              (when-not (= filename prev-filename)
                                (.updateWindowTitle this)))
   :update-window-title     (fn [{:keys [filename]}]
                              (frame/send frame/trusted-frame [:window/set-title filename]))}
  [{{:keys [persisted local]} :project
    :keys                     [filename id view/state] :as this}]

  (let [signed-in? (d/get :auth-public :signed-in?)
        {parent-url :maria-url parent-username :username} (or (:parent this) (:owner persisted) (d/entity :auth-public))
        current-filename (or (get-in local [:files filename :filename]) filename)
        {local-content :content} (get-in local [:files filename])
        {persisted-content :content} (get-in persisted [:files filename])
        update-filename #(d/transact! [[:db/update-attr id :local (fn [local]
                                                                    (assoc-in local [:files filename] {:filename %
                                                                                                       :content  (or local-content persisted-content)}))]])
        command-context (exec/get-context)]
    [:.bb.b--light-gray.flex.sans-serif.f6.items-stretch.br.b--light-gray.f7.flex-none
     [:.ph2.flex-auto.flex.items-center

      [:a.hover-underline.gray.no-underline.pl2 {:href parent-url} parent-username]
      [:.ph1.gray "/"]
      (when filename
        (text/autosize-text {:auto-focus  true
                             :ref         #(when % (swap! state assoc :title-input %))
                             :value       (strip-clj-ext current-filename)
                             :on-key-down #(when (= 13 (.-which %))
                                             (publish! this))
                             :placeholder "Enter a title..."
                             :on-change   #(update-filename (add-clj-ext (.-value (.-target %))))}))

      (or (command-button command-context :doc/fork icons/ContentDuplicate)
          (command-button command-context :doc/publish icons/Backup (when signed-in? (icons/class icons/Backup "o-50"))))
      (command-button command-context :doc/revert icons/Undo)]
     [:.flex-auto]
     (command-button command-context :doc/new icons/Add)

     (if signed-in? (user-menu)
                    [toolbar-text {:on-click #(frame/send frame/trusted-frame [:auth/sign-in])} "Sign in with GitHub"])]))