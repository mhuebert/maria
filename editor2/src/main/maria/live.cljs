(ns maria.live
  (:require ["react-dom/client" :as react.client]
            [applied-science.js-interop :as j]
            [maria.prose.editor :as prose]
            [tools.maria.dom :as dom]
            [reagent.core :as reagent]
            [clojure.string :as str]
            [maria.style :as style]
            maria.scratch
            [tools.maria.react-roots :as roots]))

(defonce !root (delay (react.client/createRoot
                       (dom/find-or-create-element :maria-live))))

(defonce fn-compiler (doto (reagent/create-compiler {:function-components true})
                       (reagent/set-default-compiler!)))

(defn link [title href]
  [:a (cond-> {:href href}
              (str/starts-with? title "http")
              (assoc :target "_blank")) title])

(def example
  [prose/editor {:source "
;; # Hello, world...

(doc circle)

(dir shapes.core)

#_(do 10)

{:a 10}

(defn my-fn \"A docstring\" []
 1 ;; number
 :abra ;; keyword
 cadabra ;; symbol
 #\"regex\" ;; regex
 \"string\"
 \\c ;; character
  ...)

;; Another Paragraph

(prn 10)

;; More text"}])

(defn landing []
  [:div
   example
   style/tailwind]
  )

(defn render []
  (roots/init! @!root #(reagent/as-element [landing])))