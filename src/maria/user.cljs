(ns maria.user
  (:require re-view-hiccup.core
            maria.messages
            maria.repl-actions.loaders
            [maria.views.repl-shapes :as shapes :refer [show
                                                        circle
                                                        rectangle
                                                        assure-shape-seq
                                                        shape-bounds
                                                        bounds
                                                        shape->vector
                                                        colorize
                                                        position
                                                        group
                                                        line-up
                                                        stack
                                                        colors]]
            [re-view.core :include-macros true])
  (:require-macros [maria.user :refer [user-macro]]))

