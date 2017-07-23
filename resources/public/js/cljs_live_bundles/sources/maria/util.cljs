(ns maria.util)

(defn some-str [s]
  (when (and (string? s) (not (identical? s "")))
    s))