(ns maria.views.repl-values
  (:require [clojure.string :as string]
            [maria.messages :as messages]
            [re-view.util :as v-util]
            [re-view.core :as v :refer [defview]]
            [maria.views.repl-shapes :as shapes]
            [cljs.pprint :refer [pprint]]
            [re-view-material.icons :as icons]))


(defn bracket-type [value]
  (cond (vector? value) ["[" "]"]
        (set? value) ["#{" "}"]
        :else ["(" ")"]))

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

     :else (if (nil? value)
             "nil"
             (try (string/trim-newline (with-out-str (pprint value)))
                  (catch js/Error e "error printing result"))))])

(defview display-result
  {:key :id}
  [{:keys [value error warnings source]}]
  [:div.bb.b--darken.overflow-hidden
   (when source
     [:.o-50.code.ma3.overflow-auto.pre
      {:style {:max-height 200}} source])
   [:.ws-prewrap.relative
    {:style {:max-height 500
             :overflow-y "auto"}}
    (cond (or error (seq warnings))
          [:.bg-near-white.ph3.pv2.overflow-auto
           (for [message (cons (some-> error messages/reformat-error)
                               (map messages/reformat-warning (distinct warnings)))
                 :when message]
             [:.ph3.pv2 message])]
          (v/is-react-element? value)
          value
          :else [:.ma3 (format-value value)])]])

(defn repl-card [& content]
  (into [:.sans-serif.bg-white.shadow-4.ma2] content))
