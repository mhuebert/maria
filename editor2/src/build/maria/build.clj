(ns maria.build
  (:require [cljs-static.page :as page]
            [cljs-static.shadow :as shadow]))

(defn index-html []
  (page/root "Maria"
             {:meta {:viewport "width=device-width, initial-scale=1"}
              :styles [{:href "https://prosemirror.net/css/editor.css"}
                       {:href "/css/tailwind.css"}]
              :scripts/head [{:src "https://polyfill.io/v3/polyfill.min.js?version=3.111.0&features=URLSearchParams%2CURL"}]
              :props/html {:class "bg-[#eeeeee]"}
              :body [:div#maria-live]
              :scripts/body [{:src (shadow/module-path :editor :main)}]}))

