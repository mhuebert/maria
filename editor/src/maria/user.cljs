(ns maria.user
  (:require re-view-hiccup.core
            maria.messages
            goog.net.jsloader
            maria.user.loaders
            maria.repl-specials
            maria.show
            [cells.cell]
            [cells.lib :as cell
             :refer [interval timeout fetch geo-location]
             :refer-macros [with-view wait]]
            [shapes.core :as shapes :refer [listen
                                            circle square rectangle triangle path text image
                                            position opacity rotate scale
                                            colorize stroke no-stroke fill no-fill
                                            color-names rgb hsl rescale
                                            layer beside above
                                            fish            ; for functional geometry demo
                                            ;; are these internal only? -jar
                                            assure-shape-seq shape-bounds bounds shape->vector]]
            [re-view.core :include-macros true])
  (:require-macros [cells.cell :refer [defcell cell]]))