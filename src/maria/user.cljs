(ns maria.user
  (:require re-view-hiccup.core
            maria.messages
            goog.net.jsloader
            maria.user.loaders
            maria.repl-specials
            [cells.cell]
            [cells.lib :as cell]
            [maria.user.shapes :as shapes :refer [show
                                                  circle square rectangle triangle path text
                                                  position opacity rotate scale
                                                  colorize stroke no-stroke fill no-fill
                                                  color-names rgb hsl rescale
                                                  layer beside above
                                                  fish      ; for functional geometry demo
                                                  ;; are these internal only? -jar
                                                  assure-shape-seq shape-bounds bounds shape->vector]]
            [re-view.core :include-macros true])
  (:require-macros [maria.user :refer [user-macro]]
                   [cells.cell :refer [defcell cell]]))