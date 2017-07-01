(ns maria.index
  (:require [re-view-routing.core :as routing]
            [cljs.core.match :refer-macros [match]]
            [cognitect.transit :as t]))

(enable-console-print!)

(def deserialize (partial t/read (t/reader :json)))
(def serialize (partial t/write  (t/writer :json)))

(def env-origin (if (= (aget js/window "location" "origin") "https://maria.cloud")
                  "https://env.maria.cloud"
                  "http://env.maria.cloud.dev:5000"))

(def env-frame
  (doto (.getElementById js/document "maria-env-frame")
    (aset "src" (str env-origin "/env"))))

(.addEventListener js/window "message"
                   (fn [e]
                     (when (and (= (.-origin e) env-origin)
                                (= (.-source e) (.-contentWindow env-frame)))
                       (match (deserialize (.-data e))

                              ;; editor content has changed
                              [:editor/update-content source] (prn "got updated content: " source)))))



(def location (atom {}))
(routing/listen (fn [{:keys [segments]}]
                  (case segments
                    ["gist" id] (.postMessage (.-contentWindow env-frame) (serialize [:editor/set-content "123"])))
                    ))


;; send editor content to app
;; receive updated editor content from app
;; save content back to source
;; track which data source the editor is linked to


;; later...
;; - multiple buffers edited at once
;; - navigate data sources

;; forced to say, 'persistence is entirely orthogonal to the editing env'

#_(match (d/get :router/location :segments)
         (:or [] ["env"]) (repl/layout)
         ["gist" id] (repl/layout {:gist-id id})
         ["walkthrough"] (walkthrough/main)
         ["paredit"] (paredit/examples)
         :else (not-found))

