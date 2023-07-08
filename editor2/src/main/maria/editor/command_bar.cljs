(ns maria.editor.command-bar
  (:require ["cmdk$Command" :as Command]
            ["cmdk" :refer [useCommandState]]
            ["prosemirror-keymap" :refer [keydownHandler]]
            ["@radix-ui/react-popover" :as Popover]
            [applied-science.js-interop :as j]
            [applied-science.js-interop.alpha :refer [js]]
            [maria.editor.code.commands :as commands]
            [maria.editor.core :as editor.core]
            [maria.editor.keymaps :as keymaps]
            [maria.editor.prosemirror.schema :refer [schema]]
            [maria.ui :as ui]
            [re-db.reactive :as r]
            [re-db.util :as u]
            [yawn.hooks :as h]
            [yawn.view :as v]))

(r/redef !state (r/atom {:sidebar/visible? false}))

(defn toggle! []
  (swap! !state update :sidebar/visible? not))

(defn get-context []
  (when-let [^js ProseView @editor.core/!mounted-view]
    (let [code-context (let [^js node (commands/prose:cursor-node (.-state ProseView))]
                         (when (= (.-type node) (.. schema -nodes -code_block))
                           (let [CodeView (-> ProseView
                                              (j/get-in [:docView :children])
                                              (u/find-first #(identical? node (j/get % :node)))
                                              (j/get-in [:spec :CodeView]))]
                             {:NodeView (j/get CodeView :NodeView)
                              :CodeView CodeView})))]

      (merge {:ProseView ProseView}
             code-context
             (if code-context
               {:focused/code true}
               {:focus/prose true})))))

(defonce !command-list (r/reaction (remove :hidden? (vals @keymaps/!commands))))

(defn active? [context cmd]
  (let [{:as ctx :keys [ProseView CodeView]} context
        {:keys [kind] pred :when
         :or   {pred (constantly true)}} cmd]
    (boolean
      (case kind :prose (and ProseView (pred ctx))
                 :code (and CodeView (pred ctx))
                 (pred ctx)))))

(defn resolve-command
  ([cmd] (resolve-command (get-context) cmd))
  ([context cmd]
   (let [cmd (if (keyword? cmd)
               (@keymaps/!command-registry cmd)
               cmd)
         cmd (assoc cmd :active? (active? context cmd))
         cmd (if-let [prepare (:prepare cmd)]
               (prepare cmd context)
               cmd)]
     cmd)))

(defn run-command
  ([cmd] (run-command (get-context) cmd))
  ([context cmd]
   (let [{:keys [ProseView CodeView]} context
         {:keys [f kind active?]} (resolve-command context cmd)]
     (when active?
       (case kind
         :prose (when ProseView
                  (j/let [^js {:as view :keys [state dispatch]} ProseView]
                    (f state dispatch view)))
         :code (when CodeView
                 (f CodeView))
         (f context))))))

(defn use-global-keymap []
  (let [bindings (h/use-deref
                   (h/use-memo
                     #(r/reaction
                        (->> @keymaps/!commands
                             (filter (comp #{:global} :kind val))
                             (mapcat (fn [[id {:as cmd :keys [bindings f]}]]
                                       (for [binding bindings]
                                         [binding (fn [& _] (run-command cmd))])))
                             (into {})
                             clj->js))))]
    (h/use-effect
      (fn []
        (let [on-keydown (let [handler (keydownHandler bindings)]
                           (fn [event]
                             (handler #js{} event)))]
          (.addEventListener js/window "keydown" on-keydown)
          #(.removeEventListener js/window "keydown" on-keydown)))
      [bindings])))

(defn current-commands []
  (into []
        (comp (map (partial resolve-command (get-context)))
              (filter :active?))
        @!command-list))

(comment
  command-list
  (time (count (current-commands))))

(v/defview command-item [{:as cmd :keys [ns title bindings]}]
  (let [value (str ns " " title)]
    (v/x [:el.px-2.py-1.rounded.mx-1.flex Command/Item
          {:key      value
           :value    value
           :onSelect (fn [_]
                       (run-command cmd)
                       (keymaps/hide-command-bar!))
           :class    "data-[selected]:bg-sky-500 data-[selected]:text-white"}
          [:div.w-20.truncate.text-menu-muted.pr-2 (when (and ns (not= \_ (first ns))) ns)]
          title
          [:div.ml-auto.pl-3.text-menu-muted (some-> (first bindings) keymaps/show-binding)]])))


(v/defview input []
  (let [!search (h/use-state "")
        !open (h/use-state false)
        close! (fn [& _] (keymaps/hide-command-bar!))
        keydown-handler (h/use-memo #(partial (keydownHandler
                                                #js {:Escape close!
                                                     :Mod-.  close!}) #js{}))]
    [:el Popover/Root {:open true}
     [:el.relative Command {:label "Command Menu"}
      [:el Popover/Anchor {:asChild true}
       [:el.rounded.border-slate-300.h-6.px-2.py-0.text-sm Command/Input
        {:value         @!search
         :placeholder   "Commands..."
         :on-mouse-down #(reset! keymaps/!prev-selected-element (.-activeElement js/document))
         :on-key-down   keydown-handler
         :on-focus      #(reset! !open true)
         :on-blur       #(do (reset! !open false)
                             (reset! !search "")
                             (keymaps/hide-command-bar!))
         :onValueChange #(reset! !search %)
         :ref           #(swap! ui/!state assoc :command-bar/element %)}]]
      [:el.bg-white.rounded.shadow.w-full.overflow-y-scroll Popover/Content
       {:sideOffset 10
        :align      "end"
        :class      ["min-w-[200px]"
                     (when-not @!open "hidden")]
        :style      {:min-width  "var(--radix-popover-trigger-width)"
                     :max-height "calc(var(--radix-popover-content-available-height) - 1rem)"}}
       (when @!open
         [:el.py-1 Command/List
          {:style {:max-height "300px"}}
          [:el.px-2.py-1 Command/Empty "Nothing found"]
          (map command-item (current-commands))])]]]))

;; TODO
;; try uFuzzy or similar