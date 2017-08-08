(ns maria.views.pages.repl
  (:require [re-view.core :as v :refer [defview]]
            [re-db.d :as d]
            [maria-commands.which-key :as which-key]
            [maria.views.codemirror :as codemirror]
            [maria.cells.list :as cell-list]
            [maria.eval :as e]
            [maria.repl-specials]
            [maria.views.repl-values :as repl-values]
            [maria.views.repl-ui :as repl-ui]
            [cljs.core.match :refer-macros [match]]
            [maria.views.doc-toolbar :as toolbar]
            [maria.persistence.local :as local]
            [maria-commands.exec :as exec])
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
  {:get-editor              (fn [{:keys [view/state]}]
                              (-> (:repl-editor @state) :view/state deref :editor))
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
        filename (.currentFile this)
        in-place-eval? (d/get :feature :in-place-eval)]
    (cond loading-message (loader loading-message)
          error [:.pa2.dark-red (str error)]
          (empty? filenames) [:.pa2 "Empty gist."]
          :else
          (let [local-value (get-in project [:local :files filename :content])
                persisted-value (get-in project [:persisted :files filename :content])]
            [:.h-100.flex.flex-column
             (toolbar/doc-toolbar {:project    project
                                   :owner      (:owner project)
                                   :filename   filename
                                   :id         id
                                   :get-editor #(.getEditor this)})
             [:.flex.flex-auto
              ((if in-place-eval?
                 cell-list/cell-list
                 codemirror/editor) {:ref           #(when % (swap! state assoc :repl-editor %))
                                     :auto-focus    (not in-place-eval?)
                                     ;; no longer attempting to keep old cell view working
                                     ;:capture-event/focus #(exec/set-context! {:cell/code {:editor (.getEditor %2)}})
                                     ;:capture-event/blur  #(exec/set-context! {:cell/code nil})
                                     :on-update     (fn [source]
                                                      (d/transact! [[:db/update-attr (:id this) :local #(assoc-in % [:files (.currentFile this) :content] source)]]))
                                     :source-id     id
                                     :class         "flex-auto"
                                     :value         (or local-value persisted-value)
                                     :default-value default-value})]]))))



(defview layout
  [{:keys [window-id]}]
  (let [in-place-eval (d/get :feature :in-place-eval)]
    [:div
     {:class (if in-place-eval "bg-light-gray"
                               "flex items-stretch h-100")
      :style {:min-height "100%"}}
     [:.relative.border-box.flex.flex-column
      {:class (if in-place-eval "w-100" "w-50 cm-ph3 cm-h-100")}
      (when-let [segments (d/get :router/location :segments)]
        (match segments
               ["new"] (edit-file {:id "new"})
               ["gist" id filename] (edit-file {:id       id
                                                :filename filename})
               ["gists" username] (gists-list {:username username})))]
     (when (d/get :commands :which-key/active?)
       (which-key/show-hints))
     (when-not in-place-eval
       [:.w-50.h-100.bg-near-white.relative.flex.flex-column.bl.b--light-gray
        {:style {:box-shadow "-1px -1px 0 0 #eee"}}


        (repl-ui/ScrollBottom
          [:.flex-auto.overflow-auto.code
           (if-let [eval-log (d/get :repl/state :eval-log)]
             (->> (map (v/partial repl-values/display-result {:show-source? true})
                       (->> (subvec eval-log (d/get :repl/state :cleared-index 0))
                            (last-n 50)))
                  (interpose [:.bt.b--darken]))
             [:.pa3.gray "Loading..."])])
        result-toolbar])]))

(defcommand :repl/clear
  "Clear the repl output"
  {:bindings ["M1-Shift-B"]}
  []
  (d/transact! [[:db/add :repl/state :cleared-index (count (d/get :repl/state :eval-log))]]))