(ns maria.index
  (:require
    [cljsjs.react]
    [cljsjs.react.dom]
    [re-view-routing.core :as routing]
    [re-view.core :as v :refer [defview]]
    [maria.frame-communication :as frame]
    [cljs.core.match :refer-macros [match]]
    [clojure.string :as string]
    [re-db.d :as d]))

(enable-console-print!)

#_(fn [{:keys [gist-id] :as this}]
    (let [editor (.getEditor this)]
      (some-> editor (.focus))
      (when gist-id
        (loaders/get-gist gist-id (fn [{:keys [value error]}]
                                    (.setValue editor (or (some-> value (loaders/gist-source))
                                                          (str "\nError loading gist:  " error))))))))

(defonce _
         (do
           (frame/listen "*"
                         (fn [message]
                           (match message
                                  ;; editor content has changed
                                  [:editor/update id source] (do (prn "got updated content: " id source)
                                                                 )
                                  [:editor/save id source] (do (prn "save updated source.." id source))
                                  [:url/navigate url] (if (string/starts-with? url "/")
                                                        (routing/nav! url)
                                                        (aset js/window "location" "href" url)))))

           (routing/listen #(d/transact! [(assoc % :db/id :router/location)]))
           (d/transact! [{:db/id         "intro"
                          :default-value ";; intro"
                          :immutable?    true}])))

(defview layout []
  (match (d/get :router/location :segments)
         [] (frame/editor-frame-view {:source-id "intro"})
         ["gist" id] (let [id (str "gist/" id)]
                       (frame/editor-frame-view {:default-value ";; put a gist here"
                                                 :source-id     id}))))

(v/render-to-dom (layout) "maria-index")


;["gist" id] (repl/layout {:gist-id id})

;; send editor content to app
;; receive updated editor content from app
;; save content back to source
;; track which data source the editor is linked to


;; later...
;; - multiple buffers edited at once
;; - navigate data sources

;; forced to say, 'persistence is entirely orthogonal to the editing env'



