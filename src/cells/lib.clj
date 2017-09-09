(ns cells.lib)

(defmacro with-view
  "Wraps cell with view expression."
  [cell view]
  `(~'cells.cell/with-view ~cell (fn [~'self]
                                   ~view)))

(defmacro wait
  "Returns cell with body wrapped in timeout of n milliseconds."
  [n & body]
  `(~'cells.lib/timeout ~n (fn [] ~@body)))


