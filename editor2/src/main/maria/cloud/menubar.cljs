(ns maria.cloud.menubar
  (:require ["@radix-ui/react-menubar" :as menu :refer [Item Separator Root Menu Trigger Portal Content]]
            ["@radix-ui/react-avatar" :as ava]
            [applied-science.js-interop :as j]
            [clojure.string :as str]
            [maria.editor.icons :as icons]
            [maria.cloud.github :as gh]
            [maria.ui :as ui]))

(defn item [props & label]
  (let [[props body] (if (map? props) [props label] [nil (cons props label)])]
    (into [:> Item
           (update props :class str
                   "flex items-center px-2 py-1 my-1 text-sm cursor-pointer rounded "
                   "data-[disabled]:cursor-default data-[disabled]:text-zinc-400 "
                   "data-[highlighted]:outline-none data-[highlighted]:bg-sky-500 data-[highlighted]:text-white")]
          body)))

(def separator (j/lit [Separator {:className (str ui/c:divider " mx-2")}]))

(def menu:trigger-class
  (str "px-2 py-1 my-[2px] bg-transparent hover:bg-zinc-50 rounded "
       "data-[highlighted]:bg-zinc-50 "
       "data-[state=open]:bg-zinc-50 "))

(defn icon-btn [props & children]
  (ui/x
   (into [:div.cursor-pointer.p-1.flex.items-center.m-1 props] children)))

(defn menu [title & children]
  [:> Menu
   [:> Trigger {:class menu:trigger-class} title]
   [:> Portal
    (into [:> Content {:class "MenubarContent mt-[4px]"}] children)]])

(defn shortcut [ks]
  (ui/x [:div.RightSlot.text-zinc-500.tracking-widest ks]))

(ui/defview menubar []
  [:<>
   [:div {:style {:height 40}}]
   [:div.w-100.fixed.top-0.right-0.flex.items-center.shadow.px-2.text-sm
    {:style {:height 40
             :left (ui/sidebar-width)}}
    (when-not (:visible? @ui/!sidebar-state)
      [icon-btn {:on-click #(swap! ui/!sidebar-state update :visible? not)}
       [icons/bars3 "w-4 h-4"]])
    [:> Root {:class "flex flex-row w-full"}
     [menu "File"
      [item "New" [shortcut "⌘N"]]
      [item "Duplicate"]
      separator
      [item "Revert"]
      separator
      [item {:disabled (not (gh/token))} "Save" [shortcut "⌘S"]]]
     [menu "View"
      [item "Sidebar" [shortcut "⇧⌘K"]]
      [item "Command Bar" [shortcut "⌘K"]]]
     [:div.flex-grow]
     (when-some [{:as user :keys [photo-url display-name]} @gh/!user]
       (if user
         [:> Menu
          [:> Trigger {:class "cursor-pointer"}
           [:> ava/Root {:class ["inline-flex items-center justify-center align-middle"
                                 "overflow-hidden select-none w-7 h-6 rounded bg-zinc-300"]}
            [:> ava/Image {:src photo-url}]
            [:> ava/Fallback {:delayMs 600
                              :class "text-xs font-bold text-zinc-700"}
             (->> (str/split display-name #"\s+") (map first) (take 2) str/join)]]]
          [:> Portal
           [:> Content {:class "MenubarContent mt-[4px]"}
            [item {:on-click #(gh/sign-out)} "Sign Out"]]]]
         [:a
          {:on-click #(gh/sign-in-with-popup!)
           :class ["rounded flex items-center px-2 shadow cursor-pointer text-sm "
                   ui/c:button-dark]}
          [:div.mr-1 [icons/arrow-left-on-rect:mini "w-4 h-4"]]
          "Sign In with GitHub"]))]]])