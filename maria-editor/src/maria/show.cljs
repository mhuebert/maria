(ns maria.show)

(defprotocol IShow
  (show [this] "Returns an alternate form to represent `this` (will be further converted to a React element if it isn't already)"))