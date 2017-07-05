(ns maria.user
  (:require re-view-hiccup.core
            maria.messages
            maria.user.loaders
            maria.repl-specials
            [maria.user.shapes :as shapes :refer [show
                                                  circle
                                                  square
                                                  rectangle
                                                  triangle
                                                  text
                                                  rotate
                                                  assure-shape-seq
                                                  shape-bounds
                                                  bounds
                                                  shape->vector
                                                  colorize
                                                  position
                                                  group
                                                  line-up
                                                  stack
                                                  color-names
                                                  rgb rgba
                                                  hsl hsla]]
            [re-view.core :include-macros true])
  (:require-macros [maria.user :refer [user-macro]]))
