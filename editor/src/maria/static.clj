(ns maria.static
  (:require [cljs-static.page :as page]))

(def index
  (page/html-page "Maria"
    {:meta         {:viewport "width=device-width, initial-scale=1"}
     :styles       [{:href "/trusted.css"}]
     :body         [:div#maria-index]
     :scripts/body [{:src "/js/compiled/trusted/shadow-trusted.js"}]}))

(def live
  (page/html-page "Maria"
    {:meta         {:viewport "width=device-width, initial-scale=1"}
     :styles       [{:href "/tachyons.min.css"}
                    {:href "/codemirror.css"}
                    {:href "/maria.css"}]
     :body         [:div#maria-env.h-100]
     :scripts/body [{:src "/js/compiled/live/shadow-live.js"}]}))