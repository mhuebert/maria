(ns user
  (:require maria.friendly.messages
            [cells.cell :refer [defcell cell]]
            [cells.lib :refer :all]
            [shapes.core :as shapes :refer :all]))

(comment
 [cells.cell :as cell :refer [defcell
                              cell
                              with-view]]
 [cells.lib :refer [interval
                    timeout
                    fetch
                    geo-location]]
 [cljs.spec.alpha :include-macros true]
 [cljs.spec.test.alpha :include-macros true]
 [applied-science.js-interop :include-macros true])

