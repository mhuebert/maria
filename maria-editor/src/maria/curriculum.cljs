(ns maria.curriculum
  (:require [clojure.set :as set]))

(defn maria-path [path]
  (js/encodeURIComponent (str "https://raw.githubusercontent.com/mhuebert/maria/master/" path)))

(def modules-by-path
  [["intro" "Learn Clojure with Shapes" (maria-path "curriculum/Learn Clojure with Shapes.cljs")]
   ["quickstart" "Editor Quickstart" (maria-path "curriculum/Editor Quickstart.cljs")]
   ["gallery" "Example Gallery" (maria-path "curriculum/Example Gallery.cljs")]])

(def module-ids (set (mapv last modules-by-path)))
(def module-slugs (set (mapv first modules-by-path)))

(def modules-owner {:username  "modules"
                    :local-url "/modules"})

(def as-gists (for [[_ friendly-name id] modules-by-path]
                {:db/id                id
                 :doc.owner/username   "modules"
                 :owner                modules-owner
                 :persistence/provider :maria/module
                 :persisted            {:owner modules-owner
                                        :id    id
                                        :files {friendly-name {}}}}))