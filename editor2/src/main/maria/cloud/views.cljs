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
        [url text] (try (u/use-fetch url :then (j/call :text))
                        (catch js/Error e (js/console.error e)))]
    (when text
      [prose/editor {:initial-value text
                     :title (str "curriculum / " file-name)
                     :id url}])))

(ui/defview gist [{:gist/keys [id]}]
  (let [text (u/use-fetch (str "https://api.github.com/gists/" id)
                          :headers (gh/auth-headers))]
    (when text
      [prose/editor {:initial-value text}])))

