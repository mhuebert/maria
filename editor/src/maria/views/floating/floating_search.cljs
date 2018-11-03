(ns maria.views.floating.floating-search
  (:require [chia.view :as v]
            [lark.commands.registry :as registry :refer-macros [defcommand]]
            [lark.commands.exec :as exec]
            [maria.views.dropdown :as dropdown]
            [maria.views.floating.float-ui :as ui]
            [maria.blocks.history :as history]
            [maria.views.bottom-bar :as bottom-bar]
            [maria.views.icons :as icons]
            [maria.frames.frame-communication :as frame]
            [chia.triple-db :as d]
            [maria.commands.doc :as doc]
            [maria.util :as util]))

(def -prev-selection (volatile! nil))

(defn return-selections! []
  (when (not= (some-> (:kind (ui/current-view))
                      (namespace))
              (namespace ::this))
    (when-let [selections @-prev-selection]
      (history/put-selections! selections)
      (vreset! -prev-selection nil))))

(v/defview FloatingSearch
  "A floating search bar, displayed near the top-middle of the screen.
  Takes care of saving and returning the current focus/selection."
  {:view/initial-state (fn [] {:q ""})
   :view/did-mount (fn [{:keys [view/state] :as this}]
                     (when-not @-prev-selection
                       (vreset! -prev-selection (history/get-selections)))
                     (some-> (:input @state) (.focus)))
   :view/will-unmount #(return-selections!)}
  [{:keys [view/state on-selection on-select! items placeholder] :as this}]
  (let [{:keys [q]} @state
        max-height (- (.-innerHeight js/window)
                      20                                    ;; margin-top
                      34                                    ;; input element
                      35                                    ;; bottom bar
                      )]
    [:.fixed
     {:style {:width 300
              ;:margin-left -150
              ;:left        "50%"
              :right 5
              :top   5}}
     [:.bg-darken.br2.pa1
      [:.shadow-4.flex.flex-column.items-stretch
       [:input.outline-0.pa2.bn.f6 {:placeholder placeholder
                                    :style       {:border-bottom "1px solid #eee"}
                                    :ref         #(when % (swap! state assoc :input %))
                                    :value       q
                                    :on-change   #(swap! state assoc :q (.. % -target -value))}]
       [dropdown/numbered-list {:on-select! (fn [value]
                                              (return-selections!)
                                              (ui/clear!)
                                              (on-select! value))
                                :select-on-enter true
                                :ui/max-height max-height
                                :default-selection 0
                                :on-selection on-selection
                                :items (items q)}]]]]))

(v/defview CommandSearch
  {:view/initial-state #(do {:context (exec/get-context)})
   :view/will-unmount  #(bottom-bar/retract-bottom-bar! :eldoc/command-search)}
  [{:keys [view/state]}]
  (let [{:keys [context]} @state
        commands (exec/contextual-commands context)]
    [FloatingSearch {:on-selection #(bottom-bar/add-bottom-bar! :eldoc/command-search (bottom-bar/ShowVar (exec/get-command context %)))
                     :placeholder "Search commands..."
                     :on-select! (fn [value]
                                   (exec/exec-command-name value context))
                     :items (fn [q]
                              (let [pattern (re-pattern (str "(?i)\\b" q))]
                                (->> commands
                                     (sequence (comp
                                                (filter #(and (not (:private %))
                                                              (re-find pattern (str (:name %)))))
                                                (map (fn [{:keys [name bindings exec?] :as command}]
                                                       {:value name
                                                        :score (if exec? 0 1)
                                                        :label [:.h2.mr2.sans-serif.f7.flex.items-center.flex-auto
                                                                [:.gray {:class (when-not exec? "o-50")}
                                                                 (some-> (:display-namespace command)
                                                                         (str "/"))]
                                                                [:div {:style {:font-weight 500}
                                                                       :class (when-not exec? "o-50")} (:display-name command)]
                                                                [:.flex-auto]
                                                                [:.gray
                                                                 (some->> (first bindings)
                                                                          (registry/binding-string->vec)
                                                                          (registry/keyset-string))]]}))))
                                     (sort-by :score))))}]))


(defcommand :doc/open
  {:bindings ["M1-O"]}
  []
  (let [Label :.sans-serif.f7.pv2.half-b]
    (if (= ::open (:kind (ui/current-view)))
      (ui/clear!)
      (ui/floating-view! {:component FloatingSearch
                          :kind      ::open

                          :props     {:placeholder "Open..."
                                      :on-select!  (fn [value]
                                                     (frame/send frame/trusted-frame [:window/navigate value {}]))
                                      :items       (fn [q]
                                                     (cons
                                                       {:value "/"
                                                        :label [Label "Home"]}
                                                       (let [username (d/get :auth-public :username)
                                                             pattern (re-pattern (str "(?i)\\b" q))]
                                                         (for [{:keys [filename local-url]} (distinct (concat (doc/locals-docs :local/recents)
                                                                                                              (doc/user-gists username)
                                                                                                              (seq doc/curriculum)))
                                                               :when (and (util/some-str filename) (re-find pattern filename))]
                                                           {:value local-url
                                                            :label [Label (doc/strip-clj-ext filename)]}))))}
                          ;:cancel-events ["scroll" "mousedown"]
                          :float/pos [(/ (.-innerWidth js/window) 2)
                                      (+ (.-scrollY js/window) 100)]}))))

;; input for text...
;; - auto-focus
;; - 
;; X get suggestions...
;; X put into a dropdown list with numbers

;; store stack of previous/common commands

(defcommand :commands/command-search
  {:bindings ["M1-P"
              "M1-Shift-P"]
   :icon     icons/Search}
  [context]
  (if (= ::search (:kind (ui/current-view)))
    (ui/clear!)
    (ui/floating-view! {:component CommandSearch
                        :kind      ::search
                        :props     nil
                        :float/pos [(/ (.-innerWidth js/window) 2)
                                    (+ (.-scrollY js/window) 100)]}))
  true)