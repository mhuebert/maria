(ns maria.code.views
  (:require [applied-science.js-interop :as j]
            ["react" :as react]
            [reagent.core :as reagent]))

(defn use-watch [ref]
  (let [[value set-value!] (react/useState [nil @ref])]
    (react/useEffect
     (fn []
       (add-watch ref set-value! (fn [_ _ old new] (set-value! [old new])))
       #(remove-watch ref set-value!)))
    value))

(defn value-viewer [!result]
  (when-let [result (second (use-watch !result))]
    (if-let [error (:error result)]
      (str "Error: " error) ;; TODO  format error
      (pr-str (:value result)))))

(j/defn code-row [^js {:keys [!result mounted!]}]
  (let [ref (react/useCallback (fn [el]
                                 (when el (mounted! el))))]
          [:div.-mx-4.mb-4.md:flex
           {:ref ref}
           [:div {:class "md:w-1/2 text-base"
                  :style {:color "#c9c9c9"}}]

           [:div
            {:class "md:w-1/2 text-sm"}
            [value-viewer !result]]]))