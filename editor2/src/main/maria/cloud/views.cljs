(ns maria.cloud.views
  (:require [applied-science.js-interop :as j]
            [maria.cloud.github :as gh]
            [maria.cloud.github :as github]
            [maria.editor.core :as prose]
            [maria.editor.util :as u]
            [maria.ui :as ui]
            [promesa.core :as p]
            [re-db.api :as db]
            [yawn.hooks :as h]))


(ui/defview curriculum
  [{:as props :curriculum/keys [name]}]
  (let [{:curriculum/keys [hash file-name]} (db/get [:curriculum/name name])
        url (str "/curriculum/" file-name
                 "?v="
                 (db/get [:curriculum/name name]
                         :curriculum/hash))
        text (u/use-promise #(p/-> (u/fetch url) (j/call :text)) [url])]
    (when text
      [prose/editor {:initial-value text
                     :title (str "curriculum / " file-name)
                     :id url}])))

(ui/defview gist [{:gist/keys [id]}]
  (let [{:keys [gist/id]
         [{:gist/keys [filename content]}] :gist/clojure-files}
        (u/use-promise #(p/-> (u/fetch (str "https://api.github.com/gists/" id)
                                       :headers (gh/auth-headers))
                              (j/call :json)
                              gh/parse-gist)
                       [id])]
    (when content
      [prose/editor {:id id
                     :title filename
                     :initial-value content}])))

