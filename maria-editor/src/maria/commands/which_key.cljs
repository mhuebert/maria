(ns maria.commands.which-key
  (:require [re-view.core :as v :refer [defview]]
            [re-db.d :as d]
            [lark.commands.registry :as registry]
            [lark.commands.exec :as exec]
            [clojure.set :as set]
            [maria.views.bottom-bar :as bottom-bar]))

(defn show-keyset
  "Render a keyset. Does not support multi-step key-combos."
  [modifiers-down [keyset]]
  (let [keyset (set/difference keyset modifiers-down)]
    [:.dib.bg-near-white.ph1.br2.pv05.gray
     {:key (str keyset)}
     (registry/keyset-string (set/difference keyset modifiers-down))]))

(defn show-namespace-commands [modifiers-down [namespace hints]]
  (let [row-height 24]
    [:.avoid-break.bt.b--near-white.pv1
     [:.flex
      [:.pr1.flex-none.tr.b.pv2.flex.items-center.justify-end
       {:style {:min-width 70
                :height    row-height}}
       namespace]
      [:.flex-auto
       (for [{:keys [display-name name parsed-bindings]} hints]
         [:.flex.items-center.ws-nowrap.pointer.hover-bg-near-white.pl1.br2
          {:on-mouse-down #(exec/exec-command-name name)
           :style         {:height row-height}}
          display-name
          [:.flex-auto]
          (show-keyset modifiers-down (first parsed-bindings))])]]]))

(defview show-commands
  {:update             (fn [{:keys [view/state]}]
                         (let [{:keys [active? prev-content]} @state
                               next-active? (d/get :commands :which-key/active?)
                               start? (and next-active? (not active?))
                               finish? (and (not next-active?) active?)]
                           (when start?
                             (v/swap-silently! state assoc :prev-content prev-content :active? true))
                           (when finish?
                             (v/swap-silently! state dissoc :prev-content :active?))
                           (bottom-bar/set-bottom-bar! (cond next-active?
                                                             (let [modifiers-down (d/get :commands :modifiers-down)]
                                                               (if-let [commands (seq (exec/keyset-commands modifiers-down (exec/get-context)))]
                                                                 [:.f7.sans-serif.ph2.hint-columns.bg-white
                                                                  {:style {:max-height 150}}
                                                                  (->> commands
                                                                       (group-by :display-namespace)
                                                                       (map (partial show-namespace-commands modifiers-down)))]
                                                                 [:.fixed]))
                                                             finish? prev-content
                                                             :else nil))))
   :view/did-mount     #(.update %)
   :view/should-update (constantly true)
   :view/did-update    #(.update %)}
  []
  (d/get :commands :which-key/active?)
  nil)