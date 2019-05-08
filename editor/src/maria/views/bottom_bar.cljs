(ns maria.views.bottom-bar
  (:require [chia.view :as v]
            [chia.db :as d]
            [maria.live.ns-utils :as ns-utils]
            [maria.util :as util]))

(defn ShowVar [{:keys [name display-name display-namespace arglists meta doc] :as the-var}]
  (when the-var
    [:.ph3.pv2.flex.items-center
     [:.gray.flex-none.nowrap (or display-namespace (str (namespace name))) "/"]
     [:span.nowrap.b.mr2 (or display-name (clojure.core/name name))] " "
     (when-let [arglists (or (:arglists meta) arglists)]
       (->> arglists
            (ns-utils/elide-quote)
            (mapv str)
            (interpose " ")
            (into [:.mr2.truncate.blue.flex-none.nowrap])))
     [:.gray.nowrap.truncate (or doc (:doc meta))]]))

(defn retract-bottom-bar! [key]
  (d/transact! [[:db/update-attr :ui/globals :bottom-bar-stack #(remove (fn [[the-key _]]
                                                                          (= the-key key)) %)]]))

(defn add-bottom-bar! [key view]
  (if (nil? view)
    (retract-bottom-bar! key)
    (d/transact! [[:db/update-attr :ui/globals :bottom-bar-stack #(->> (cons [key view] %)
                                                                       (util/distinct-by first))]])))

(v/defclass BottomBar
  [this]
  (let [bottom-bar (d/get :ui/globals :bottom-bar-stack)
        [_ top-view] (first bottom-bar)]
    [:#bottom-bar.bt.monospace.flex-none.fixed.bottom-0.left-0.right-0.f7.z-999
     {:style {:border-color     "rgba(0,0,0,0.03)"
              :background-color "#e9e9e9"}}

     top-view]))