(ns cells.lib)

(defmacro with-view [cell view]
  `(~'cells.cell/with-view ~cell (fn [~'self]
                                   ~view)))