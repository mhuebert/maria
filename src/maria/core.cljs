(ns maria.core
  (:require
    [maria.html]
    [maria.walkthrough :as walkthrough]
    [maria.views.repl :as repl]


    ;; include to precompile for self-hosted env
    [clojure.set]
    [clojure.string]
    [clojure.walk]
    [maria.user :include-macros true]
    [cljs.spec :include-macros true]


    [re-view.routing :refer [router]]
    [re-view.core :as v :refer-macros [defcomponent]]))

(enable-console-print!)

(defcomponent not-found
  :render
  (fn [] [:div "We couldn't find this page!"]))

(defcomponent layout
  :subscriptions {:main-view (router "/" repl/main
                                     "/walkthrough" walkthrough/main
                                     not-found)}
  :render
  (fn [_ _ {:keys [main-view]}]
    [:div.h-100
     [:.w-100.fixed.bottom-0.z-3
      [:.dib.center
       [:a.dib.pa2.black-70.no-underline.f6.bg-black-05 {:href "/"} "REPL"]
       [:a.dib.pa2.black-70.no-underline.f6.bg-black-05 {:href "/walkthrough"} "Walkthrough"]]]
     (main-view)]))

(defn main []
  (v/render-to-dom (layout) "maria-main"))

(main)