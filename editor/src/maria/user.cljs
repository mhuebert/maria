(ns maria.user
  (:require chia.view.hiccup
            maria.friendly.messages
            [maria.friendly.kinds :refer [what-is]]
            goog.net.jsloader
            goog.crypt                                      ;; solely for `stringToUtf8ByteArray` in Shannon's Entropy lesson -- feel free to remove once we switch to a simple story for grabbing a single external dependency
            maria.user.loaders
            maria.repl-specials
            [cells.cell :as cell :refer [defcell
                                         cell
                                         with-view]]
            [cells.lib :refer [interval
                               timeout
                               fetch
                               geo-location]]
            [shapes.core :as shapes :refer [listen
                                            circle ellipse square rectangle triangle polygon polyline text image
                                            position opacity rotate scale
                                            colorize stroke stroke-width no-stroke fill no-fill
                                            color-names colors-named rgb hsl rescale
                                            layer beside above value-to-cell!
                                            #_gfish
                                            ;; are these internal only? -jar
                                            ;;assure-shape-seq shape-bounds bounds shape->vector
                                            ]]
            [cljs.spec.alpha :include-macros true]
            [cljs.spec.test.alpha :include-macros true]
            [chia.view :include-macros true]
            [applied-science.js-interop :include-macros true]))
