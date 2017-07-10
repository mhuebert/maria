(ns maria.views.pages.repl
  (:require [re-view.core :as v :refer [defview]]
            [re-db.d :as d]
            [magic-tree-codemirror.util :as cm]
            [maria.editor :as editor]
            [maria.eval :as eval]
            [cljs.pprint :refer [pprint]]
            [cljs-live.compiler :as c]
            [maria.repl-specials]
            [magic-tree.core :as tree]
            [maria.views.repl-values :as repl-values]
            [maria.views.repl-utils :as repl-ui]
            [cljs.core.match :refer-macros [match]]
            [maria.views.toolbar :as toolbar]
            [maria.persistence.local :as local]))



(defn init []
  ;(set! cljs-live.compiler/debug? true)
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

(defn eval-editor [cm scope]
  (let [traverse (case scope :top-level tree/top-loc
                             :bracket identity)]
    (when-let [source (or (cm/selection-text cm)
                          (->> cm
                               :magic/cursor
                               :bracket-loc
                               (traverse)
                               (tree/string (:ns @eval/c-env))))]

      (d/transact! [[:db/update-attr :repl/state :eval-log (fnil conj []) (assoc (eval/eval-str source)
                                                                            :id (d/unique-id)
                                                                            :source source)]]))))


(def result-toolbar
  [:.bt.code.flex.items-center.z-1.flex-none
   {:style {:border-color     "rgba(0,0,0,0.03)"
            :background-color "rgba(0,0,0,0.02)"}}
   [:.flex-auto]
   ;; ...toolbar items
   ])


(defn loader [message]
  [:.w-100.sans-serif.tc
   [:.b.progress-indeterminate]
   [:.pa3.gray message]])

(defview gists-list [{:keys [username]}]
  (let [gists (d/get username :gists)]
    [:.flex-auto.flex.flex-column.relative
     (toolbar/toolbar {:owner (:owner (first gists))})
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
   :life/will-receive-props (fn [{id :id {prev-id :id} :view/prev-props}]
                              (when-not (= id prev-id)
                                (local/init-storage id)))
   :life/will-mount         (fn [{:keys [id]}]
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
            [:.flex.flex-column.flex-auto.relative
             (toolbar/toolbar {:project    project
                               :owner      (:owner project)
                               :filename   filename
                               :id         id
                               :get-editor #(.getEditor this)})
             (editor/editor {:ref             #(when % (swap! state assoc :repl-editor %))
                             :class           "flex-auto overflow-auto"
                             :on-update       (fn [source]
                                                (d/transact! [[:db/update-attr (:id this) :local #(assoc-in % [:files (.currentFile this) :content] source)]]))
                             :source-id       id
                             :value           (or local-value persisted-value)
                             :default-value   default-value
                             :event/mousedown #(when (.-metaKey %)
                                                 (.preventDefault %)
                                                 (eval-editor (.getEditor this) :bracket))
                             :event/keydown   (fn [editor e]
                                                (case (.keyName js/CodeMirror e)
                                                  ("Shift-Cmd-Enter"
                                                    "Shift-Ctrl-Enter") (eval-editor editor :top-level)
                                                  ("Cmd-Enter"
                                                    "Ctrl-Enter") (eval-editor editor :bracket)
                                                  nil))})]))))

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
    (repl-ui/ScrollBottom
      [:.flex-auto.overflow-auto.code
       (if-let [eval-log (d/get :repl/state :eval-log)]
         (map repl-values/display-result (last-n 50 eval-log))
         [:.pa3.gray "Loading..."])])
    result-toolbar]])

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