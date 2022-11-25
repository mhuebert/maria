(ns maria.ext.sicm
  (:require [maria.ext.katex :as katex]
            [sci.core :as sci]
            [sicmutils.env :refer [->TeX simplify]]
            [sicmutils.env.sci :as sicm.sci]
            [sicmutils.structure]
            [sicmutils.value :as value]))

(def viewers [(fn [opts x]
              (when (or (value/numerical? x)
                        (instance? sicmutils.structure/Structure x))
                (-> x
                    simplify
                    ->TeX
                    str
                    (->> (katex/show-katex :span)))))])

(defn init [ctx]

  ;; TODO
  ;; install viewers
  (sci/merge-opts ctx sicm.sci/context-opts))