(ns maria.editor.extensions.katex
  (:require ["katex" :as katex]
            [yawn.hooks :as h]
            [yawn.view :as v]))

(defonce !load-css! (delay
                      (let [link (.createElement js/document "link")]
                        (set! (.-rel link) "stylesheet")
                        (set! (.-href link) "https://cdn.jsdelivr.net/npm/katex@0.16.2/dist/katex.min.css")
                        (js/document.head.appendChild link))))

(v/defview show-katex [el x]
  @!load-css!
  [el {:ref (h/use-callback
              (fn [el]
                (when el
                  (katex/render x el))))}])