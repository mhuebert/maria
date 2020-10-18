(ns chia.view.render-loop
  (:require ["react" :as react]
            ["react-dom" :as react-dom]
            [applied-science.js-interop :as j]
            [chia.view.fps :as fps]
            [chia.view.impl :as impl]
            [chia.util :as u]))

(def -batch react-dom/unstable_batchedUpdates)

(def -raf (or (when (exists? js/window)
                (or (j/get js/window :requestAnimationFrame)
                    (j/get js/window :webkitRequestAnimationFrame)
                    (j/get js/window :mozRequestAnimationFrame)
                    (j/get js/window :oRequestAnimationFrame)
                    (j/get js/window :msRequestAnimationFrame)))
              (fn [cb]
                (js/setTimeout cb (/ 1000 60)))))

;; When true, updates will not be queued.
(defonce ^:dynamic *immediate-updates* false)

(defonce to-render (volatile! #{}))

(defn dequeue! [view]
  (j/assoc! view .-chia$toUpdate false))

;;;;;;;;;;;;;;;;;;
;;
;; Render loop

(defn- flush* []
  (when-let [views (seq @to-render)]
    (vreset! to-render #{})
    (doseq [c views]
      (when (and ^boolean (j/get c .-chia$toUpdate)
                 (not ^boolean (j/get c .-chia$unmounted)))
        (j/call c :forceUpdate)))))

(defn flush!
  "Render all queued updates immediately."
  []
  (-batch flush*))

(defn force-update
  "Force-updates `view` immediately."
  [view]
  (vswap! to-render disj view)
  (j/call view :forceUpdate))

(defn schedule-update!
  [view]
  "Queues a force-update for `component`"
  (if (true? *immediate-updates*)
    (force-update view)
    (do
      (j/assoc! view .-chia$toUpdate true)
      (vswap! to-render conj view)
      (-raf flush!))))

(defn apply-sync!
  "Wraps function `f` to flush the render loop before returning."
  [f]
  (fn [& args]
    (let [result (apply f args)]
      (flush!)
      result)))