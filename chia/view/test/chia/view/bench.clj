(ns chia.view.bench
  (:require [hicada.compiler :as hicada]))

(defmacro hicada [body]
  (hicada/compile body {:create-element  'chia.view.bench/element
                        :transform-fn    (comp)
                        :array-children? false
                        {}               &env}))