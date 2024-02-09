(ns maria.editor.keymaps
  (:require ["@codemirror/commands" :as cm.commands]
            ["@codemirror/view" :as cm.view]
            ["prosemirror-state" :refer [NodeSelection TextSelection Selection]]
            ["prosemirror-commands" :as pm.cmd :refer [baseKeymap]]
            ["prosemirror-keymap" :as pm.keymap]
            ["prosemirror-schema-list" :as pm.schema-list]
            ["prosemirror-history" :as pm.history]
            [applied-science.js-interop :as j]
            [applied-science.js-interop.alpha :refer [js]]
            [clojure.string :as str]
            [maria.editor.code.commands :as commands]
            [maria.editor.code.commands :as code.commands]
            [maria.editor.prosemirror.links :as links]
            [maria.editor.prosemirror.schema :refer [schema]]
            [maria.editor.util :as u]
            [maria.ui :as ui]
            [nextjournal.clojure-mode :as clj-mode]
            [nextjournal.clojure-mode.commands :refer [paredit-index]]
            [re-db.hooks :as h]
            [re-db.reactive :as r]
            [yawn.view :as v]))

(def mac? (and (exists? js/navigator)
               (.test #"Mac|iPhone|iPad|iPod" js/navigator.platform)))

(def default-keys (pm.keymap/keymap baseKeymap))

(def chain pm.cmd/chainCommands)

(defn spaced-name [the-name]
  (str/capitalize (str/replace (name the-name) "-" " ")))

(defn format-commands [m]
  (reduce-kv (fn [out id cmd]
               (assoc out id
                          (assoc cmd :id id
                                     :ns (some-> (namespace id) spaced-name)
                                     :title (or (:title cmd)
                                                (spaced-name (name id))))))
             {}
             m))

(defn run-prosemirror [{:keys [ProseView]} f]
  (when ProseView
    (j/let [^js {:as view :keys [state dispatch]} ProseView]
      (f state dispatch view))))

(defn run-codemirror [{:keys [CodeView]} f]
  (when CodeView
    (f CodeView)))

(def commands:prose
  (j/let [^js {{:keys [strong em code]} :marks
               {:keys [bullet_list ordered_list blockquote
                       hard_break list_item paragraph
                       code_block heading horizontal_rule]} :nodes} schema
          hard-break-cmd (chain
                           pm.cmd/exitCode
                           (fn [^js state dispatch]
                             (when dispatch
                               (dispatch (.. state -tr
                                             (replaceSelectionWith (.create hard_break))
                                             (pm.cmd/scrollIntoView))))
                             true))]
    {:text/bold {:kind :prose
                 :when :focus/prose
                 :bindings [:Mod-b]
                 :f (pm.cmd/toggleMark strong)}
     :text/italic {:kind :prose
                   :when :focus/prose
                   :bindings [:Mod-i]
                   :f (pm.cmd/toggleMark em)}
     :text/inline-code {:kind :prose
                        :when :focus/prose
                        :bindings ["Mod-`"]
                        :f (pm.cmd/toggleMark code)}
     :block/paragraph {:bindings [:Shift-Ctrl-0]
                       :kind :prose
                       :f (pm.cmd/setBlockType paragraph)}
     :block/code {:bindings ["Shift-Ctrl-\\"]
                  :kind :prose
                  :when :focus/prose
                  :f (pm.cmd/setBlockType code_block)}
     :block/h1 {:bindings [:Shift-Ctrl-1]
                :kind :prose
                :when :focus/prose
                :f (pm.cmd/setBlockType heading #js{:level 1})}
     :block/h2 {:bindings [:Shift-Ctrl-2]
                :kind :prose
                :when :focus/prose
                :f (pm.cmd/setBlockType heading #js{:level 2})}
     :block/h3 {:bindings [:Shift-Ctrl-3]
                :kind :prose
                :when :focus/prose
                :f (pm.cmd/setBlockType heading #js{:level 3})}
     :block/h4 {:bindings [:Shift-Ctrl-4]
                :kind :prose
                :when :focus/prose
                :f (pm.cmd/setBlockType heading #js{:level 4})}
     :block/h5 {:bindings [:Shift-Ctrl-5]
                :kind :prose
                :when :focus/prose
                :f (pm.cmd/setBlockType heading #js{:level 5})}
     :block/h6 {:bindings [:Shift-Ctrl-6]
                :kind :prose
                :when :focus/prose
                :f (pm.cmd/setBlockType heading #js{:level 6})}
     :block/bullet-list {:title "Convert block to bullet list"
                         :bindings [:Shift-Ctrl-8]
                         :kind :prose
                         :when :focus/prose
                         :f (pm.schema-list/wrapInList bullet_list)}
     :block/blockquote {:title "Convert block to blockquote"
                        :bindings [:Ctrl->]
                        :kind :prose
                        :when :focus/prose
                        :f (pm.cmd/wrapIn blockquote)}
     ;; does not work
     #_#_:block/ordered-list {:bindings [:Shift-Ctrl-9]
                              :f (pm.schema-list/wrapInList ordered_list)}
     :history/undo {:bindings [:Mod-z]
                    :kind :prose
                    :f pm.history/undo}
     :history/redo {:bindings (cond-> [:Mod-Shift-z]
                                      (not mac?)
                                      (conj :Mod-y))
                    :kind :prose
                    :f pm.history/redo}
     :list/outdent {:title "Outdent list item"
                    :bindings ["Mod-["
                               :Shift-Tab]
                    :kind :prose
                    :when :focus/prose
                    :f (pm.schema-list/liftListItem list_item)}
     :list/indent {:title "Indent list item"
                   :bindings ["Mod-]"
                              :Tab]
                   :kind :prose
                   :when :focus/prose
                   :f (pm.schema-list/sinkListItem list_item)}
     :insert/horizontal-rule {:bindings [:Mod-_]
                              :kind :prose
                              :when :focus/prose
                              :f (fn [^js state dispatch]
                                   (when dispatch
                                     (dispatch (.. state -tr
                                                   (replaceSelectionWith (.create horizontal_rule))
                                                   (scrollIntoView))))
                                   true)}
     :code/eval-doc {:kind :prose
                     :bindings [:Mod-Alt-Enter]
                     :f (fn [state dispatch view]
                          (code.commands/prose:eval-prose-view! view)
                          true)}
     :code/hide-source {:when :NodeView
                        :f (fn [{:keys [NodeView]}]
                             (swap! (j/get NodeView :!ui-state) update :hide-source (fnil not false)))}
     :prose/backspace {:bindings [:Backspace]
                       :hidden? true
                       :f (chain links/open-link-on-backspace
                                 pm.cmd/joinTextblockBackward
                                 pm.cmd/selectNodeBackward
                                 pm.cmd/deleteSelection)
                       :kind :prose}
     :prose/join-up {:bindings [:Alt-ArrowUp]
                     :hidden? true
                     :f pm.cmd/joinUp
                     :kind :prose}
     :prose/join-down {:bindings [:Alt-ArrowDown]
                       :hidden? true
                       :f pm.cmd/joinDown
                       :kind :prose}
     :prose/lift {:bindings [:Mod-BracketLeft]
                  :hidden? true
                  :f pm.cmd/lift
                  :kind :prose}
     :prose/select-parent {:bindings [:Escape]
                           :hidden? true
                           :kind :prose
                           :f pm.cmd/selectParentNode}

     :prose/enter {:bindings [:Enter]
                   :hidden? true
                   :f (chain (pm.schema-list/splitListItem list_item)
                             pm.cmd/liftEmptyBlock
                             code.commands/prose:convert-to-code)
                   :kind :prose}
     :prose/arrow-left {:bindings [:ArrowLeft]
                        :hidden? true
                        :f (code.commands/prose:arrow-handler -1)
                        :kind :prose}
     :prose/arrow-up {:bindings [:ArrowUp]
                      :hidden? true
                      :f (code.commands/prose:arrow-handler -1)
                      :kind :prose}
     :prose/arrow-right {:bindings [:ArrowRight]
                         :hidden? true
                         :f (code.commands/prose:arrow-handler 1)
                         :kind :prose}
     :prose/arrow-down {:bindings [:ArrowDown]
                        :hidden? true
                        :f (code.commands/prose:arrow-handler 1)
                        :kind :prose}

     :prose/hard-break {:bindings (cond-> [:Mod-Enter
                                           :Shift-Enter]
                                          mac?
                                          (conj :Ctrl-Enter))
                        :hidden? true
                        :kind :prose
                        :f hard-break-cmd}}))


(def prose-keymap
  (let [out #js{}]
    (doseq [[_ {:keys [bindings f]}] commands:prose
            binding bindings]
      (j/!set out (name binding) f))
    (pm.keymap/keymap out)))

(defn something-selected [cm-state]
  (.. cm-state -selection -ranges (some #(not (.-empty ^js %)))))

(def commands:code
  {:code/eval-block {:bindings [:Shift-Enter]
                     ;; TODO :f
                     :kind :code}
   :code/eval-region {:bindings [:Mod-Enter]
                      ;; TODO :f
                      :kind :code}
   :code/format {:bindings [:Alt-Tab]
                 :kind :code
                 ;; set title based on whether selection is empty
                 :prepare (fn [cmd {:keys [NodeView]}]
                            (cond-> cmd
                                    NodeView
                                    (assoc :title
                                           (if (something-selected (.-state (j/get NodeView :CodeView)))
                                             "Format selection"
                                             "Format cell"))))
                 :f (:indent paredit-index)}
   :code/unwrap {:bindings [:Alt-s]
                 :title "Splice/unwrap form into parent"
                 :kind :code
                 :f (:unwrap paredit-index)}
   :code/slurp-right {:bindings [:Ctrl-ArrowRight
                                 :Mod-Shift-ArrowRight
                                 :Mod-Shift-k]
                      :doc "Expand collection to include form to the right"
                      :kind :code
                      :f (:slurp-forward paredit-index)}
   :code/barf-right {:bindings [:Ctrl-ArrowLeft
                                :Mod-Shift-ArrowLeft]
                     :doc "Push last element of collection out to the right"
                     :kind :code
                     :f (:barf-forward paredit-index)}
   :code/slurp-backward {:bindings [:Shift-Ctrl-ArrowLeft]
                         :doc "Expand collection to include form to the left"
                         :kind :code
                         :f (:slurp-backward paredit-index)}
   :code/barf-left {:bindings [:Shift-Ctrl-ArrowRight]
                    :doc "Push first element of collection out to the left"
                    :kind :code
                    :f (:barf-backward paredit-index)}
   :code/kill {:bindings [:Ctrl-k]
               :doc "Remove all forms from cursor to end of line"
               :kind :code
               :f (:kill paredit-index)}
   :code/hop-cursor-right {:bindings [:Alt-ArrowRight]
                           :doc "Move cursor one form to the right"
                           :kind :code
                           :f (:nav-right paredit-index)}
   :code/hop-cursor-left {:bindings [:Alt-ArrowLeft]
                          :doc "Move cursor one form to the left"
                          :kind :code
                          :f (:nav-left paredit-index)}
   :code/expand-selection-right {:bindings [:Shift-Alt-ArrowRight]
                                 :doc "Expand selection one form to the left"
                                 :kind :code
                                 :f (:nav-select-right paredit-index)}
   :code/expand-selection-left {:bindings [:Shift-Alt-ArrowLeft]
                                :doc "Expand selection one form to the right"
                                :kind :code
                                :f (:nav-select-left paredit-index)}
   :code/grow-selection {:bindings [:Mod-1
                                    :Alt-ArrowUp
                                    :Mod-ArrowUp]
                         :kind :code
                         :f (:selection-grow paredit-index)}
   :code/shrink-selection {:bindings [:Mod-2
                                      :Alt-ArrowDown
                                      :Mod-ArrowDown]
                           :kind :code
                           :f (:selection-return paredit-index)}
   :code/arrow-up {:bindings [:ArrowUp]
                   :kind :code
                   :hidden? true
                   :f (code.commands/code:arrow-handler :line -1)}
   :code/arrow-left {:bindings [:ArrowLeft]
                     :kind :code
                     :hidden? true
                     :f (code.commands/code:arrow-handler :char -1)}
   :code/arrow-down {:bindings [:ArrowDown]
                     :kind :code
                     :hidden? true
                     :f (code.commands/code:arrow-handler :line 1)}
   :code/arrow-right {:bindings [:ArrowRight]
                      :kind :code
                      :hidden? true
                      :f (code.commands/code:arrow-handler :char 1)}
   :code/enter {:bindings [:Enter]
                :kind :code
                :hidden? true
                :f code.commands/code:handle-enter}
   :code/escape {:bindings [:Escape]
                 :kind :code
                 :hidden? true
                 :f code.commands/code:handle-escape}
   :code/remove-empty-code-block {:bindings [:Backspace]
                                  :kind :code
                                  :hidden? true
                                  :f code.commands/code:remove-on-backspace}
   :code/copy {:bindings [:Mod-c]
               :kind :code
               :hidden? true
               :title "Copy code"
               :f code.commands/code:copy-current-region}
   :code/cut {:bindings [:Mod-x]
              :kind :code
              :hidden? true
              :title "Cut code"
              :f code.commands/code:cut-current-region}})

(def code-keymap
  (.of cm.view/keymap
       (-> (let [out #js []]
             (doseq [[_ {:keys [doc bindings f]}] commands:code
                     binding bindings]
               (j/push! out #js{:key (name binding)
                                :run f}))
             out)
           (.concat clj-mode/builtin-keymap))))

(def !prev-selected-element (atom nil))

(defn hide-command-bar! [{:keys [command-bar/element]}]
  (some-> element (j/call :blur))
  (when-let [^js prev @!prev-selected-element]
    (reset! !prev-selected-element nil)
    (.focus prev)))

(defn show-command-bar! [{:keys [command-bar/element]}]
  (when-let [el element]
    (when-not (= @!prev-selected-element (.-activeElement js/document))
      (reset! !prev-selected-element (.-activeElement js/document))
      (.focus el))))

(defn command-bar-open? [{:keys [command-bar/element]}]
  (when-let [el element]
    (identical? el (.-activeElement js/document))))

(def commands:global
  {:editor/focus! {:f (comp (j/call :focus) :ProseView)
                   :when :ProseView
                   :hidden? true}
   :editor/toggle-sidebar {:bindings [:Shift-Mod-k]
                           :kind :global
                           :prepare (fn [cmd _]
                                      (assoc cmd
                                        :title (if (:sidebar/visible? @ui/!state)
                                                 "Hide sidebar"
                                                 "Show sidebar")))
                           :f (fn [_]
                                (swap! ui/!state update :sidebar/visible? not)
                                false)}
   :editor/toggle-command-bar {:bindings [:Mod-k]
                               :kind :global
                               :hidden? true
                               :prepare (fn [cmd ctx]
                                          (assoc cmd :f
                                                     (if (command-bar-open? ctx)
                                                       hide-command-bar!
                                                       show-command-bar!)))}
   :prose/toggle-prose-visibility {:kind :global
                                   :when :ProseView
                                   :prepare (fn [cmd {:keys [ProseView]}]
                                              (merge cmd
                                                     (let [^js classes (j/get-in ProseView [:dom :classList])]
                                                       (if (.contains classes "hide-all-prose")
                                                         {:title "Show all prose"
                                                          :f #(.remove classes "hide-all-prose")}
                                                         {:title "Hide all prose"
                                                          :f #(.add classes "hide-all-prose")}))))}})

(defonce !command-registry (atom {}))
(defonce !binding-overrides (atom {}))
(defonce !commands
  (r/reaction
    (reduce-kv (fn [registry id bindings]
                 (if-some [cmd (registry id)]
                   (assoc registry id (assoc cmd :bindings bindings))
                   registry))
               (h/use-deref !command-registry)
               (h/use-deref !binding-overrides))))

(defn register-commands! [commands]
  (swap! !command-registry merge (format-commands commands)))

(register-commands! commands:prose)
(register-commands! commands:code)
(register-commands! commands:global)

(defn segment-str [segment]
  (case segment
    "Mod" (if mac? "⌘" "Ctrl")
    "Alt" (if mac? "⌥" "Alt")
    "Ctrl" (if mac? "⌃" "Ctrl")
    ;"m3" (if mac? "Ctrl" "Meta")
    "ArrowLeft" "◄"
    "ArrowRight" "►"
    "ArrowUp" "▲"
    "ArrowDown" "▼"
    "Backspace" "⌫"
    "Shift" "⇧"
    "Tab" "⇥"
    "Enter" "⏎"
    "\\" "\\"
    ;; ⌫
    (str/capitalize segment)))

(defn binding-str [binding]
  (str/join (map segment-str (str/split (name binding) #"\-"))))

(defn show-binding [binding]
  (v/x [:div.inline-flex
        {:class "gap-[2px]"}
        (map-indexed (fn [i segment]
                       [:span {:key i} (segment-str segment)])
                     (str/split (name binding) #"\-"))]))

(defn active? [context cmd]
  (let [{:as ctx :keys [ProseView CodeView]} context
        {:keys [kind] pred :when
         :or {pred (constantly true)}} cmd]
    (boolean
      (case kind :prose (and ProseView (pred ctx))
                 :code (and CodeView (pred ctx))
                 (pred ctx)))))

(defonce !context (atom {}))

(def add-context (partial swap! !context assoc))
(def remove-context (partial swap! !context u/dissoc-value))

(defn get-context []
  (merge @!context
         (when-let [^js ProseView (:ProseView @!context)]
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
                      {:focus/prose true}))))))

(defn resolve-command
  ([cmd] (if (and (map? cmd) (:context cmd))
           cmd
           (resolve-command (get-context) cmd)))
  ([context cmd]
   (let [cmd (if (keyword? cmd)
               (@!command-registry cmd)
               cmd)
         is-active (active? context cmd)
         cmd (assoc cmd :active? is-active)
         cmd (if-let [prepare (and is-active (:prepare cmd))]
               (prepare cmd context)
               cmd)]
     (assoc cmd :context context))))

(defn run-command
  ([cmd] (run-command (or (:context cmd) (get-context)) cmd))
  ([context cmd]
   (let [{:keys [f kind active?]} (resolve-command context cmd)]
     (when active?
       (case kind
         :prose (run-prosemirror context f)
         :code (run-codemirror context f)
         (f context))))))

(defn use-global-keymap []
  (let [bindings (h/use-deref
                   (h/use-memo
                     #(r/reaction
                        (->> @!commands
                             (remove (comp #{:prose :code} :kind val))
                             (mapcat (fn [[id {:as cmd :keys [bindings f]}]]
                                       (for [binding bindings]
                                         [binding (fn [& _] (run-command cmd))])))
                             (into {})))))]
    (h/use-effect
      (fn []
        (let [on-keydown (ui/keydown-handler bindings)]
          (.addEventListener js/window "keydown" on-keydown)
          #(.removeEventListener js/window "keydown" on-keydown)))
      [bindings])))

(defonce !command-list (r/reaction (remove :hidden? (vals @!commands))))

(defn current-commands []
  (into []
        (comp (map (partial resolve-command (get-context)))
              (filter :active?))
        @!command-list))