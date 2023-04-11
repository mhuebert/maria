(ns maria.cloud.sidebar
  (:require ["@radix-ui/react-accordion" :as acc]
            [maria.cloud.routes :as routes]
            [maria.editor.icons :as icons]
            [maria.ui :as ui]
            [re-db.api :as db]))

(ui/defview content []
  (let [{current-path ::routes/path} @routes/!location]
    [:> acc/Root {:type "multiple" :defaultValue #js["curriculum"]}
     [:> acc/Item
      {:value "curriculum"
       :class ui/c:divider}
      [:> acc/Header
       {:class "flex flex-row h-[40px]"}
       [:> acc/Trigger {:class "text-sm font-bold cursor-pointer p-2 AccordionTrigger flex-grow"}
        [icons/chevron-right:mini "w-4 h-4 -ml-1 mr-1 AccordionChevron"]
        "Learn"]
       [:div.flex.items-center.justify-center.px-2.cursor-pointer.text-zinc-600.hover:text-zinc-800
        {:on-click #(swap! ui/!sidebar-state assoc :visible? false)}
        [icons/x-mark:mini "w-5 h-5 rotate-180"]]]

      (into [:> acc/Content]
            (map (fn [{:as m
                       :keys [curriculum/file-name
                              curriculum/name
                              curriculum/hash
                              title
                              description]}]
                   (let [path (routes/path-for 'maria.cloud.views/curriculum
                                               {:curriculum/name name
                                                :query {:v hash}})
                         current? (= path current-path)]
                     [:a.block.px-1.mx-1.py-1.my-1.text-sm.no-underline.rounded
                      {:key file-name
                       :href path
                       :style {:color (when current? "white")}
                       :class (if current?
                                "bg-sky-600"
                                "hover:bg-zinc-100")}
                      title]))
                 (db/where [:curriculum/name])))]]))