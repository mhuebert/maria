(ns maria.editor.extensions.reagent
  (:require [sci.ctx-store :as ctx]
            [sci.core :as sci]
            [sci.configs.reagent.reagent :as reagent.sci]))

(defn install! []
  (ctx/swap-ctx! sci/merge-opts reagent.sci/config))