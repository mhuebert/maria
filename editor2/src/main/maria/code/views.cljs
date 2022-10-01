(ns maria.code.views
  (:require [applied-science.js-interop :as j]
            [yawn.view :as v]
            [yawn.convert :as c :refer [IElement]]
            [shapes.core :as shapes]
            ["react" :as react]
            [shadow.cljs.modern :refer [defclass]]))

(v/defview render-error-boundary [this]
  (j/let [^js {{[error-view body] :children} :props
               {:keys [error]} :state} this]
    (if error
      [error-view error]
      [:div body])))

(defclass ErrorBoundary
  (extends react/Component)
  (constructor [this] (super))
  Object
  (render [^js this] (render-error-boundary this)))

(j/!set ErrorBoundary :getDerivedStateFromError (fn [error]
                                                  (j/log :found-error error)
                                                  #js{:error error}))

(extend-protocol IElement
  shapes/Shape
  (to-element [this]
    (v/x (shapes/to-hiccup this))))

(defn use-watch [ref]
  (let [[value set-value!] (react/useState [nil @ref])]
    (react/useEffect
     (fn []
       (add-watch ref set-value! (fn [_ _ old new] (set-value! [old new])))
       #(remove-watch ref set-value!)))
    value))

(v/defview error-viewer [error]
  (ex-message error))

(v/defview value-viewer [!result]
  (when-let [{:as result :keys [value error]} (second (use-watch !result))]
    [:...
     (if error
       [error-viewer error]
       (j/lit [ErrorBoundary {:key result}
               error-viewer
               (cond (string? value) (pr-str value)
                     (number? value) value
                     (boolean? value) (pr-str value)
                     :else value)]))]))

(v/defview code-row [^js {:keys [!result mounted!]}]
  (let [ref (react/useCallback (fn [el]
                                 (when el (mounted! el))))]
    [:div.-mx-4.mb-4.md:flex
     {:ref ref}
     [:div {:class "md:w-1/2 text-base"
            :style {:color "#c9c9c9"}}]
     [:div
      {:class "md:w-1/2 text-sm"}
      [value-viewer !result]]]))