(ns maria.views.repl
  (:require [re-view.core :as v :refer [defview]]
            [re-view.util :as v-util]
            [re-db.d :as d]
            [re-view.hoc :as hoc]
            [magic-tree.codemirror.util :as cm]
            [maria.editor :as editor]
            [maria.eval :as eval]
            [maria.user :refer [show]]
            [cljs.pprint :refer [pprint]]
            [maria.messages :refer [reformat-error reformat-warning]]
            [magic-tree.core :as tree]
            [maria.ns-utils :as ns-utils]
            [re-view-material.core :as ui]
            [re-view-material.icons :as icons]))

(def repl-editor-id "maria-repl-left-pane")

(defn bracket-type [value]
  (cond (vector? value) ["[" "]"]
        (set? value) ["#{" "}"]
        :else ["(" ")"]))                                   ; XXX probably wrong


(def current-namespace
  (fn [] (hoc/bind-atom (v/view [{:keys [ns]}]
                                [:.dib
                                 (ui/SimpleMenuWithTrigger
                                   (ui/Button {:label   (str ns)
                                               :compact true
                                               :dense   true
                                               :style   {:margin-left "-0.25rem"}})
                                   (map (fn [item-ns] (ui/SimpleMenuItem {:text-primary (str item-ns)
                                                                          :ripple       false
                                                                          :style        (when (= item-ns ns)
                                                                                          {:background-color "rgba(0,0,0,0.05)"})
                                                                          :on-click     #(eval/eval-str `(~'in-ns ~item-ns))})) (-> (cons ns (ns-utils/user-namespaces @eval/c-state))
                                                                                                                                    (distinct))))
                                 (when-let [ns-doc (:doc (ns-utils/ns-map @eval/c-state ns))]
                                   [:span.pl2.f7.o-50 ns-doc])]) eval/c-env)))

(defn source-bar []
  [:.ph3.pv2.bb.code {:style {:border-color     "rgba(0,0,0,0.03)"
                              :background-color "#f7eed4"}}
   (current-namespace)])

(defn format-value
  [value]
  (let [element? (v/is-react-element? value)]
    (if element?
      value
      [:.ph3
       (cond
         (= :shape (:is-a value)) (show value)              ; synthesize component for shape
         (or (vector? value)
             (seq? value)
             (set? value)) (let [[lb rb] (bracket-type value)]
                             (list
                               [:span.output-bracket lb]
                               (interpose " " (v-util/map-with-keys format-value value))
                               [:span.output-bracket rb]))

         :else (if (nil? value)
                 "nil"
                 (try (with-out-str (pprint value))
                      (catch js/Error e "error printing result"))))])))

(defview display-result
  {:key :id}
  [{:keys [value error warnings source ns view/props] :as result}]
  [:div.bb.b--near-white.mb4
   [:.o-30.code.overflow-auto.pa3 source]
   [:.ws-prewrap.overflow-hidden

    (if (or error (seq warnings))
      [:.bg-near-white.ph3.pv2.overflow-auto
       (when error
         (.error js/console "Eval Result Contains Error" error))
       (for [message (cons (some-> error reformat-error)
                           (map reformat-warning (distinct warnings)))
             :when message]
         [:.pv2 message])]
      (format-value value))]
   (when ns
     [:.pa3 [:span.b "Namespace: "] (str ns)])])

(defn scroll-bottom [component]
  (let [el (v/dom-node component)]
    (set! (.-scrollTop el) (.-scrollHeight el))))

(defn last-n [n v]
  (subvec v (max 0 (- (count v) n))))

(defn eval-editor [cm]
  (when-let [source (or (cm/selection-text cm)
                        (->> cm
                             :magic/cursor
                             :bracket-loc
                             tree/top-loc
                             (tree/string (:ns @eval/c-env))))]

    (d/transact! [[:db/update-attr repl-editor-id :eval-result-log (fnil conj []) (assoc (eval/eval-str source)
                                                                                    :id (d/unique-id)
                                                                                    :source source)]])))

(defview result-pane
  {:life/did-update scroll-bottom
   :life/did-mount  scroll-bottom}
  []
  [:div.h-100.overflow-auto.code.pt
   (map display-result (last-n 50 (d/get repl-editor-id :eval-result-log)))])

(defview main
  {:life/initial-state {:repl-editor nil}
   :get-editor         (fn [{:keys [view/state]}]
                         (some-> (:repl-editor @state) :view/state deref :editor))
   :life/did-mount     (fn [this] (some-> (.getEditor this) (.focus)))}
  [{:keys [view/state] :as this}]
  [:.flex.flex-row.h-100
   [:.w-50.h-100.bg-solarized-light.pb4.relative
    (source-bar)
    (editor/editor {:ref             #(when % (swap! state assoc :repl-editor %))
                    :local-storage   [repl-editor-id
                                      ";; Type code here; press command-enter or command-click to evaluate forms.\n"]
                    :event/mousedown #(when (.-metaKey %)
                                        (.preventDefault %)
                                        (eval-editor (.getEditor this)))
                    :event/keydown   #(when (and (= 13 (.-which %2)) (.-metaKey %2))
                                        (eval-editor %1))})]
   [:.w-50.h-100
    (result-pane)]])
