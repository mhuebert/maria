(ns maria.index
  (:require
    [cljsjs.react]
    [cljsjs.react.dom]
    [re-view-routing.core :as routing]
    [re-view.core :as v :refer [defview]]
    [maria.frames.communication :as frame]
    [maria.frames.views :as frame-view]
    [cljs.core.match :refer-macros [match]]
    [clojure.string :as string]
    [maria.persistence.remote :as remote]
    [re-db.d :as d]))

(enable-console-print!)

(defview layout []
  (match (d/get :router/location :segments)
         [] (frame-view/editor-frame-view {:source-id "intro"})
         ["gist" id] (remote/load
                       {:load-fn    (partial remote/get-gist id)
                        :on-success #(let [gist-data (remote/parse-gist %)]
                                       (d/transact! [(merge gist-data
                                                            {:db/id id
                                                             :url   (str "https://gist.github.com/" id)
                                                             :save? true
                                                             :fork? true})])
                                       (frame-view/editor-frame-view {:default-value ";; put a gist here"
                                                                      :source-id     id}))
                        :on-loading #(frame-view/editor-frame-view {:source-id       id
                                                                    :loading-message "Loading gist..."})
                        :on-error   #(frame-view/editor-frame-view {:source-id id
                                                                    :error     %})})))

(defonce _
         (do
           (routing/listen #(d/transact! [(assoc % :db/id :router/location)]))
           (d/transact! [{:db/id         "intro"
                          :default-value ";; intro"}])
           (v/render-to-dom (layout) "maria-index")))


;["gist" id] (repl/layout {:gist-id id})

;; send editor content to app
;; receive updated editor content from app
;; save content back to source
;; track which data source the editor is linked to


;; later...
;; - multiple buffers edited at once
;; - navigate data sources

;; forced to say, 'persistence is entirely orthogonal to the editing env'



