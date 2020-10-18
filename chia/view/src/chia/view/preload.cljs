(ns chia.view.preload
  (:require ["react-refresh/runtime" :as refresh]))

(defonce _
  (.injectIntoGlobalHook js/ReactRefresh goog/global))

(defn ^:dev/after-load refresh!
  []
  (->> (refresh/performReactRefresh)
       (js/console.log "refreshed:")))