(ns maria.trusted.trusted-actions
  (:require [re-db.d :as d]
            [maria.trusted.persistence.remote :as remote]
            [clojure.string :as string]
            [re-view-routing.core :as routing]
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
  (memoize (fn [source-id]
             (fn [message]
               (match message
                      [:project/update-file source-id filename attr value]
                      (update-local-gist source-id filename attr value)
                      [:project/publish source-id]
                      (prn (github/project->gist (d/entity source-id)))
                      [:auth/sign-in] (remote/sign-in :github)
                      [:auth/sign-out] (remote/sign-out)
                      [:window/navigate url opts] (navigate! url opts)
                      :else (prn "Unknown message: " message))))))