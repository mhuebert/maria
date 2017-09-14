(ns maria.user
  (:require [re-view.core]))

(defmacro user-macro [& body]
  `[~@body])


