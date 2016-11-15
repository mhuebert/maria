(ns maria.user
  (:require-macros [maria.user :refer [user-macro]]))

(defn user-f [x]
  (take 10 (repeat x)))