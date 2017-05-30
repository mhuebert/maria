(ns maria.eval
  (:require [cljs.js :as cljs]
            [cljs-live.compiler :as c]
            [cljs-live.eval :as e :refer [defspecial]]
            [maria.views.repl-values :refer [repl-card]]

            [maria.repl-specials]))

(defonce c-state (cljs/empty-state))
(defonce c-env (atom {:ns (symbol "cljs.user")}))

(def eval (partial e/eval c-state c-env))
(def eval-str (partial e/eval-str c-state c-env))

(defonce _
         (do (set! cljs-live.compiler/debug? true)
             (c/load-bundles! ["/js/cljs_bundles/cljs.core.json"
                               "/js/cljs_bundles/maria.user.json"
                               #_"/js/cljs_bundles/cljs.spec.alpha.json"]
                              (fn []
                                (eval '(require '[cljs.core :include-macros true]))
                                (eval '(require '[maria.user :include-macros true]))

                                (eval '(inject 'cljs.core '{what-is          maria.messages/what-is
                                                            load-gist        maria.repl-actions.loaders/load-gist
                                                            html             re-view-hiccup.core/element

                                                            show             maria.views.repl-shapes/show
                                                            circle           maria.views.repl-shapes/circle
                                                            rectangle        maria.views.repl-shapes/rectangle
                                                            assure-shape-seq maria.views.repl-shapes/assure-shape-seq
                                                            shape-bounds     maria.views.repl-shapes/shape-bounds
                                                            bounds           maria.views.repl-shapes/bounds
                                                            shape->vector    maria.views.repl-shapes/shape->vector
                                                            colorize         maria.views.repl-shapes/colorize
                                                            position         maria.views.repl-shapes/position
                                                            group            maria.views.repl-shapes/group
                                                            line-up          maria.views.repl-shapes/line-up
                                                            stack            maria.views.repl-shapes/stack
                                                            colors           maria.views.repl-shapes/colors}))
                                (eval '(in-ns maria.user))))))







