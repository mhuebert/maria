(ns maria.show-values.katex
  (:require ["katex" :as katex]
            [yawn.hooks :as h]
            [yawn.view :as v]))

;; TODO - load this css
;; {:href "https://cdn.jsdelivr.net/npm/katex@0.16.2/dist/katex.min.css"}
(v/defview show-katex [el x]
           [el {:ref (h/use-callback
                      (fn [el]
                        (when el
                          (katex/render x el))))}])