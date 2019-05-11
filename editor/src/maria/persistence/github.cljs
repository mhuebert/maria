(ns maria.persistence.github
  (:require [goog.net.XhrIo :as xhr]
            [maria.persistence.tokens :as tokens]
            [chia.db :as d]
            [maria.curriculum :as curriculum]
            [clojure.string :as string]
            [applied-science.js-interop :as j]))

(d/merge-schema! {:doc.owner/username {:db/index true}})

(defn send [url cb & args]
  (d/transact! [[:db/update-attr :remote/status :in-progress (fnil inc 0)]])
  (.apply xhr/send nil (to-array (concat [(str url "?ts=" (.now js/Date))
                                          (fn [e]
                                            (d/transact! [[:db/update-attr :remote/status :in-progress dec]])
                                            (cb e))] args)) args))

(defn gist-person [{:keys [html_url url id login gists_url] :as person}]
  {:username  login
   :local-url (str "/gists/" login)
   :id        (str id)})

(defn gist->project
  "Convert a gist to local project format"
  [{:keys [description files updated_at owner html_url id history] :as gist-data}]
  (let [{:keys [username] :as owner} (if (contains? curriculum/by-id id)
                                       curriculum/owner
                                       (gist-person owner))]
    (let [files (->> files
                     (reduce-kv (fn [m filename {:keys [language]
                                                 :as   file}]
                                  (cond-> m
                                          (= language "Clojure")
                                          (assoc (name filename) (select-keys file [:filename :truncated :content])))) {}))]
      (when (seq files)
        {:db/id              id
         :updated-at         updated_at
         :doc.owner/username username
         :persisted          {:description          description
                              :version              (:version (first history))
                              :id                   id
                              :owner                owner
                              :remote-url           html_url
                              :persistence/provider :gist
                              :files                files}}))))

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
        (tokens/auth-headers "github.com")))

(defn get-url [url cb]
  (send url (fn [e]
              (let [target (.-target e)]
                (if (.isSuccess target)
                  (cb {:value (.getResponseText target)})
                  (cb {:error (.getLastError target)}))))
        "GET"))

(defn load-gist [id]
  (when-not (d/get id :persisted)
    (d/transact! [{:db/id           id
                   :loading-message "Loading gist..."}]))
  (get-gist id (fn [{:keys [value error]}]
                 (d/transact! [(merge {:loading-message false}
                                      (or value
                                          {:persisted-error error}))]))))

(defn load-url-text [url]
  (let [id (js/encodeURIComponent url)]
    (when-not (d/get id :persisted)
      (d/transact! [{:db/id           id
                     :loading-message "Loading..."}]))
    (get-url url (fn [{:keys [value error]}]
                   (let [filename (last (string/split url #"/"))]
                     (d/transact! [(merge {:db/id           id
                                           :loading-message false}
                                          (or {:persisted {:files                {filename {:content value}}
                                                           :persistence/provider :http-text}}
                                              {:persisted-error error}))]))))))

(defn get-user-gists [username cb]
  (send (str "https://api.github.com/users/" username "/gists")
        (fn [e]
          (let [target (.-target e)]
            (if (.isSuccess target)
              (cb {:value (keep gist->project (js->clj (.getResponseJson target) :keywordize-keys true))})
              (cb nil))))
        "GET"
        nil
        (tokens/auth-headers "github.com")))

(defn load-user-gists [username]
  (case username
    "curriculum"
    nil
    (when-not (d/get username :gists)
      (d/transact! [{:db/id           username
                     :loading-message "Loading gists..."}])
      (get-user-gists username (fn [{:keys [value error]}]
                                 (d/transact! (if value
                                                (into value
                                                      [[:db/add username :gists true]
                                                       [:db/add username :loading-message false]])
                                                [{:error error}])))))))

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
        (tokens/auth-headers "github.com")))

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
        (tokens/auth-headers "github.com")))

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
          (tokens/auth-headers "github.com")))

(defn get-username [id cb]
  (send (str "https://api.github.com/user/" id)
        (fn [e]
          (let [target (.-target e)]
            (if (.isSuccess target)
              (cb {:value (j/get (.getResponseJson target) :login)})
              (cb {:error (.getLastError target)}))))
        "GET"
        nil
        (tokens/auth-headers "github.com")))
