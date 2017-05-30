(ns maria.views.repl-values
  (:require [clojure.string :as string]
            [maria.messages :as messages]
            [re-view.util :as v-util]
            [re-view.core :as v :refer [defview]]
            [maria.views.repl-shapes :as shapes]
            [cljs.pprint :refer [pprint]]
            [re-view-material.icons :as icons]
            [maria.magic-tree :as magic]
            [maria.editor :as editor]))

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
     (instance? cljs.core/Namespace value) (str value)
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
  (when error
    (.error js/console error))
  (when warnings
    (prn :warnings warnings))
  [:div.bb.b--darken.overflow-hidden
   (when source
     [:.code.mv3.overflow-auto.pre.gray
      {:style {:max-height 200}}
      (editor/viewer {:error-ranges (cond-> []
                                            error (into (magic/error-ranges source error-location))
                                            (seq warnings) (into (mapcat #(magic/error-ranges source (:env %)) warnings)))} source)])
   [:.ws-prewrap.relative.mv3
    {:style {:max-height 500
             :overflow-y "auto"}}
    (cond (or error (seq warnings))
          [:.bg-near-white.ph3.pv2.overflow-auto
           (for [message (cons (when error (messages/reformat-error result))
                               (map messages/reformat-warning (distinct warnings)))
                 :when message]
             [:.ph3.pv2 message])]
          (v/is-react-element? value)
          value
          :else [:.mh3 (format-value value)])]])

(defn repl-card [& content]
  (into [:.sans-serif.bg-white.shadow-4.ma2] content))
