(ns maria.cloud.menubar
  (:require ["@radix-ui/react-menubar" :as menu :refer [Item Separator Root Menu Trigger Portal Content]]
            ["@radix-ui/react-avatar" :as ava]
            [clojure.string :as str]
            [maria.cloud.github :as gh]
            [maria.cloud.sidebar :as sidebar]
            [maria.editor.command-bar :as command-bar]
            [maria.editor.icons :as icons]
            [maria.editor.keymaps :as keymaps]
            [maria.ui :as ui]
            [yawn.view :as v]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Components

(def item
  (v/from-element menu/Item
                  {:class ["flex items-center px-2 py-1 text-sm cursor-pointer rounded"
                           "data-[disabled]:cursor-default data-[disabled]:text-zinc-400"
                           "data-[highlighted]:outline-none data-[highlighted]:bg-sky-500 data-[highlighted]:text-white"]}))

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
  (let [{:as cmd :keys [title active?]} (command-bar/resolve-command cmd)]
    (v/x [item {:key      title
                :disabled (not active?)}
          title
          (when (:bindings cmd)
            [shortcut (keymaps/show-binding (first (:bindings cmd)))])])))

(def trigger
  (v/from-element menu/Trigger
                  {:class ["px-1 h-7 bg-transparent hover:bg-zinc-200 rounded"
                           "data-[highlighted]:bg-zinc-200"
                           "data-[state=open]:bg-zinc-200"]}))

(def content
  (v/from-element menu/Content
                  {:class "MenubarContent mt-2"}))

(def separator (v/x [:el menu/Separator {:class [ui/c:divider "mx-2"]}]))

(defn menu [title & children]
  (v/x
    [:el menu/Menu
     [trigger title]
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
                        :class   "text-xs font-bold text-zinc-700"} initials]]))

(def button-small-med
  (v/from-element :a
                  {:class ["rounded flex items-center px-2 py-1 shadow cursor-pointer text-sm"
                           ui/c:button-med]}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Menubar

(ui/defview menubar []
  [:<>
   [:div {:style {:height 40}}]
   [:div.w-100.fixed.top-0.right-0.flex.items-center.shadow.px-2.text-sm.z-50.bg-neutral-50
    {:style {:height 40
             :left   (sidebar/sidebar-width)}}
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
      [command-item :clerkify]]
     [menu "View"
      [item {:on-click #(swap! ui/!state update :sidebar/visible? not)} "Sidebar" [shortcut "⇧⌘K"]]
      [item "Command Bar" [shortcut "⌘K"]]]
     [:div.flex-grow]
     [:div#menubar-title]
     [:div.flex-grow]
     [command-bar/input]
     (if-some [{:as user :keys [photo-url display-name]} @gh/!user]
       [menu [avatar photo-url display-name]
        [command-item :account/sign-out]]
       [button-small-med
        {:on-click #(gh/sign-in-with-popup!)}
        "Sign In " [:span.hidden.md:inline.pl-1 " with GitHub"]])]]])

(defn title! [content]
  (v/portal :menubar-title (v/x content)))
