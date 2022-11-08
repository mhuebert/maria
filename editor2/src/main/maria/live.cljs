(ns maria.live
  (:require ["react-dom/client" :as react.client]
            ["react" :as react]
            [maria.prose.editor :as prose]
            [clojure.string :as str]
            [maria.examples :as ex]
            maria.scratch
            [shadow.resource :as rc]
            [yawn.view.dom :as dom]
            [yawn.view :as v]
            [maria.code.eldoc :as eldoc]
            [nextjournal.react-hooks :as hooks]))

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
                    ex/sicm
                    ex/promise
                    ex/string
                    ex/js-interop
                    ex/view
                    )
                   ex/sci-errors



                   #_(rc/inline "maria/curriculum/learn_clojure_with_shapes.cljs")
                   #_(rc/inline "maria/curriculum/welcome_to_cells.cljs")
                   #_(rc/inline "maria/curriculum/animation_quickstart.cljs")
                   #_(rc/inline "maria/curriculum/example_gallery.cljs")

                   )}])


(v/defview landing []
  [:div
   example
   [eldoc/view]])

(v/defview hooks-test []
  (let [!ref (hooks/use-ref)]
    (hooks/use-effect
     (fn []
       (prn :effect @!ref)
       ))
    [:div {:ref !ref}]))

(defn init []
  (dom/mount :maria-live #(v/x #_[hooks-test]
                               [landing])))