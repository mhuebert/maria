(ns maria.cloud.markdown
  (:require ["markdown-it" :as md]
            [yawn.view :as v]))

(defonce ^js Markdown (md))

(defn show-markdown [source]
  (v/x [:div {:class "prose markdown-prose -my-4"}
        [:div {:dangerouslySetInnerHTML #js{:__html (.render Markdown source)}}]]))