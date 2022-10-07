(ns maria.code.views
  (:refer-clojure :exclude [var?])
  (:require [applied-science.js-interop :as j]
            [yawn.view :as v]
            [yawn.convert :as c :refer [IElement]]
            [re-db.reactive :as r]
            [shapes.core :as shapes]
            ["react" :as react]
            [shadow.cljs.modern :refer [defclass]]
            [sci.lang]
            [nextjournal.clerk.viewer :as clerk.viewer]
            [nextjournal.clerk.sci-viewer :as clerk.sci-viewer]
            [reagent.core :as reagent]
            [cells.cell :as cell]
            [promesa.core :as p]))

(def SHOW-VARS? false)
(def COLL-PADDING 4)

(defn var? [x] (instance? sci.lang/Var x))

(defclass ErrorBoundary
  (extends react/Component)
  (constructor [this] (super))
  Object
  (render [^js this]
    (j/let [error (j/get-in this [:state :error])
            ^js [show body :as children] (.. this -props -children)]
      ;; todo - hint to avoid interpretation here
      (v/x (if error
             [show error]
             [show body])))))

(j/!set ErrorBoundary :getDerivedStateFromError (fn [error]
                                                  (j/log :found-error error)
                                                  #js{:error error}))

(defn use-watch [ref]
  (let [[value set-value!] (v/use-state [nil (r/peek ref)])]
    (v/use-effect
     (fn []
       (add-watch ref set-value! (fn [_ _ old new] (set-value! [old new])))
       #(remove-watch ref set-value!)))
    value))

(v/defview error-viewer [error]
  (js/console.error error)
  (ex-message error))

(declare ^:dynamic *viewers*)

(v/defview show {:key identity} [x]
  (reduce (fn [_ v] (if-some [out (v x)] (reduced out) _)) "No viewer" *viewers*))

(v/defview more-btn [on-click]
  [:div.pb-1.-mt-1.px-1.mx-1.cursor-pointer.bg-gray-200.hover:bg-gray-300 {:on-click on-click}
   "..."])

(defn punctuate ^v/el [s]
  (v/x [:div.inline-block.font-bold.opacity-60 s]))

(v/defview show-brackets [left right more children !wrapper !parent]
  (let [bracket-classes "flex flex-none"]
    [:div.inline-flex.max-w-full.gap-list {:ref !wrapper}
     [:div.items-start {:class bracket-classes} (punctuate left)]

     (-> [:div.inline-flex.flex-wrap.items-end.gap-list.overflow-hidden {:ref !parent}]
         (into (map (fn [x] x)) children))

     [:div.items-end {:class bracket-classes}
      (when more [more-btn more])
      (punctuate right)]]))

(defn measure-node [^js node]
  (some->> (if (= 3 (j/get node :nodeType))
             (-> (js/document.createRange)
                 (doto (.selectNodeContents node))
                 (.getClientRects)
                 first
                 ((juxt (j/get :width) (j/get :height))))
             [(.-offsetWidth node) (.-offsetHeight node)])
           (mapv Math/round)))

(defn compute-box-limit [^js wrapper-el ^js parent-el]
  (when-let [^js item (and wrapper-el parent-el (.-lastChild parent-el))]
    (j/let [wrapper-width (- (.. wrapper-el -offsetWidth)
                             (.. wrapper-el -firstChild -offsetWidth)
                             (.. wrapper-el -lastChild -offsetWidth))
            [item-width
             item-height] (measure-node item)
            total-height (Math/round (-> (j/get js/window :innerHeight)
                                         (/ 2)))]
      (loop [available-height (- total-height item-height COLL-PADDING)
             available-width (- wrapper-width item-width COLL-PADDING)
             limit 1]
        (let [available-width (- available-width item-width)]
          (cond (= limit 500) limit

                (and (neg? available-height) (neg? available-width)) ;; return, dimensions exceeded
                limit

                (neg? available-width) ;; wrap, width exceeded
                (recur (- available-height item-height COLL-PADDING) (- wrapper-width item-width COLL-PADDING) (inc limit))

                :else ;; width is available, add to row
                (recur available-height available-width (inc limit))))))))

(defn use-limit [coll initial-limit]
  (j/let [!inc (v/use-ref initial-limit)
          [showing set-showing!] (v/use-state initial-limit)
          !wrapper (v/use-ref)
          !parent (v/use-ref)
          total (bounded-count (+ showing @!inc) coll)]
    [(take showing coll)
     (when (> total showing)
       (fn []
         (when-let [new-limit (compute-box-limit @!wrapper @!parent)]
           (reset! !inc new-limit))
         (set-showing! (+ showing @!inc))))
     !wrapper
     !parent]))

(v/defview show-coll [left right coll]
  (let [[coll more! !wrapper !parent] (use-limit coll 10)]
    [show-brackets
     left
     right
     more!
     (into []
           (map #(do [:span (show %)]))
           coll)
     !wrapper
     !parent]))

(v/defview show-map-entry [this]
  [:<>
   (show (key this)) " "
   (show (val this))
   [:br]])

(defn use-watchable [x]
  (v/use-sync-external-store (fn [changed!]
                               (add-watch x changed! (fn [_ _ _ _] (changed!)))
                               #(remove-watch x changed!))
                             #(r/peek x)))

(v/defview show-watchable [x]
  (show (use-watchable x)))

(def loader (v/x [:div.cell-status
                  [:div.circle-loading
                   [:div]
                   [:div]]]))

(v/defview show-async-status [astate]
  (if-let [error (cell/error astate)]
    [:div (ex-message error)]
    loader))

(v/defview show-promise [p]
  (let [[v v!] (v/use-state ::loading)]
    (v/use-effect
     (fn []
       (v! ::loading)
       (p/then p v!)
       nil)
     [p])
    (v/x
     (if (= v ::loading)
       loader
       (show v)))))

(def ^:dynamic *viewers*
  [(fn [x] (some-> (cell/async-status x)
                   (use-watchable)
                   show-async-status))
   (fn [x] (when (satisfies? IWatchable x)
             (show-watchable x)))
   (fn [x] (when (or (instance? PersistentHashMap x)
                     (instance? PersistentArrayMap x))
             [show-coll \{ \} x]))
   (fn [x] (when (instance? MapEntry x) [show-map-entry x]))
   (fn [x] (when (string? x) (pr-str x)))
   (fn [x] (when (number? x) (str x)))
   (fn [x] (when (boolean? x) (pr-str x)))
   (fn [x] (when (vector? x) (show-coll \[ \] x)))
   (fn [x] (when (set? x) (show-coll "#{" "}" x)))
   (fn [x] (when (var? x) (v/x [:<>
                                (when SHOW-VARS? [:div.text-gray-400.mb-1 (str x)])
                                (show @x)])))
   (fn [x] (when (instance? js/Error x) (error-viewer x)))
   (fn [x] (when (instance? shapes/Shape x) (v/x (shapes/to-hiccup x))))
   (fn [x] (when (fn? x) (str x)))
   (fn [x] (when (seq? x) (show-coll "(" ")" x)))
   (fn [x] (when (instance? js/Promise x) (show-promise x)))
   (fn [x] (when keyword? x) (str x))
   (fn [x] (when (nil? x) "nil"))

   ])

(defn shape? [x] (instance? shapes.core/Shape x))

(clerk.viewer/reset-viewers! :default (clerk.viewer/add-viewers clerk.viewer/default-viewers
                                                                [{:pred #(shape? (cond-> % (coll? %) first))
                                                                  :transform-fn clerk.viewer/mark-presented
                                                                  :render-fn show}]))

(v/defview value-viewer [!result]
  (let [{:as result :keys [value error]} (second (use-watch !result))]
    [:...
     (if error
       (show error)
       (j/lit [ErrorBoundary {:key result}
               show
               value]))]))

(v/defview code-row [^js {:keys [!result mounted!]}]
  (let [ref (v/use-callback (fn [el] (when el (mounted! el))))]
    [:div.-mx-4.mb-4.md:flex.w-full
     {:ref ref}
     [:div {:class "md:w-1/2 text-base"
            :style {:color "#c9c9c9"}}]
     [:div
      {:class "md:w-1/2 font-mono text-sm m-3 md:my-0 max-h-screen overflow-scroll"}
      [value-viewer !result]]]))



#_(comment
   (ns maria.views.values
     (:require [shapes.core :as shapes]
               [cells.cell :as cell]
               [maria.friendly.messages :as messages]
               [maria.views.icons :as icons]
               [chia.view :as v]
               [maria.editors.code :as code]
               [maria.live.source-lookups :as source-lookups]
               [maria.views.repl-specials :as special-views]
               [maria.views.error :as error-view]
               [chia.view.hiccup :as hiccup]
               [maria.util :refer [space]]
               [maria.eval :as e]
               [lark.value-viewer.core :as views]
               [lark.tree.core :as tree]
               [lark.tree.range :as range]
               [fast-zip.core :as z]
               [lark.tree.nav :as nav]
               [clojure.string :as str]
               [maria.views.cards :as repl-ui]
               [applied-science.js-interop :as j]
               [chia.view.hooks :as hooks])
     (:import [goog.async Deferred]))

   (def ^:dynamic *format-depth-limit* 3)

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

   (declare format-function)
   (declare display-result)

   (v/defclass display-deferred
               {:view/did-mount (fn [{:keys [deferred view/state]}]
                                  (doto ^js deferred
                                    (.addCallback #(swap! state assoc :value %1))
                                    (.addErrback #(swap! state assoc :error %))))}
               [{:keys [view/state]}]
               (let [{:keys [value error] :as s} @state]
                 [:div
                  [:.gray.i "goog.async.Deferred"]
                  [:.pv3 (cond (nil? s) [:.progress-indeterminate]
                               error (str error)
                               :else (or (some-> value (views/format-value)) [:.gray "Finished."]))]]))

   (def expander-outter :.dib.bg-darken.ph2.pv1.mh1.br2)
   (def inline-centered :.inline-flex.items-center)

   (defn expanded? [{:keys [view/state]} depth]
     (if (boolean? (:collection-expanded? @state))
       (:collection-expanded? @state)
       (and depth (< depth *format-depth-limit*))))

   (def ^:private partial-str (.toString (partial +)))

   (v/defclass format-function
               {:view/initial-state (fn [_ value] {:expanded? false})}
               [{:keys [view/state]} f]
               (let [{:keys [expanded?]} @state
                     fn-name (some-> (source-lookups/fn-name f) (symbol) (name))]
                 [:span
                  [expander-outter {:on-click #(swap! state update :expanded? not)
                                    :class "pointer hover-opacity-parent"}
                   [inline-centered [:span.o-50.mr1 "ƒ "]
                    (if (and fn-name (not= "" fn-name))
                      (some-> (source-lookups/fn-name f) (symbol) (name)))
                    (-> icons/ArrowPointingDown
                        (icons/size 20)
                        (icons/class "mln1 mrn1 hover-opacity-child")
                        (icons/style {:transition "all ease 0.2s"
                                      :transform (when-not expanded?
                                                   "rotate(90deg)")}))]
                   (when expanded?
                     (or (some-> (source-lookups/fn-var f)
                                 (special-views/var-source))
                         (some-> (source-lookups/js-source->clj-source f (.toString f))
                                 (code/viewer))

                         [:div.pre
                          (code/viewer (.toString f))]))]]))

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

   (v/defview show-stack [error]
     (when (instance? js/Error error)
       (let [expanded? (hooks/use-atom false)
             stack (or (some-> (ex-cause error)
                               (j/get :stack))
                       (j/get error :stack))]
         [:div
          [:a.pv2.flex.items-center.nl2.pointer.hover-underline.gray {:on-click #(swap! expanded? not)}
           (repl-ui/arrow (if @expanded? :down :right))
           "stacktrace"]
          (when @expanded? [:pre stack])])))

   (v/defview show-error
     [{:keys [error messages]}]
     (let [messages (or messages (messages/error-messages error))
           sections (cond-> (mapv #(vector :.mv2 %) messages)
                            (instance? js/Error error) (conj (show-stack error)))]
       (interpose [:.bb.b--red.o-20.bw2] sections)))

   (defn render-error-result [{:keys [error source show-source? formatted-warnings warnings] :as result}]
     [:div
      {:class "bg-darken-red cf"}
      (when source
        (display-source result))
      [:.ws-prewrap.relative.nt3
       [:.ph3.overflow-auto
        (show-error
         {:messages (->> (concat (or formatted-warnings
                                     (format-warnings warnings))
                                 (some-> error (messages/error-messages)))
                         (filter #(and %
                                       (if (string? %)
                                         (not (str/blank? %))
                                         true)))
                         (distinct))
          :error error})]]])

   (v/defclass display-result
               {:key :id}
               [{:keys [id
                        value
                        error
                        warnings
                        show-source?
                        block-id
                        source
                        compiled-js]
                 result :view/props
                 :as this}]
               (error-view/error-boundary {:key id
                                           :on-error (fn [error _]
                                                       (e/handle-block-error block-id error))
                                           :fallback (fn [error info]
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
                                                [:.ph3 [views/format-value value]]]]))))

   (defn repl-card [& content]
     (into [:.sans-serif.bg-white.shadow-4.ma2] content))

   (defn cell-view [cell]
     (case (cell/status cell)
       :loading (hiccup/to-element
                 [:.cell-status
                  [:.circle-loading
                   [:div]
                   [:div]]])
       :error (display-result {:error (cell/error cell)})
       (display-result {:value @cell})))

   (extend-protocol hiccup/IElement
     shapes/Shape
     (-to-element [this]
       (v/to-element
        (shapes/to-hiccup this)))
     cell/Cell
     (-to-element [this] (cell-view this))
     function
     (-to-element [this]
       (format-function nil this))
     js/Error
     (-to-element [error]
       (show-error {:error error})))
   )