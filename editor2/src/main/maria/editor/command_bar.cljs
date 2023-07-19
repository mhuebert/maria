(ns maria.editor.command-bar
  (:require ["cmdk$Command" :as Command]
            ["cmdk" :refer [useCommandState]]
            ["@radix-ui/react-popover" :as Popover]
            [maria.editor.keymaps :as keymaps]
            [maria.ui :as ui]
            [re-db.reactive :as r]
            [yawn.hooks :as h]
            [yawn.view :as v]))

(r/redef !state (r/atom {:sidebar/visible? false}))

(defn toggle! []
  (swap! !state update :sidebar/visible? not))

(comment
  command-list
  (time (count (current-commands))))

(v/defview command-item [{:as cmd :keys [ns title bindings]}]
  (let [value (str ns " " title)]
    (v/x [:el.px-2.py-1.rounded.mx-1.flex Command/Item
          {:key      value
           :value    value
           :onSelect (fn [_]
                       (keymaps/run-command cmd)
                       (keymaps/hide-command-bar!))
           :class    "data-[selected]:bg-sky-500 data-[selected]:text-white"}
          [:div.w-20.truncate.text-menu-muted.pr-2 (when (and ns (not= \_ (first ns))) ns)]
          title
          [:div.ml-auto.pl-3.text-menu-muted (some-> (first bindings) keymaps/show-binding)]])))


(v/defview input []
  (let [!search (h/use-state "")
        !open (h/use-state false)
        close! (fn [& _] (keymaps/hide-command-bar!))
        keydown-handler (h/use-memo #(ui/keydown-handler {:Escape close!
                                                          :Mod-.  close!}))]
    [:el Popover/Root {:open true}
     [:el.relative Command {:label "Command Menu"}
      [:el Popover/Anchor {:asChild true}
       [:el.rounded.border-slate-300.h-6.px-2.py-0.text-sm.h-7 Command/Input
        {:value         @!search
         :placeholder (str (-> (:editor/toggle-command-bar keymaps/commands:global)
                               :bindings
                               first
                               keymaps/binding-str) "  Commands")
         :on-mouse-down #(reset! keymaps/!prev-selected-element (.-activeElement js/document))
         :on-key-down keydown-handler
         :on-focus #(reset! !open true)
         :on-blur #(do (reset! !open false)
                       (reset! !search "")
                       (keymaps/hide-command-bar!))
         :onValueChange #(reset! !search %)
         :ref #(swap! ui/!state assoc :command-bar/element %)}]]
      [:el.bg-white.rounded.shadow.w-full.overflow-y-auto Popover/Content
       {:sideOffset 10
        :align "end"
        :class ["min-w-[200px]"
                (when-not @!open "hidden")]
        :style {:min-width  "var(--radix-popover-trigger-width)"
                     :max-height "calc(var(--radix-popover-content-available-height) - 1rem)"}}
       (when @!open
         [:el.py-1 Command/List
          {:style {:max-height "300px"}}
          [:el.px-2.py-1 Command/Empty "Nothing found"]
          (map command-item (keymaps/current-commands))])]]]))

;; TODO
;; try uFuzzy or similar