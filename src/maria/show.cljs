(ns maria.show
  (:require [cells.cell :as cell]))

(defprotocol IShow
  (show [this] "Return a version of `this` suitable for display."))

(extend-type cell/Cell
  IShow
  (show [this] @this))