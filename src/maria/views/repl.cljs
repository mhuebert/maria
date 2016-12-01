(ns maria.views.repl
  (:require [re-view.core :as v :refer [defview]]
            [re-db.d :as d]
            [maria.codemirror :as cm]
            [maria.editor :as editor]
            [maria.eval :as eval]
            [maria.user :refer [show]]
            [cljs.pprint :refer [pprint]]
            [maria.messages :refer [reformat-error reformat-warning]]
            [re-view.subscriptions :as subs]
            [maria.tree.core :as tree]))

(def repl-editor-id "maria-repl-left-pane")

(defn bracket-type [value]
  (cond (vector? value) ["[" "]"]
        (set? value) ["#{" "}"]
        :else ["(" ")"]))                                   ; XXX probably wrong

(defn format-value [value]
  (cond
    (or (vector? value)
        (seq? value)
        (set? value)) (let [[lb rb] (bracket-type value)]
                        (list
                          [:span.output-bracket lb]
                          [:span (interpose " " (map format-value value))]
                          [:span.output-bracket rb]))
    (= :shape (:is-a value)) ((show value))                 ; synthesize component for shape
    (v/is-react-element? value) (value)
    :else (if (nil? value)
            "nil"
            (try (with-out-str (pprint value))
                 (catch js/Error e "error printing result")))))

(defn display-result [{:keys [value error warnings]}]
  [:div.bb.b--near-white.ph3
   [:.mv2.ws-prewrap
    (if (or error (seq warnings))
      [:.bg-near-white.ph3.pv2.mv2
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
                        (->> (:state (.-view cm))
                             :highlight-state
                             :node
                             (tree/string (:ns @eval/c-env))))]

    (d/transact! [[:db/update-attr repl-editor-id :eval-result-log (fnil conj []) (eval/eval-src source)]])))

(defview result-pane
         {:did-update scroll-bottom
          :did-mount  scroll-bottom}
         [:div.h-100.overflow-auto.code
          (map display-result (last-n 50 (d/get repl-editor-id :eval-result-log)))])

(defview main
         {:get-editor #(-> %1 (v/get-ref "repl-editor") :state :editor)}
         (fn [this]
           [:.flex.flex-row.h-100
            [:.w-50.h-100.bg-solarized-light
             {:on-mouse-move #(when-let [editor (.getEditor this)]
                                (editor/update-highlights editor %))}
             (editor/editor {:ref             "repl-editor"
                             :local-storage   [repl-editor-id
                                               ";; Type code here;
                                               ;; Press command-enter or command-click to evaluate forms."]
                             :event/mousedown #(when (.-metaKey %)
                                                 (.preventDefault %)
                                                 (eval-editor (.getEditor this)))
                             :event/keyup     editor/update-highlights
                             :event/keydown   #(do (editor/update-highlights %1 %2)
                                                   (when (and (= 13 (.-which %2)) (.-metaKey %2))
                                                     (eval-editor %1)))})]
            [:.w-50.h-100
             (result-pane)]]))
