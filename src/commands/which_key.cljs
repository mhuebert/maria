(ns commands.which-key
  (:require [re-view.core :as v :refer [defview]]
            [re-db.d :as d]
            [commands.registry :as registry]
            [commands.exec :as exec]
            [clojure.set :as set]))

(defn show-keyset
  "Render a keyset. Does not support multi-step key-combos."
  [modifiers-down [keyset]]
  (let [keyset (set/difference keyset modifiers-down)
        sort-ks #(sort-by (fn [x] (if (string? x) x (:name (meta x)))) %)]
    [:.dib.bg-near-white.ph1.br2.pv05
     {:key (str keyset)}
     (->> (sort-ks (set/intersection keyset registry/modifiers))
          (map registry/show-key))
     " "
     (->> (sort-ks (set/difference keyset registry/modifiers))
          (map registry/show-key))]))

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
  []
  (let [modifiers-down (d/get :commands :modifiers-down)]
    (if-let [commands (and (d/get :commands :which-key/active?)
                           (seq (exec/keyset-commands modifiers-down (exec/get-context))))]
      [:.fixed.left-0.right-0.z-999.bg-white.shadow-4.f7.sans-serif.ph2.hint-columns
       {:style {:max-height 150
                :bottom 30}}
       (->> commands
            (group-by :display-namespace)
            (map (partial show-namespace-commands modifiers-down)))]
      [:.fixed])))