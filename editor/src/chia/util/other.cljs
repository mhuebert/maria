(ns chia.util.other
  "Temp namespace for stuff that should go somewhere else")

(defn focus-first-input [^js root-element]
  (some-> root-element
          (.querySelector "textarea, input, input[type=text], select")
          (.focus)))