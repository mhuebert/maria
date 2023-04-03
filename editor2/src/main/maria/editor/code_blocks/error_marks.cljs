(ns maria.editor.code-blocks.error-marks
  (:require ["@codemirror/view" :refer [EditorView Decoration]]
            ["@codemirror/state" :refer [StateEffect StateField]]
            [applied-science.js-interop :as j]
            [applied-science.js-interop.alpha :refer [js]]
            [maria.editor.code-blocks.eval-region :refer [uppermost-edge-here]]
            [nextjournal.clojure-mode.node :as n]))

(defn pos [^js state [line column]]
  (+ (.. state -doc (line line) -from)
     (dec column)))

(defn get-range [state location]
  (let [from (pos state location)]
    (some->> (n/nearest-touching state from 1)
             (uppermost-edge-here from)
             (n/balanced-range state))))

(def mark:error (j/lit {:attributes {:class "bg-red-100 text-red-700"}}))

(js

  (defn get-decorations [state line column]
    (let [{:keys [from to]} (get-range state [line column])]
      (.set Decoration
            [(-> (.mark Decoration mark:error)
                 (.range from to))])))

  (def no-decorations (.-none Decoration))
  (defonce effect (.define StateEffect))
  (defonce field (.define StateField {:create (fn [state] no-decorations)
                                      :update (fn [prev {:as tr :keys [effects docChanged selectionChanged]}]
                                                (or (some-> (first (filter #(.is % effect) effects))
                                                            (j/get :value))
                                                    no-decorations))}))



  (defn set-highlight! [{:as view :keys [state dispatch]} ^:clj [line column :as location]]
    (dispatch view {:effects (.of effect (if location
                                           (get-decorations state line column)
                                           no-decorations))}))

  (defn extension []
    [field
     (.. EditorView -decorations (from field))]))