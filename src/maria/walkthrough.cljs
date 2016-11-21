(ns maria.walkthrough
  (:require [cljsjs.marked]
            [maria.eval :refer [eval-src]]
            [maria.codemirror :refer [viewer]]
            [re-view.core :as v :refer [defcomponent]]))

(def ^:dynamic *depth* 0)

(defn with-attrs [el attrs]
  {:pre [(vector? el)]}
  (if (map? (first el)) (update el 0 merge attrs)
                        (into [attrs] el)))

(defn md [s]
  [:div {:dangerouslySetInnerHTML {:__html (js/marked s)}
         :key                     (str "md-" *depth* "-" (some-> s (subs 0 20)))}])

(defn mixed [& items]
  (map-indexed (fn [i item]
                 (cond (js/React.isValidElement item) item
                       (string? item) (md item)
                       (seq? item) (doall (binding [*depth* (inc *depth*)] (map mixed item)))
                       (vector? item) (with-attrs item {:key (str "mixed-" *depth* "-" i)})
                       :else (println "Unknown form passed to `mixed`"))) items))

(defcomponent code-split
              :render
              (fn [{{:keys [evaluate]} :state
                    [source] :children
                    :as this}]
                [:.flex
                 [:.w-50.bg-solarized-light (viewer source)]
                 [:.w-50
                  (if evaluate
                    ;; does not yet differentiate between error and value
                    (viewer (with-out-str (prn (:value (eval-src source)))))
                    [:div.pointer.br-100-ns.bg-near-white.tc.dib.bold.w3.h3.v-mid.ml3.serif.f2.black-30
                     {:on-click #(v/swap-state! this assoc :evaluate true)} "?"])]]))

(defcomponent
  main
  [:.serif.center.mw7.mt5.f4
   [:.f1.tc "Walkthrough"]
   (mixed
     "Explore basic data types: String, list, nil"

     (code-split "\"duck\"")

     "Now we can explain something else:"

     (code-split "'(\"duck\" \"Berlin bear\" \"whale\")")

     )])
