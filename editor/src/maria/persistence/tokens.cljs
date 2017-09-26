(ns maria.persistence.tokens
  (:require [maria.persistence.local :as local]))

(defn clear-tokens []
  (local/local-put! "auth/accessToken" nil))

(defn put-token [provider token]
  (local/local-update! "auth/accessToken" merge {provider token}))

(defn get-token [provider]
  (get (local/local-get "auth/accessToken") provider))

(defn auth-headers [provider]
  (when-let [token (get-token provider)]
    #js {"Authorization" (str "token " token)}))