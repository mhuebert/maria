(ns maria.curriculum
  (:require [clojure.set :as set]))

(def modules-by-path {"Learn Clojure with Shapes" "6121050c023ad640688e1d0220c0f50d"
                      "Editor Quickstart"         "7fd1790db0ed178995bab207a2250f0e"
                      "Example Gallery"           "c41203d2f973c838ee0ee4aed32d0679"})
(def modules-by-id (set/map-invert modules-by-path))

(def modules-owner {:username  "modules"
                    :local-url "/modules"})

(def as-gists (for [[path id] modules-by-path]
                {:db/id               id
                 :gist.owner/username "modules"
                 :persisted           {:owner                modules-owner
                                       :id                   id
                                       :persistence/provider :maria/module
                                       :files                {path {}}}}))