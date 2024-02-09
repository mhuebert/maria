(ns maria.cloud.core
  (:require [applied-science.js-interop :as j]
            [cells.core :as cells]
            [clojure.edn :as edn]
            [maria.clerkify]
            [maria.cloud.github :as gh]
            [maria.cloud.menubar :as menu]
            [maria.cloud.github]
            [maria.cloud.routes :as routes]
            [maria.cloud.sidebar :as sidebar]
            [maria.cloud.views]
            [maria.editor.code.docbar :as docbar]
            [maria.editor.code.docbar]
            [maria.editor.keymaps :as keymaps]
            [maria.scratch]
            [maria.ui :as ui :refer [defview]]
            [re-db.api :as db]
            [re-db.reactive :as r]
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

(defview root []
  (let [{:as location ::routes/keys [view]} (h/use-deref routes/!location)]
    (keymaps/use-global-keymap)
    (ui/provide-context {::menu/!content @(h/use-state #(r/atom nil))}
      (when @gh/!initialized?
        [:div.h-screen
         {:on-click #(when (= (j/get % :target)
                              (j/get % :currentTarget))
                       (keymaps/run-command :editor/focus!))}
         [sidebar/with-sidebar
          [sidebar/content]
          [:div
           [menu/menubar ]
           (when view
             [view location])]]
         [docbar/view]]))))

(defn ^:export init []
  (init-re-db)
  (routes/init)
  (root/create :maria-live (v/<> [root])))

(comment
  (init))
