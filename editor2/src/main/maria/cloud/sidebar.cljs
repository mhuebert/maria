(ns maria.cloud.sidebar
  (:require ["@radix-ui/react-accordion" :as acc]
            [applied-science.js-interop :as j]
            [clojure.string :as str]
            [maria.cloud.github :as gh]
            [maria.cloud.persistence :as persist]
            [maria.cloud.routes :as routes]
            [maria.editor.icons :as icons]
            [maria.editor.util :as u]
            [maria.ui :as ui]
            [promesa.core :as p]
            [re-db.api :as db]
            [re-db.hooks :as hooks]
            [yawn.view :as v]))

(defn sidebar-width []
  (let [{:sidebar/keys [visible? width]} @ui/!state]
    (if visible? width 0)))

(ui/defview with-sidebar [sidebar content]
  (let [{:sidebar/keys [visible? width]} (hooks/use-deref ui/!state)]
    [:div
     {:style {:padding-left (if visible? width 0)
              :transition ui/sidebar-transition}}
     [:div.fixed.top-0.bottom-0.bg-white.rounded.z-10.drop-shadow-md.divide-y.overflow-hidden.border-r.border-zinc-100.flex
      {:style {:width width
               :transition ui/sidebar-transition
               :left (if visible? 0 (- width))}}
      [:div.flex-grow.overflow-y-auto sidebar]]
     content]))

(def acc-item (v/from-element :a.block
                              {:class ["text-sm text-black no-underline"
                                       "pr-2 pl-4 mx-1 py-1 rounded"
                                       "hover:bg-black/5 hover:text-black visited:text-black"
                                       "data-[selected=true]:bg-sky-500 data-[selected=true]:text-white"]}))

(defn acc-props [current? props]
  (merge {:data-selected current?}
         props))

(ui/defview acc-section [title items]
  [:> acc/Item
   {:value title
    :class ui/c:divider}
   [:> acc/Header
    {:class "flex flex-row h-[40px] m-0"}
    [:> acc/Trigger {:class "text-sm font-bold cursor-pointer p-2 AccordionTrigger flex-grow"}
     [icons/chevron-right:mini "w-4 h-4 -ml-1 AccordionChevron"]
     title]]
   (into [:el.flex.flex-col.gap-1 acc/Content] items)])

(ui/defview recents [current-path]
  (when-let [recents (seq @persist/!recents)]
    [acc-section "Recently Viewed"
     (for [{:keys [maria/path file/id file/title]} recents]
       [acc-item (acc-props (= current-path path)
                            {:key id
                             :href path})
        title])]))

(ui/defview user-gist-list [{:keys [username current-path]}]
  (let [gists (u/use-promise #(p/-> (u/fetch (str "https://api.github.com/users/" username "/gists")
                                             :headers (merge {:Content-Type "text/plain"}
                                                             (gh/auth-headers)))
                                    (j/call :json))
                             [username])]
    [acc-section "My Gists"
     (for [{:keys [file/name :gist/id :gist/description]} (keep gh/parse-gist gists)]
       (v/x [acc-item {:href (routes/path-for 'maria.cloud.views/gist
                                              {:gist/id id})
                       :key id}
             (or (some-> description
                         str/trim
                         (str/split-lines)
                         first
                         (u/guard (complement str/blank?)))
                 name)]))]))

(ui/defview curriculum-list [current-path]
  [acc-section "Curriculum"
   (map (fn [{:as m
              :keys [curriculum/name
                     file/hash
                     file/title]}]
          (let [path (routes/path-for 'maria.cloud.views/curriculum
                                      {:curriculum/name name
                                       :query {:v hash}})
                current? (= path current-path)]
            (v/x [acc-item
                  (acc-props current?
                             {:key name
                              :href path})
                  title])))
        (db/where [:curriculum/name]))])

(defn icon:home [class]
  [:svg {:class class :xmlns "http://www.w3.org/2000/svg" :viewBox "0 0 20 20" :fill "currentColor"}
   [:path {:fillRule "evenodd" :d "M9.293 2.293a1 1 0 011.414 0l7 7A1 1 0 0117 11h-1v6a1 1 0 01-1 1h-2a1 1 0 01-1-1v-3a1 1 0 00-1-1H9a1 1 0 00-1 1v3a1 1 0 01-1 1H5a1 1 0 01-1-1v-6H3a1 1 0 01-.707-1.707l7-7z" :clipRule "evenodd"}]])

(ui/defview content []
  (let [{current-path ::routes/path} @routes/!location]
    [:> acc/Root {:type "multiple"
                  :value (-> @ui/!state :sidebar/open to-array)
                  :on-value-change #(swap! ui/!state assoc :sidebar/open (set %))
                  :class "relative"}

     [:div {:class "flex flex-row h-[40px] items-center border-b border-zinc-100"}
      [:div.flex.items-center.justify-center.py-2.px-3.cursor-pointer.text-zinc-500.hover:text-zinc-700
       {:on-click #(swap! ui/!state assoc :sidebar/visible? false)
        :style {:margin-top 3}}
       [icons/x-mark:mini "w-5 h-5 rotate-180"]]
      [:div.flex-grow]
      [:a.p-2.hover:bg-slate-300.cursor-pointer.text-black {:href "/"} [icon:home "w-4 h-4"]]]

     [recents current-path]
     [curriculum-list current-path]

     (when-let [username (:username (gh/get-user))]
       (user-gist-list {:username username
                        :current-path current-path}))]))