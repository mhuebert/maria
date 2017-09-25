(ns maria.pages.docs
  (:require [re-view.core :as v :refer [defview]]
            [maria.views.icons :as icons]
            [re-db.d :as d]
            [maria.views.top-bar :as toolbar]
            [maria.commands.doc :as doc]
            [maria.util :as util]
            [maria.persistence.local :as local]
            [maria.pages.block_list :as block-list]
            [maria.curriculum :as curriculum]))

(d/merge-schema! {:doc.owner/username {:db/index true}})
(d/transact! curriculum/docs)


(defview file-edit
  {:doc-editor              (fn [{:keys [view/state]}] (:doc-editor @state))
   :init-doc                (fn [this]
                              (doc/locals-push! :local/recents (:id this))
                              (local/init-storage (:id this)))
   :view/will-receive-props (fn [{:keys [id] {prev-id :id} :view/prev-props :as this}]
                              (when-not (= id prev-id)
                                (.initDoc this)))
   :view/will-mount         (fn [this]
                              (.initDoc this))
   :project-files           (fn [{:keys [id]}]
                              (-> (concat (keys (d/get-in id [:persisted :files]))
                                          (keys (d/get-in id [:local :files])))
                                  (distinct)))
   :current-file            (fn [{:keys [filename] :as this}]
                              (or filename (first (.projectFiles this))))}
  [{:keys [view/state local? id] :as this}]
  (let [{:keys [default-value
                loading-message
                error
                persisted-error]
         :as   project} (d/entity id)
        error (or persisted-error error)
        filenames (.projectFiles this)
        filename (.currentFile this)
        owner (if local? {:local-url "/local"
                          :username  "local"}
                         (:owner project))]
    (if loading-message (util/loader loading-message)

                        (let [local-value (get-in project [:local :files filename :content])
                              persisted-value (get-in project [:persisted :files filename :content])]
                          [:.h-100.flex.flex-column
                           (toolbar/doc-toolbar (cond (empty? filenames)
                                                      {:left-content "Empty Gist"}
                                                      error nil
                                                      :else {:project  project
                                                             :owner    owner
                                                             :filename filename
                                                             :id       id}))
                           [:.flex.flex-auto
                            (or (some->> error (conj [:.pa3.dark-red]))
                                (when-let [value (or local-value persisted-value)]
                                  (block-list/BlockList {:ref           #(when % (swap! state assoc :doc-editor %))
                                                         :on-update     (fn [source]
                                                                          (d/transact! [[:db/update-attr (:id this) :local #(assoc-in % [:files (.currentFile this) :content] source)]]))
                                                         :source-id     id
                                                         :class         "flex-auto"
                                                         :value         value
                                                         :default-value default-value})))]]))))

(def small-label :.silver.f7.flex.items-stretch.ph2.w4.tl.flex-none)
(def small-icon-classes " silver hover-black ph2 flex items-center pointer")

(defview doc-list
  {:view/initial-state {:limit-n 8}}
  [{:keys [view/state context]} docs]
  (let [{:keys [limit-n]} @state
        more? (= (count (take (inc limit-n) docs)) (inc limit-n))]
    [:.flex-auto.overflow-auto.sans-serif.f6
     (for [doc (take limit-n docs)
           :let [{:keys [id description persistence/provider local-url filename]} doc
                 trashed? (= context :trash)]
           :when local-url]

       [:.flex.bb.b--near-white.items-stretch
        {:class (when-not trashed? "hover-bg-washed-blue")}
        [:a.db.ph3.pv2.black.no-underline.b.flex-auto
         {:class (if trashed? "o-50" "pointer")
          :href  (when-not trashed? local-url)}
         (doc/strip-clj-ext filename)
         (some->> description (conj [:.gray.f7.mt1.normal]))]

        (case provider
          :maria/local
          [small-label
           (if trashed?
             [:.blue.hover-underline.hover-dark-blue.f7.pointer.flex.items-center
              {:on-click #(do (doc/locals-push! :local/recents id)
                              (doc/locals-remove! :local/trash id))}
              "Restore"]
             [:.flex.items-center "Unsaved"])]
          :gist [small-label [:.flex.items-center "Gist"]
                 [:a
                  {:class        small-icon-classes
                   :href         (str "https://gist.github.com/" id)
                   :data-tooltip (pr-str "View on GitHub")
                   :target       "_blank"}
                  (-> icons/OpenInNew
                      (icons/size 16))]]

          :maria/curriculum
          [small-label [:.flex.items-center "Curriculum"]]
          nil)

        (case context
          :recents
          (when (= context :recents)
            [:div
             {:class        small-icon-classes
              :on-click     #(do (doc/locals-remove! :local/recents id)
                                 (when (= provider :maria/local)
                                   (doc/locals-push! :local/trash id)))
              :data-tooltip (pr-str "Remove")}
             (icons/size icons/X 16)])
          :trash
          [:div
           {:data-tooltip (pr-str "Delete")
            :class        (str small-icon-classes " hover-dark-red")
            :on-click     #(do
                             (doc/locals-remove! :local/trash id)
                             (doc/locals-remove! :local/recents id)
                             (d/transact! [[:db/retract-entity id]]))}
           (icons/size icons/Delete 16)]
          [:.ph2 (icons/size icons/Blank 16)])

        ])
     (when more? [:.pointer.gray.hover-black.ph2.hover-bg-washed-blue.tc
                  {:on-click #(swap! state update :limit-n (partial + 20))}
                  icons/ExpandMore])]))

(defn gists-list
  [username]
  (let [gists (doc/user-gists username)]
    [:.flex-auto.flex.flex-column.relative
     (toolbar/doc-toolbar {:left-content [:.flex.items-center.ph2.gray
                                          [:a.hover-underline.gray.no-underline.flex.items-center {:href (str "/gists/" username)} username]
                                          util/space "/"]})
     [:.ma3.bg-white
      (doc-list nil gists)]]))