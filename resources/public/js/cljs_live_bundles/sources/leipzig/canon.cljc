(ns leipzig.canon
  (:require [leipzig.melody :refer [where with]]))

(defn canon
  "Accompanies notes with a melody created by applying f to notes.
  e.g. (->> leader (canon (simple 4)))"
  [f notes] (->> (f notes) (with notes)))

(defn- from [base] (partial + base))

(defn simple
  "Returns a transformation that delays a melody by wait."
  [wait] (partial where :time (from wait)))

(defn interval
  "Returns a transformation that raises a melody by interval."
  [interval] (partial where :pitch (from interval)))

(def mirror
  "A transformation that reflects a melody over pitch."
  (fn [notes] (->> notes (where :pitch -))))

(def crab
  "A transformation that reflects a melody over time."
  (fn [notes]
   (let [{start :time length :duration} (last notes)
         reflect (fn [{start :time length :duration :as note}]
                   (assoc note :time (- (+ start length))))]
     (->> notes
          (map reflect)
          (sort-by :time)
          (where :time (from (+ start length)))))))

(def table
  "A transformation that reflects a melody over time and pitch."
  (comp mirror crab))
