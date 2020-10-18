(ns chia.util.dev-errors
  (:require [cljs.repl :as repl]
            [applied-science.js-interop :as j]))

(def devtools-error-formatter
  "Uses cljs.repl utilities to format ExceptionInfo objects in Chrome devtools console."
  #js{:header
      (fn [object config]
        (when (instance? ExceptionInfo object)
          (let [message (some->> (repl/error->str object)
                                 (re-find #"[^\n]+"))]
            #js["span" message])))
      :hasBody (constantly true)
      :body (fn [object config]
              #js["div" (repl/error->str object)])})

(defn install-formatter! []
  (-> js/window
      (j/get :devtoolsFormatters)
      (j/unshift! devtools-error-formatter)))

