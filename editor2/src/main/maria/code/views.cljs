(ns maria.code.views
  (:require ["react" :as react]
            [applied-science.js-interop :as j]
            [yawn.view :as v]
            [shapes.core :as shapes]
            [sci.lang]
            [nextjournal.clerk.viewer :as clerk.viewer]
            [cells.async]
            maria.sicm-views
            [maria.util :refer [use-watch]]
            [maria.show :refer [show]]
            [shadow.cljs.modern :refer [defclass]]))

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

(clerk.viewer/reset-viewers! :default (clerk.viewer/add-viewers clerk.viewer/default-viewers
                                                                [{:pred #(shape? (cond-> % (coll? %) first))
                                                                  :transform-fn clerk.viewer/mark-presented
                                                                  :render-fn #(show nil %)}]))

(v/defview value-viewer [!result]
  (let [{:as result :keys [value error key]} (use-watch !result)]
    [:... {:key key}
     (if error
       (show nil error)
       (j/lit [ErrorBoundary {:key key
                              :render #(show nil %)
                              :value value}]))]))

(v/defview code-row [^js {:keys [!result mounted!]}]
  (let [ref (v/use-callback (fn [el] (when el (mounted! el))))]
    [:div.-mx-4.mb-4.md:flex.w-full
     {:ref ref}
     [:div {:class "md:w-1/2 text-base"
            :style {:color "#c9c9c9"}}]
     [:div
      {:class "md:w-1/2 font-mono text-sm m-3 md:my-0 max-h-screen overflow-auto pb-4"}
      [value-viewer !result]]]))
