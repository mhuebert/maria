(ns build
  (:require [cljs-static.page :as page]
            [cljs-static.shadow :as shadow]))

(defn index-html []
  (page/root "Maria"
             {:meta {:viewport "width=device-width, initial-scale=1"}
              :styles [{:href "https://prosemirror.net/css/editor.css"}
                       {:href "/css/tailwind.css"}]
              :props/html {:class "bg-[#eeeeee]"}
              :body [:div#maria-live]
              :scripts/body [{:src (shadow/module-path :editor :main)}]}))