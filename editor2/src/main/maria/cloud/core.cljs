(ns maria.cloud.core
  (:require ["@radix-ui/react-menubar" :as menu]
            [applied-science.js-interop :as j]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [maria.cloud.routes :as routes]
            [maria.cloud.views :as views]
            [maria.editor.code-blocks.docbar :as docbar]
            [maria.editor.code-blocks.docbar]
            [maria.editor.command-bar :as command-bar]
            [maria.editor.icons :as icons]
            [maria.scratch]
            [maria.ui :as ui :refer [defview]]
            [re-db.api :as db]
            [yawn.hooks :as h]
            [yawn.root :as root]
            [yawn.view :as v]))

;; TODO
;; - UI for sidebar,
;; - support per-attribute local-state persistence
;; - include curriculum as a re-db transaction,
;; -

(defn get-scripts [type]
  (->> (js/document.querySelectorAll (str "[type='" type "']"))
       (map (comp edn/read-string (j/get :innerHTML)))))

(defn init-re-db []
  (doseq [schema (get-scripts "application/re-db:schema")]
    (db/merge-schema! schema))
  (doseq [tx (get-scripts "application/re-db:tx")]
    (db/transact! tx)))

(defn link [title href]
  [:a (cond-> {:href href}
              (str/starts-with? title "http")
              (assoc :target "_blank")) title])


(defn menu:item [& label]
  (j/lit
   [menu/Item {:className (str "flex items-center px-2 py-1 my-1 text-sm cursor-pointer rounded "
                               "data-[highlighted]:outline-none data-[highlighted]:bg-sky-500 data-[highlighted]:text-white")} ~@label]))

(def menu:separator (j/lit [menu/Separator {:className (str ui/divider-classes " mx-2")}]))

(def menu:trigger-class
  (str "px-2 py-1 my-[2px] bg-transparent cursor-pointer rounded "
       "data-[highlighted]:bg-zinc-50 "
       "data-[state=open]:bg-zinc-50 "))

(defview menubar []
  [:<>
   [:div {:style {:height 40}}]
   [:div.w-100.fixed.top-0.right-0.flex.items-center.shadow.px-2
    {:style {:height 40
             :left (ui/sidebar-width)}}

    [:div.cursor-pointer.p-1.flex.items-center.m-1 {:on-click #(swap! ui/!sidebar-state update :visible? not)}
     [icons/bars3 "w-4 h-4"]]
    [:> menu/Root {:class "text-sm"}
     [:> menu/Menu
      [:> menu/Trigger {:class menu:trigger-class} "File"]
      [:> menu/Portal
       [:> menu/Content {:class "MenubarContent mt-[4px]"}
        [menu:item "New" [:div.RightSlot "⌘ N"]]
        [menu:item "Duplicate"]
        menu:separator
        [menu:item "Revert"]
        menu:separator
        [menu:item "Save"]]]]]

    ]])

(defview root []
  (let [{:as location ::routes/keys [view]} (h/use-deref routes/!location)]
    [:<>
     (ui/use-global-keymap {:mod-k (fn [& _] (command-bar/toggle!))
                            :shift-mod-k (fn [& _] (swap! ui/!sidebar-state update :visible? not))})
     [command-bar/view]
     [ui/with-sidebar
      [views/sidebar-content]
      [:div
       [menubar]
       [view location]]]
     [docbar/view]]))

(defn ^:export init []
  (init-re-db)
  (routes/init)
  (root/create :maria-live (v/x [root])))
