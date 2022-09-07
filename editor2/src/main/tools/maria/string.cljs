(ns tools.maria.string
  (:require [clojure.string :as str]))

(defn ensure-prefix [s pfx]
  (cond->> s
           (not (str/starts-with? s pfx)) (str pfx)))

(defn strip-prefix [s pfx]
  (cond-> s
          (str/starts-with? s pfx)
          (subs (count pfx))))