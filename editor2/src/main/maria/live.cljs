(ns maria.live
  (:require ["react-dom/client" :as react.client]
            [maria.prose.editor :as prose]
            [clojure.string :as str]
            [maria.style :as style]
            maria.scratch
            [shadow.resource :as rc]
            [yawn.view.dom :as dom]
            [yawn.view :as v]))

(defn link [title href]
  [:a (cond-> {:href href}
              (str/starts-with? title "http")
              (assoc :target "_blank")) title])

(def syntax-example "
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

;; More text")

(def para-example "
;; One

;; Two
;; Three")

(def values-examples "

(p/delay 10000 :result)

(await (p/delay 1000 :result))

js/Promise

(def num-circles (await (p/do (p/delay 300) 5)))

(->> (range num-circles)
     (map #(circle (* 10 %))))

(10)

(.-x nil)
")

(def example
  [prose/editor {:source
                 (do
                   "(is-valid-element? 1)
                   (is-valid-element? \"a\")\n"

                   ;; invalid element
                   #_"{1 2}\n(circle 10)"

                   (rc/inline "curriculum/Learn Clojure with Shapes.cljs")

                   values-examples

                   )}])

(v/defview landing []
  [:div
   example
   style/tailwind])

(macroexpand '(v/defview landing []
                [:div
                 example
                 style/tailwind]))

(defn init []
  (dom/mount :maria-live #(v/x [landing])))