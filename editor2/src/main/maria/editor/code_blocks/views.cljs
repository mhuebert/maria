(ns maria.editor.code-blocks.views
  (:require ["react" :as react]
            [applied-science.js-interop :as j]
            [cells.async]
            [maria.editor.code-blocks.show-values :refer [show]]
            [sci.core :as sci]
            [sci.lang]
            [shadow.cljs.modern :refer [defclass]]
            [shapes.core :as shapes]
            [yawn.hooks :as h]
            [yawn.view :as v]
            [cljs.tools.reader.edn :as edn]
            [maria.editor.code-blocks.commands :as commands]))

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

(defn eye-slash [class]
  (v/x
   [:svg {:class class :xmlns "http://www.w3.org/2000/svg" :viewBox "0 0 20 20" :fill "currentColor"}
    [:path {:fillRule "evenodd" :d "M3.28 2.22a.75.75 0 00-1.06 1.06l14.5 14.5a.75.75 0 101.06-1.06l-1.745-1.745a10.029 10.029 0 003.3-4.38 1.651 1.651 0 000-1.185A10.004 10.004 0 009.999 3a9.956 9.956 0 00-4.744 1.194L3.28 2.22zM7.752 6.69l1.092 1.092a2.5 2.5 0 013.374 3.373l1.091 1.092a4 4 0 00-5.557-5.557z" :clipRule "evenodd"}]
    [:path {:d "M10.748 13.93l2.523 2.523a9.987 9.987 0 01-3.27.547c-4.258 0-7.894-2.66-9.337-6.41a1.651 1.651 0 010-1.186A10.007 10.007 0 012.839 6.02L6.07 9.252a4 4 0 004.678 4.678z"}]]))

(defn eye [class]
  (v/x
   [:svg {:class class :xmlns "http://www.w3.org/2000/svg" :viewBox "0 0 20 20" :fill "currentColor"}
    [:path {:d "M10 12.5a2.5 2.5 0 100-5 2.5 2.5 0 000 5z"}]
    [:path {:fillRule "evenodd" :d "M.664 10.59a1.651 1.651 0 010-1.186A10.004 10.004 0 0110 3c4.257 0 7.893 2.66 9.336 6.41.147.381.146.804 0 1.186A10.004 10.004 0 0110 17c-4.257 0-7.893-2.66-9.336-6.41zM14 10a4 4 0 11-8 0 4 4 0 018 0z" :clipRule "evenodd"}]]))

(v/defview code-row [^js {:as this :keys [!result mounted! id initialNs]}]
  (let [ref (h/use-callback (fn [el] (when el (mounted! el))))
        !minimize? (h/use-state initialNs)]
    (if @!minimize?
      [:div.text-sm.text-zinc-400.font-mono.hover:underline.cursor-pointer
       {:class "md:ml-[50%]"
        :on-click #(reset! !minimize? false)}
       "show ns" #_(str "ns: " (second (edn/read-string (commands/code:block-str this))))]
      [:div.my-4.md:flex.w-full
       {:ref ref :id id}
       [:div {:class "md:w-1/2 text-base"
              :style {:color "#c9c9c9"}}]
       [:div
        {:class "md:w-1/2 font-mono text-sm md:ml-3 mt-3 md:mt-0 max-h-screen overflow-auto"}
        [value-viewer this]]])))
