(ns maria.code.views
  (:refer-clojure :exclude [var?])
  (:require [applied-science.js-interop :as j]
            [yawn.view :as v]
            [re-db.reactive :as r]
            [shapes.core :as shapes]
            ["react" :as react]
            [shadow.cljs.modern :refer [defclass]]
            [sci.lang]
            [nextjournal.clerk.viewer :as clerk.viewer]
            [nextjournal.clerk.sci-viewer :as clerk.sci-viewer]
            [cells.async]
            [promesa.core :as p]
            maria.sicm-views
            [clojure.string :as str]))

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
      (if error
        (show error)
        (show body)))))

(j/!set ErrorBoundary :getDerivedStateFromError (fn [error]
                                                  (j/log :found-error error)
                                                  #js{:error error}))

(v/defview show-error [opts error]
  (js/console.error error)
  (ex-message error))

(declare ^:dynamic *viewers*)

(v/defview show {:key (fn [opts x] x)}
  [opts x]
  (let [opts (or opts {:depth -1 :stack ()})
        opts (-> opts
                 (update :depth inc)
                 (update :stack conj x))]
    (reduce (fn [_ viewer] (if-some [out (viewer opts x)]
                             (reduced out)
                             _)) "No viewer" *viewers*)))

(v/defview more-btn [on-click]
  [:div.pb-1.-mt-1.px-1.mx-1.cursor-pointer.bg-gray-200.hover:bg-gray-300 {:on-click on-click}
   "..."])

(defn punctuate ^v/el [s]
  (v/x [:div.inline-block.font-bold.opacity-60 s]))

(v/defview show-brackets [left right more children !wrapper !parent]
  (let [bracket-classes "flex flex-none"]
    [:div.inline-flex.max-w-full.gap-list.whitespace-nowrap {:ref !wrapper}
     [:div.items-start {:class bracket-classes} (punctuate left)]

     (-> [:div.inline-flex.flex-wrap.items-end.gap-list.overflow-hidden.interpose-comma {:ref !parent}]
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

(v/defview show-coll [left right opts coll]
  (let [[coll more! !wrapper !parent] (use-limit coll 10)]
    [show-brackets
     left
     right
     more!
     (into []
           (comp (map #(do [:span.inline-flex.align-center (show opts %)])))
           coll)
     !wrapper
     !parent]))

(v/defview show-map-entry [opts this]
  [:div.inline-flex.gap-list
   [:span (show opts (key this))] " "
   [:span (show opts (val this))]])

(defn use-watch [x]
  (let [id (v/use-callback #js{})]
    (v/use-sync-external-store
     (v/use-callback
      (fn [changed!]
        (add-watch x id (fn [_ _ _ _] (changed!)))
        #(remove-watch x id))
      #js[x])
     #(r/peek x))))

(def loader (v/x [:div.cell-status
                  [:div.circle-loading
                   [:div]
                   [:div]]]))

(defn show-async-status [opts {:keys [loading error]}]
  (cond loading loader
        error (show-error opts error)))

(v/defview show-promise [opts p]
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
       (show opts v)))))

(v/defview show-var [opts x]
  (v/x [:<>
        (when (fn? @x) [:div.text-gray-500.mb-1 (str x)])
        (show opts @x)]))

(v/defview show-str [opts x]
  ;; TODO - collapse via button, not clicking anywhere on the text
  (let [[collapse toggle!] (v/use-state (or (> (count (str/split-lines x)) 10)
                                            (> (count x) 200)))
        x (str \" x \")]
    (if collapse
      [:div.line-clamp-6 {:on-click #(toggle! not)} x]
      [:div {:class ["max-h-[80vh] overflow-y-scroll"]
             :on-click #(toggle! not)} x])))

(def show-map (partial show-coll "{" "}"))
(def show-set (partial show-coll "#{" "}"))
(def show-list (partial show-coll "(" ")"))

(v/defview show-function [_ f]
  (let [[showing toggle!] (v/use-state false)]
    ;; TODO show arrow
    [:div {:on-click #(toggle! not)}
     [:div "ƒ"]
     (when showing
       [:div.pre-wrap (str f)])]))

(v/defview show-shape [_ x]
  (v/x (shapes/to-hiccup x)))

(defn show-identity [opts x] x)
(defn show-terminal [f] (fn [opts x] (f x)))

(v/defview show-watchable [opts x]
  (show opts (use-watch x)))

(v/defview show-with-async-status [opts !status !value]
  ;; always watch status & value together, to avoid cell value being unwatched during loading/error states.
  (let [status (use-watch !status)
        value (use-watch !value)]
    (if status
      (show-async-status opts status)
      (show opts value))))

(defn by-type [views]
  (fn [opts x]
    (when-some [f (views (type x))]
      (f opts x))))

(def ^:dynamic *viewers*
  (flatten [
            (fn [opts x]
              (when-let [!status (cells.async/!status x)]
                (show-with-async-status opts !status x)))

            (fn [opts x]
              (when (react/isValidElement x)
                x))

            (fn [opts x] (when (:hiccup (meta x))
                           (v/x x)))

            (by-type {js/Number (show-terminal identity)
                      Symbol (show-terminal str) js/String show-str
                      js/Boolean (show-terminal pr-str)
                      Keyword (show-terminal str)
                      MapEntry show-map-entry
                      Var show-var
                      js/Function show-function
                      js/Promise show-promise
                      PersistentArrayMap show-map
                      PersistentHashMap show-map
                      TransientHashMap show-map
                      PersistentTreeSet show-set
                      PersistentHashSet show-set
                      List show-list
                      LazySeq show-list
                      shapes/Shape show-shape
                      sci.lang/Namespace (show-terminal str)})

            (fn [opts x] (when (satisfies? IWatchable x)
                           (show-watchable opts x)))

            (fn [opts x] (when (vector? x) (show-coll \[ \] opts x)))
            (fn [opts x] (when (map? x) (show-map opts x)))
            (fn [opts x] (when (set? x) (show-set opts x)))
            (fn [opts x] (when (seq? x) (show-list opts x)))
            (fn [opts x] (when (fn? x) (show-function opts x)))
            (fn [opts x] (when (var? x) (show-var opts x)))
            (fn [opts x] (when (nil? x) (punctuate "nil")))
            (fn [opts x] (when (symbol? x) (show-str opts x)))

            (fn [opts x] (when (instance? js/Error x) (show-error opts x)))
            (fn [opts x] (when (instance? js/Promise x) (show-promise opts x)))

            #_(fn [x] (clerk.sci-viewer/inspect x))
            maria.sicm-views/views]))

(defn shape? [x] (instance? shapes.core/Shape x))

(clerk.viewer/reset-viewers! :default (clerk.viewer/add-viewers clerk.viewer/default-viewers
                                                                [{:pred #(shape? (cond-> % (coll? %) first))
                                                                  :transform-fn clerk.viewer/mark-presented
                                                                  :render-fn #(show nil %)}]))

(v/defview value-viewer [!result]
  (let [{:as result :keys [value error]} (use-watch !result)]
    [:... {:key result}
     (if error
       (show nil error)
       (j/lit [ErrorBoundary {:key result}
               #(show nil %)
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