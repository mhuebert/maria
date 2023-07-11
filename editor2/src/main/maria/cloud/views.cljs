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
            [yawn.hooks :as hooks]
            [yawn.view :as v]))

(ui/defview editor [options]
  [:div.relative.notebook.my-4 {:ref (prose/use-editor options)}])

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
       [editor {:id              url
                :persisted-value text}]])))

(ui/defview gist [{:gist/keys [id]}]
  (let [{:keys                             [gist/id]
         [{:gist/keys  [filename content]}] :gist/clojure-files}
        (u/use-promise #(p/-> (u/fetch (str "https://api.github.com/gists/" id)
                                       :headers (gh/auth-headers))
                              (j/call :json)
                              gh/parse-gist)
                       [id])]
    (when content
      [:<>
       [doc-title filename]
       [editor {:id              id
                :persisted-value content}]])))
