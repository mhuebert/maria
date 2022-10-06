(ns maria.live
  (:require ["react-dom/client" :as react.client]
            ["react" :as react]
            [maria.prose.editor :as prose]
            [clojure.string :as str]
            [maria.style :as style]
            maria.scratch
            [shadow.resource :as rc]
            [yawn.view.dom :as dom]
            [maria.code.views :as code.views]
            [yawn.view :as v]
            [re-db.reactive :as r]
            [cells.lib :refer [interval timeout]]))

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

(def error-examples "
;; ## Other values

js/Promise

;; ## Errors

(10)

(.-x nil)
")

(def promise-examples
  "
;; ## Promises with top-level `await`

(p/delay 10000 (circle 40))

(await (p/delay 2000 (circle 40)))

;; Can reference previous vars

(def num-circles (await (p/delay 300 50)))

(->> (range num-circles)
     (map #(circle (* % 10))))
")

(def list-examples "
;; ## Collections

{1 2 3 4 5 6 7 8 9 10}

(list (range 1000) (range 1000) (range 1000))

[(circle 30)(triangle 330) (rectangle 500 10)]

{:hello \"world 👋\"
 :tacos (map #(repeat % '🌮) (range 1 30))
 :zeta \"The\npurpose\nof\nvisualization\nis\ninsight,\nnot\npictures.\"}")

(def cell-examples "

;; ## Cells

(defcell counter (interval 400 inc))

(cell
  (->> (repeat (circle 20))
       (take (rem @counter 6))))

(cell (fetch \"http://example.com/movies.json\"))

(cell (timeout 1000 \"hello\"))

")
(def example
  [prose/editor {:source
                 (do
                   "(is-valid-element? 1)
                   (is-valid-element? \"a\")\n"

                   ;; invalid element
                   #_"{1 2}\n(circle 10)"

                   (rc/inline "curriculum/Learn Clojure with Shapes.cljs")



                   (str
                    list-examples
                    cell-examples
                    error-examples
                    promise-examples)

                   )}])


(v/defview landing []
  [:div
     example
     style/tailwind])

(defn init []
  (dom/mount :maria-live #(v/x [landing])))