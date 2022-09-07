(ns maria.static
  (:require [cljs-static.page :as page]
            [cljs-static.shadow :as shadow]))

(defn index []
  (page/root "Maria"
             {:meta {:viewport "width=device-width, initial-scale=1"}
              :styles [{:href "/trusted.css"}]
              :body [:div#maria-index]
              :scripts/body [{:src (shadow/module-path :trusted :shadow-trusted)}]}))

(defn live []
  (page/root "Maria"
             {:meta {:viewport "width=device-width, initial-scale=1"}
              :styles [{:href "/tachyons.min.css"}
                       {:href "/codemirror.css"}
                       {:href "/maria.css"}]
              :body [:div#maria-env.h-100]
              :scripts/body [{:src (shadow/module-path :live :shadow-live)}]}))