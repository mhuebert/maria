(ns maria.commands.doc
  (:require [lark.commands.registry :refer-macros [defcommand]]
            [cljs.core.match :refer-macros [match]]
            [clojure.string :as string]
            [maria.frames.frame-communication :as frame]
            [re-db.d :as d]
            [maria.views.icons :as icons]
            [maria.persistence.github :as github]
            [maria.persistence.local :as local]
            [maria.curriculum :as curriculum]
            [maria.util :as util]))

(def send (partial frame/send frame/trusted-frame))
(local/init-storage ::locals)

(defn can-persist? []
  (boolean (d/get :auth-public :username)))

(defn add-clj-ext [s]
  (when s
    (cond-> (string/replace s "/" "_")
            (not (re-find #"\.clj[cs]?$" s)) (str ".cljs"))))

(defn strip-clj-ext [s]
  (some-> s
          (string/replace #"\.clj[cs]?$" "")))

(defn local-url* [provider id]
  (case (or provider (d/get id :persistence/provider))
    :gist (str "/gist/" id)
    :maria/local (str "/local/" id)
    :maria/curriculum (str "/" (get-in curriculum/by-id [id :slug]))
    (do #_(prn "NO LOCAL URL" provider id)
        #_(throw (js/Error. (str "no local url" provider id))))))

(defn locals-path
  ([store] (locals-path (d/get :auth-public :username) store))
  ([username store]
   [username store]))

(defn get-filename
  "Given a map of <filename, {:filename, content}>, return the inner or outer filename when present."
  [file]
  (let [[old-name {new-name :filename}] file]
    (or (util/some-str new-name) (util/some-str old-name))))

(defn project-filename [project]
  (get-filename (first (:files (or (:local project)
                                   (:persisted project))))))

(defn locals-dir
  [store]
  (d/get-in ::locals (conj [:local] (locals-path store))))

(defn locals-push!
  "Add a locally-stored ids to `path` (if doc exists locally and has a filename)"
  [store id]
  (d/transact! [[:db/update-attr ::locals :local update (locals-path store) #(->> (cons id %)
                                                                                  (distinct))]]))
(defn locals-remove!
  "Remove a locally-stored id from `path`"
  [store id]
  (d/transact! [[:db/update-attr ::locals :local update (locals-path store) #(remove (fn [x] (or (nil? x)
                                                                                                 (= x id))) %)]]))

(defn normalize-doc
  ([doc] (normalize-doc (:id doc) doc))
  ([doc-id {updated-at :updated-at
            root-id    :db/id
            :keys      [persisted local]
            :as        the-doc}]
   (let [{:keys [filename files local-url id owner]
          :as   doc} (merge (:persisted the-doc)
                            (:local the-doc)
                            (select-keys the-doc [:owner]))
         provider (or (:persistence/provider the-doc)
                      (:persistence/provider persisted)
                      (:persistence/provider local))
         the-id (or doc-id root-id id)
         the-filename (get-filename (first files))]

     (cond-> (assoc doc :updated-at updated-at
                        :persistence/provider provider)
             (nil? filename) (assoc :filename the-filename)
             (nil? id) (assoc :id the-id)
             (nil? owner) (assoc :owner (or (:owner persisted)
                                            (:owner local)))
             (nil? local-url) (assoc :local-url (local-url* provider the-id))))))

(defn sort-projects [projects]
  (sort-by #(or
              (:updated-at %)
              (project-filename %)) (fn [a b] (compare b a)) projects))

(defn locals-docs
  "Return the locally-stored docs for `path`"
  [store]
  (seq (for [id (locals-dir store)
             :when id]
         ;; init local storage for docs with localStorage pointers.
         ;; memoized, so ok to repeat
         (do (local/init-storage id)
             (normalize-doc id (merge {:local (local/local-get id)}
                                      (d/entity id)))))))

(defn user-gists [username]
  (seq (->> (map normalize-doc (d/entities [[:doc.owner/username username]]))
            (sort-projects))))

(def curriculum (mapv normalize-doc curriculum/docs))

(defn unsaved-changes? [{{{local-files :files}     :local
                          {persisted-files :files} :persisted} :project
                         filename                              :filename}]
  (let [{local-content  :content
         local-filename :filename
         :as            local-file} (get local-files filename)
        {persisted-content :content} (get persisted-files filename)]
    (and filename
         local-file
         (or (and local-content (not= local-content persisted-content))
             (and local-filename (not= local-filename filename))))))

(defn valid-content? [{:keys [project filename]}]
  (let [{local-content  :content
         local-filename :filename} (get-in project [:local :files filename])
        persisted-content (get-in project [:persisted :files filename :content])
        content-to-persist (or local-content persisted-content)
        filename-to-persist (some-> (or local-filename filename)
                                    (strip-clj-ext))]
    (and (not (empty? content-to-persist))
         (not (re-find #"^\s*$" content-to-persist))
         (not (empty? filename-to-persist))
         (not (re-find #"^\s*$" filename-to-persist)))))

(defn persistence-mode [{{:keys [persisted]} :project :as toolbar}]
  (when (valid-content? toolbar)
    (let [owned-by-current-user? (or (nil? persisted)
                                     (let [id (d/get :auth-public :id)]
                                       (and id (= (str (:id (:owner persisted))) id))))
          unsaved-changes (unsaved-changes? toolbar)]
      (match [(boolean persisted) owned-by-current-user? unsaved-changes]
             [false _ true] :create
             [true true true] :save
             [true false _] :copy
             [true true false] :saved
             :else nil))))

(defn create! [local local-id]
  (send [:project/create local local-id]))

(defn rename-file [version old-name new-name]
  (-> version
      (update :files dissoc old-name)
      (assoc-in [:files new-name :content] (get-in version [:files old-name :content]))))

(defn init-new!
  ([] (init-new! "Untitled" ""))
  ([filename content]
   (let [id (d/unique-id)]
     (local/init-storage id {:persistence/provider :maria/local
                             :files                {(add-clj-ext filename) {:content (or content "")}}})
     (frame/send frame/trusted-frame [:window/navigate (str "/local/" id) {}])
     id)))

(defn make-a-copy! [{{local-version :local} :project
                     filename               :filename}]
  (let [new-filename (str "Copy of "
                          (or (get-in local-version [:files filename :filename])
                              filename))]
    (if (can-persist?)
      (create! (-> local-version
                   (rename-file filename new-filename))
               false)
      (init-new! new-filename (get-in local-version [:files filename :content])))))

(defn save! [{filename                                      :filename
              {{persisted-id :id :as persisted} :persisted} :project
              :as                                           toolbar}]
  (let [{local-content  :content
         local-filename :filename} (get-in toolbar [:project :local :files filename])]
    (send [:project/save persisted-id {:files {filename {:filename (or local-filename filename)
                                                         :content  (or local-content (get-in persisted [:files filename :content]))}}}])))

(defn persist! [toolbar]
  (case (persistence-mode toolbar)
    :save (save! toolbar)
    :create (create! (d/get (:id toolbar) :local) (:id toolbar))))

(defcommand :doc/new
  "Create a blank doc"
  {:bindings       ["M1-Shift-B"]
   :intercept-when true
   :icon           icons/Add}
  [{{:keys [view/state doc-editor id]} :current-doc}]
  (init-new!)
  true)

(defcommand :doc/save
  "Save the current doc"
  {:bindings       ["M1-S"]
   :intercept-when true
   :when           #(and (can-persist?)
                         (#{:create :save} (persistence-mode (:current-doc %))))
   :icon           icons/Backup}
  [{:keys [current-doc]}]
  (persist! current-doc)
  true)

(defcommand :doc/duplicate
  "Save a new copy of a project"
  {:bindings       ["M1-Shift-S"]
   :intercept-when true
   :icon           icons/ContentDuplicate
   :when           #(valid-content? (:current-doc %))}
  [{:keys [current-doc]}]
  (make-a-copy! current-doc)
  true)

(defcommand :doc/revert-to-saved-version
  {:when (fn [{:keys [current-doc]}]
           (and (get-in current-doc [:project :persisted])
                (unsaved-changes? current-doc)))}
  [{{{persisted :persisted :as project} :project
     filename                           :filename} :current-doc}]
  (d/transact! [[:db/update-attr (:db/id project) :local merge (dissoc persisted :persistence/provider)]])
  true)