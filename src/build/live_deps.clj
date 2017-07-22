(ns build.live-deps
  (:require [cljs-live.bundle :as bundle]))

(defn -main [bundle-spec-path]
  (bundle/main bundle-spec-path))