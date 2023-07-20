(ns maria.editor.extensions.config
  (:require [shadow.lazy :as lazy]))

;; feel free to submit a PR with additional extension config here

(def loadables
  "A map of shadow lazy modules to namespace prefixes that they provide."
  {(lazy/loadable maria.editor.extensions.reagent/install!) '[reagent]
   (lazy/loadable maria.editor.extensions.emmy/install!) '[leva
                                                           emmy
                                                           mafs
                                                           jsxgraph
                                                           mathbox
                                                           mathlive]})