(ns maria.editor.extensions.emmy
  (:require [clojure.string :as str]
            [emmy.viewer.sci]
            [emmy.portal.css :refer [inject!] :rename {inject! inject-css!}]
            [emmy.viewer.css :refer [css-map]]
            [emmy.mafs]
            [maria.editor.extensions.reagent :as ext.reagent]
            [maria.editor.code.show-values :as show]
            [sci.ctx-store :refer [get-ctx]]))

(defn show-emmy [opts x]
  (when-let [m (meta x)]
    (cond (= emmy.mafs/default-viewer (:nextjournal.clerk/viewer m))
          (show/reagent-eval opts (emmy.viewer/expand x))

          (:portal.viewer/reagent? m)
          (show/reagent-eval opts x))))

(defn inject-css-source! [source]
  (let [style (.createElement js/document "style")]
    (set! (.-type style) "text/css")
    (.appendChild style (js/document.createTextNode source))
    (js/document.head.appendChild style)))

(defn install! []
  (show/add-global-viewers! (get-ctx) :before :vector [show-emmy])
  (ext.reagent/install!)
  (doseq [href (apply concat (vals css-map))]
    (if (= href "https://unpkg.com/mafs@0.17.0/font.css")
      (inject-css-source!
        ".MafsView { font-family: \"CMU Serif\", serif;}")
      (inject-css! href)))
  (emmy.viewer.sci/install!))