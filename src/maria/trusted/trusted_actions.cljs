(ns maria.trusted.trusted-actions
  (:require [re-db.d :as d]
            [maria.trusted.persistence.remote :as remote]
            [clojure.string :as string]
            [re-view-routing.core :as routing]
            [cljs.pprint :refer [pprint]]
            [cljs.core.match :refer-macros [match]]
            [maria.persistence.github :as github]))



(defn navigate! [url opts]
  (if (string/starts-with? url "/")
    (routing/nav! url)
    (if (:popup? opts)
      (.open js/window url)
      (aset js/window "location" "href" url))))

(def editor-message-handler
  (memoize (fn [project-id]
             (fn [frame-id message]
               (match message
                      [:project/publish project-id project]
                      (let [owned? (= (str (d/get-in project-id [:persisted :owner :id]))
                                      (str (d/get-in :auth-secret [:provider-data 0 :uid])))]
                        (if owned?
                          (github/patch-gist project-id (github/project->gist project))
                          (throw (js/Error. "Cannot publish project owned by another user."))))

                      [:project/create project]
                      (github/create-gist frame-id (github/project->gist project))

                      [:project/fork project-id] (github/fork-gist frame-id project-id)

                      [:auth/sign-in] (remote/sign-in :github)

                      [:auth/sign-out] (remote/sign-out)

                      [:window/navigate url opts] (navigate! url opts)
                      :else (prn "Unknown message: " message))))))