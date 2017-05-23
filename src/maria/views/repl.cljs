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
            [goog.net.XhrIo :as xhr]
            [clojure.string :as string]))

(def repl-editor-id "maria-repl-left-pane")

(defn bracket-type [value]
  (cond (vector? value) ["[" "]"]
        (set? value) ["#{" "}"]
        :else ["(" ")"]))                                   ; XXX probably wrong

(defn builtin-ns? [s]
  (re-find #"^(?:re-view|maria|cljs|re-db|clojure)" (name s)))

(defn ns-map [ns]
  (get-in @eval/c-state [:cljs.analyzer/namespaces ns]))

(defn usable-names [ns]
  (->> (ns-map ns)
       (dissoc :defs)
       (vals)
       (filter map?)
       (map #(dissoc % :order :seen))
       (apply merge)))

(defn user-namespaces []
  (->> (keys (:cljs.analyzer/namespaces @eval/c-state))
       (filter (complement builtin-ns?))))

(def current-namespace
  (fn [] (hoc/bind-atom (v/view [{:keys [ns]}]
                                [:span
                                 (str ns)
                                 (when-let [ns-doc (:doc (ns-map ns))]
                                   [:span.pl2.f7.o-50 ns-doc])]) eval/c-env)))

(defn source-bar []
  [:.ph3.pv2.bb.code.o-60 {:style {:border-color     "rgba(0,0,0,0.03)"
                                   :background-color "#f7eed4"}}
   (current-namespace)])

(defn format-value
  [value]
  [:span
   (cond
     (= :shape (:is-a value)) (show value)                  ; synthesize component for shape
     (or (vector? value)
         (seq? value)
         (set? value)) (let [[lb rb] (bracket-type value)]
                         (list
                           [:span.output-bracket lb]
                           (interpose " " (v-util/map-with-keys format-value value))
                           [:span.output-bracket rb]))

     (v/is-react-element? value) value
     :else (if (nil? value)
             "nil"
             (try (with-out-str (pprint value))
                  (catch js/Error e "error printing result"))))])

(defview display-result
  {:key                :id
   :life/should-update #(not= (:view/props %) (:view/prev-props %))}
  [{:keys [value error warnings]}]
  [:div.bb.b--near-white.ph3
   [:.mv2.ws-prewrap.overflow-auto
    (if (or error (seq warnings))
      [:.bg-near-white.ph3.pv2.mv2
       (when error
         (.error js/console "Eval Result Contains Error" error))
       (for [message (cons (some-> error reformat-error)
                           (map reformat-warning (distinct warnings)))
             :when message]
         [:.pv2 message])]
      (format-value value))]])

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

    (d/transact! [[:db/update-attr repl-editor-id :eval-result-log (fnil conj []) (assoc (eval/eval-str source) :id (d/unique-id))]])))

(defview result-pane
  {:life/did-update scroll-bottom
   :life/did-mount  scroll-bottom}
  []
  [:div.h-100.overflow-auto.code
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
