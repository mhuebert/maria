(ns cells.lib.impl
  (:require [cells.cell :as cell]))

(defn- timeout
       ([n f] (timeout n f nil))
       ([n f initial-value]

        (let [self (first cell/*cell-stack*)
              _ (cell/status! self :loading)
              clear-key (js/setTimeout (cell/cell-fn []
                                                     (cell/status! self nil)
                                                     (reset! self (f @self))) n)]
             (on-dispose self #(js/clearTimeout clear-key))
             initial-value)))