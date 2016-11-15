(ns maria.core
  (:require
    [maria.codemirror :as cm]
    [maria.eval :refer [eval-src]]
    [maria.walkthrough :refer [walkthrough]]
    [maria.tree.parse]

    [clojure.set]
    [clojure.string]
    [clojure.walk]

    [cljs.spec :include-macros true]
    [cljs.pprint :refer [pprint]]
    [re-db.d :as d]
    [re-view.subscriptions :as subs]
    [re-view.routing :refer [router]]
    [re-view.core :as v :refer-macros [defcomponent]]))

(enable-console-print!)

;; to support multiple editors
(defonce editor-id 1)
;(d/transact! [[:db/add editor-id :source ";;;; Explore basic data types: String, list, nil\n;; Introduce String: everything that goes between two double-quotes\n\"duck\"\n\n\"a\"\n\n\"\"\n\n\"Berlin bear\"\n\n;; Introduce lists: \n()\n\n'(\"duck\") ;; TODO (somehow explain that plain lists need quotes. Let them error first?)\n\n(\"duck\") ;; ERROR -- gosh some nice error messages would be swell\n\n'(\"duck\" \"Berlin bear\" \"whale\")"]])
(d/transact! [[:db/add editor-id :source ";; try spec:\n(require '[cljs.spec :as s :include-macros true])"]])
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

(defcomponent result-pane
              :component-did-update scroll-bottom
              :component-did-mount scroll-bottom
              :render
              (fn [this]
                [:div.h-100.overflow-auto.code
                 (map display-result (reverse (first (v/children this))))]))

(defcomponent repl
              :subscriptions {:source      (subs/db [editor-id :source])
                              :eval-result (subs/db [editor-id :eval-result])}
              :render
              (fn [_ _ {:keys [eval-result source]}]
                [:.flex.flex-row.h-100
                 [:.w-50.h-100.bg-solarized-light
                  (cm/editor {:value         source
                              :event/keydown #(when (and (= 13 (.-which %2)) (.-metaKey %2))
                                               (when-let [source (or (cm/selection-text %1)
                                                                     (cm/bracket-text %1))]
                                                 (d/transact! [[:db/update-attr editor-id :eval-result conj (eval-src source)]])))
                              :event/change  #(d/transact! [[:db/add editor-id :source (.getValue %1)]])})]
                 [:.w-50.h-100
                  (result-pane eval-result)]]))

(defcomponent not-found
              :render
              (fn [] [:div "We couldn't find this page!"]))

(defcomponent layout
              :subscriptions {:main-view (router "/" repl
                                                 "/walkthrough" walkthrough
                                                 not-found)}
              :render
              (fn [_ _ {:keys [main-view]}]
                [:div.h-100
                 [:.w-100.fixed.bottom-0.z-3
                  [:.dib.center
                   [:a.dib.pa2.black-70.no-underline.f6.bg-black-05 {:href "/"} "REPL"]
                   [:a.dib.pa2.black-70.no-underline.f6.bg-black-05 {:href "/walkthrough"} "Walkthrough"]]]
                 (main-view)]))

(defn main []
  (v/render-to-dom (layout) "maria"))

(main)
