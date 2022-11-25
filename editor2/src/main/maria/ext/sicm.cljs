(ns maria.ext.sicm
  (:require [maria.ext.katex :as katex]
            [maria.show :as show]
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
  (-> ctx
      (sci/merge-opts sicm.sci/context-opts)
      (show/add-viewers :sicm viewers)))