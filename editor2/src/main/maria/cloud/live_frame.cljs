(ns maria.cloud.live-frame
  (:require [applied-science.js-interop :as j]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [maria.editor.code-blocks.docbar :as eldoc]
            [maria.editor.code-blocks.examples :as ex]
            [maria.editor.code-blocks.sci :as sci]
            [maria.editor.command-bar :as command-bar]
            [maria.editor.core :as prose]
            [maria.scratch]
            [maria.ui :as ui :refer [defview]]
            [re-db.api :as db]
            [re-db.reactive :as r]
            [re-db.hooks :as h]
            [yawn.root :as root]
            [yawn.view :as v]))

;; TODO
;; - UI for sidebar,
;; - support per-attribute local-state persistence
;; - include curriculum as a re-db transaction,
;; -

(r/redef !sidebar-state (r/atom {:visible? true}))
(def sidebar-transition "all 0.2s ease 0s")

(defview sidebar [{:keys [visible? width]}]
  (into [:div.fixed.top-0.bottom-0.bg-white.rounded.z-10.drop-shadow-md.divide-y
         {:style {:width width
                  :transition sidebar-transition
                  :left (if visible? 0 (- width))}}]
        (map (fn [{:keys [curriculum/path
                          name
                          title
                          description]}]
               [:div.p-2.text-sm title]))
        (db/where [:curriculum/path])))

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
  (let [sidebar? (ui/use-> !sidebar-state :visible?)
        sidebar-width 250
        !docbar (h/use-memo #(r/atom nil))]
    [:<>
     (ui/use-global-keymap {:mod-k (fn [& _] (prn 2) (command-bar/toggle!))
                            :shift-mod-k (fn [& _] (prn 1) (swap! !sidebar-state update :visible? not))})
     [command-bar/view]
     [:div
      {:style {:padding-left (if sidebar? sidebar-width 0) :transition sidebar-transition}}
      [sidebar {:visible? sidebar? :width sidebar-width}]
      [prose/editor {:source ex/examples
                     :make-sci-ctx sci/initial-context
                     :!docbar !docbar}]
      [:div.fixed.bottom-0.right-0
       {:style {:left (if sidebar? sidebar-width 0)
                :transition sidebar-transition}}
       [eldoc/view !docbar]]]]))

(defn ^:export init []
  (init-re-db)
  (root/create :maria-live (v/x [landing])))