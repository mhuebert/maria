(ns maria.editor.command-bar
  (:require ["cmdk$Command" :as Command]
            ["cmdk" :refer [useCommandState]]
            ["prosemirror-keymap" :refer [keydownHandler]]
            ["@radix-ui/react-popover" :as Popover]
            [maria.editor.keymaps :as keymaps]
            [maria.editor.core :as editor.core]
            [maria.editor.code-blocks.commands :as commands]
            [maria.editor.prosemirror.schema :refer [schema]]
            [applied-science.js-interop :as j]
            [applied-science.js-interop.alpha :refer [js]]
            [maria.ui :as ui]
            [re-db.reactive :as r]
            [re-db.util :as u]
            [yawn.view :as v]
            [yawn.hooks :as h]))

(r/redef !state (r/atom {:sidebar/visible? false}))

(defn toggle! []
  (swap! !state update :sidebar/visible? not))

(defn get-context []
  (when-let [^js proseView @editor.core/!mounted-view]
    (or (let [^js node (commands/prose:cursor-node (.-state proseView))]
          (when (= (.-type node) (.. schema -nodes -code_block))
            (-> proseView
                (j/get-in [:docView :children])
                (u/find-first #(identical? node (j/get % :node)))
                (j/get-in [:spec :codeView :node-view]))))
        #js{:proseView proseView})))

(def command-list (reduce-kv (fn [out id cmd]
                               (if (:hidden? cmd)
                                 out
                                 (conj out (assoc cmd :id id))))
                             []
                             keymaps/commands:all))

(defn resolve-command [cmd]
  (if (keyword? cmd)
    (keymaps/commands:all cmd)
    cmd))

(defn run-command
  ([cmd] (run-command (get-context) cmd))
  ([context cmd]
   (j/let [{:keys [f kind]} (resolve-command cmd)
           ^js {:as context :keys [proseView codeView]} context]
     (case kind
       :prose (when proseView
                (j/let [^js {:as view :keys [state dispatch]} proseView]
                  (f state dispatch view)))
       :code (when codeView
               (f codeView))
       (f context)))))

(defn use-global-keymap []
  (h/use-effect
    (fn []
      (let [bindings (->> keymaps/commands:global
                          (mapcat (fn [[id {:as cmd :keys [bindings f]}]]
                                    (for [binding bindings]
                                      [binding (fn [& _] (run-command cmd))])))
                          (into {})
                          clj->js)
            on-keydown (let [handler (keydownHandler bindings)]
                         (fn [event]
                           (handler #js{} event)))]
        (.addEventListener js/window "keydown" on-keydown)
        #(.removeEventListener js/window "keydown" on-keydown)))))

(defn current-commands []
  (j/let [^js {:as ctx :keys [proseView codeView]} (get-context)]
    (into []
          (remove (fn [{:keys [kind when]}]
                    (or (and (nil? proseView) (= kind :prose))
                        (and (nil? codeView) (= kind :code))
                        (and when (not (when ctx))))))
          command-list)))
(comment
  command-list
  (time (count (current-commands))))

(defn menu-item [label]
  (v/x [:el.px-2.py-1 Command/Item {:class "data-[selected]:bg-sky-100"} label]))

(v/defview input []
  (let [!search (h/use-state "")
        !open (h/use-state false)
        close! (fn [& _] (prn :close) (.blur (:command-bar/element @ui/!state)))
        keydown-handler (h/use-memo #(partial (keydownHandler
                                                #js {:Escape close!
                                                     :Mod-.  close!}) #js{}))]
    [:el Popover/Root {:open true}
     [:el.relative Command {:label "Command Menu"}
      [:el Popover/Anchor {:asChild true}
       [:el.rounded.border-slate-300.h-6.px-2.py-0.text-sm Command/Input
        {:value @!search
         :on-key-down keydown-handler
         :on-focus #(reset! !open true)
         :on-blur #(reset! !open false)
         :onValueChange #(reset! !search %)
         :ref #(swap! ui/!state assoc :command-bar/element %)}]]
      [:el.bg-white.rounded.shadow.w-full Popover/Content
       {:sideOffset 10
        :align "end"
        :class      ["min-w-[200px]"
                     (when-not @!open "hidden")]}
       [:el Command/List
        {:style {:height "var(--cmdk-list-height)"}}
        [:el.px-2.py-1 Command/Empty "Nothing found"]
        [menu-item "Apple"]
        [menu-item "Banana"]
        [menu-item "Carrot"]]]]]))