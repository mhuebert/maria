(ns user
  (:require maria.friendly.messages
            [maria.friendly.kinds :refer [what-is]]
            [maria.eval.repl :refer [eval eval-string]]
            sci.impl.resolve
            [shapes.core :as shapes :refer [listen
                                            circle ellipse square rectangle triangle polygon polyline text image
                                            position opacity rotate scale
                                            colorize stroke stroke-width no-stroke fill no-fill
                                            color-names colors-named rgb hsl rescale
                                            layer beside above value-to-cell!]]))


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

(defn ^:sci/macro doc
  "Show documentation for given symbol"
  [&form &env sym]
  (-> (sci.impl.resolve/resolve-symbol @maria.eval.repl/*context* sym)
      meta
      :doc))

(defn ^:sci/macro dir
  "Display public vars in namespace (symbol)"
  [&form &env ns]
  `'~(some->> @maria.eval.repl/*context*
              :env
              deref
              :namespaces
              (#(% ns))
              keys
              (filter symbol?)
              sort))