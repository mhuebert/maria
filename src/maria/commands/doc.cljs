(ns maria.commands.doc
  (:require [commands.registry :refer-macros [defcommand]]
            [cljs.core.match :refer-macros [match]]
            [clojure.string :as string]
            [maria.frames.frame-communication :as frame]
            [re-db.d :as d]
            [maria.views.icons :as icons]
            [maria.persistence.github :as github]))

(def send (partial frame/send frame/trusted-frame))

(defn add-clj-ext [s]
  (when s
    (cond-> s
            (not (re-find #"\.clj[cs]?$" s)) (str ".cljs"))))

(defn strip-clj-ext [s]
  (some-> s
          (string/replace #"\.clj[cs]?$" "")))

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
                                     (= (str (:id (:owner persisted))) (d/get :auth-public :id)))
          unsaved-changes (unsaved-changes? toolbar)]
      (match [(boolean persisted) owned-by-current-user? unsaved-changes]
             [false _ true] :create
             [true true true] :save
             [true false _] :copy
             :else nil))))

(defn create! [local]
  (send [:project/create local]))

(defn copy! [{{local-version :local} :project
              filename               :filename}]
  (let [{local-filename :filename
         local-content  :content} (or (get-in local-version [:files filename])
                                      filename)
        new-filename (str "Copy of " local-filename)]
    (create! (-> local-version
                 (update :files dissoc filename)
                 (assoc-in [:files new-filename :content] local-content)))))

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
    :create (create! (d/get "new" :local))))

(defcommand :doc/new
  "Create a blank doc"
  {:bindings       ["M1-Shift-B"]
   :intercept-when true
   :when           :current-doc
   :icon           icons/Add}
  [{{:keys [view/state doc-editor id]} :current-doc}]
  (github/clear-new!)
  (frame/send frame/trusted-frame [:window/navigate "/new" {}])
  true)

(defcommand :doc/save
  "Save the current doc"
  {:bindings       ["M1-S"]
   :intercept-when true
   :when           #(and (:signed-in? %)
                         (#{:create :save} (persistence-mode (:current-doc %))))
   :icon           icons/Backup}
  [{:keys [current-doc]}]
  (persist! current-doc)
  true)

(defcommand :doc/save-a-copy
  "Save a new copy of a project"
  {:bindings       ["M1-Shift-S"]
   :intercept-when true
   :icon           icons/ContentDuplicate
   :when           #(and (:signed-in? %)
                         (get-in % [:current-doc :project :persisted])
                         (valid-content? (:current-doc %)))}
  [{:keys [current-doc]}]
  (copy! current-doc)
  true)

(defcommand :doc/revert
  {:when (fn [{:keys [current-doc]}]
           (and (get-in current-doc [:project :persisted])
                (unsaved-changes? current-doc)))}
  [{{{persisted :persisted} :project
     filename               :filename} :current-doc}]
  (d/transact! [[:db/add (:id persisted) :local persisted]])
  true)
