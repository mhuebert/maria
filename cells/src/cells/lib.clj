(ns cells.lib)

(defmacro wait
  "Returns cell with body wrapped in timeout of n milliseconds."
  [n & body]
  `(~'cells.lib/timeout ~n (fn [] ~@body)))


