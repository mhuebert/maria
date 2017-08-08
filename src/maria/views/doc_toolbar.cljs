(ns maria.views.doc-toolbar
  (:require [re-view.core :as v :refer [defview]]
            [maria-commands.registry :refer-macros [defcommand]]
            [maria-commands.exec :as exec]
            [maria.views.text :as text]
            [maria.frames.communication :as frame]
            [re-view-material.icons :as icons]
            [maria.persistence.github :as github]
            [re-db.d :as d]
            [re-view-material.core :as ui]
            [clojure.string :as string]
            [cljs.core.match :refer-macros [match]]
            [maria.util :as util]))

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
                            :class    (when action "pointer hover-bg-near-white")}
   (-> icon
       (icons/size 20)
       (icons/class "gold"))])

(defn command-button
  ([context command-name icon]
   (command-button context command-name icon nil))
  ([context command-name icon else-icon]
   (if (:exec? (exec/get-command context command-name))
     (toolbar-button [#(exec/exec-command-name command-name) icon])
     (when else-icon
       (toolbar-button [nil else-icon])))))

(def toolbar-text :.f7.gray.no-underline.pa2.pointer.hover-underline.flex.items-center)


(defn unsaved-changes? [{{{local-files :files}     :local
                          {persisted-files :files} :persisted} :project
                         filename                              :filename}]
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

(defn persistence-mode [{{:keys [persisted]} :project :as toolbar}]
  (when (valid-content? toolbar)
    (let [owned-by-current-user? (or (nil? persisted)
                                     (= (str (:id (:owner persisted))) (d/get :auth-public :id)))
          unsaved-changes (unsaved-changes? toolbar)]
      (match [(boolean persisted) owned-by-current-user? unsaved-changes]
             [false _ true] :create
             [true true true] :save
             [true false _] :copy
             :else nil))))

(defn create! [local]
  (send [:project/create local]))

(defn copy! [{{local-version :local} :project
              filename               :filename}]
  (let [{local-filename :filename
         local-content  :content} (or (get-in local-version [:files filename])
                                      filename)
        new-filename (str "Copy of " local-filename)]
    (create! (-> local-version
                 (update :files dissoc filename)
                 (assoc-in [:files new-filename :content] local-content)))))

(defn save! [{filename                                      :filename
              {{persisted-id :id :as persisted} :persisted} :project
              :as                                           toolbar}]
  (let [{local-content  :content
         local-filename :filename} (get-in toolbar [:project :local :files filename])]
    (send [:project/save persisted-id {:files {filename {:filename (or local-filename filename)
                                                         :content  (or local-content (get-in persisted [:files filename :content]))}}}])))

(defn persist! [toolbar]
  (case (persistence-mode toolbar)
    :save (save! toolbar)
    :create (create! (d/get "new" :local))))

(defcommand :doc/new
  "Create a blank doc"
  {
   ;; the ordinary shortcuts for new docs are always captured by browsers.
   ;; hold off and use M-X-style command for this.
   :bindings       ["M1-Shift-B"]
   :intercept-when true
   :when           :current-doc}
  [{{:keys [view/state get-editor id]} :current-doc}]
  (github/clear-new!)
  ;(some-> title-input (.focus))
  (when (and (= id "new") get-editor)
    ;; if we are already in the document with the id "new", clear the editor.
    ;; otherwise, we will get a blank editor by switching ids.
    (some-> (get-editor)
            (.setValue "")))
  (frame/send frame/trusted-frame [:window/navigate "/new" {}]))

(defcommand :doc/save
  "Save the current doc"
  {:bindings       ["M1-S"]
   :intercept-when true
   :when           #(and (:signed-in? %)
                         (#{:create :save} (persistence-mode (:current-doc %))))}
  [{:keys [current-doc]}]
  (persist! current-doc))

(defcommand :doc/save-a-copy
  "Save a new copy of a project"
  {:bindings       ["M1-Shift-S"]
   :intercept-when true
   :when           #(and (:signed-in? %)
                         (get-in % [:current-doc :project :persisted])
                         (valid-content? (:current-doc %)))}
  [{:keys [current-doc]}]
  (copy! current-doc))

(defcommand :doc/revert
  {:when (fn [{:keys [current-doc]}]
           (and (get-in current-doc [:project :persisted])
                (unsaved-changes? current-doc)))}
  [{{{persisted :persisted} :project
     filename               :filename
     get-editor             :get-editor} :current-doc}]
  (d/transact! [[:db/add (:id persisted) :local persisted]])
  (some-> (get-editor)
          (.setValueAndRefresh (get-in persisted [:files filename :content]))))

(defview doc-toolbar
  {:view/did-mount          (fn [this]
                              (.updateWindowTitle this)
                              (exec/set-context! {:current-doc this}))
   :view/will-unmount       #(exec/set-context! {:current-doc nil})
   :view/will-receive-props (fn [{filename                  :filename
                                  {prev-filename :filename} :view/prev-props
                                  :as                       this}]
                              (when-not (= filename prev-filename)
                                (.updateWindowTitle this)))
   :update-window-title     (fn [{:keys [filename]}]
                              (frame/send frame/trusted-frame [:window/set-title (util/some-str filename)]))}
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
    [:.bb.flex.sans-serif.items-stretch.br.b--light-gray.f7.flex-none.b--light-gray
     {:class (when (d/get :feature :in-place-eval) "bg-white")}
     [:.ph2.flex-auto.flex.items-center
      [:a.hover-underline.gray.no-underline.pl2 {:href parent-url} parent-username]
      [:.ph1.gray "/"]
      (when filename
        (text/autosize-text {:auto-focus  true
                             :ref         #(when % (swap! state assoc :title-input %))
                             :value       (strip-clj-ext current-filename)
                             :on-key-down #(when (= 13 (.-which %))
                                             (persist! this))
                             :placeholder "Enter a title..."
                             :on-change   #(update-filename (add-clj-ext (.-value (.-target %))))}))

      (command-button command-context :doc/save icons/Backup (when signed-in? (icons/class icons/Backup "o-50")))
      (command-button command-context :doc/save-a-copy icons/ContentDuplicate)

      (command-button command-context :doc/revert icons/Undo)]
     [:.flex-auto]
     (command-button command-context :doc/new icons/Add)

     (if signed-in? (user-menu)
                    [toolbar-text {:on-click #(frame/send frame/trusted-frame [:auth/sign-in])} "Sign in with GitHub"])]))