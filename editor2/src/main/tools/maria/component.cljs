(ns tools.maria.component
  (:require ["react" :as re]
            [applied-science.js-interop :as j]
            [tools.maria.hooks :as hooks]))

(defn with-element
  ([init] (with-element nil init))
  ([{:keys [el props] :or {el :div}} init]
   (j/let [!destroy (hooks/volatile nil)
           ref-fn (re/useCallback
                   (fn [el]
                     (when-let [f @!destroy] (when (fn? f) (f)))
                     (when el
                       (reset! !destroy (init el)))
                     nil))]
     [el (assoc props :ref ref-fn)])))