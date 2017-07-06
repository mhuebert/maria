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
            [maria.views.repl-values :as repl-values]
            [maria.views.repl-utils :as repl-ui]
            [cognitect.transit :as t]
            [maria.frames.communication :as frame]))

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


(defview layout
  {:life/initial-state {:repl-editor nil}
   :get-editor         (fn [{:keys [view/state]}]
                         (some-> (:repl-editor @state) :view/state deref :editor))}
  [{:keys [view/state window-id] :as this}]
  [:.h-100.flex.items-stretch
   [:.w-50.relative.border-box.flex.flex-column
    (let [source-id (d/get :layout window-id)
          {:keys [local-value default-value persisted-value loading-message]} (d/entity source-id)]
      (prn :layout {:source source-id
                    :entity (d/entity source-id)})
      (if loading-message
        [:.w-100
         [:.b.progress-indeterminate]
         [:.pa2.gray loading-message]]
        (list
          (when (and local-value
                     persisted-value
                     (not= local-value persisted-value))
            "can-save")
          (let [revertible-value (or persisted-value default-value)]
            (when (and local-value
                       (not= local-value revertible-value))
              (prn :loc local-value :rev revertible-value)
              [:.dib.pointer {:on-click #(.setValue (.getEditor this) revertible-value)} "revert"]))
          (editor/editor {:ref             #(when % (swap! state assoc :repl-editor %))
                          :on-update       #(frame/send frame/parent-frame [:source/update-local (d/get :layout window-id) %])
                          :source-id       source-id
                          :default-value   (or local-value default-value)
                          :event/mousedown #(when (.-metaKey %)
                                              (.preventDefault %)
                                              (eval-editor (.getEditor this) :bracket))
                          :event/keydown   (fn [editor e]
                                             (case (.keyName js/CodeMirror e)
                                               ("Shift-Cmd-Enter"
                                                 "Shift-Ctrl-Enter") (eval-editor editor :top-level)
                                               ("Cmd-Enter"
                                                 "Ctrl-Enter") (eval-editor editor :bracket)
                                               nil))}))))]
   [:.w-50.h-100.bg-near-white.relative.flex.flex-column.bl.b--light-gray
    (repl-ui/ScrollBottom
      [:.flex-auto.overflow-auto.code
       (if-let [eval-log (d/get :repl/state :eval-log)]
         (map repl-values/display-result (last-n 50 eval-log))
         [:.pa3.gray "Loading..."])])
    result-toolbar]])
