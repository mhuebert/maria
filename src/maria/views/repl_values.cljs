(ns maria.views.repl-values
  (:require [clojure.string :as string]
            [maria.messages :as messages]
            [re-view.util :as v-util]
            [re-view.core :as v :refer [defview]]
            [maria.views.repl-shapes :as shapes]
            [cljs.pprint :refer [pprint]]))


(defn bracket-type [value]
  (cond (vector? value) ["[" "]"]
        (set? value) ["#{" "}"]
        :else ["(" ")"]))

(defn format-value
  [value]
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
  [{:keys [value error warnings source ns view/props] :as result}]
  [:div.bb.b--darken
   [:.o-50.code.overflow-auto.ma3 source]
   [:.ws-prewrap.overflow-hidden.mv3
    (cond (or error (seq warnings))
          [:.bg-near-white.ph3.pv2.overflow-auto
           (for [message (cons (some-> error messages/reformat-error)
                               (map messages/reformat-warning (distinct warnings)))
                 :when message]
             [:.pv2 message])]
          (v/is-react-element? value)
          value
          :else [:.mh3 (format-value value)])]
   (when ns
     [:.pa3 [:span.b "Namespace: "] (str ns)])])

(defn repl-card [& content]
  (into [:.sans-serif.bg-white.shadow-4.ma2] content))