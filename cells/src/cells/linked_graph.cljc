(ns cells.linked-graph
  (:require [clojure.set :as set]))

(defprotocol ILinkedGraph
  "Protocol for graphs that store relationships directly on nodes."
  (add-dependency! [cell dep])
  (remove-dependency! [cell dep])
  (add-dependent! [cell dep])
  (remove-dependent! [cell dep])
  (immediate-dependencies [cell])
  (immediate-dependents [cell]))

(defn transitive-sorted [f]
  (fn -transitive-sorted
    ([dep]
     (->> dep
          (-transitive-sorted [#{dep} []])
          (second)))
    ([[seen results] dep]
     (let [new (set/difference (f dep) seen)]
       (reduce -transitive-sorted
               [(into (conj seen dep) new)
                (-> results
                    (cond-> (not (seen dep)) (conj dep))
                    (into new))]
               new)))))

(def dependencies (transitive-sorted immediate-dependencies))
(def dependents (transitive-sorted immediate-dependents))

(defn depend! [node dep]
  (add-dependency! node dep)
  (add-dependent! dep node))

(defn un-depend! [node dep]
  (remove-dependency! node dep)
  (remove-dependent! dep node))

(defn transition-deps! [node next-dependencies]
  (let [prev-dependencies (immediate-dependencies node)]
    (doseq [added (set/difference next-dependencies prev-dependencies)]
      (depend! node added))
    (doseq [removed (set/difference prev-dependencies next-dependencies)]
      (un-depend! node removed))
    nil))