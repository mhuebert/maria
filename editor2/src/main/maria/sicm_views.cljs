(ns maria.sicm-views
  (:require [sicmutils.value :as value]
            [sicmutils.structure]
            [sicmutils.env :refer [simplify ->TeX]]
            ["katex" :as katex]
            [yawn.view :as v]))

(v/defview katex [x]
  [:div {:ref (v/use-callback
               (fn [el]
                 (when el
                   (katex/render x el))))}])

(def views [(fn [x]
              (when (or (value/numerical? x)
                        (instance? sicmutils.structure/Structure x))
                (-> x
                    simplify
                    ->TeX
                    str
                    katex)))])