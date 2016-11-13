(ns maria.core
  (:require
    [maria.eval :refer [eval-src]]
    [cljs.pprint :refer [pprint]]
    [maria.codemirror :refer [editor]]
    [re-db.d :as d]
    [re-view.subscriptions :as subs]
    [re-view.core :as v :refer-macros [defcomponent]]))

(enable-console-print!)

;; to support multiple editors
(defonce editor-id 1)

(defn display-result [{:keys [value error warnings]}]
  [:div.bb.b--near-white.ph3
   (cond error [:.pa3.dark-red.mv2 (str error)]
         (js/React.isValidElement value) value
         :else [:.bg-white.pv2.mv2 (if (nil? value) "nil" (with-out-str (prn value)))])
   (when (seq warnings)
     [:.bg-near-white.pa2.pre.mv2
      [:.dib.dark-red "Warnings: "]
      (for [warning (distinct (map #(dissoc % :env) warnings))]
        (str "\n" (with-out-str (pprint warning))))])])

(defn scroll-bottom [component]
  (let [el (js/ReactDOM.findDOMNode component)]
    (set! (.-scrollTop el) (.-scrollHeight el))))

(defcomponent repl-pane
              :component-did-update scroll-bottom
              :component-did-mount scroll-bottom
              :render
              (fn [this]
                [:div.h-100.overflow-auto
                 (map display-result (reverse (first (v/children this))))]))

(defcomponent app
              :subscriptions {:source      (subs/db [editor-id :source])
                              :eval-result (subs/db [editor-id :eval-result])}
              :render
              (fn [_ _ {:keys [eval-result source]}]
                [:.flex.flex-row.h-100
                 [:.w-50.h-100
                  (editor {:value         source
                           :event/keydown #(when (and (= 13 (.-which %2)) (.-metaKey %2))
                                            (d/transact! [[:db/update-attr editor-id :eval-result conj (eval-src (d/get editor-id :source))]]))
                           :event/change  #(d/transact! [[:db/add editor-id :source (.getValue %1)]])})]
                 [:.w-50.h-100
                  (repl-pane eval-result)]]))

(defn main []
  (v/render-to-dom (app) "maria"))

(main)
