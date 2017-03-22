(ns maria.walkthrough
  (:require [cljsjs.marked]
            [maria.eval :refer [eval-str]]
            [maria.editor :refer [viewer]]
            [re-view.core :as v :refer [defview]]))

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
                 (let [k (str "mixed-" *depth* "-" i)]

                   (cond (v/is-react-element? item) item
                         (string? item) (md item)
                         (seq? item) (doall (binding [*depth* (inc *depth*)] (map mixed item)))
                         (vector? item) (with-attrs item {:key k})
                         :else (println "Unknown form passed to `mixed`")))) items))

(defview code-split
  {:key (fn [_ x] x)}
  [{:keys [view/state] :as this} source]
  [:.flex
   [:.w-50.bg-solarized-light (viewer source)]
   [:.w-50
    (if (:evaluate @state)
      ;; does not yet differentiate between error and value
      (viewer (with-out-str (prn (:value (eval-str source)))))
      [:div.pointer.br-100-ns.bg-near-white.tc.dib.bold.w3.h3.v-mid.ml3.serif.f2.black-30
       {:on-click #(swap! state assoc :evaluate true)} "?"])]])

(defview main []
  [:.serif.center.mw7.mt5.f4
   [:.f1.tc "Walkthrough"]

   (mixed
     "Explore basic data types: String, list, nil"

     (code-split "\"duck\"")

     "Now we can explain something else:"

     (code-split "'(\"duck\" \"Berlin bear\" \"whale\")")

     )])
