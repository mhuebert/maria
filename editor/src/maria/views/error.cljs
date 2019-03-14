(ns maria.views.error
  (:require ["react" :as react]
            [goog.object :as gobj]
            [chia.view :as v]
            [chia.view.legacy :as vlegacy]
            [maria.eval :as e]
            [applied-science.js-interop :as j]))

(defn- make-error-handler [{:keys [key
                                   error-content
                                   block-id
                                   on-error]} view]
  (let [error-handler (fn error-handler [props context updater]
                        (this-as this
                          (react/Component.call this props context updater)
                          (j/assoc! this
                                    :componentDidCatch
                                    (fn [error info]
                                      (when on-error (on-error {:error error
                                                                :info info})))
                                    :render
                                    (fn []
                                      (v/to-element
                                       [view (-> (j/get this :state)
                                                 (js->clj :keywordize-keys true)
                                                 (assoc :key key))])))))]

    (gobj/extend (j/get error-handler :prototype)
                 (j/get react/Component :prototype))

    (j/assoc! error-handler
              :getDerivedStateFromError (fn [error info] #js {:error error
                                                              :info info}))

    error-handler))

(v/defn error-boundary [{:as props
                         :keys [error-content
                                block-id
                                on-error]} child]
  (v/-create-element
   (make-error-handler props
     (fn [{:keys [error
                  info]}]
       (if-not error
         child
         (cond error-content (error-content {:error error
                                             :info info})

               [:.bg-washed-red.flex.items-center.tc.pa2 (or (.-message error)
                                                             "Render error")]
               child)))) #js{:key key} nil))

