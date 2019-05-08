(ns maria.views.hoc
  (:require [chia.view :as v]
            [chia.view.hiccup :as hiccup]
            [applied-science.js-interop :as j]
            [goog.dom :as gdom]
            ["react-dom" :as react-dom]))

(v/defclass bind-atom
                 "Calls component with value of atom & re-renders when atom changes."
                 {:key               (fn [_ _ prop-atom]
                                       (let [args @prop-atom
                                             {:keys [key id name]} (if (map? args)
                                                                     args
                                                                     (first args))]
                                         (or key id name)))
                  :view/did-mount    (fn [this component atom]
                                       (some-> atom
                                               (add-watch this (fn [_ _ old new]
                                                                 (when (not= old new)
                                                                   (v/force-update this))))))
                  :view/will-unmount (fn [this _ atom]
                                       (some-> atom
                                               (remove-watch this)))}
                 [_ component prop-atom]
                 (let [args @prop-atom]
                   (cond (vector? args)
                         (apply component args)
                         (map? args)
                         (component args)
                         :else
                         (throw (js/Error (str "Invalid format of prop atom, should be vector or map: " args))))))

(defn get-frame-element [this]
  (-> (v/dom-node this)
      (j/get-in [:contentDocument :body])
      (gdom/getFirstElementChild)))

(defn render-frame! [this content]
  (v/render-to-dom (hiccup/element
                    [:div
                     [:link {:type "text/css"
                             :rel "stylesheet"
                             :href "/app.css"}]
                     content]) (get-frame-element this)))

(v/defclass Frame
                 "Renders component (passed in as child) to an iFrame."
                 {:spec/children [:Element]
                  :view/did-mount (fn [this content]
                                    (-> (v/dom-node this)
                                        (j/get-in [:contentDocument :body])
                                        (gdom/appendChild (gdom/createDom "div")))
                                    (render-frame! this content))
                  :view/will-unmount (fn [this]
                                       (react-dom/unmountComponentAtNode (get-frame-element this)))}
                 [{:keys [view/props]} component]
                 [:iframe.bn.shadow-2 props])