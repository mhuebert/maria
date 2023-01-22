(ns maria.live
  (:require [clojure.string :as str]
            [maria.code.eldoc :as eldoc]
            [maria.examples :as ex]
            [maria.prose.editor :as prose]
            [maria.scratch]
            [shadow.resource :as rc]
            [yawn.view :as v]
            [yawn.root :as root]))

(defn link [title href]
  [:a (cond-> {:href href}
              (str/starts-with? title "http")
              (assoc :target "_blank")) title])

(def requires
  (str '(ns maria.examples
          (:require [shapes.core :refer :all]
                    [cells.api :refer :all]))))

(def example
  [prose/editor {:source
                 (do
                   (str
                    ex/emoji
                    ex/link
                    ex/repl
                    ex/namespaces
                    ex/cell
                    ex/collections
                    ex/error
                    ex/sci-errors
                    ex/promise
                    ex/string
                    ex/js-interop
                    ex/yawn
                    )










                   #_(rc/inline "maria/curriculum/learn_clojure_with_shapes.cljs")
                   #_(rc/inline "maria/curriculum/welcome_to_cells.cljs")
                   #_(rc/inline "maria/curriculum/animation_quickstart.cljs")
                   #_(rc/inline "maria/curriculum/example_gallery.cljs")

                   )}])


(v/defview landing []
  [:div
   example
   [eldoc/view]])

(defn ^:export init []
  (root/create :maria-live (v/x [landing])))