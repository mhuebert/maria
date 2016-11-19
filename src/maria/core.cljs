(ns maria.core
  (:require
    [maria.codemirror :as cm]
    [maria.eval :refer [eval-src]]
    [maria.walkthrough :refer [walkthrough]]
    [maria.tree.core :as tree]
    [maria.messages :refer [reformat-error reformat-warning]]
    [maria.html]

    [clojure.set]
    [clojure.string :as string]
    [clojure.walk]

    [maria.user :include-macros true]

    [cljs.spec :include-macros true]
    [re-db.d :as d]
    [re-view.subscriptions :as subs]
    [re-view.routing :as routing :refer [router]]
    [re-view.core :as v :refer-macros [defcomponent]]
    [goog.object :as gobj]))

(enable-console-print!)

;; to support multiple editors
(defonce editor-id "maria-repl-left-pane")

(defonce _ (d/listen! [editor-id :source] #(gobj/set (.-localStorage js/window) editor-id %)))

(defn bracket-type [value]
  (cond (vector? value) ["[" "]"]
        :else ["(" ")"]))                                   ; XXX probably wrong

(defn format-value [value]
  (cond
    (or (vector? value)
        (seq? value)) (let [[lb rb] (bracket-type value)]
                        [:span.output-bracket lb
                         [:span (interpose " " (map format-value value))]
                         [:span.output-bracket rb]])
    (= :shape (:is-a value)) ((maria.user/show value))      ; synthesize component for shape
    (v/is-react-element? value) (value)
    :else (if (nil? value)
            "nil"
            (try (pr-str value)
                 (catch js/Error e "error printing result")))))

(defn display-result [{:keys [value error warnings]}]
  [:div.bb.b--near-white.ph3
   [:.mv2 (if (or error (seq warnings))
            [:.bg-near-white.ph3.pv2.mv2.ws-prewrap
             (for [message (cons (some-> error str reformat-error)
                                 (map reformat-warning (distinct warnings)))
                   :when message]
               [:.pv2 message])]
            (format-value value))]])

(defn scroll-bottom [component]
  (let [el (js/ReactDOM.findDOMNode component)]
    (set! (.-scrollTop el) (.-scrollHeight el))))

(defn last-n [n v]
  (subvec v (max 0 (- (count v) n))))

(defn eval-editor [cm]
  (when-let [source (or (cm/selection-text cm)
                        (-> (v/state (.-view cm))
                            :highlight-state
                            :node
                            tree/string))]

    (d/transact! [[:db/update-attr editor-id :eval-result (fnil conj []) (eval-src source)]])))

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
  :get-editor
  #(-> %1 (v/react-ref "repl-editor") v/state :editor)
  :render
  (fn [this _ {:keys [eval-result source]}]
    [:.flex.flex-row.h-100
     [:.w-50.h-100.bg-solarized-light
      {:on-mouse-move #(when-let [editor (.getEditor this)]
                        (cm/update-highlights editor %))}
      (cm/editor {:value           source
                  :ref             "repl-editor"
                  :event/mousedown #(when (.-metaKey %)
                                     (.preventDefault %)
                                     (eval-editor (.getEditor this)))
                  :event/keyup     cm/update-highlights
                  :event/keydown   #(do (cm/update-highlights %1 %2)
                                        (when (and (= 13 (.-which %2)) (.-metaKey %2))
                                          (eval-editor %1)))

                  :event/change    #(d/transact! [[:db/add editor-id :source (.getValue %1)]])})]
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
