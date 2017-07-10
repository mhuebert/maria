(ns maria.persistence.transit
  (:require [cognitect.transit :as t]
            [clojure.string :as string]))

(def reader (t/reader :json))
(def writer (t/writer :json))
(defn deserialize [x]
  (when (and (string? x) (string/starts-with? x "transit/json:"))
    (t/read reader (subs x 13))))
(defn serialize [x]
  (str "transit/json:" (t/write writer x)))