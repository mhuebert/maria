(ns maria.commands.which-key
  (:require [re-view.core :as v :refer [defview]]
            [re-db.d :as d]
            [maria.commands.registry :as commands]
            [clojure.set :as set]))

(defn show-keyset
  "Render a keyset. Does not support multi-step key-combos."
  [modifiers-down [keyset]]
  [:.dib.bg-near-white.ph1.br2.pv05
   {:key (str keyset)}
   (->> (set/difference keyset modifiers-down)
        (map #(commands/show-key %))
        (sort-by (fn [x] (if (string? x) x (:name (meta x)))))
        (reverse))])

(defview hints
  []
  (let [modifiers-down (d/get :commands :modifiers-down)
        show-keyset #(show-keyset modifiers-down %)]
    (if-let [hints (seq (commands/get-hints modifiers-down))]
      [:.fixed.bottom-0.left-0.right-0.z-999.bg-white.shadow-4.f7.sans-serif.pv2.cc3
       (->> hints
            (keep (fn [{{command-name :exec} :results
                        keyset :keyset}]
                    (@commands/commands command-name)))
            (distinct)
            (sort-by :name)
            (map (fn [{:keys [display-name namespace doc key-patterns]}]
                   [:.pv1
                    [:.flex.items-center
                     [:.dib.w3.tr.flex-none.mr2
                      (show-keyset (first key-patterns))]
                     (if namespace [:span [:.dib.b.mr1 namespace ":"] display-name]
                                   [:span.b display-name])]
                    ;; TODO: add tooltip with doc
                    #_(when doc [:.gray.f7 [:.dib.w1] [:.dib.w3] doc])])))]
      [:.fixed])))