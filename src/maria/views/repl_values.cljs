(ns maria.views.repl-values
  (:require [clojure.string :as string]
            [maria.messages :as messages]
            [re-view.util :as v-util]
            [re-view.core :as v :refer [defview]]
            [maria.views.repl-shapes :as shapes]
            [cljs.pprint :refer [pprint]]
            [re-view-material.icons :as icons]
            [maria.magic-tree :as magic]
            [maria.editor :as editor])
  (:import [goog.async Deferred]))

(defn bracket-type [value]
  (cond (vector? value) ["[" "]"]
        (set? value) ["#{" "}"]
        :else ["(" ")"]))

(declare format-value)

(defview display-deferred
  {:life/will-mount (fn [{:keys [deferred view/state]}]
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

(defn format-value [value]
  [:span
   (cond
     (= :shape (:is-a value)) (shapes/show value)           ; synthesize component for shape
     (or (vector? value)
         (seq? value)
         (set? value)) (let [[lb rb] (bracket-type value)]
                         (list
                           [:span.output-bracket lb]
                           (interpose " " (v-util/map-with-keys format-value value))
                           [:span.output-bracket rb]))
     (v/is-react-element? value) value
     (instance? cljs.core/Namespace value) (str value)
     (instance? Deferred value) (display-deferred {:deferred value})
     :else (if (nil? value)
             "nil"
             (try (string/trim-newline (with-out-str (pprint value)))
                  (catch js/Error e "error printing result"))))])

(defview display-result
  {:key :id}
  [{:keys [value
           error
           error-location
           warnings
           source] :as result}]
  (let [error? (or error (seq warnings))]
    (when error
      (.error js/console error))
    (when (seq warnings)
      (prn :warnings warnings))
    [:div.bb.b--darken.overflow-hidden
     {:class (when error? "bg-darken-red")}
     (when source
       [:.code.overflow-auto.pre.gray.mv3
        {:style {:max-height 200}}
        (editor/viewer {:error-ranges (cond-> []
                                              error (conj (magic/error-range source error-location))
                                              (seq warnings) (into (map #(magic/error-range source (:env %)) warnings)))} source)])
     [:.ws-prewrap.relative.mv3
      {:style {:max-height 500
               :overflow-y "auto"}}
      (cond error?
            [:.ph3.overflow-auto
             (for [message (cons (when error (messages/reformat-error result))
                                 (map messages/reformat-warning (distinct warnings)))
                   :when message]
               [:.mv2 message])]
            (v/is-react-element? value)
            value
            :else [:.ph3 (format-value value)])]]))

(defn repl-card [& content]
  (into [:.sans-serif.bg-white.shadow-4.ma2] content))
