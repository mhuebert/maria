(ns cloud.maria.build
  (:require [cljs-static.page :as page]
            [cljs-static.shadow :as shadow]))

(defn index-html []
  (page/root "Maria"
             {:meta {:viewport "width=device-width, initial-scale=1"}
              :styles [{:href "/css/tailwind.css"}
                       {:href "https://prosemirror.net/css/editor.css"}
                       ;; prosemirror md
                       ;; custom maria css
                       ]
              :body [:div#maria-env.h-100]
              :scripts/body [{:src (shadow/module-path :editor :main)}]}))