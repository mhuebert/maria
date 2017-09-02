(ns maria.views.pages.blocks
  (:require [re-view.core :as v :refer [defview]]
            [re-db.d :as d]
            [maria-commands.which-key :as which-key]
            [maria.blocks.block-list :as block-list]
            [maria.eval :as e]
            [maria.repl-specials]
            [maria.views.cards :as repl-ui]
            [cljs.core.match :refer-macros [match]]
            [maria.views.doc-toolbar :as toolbar]
            [maria.persistence.local :as local]
            [goog.events :as events]
            [re-view-routing.core :as r]
            [maria.views.floating-hint :as hint])
  (:require-macros [maria-commands.registry :refer [defcommand]]))

(defonce _
         (e/on-load #(d/transact! [[:db/add :repl/state :eval-log [{:id    (d/unique-id)
                                                                    :value (repl-ui/plain [:span.gray "Ready."])}]]])))

(defn last-n [n v]
  (subvec v (max 0 (- (count v) n))))


(def result-toolbar
  [:.bt.code.flex.items-center.z-1.flex-none
   {:style {:border-color     "rgba(0,0,0,0.03)"
            :background-color "rgba(0,0,0,0.02)"}}
   [:.flex-auto]
   ;; ...toolbar items
   ])

(defn loader [message]
  [:.w-100.sans-serif.tc
   [:.pa3.gray message]])

(defview gists-list [{:keys [username]}]
  (let [gists (d/get username :gists)]
    [:.flex-auto.flex.flex-column.relative.bg-white
     (toolbar/doc-toolbar {:parent (:owner (first gists))})
     [:.flex-auto.overflow-auto.sans-serif.f6
      (if-let [message (d/get username :loading-message)]
        (loader message)
        (for [{:keys [id description files]} gists
              :let [[filename _] (first files)]]
          [:a.db.ph3.pv2.bb.b--near-white.black.no-underline.b.hover-bg-washed-blue.pointer
           {:href (str "/gist/" id)}
           (toolbar/strip-clj-ext filename)
           (some->> description (conj [:.gray.f7.mt1.normal]))]))]]))

(defview edit-file
  {:doc-editor              (fn [{:keys [view/state]}] (:doc-editor @state))
   :view/will-receive-props (fn [{id :id {prev-id :id} :view/prev-props}]
                              (when-not (= id prev-id)
                                (local/init-storage id)))
   :view/will-mount         (fn [{:keys [id]}]
                              (local/init-storage id))
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
    (cond loading-message (loader loading-message)
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



(defview layout
  [{:keys []}]
  [:div
   {:class "bg-light-gray"
    :style {:min-height "100%"}}
   (hint/display-hint)
   [:.relative.border-box.flex.flex-column.w-100
    (when-let [segments (d/get :router/location :segments)]
      (match segments
             ["new"] (edit-file {:id "new"})
             ["gist" id filename] (edit-file {:id       id
                                              :filename filename})
             ["gists" username] (gists-list {:username username})))]
   (when (d/get :commands :which-key/active?)
     (which-key/show-hints))])

(defcommand :repl/clear
  "Clear the repl output"
  {:bindings ["M1-Shift-B"]}
  []
  (d/transact! [[:db/add :repl/state :cleared-index (count (d/get :repl/state :eval-log))]]))