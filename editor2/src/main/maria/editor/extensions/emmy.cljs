(ns maria.editor.extensions.emmy
  (:require [emmy.viewer.sci]
            [emmy.portal.css :refer [inject!]]
            [emmy.viewer.css :refer [css-map]]
            [promesa.core :as p]
            [sci.ctx-store :as ctx]
            [shadow.lazy :as lazy]))

(defn install! []
  ;; capture bound sci context
  (let [ctx (ctx/get-ctx)]
    ;; first install reagent (necessary for viewers)
    (p/let [install! (lazy/load (lazy/loadable maria.editor.extensions.reagent/install!))]
      (ctx/with-ctx ctx
        (install!)
        (doseq [href (apply concat (vals css-map))]
          (inject! href))
        (emmy.viewer.sci/install!)))))