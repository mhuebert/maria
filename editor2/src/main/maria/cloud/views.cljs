(ns maria.cloud.views
  (:require [applied-science.js-interop :as j]
            [maria.cloud.github :as gh]
            [maria.cloud.menubar :as menubar]
            [maria.editor.core :as prose]
            [maria.editor.icons :as icons]
            [maria.editor.util :as u]
            [maria.ui :as ui]
            [promesa.core :as p]
            [re-db.api :as db]
            [yawn.hooks :as hooks]))

(ui/defview editor [options]
  (let [!element-ref (hooks/use-ref nil)]
    (hooks/use-effect
     (fn []
       (when-let [element @!element-ref]
         (prose/init options element)))
     [@!element-ref (:initial-value options)])
    [:div.relative.notebook {:ref !element-ref}]))

(defn doc-title [title]
  (menubar/title!
   [:div.m-1.px-3.py-1.bg-zinc-100.border.border-zinc-200.rounded
    title
    [icons/chevron-down:mini "w-4 h-4 ml-1 -mr-1 text-zinc-500"]]))

(ui/defview curriculum
  [{:as props :curriculum/keys [name]}]
  (let [{:curriculum/keys [hash file-name]} (db/get [:curriculum/name name])
        url (str "/curriculum/" file-name "?v=" hash)
        text (u/use-promise #(p/-> (u/fetch url) (j/call :text)) [url])]
    (when text
      [:<>
       (doc-title (str "curriculum / " name))
       [editor {:initial-value text
                :id url}]])))

(ui/defview gist [{:gist/keys [id]}]
  (let [{:keys [gist/id]
         [{:gist/keys [filename content]}] :gist/clojure-files}
        (u/use-promise #(p/-> (u/fetch (str "https://api.github.com/gists/" id)
                                       :headers (gh/auth-headers))
                              (j/call :json)
                              gh/parse-gist)
                       [id])]
    (when content
      [:<>
       (doc-title filename)
       [editor {:id id
                :initial-value content}]])))

