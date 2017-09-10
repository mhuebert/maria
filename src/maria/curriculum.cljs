(ns maria.curriculum
  (:require [clojure.set :as set]))

(def modules-by-path {"Intro"   "6121050c023ad640688e1d0220c0f50d"
                      "Gallery" "c41203d2f973c838ee0ee4aed32d0679"})
(def modules-by-id (set/map-invert modules-by-path))

(def modules-owner {:username  "modules"
                    :maria-url "/modules"})
