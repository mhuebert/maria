(ns build
  (:require [cljs-static.page :as page]
            [cljs-static.shadow :as shadow]))

(defn index-html []
  (page/root "Maria"
             {:meta {:viewport "width=device-width, initial-scale=1"}
              :scripts/head [{:src "https://cdn.tailwindcss.com?plugins=forms,typography,line-clamp"}
                             {:src "https://cdn.jsdelivr.net/npm/jsxgraph@1.4.6/distrib/jsxgraphcore.js"}]
              :styles [{:href "https://prosemirror.net/css/editor.css"}
                       {:href "https://cdn.jsdelivr.net/npm/katex@0.16.2/dist/katex.min.css"}]
              :props/html {:class "bg-[#eeeeee]"}
              :body [:div#maria-live]
              :scripts/body [{:src (shadow/module-path :editor :main)}]}))