(ns maria.cloud.persistence
  (:require [applied-science.js-interop :as j]
            [clojure.string :as str]
            [maria.cloud.github :as gh]
            [maria.cloud.local-sync :as local-sync]
            [maria.editor.doc :as doc]
            [maria.editor.keymaps :as keymaps]
            [goog.functions :as gf]
            [maria.cloud.routes :as routes]
            [maria.editor.util :as u]
            [maria.ui :as ui]
            [promesa.core :as p]
            [re-db.api :as db]))

;; Maria is currently a single-file editor.
;;
;; What we get from gist is a project; we ignore the project and just use the first Clojure file.

(def animals
  ["pig"
   "cow"
   "mole"
   "vole"
   "goat"
   "sheep"
   "horse"
   "donkey"
   "chicken"
   "duck"
   "goose"
   "turkey"
   "pigeon"
   "rabbit"
   "guinea pig"
   "hamster"
   "gerbil"
   "rat"
   "mouse"
   "cat"
   "dog"
   "ferret"
   "parrot"])

(defn untitled-filename []
  (str
    "untitled-" (rand-nth animals) ".cljs"))

(def ratom
  (memoize
    (fn [db-id]
      (reify
        IDeref
        (-deref [o] (db/get db-id))
        ISwap
        (-swap! [o f] (reset! o (f @o)))
        (-swap! [o f a] (reset! o (f @o a)))
        (-swap! [o f a b] (reset! o (f @o a b)))
        (-swap! [o f a b xs] (reset! o (apply f @o a b xs)))
        IReset
        (-reset! [o new-value]
          (let [prev-value @o]
            (db/transact! (into [(assoc new-value :db/id db-id)]
                                (for [[k v] prev-value
                                      :when (not (contains? new-value k))]
                                  [:db/retract db-id k v])))))))))

(defn local-ratom [id]
  (local-sync/sync-entity! id)
  (ratom (local-sync/db-id id)))

(defn persisted-ratom [id]
  (ratom [:file/id id]))

(def state-source (comp doc/doc->clj (j/get :doc)))

(defn new-local-file! [& [{:file/keys [source name]}]]
  (let [id (str (random-uuid))]
    (reset! (local-ratom id)
            {:file/source (or source "")
             :file/name (or name (untitled-filename))})
    (routes/navigate! 'maria.cloud.views/local {:local/id id})))

(defn current-file [id]
  (merge @(persisted-ratom id)
         @(local-ratom id)))

(defn changes [id]
  (when id
    (let [persisted (and id @(persisted-ratom id))
          local @(local-ratom id)]
      (not-empty
        (into {}
              (keep (fn [k]
                      (let [before (k persisted)
                            after (k local)]
                        (when (and after (not= before after))
                          [k [before after]]))))
              [:file/name
               :file/source])))))

(defn autosave-local-fn
  "Returns a callback that will save the current doc to local storage after a 1s debounce."
  []
  (-> (fn [id ^js prev-state ^js next-state]
        (when-not (.eq (.-doc prev-state) (.-doc next-state))
          (swap! (local-ratom id) assoc :file/source (state-source next-state))))
      (gf/debounce 100)))

(defn swap-name [n f & args]
  (let [ext (re-find #"\..*$" n)
        pre (subs n 0 (- (count n) (count ext)))]
    (str (apply f pre args) ext)))

(defn gh-fetch [url options]
  (js/console.log (ui/pprinted options))
  (p/-> (js/fetch url (clj->js (-> options
                                   (u/update-some {:body (comp js/JSON.stringify clj->js)})
                                   (update :headers merge
                                           {:X-GitHub-Api-Version "2022-11-28"
                                            :accept "application/vnd.github+json"}
                                           (gh/auth-headers)))))
        (j/call :json)
        (clj->js :keywordize-keys true)))

(defn update-gist [id]
  (let [!local (local-ratom id)
        !persisted (persisted-ratom id)
        {filename :file/name} @!persisted
        changes (changes id)
        new-source (second (:file/source changes))
        body (cond-> {}
                     (:file/name changes) (assoc-in [:files filename :filename] (second (:file/name changes)))
                     (:file/source changes) (assoc-in [:files filename :content] new-source))
        gist-id (:gist/id @!persisted)]
    (when (seq body)
      (assert gist-id "No gist ID found for update")
      (p/let [file (p/-> (gh-fetch (str "https://api.github.com/gists/" gist-id)
                                   {:method "PATCH"
                                    :body body})
                         gh/parse-gist)]
        (when file
          (reset! !persisted file)
          (reset! !local (select-keys file [:file/source])))))))

(defn create-gist [id]
  (let [!local (local-ratom id)
        local @!local
        !persisted (persisted-ratom id)
        source (:file/source local)
        body {:files {(:file/name local) {:content source}}}]
    (when (seq body)
      (p/let [file (p/-> (gh-fetch (str "https://api.github.com/gists")
                                   {:method "POST"
                                    :body body})
                         gh/parse-gist)]
        (when file
          (reset! !persisted file)
          (reset! !local (select-keys file [:file/source]))
          (routes/navigate! 'maria.cloud.views/gist {:gist/id (:gist/id file)}))))))

(keymaps/register-commands!
  {:file/new {:bindings [(if keymaps/mac?
                           :Ctrl-n
                           :Meta-n)]
              :f (fn [_] (new-local-file!))}
   :file/duplicate {:when (every-pred :ProseView :file/id)
                    ;; create a new gist with contents of current doc.
                    :f (fn [{:keys [ProseView file/id]}]
                         (new-local-file! {:file/source (state-source (j/get ProseView :state))
                                           :file/name (if-let [name (:file/name (current-file id))]
                                                        (swap-name name str "_copy")
                                                        (untitled-filename))}))}
   :file/revert {:when (comp changes :file/id)
                 ;; :when local state diverges from gist state.
                 ;; reset local state to gist state.
                 :f #(reset! (local-ratom (:file/id %)) nil)}
   :file/rename {:f (fn [{:keys [ProseView]}]
                      (let [id (j/get ProseView "file/id")]))}
   :file/save {:bindings [:Ctrl-s]
               :when (fn [_] (gh/get-token))
               ;; if local, create a new gist and then navigate there.
               ;; if gist, save a new revision of that gist.
               :f (fn [{:keys [file/id]}]
                    (if (:gist/id @(persisted-ratom id))
                      (update-gist id)
                      (create-gist id)))}})