(ns maria.cloud.routes
  (:require [bidi.bidi :as bidi]
            [maria.cloud.impl.routes :as impl]
            [maria.cloud.query-params :as query-params]
            [maria.cloud.views :as-alias views]
            [re-db.reactive :as r]
            [shadow.lazy :as lazy]
            #?(:cljs [vendor.pushy.core :as pushy]))
  #?(:cljs (:require-macros maria.cloud.routes)))

(defonce !location
  (r/atom {}))

(r/redef !routes
  (r/atom
   (impl/resolve-views
    ["/"
     {"" 'maria.cloud.pages.landing/page
      ["curriculum/" :curriculum/name] 'maria.cloud.views/curriculum}])))

(defn match-route [path]
  (when-let [{:keys [route-params
                     handler]} (bidi/match-route @!routes path)]
    (assoc route-params ::view handler ::path path)))

#?(:clj
   (defmacro path-for [view & {:as params}]
     ;; TODO - when CLJS-3399 is fixed, this can be a function again
    `(bidi/path-for @!routes ~(impl/resolve-syms view) ~params)))

#?(:cljs
   (defn handle-match [{:as match ::keys [view]}]
     (lazy/load view (fn [view] (reset! !location (assoc match ::view view))))))

#?(:cljs
   (defonce history (pushy/pushy handle-match match-route)))

#?(:cljs
   (defn init []
     (pushy/start! history)))

#?(:cljs
   (defn merge-query! [params]
     (let [{::keys [path query-params]} (query-params/merge-query (::path @!location) params)]
       (pushy/set-token! history path)
       query-params
       #_(swap! !current-location assoc :query-params query-params))))

(comment
 (bidi/path-for @!routes 'maria.cloud.views/curriculum {:curriculum/name "x"})
 (bidi/match-route @!routes "/curriculum/x")

 (match-route "/curriculum/x"))