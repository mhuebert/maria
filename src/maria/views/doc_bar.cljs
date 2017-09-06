(ns maria.views.doc-bar
  (:require [re-view.core :as v :refer [defview]]
            [re-db.d :as d]
            [maria.live.ns-utils :as ns-utils]))

(defview dock
  [this]
  [:.bt.code.flex.items-center.z-1.flex-none.fixed.bottom-0.left-0.right-0.ph3.f7.z-9999
   {:style {:border-color     "rgba(0,0,0,0.03)"
            :background-color "#e9e9e9"
            :height           30
            }}
   (when-let [{:keys [name arglists meta doc]} (some-> (d/get :code/context :current-sym)
                                                   (ns-utils/resolve-var-or-special))]
     (list
       [:.gray.flex-none.nowrap (namespace name) "/"]
       [:span.nowrap (clojure.core/name name)] " "
       (->> (or (:arglists meta) arglists)
            (ns-utils/elide-quote)
            (mapv str)
            (interpose " ")
            (into [:.mh2.truncate.blue.flex-none.nowrap]))
       [:.gray.nowrap.truncate (or doc (:doc meta))]))])