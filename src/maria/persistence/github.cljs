(ns maria.persistence.github
  (:require [goog.net.XhrIo :as xhr]
            [maria.persistence.tokens :as tokens]
            [re-db.d :as d]))

(defn gist-person [{:keys [html_url url login gists_url] :as person}]
  {:html-url  (str "https://gist.github.com/" login)
   :index-url (str "/gists/" login)
   :username  login})

(defn gist->project
  "Convert a gist to local project format"
  [{:keys [description files owner html_url id] :as gist-data}]
  {:title                description
   :id                   id
   :owner                (gist-person owner)
   :html-url             html_url
   :persistence/provider :gist
   :files                (->> files
                              (reduce-kv (fn [m filename {:keys [language]
                                                          :as   file}]
                                           (cond-> m
                                                   (= language "Clojure")
                                                   (assoc (name filename) (select-keys file [:filename :truncated :content])))) {}))})

(defn project->gist
  "Convert a project to gist format, for persistence"
  [{:keys           [title]
    {:keys [files]} :local}]
  (cond->
    {:files (reduce-kv (fn [m filename file]
                         (assoc m filename (select-keys file [:filename :content]))) {} files)}
    title (assoc :description title)))

(defn get-gist [id cb]
  (xhr/send (str "https://api.github.com/gists/" id)
            (fn [e]
              (let [target (.-target e)]
                (if (.isSuccess target)
                  (cb {:value (gist->project (js->clj (.getResponseJson target) :keywordize-keys true))})
                  (cb {:error (.getLastError target)}))))
            "GET"
            nil
            (tokens/auth-headers :github)))

(defn load-gist [id]
  (d/transact! [{:db/id           id
                 :loading-message "Loading gist..."}])
  (get-gist id (fn [{:keys [value error]}]
                 (d/transact! [[:db/add id :persisted (or value {:error error})]
                               [:db/add id :loading-message false]]))))

(defn get-user-gists [username cb]
  (xhr/send (str "https://api.github.com/users/" username "/gists")
            (fn [e]
              (let [target (.-target e)]
                (if (.isSuccess target)
                  (cb {:value (->> (js->clj (.getResponseJson target) :keywordize-keys true)
                                   (map gist->project)
                                   (filter #(> (count (:files %)) 0)))})
                  (cb {:error (.getLastError target)}))))
            "GET"
            nil
            (tokens/auth-headers :github)))

(defn load-user-gists [username]
  (d/transact! [{:db/id           username
                 :loading-message "Loading gists..."}])
  (get-user-gists username (fn [{:keys [value error]}]
                             (d/transact! (if value
                                            [[:db/add username :gists value]
                                             [:db/add username :loading-message false]]
                                            [{:error error}])))))

(defn gist-patch [id data]
  (xhr/send (str "https://api.github.com/gists/" id)
            (fn [e]
              (let [target (.-target e)]
                (if (.isSuccess target)
                  (prn (-> (.getResponseJson target)
                           (js->clj :keywordize-keys true)
                           (gist->project)))
                  (prn :error-saving-gist (.getLastError target)))))
            "PATCH"
            (clj->js data)))