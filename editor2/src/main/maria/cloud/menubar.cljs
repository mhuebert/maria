(ns maria.cloud.menubar
  (:require ["@radix-ui/react-menubar" :as menu :refer [Item Separator Root Menu Trigger Portal Content]]
            ["@radix-ui/react-avatar" :as ava]
            [applied-science.js-interop :as j]
            [clojure.string :as str]
            [maria.cloud.github :as gh]
            [maria.cloud.persistence :as persist]
            [maria.cloud.sidebar :as sidebar]
            [maria.editor.command-bar :as command-bar]
            [maria.editor.icons :as icons]
            [maria.editor.keymaps :as keymaps]
            [maria.ui :as ui]
            [yawn.hooks :as h]
            [yawn.view :as v]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Components

(def item-classes (v/classes ["flex items-center h-7 px-2 text-sm cursor-pointer rounded text-inherit visited:text-inherit no-underline"
                              "data-[disabled]:cursor-default data-[disabled]:text-zinc-400"
                              "data-[highlighted]:outline-none data-[highlighted]:bg-sky-500 data-[highlighted]:text-white"]))

(def item
  (v/from-element menu/Item {:class item-classes}))

(def icon-btn
  (v/from-element :div.cursor-pointer.p-1.flex.items-center.m-1))

(def shortcut
  (v/from-element :div.ml-auto.pl-3.tracking-widest.text-menu-muted))

(v/defview command-item
  {:key (fn [cmd]
          (if (keyword? cmd)
            (str cmd)
            (:title cmd)))}
  [cmd]
  (let [{:as cmd :keys [title active?]} (keymaps/resolve-command cmd)]
    (v/x [item {:key title
                :disabled (not active?)
                :on-click #(keymaps/run-command cmd)}
          title
          (when (:bindings cmd)
            [shortcut (keymaps/show-binding (first (:bindings cmd)))])])))

(def trigger-classes (v/classes ["px-1 h-7 bg-transparent hover:bg-zinc-200 rounded"
                                 "data-[highlighted]:bg-zinc-200"
                                 "data-[state=open]:bg-zinc-200"]))
(def trigger
  (v/from-element menu/Trigger
                  {:class trigger-classes}))

(def content
  (v/from-element menu/Content
                  {:class "MenubarContent mt-2"}))

(def separator (v/x [:el menu/Separator {:class [ui/c:divider "mx-2"]}]))

(def Trigger menu/Trigger)

(defn menu [title & children]
  (v/x
    [:el menu/Menu
     (if (string? title)
       [trigger title]
       title)
     [:el menu/Portal (into [content] children)]]))

(v/defview avatar [photo-url display-name]
  (let [initials (->> (str/split display-name #"\s+")
                      (map first)
                      (take 2)
                      str/join)]
    [:el ava/Root {:class ["inline-flex items-center justify-center align-middle"
                           "overflow-hidden select-none w-6 h-6 rounded-full bg-zinc-300"]}
     [:el ava/Image {:src photo-url}]
     [:el ava/Fallback {:delayMs 600
                        :class "text-xs font-bold text-zinc-700"} initials]]))

(def button-small-med
  (v/from-element :a
                  {:class ["rounded flex items-center px-2 py-1 shadow cursor-pointer text-sm"
                           ui/c:button-med]}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Menubar

(ui/defview doc-menu [id]
  (let [current (persist/current-file id)
        title (or (:file/name current) "Untitled.cljs")
        !initial-title (h/use-ref title)
        !editing? (h/use-state false)
        stop-editing! (fn [& _]
                        (reset! !editing? false))
        start-editing! (fn [& _]
                         (reset! !initial-title title)
                         (reset! !editing? true))
        on-value #(swap! (persist/local-ratom id) assoc :file/name %)
        cancel-editing! (fn [& _]
                          (on-value @!initial-title)
                          (stop-editing!))
        select-name! (fn [^js el]
                       (doto el
                         (.focus)
                         (.setSelectionRange 0
                                             (if-let [ext (re-find #"\..*$" title)]
                                               (- (count title) (count ext))
                                               (count title)))))

        on-keydown (h/use-memo #(ui/keydown-handler {:Escape cancel-editing!
                                                     :Meta-. cancel-editing!
                                                     :Enter stop-editing!}))
        provider (:file/provider current)]
    [:<>
     [:div.w-2.h-2.rounded-full.transition-all.delay-300
      {:class (if (or (= provider :file.provider/local)
                      (seq (persist/changes id)))
                "bg-yellow-500"
                "bg-transparent")}]
     [:el menu/Menu
      [:div.relative.flex.bg-zinc-100.border.border-zinc-200.hover:border-zinc-300.rounded.h-7
       (when @!editing?
         [:input.inset-0.absolute.z-20.p-2.pr-6.border-none
          {:value title
           :ref #(when (and @!editing? % (not (identical? % (j/get js/document :activeElement))))
                   (select-name! %))
           :on-blur stop-editing!
           :on-key-down on-keydown
           :on-change #(on-value (.. ^js % -target -value))}])
       [:el menu/Trigger
        [:span.p-2.pr-6
         {:class (when @!editing? "opacity-0 overflow-hidden z-10 relative min-w-8")}
         title]
        [:div.px-1.rounded.flex.items-center.justify-center.absolute.top-0.right-0.bottom-0.z-30
         [icons/chevron-down:mini "w-4 h-4"]]]]
      [:el menu/Portal
       [:el menu/Content {:class "MenubarContent mt-2"}
        (when (#{:file.provider/local
                 :file.provider/gist} provider)
          [item {:on-click start-editing!} "Rename"])


        [command-item :file/duplicate]
        (when (= :file.provider/gist provider)
          [:<>
           separator
           [:el menu/Item {:as-child true}
            [:a
             {:href (str "https://gist.github.com/" (:gist/id current))
              :target "_blank"
              :class item-classes}
             "View on GitHub"
             [icons/arrow-top-right-on-square:mini "w-4 h-4 ml-1"]]]])]]]]))

(defn has-selection? [el]
  (some-> (j/get el :selectionStart)
          (not= (j/get el :selectionEnd))))

(comment
  (when (= :file.provider/gist provider)
    [icons/github "w-4 h-5 mr-2"]))

(ui/defview menubar []
  (let [menubar-content @(ui/use-context ::!content)]
    [:<>
     [:div {:style {:height 40}}]
     [:div.w-100.fixed.top-0.right-0.flex.items-center.shadow.px-2.text-sm.z-50.bg-neutral-50
      {:style {:height 40
               :left (sidebar/sidebar-width)}}
      (when-not (:sidebar/visible? @ui/!state)
        [icon-btn {:on-click #(swap! ui/!state update :sidebar/visible? not)}
         [icons/bars3 "w-4 h-4"]])
      [:el Root {:class "flex flex-row w-full items-center gap-1"}
       [menu "File"
        [command-item :file/new]
        [command-item :file/duplicate]
        separator
        [command-item :file/revert]
        separator
        [command-item :file/save]
        [command-item :file/save-as-clerk-project]]
       [menu "View"
        [item {:on-click #(swap! ui/!state update :sidebar/visible? not)} "Sidebar" [shortcut "⇧⌘K"]]
        [item "Command Bar" [shortcut "⌘K"]]]
       [:div.flex-grow]
       menubar-content
       [:div.flex-grow]
       [command-bar/input]
       (if-let [{:as user :keys [photo-url display-name]} (gh/get-user)]
         [menu [:el menu/Trigger {:class [trigger-classes
                                          "rounded-full"]} [avatar photo-url display-name]]
          [command-item :account/sign-out]]
         (if (gh/any-tokens?)
           [icons/loading "w-5 h-5 opacity-30"]
           [button-small-med
            {:on-click #(gh/sign-in-with-popup!)}
            "Sign In " [:span.hidden.md:inline.pl-1 " with GitHub"]]))]]]))
