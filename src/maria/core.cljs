(ns maria.core
  (:require

    [maria.walkthrough :as walkthrough]
    [maria.views.repl :as repl]
    [maria.views.paredit :as paredit]

    ;; include to precompile for self-hosted env
    [clojure.set]
    [clojure.string]
    [clojure.walk]
    [cljs.spec :include-macros true]
    [maria.html]
    [maria.user :include-macros true]
    [re-view.routing :as r]
    [re-view.subscriptions :as subs :include-macros true]
    [re-view.core :as v :refer [defview]]
    [re-db.d :as d]))

(enable-console-print!)

(defview not-found
         [:div "We couldn't find this page!"])

(defonce _ (r/on-route #(d/transact! [[:db/add ::state :route %]])))

(defview layout
         [:div.h-100
          [:.w-100.fixed.bottom-0.z-3
           [:.dib.center.left-50
            (for [[href title] [["/" "REPL"]
                                ["/walkthrough" "Walkthrough"]
                                ["/paredit" "Paredit"]]]
              [:a.dib.pa2.black-70.no-underline.f6.bg-black-05 {:href href} title])
            ]]
           (r/router (d/get ::state :route)
                    "/" repl/main
                    "/walkthrough" walkthrough/main
                    "/paredit" paredit/examples
                    not-found)])

(defn main []
  (v/render-to-dom (layout {:x 1}) "maria-main"))

(main)