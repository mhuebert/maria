(ns maria.cloud.index
  (:require [applied-science.js-interop :as j]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [maria.cloud.routes :as routes]
            [maria.cloud.views :as views]
            [maria.editor.code-blocks.docbar :as docbar]
            [maria.editor.code-blocks.docbar]
            [maria.editor.command-bar :as command-bar]
            [maria.scratch]
            [maria.ui :as ui :refer [defview]]
            [re-db.api :as db]
            [yawn.hooks :as h]
            [yawn.root :as root]
            [yawn.view :as v]))

;; TODO
;; - UI for sidebar,
;; - support per-attribute local-state persistence
;; - include curriculum as a re-db transaction,
;; -

(defn get-scripts [type]
  (->> (js/document.querySelectorAll (str "[type='" type "']"))
       (map (comp edn/read-string (j/get :innerHTML)))))

(defn init-re-db []
  (doseq [schema (get-scripts "application/re-db:schema")]
    (db/merge-schema! schema))
  (doseq [tx (get-scripts "application/re-db:tx")]
    (db/transact! tx)))

(defn link [title href]
  [:a (cond-> {:href href}
              (str/starts-with? title "http")
              (assoc :target "_blank")) title])

(defview landing []
  (let [{:as location ::routes/keys [view]} (h/use-deref routes/!location)]
    [:<>
     (ui/use-global-keymap {:mod-k (fn [& _] (command-bar/toggle!))
                            :shift-mod-k (fn [& _] (swap! ui/!sidebar-state update :visible? not))})
     [command-bar/view]
     [ui/with-sidebar
      [views/sidebar-content]
      [view location]]
     [docbar/view]]))

(defn ^:export init []
  (init-re-db)
  (routes/init)
  (root/create :maria-live (v/x [landing])))

