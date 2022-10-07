(ns maria.scratch
  (:require [yawn.view :as v]))

(v/defview show [x])
(v/x (if true
       [show "x"]
       [show "y"]))