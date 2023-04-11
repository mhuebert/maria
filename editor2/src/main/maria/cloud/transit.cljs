(ns maria.cloud.transit
  (:require [cognitect.transit :as t]))

(def reader (t/reader :json))
(def writer (t/writer :json))
(defn read [x] (t/read reader x))
(defn write [x] (t/write writer x))