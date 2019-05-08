(ns cells.util
  #?(:cljs (:require [cljs-uuid-utils.core :as uuid-utils])))

(defn unique-id []
  #?(:cljs (uuid-utils/make-random-uuid)
     :clj  (str (java.util.UUID/randomUUID))))
