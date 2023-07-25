(ns maria.cloud.views
  (:require [applied-science.js-interop :as j]
            [clojure.string :as str]
            [maria.cloud.github :as gh]
            [maria.cloud.routes :as routes]
            [maria.editor.core :as editor.core]
            [maria.editor.util :as u]
            [maria.ui :as ui]
            [promesa.core :as p]
            [re-db.api :as db]
            [yawn.hooks :as h]
            [maria.cloud.persistence :as persist]
            [maria.editor.code.show-values :as show]))

(defn use-persisted-file
  "Syncs persisted file to re-db"
  [id v]
  (h/use-effect (fn []
                  (when (and id v)
                    (reset! (persist/persisted-ratom id) v)))
                [id v]))

(comment
  ;; this was one approach to "exporting" a cell as a webview.
  ;; would rather try something else: let a user 'maximize' a cell
  ;; and then share a link to it (retaining possibility of minimizing it)
  (if-let [eval (:eval (::routes/query-params params))]
    [show-expr source eval])
  (ui/defview show-expr [source expr]
    (let [ctx (h/use-memo maria.editor.code.sci/initial-context)
          result (u/use-promise #(p/let [results (editor.core/prose:eval-clojure!
                                                   ctx
                                                   (editor.core/clj->doc (str source
                                                                              \newline
                                                                              expr)))]
                                   (last results))
                                [source expr])]
      [show/show {:sci/context ctx
                  :sci/get-ns #(deref (:last-ns ctx))}
       (or (:error result)
           (:value result))])))

(ui/defview curriculum
  [{:as props :curriculum/keys [name]}]
  (let [{:as file
         :keys [file/id
                file/url]} (db/get [:curriculum/name name])
        source (u/use-promise #(p/-> (u/fetch url)
                                     (j/call :text))
                              name)]
    (use-persisted-file id (assoc file :file/source source))
    (when source
      [editor.core/editor {:id id
                           :default-value (or (:file/source @(persist/local-ratom id))
                                              source)}])))

(ui/defview gist
  {:key :gist/id}
  [{gist-id :gist/id}]
  (let [{:keys [file/id file/source] :as file} (u/use-promise #(p/-> (u/fetch (str "https://api.github.com/gists/" gist-id)
                                                                              :headers (gh/auth-headers))
                                                                     (j/call :json)
                                                                     gh/parse-gist)
                                                              [gist-id])]
    (use-persisted-file id file)
    (when source
      [editor.core/editor {:id id
                           :default-value (or (:file/source @(persist/local-ratom id))
                                              source)}])))

(defn local-file [id]
  {:file/id (str "local:" id)
   :file/provider :file.provider/local})

(ui/defview local [{:keys [local/id]}]
  [editor.core/editor {:id id
                       :default-value (or (:file/source @(persist/local-ratom id)) "")}])

(ui/defview http-text [{:keys [url]}]
  (let [url (cond-> url
                    (str/includes? url "%2F")
                    (js/decodeURIComponent))
        url (cond->> url
                     (not (str/starts-with? url "http"))
                     (str "https://"))
        source (u/use-promise #(p/-> (u/fetch url)
                                     (j/call :text))
                              [url])]
    (use-persisted-file url {:file/source source
                             :file/id url})
    (when source
      [editor.core/editor {:id url
                           :default-value (or (:file/source @(persist/local-ratom url))
                                              source)}])))
