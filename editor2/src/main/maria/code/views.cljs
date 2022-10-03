(ns maria.code.views
  (:refer-clojure :exclude [var?])
  (:require [applied-science.js-interop :as j]
            [yawn.view :as v]
            [yawn.convert :as c :refer [IElement]]
            [shapes.core :as shapes]
            ["react" :as react]
            [shadow.cljs.modern :refer [defclass]]
            [sci.lang]))

(defn var? [x] (instance? sci.lang/Var x))

(defclass ErrorBoundary
  (extends react/Component)
  (constructor [this] (super))
  Object
  (render [^js this]
    (j/let [error (j/get-in this [:state :error])
            ^js [show body :as children] (.. this -props -children)]
      (if error
        (show error)
        (show body)))))

(j/!set ErrorBoundary :getDerivedStateFromError (fn [error]
                                                  (j/log :found-error error)
                                                  #js{:error error}))



(defn use-watch [ref]
  (let [[value set-value!] (react/useState [nil @ref])]
    (react/useEffect
     (fn []
       (add-watch ref set-value! (fn [_ _ old new] (set-value! [old new])))
       #(remove-watch ref set-value!)))
    value))

(v/defview error-viewer [error]
  (ex-message error))

(declare ^:dynamic *viewers*)

(defn show [x]
  (reduce (fn [_ v] (if-some [out (v x)] (reduced out) _)) "No viewer" *viewers*))

(v/defview show-coll [^string left ^string right coll]
  [:div.inline-flex.items-stretch
   [:div.flex.items-start.whitespace-nowrap.m-1 left]
   (into [:div.inline-flex.items-end.m-1]
         (comp (map show)
               (interpose \u00A0))
         coll)
   [:div.flex.items-end.whitespace-nowrap.m-1 right]])

(def ^:dynamic *viewers*
  [(fn [this] (when (instance? MapEntry this)
                [:div.flex (show (key this)) ": " (show (val this))]))
   (fn [this] (when (string? this) (pr-str this)))
   (fn [this] (when (number? this) (str this)))
   (fn [this] (when (boolean? this) (pr-str this)))
   (fn [this] (when (or (instance? PersistentHashMap this)
                        (instance? PersistentArrayMap this))
                [:div
                 "{"
                 (into [:...] (map (fn [this] [:div.flex (show (key this)) ": " (show (val this))])) this)
                 "}"]))
   (fn [this] (when (vector? this) (show-coll \[ \] this)))
   (fn [this] (when (set? this) (show-coll "#{" "}" this)))
   (fn [this] (when (var? this) (v/x [:...
                                      [:div.text-gray-400 (str this)]
                                      (show @this)])))
   (fn [this] (when (instance? js/Error this) (error-viewer this)))
   (fn [this] (when (instance? shapes/Shape this) (v/x (shapes/to-hiccup this))))
   (fn [this] (when (fn? this) (str this)))
   (fn [this] (when (seq? this) (show-coll "(" ")" this)))
   (fn [this] (when (instance? js/Promise this) (v/x [:em "js/Promise"])))
   (fn [this] (when keyword? this) (str this))
   ])

(v/defview value-viewer [!result]
  (when-let [{:as result :keys [value error]} (second (use-watch !result))]
    [:...
     (if error
       (show error)
       (j/lit [ErrorBoundary {:key result}
               show
               value]))]))

(v/defview code-row [^js {:keys [!result mounted!]}]
  (let [ref (react/useCallback (fn [el]
                                 (when el (mounted! el))))]
    [:div.-mx-4.mb-4.md:flex.w-full
     {:ref ref}
     [:div {:class "md:w-1/2 text-base"
            :style {:color "#c9c9c9"}}]
     [:div
      {:class "md:w-1/2 text-sm m-3 md:my-0 overflow-scroll"}
      [value-viewer !result]]]))



(comment
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