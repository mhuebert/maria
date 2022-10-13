(ns maria.live
  (:require [applied-science.js-interop :as j]
            ["react-dom/client" :as react.client]
            ["react" :as react]
            [maria.prose.editor :as prose]
            [clojure.string :as str]
            [maria.style :as style]
            maria.scratch
            [shadow.resource :as rc]
            [yawn.view.dom :as dom]
            [maria.code.views :as cv]
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

(p/delay 3000 (square 20))

;; You can await a `def` that contains a promise.

(await
  (def num-circles (p/delay 300 5)))

(->> (range 1 (inc num-circles))
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


;; ## Async state
(for [i (range 10)]
  (cell (timeout (* i 100) (circle 10))))

")


(def repl-examples ";; ## REPL utils
(doc inc)
(dir user)")

(def ns-examples "
;; Namespaces

(ns my.app)
(doc circle)
(ns user)
(doc circle)
")

(def sicm-examples "

;; ## SICM Utils
(ns maria.sicm
  (:require [sicmutils.env :refer :all]))

(take 10 (((exp D) sin) 'x))

(complex 1 2)

(square (sin (+ 'a 3)))
(+ 'Theta 'alpha)
(up
  'alphadot_beta
  'xdotdot
  'zetaprime_alphadot
  'alphaprimeprime_mubar
  'vbar
  'Pivec
  'alphatilde)
((D cube) 'x)

(defn L-central-polar [m U]
  (fn [[_ [r] [rdot thetadot]]]
    (- (* 1/2 m
          (+ (square rdot)
             (square (* r thetadot))))
       (U r))))

(let [potential-fn (literal-function 'U)
      L     (L-central-polar 'm potential-fn)
      state (up (literal-function 'r)
                (literal-function 'theta))]
  (((Lagrange-equations L) state) 't))

  (ns user)
")

(def requires
  (str '(ns maria.examples
          (:require [shapes.core :refer :all]
                    [cells.cell :refer [defcell cell]]
                    [cells.lib :refer :all]))))

(def example
  [prose/editor {:source
                 (do
                   (str


                    repl-examples
                    ns-examples
                    cell-examples
                    list-examples
                    error-examples
                    sicm-examples
                    promise-examples


                    )




                   #_(rc/inline "maria/curriculum/learn_clojure_with_shapes.cljs")
                   #_(rc/inline "maria/curriculum/welcome_to_cells.cljs")
                   #_(rc/inline "maria/curriculum/animation_quickstart.cljs")
                   #_(rc/inline "maria/curriculum/example_gallery.cljs")



                   )}])


(v/defview landing []
  [:div
   example
   style/tailwind])

(defn init []
  (dom/mount :maria-live #(v/x [landing])))