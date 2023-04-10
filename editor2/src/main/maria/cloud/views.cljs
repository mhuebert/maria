(ns maria.cloud.views
  (:require ["@radix-ui/react-accordion" :as acc]
            [applied-science.js-interop :as j]
            [maria.editor.core :as prose]
            [maria.editor.icons :as icons]
            [maria.ui :as ui]
            [promesa.core :as p]
            [re-db.api :as db]
            [maria.cloud.routes :as routes]
            [yawn.hooks :as h]))

(defn use-fetch-text
  "Uses browser's fetch api to request url"
  [url]
  (let [[v v!] (h/use-state nil)]
    (p/catch (h/use-effect #(p/-> (js/fetch url
                                            (j/lit {:method "GET"
                                                    :headers {"Content-Type" "text/plain"}}))
                                  (j/call :text)
                                  v!)
                           [url])
             v!)
    v))


(ui/defview curriculum
  {:key ::routes/path}
  [{:as props :curriculum/keys [name]}]
  (let [{:curriculum/keys [hash file-name]} (db/get [:curriculum/name name])
        path (str "/curriculum/" file-name
                  "?v="
                  (db/get [:curriculum/name name]
                          :curriculum/hash))
        text (try (use-fetch-text path)
                  (catch js/Error e (js/console.error e)))]
    (when text
      [prose/editor {:initial-value text}])))

(ui/defview sidebar-content []
  (let [{current-path ::routes/path} @routes/!location]
    [:> acc/Root {:type "multiple" :defaultValue #js["curriculum"]}
     [:> acc/Item
      {:value "curriculum"
       :class ui/divider-classes}
      [:> acc/Header
       [:> acc/Trigger {:class "text-sm font-bold cursor-pointer p-2 AccordionTrigger"}
        [icons/chevron-right:mini "w-4 h-4 -ml-1 AccordionChevron"]
        "Learn"]]

      (into [:> acc/Content]
            (map (fn [{:as m
                       :keys [curriculum/file-name
                              curriculum/name
                              title
                              description]}]
                   (let [path (routes/path-for 'maria.cloud.views/curriculum
                                               {:curriculum/name name})]
                     [:a.block.px-1.mx-1.py-1.my-1.text-sm.no-underline.rounded
                      {:key file-name
                       :href path
                       :class (if (= path current-path)
                                "bg-sky-600 visited:text-white text-white"
                                "hover:bg-zinc-100 visited:text-black")}
                      title]))
                 (db/where [:curriculum/name])))]]))