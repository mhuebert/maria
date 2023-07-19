(ns maria.cloud.persistence
  (:require [applied-science.js-interop :as j]
            [goog.functions :as gf]
            [maria.cloud.github :as gh]
            [maria.cloud.local-sync :as local-sync]
            [maria.cloud.routes :as routes]
            [maria.editor.doc :as doc]
            [maria.editor.keymaps :as keymaps]
            [maria.editor.util :as u]
            [maria.ui :as ui]
            [promesa.core :as p]
            [re-db.api :as db]
            [maria.editor.code.commands :as commands]))

;; Maria is currently a single-file editor.
;; When loading a gist, we pick the first Clojure file.

(defn extract-filename [source]
  (some-> (u/extract-title source)
          u/slug
          (str ".cljs")))

(def entity-ratom
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
  (entity-ratom (local-sync/db-id id)))

(defn persisted-ratom [id]
  (entity-ratom [:file/id id]))

(def state-source (comp doc/doc->clj (j/get :doc)))

(defn new-local-file! [& [{:as file
                           :file/keys [source name]}]]
  (let [id (str (random-uuid))]
    (reset! (local-ratom id)
            {:file/source (or source "")
             :file/name (or name (extract-filename source))})
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
        filename (or (:file/name local)
                     (extract-filename source)
                     "untitled.cljs")
        body {:files {filename {:content source}}}]
    (when (seq body)
      (p/let [file (p/-> (gh-fetch (str "https://api.github.com/gists")
                                   {:method "POST"
                                    :body body})
                         gh/parse-gist)]
        (when file
          (reset! !persisted file)
          (reset! !local (select-keys file [:file/source]))
          (routes/navigate! 'maria.cloud.views/gist {:gist/id (:gist/id file)}))))))

(defn writable? [id]
  (not= :file.provider/curriculum (:file/provider (current-file id))))

(keymaps/register-commands!
  {:file/new {:bindings [(if keymaps/mac?
                           :Ctrl-n
                           :Meta-n)]
              :f (fn [_] (new-local-file!))}
   :file/duplicate {:when (every-pred :ProseView :file/id)
                    ;; create a new gist with contents of current doc.
                    :f (fn [{:keys [ProseView file/id]}]
                         (let [source (state-source (j/get ProseView :state))]
                           (new-local-file! {:file/source source
                                             :file/name (some-> (:file/name (current-file id))
                                                                (swap-name (partial str "copy_of_")))})))}
   :file/revert {:when (comp seq changes :file/id)
                 :f (fn [{:keys [file/id ProseView]}]
                      (j/let [source (:file/source @(persisted-ratom id))]
                        (reset! (local-ratom id) nil)
                        (commands/prose:replace-doc ProseView source)))}
   :file/save {:bindings [:Ctrl-s]
               :when (fn [{:keys [file/id]}]
                       (and (gh/get-token)
                            (writable? id)))
               ;; if local, create a new gist and then navigate there.
               ;; if gist, save a new revision of that gist.
               :f (fn [{:keys [file/id]}]
                    (if (:gist/id @(persisted-ratom id))
                      (update-gist id)
                      (create-gist id)))}})