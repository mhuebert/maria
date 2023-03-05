(ns maria.code.views
  (:require ["react" :as react]
            [applied-science.js-interop :as j]
            [cells.async]
            [maria.show :refer [show]]
            [sci.lang]
            [shadow.cljs.modern :refer [defclass]]
            [shapes.core :as shapes]
            [yawn.hooks :as h]
            [yawn.view :as v]))

(defclass ErrorBoundary
  (extends react/Component)
  (constructor [this] (super))
  Object
  (render [^js this]
          (j/let [^js {{:keys [error]} :state
                       {:keys [render value]} :props} this]
            (try (render (or error value))
                 (catch js/Error e
                   (js/console.debug (.-stack e))
                   (render e))))))

(j/!set ErrorBoundary :getDerivedStateFromError (fn [error]
                                                  (j/log :found-error)
                                                  #js{:error error}))

(defn shape? [x] (instance? shapes/Shape x))

(v/defview value-viewer [this]
  (let [{:keys [value error key]} (h/use-deref (j/get this :!result))]
    [:... {:key key}
     (if error
       (show {:node-view this} error)
       (j/lit [ErrorBoundary {:key key
                              :render #(show {:node-view this} %)
                              :value value}]))]))

(v/defview code-row [^js {:as this :keys [!result mounted! id]}]
  (let [ref (h/use-callback (fn [el] (when el (mounted! el))))]
    [:div.-mx-4.mb-4.md:flex.w-full
     {:ref ref :id id}
     [:div {:class "md:w-1/2 text-base"
            :style {:color "#c9c9c9"}}]
     [:div
      {:class "md:w-1/2 font-mono text-sm m-3 md:my-0 max-h-screen overflow-auto pb-4"}
      [value-viewer this]]]))
