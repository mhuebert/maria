(ns maria.show)

(defprotocol IShow
  (show [this] "Return a version of `this` suitable for display."))