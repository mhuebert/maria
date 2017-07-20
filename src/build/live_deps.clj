(ns build.live-deps
  (:require [cljs-live.bundle :as bundle]
            [cljs-live.clj-scratch :as scratch]))

(defn -main [bundle-spec-path]
  #_(bundle/build bundle-spec-path)
  (scratch/main)
  )