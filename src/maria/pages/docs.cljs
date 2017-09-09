(ns maria.pages.docs
  (:require [re-view.core :as v :refer [defview]]
            [maria.views.icons :as icons]
            [re-db.d :as d]
            [maria.views.top-bar :as toolbar]
            [maria.commands.doc :as doc]
            [maria.util :as util]
            [maria.persistence.local :as local]
            [maria.pages.block_list :as block-list]))

(defn push-recents! [this]
  (let [{:keys [id]} this
        filename (.currentFile this)]
    (when-let [username (and filename (d/get :auth-public :username))]
      (local/local-update! (str username "/recent-docs") #(-> (cons {:id id :filename filename}
                                                                    %)
                                                              (distinct))))))

(defview file-edit
  {:doc-editor              (fn [{:keys [view/state]}] (:doc-editor @state))
   :view/will-receive-props (fn [{:keys [id filename] {prev-id :id} :view/prev-props :as this}]
                              (when-not (= id prev-id)
                                (local/init-storage id)
                                (push-recents! this)
                                ))
   :view/will-mount         (fn [{:keys [id filename] :as this}]
                              (local/init-storage id)
                              (push-recents! this))
   :project-files           (fn [{:keys [id]}]
                              (-> (concat (keys (d/get-in id [:persisted :files])) (keys (d/get-in id [:local :files])))
                                  (distinct)))
   :current-file            (fn [{:keys [filename] :as this}]
                              (or filename (first (.projectFiles this))))}
  [{:keys [view/state id] :as this}]
  (let [{:keys [default-value
                loading-message
                error]
         :as   project} (d/entity id)
        filenames (.projectFiles this)
        filename (.currentFile this)]
    (cond loading-message (util/loader loading-message)
          error [:.pa2.dark-red (str error)]
          (empty? filenames) [:.pa2 "Empty gist."]
          :else
          (let [local-value (get-in project [:local :files filename :content])
                persisted-value (get-in project [:persisted :files filename :content])]
            [:.h-100.flex.flex-column
             (toolbar/doc-toolbar {:project  project
                                   :owner    (:owner project)
                                   :filename filename
                                   :id       id})
             [:.flex.flex-auto
              (block-list/block-list {:ref           #(when % (swap! state assoc :doc-editor %))
                                      :on-update     (fn [source]
                                                       (d/transact! [[:db/update-attr (:id this) :local #(assoc-in % [:files (.currentFile this) :content] source)]]))
                                      :source-id     id
                                      :class         "flex-auto"
                                      :value         (or local-value persisted-value)
                                      :default-value default-value})]]))))

(defview doc-list
  {:view/initial-state {:limit-n 8}}
  [{:keys [view/state]} docs]
  (let [{:keys [limit-n]} @state
        more? (= (count (take (inc limit-n) docs)) (inc limit-n))]
    [:.flex-auto.overflow-auto.sans-serif.f6
     (for [{:keys [id description files filename]} (take limit-n docs)
           :let [filename (or filename (ffirst files))]]
       [:a.db.ph3.pv2.bb.b--near-white.black.no-underline.b.hover-bg-washed-blue.pointer
        {:href (str "/gist/" id)}
        (doc/strip-clj-ext filename)
        (some->> description (conj [:.gray.f7.mt1.normal]))])
     (when more? [:.pointer.gray.hover-black.ph3
                  {:on-click #(swap! state update :limit-n (partial + 20))}
                  icons/ExpandMore])]))

(defn gists-list
  [username]
  (let [gists (d/get username :gists)]
    [:.flex-auto.flex.flex-column.relative.bg-white
     (toolbar/doc-toolbar {:parent (:owner (first gists))})
     (doc-list nil gists)]))