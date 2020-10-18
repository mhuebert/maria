(ns chia.util.string
  (:require [clojure.string :as str]))

(defn some-str [s]
  (when (and s (string? s) (not (str/blank? s)))
    s))

(defn ensure-prefix [s pfx]
  (cond->> s
           (not (str/starts-with? s pfx)) (str pfx)))

(defn trim-prefix [s prefix]
  (cond-> s
          (str/starts-with? s prefix) (subs (count prefix))))