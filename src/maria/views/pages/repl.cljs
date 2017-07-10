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
            [maria.ns-utils :as ns-utils]
            [re-view-material.core :as ui]
            [re-view-material.icons :as icons]
            [maria.views.repl-values :as repl-values]
            [maria.views.repl-utils :as repl-ui]
            [re-view-material.core :as ui]
            [maria.frame-communication :as frame]
            [clojure.string :as string]
            [cljs.core.match :refer-macros [match]]
            [maria.persistence.github :as github]
            [maria.persistence.local :as local]))

(defn strip-clj-ext [s]
  (some-> s
          (string/replace #"\.clj[cs]?$" "")))

(defonce _
         (do
           (add-watch eval/c-env :log-namespace-changes
                      (fn [_ _ {prev-ns :ns} {ns :ns}]
                        (when (not= prev-ns ns)
                          (js/setTimeout #(d/transact! [[:db/update-attr :repl/state :eval-log
                                                         (fnil conj [])
                                                         {:id    (d/unique-id)
                                                          :value (repl-ui/plain [:span.gray "Namespace: "] (str ns))}]]) 0))))

           (set! cljs-live.compiler/debug? true)
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
                              ))))

(defview current-namespace
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

(defn user-menu []
  (ui/SimpleMenuWithTrigger
    {:open-from         :top-left
     :container-classes ["mdc-menu-anchor" "flex" "items-stretch"]
     }
    [:.flex.items-center.b.pointer.ph2.hover-bg-near-white (d/get :auth-public :username)]
    (ui/SimpleMenuItem {:text-primary "Sign out"
                        :on-click     #(frame/send frame/trusted-frame [:auth/sign-out])
                        :dense        true})))

(def toolbar-button :.pa2.flex.items-center)
(v/defn toolbar-item [props [action icon]]
  [toolbar-button (merge {:on-click action}
                         (cond-> props
                                 action (update :classes conj "pointer hover-bg-near-white"))) (icons/size icon 20)])

(def toolbar-text :.f7.gray.no-underline.pa2.pointer.hover-underline.flex.items-center)

(defview toolbar
  [{{:keys [persisted local] :as project} :project
    :keys                                 [filename get-editor id owner]}]

  (let [signed-in? (d/get :auth-public :signed-in?)
        {:keys [maria-url] :as owner} (cond owner owner
                                            persisted (:owner persisted)
                                            :else (d/entity :auth-public))
        owned-by-current-user? (= (str (:id owner)) (d/get :auth-public :id))
        current-filename (or (get-in local [:files filename :filename]) filename)]
    [:.bb.b--light-gray.flex.sans-serif.f6.items-stretch.flex-none.br.b--light-gray.f7.flex-none.relative
     [:.ph2.flex-auto.flex.items-center
      [:.pl2.flex.items-center
       [:a.hover-underline.gray.no-underline {:href maria-url} (:username owner)]
       [:.ph1.gray "/"]
       (when filename
         [:input {:value     (strip-clj-ext current-filename)
                  :on-change #(let [next-value (str (.-value (.-target %)) ".cljs")]
                                (prn :update next-value (d/entity id))
                                (d/transact! [[:db/update-attr id :local (fn [local]
                                                                           (assoc-in local [:files filename :filename] (str (.-value (.-target %)) ".cljs")))]]))}]
         #_(cond->> [:span.b.mr1 (strip-clj-ext filename)]
                    persisted (conj [:a.no-underline.black.hover-underline
                                     {:href   (:html-url persisted)
                                      :target "_blank"}])))]

      (when (and filename signed-in?)
        (let [unsaved-changes? (and filename
                                    (contains? (:files local) filename)
                                    (not= (get-in local [:files filename :content])
                                          (get-in persisted [:files filename :content])))
              save-action (match [(boolean persisted) owned-by-current-user?]
                                 [true true] :publish
                                 [true false] :fork
                                 [false _] :create)
              save-icon (case save-action (:publish :create) (icons/class icons/Backup "gold")
                                          :fork (icons/class icons/ContentDuplicate "gold"))]
          (if-not unsaved-changes?
            (toolbar-item {:class "o-50"} [nil save-icon ""])
            (toolbar-item [#(frame/send frame/trusted-frame (case save-action
                                                              :publish [:project/publish (:id persisted) (d/get (:id persisted) :local)]
                                                              :create [:project/create (d/get "new" :local)]
                                                              :fork [:project/fork (:id persisted)])) save-icon]))))

      (toolbar-item [#(do
                        (github/clear-new!)
                        (some-> get-editor
                                (apply [])
                                :view/state
                                (deref)
                                :editor
                                (.setValue ";; type here"))
                        (frame/send frame/trusted-frame [:window/navigate "/new" {}])) (icons/class icons/Add "gold") "New namespace"])
      ]
     (if signed-in? (user-menu)
                    [toolbar-text {:on-click #(frame/send frame/trusted-frame [:auth/sign-in])} "Sign in with GitHub"])]
    ))

(defn loader [message]
  [:.w-100.sans-serif.tc
   [:.b.progress-indeterminate]
   [:.pa3.gray message]])

(defview gists-list [{:keys [username]}]
  (let [gists (d/get username :gists)]
    [:.flex-auto.flex.flex-column.relative
     (toolbar {:owner (:owner (first gists))})
     [:.flex-auto.overflow-scroll.sans-serif.f6
      (if-let [message (d/get username :loading-message)]
        (loader message)
        (for [{:keys [id description files]} gists
              :let [[filename _] (first files)]]
          [:a.db.ph3.pv2.bb.b--near-white.black.no-underline.b.hover-bg-washed-blue.pointer
           {:href (str "/gist/" id)}
           (strip-clj-ext filename)
           (some->> description (conj [:.gray.f7.mt1.normal]))]))]]))

(defview edit-file
  {:get-editor              (fn [{:keys [view/state]}]
                              (some-> (:repl-editor @state) :view/state deref :editor))
   :life/will-receive-props (fn [{id :id {prev-id :id} :view/prev-props}]
                              (when-not (= id prev-id)
                                (local/init-storage id)))
   :life/will-mount         (fn [{:keys [id]}]
                              (local/init-storage id))
   :project-files           (fn [{:keys [id]}]
                              (-> (set (keys (d/get-in id [:local :files])))
                                  (into (keys (d/get-in id [:persisted :files])))))
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
             (toolbar {:project    project
                       :owner      (:owner project)
                       :filename   filename
                       :id         id
                       :get-editor #(:repl-editor @state)})
             (editor/editor {:ref             #(when % (swap! state assoc :repl-editor %))
                             :class           "flex-auto overflow-scroll"
                             :on-update       #(local/update-local-gist (:id this) (.currentFile this) :content %)
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
                                                  nil))})]))


    #_(do
        (and (> (count filenames) 1)
             (nil? filename))
        [:.sans-serif
         (toolbar project)
         (when title [:.f5.b.pa2.bb.b--near-white title])
         (for [filename filenames]
           [:a.db.pa3.bb.b--near-white.black.no-underline.b.hover-underline
            {:href (str "/gist/" id "/" filename)}
            (strip-clj-ext filename)])]
        )))

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
    (repl-ui/ScrollBottom
      [:.flex-auto.overflow-auto.code
       (if-let [eval-log (d/get :repl/state :eval-log)]
         (map repl-values/display-result (last-n 50 eval-log))
         [:.pa3.gray "Loading..."])])
    result-toolbar]])
