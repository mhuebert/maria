(ns user
  (:require maria.friendly.messages
            [maria.friendly.kinds :refer [what-is]]
            [maria.eval.repl :refer [eval eval-string doc dir is-valid-element?]]
            sci.impl.resolve
            [shapes.core :as shapes :refer [listen
                                            circle ellipse square rectangle triangle polygon polyline text image
                                            position opacity rotate scale
                                            colorize stroke stroke-width no-stroke fill no-fill
                                            color-names colors-named rgb hsl rescale
                                            layer beside above value-to-cell!]]))


(comment
 [cells.cell :as cell :refer [defcell
                              cell
                              with-view]]
 [cells.lib :refer [interval
                    timeout
                    fetch
                    geo-location]]
 [cljs.spec.alpha :include-macros true]
 [cljs.spec.test.alpha :include-macros true]
 [applied-science.js-interop :include-macros true])

