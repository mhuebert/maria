(ns cells.util
  #?(:cljs (:require [re-db.d :as d])))

(defn unique-id []
  #?(:cljs (d/unique-id)
     :clj  (str (java.util.UUID/randomUUID))))