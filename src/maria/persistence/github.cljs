(ns maria.persistence.github
  (:require [goog.net.XhrIo :as xhr]
            [maria.persistence.tokens :as tokens]
            [re-db.d :as d]
            [re-view-routing.core :as r]
            [maria.persistence.local :as local]
            [maria.frame-communication :as frame]))

(defn gist-person [{:keys [html_url url id login gists_url] :as person}]
  {:html-url  (str "https://gist.github.com/" login)
   :index-url (str "/gists/" login)
   :username  login
   :id        id})

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
  [{:keys [files title]}]
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

(defn patch-gist [id data]
  (xhr/send (str "https://api.github.com/gists/" id)
            (fn [e]
              (let [target (.-target e)]
                (if (.isSuccess target)
                  (do
                    (prn (-> (.getResponseJson target)
                             (js->clj :keywordize-keys true)
                             (gist->project)))
                    (d/transact! [[:db/add id :persisted (-> (.getResponseJson target)
                                                             (js->clj :keywordize-keys true)
                                                             (gist->project))]]))
                  (prn :error-saving-gist (.getLastError target)))))
            "PATCH"
            (->> data (clj->js) (.stringify js/JSON))
            (tokens/auth-headers :github)))

(defn fork-gist [child-id gist-id]
  (xhr/send (str "https://api.github.com/gists/" gist-id "/forks")
            (fn [e]
              (let [target (.-target e)]
                (if (.isSuccess target)
                  (let [{new-id :id
                         :as    gist-data} (-> (.getResponseJson target)
                                               (js->clj :keywordize-keys true))]
                    (frame/send child-id [:db/copy-local gist-id new-id])
                    (d/transact! [{:db/id     new-id
                                   :persisted (gist->project gist-data)}])
                    (r/nav! (str "/gist/" new-id)))
                  (prn :error-saving-gist (.getLastError target)))))
            "POST"
            nil
            (tokens/auth-headers :github)))