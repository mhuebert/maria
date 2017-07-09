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
            [maria.persistence.github :as github]))

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
    {:open-from :top-left}
    [:.flex.items-center.b.h-100.pointer.ph2.hover-bg-near-white (d/get :auth-public :display-name)]
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
  [{{{:keys [html-url id]} :persisted
     :keys                 [local persisted]
     :as                   project} :project
    :keys                           [filename owner]}]

  (let [owner (or owner (get-in project [:persisted :owner]))
        owned-by-user? (= (str (:id owner))
                          (str (d/get :auth-public :id)))
        {:keys [signed-in?]} (d/entity :auth-public)]
    [:.bb.b--light-gray.flex.items-center.sans-serif.f6.items-stretch.flex-none.br.b--light-gray.f7.flex-none
     [:.ph2.flex-auto.flex.items-center
      [:.pl2.flex.items-center
       [:a.hover-underline.gray.no-underline
        {:href (:index-url owner)} (:username owner)]
       [:.ph1.gray "/"]
       (when filename
         [:a.no-underline.black.hover-underline.b.mr1
          {:href   html-url
           :target "_blank"}
          (strip-clj-ext filename)])]

      (comment
        (toolbar-item [#(prn "New") (icons/class icons/Add "gold") "New namespace"]))
      (when signed-in?
        (if owned-by-user?
          (let [unsaved-changes? (and filename
                                      (contains? (:files local) filename)
                                      (not= (get-in local [:files filename :content])
                                            (get-in persisted [:files filename :content])))]
            (if unsaved-changes?
              (toolbar-item {:class (when-not unsaved-changes? "o-50")}
                            [#(frame/send frame/trusted-frame [:project/publish id]) (icons/class icons/Backup "gold") "Save"])
              (toolbar-item {:class "o-50"}
                            [nil (icons/class icons/Backup "gold") ""])))
          (toolbar-item [#(frame/send frame/trusted-frame [:project/publish id]) (icons/class icons/Fork "gold")])))
      ]
     (if signed-in? (user-menu)
                    [toolbar-text {:on-click #(frame/send frame/trusted-frame [:auth/sign-in])} "Sign in"])]
    ))

(defn loader [message]
  [:.w-100.sans-serif.tc
   [:.b.progress-indeterminate]
   [:.pa3.gray message]])

(defview list-gists [{:keys [username]}]
  (let [gists (d/get username :gists)]
    [:.flex-auto.flex.flex-column
     (toolbar {:project (first gists)
               :owner   (:owner (first gists))})
     (if-let [message (d/get username :loading-message)]
       (loader message)
       [:.flex-auto.overflow-scroll.sans-serif.f6
        (for [{:keys [id title files]} gists
              :let [[_ {:keys [filename]}] (first files)]]
          [:a.db.ph3.pv2.bb.b--near-white.black.no-underline.b.hover-bg-washed-blue.pointer
           {:href (str "/gist/" id "/" filename)}
           (strip-clj-ext filename)
           (some->> title (conj [:.gray.f7.mt1.normal]))])])]))

(defview edit-file
  {:get-editor    (fn [{:keys [view/state]}]
                    (some-> (:repl-editor @state) :view/state deref :editor))
   :project-files (fn [{:keys [id]}]
                    (-> (set (keys (d/get-in id [:local :files])))
                        (into (keys (d/get-in id [:persisted :files])))))
   :current-file  (fn [{:keys [filename] :as this}]
                    (or filename (first (.projectFiles this))))
   }
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
            [:.flex.flex-column.flex-auto
             (toolbar {:project  project
                       :owner    (:owner project)
                       :filename filename})
             (editor/editor {:ref             #(when % (swap! state assoc :repl-editor %))
                             :class           "flex-auto overflow-scroll"
                             :on-update       #(do
                                                 (prn :update-id id)
                                                 (frame/send frame/trusted-frame [:project/update-file
                                                                                  (:id this)
                                                                                  (.currentFile this)
                                                                                  :content
                                                                                  %]))
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
             ["gist" id filename] (edit-file {:id       id
                                              :filename filename})
             ["gists" username] (list-gists {:username username})))
    ]
   [:.w-50.h-100.bg-near-white.relative.flex.flex-column.bl.b--light-gray
    (repl-ui/ScrollBottom
      [:.flex-auto.overflow-auto.code
       (if-let [eval-log (d/get :repl/state :eval-log)]
         (map repl-values/display-result (last-n 50 eval-log))
         [:.pa3.gray "Loading..."])])
    result-toolbar]])
