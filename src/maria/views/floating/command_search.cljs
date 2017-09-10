(ns maria.views.floating.command-search
  (:require [re-view.core :as v :refer [defview]]
            [commands.registry :as registry :refer-macros [defcommand]]
            [commands.which-key :as which-key]
            [commands.exec :as exec]
            [maria.views.dropdown :as dropdown]
            [maria.views.floating.float-ui :as ui]
            [clojure.string :as string]
            [maria.blocks.history :as history]
            [maria.views.bottom-bar :as bottom-bar]
            [maria.views.icons :as icons]))

(defview CommandSearch
  {:view/initial-state (fn [] {:q       ""
                               :context (exec/get-context)})
   :view/did-mount     (fn [{:keys [view/state] :as this}]
                         (.captureSelections this)
                         (some-> (:input @state) (.focus)))
   :capture-selections (fn [{:keys [view/state]}]
                         (swap! state assoc :prev-selections (history/get-selections)))
   :return-selections  (fn [{:keys [view/state]}]
                         (when-let [selections (:prev-selections @state)]
                           (history/put-selections! selections)
                           (v/swap-silently! state dissoc :prev-selections)))
   :view/will-unmount  (fn [this] (.returnSelections this))}
  [{:keys [view/state] :as this}]
  (let [{:keys [q context]} @state
        commands (exec/contextual-commands context)
        max-height (- (.-innerHeight js/window)
                      20                                    ;; margin-top
                      34                                    ;; input element
                      35                                    ;; bottom bar
                      )]
    [:.fixed
     {:style {:width       300
              :margin-left -150
              :left        "50%"
              :top         20}}
     [:.bg-darken.br2.pa2
      [:.shadow-4.flex.flex-column.items-stretch
       [:input.outline-0.pa2.bn {:placeholder "Search commands..."
                                 :style       {:border-bottom "1px solid #eee"}
                                 :ref         #(when % (swap! state assoc :input %))
                                 :value       q
                                 :on-change   #(swap! state assoc :q (.. % -target -value))}]
       (dropdown/numbered-list {:on-select!        #(do (.returnSelections this)
                                                        (ui/clear-hint!)
                                                        (exec/exec-command-name % context))
                                :ui/max-height     max-height
                                :default-selection 0
                                :on-selection      #(bottom-bar/show-var! (exec/get-command context %))
                                :items             (for [{:keys [name private parsed-bindings icon] :as command} commands
                                                         :when (and (string/includes? (str name) q)
                                                                    (not private))]
                                                     {:value name
                                                      :label [:.pv2.mr2.sans-serif.f7.flex.items-center.flex-auto

                                                              ;; Icons may add too much visual clutter
                                                              #_(-> (or icon icons/Blank)
                                                                    (icons/size 16))
                                                              [:span.gray (some-> (:display-namespace command)
                                                                                  (str "/"))]
                                                              (:display-name command)
                                                              [:.flex-auto]
                                                              (some->> (first parsed-bindings)
                                                                       (which-key/show-keyset #{}))]})})]]]))

;; input for text...
;; - auto-focus
;; - 
;; X get suggestions...
;; X put into a dropdown list with numbers

;; store stack of previous/common commands

(defcommand :commands/search
  {:bindings ["M1-P"
              "M1-Shift-P"]
   :icon     icons/Search}
  [context]
  (if (= :commands/search (:kind (ui/current-hint)))
    (ui/clear-hint!)
    (ui/floating-hint! {:component     CommandSearch
                        :kind          :commands/search
                        :props         nil
                        :cancel-events ["scroll" "mousedown"]
                        :rect          #js {:top    (+ (.-scrollY js/window) 100)
                                            :bottom (+ (.-scrollY js/window) 100)
                                            :left   (/ (.-innerWidth js/window) 2)}}))
  true)