(ns maria.views.toolbar
  (:require [re-view.core :as v :refer [defview]]
            [maria.commands.registry :refer-macros [defcommand]]
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

(def toolbar-button :.pa2.flex.items-center)
(v/defn toolbar-item [props [action icon]]
  [toolbar-button (merge {:on-click action}
                         (cond-> props
                                 action (update :classes conj "pointer hover-bg-near-white"))) (-> icon
                                                                                                   (icons/size 20)
                                                                                                   (icons/class "gold"))])

(def toolbar-text :.f7.gray.no-underline.pa2.pointer.hover-underline.flex.items-center)

(def current-toolbar nil)

(defn persistence-mode [{:keys [persisted]}]
  (let [owned-by-current-user? (or (nil? persisted)
                                   (= (str (:id (:owner persisted))) (d/get :auth-public :id)))]
    (match [(boolean persisted) owned-by-current-user?]
           [false _] :create
           [true true] :publish
           [true false] :fork)))

(defn unsaved-changes? [{{local-files :files}     :local
                         {persisted-files :files} :persisted
                         :as                      project} filename]
  (let [{local-content  :content
         local-filename :filename
         :as            local-file} (get local-files filename)
        {persisted-content :content} (get persisted-files filename)]
    (and filename
         local-file
         (or (and local-content (not= local-content persisted-content))
             (and local-filename (not= local-filename filename))))))

(defn valid-content? [project filename]
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

(defn persist! [{:keys [local persisted] :as project} filename]
  (when (and (unsaved-changes? project filename)
             (valid-content? project filename))
    (let [persist-mode (persistence-mode project)
          {local-content  :content
           local-filename :filename} (get-in local [:files filename])]
      (send (case persist-mode
              :publish [:project/publish (:id persisted) {:files {filename {:filename (or local-filename filename)
                                                                            :content  (or local-content (get-in persisted [:files filename :content]))}}}]
              :create [:project/create (d/get "new" :local)]
              :fork [:project/fork (:id persisted)])))))

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
            ["Cmd-B"]
            "Create a blank doc"
            (fn [] (some-> current-toolbar (new-file!))))

(defcommand :doc/publish
            ["Cmd-Shift-P"]
            "Publish the current doc"
            (fn [] (when current-toolbar
                     (persist! (:project current-toolbar)
                               (:filename current-toolbar)))))



(defview toolbar
  {:life/did-mount          (fn [this]
                              (.updateWindowTitle this)
                              (set! current-toolbar this))
   :life/will-unmount       #(set! current-toolbar nil)
   :life/will-receive-props (fn [{filename                  :filename
                                  {prev-filename :filename} :view/prev-props
                                  :as                       this}]
                              (when-not (= filename prev-filename)
                                (.updateWindowTitle this)))
   :update-window-title     (fn [{:keys [filename]}]
                              (frame/send frame/trusted-frame [:window/set-title filename]))}
  [{{:keys [persisted local]
     :as   project} :project
    :keys           [filename get-editor id view/state] :as this}]

  (let [signed-in? (d/get :auth-public :signed-in?)
        {parent-url :maria-url parent-username :username} (or (:parent this) (:owner persisted) (d/entity :auth-public))
        persist-mode (persistence-mode project)
        current-filename (or (get-in local [:files filename :filename]) filename)
        {local-content :content} (get-in local [:files filename])
        {persisted-content :content} (get-in persisted [:files filename])
        persisted-id (:id persisted)
        update-filename #(d/transact! [[:db/update-attr id :local (fn [local]
                                                                    (assoc-in local [:files filename] {:filename %
                                                                                                       :content  (or local-content persisted-content)}))]])
        has-unsaved-changes (unsaved-changes? project filename)]
    [:.bb.b--light-gray.flex.sans-serif.f6.items-stretch.flex-none.br.b--light-gray.f7.flex-none.relative
     [:.ph2.flex-auto.flex.items-center
      [:.pl2.flex.items-center
       [:a.hover-underline.gray.no-underline {:href parent-url} parent-username]
       [:.ph1.gray "/"]
       (when filename
         (text/autosize-text {:auto-focus  true
                              :ref         #(when % (swap! state assoc :title-input %))
                              :value       (strip-clj-ext current-filename)
                              :on-key-down #(when (= 13 (.-which %))
                                              (persist! (:project this) (:filename this)))
                              :placeholder "Enter a title..."
                              :on-change   #(update-filename (add-clj-ext (.-value (.-target %))))}))]

      (when (and filename signed-in?)
        (if (= :fork persist-mode)
          (toolbar-item [persist! icons/ContentDuplicate])
          (list
            (if (and (valid-content? project filename)
                     has-unsaved-changes)
              (toolbar-item [persist! icons/Backup])
              (toolbar-item {:class "o-50"} [nil icons/Backup ""])))))
      (when (and persisted has-unsaved-changes)
        (toolbar-item [#(do (d/transact! [[:db/add persisted-id :local persisted]])
                            (.setValueAndRefresh (get-editor) persisted-content)) icons/Undo]))]

     (toolbar-item [#(new-file! this) icons/Add "New namespace"])
     (if signed-in? (user-menu)
                    [toolbar-text {:on-click #(frame/send frame/trusted-frame [:auth/sign-in])} "Sign in with GitHub"])]))