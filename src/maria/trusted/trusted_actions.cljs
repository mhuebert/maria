(ns maria.trusted.trusted-actions
  (:require [re-db.d :as d]
            [maria.trusted.persistence.remote :as remote]
            [clojure.string :as string]
            [re-view-routing.core :as routing]
            [cljs.pprint :refer [pprint]]
            [cljs.core.match :refer-macros [match]]
            [maria.persistence.github :as github]))

(defn update-local-gist [id filename attr value]
  (d/transact! [[:db/update-attr id :local #(assoc-in % [:files filename attr] value)]]))

(defn navigate! [url opts]
  (if (string/starts-with? url "/")
    (routing/nav! url)
    (if (:popup? opts)
      (.open js/window url)
      (aset js/window "location" "href" url))))

(def editor-message-handler
  (memoize (fn [project-id]
             (fn [message]
               (match message
                      [:project/update-file project-id filename attr value]
                      (update-local-gist project-id filename attr value)
                      [:project/publish project-id]
                      (let [owned? (= (str (d/get-in project-id [:persisted :owner :id]))
                                      (str (d/get-in :auth-secret [:provider-data 0 :uid])))]
                        (if owned?
                          (github/patch-gist project-id (github/project->gist (d/get project-id :local)))
                          (github/fork-gist project-id)))
                      [:auth/sign-in] (remote/sign-in :github)
                      [:auth/sign-out] (remote/sign-out)
                      [:window/navigate url opts] (navigate! url opts)
                      :else (prn "Unknown message: " message))))))