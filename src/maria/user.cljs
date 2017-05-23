(ns maria.user
  (:require
    [re-view-hiccup.core :refer [element] :rename {element html}]
    [maria.messages :as messages]
    [maria.repl-actions.loaders :refer [load-gist]]
    [maria.views.repl-shapes :as shapes :refer [show
                                                circle
                                                rectangle
                                                assure-shape-seq
                                                shape-bounds
                                                bounds
                                                shape->vector
                                                show
                                                colorize
                                                position
                                                group
                                                line-up
                                                stack
                                                colors]]

    [re-view.core :as v :include-macros true])
  (:require-macros
    [maria.user :refer [user-macro]]))
