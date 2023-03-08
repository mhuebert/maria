(ns maria.curriculum
  (:require [babashka.fs :as fs]
            [edamame.core :as eda]
            [clojure.string :as str]))

(defn parse-ns-meta [file]
  (let [src (slurp file)
        [_ns name & args :as form] (eda/parse-string src)
        opts (merge {:name name} (dissoc (meta name) :row :col :end-row :end-col))
        [opts args] (if (string? (first args))
                      [(assoc opts :doc (first args)) (rest args)]
                      [opts args])
        [opts args] (if (map? (first args))
                      [(merge opts (first args)) (rest args)]
                      [opts args])
        opts (if-let [title (or (:title opts)
                                (->> (str/split-lines src)
                                     (drop (:end-row (meta form)))
                                     (drop-while str/blank?)
                                     first
                                     (re-find #";+\s+#+\s*(.*)")
                                     second))]
               (assoc opts :title title)
               opts)]

    opts))

(defn read-curriculum-namespaces []
  (->> (fs/file "src/main/maria/curriculum")
       fs/list-dir
       (mapv (comp #(-> (parse-ns-meta %)
                        (assoc :path (str "/cljs/" (fs/file-name %))))
                   fs/file))))

(defmacro curriculum-namespaces []
  (read-curriculum-namespaces))

(comment
 (read-curriculum-namespaces)
 :=>
 [{:name maria.curriculum.animation-quickstart,
   :description "Get a running start at making basic animations using the Shapes library.",
   :title "Animations Quick-Start",
   :path "/cljs/animation_quickstart.cljs"}
  {:name maria.curriculum.welcome-to-cells,
   :title "Cells Quickstart",
   :description "Learn how to make things come alive and change over time.",
   :path "/cljs/welcome_to_cells.cljs"}
  {:name maria.curriculum.example-gallery,
   :description "See what other people have made using Maria",
   :title "Example Gallery",
   :path "/cljs/example_gallery.cljs"}
  {:name maria.curriculum.learn-clojure-with-shapes,
   :title "Learn Clojure with Shapes",
   :description "Your journey begins here!",
   :path "/cljs/learn_clojure_with_shapes.cljs"}]
 )

