(ns maria.views.pages.repl
  (:require [re-view.core :as v :refer [defview]]
            [re-db.d :as d]
            [maria.commands.which-key :as which-key]
            [maria.editor :as editor]
            [maria.eval :as eval]
            [cljs.pprint :refer [pprint]]
            [cljs-live.compiler :as c]
            [maria.repl-specials]
            [maria.views.repl-values :as repl-values]
            [maria.views.repl-utils :as repl-ui]
            [cljs.core.match :refer-macros [match]]
            [maria.views.doc-toolbar :as toolbar]
            [maria.persistence.local :as local])
  (:require-macros [maria.commands.registry :refer [defcommand]]))

(defn init []
  #_(set! cljs-live.compiler/debug? true)
  (c/load-bundles! ["/js/cljs_bundles/cljs.core.json"
                    "/js/cljs_bundles/maria.user.json"
                    "/js/cljs_bundles/cljs.spec.alpha.json"]
                   (fn []
                     (eval/eval '(require '[cljs.core :include-macros true]))
                     (eval/eval '(require '[maria.user :include-macros true]))
                     (eval/eval '(inject 'cljs.core '{what-is   maria.messages/what-is
                                                      load-gist maria.user.loaders/load-gist
                                                      load-js   maria.user.loaders/load-js
                                                      load-npm  maria.user.loaders/load-npm
                                                      html      re-view-hiccup.core/element}))
                     (eval/eval '(in-ns maria.user))

                     (add-watch eval/c-env :log-namespace-changes
                                (fn [_ _ {prev-ns :ns} {ns :ns}]
                                  (when (not= prev-ns ns)
                                    (js/setTimeout #(d/transact! [[:db/update-attr :repl/state :eval-log
                                                                   conj
                                                                   {:id    (d/unique-id)
                                                                    :value (repl-ui/plain [:span.gray "Namespace: "] (str ns))}]]) 0))))
                     (d/transact! [[:db/add :repl/state :eval-log [{:id    (d/unique-id)
                                                                    :value (repl-ui/plain [:span.gray "Ready."])}]]]))))


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
    [:.flex-auto.flex.flex-column.relative
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
        filename (.currentFile this)]
    (cond loading-message (loader loading-message)
          error [:.pa2.dark-red (str error)]
          (empty? filenames) [:.pa2 "Empty gist."]
          :else
          (let [local-value (get-in project [:local :files filename :content])
                persisted-value (get-in project [:persisted :files filename :content])]
            [:.h-100.flex.flex-column
             (toolbar/doc-toolbar {:project project
                               :owner       (:owner project)
                               :filename    filename
                               :id          id
                               :get-editor  #(.getEditor this)})
             [:.flex.flex-auto
              (editor/editor {:ref           #(when % (swap! state assoc :repl-editor %))

                              :on-update     (fn [source]
                                               (d/transact! [[:db/update-attr (:id this) :local #(assoc-in % [:files (.currentFile this) :content] source)]]))
                              :source-id     id
                              :value         (or local-value persisted-value)
                              :default-value default-value})]]))))



(defview layout
  [{:keys [window-id]}]
  [:.h-100.flex.items-stretch
   [:.w-50.relative.border-box.flex.flex-column
    (when-let [segments (d/get :layout window-id)]
      (match segments
             ["new"] (edit-file {:id "new"})
             ["gist" id filename] (edit-file {:id       id
                                              :filename filename})
             ["gists" username] (gists-list {:username username})))
    ]
   [:.w-50.h-100.bg-near-white.relative.flex.flex-column.bl.b--light-gray
    {:style {:box-shadow "-1px -1px 0 0 #eee"}}

    (when (d/get :commands :which-key/active?)
      (which-key/show-hints))

    (repl-ui/ScrollBottom
      [:.flex-auto.overflow-auto.code
       (if-let [eval-log (d/get :repl/state :eval-log)]
         (map repl-values/display-result (->> (subvec eval-log (d/get :repl/state :cleared-index 0))
                                              (last-n 50)))
         [:.pa3.gray "Loading..."])])
    result-toolbar]])

(defcommand :repl/clear
            "Clear the repl output"
            {:bindings ["Cmd-Shift-B"]}
            []
            (d/transact! [[:db/add :repl/state :cleared-index (count (d/get :repl/state :eval-log))]]))

#_(defview current-namespace
    {:view/spec {:props {:ns symbol?}}}
    [{:keys [ns]}]
    [:.dib
     (ui/SimpleMenuWithTrigger
       (ui/Button {:label   (str ns)
                   :compact true
                   :dense   true
                   :style   {:margin-left "-0.25rem"}})
       (map (fn [item-ns] (ui/SimpleMenuItem {:text-primary (str item-ns)
                                              :ripple       false
                                              :style        (when (= item-ns ns)
                                                              {:background-color "rgba(0,0,0,0.05)"})
                                              :on-click     #(eval/eval-str (str `(~'in-ns ~item-ns)))})) (-> (cons ns (ns-utils/user-namespaces @eval/c-state))
                                                                                                              (distinct))))
     (when-let [ns-doc (:doc (ns-utils/analyzer-ns @eval/c-state ns))]
       [:span.pl2.f7.o-50 ns-doc])])