(ns maria.cloud.views
  (:require [applied-science.js-interop :as j]
            [maria.cloud.routes :as routes]
            [maria.editor.core :as prose]
            [maria.ui :as ui]
            [promesa.core :as p]
            [re-db.api :as db]
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

