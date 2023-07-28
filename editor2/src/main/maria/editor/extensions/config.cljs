(ns maria.editor.extensions.config
  (:require [shadow.lazy :as lazy]
            [shadow.loader]))

;; feel free to submit a PR with additional extension config here

(def loadables
  "A map of shadow lazy modules to namespace prefixes that they provide."
  {:reagent {:loadable (lazy/loadable maria.editor.extensions.reagent/install!)
             :provides '[reagent]}
   :emmy {:loadable (lazy/loadable maria.editor.extensions.emmy/install!)
          :provides '[leva
                      emmy
                      mafs
                      jsxgraph
                      mathbox
                      mathlive]
          :depends-on #{:reagent}}})

(defn get-load-order [dep-name loadables]
  (let [dep (get loadables dep-name)
        deps (set (:depends-on dep))]
    (concat
      (mapcat #(get-load-order % loadables) deps)
      [(:loadable dep)])))