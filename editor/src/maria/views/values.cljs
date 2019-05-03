(ns maria.views.values
  (:require [goog.object :as gobj]
            [shapes.core :as shapes]
            [cells.cell :as cell]
            [maria.friendly.messages :as messages]
            [maria.views.icons :as icons]
            [re-view.util :as v-util]
            [re-view.core :as v :refer [defview]]
            [maria.editors.code :as code]
            [maria.live.source-lookups :as source-lookups]
            [maria.views.repl-specials :as special-views]
            [maria.views.error :as error-view]
            [re-view.hiccup.core :as hiccup]
            [maria.util :refer [space]]
            [maria.eval :as e]
            [lark.value-viewer.core :as views]
            [lark.tree.core :as tree]
            [lark.tree.range :as range]
            [fast-zip.core :as z]
            [lark.tree.nav :as nav]
            [clojure.string :as str]
            [maria.views.cards :as repl-ui]
            [applied-science.js-interop :as j])
  (:import [goog.async Deferred]))

(defn highlights-for-position
  "Return ranges for appropriate highlights for a position within given Clojure source."
  [source position]
  (when-let [highlights (some-> (tree/ast source)
                                (tree/ast-zip)
                                (nav/navigate position)
                                (z/node)
                                (range/node-highlights))]
    (case (count highlights)
      0 nil
      1 (first highlights)
      2 (merge (second highlights)
               (range/bounds (first highlights) :left)))))

(defn bracket-type [value]
  (cond (vector? value) ["[" "]"]
        (set? value) ["#{" "}"]
        (map? value) ["{" "}"]
        :else ["(" ")"]))

(defn wrap-value [[lb rb] value]
  [:.inline-flex.items-stretch
   [:.flex.items-start.nowrap lb]
   [:div.v-top value]
   [:.flex.items-end.nowrap rb]])

(extend-protocol hiccup/IEmitHiccup
  shapes/Shape
  (to-hiccup [this] (shapes/to-hiccup this)))

(extend-protocol cell/IRenderHiccup
  object
  (render-hiccup [this] (hiccup/element this)))

(declare format-function)
(extend-protocol views/IView
  cell/Cell
  (view [this] (cell/view this))
  function
  (view [this] (format-function this)))

(declare format-value)

