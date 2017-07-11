(ns maria.commands.which-key
  (:require [re-view.core :as v :refer [defview]]
            [re-db.d :as d]
            [re-view-material.icons :as icons]
            [maria.commands.core :as commands]
            [clojure.set :as set]))

(defview hints
  []
  (let [modifiers-down (d/get :commands :modifiers-down)]
    (if-let [hints (seq (commands/get-hints modifiers-down))]
      [:.fixed.bottom-0.left-0.right-0.z-999.bg-white.shadow-4.f6.sans-serif.flex-wrap.flex.pv2
       (->> hints
            (map (fn [{:keys [keyset results]}]
                   (let [exec (:exec results)
                         {:keys [display-name doc]} (@commands/commands exec)]
                     [:.w-third.dib.f6.mv1
                      [:.flex.items-center
                       [:.dib.w3.tr.flex-none.mr2
                        [:.dib.bg-near-white.ph1.br2.pv05 (->> (set/difference keyset modifiers-down)
                                                               (map #(commands/show-key %))
                                                               (sort-by compare)
                                                               (reverse))]]

                       display-name]
                      ;; TODO: add tooltip with doc
                      #_(when doc [:.gray.f7 [:.dib.w1] [:.dib.w3] doc])]))))]
      [:.fixed])))