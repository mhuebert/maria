(ns maria.cloud.live-frame
  (:require [applied-science.js-interop :as j]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [maria.editor.code-blocks.eldoc :as eldoc]
            [maria.editor.code-blocks.examples :as ex]
            [maria.editor.command-bar :as command-bar]
            [maria.editor.core :as prose]
            [maria.scratch]
            [yawn.root :as root]
            [yawn.view :as v]))

(def env (->> (js/document.querySelectorAll "[type='application/x-maria:env']")
              (map (comp edn/read-string (j/get :innerHTML)))
              (apply merge)))

(defn link [title href]
  [:a (cond-> {:href href}
              (str/starts-with? title "http")
              (assoc :target "_blank")) title])

(v/defview landing []
  [:<>
   [command-bar/view {:binding :mod-k}]
   [:div
    [prose/editor {:source ex/examples}]
    [eldoc/view]]])

(defn ^:export init []
  (root/create :maria-live (v/x [landing])))