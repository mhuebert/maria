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

(d/transact! [[:db/add "modules" :gists curriculum/as-gists]])



(defview file-edit
  {:doc-editor              (fn [{:keys [view/state]}] (:doc-editor @state))
   :view/will-receive-props (fn [{:keys [id filename] {prev-id :id} :view/prev-props :as this}]
                              (when-not (= id prev-id)
                                (local/init-storage id)))
   :view/will-mount         (fn [{:keys [id filename] :as this}]
                              (local/init-storage id))
   :project-files           (fn [{:keys [id]}]
                              (-> (concat (keys (d/get-in id [:persisted :files])) (keys (d/get-in id [:local :files])))
                                  (distinct)))
   :current-file            (fn [{:keys [filename] :as this}]
                              (or filename (first (.projectFiles this))))}
  [{:keys [view/state id] :as this}]
  (let [{:keys [default-value
                loading-message
                error
                persisted-error]
         :as   project} (d/entity id)
        error (or persisted-error error)
        filenames (.projectFiles this)
        filename (.currentFile this)]
    (if loading-message (util/loader loading-message)

                        (let [local-value (get-in project [:local :files filename :content])
                              persisted-value (get-in project [:persisted :files filename :content])]
                          [:.h-100.flex.flex-column
                           (toolbar/doc-toolbar (cond (empty? filenames)
                                                      {:left-content "Empty Gist"}
                                                      error nil
                                                      :else {:project  project
                                                             :owner    (:owner project)
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

(defview doc-list
  {:view/initial-state {:limit-n 8}}
  [{:keys [view/state]} docs]
  (let [{:keys [limit-n]} @state
        more? (= (count (take (inc limit-n) docs)) (inc limit-n))]
    [:.flex-auto.overflow-auto.sans-serif.f6
     (for [doc (take limit-n docs)
           :let [{:keys [id description url filename]} (doc/normalize-doc doc)]]
       [:a.db.ph3.pv2.bb.b--near-white.black.no-underline.b.hover-bg-washed-blue.pointer
        {:href url}
        (doc/strip-clj-ext filename)
        (some->> description (conj [:.gray.f7.mt1.normal]))])
     (when more? [:.pointer.gray.hover-black.ph3
                  {:on-click #(swap! state update :limit-n (partial + 20))}
                  icons/ExpandMore])]))

(defn gists-list
  [username]
  (let [gists (d/get username :gists)]
    [:.flex-auto.flex.flex-column.relative
     (toolbar/doc-toolbar {:left-content [:.flex.items-center.ph2.gray
                                          [:a.hover-underline.gray.no-underline.flex.items-center {:href (str "/gists/" username)} username]
                                          util/space "/"]})
     [:.ma3.bg-white
      (doc-list nil gists)]]))