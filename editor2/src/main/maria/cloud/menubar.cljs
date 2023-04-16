(ns maria.cloud.menubar
  (:require ["@radix-ui/react-menubar" :as menu :refer [Item Separator Root Menu Trigger Portal Content]]
            ["@radix-ui/react-avatar" :as ava]
            [clojure.string :as str]
            [maria.cloud.sidebar :as sidebar]
            [maria.editor.icons :as icons]
            [maria.cloud.github :as gh]
            [maria.ui :as ui]
            [yawn.view :as v]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Components

(def item
  (v/from-element menu/Item
    {:class ["flex items-center px-2 py-1 my-1 text-sm cursor-pointer rounded"
             "data-[disabled]:cursor-default data-[disabled]:text-zinc-400"
             "data-[highlighted]:outline-none data-[highlighted]:bg-sky-500 data-[highlighted]:text-white"]}))

(def icon-btn
  (v/from-element :div.cursor-pointer.p-1.flex.items-center.m-1))

(def shortcut
  (v/from-element :div.RightSlot.text-zinc-500.tracking-widest))

(def trigger
  (v/from-element menu/Trigger
    {:class ["px-2 py-1 my-[2px] bg-transparent hover:bg-zinc-50 rounded"
             "data-[highlighted]:bg-zinc-50"
             "data-[state=open]:bg-zinc-50"]}))

(def content
  (v/from-element menu/Content
    {:class "MenubarContent mt-[4px]"}))

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
                           "overflow-hidden select-none w-7 h-6 rounded bg-zinc-300"]}
     [:el ava/Image {:src photo-url}]
     [:el ava/Fallback {:delayMs 600
                        :class "text-xs font-bold text-zinc-700"} initials]]))

(def button-small-dark
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
             :left (sidebar/sidebar-width)}}
    (when-not (:sidebar/visible? @ui/!state)
      [icon-btn {:on-click #(swap! ui/!state update :sidebar/visible? not)}
       [icons/bars3 "w-4 h-4"]])
    [:el Root {:class "flex flex-row w-full items-center"}
     [menu "File"
      [item "New" [shortcut "⌘N"]]
      [item "Duplicate"]
      separator
      [item "Revert"]
      separator
      [item {:disabled (not (gh/token))} "Save" [shortcut "⌘S"]]]
     [menu "View"
      [item {:on-click #(swap! ui/!state update :sidebar/visible? not)} "Sidebar" [shortcut "⇧⌘K"]]
      [item "Command Bar" [shortcut "⌘K"]]]
     [:div.flex-grow]
     [:div#menubar-title]
     [:div.flex-grow]
     (when-some [{:as user :keys [photo-url display-name]} @gh/!user]
       (if user
         [:el Menu
          [:el Trigger {:class "cursor-pointer"}
           [avatar photo-url display-name]]
          [:el Portal
           [:el Content {:class "MenubarContent mt-[4px]"}
            [item {:on-click #(gh/sign-out)} "Sign Out"]]]]
         [button-small-dark
          {:on-click #(gh/sign-in-with-popup!)}
          "Sign In " [:span.hidden.md:inline.pl-1 " with GitHub"]]))]]])

(defn title! [content]
  (v/portal :menubar-title (v/x content)))