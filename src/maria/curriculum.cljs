(ns maria.curriculum
  (:require [clojure.set :as set]))

(def modules-by-path {"intro"   "6121050c023ad640688e1d0220c0f50d"
                      "gallery" "c41203d2f973c838ee0ee4aed32d0679"})
(def modules-by-id (set/map-invert modules-by-path))

(def modules-owner {:username  "modules"
                    :maria-url "/modules"})

(def as-gists (for [[path id] modules-by-path]
                {:owner modules-owner
                 :id    id
                 :files {path {}}}))