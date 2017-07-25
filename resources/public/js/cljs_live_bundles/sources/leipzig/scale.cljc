(ns leipzig.scale
  #?(:clj (:require [clojure.math.numeric-tower :as math])))

(defn- sum-n [series n] (apply + (take n series)))

(def floor
  #?(:clj #(math/floor %)
     :cljs #(.floor js/Math %)))

(def ceil
  #?(:clj #(math/ceil %)
     :cljs #(.ceil js/Math %)))

(defmulti scale-of
  (fn [intervals degree]
    (cond 
      (not= degree (floor degree)) :fraction
      (neg? degree)                     :negative
      :otherwise                        :natural)))

(defn scale [intervals] (partial scale-of intervals))

(defmethod scale-of :natural [intervals degree]
  (sum-n (cycle intervals) degree))
(defmethod scale-of :negative [intervals degree]
  (->> degree - (scale-of (reverse intervals)) -))
(defmethod scale-of :fraction [intervals degree]
  (let [lower (scale-of intervals (floor degree))
        upper (scale-of intervals (ceil degree))
        fraction (- degree (floor degree))]
  (+ lower (* fraction (- upper lower)))))

(def major "Seven-tone scale, commonly used in Western music." (scale [2 2 1 2 2 2 1]))
(def blues "Six-tone scale, used for blues music." (scale [3 2 1 1 3 2]))
(def pentatonic "Five-tone scale, common to East Asian music."(scale [3 2 2 3 2]))
(def chromatic "Scale consisting of all twelve tones." (scale [1]))

(defn from [base] (partial + base))

(def C (from 60))
(def D (from 62))
(def E (from 64))
(def F (from 65))
(def G (from 67))
(def A (from 69))
(def B (from 71))

(def sharp inc)
(def flat dec)

(defn mode [scale n] (comp #(- % (scale n)) scale (from n)))

(def ionian (mode major 0))
(def dorian (mode major 1))
(def phrygian (mode major 2))
(def lydian (mode major 3))
(def mixolydian (mode major 4))
(def aeolian (mode major 5))
(def locrian (mode major 6))

(def minor "Natural minor is another name for the Aeolian mode." aeolian)

(defn low
  "Lower midi one octave.
  e.g. (comp low D minor)"
  [midi]
  ((from -12) midi))

(defn high
  "Raise midi one octave.
  e.g. (comp high C major)"
  [midi]
  ((from 12) midi))

(defn lower
  "Lower degree one octave (assuming a heptatonic scale)."
  [degree]
  ((from -7) degree))

(defn raise
  [degree]
  "Raise degree one octave (assuming a heptatonic scale)."
  ((from 7) degree))
