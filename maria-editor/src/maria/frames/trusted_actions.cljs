(ns maria.frames.trusted-actions
  (:require [re-db.d :as d]
            [maria.persistence.firebase :as remote]
            [clojure.string :as string]
            [re-view-routing.core :as routing]
            [cljs.core.match :refer-macros [match]]
            [maria.persistence.github :as github]
            [maria.frames.frame-communication :as frame]
            [re-view-routing.core :as r]))

(defn navigate! [url opts]
  (if (string/starts-with? url "/")
    (routing/nav! url)
    (if (:popup? opts)
      (.open js/window url)
      (aset js/window "location" "href" url))))

(def handle-message
  (memoize (fn [project-id]
             (fn [frame-id message]
               (match message
                      [:project/save project-id project]
                      (let [owned? (= (str (d/get-in project-id [:persisted :owner :id]))
                                      (str (d/get-in :auth-secret [:provider-data 0 :uid])))]
                        (if owned?
                          (github/patch-gist project-id
                                             (github/project->gist project)
                                             (fn [{:keys [value error]}]
                                               (if value (d/transact! [(assoc value :local (:persisted value))])
                                                         (.error js/console "Error patching gist: " error))))
                          (throw (js/Error. "Cannot publish project owned by another user."))))

                      [:project/create project local-id]
                      (github/create-gist (github/project->gist project) (fn [{{{id :id :as project} :persisted :as value} :value
                                                                               error                                       :error}]
                                                                           (if project (do
                                                                                         (d/transact! [value])
                                                                                         (frame/send frame-id [:project/clear-local! local-id])
                                                                                         (r/nav! (str "/gist/" id)))
                                                                                       (.error js/console "Error creating gist: " error))))

                      [:project/copy project]
                      (github/create-gist (github/project->gist project) (fn [{{{id :id :as project} :persisted :as value} :value
                                                                               error                                       :error}]
                                                                           (if project (do
                                                                                         (d/transact! [value])
                                                                                         (r/nav! (str "/gist/" id)))
                                                                                       (.error js/console "Error creating gist: " error))))

                      [:auth/sign-in] (remote/sign-in "github.com")

                      [:auth/sign-out] (remote/sign-out)

                      [:window/navigate url opts] (navigate! url opts)
                      [:window/set-title title] (d/transact! [[:db/add :window :title title]])
                      :else (prn "Unknown message: " message))))))