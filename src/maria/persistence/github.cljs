(ns maria.persistence.github
  (:require [goog.net.XhrIo :as xhr]
            [goog.object :as gobj]
            [maria.persistence.tokens :as tokens]
            [re-db.d :as d]
            [maria.curriculum :as curriculum]))

(defn send [url cb & args]
  (d/transact! [[:db/update-attr :remote/status :in-progress (fnil inc 0)]])
  (.apply xhr/send nil (to-array (concat [(str url "?ts=" (.now js/Date))
                                          (fn [e]
                                            (d/transact! [[:db/update-attr :remote/status :in-progress dec]])
                                            (cb e))] args)) args))

(defn gist-person [{:keys [html_url url id login gists_url] :as person}]
  {:username  login
   :maria-url (str "/gists/" login)
   :id        (str id)})

(defn gist->project
  "Convert a gist to local project format"
  [{:keys [description files owner html_url id] :as gist-data}]
  {:description          description
   :id                   id
   :owner                (if (curriculum/modules-by-id id)
                           curriculum/modules-owner
                           (gist-person owner))
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
  [{:keys [files description]}]
  (cond->
    {:files (reduce-kv (fn [m filename file]
                         (assoc m filename (select-keys file [:filename :content]))) {} files)}
    description (assoc :description description)))

(defn get-gist [id cb]
  (send (str "https://api.github.com/gists/" id)
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
                 (d/transact! [{:db/id           id
                                :persisted       value
                                :persisted-error error
                                :loading-message false}]))))

(defn get-user-gists [username cb]
  (send (str "https://api.github.com/users/" username "/gists")
        (fn [e]
          (let [target (.-target e)]
            (if (.isSuccess target)
              (cb {:value (->> (js->clj (.getResponseJson target) :keywordize-keys true)
                               (map gist->project)
                               (filter #(> (count (:files %)) 0)))})
              (cb nil))))
        "GET"
        nil
        (tokens/auth-headers :github)))

(defn load-user-gists [username]
  (when username
    (case username
      "modules"
      nil
      (do
        (d/transact! [{:db/id           username
                       :loading-message "Loading gists..."}])
        (get-user-gists username (fn [{:keys [value error]}]
                                   (d/transact! (if value
                                                  [[:db/add username :gists value]
                                                   [:db/add username :loading-message false]]
                                                  [{:error error}]))))))))

(defn patch-gist [gist-id gist-data cb]
  (send (str "https://api.github.com/gists/" gist-id)
        (fn [e]
          (let [target (.-target e)]
            (if (.isSuccess target)
              (cb {:value (-> (.getResponseJson target)
                              (js->clj :keywordize-keys true)
                              (gist->project))})
              (cb {:error (.getLastError target)}))))
        "PATCH"
        (->> gist-data (clj->js) (.stringify js/JSON))
        (tokens/auth-headers :github)))

(defn create-gist [gist-data cb]
  (send (str "https://api.github.com/gists")
        (fn [e]
          (let [target (.-target e)]
            (if (.isSuccess target)
              (cb {:value (-> (.getResponseJson target)
                              (js->clj :keywordize-keys true)
                              (gist->project))})
              (cb {:error (.getLastError target)}))))
        "POST"
        (->> gist-data (clj->js) (.stringify js/JSON))
        (tokens/auth-headers :github)))

#_(defn fork-gist [gist-id cb]
    (send (str "https://api.github.com/gists/" gist-id "/forks")
          (fn [e]
            (let [target (.-target e)]
              (if (.isSuccess target)
                (cb {:value (-> (.getResponseJson target)
                                (js->clj :keywordize-keys true)
                                (gist->project))})
                (cb {:error (.getLastError target)}))))
          "POST"
          nil
          (tokens/auth-headers :github)))

(defn get-username [id cb]
  (send (str "https://api.github.com/user/" id)
        (fn [e]
          (let [target (.-target e)]
            (if (.isSuccess target)
              (cb {:value (gobj/get (.getResponseJson target) "login")})
              (cb {:error (.getLastError target)}))))
        "GET"
        nil
        (tokens/auth-headers :github)))

(def blank {:files {"" {:content ""}}})

(defn clear-new! []
  (d/transact! [{:db/id     "new"
                 :persisted nil
                 :local     blank}]))