(ns tools.maria.hooks
  (:require [applied-science.js-interop :as j]
            ["react" :as react]))

(defn volatile [init]
  (j/let [^js {:keys [current] :as ref} (react/useRef init)]
    (reify
      IDeref
      (-deref [_] current)
      IReset
      (-reset! [_ new-val] (j/!set ref :current new-val))
      ISwap
      (-swap! [o f] (j/!set ref :current (f current)))
      (-swap! [o f a] (j/!set ref :current (f current a)))
      (-swap! [o f a b] (j/!set ref :current (f current a b)))
      (-swap! [o f a b xs] (j/!set ref :current (apply f current a b xs))))))