(ns maria.views.bottom-bar
  (:require [re-view.core :as v :refer [defview]]
            [re-db.d :as d]
            [maria.live.ns-utils :as ns-utils]))

(defn ShowVar [{:keys [name display-name display-namespace arglists meta doc] :as the-var}]
  (when the-var
    (list
      [:.gray.flex-none.nowrap (or display-namespace (str (namespace name))) "/"]
      [:span.nowrap.b.mr2 (or display-name (clojure.core/name name))] " "
      (when-let [arglists (or (:arglists meta) arglists)]
        (->> arglists
             (ns-utils/elide-quote)
             (mapv str)
             (interpose " ")
             (into [:.mh2.truncate.blue.flex-none.nowrap])))
      [:.gray.nowrap.truncate (or doc (:doc meta))])))

(defn set-bottom-bar! [view]
  (d/transact! [[:db/add :ui/globals :bottom-bar view]]))

(defn show-var! [the-var]
  (set-bottom-bar! (ShowVar the-var)))

(defview BottomBar
  [this]
  (let [bottom-bar (d/get :ui/globals :bottom-bar)]
    [:.bt.monospace.flex.items-center.flex-none.fixed.bottom-0.left-0.right-0.ph3.f7.z-999
     {:style {:border-color     "rgba(0,0,0,0.03)"
              :background-color "#e9e9e9"
              :height           (when bottom-bar 30)}}
     bottom-bar]))