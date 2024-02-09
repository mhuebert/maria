(ns maria.cloud.persistence
  (:require [applied-science.js-interop :as j]
            [goog.functions :as gf]
            [maria.cloud.github :as gh]
            [maria.cloud.local :as local]
            [maria.cloud.local-sync :as local-sync]
            [maria.cloud.routes :as routes]
            [maria.editor.doc :as doc]
            [maria.editor.keymaps :as keymaps]
            [maria.editor.util :as u]
            [maria.ui :as ui]
            [promesa.core :as p]
            [re-db.api :as db]
            [maria.editor.code.commands :as commands]
            [yawn.hooks :as h]))

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
            (db/transact! (into [(assoc new-value :db/id (:db/id prev-value db-id))]
                                (for [[k v] (dissoc prev-value :db/id)
                                      :when (not (contains? new-value k))]
                                  [:db/retract db-id k v])))
            @o))))))

(defn local-ratom [id]
  (local-sync/sync-entity! id)
  (entity-ratom (local-sync/db-id id)))

(defn persisted-ratom [id]
  (entity-ratom [:file/id id]))

(def state-source (comp doc/doc->clj (j/get :doc)))

(defn new-blank-file! [& [{:as file
                           :file/keys [source name]}]]
  (some-> js/window.event (j/call :preventDefault))
  (let [id (str (random-uuid))]
    (reset! (local-ratom id)
            {:file/source (or source "")
             :file/name (or name (extract-filename source))})
    (routes/navigate! 'maria.cloud.views/local {:local/id id})
    true))

(defn current-file [id]
  (merge @(persisted-ratom id)
         @(local-ratom id)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Keep track of recently viewed files

(defonce !recents (local/ratom ::recent-docs ()))

(comment
  (swap! !recents empty))

(defn add-to-recents! [path file]
  (let [entry {:maria/path path
               :file/id (:file/id file)
               :file/title (or (:file/title file)
                               (some-> (:file/source file) u/extract-title)
                               (:file/name file))}]
    (when (:file/title entry)
      (swap! !recents (fn [xs]
                        (->> xs
                             (remove #(= (:file/id %) (:file/id file)))
                             (cons entry)))))))

(defn remove-from-recents! [id]
  (swap! !recents (partial remove #(= (:file/id %) id))))

(defn use-recents! [path {:as file :keys [file/id]}]
  (let [possible-title (or (:file/title file)
                           (some-> (:file/source file) u/extract-title)
                           (:file/name file))]
    (h/use-effect
      (fn []
        (when (seq (:file/source file))
          (add-to-recents! path file)))
      [path
       id
       possible-title])))

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

(defn swap-name [n f & args]
  (let [ext (re-find #"\..*$" n)
        pre (subs n 0 (- (count n) (count ext)))]
    (str (apply f pre args) ext)))

(defn gh-fetch [url options]
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
        filename (u/ensure-suffix (or (:file/name local)
                                      (extract-filename source)
                                      "untitled.cljs")
                                  ".cljs")
        body {:files {filename {:content source}}}]
    (when (seq body)
      (remove-from-recents! id)
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
  {:file/new {:bindings [:Shift-Mod-b]
              :f (fn [_] (new-blank-file!))}
   :file/duplicate {:when (every-pred :file/id :ProseView)
                    ;; create a new gist with contents of current doc.
                    :f (fn [{:keys [ProseView file/id]}]
                         (let [source (state-source (j/get ProseView :state))]
                           (new-blank-file! {:file/source source
                                             :file/name (some-> (:file/name (current-file id))
                                                                (swap-name (partial str "copy_of_")))})))}
   :file/revert {:when (comp seq changes :file/id)
                 :f (fn [{:keys [file/id ProseView]}]
                      (j/let [source (:file/source @(persisted-ratom id))]
                        (reset! (local-ratom id) nil)
                        (commands/prose:replace-doc ProseView source)))}
   :file/save {:bindings [:Ctrl-s]
               :when (fn [{:keys [file/id]}]
                       (and id
                            (gh/get-token)
                            (writable? id)))
               ;; if local, create a new gist and then navigate there.
               ;; if gist, save a new revision of that gist.
               :f (fn [{:keys [file/id]}]
                    (if (:gist/id @(persisted-ratom id))
                      (update-gist id)
                      (create-gist id)))}})

(defn use-persisted-file
  "Syncs persisted file to re-db"
  [{:as file :file/keys [id source provider]}]
  (h/use-effect (fn []
                  (when (not= :file.provider/local provider)
                    (reset! (persisted-ratom id) file)))
    [id source]))