(ns maria.curriculum
  (:require [clojure.set :as set]))

(def modules-by-path {"Learn Clojure with Shapes" (js/encodeURIComponent "https://raw.githubusercontent.com/mhuebert/maria/master/curriculum/Learn Clojure with Shapes.cljs")
                      "Editor Quickstart"         (js/encodeURIComponent "https://raw.githubusercontent.com/mhuebert/maria/master/curriculum/Editor Quickstart.cljs")
                      "Example Gallery"           (js/encodeURIComponent "https://raw.githubusercontent.com/mhuebert/maria/master/curriculum/Example Gallery.cljs")})
(def modules-by-id (set/map-invert modules-by-path))

(def modules-owner {:username  "modules"
                    :local-url "/modules"})

(def as-gists (for [[path id] modules-by-path]
                {:db/id                id
                 :doc.owner/username   "modules"
                 :owner                modules-owner
                 :persistence/provider :maria/module
                 :persisted            {:owner modules-owner
                                        :id    id
                                        :files {path {}}}}))