(ns maria.cloud.views
  (:require [applied-science.js-interop :as j]
            [maria.cloud.routes :as routes]
            [maria.editor.core :as prose]
            [maria.ui :as ui]
            [promesa.core :as p]
            [re-db.api :as db]
            [yawn.hooks :as h]
            [maria.cloud.github :as github]))

(defn use-fetch-text
  "Uses browser's fetch api to request url"
  [url & {:as opts :keys [headers]}]
  (let [[v v!] (h/use-state nil)]
    (p/catch (h/use-effect #(p/-> (js/fetch url
                                            (j/lit {:method "GET"
                                                    :headers (j/extend! #js {"Content-Type" "text/plain"}
                                                               (clj->js headers))}))
                                  (j/call :text)
                                  v!)
                           [url])
             v!)
    v))


(ui/defview curriculum
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

(ui/defview gist [{:gist/keys [id]}]
  (let [text (use-fetch-text (str "https://api.github.com/gists/" id)
                             :headers {"Authorization"
                                       (str "Bearer " (github/token))})]
    (when text
      [prose/editor {:initial-value text}])))

