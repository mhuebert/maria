(ns maria.ext.katex
  (:require ["katex" :as katex]
            [yawn.hooks :as h]
            [yawn.view :as v]))

(v/defview show-katex [el x]
           [el {:ref (h/use-callback
                      (fn [el]
                        (when el
                          (katex/render x el))))}])