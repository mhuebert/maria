(ns maria.cloud.sidebar
  (:require ["@radix-ui/react-accordion" :as acc]
            [applied-science.js-interop :as j]
            [maria.cloud.github :as gh]
            [maria.cloud.routes :as routes]
            [maria.editor.icons :as icons]
            [maria.editor.util :as u]
            [maria.ui :as ui]
            [promesa.core :as p]
            [re-db.api :as db]
            [re-db.reactive :as r]
            [re-db.hooks :as hooks]
            [yawn.view :as v]))

(r/redef !state (r/atom {:visible? true
                         :width 250
                         :transition "all 0.2s ease 0s"}))

(defn sidebar-width []
  (let [{:keys [visible? width transition]} @!state]
    (if visible? width 0)))

(ui/defview with-sidebar [sidebar content]
  (let [{:keys [visible? width transition]} (hooks/use-deref !state)]
    [:div
     {:style {:padding-left (if visible? width 0)
              :transition transition}}
     [:div.fixed.top-0.bottom-0.bg-white.rounded.z-10.drop-shadow-md.divide-y.overflow-hidden.border-r.border-zinc-100.flex
      {:style {:width width
               :transition transition
               :left (if visible? 0 (- width))}}
      [:div.flex-grow.overflow-y-auto sidebar]]
     content]))

(def acc-item (v/from-element :a.block.px-2.mx-1.py-1.my-1.text-sm.no-underline.rounded))

(defn acc-props [current? props]
  (merge {:style {:color (when current? "white")}
          :class (if current?
                   "bg-sky-600"
                   "hover:bg-zinc-100")}
         props))

(ui/defview acc-section [title items]
  [:> acc/Item
   {:value title
    :class ui/c:divider}
   [:> acc/Header
    {:class "flex flex-row h-[40px]"}
    [:> acc/Trigger {:class "text-sm font-bold cursor-pointer p-2 AccordionTrigger flex-grow"}
     [icons/chevron-right:mini "w-4 h-4 -ml-1 mr-1 AccordionChevron"]
     title]]
   (into [:el acc/Content] items)])

(ui/defview user-gist-list [{:keys [username current-path]}]
  (let [gists (u/use-promise #(p/-> (u/fetch (str "https://api.github.com/users/" username "/gists")
                                             :headers (merge {:Content-Type "text/plain"}
                                                             (gh/auth-headers)))
                                    (j/call :json))
                             [username])]
    [acc-section "My Gists"
     (for [{:gist/keys [id description clojure-files]} (keep gh/parse-gist gists)]
       (v/x [acc-item {:href (routes/path-for 'maria.cloud.views/gist
                                              {:gist/id id})
                       :key id}
             (or (u/guard description seq) (:gist/filename (first clojure-files)))]))]))

(defn curriculum-list [current-path]
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
           (v/x [acc-item
                 (acc-props current?
                            {:key file-name
                             :href path})
                 title])))
       (db/where [:curriculum/name])))


(ui/defview content []
  (let [{current-path ::routes/path} @routes/!location]
    [:> acc/Root {:type "multiple" :defaultValue #js["curriculum"] :class "relative"}
     [:div.flex.items-center.justify-center.p-2.cursor-pointer.text-zinc-500.hover:text-zinc-700.absolute.top-0.right-0
      {:on-click #(swap! !state assoc :visible? false)
       :style {:margin-top 3}}
      [icons/x-mark:mini "w-5 h-5 rotate-180"]]
     [acc-section "Learn"
      (curriculum-list current-path)]
     (when-let [username (:username @gh/!user)]
       (user-gist-list {:username username
                        :current-path current-path}))]))