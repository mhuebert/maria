(ns maria.frames.user-actions
  (:require [cljs.core.match :refer-macros [match]]
            [re-db.d :as d]
            [maria.persistence.local :as local]
            [maria.persistence.github :as github]))

(defn handle-message [_ message]
  (match message
         [:db/transactions txs] (d/transact! txs)
         [:db/copy-local from-id to-id]
         (local/init-storage to-id (d/get from-id :local))
         [:project/clear-new!] (github/clear-new!)))