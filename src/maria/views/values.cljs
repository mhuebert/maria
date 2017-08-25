(ns maria.views.values
  (:require [clojure.string :as string]
            [goog.object :as gobj]
            [maria.messages :as messages]
            [maria.views.icons :as icons]
            [re-view.util :as v-util]
            [re-view.core :as v :refer [defview]]
            [maria.user.shapes :as shapes]
            [maria.live.magic-tree :as magic]
            [maria.views.codemirror :as codemirror]
            [maria.live.source-lookups :as source-lookups]
            [maria.views.repl-specials :as special-views]
            [maria.views.error :as error-view]
            [maria.show :as show])
  (:import [goog.async Deferred]))

(defn bracket-type [value]
  (cond (vector? value) ["[" "]"]
        (set? value) ["#{" "}"]
        (map? value) ["{" "}"]
        :else ["(" ")"]))

(declare format-value)

(defview display-deferred
  {:view/will-mount (fn [{:keys [deferred view/state]}]
                      (-> deferred
                          (.addCallback #(swap! state assoc :value %1))
                          (.addErrback #(swap! state assoc :error %)))

                      )}
  [{:keys [view/state]}]
  (let [{:keys [value error] :as s} @state]
    [:div
     [:.gray.i "goog.async.Deferred"]
     [:.pv3 (cond (nil? s) [:.progress-indeterminate]
                  error (str error)
                  :else (or (some-> value (format-value)) [:.gray "Finished."]))]]))

(def expander-outter :.dib.bg-darken.ph2.pv1.ma1.br2.pointer)
(def expander-label :.inline-flex.items-center)

(defview format-collection
  {:view/initial-state {:limit-n     20
                        :limit-depth 5}}
  [{state :view/state} depth value]
  (let [{:keys [limit-n limit-depth]} @state
        [lb rb] (bracket-type value)
        more? (= (count (take (inc limit-n) value)) (inc limit-n))]
    (if (= depth limit-depth)
      [:div [expander-outter {:on-click #(swap! state update :limit-depth inc)} [expander-label "…"]]]
      [:div
       [:span.output-bracket lb]
       (interpose " " (v-util/map-with-keys (partial format-value (inc depth)) (take limit-n value)))
       (when more? [expander-outter {:on-click #(swap! state update :limit-n + 20)} [expander-label "…"]])
       [:span.output-bracket rb]])))

(defview format-map
  {:view/initial-state {:limit-n     20
                        :limit-depth 1}}
  [{state :view/state} depth value]
  (let [{:keys [limit-n limit-depth]} @state
        more? (= (count (take (inc limit-n) value)) (inc limit-n))
        last-n (if more? limit-n (count value))]
    (if (> depth limit-depth)
      [expander-outter {:on-click #(swap! state assoc :limit-depth (inc depth))} [expander-label "…"]]
      [:table.fl.relative.mh2.cb
       [:tbody
        (or (some->> (seq (take limit-n value))
                     (map-indexed (fn [n [a b]]
                                    [:tr
                                     {:key n}
                                     [:td (when (= n 0) "{")]
                                     [:td.v-top
                                      (format-value (inc depth) a)]
                                     [:td.v-top
                                      (format-value (inc depth) b)]
                                     [:td (when (= (inc n) last-n) "}")]])))
            [:td "{}"])
        (when more? [:tr [:td {:col-span 2}
                          [expander-outter {:on-click #(swap! state update :limit-n + 20)} [expander-label "…"]]]])]])))

(defview format-function
  {:view/initial-state (fn [_ value] {:expanded? false})}
  [{:keys [view/state]} value]
  (let [{:keys [expanded?]} @state
        fn-name (some-> (source-lookups/fn-name value) (symbol) (name))]

    [:span
     [expander-outter {:on-click #(swap! state update :expanded? not)}
      [expander-label
       (if (and fn-name (not= "" fn-name))
         (some-> (source-lookups/fn-name value) (symbol) (name))
         [:span.o-50.mr1 "ƒ"])
       (-> (if expanded? icons/ArrowDropUp
                         icons/ArrowDropDown)
           (icons/size 20)
           (icons/class "mln1 mrn1 o-50"))]
      (when expanded?
        (or (some-> (source-lookups/js-source->clj-source (.toString value))
                    (codemirror/viewer))
            (some-> (source-lookups/fn-var value)
                    (special-views/var-source))))]]))

(def ^:dynamic *format-depth* 0)

(defn format-value
  ([value] (format-value 1 value))
  ([depth value]
   (cond (satisfies? show/IShow value)

         (format-value depth (show/show value))

         (and (satisfies? IMeta value)
              (contains? (meta value) :IView/view))
         (:IView/view (meta value))

         :else (case (messages/kind value)

                 (:maria.kinds/vector
                   :maria.kinds/sequence
                   :maria.kinds/set) (format-collection nil depth value)

                 :maria.kinds/map (format-map nil depth value)

                 (:maria.kinds/var
                   :maria.kinds/cell) (format-value depth @value)

                 :maria.kinds/nil "nil"

                 :maria.kinds/function (format-function value)

                 :maria.kinds/atom (format-value depth (gobj/get value "state"))

                 (cond
                   (v/is-react-element? value) value
                   (instance? cljs.core/Namespace value) (str value)
                   (instance? Deferred value) (display-deferred {:deferred value})
                   :else (try (pr-str value)
                              (catch js/Error e
                                (do "error printing result"
                                    (.log js/console e)
                                    (prn (type value))
                                    (prn :kind (messages/kind value))
                                    (.log js/console value)
                                    (prn value)))))))))

(defn display-source [{:keys [source error error/position warnings]}]
  [:.code.overflow-auto.pre.gray.mv3.ph3
   {:style {:max-height 200}}
   (codemirror/viewer {:error-ranges (cond-> []
                                             position (conj (magic/highlights-for-position source position))
                                             (seq warnings) (into (map #(magic/highlights-for-position source (:warning-position %)) warnings)))} source)])

(defview display-result
  {:key :id}
  [{:keys [value
           error
           warnings
           show-source?
           block-id
           source] :as result}]
  (error-view/error-boundary {:block-id block-id}
                             (let [warnings (sequence (comp (distinct)
                                                            (map messages/reformat-warning)
                                                            (keep identity)) warnings)
                                   error? (or error (seq warnings))]
                               (when error
                                 (.error js/console error))
                               [:div
                                {:class (when error? "bg-darken-red")}
                                (when (and source (or show-source? error (seq warnings)))
                                  (display-source result))
                                [:.ws-prewrap.relative      ;.mv3.pv1
                                 (if error?
                                   [:.ph3.overflow-auto
                                    (->> (for [message (concat warnings
                                                               (messages/reformat-error result))
                                               :when message]
                                           [:.mv2 message])
                                         (interpose [:.bb.b--red.o-20.bw2]))]

                                   [:.ph3 (format-value value)])]])))

(defn repl-card [& content]
  (into [:.sans-serif.bg-white.shadow-4.ma2] content))
