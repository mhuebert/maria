(ns chia.view.fps
  (:require ["react" :as react]
            ["react-dom" :as react-dom]
            [applied-science.js-interop :as j]))

(goog-define count-fps?* false)                             ;; allows setting a default at compile time

(defonce ^:private count-fps? count-fps?*)
(defonce ^:private last-fps-time 1)
(defonce frame-rate 0)
(defonce frame-count 0)

(defonce fps-element
         (memoize (fn []
                    (-> (j/get js/document :body)
                        (.appendChild (doto (js/document.createElement "div")
                                        (.setAttribute "style" "padding: 3px 3px 0 0; font-size: 9px;")
                                        (.setAttribute "class" "fixed top-0 right-0 z-max monospace gray")))))))

(defn render-fps []
  (react-dom/render
    (react/createElement "div" #js {} (str (js/Math.floor frame-rate)))
    (fps-element)))

(defn measure-frame-rate!
  [value]
  (set! count-fps? value))

(defn tick! [frame-ms]
  (when ^boolean (true? count-fps?)
    (set! frame-count (inc frame-count))
    (when (identical? 0 (mod frame-count 29))
      (set! frame-rate (* 1000 (/ 30 (- frame-ms last-fps-time))))
      (set! last-fps-time frame-ms)
      (render-fps))))