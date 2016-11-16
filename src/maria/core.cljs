(ns maria.core
  (:require
    [maria.codemirror :as cm]
    [maria.eval :refer [eval-src]]
    [maria.walkthrough :refer [walkthrough]]
    [maria.tree.core :as tree]
    [maria.html]

    [clojure.set]
    [clojure.string :as string]
    [clojure.walk]

    [maria.user :include-macros true]

    [cljs.spec :include-macros true]
    [cljs.pprint :refer [pprint]]
    [re-db.d :as d]
    [re-view.subscriptions :as subs]
    [re-view.routing :as routing :refer [router]]
    [re-view.core :as v :refer-macros [defcomponent]]
    [goog.object :as gobj]))

(enable-console-print!)

;; to support multiple editors
(defonce editor-id "maria-repl-left-pane")

(defonce _ (d/listen! [editor-id :source] #(gobj/set (.-localStorage js/window) editor-id %)))

(defn display-result [{:keys [value error warnings]}]
  [:div.bb.b--near-white
   (cond error [:.pa3.dark-red.ph3.mv2 (str error)]
         (v/is-react-element? value) (value)
         :else [:.bg-white.pv2.ph3.mv2 (if (nil? value) "nil" (try (with-out-str (prn value))
                                                                   (catch js/Error e "error printing result")))])
   (when (seq warnings)
     [:.bg-near-white.pa2.pre.mv2
      [:.dib.dark-red "Warnings: "]
      (for [warning (distinct (map #(dissoc % :env) warnings))]
        (str "\n" (with-out-str (pprint warning))))])])

(defn scroll-bottom [component]
  (let [el (js/ReactDOM.findDOMNode component)]
    (set! (.-scrollTop el) (.-scrollHeight el))))

(defn last-n [n v]
  (subvec v (max 0 (- (count v) n))))

(defn eval-editor [cm]
  (when-let [source (or (cm/selection-text cm)
                        (-> (v/state (.-view cm))
                            :cursor-state
                            :node
                            tree/string))]
    (d/transact! [[:db/update-attr editor-id :eval-result (fnil conj []) (eval-src source)]])))

(defn highlight-cursor-form [cm e]
  (let [this (.-view cm)
        {{node :node}                     :cursor-state
         {prev-node :node handle :handle} :highlight-state} (v/state this)]
    (if (.-metaKey e)
      (when (not= node prev-node)
        (let [[from to] (cm/parse-range (update node :col dec))]
          (v/update-state! this assoc :highlight-state
                           {:node   node
                            :handle (.markText cm from to #js {:className "CodeMirror-cursor-form"})})))
      (do (some-> handle (.clear))
          (v/update-state! this dissoc :highlight-state)))))

(defcomponent result-pane
              :component-did-update scroll-bottom
              :component-did-mount scroll-bottom
              :render
              (fn [this]
                [:div.h-100.overflow-auto.code
                 (map display-result (last-n 50 (first (v/children this))))]))

(defcomponent repl
              :subscriptions {:source      (subs/db [editor-id :source])
                              :eval-result (subs/db [editor-id :eval-result])}
              :component-will-mount
              #(d/transact! [[:db/add editor-id :source (gobj/getValueByKeys js/window #js ["localStorage" editor-id])]])
              :render
              (fn [this _ {:keys [eval-result source]}]
                [:.flex.flex-row.h-100
                 [:.w-50.h-100.bg-solarized-light
                  (cm/editor {:value         source
                              :ref           "repl-editor"
                              :event/keydown #(do (when (and (= 13 (.-which %2)) (.-metaKey %2))
                                                    (eval-editor %1))
                                                  (highlight-cursor-form %1 %2))
                              :event/keyup   highlight-cursor-form
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
  (v/render-to-dom (layout) "maria-main"))

(main)