(defview display-deferred
  {:view/will-mount (fn [{:keys [deferred view/state]}]
                      (-> deferred
                          (.addCallback #(swap! state assoc :value %1))
                          (.addErrback #(swap! state assoc :error %))))}
  [{:keys [view/state]}]
  (let [{:keys [value error] :as s} @state]
    [:div
     [:.gray.i "goog.async.Deferred"]
     [:.pv3 (cond (nil? s) [:.progress-indeterminate]
                  error (str error)
                  :else (or (some-> value (format-value)) [:.gray "Finished."]))]]))

(def expander-outter :.dib.bg-darken.ph2.pv1.mh1.br2)
(def inline-centered :.inline-flex.items-center)

(def ^:dynamic *format-depth-limit* 3)

(defn expanded? [{:keys [view/state]} depth]
  (if (boolean? (:collection-expanded? @state))
    (:collection-expanded? @state)
    (and depth (< depth *format-depth-limit*))))

(defview format-function
  {:view/initial-state (fn [_ value] {:expanded? false})}
  [{:keys [view/state]} value]
  (let [{:keys [expanded?]} @state
        fn-name (some-> (source-lookups/fn-name value) (symbol) (name))]

    [:span
     [expander-outter {:on-click #(swap! state update :expanded? not)
                       :class    "pointer hover-opacity-parent"}
      [inline-centered
       (if (and fn-name (not= "" fn-name))
         (some-> (source-lookups/fn-name value) (symbol) (name))
         [:span.o-50.mr1 "ƒ"])
       (-> icons/ArrowPointingDown
           (icons/size 20)
           (icons/class "mln1 mrn1 hover-opacity-child")
           (icons/style {:transition "all ease 0.2s"
                         :transform  (when-not expanded?
                                       "rotate(90deg)")}))]
      (when expanded?
        (or (some-> (source-lookups/js-source->clj-source (.toString value))
                    (code/viewer))
            (some-> (source-lookups/fn-var value)
                    (special-views/var-source))
            [:div.pre
             (code/viewer (.toString value))]))]]))

(def format-value views/format-value)

(defn display-source [{:keys [source error error/position warnings]}]
  [:.code.overflow-auto.pre.gray.mv3.ph3
   {:style {:max-height 200}}
   (code/viewer {:error-ranges (cond-> []
                                       position (conj (highlights-for-position source position))
                                       (seq warnings) (into (map #(highlights-for-position source (:warning-position %)) warnings)))}
                source)])

(defn format-warnings [warnings]
  (sequence (comp (map messages/reformat-warning)
                  (distinct)
                  (keep identity)) warnings))

(def error-divider [:.bb.b--red.o-20.bw2])

(v/defview show-stack [{:keys     [stack]
                        expanded? :view/state}]
  [:div
   [:a.pv2.flex.items-center.nl2.pointer.hover-underline.gray {:on-click #(swap! expanded? not)}
    (repl-ui/arrow (if @expanded? :down :right))
    "stacktrace"]
   (when @expanded? [:pre stack])])

(defn render-error-result [{:keys [error source show-source? formatted-warnings warnings] :as result}]
  [:div
   {:class "bg-darken-red cf"}
   (when source
     (display-source result))
   [:.ws-prewrap.relative.nt3
    [:.ph3.overflow-auto
     (->> (for [message (->> (concat (or formatted-warnings
                                         (format-warnings warnings))
                                     (messages/reformat-error result))
                             (filter #(and %
                                           (if (string? %)
                                             (not (str/blank? %))
                                             true)))
                             (distinct))
                :when (and message (not (str/blank? message)))]
            [:.mv2 message])
          (interpose error-divider))
     (when-let [stack (or (some-> (ex-cause error)
                                  (j/get :stack))
                          (j/get error :stack))]
       (list error-divider
             (show-stack {:stack stack})))]]])

(defview display-result
  {:key :id}
  [{:keys  [value
            error
            warnings
            show-source?
            block-id
            source
            compiled-js]
    result :view/props
    :as    this}]
  (error-view/error-boundary {:on-error      (fn [{:keys [error]}]
                                               (e/handle-block-error block-id error))
                              :error-content (fn [{:keys [error info]}]
                                               (-> result
                                                   (assoc :error (or error (js/Error. "Unknown error"))
                                                          :error/kind :eval)
                                                   (e/add-error-position)
                                                   (render-error-result)))}
    (let [warnings (format-warnings warnings)
          error? (or error (seq warnings))]
      (when error
        (.error js/console error)
        (js/console.log compiled-js))
      (if error?
        (render-error-result (assoc result :formatted-warnings warnings))
        [:div
         (when (and source show-source?)
           (display-source result))
         [:.ws-prewrap.relative
          [:.ph3 (format-value value)]]]))))

(defn repl-card [& content]
  (into [:.sans-serif.bg-white.shadow-4.ma2] content))

(comment

 ;; for future stacktrace parsing.
 ;; I found cljs.stacktrace unable to parse chrome stacktraces.
 (defn detect-ua-product []
   ;; https://stackoverflow.com/questions/9847580/how-to-detect-safari-chrome-ie-firefox-and-opera-browser
   (cond (exists? js/chrome) :chrome
         (exists? js/InstallTrigger) :firefox
         (or (.test #"(?i)constructor" (j/get js/window :HTMLElement))
             (and (some-> (j/get js/window [:safari :pushNotification])
                          (.toString)
                          (= "[object SafariRemoteNotification]")))) :safari)))