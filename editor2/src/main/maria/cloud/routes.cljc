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

(declare path-for)

;; TODO - when CLJS-3399 is fixed, we can use :as-alias with symbols

(r/redef !routes
  (r/atom
   (impl/resolve-views
    ["/"
     {"" 'maria.cloud.pages.landing/page
      ["curriculum/" :curriculum/name] 'maria.cloud.views/learn
      ["gist/" :gist/id] 'maria.cloud.views/gist


      "intro" [::redirect "/curriculum/clojure-with-shapes"]}])))

(defn match-route [path]
  (when-let [{:keys [route-params
                     handler]} (bidi/match-route @!routes path)]
    (merge route-params handler {::path path})))

#?(:cljs (declare history))

#?(:cljs
   (defn handle-match [{:as match ::keys [view redirect]}]
     (if redirect
       (pushy/set-token! history redirect)
       (lazy/load view (fn [view] (reset! !location (assoc match ::view view)))))))

#?(:cljs
   (defonce history (pushy/pushy handle-match match-route)))

#?(:cljs
   (defn merge-query! [params]
     (let [{::keys [path query-params]} (query-params/merge-query (::path @!location) params)]
       (pushy/set-token! history path)
       query-params)))

(defn path-for [view & {:as params :keys [query]}]
  (cond-> (bidi/path-for @!routes view (dissoc params :query))
          query
          (-> (query-params/merge-query query) :path)))

#?(:cljs
   (defn init []
     (pushy/start! history)))

(comment
 (bidi/path-for @!routes 'maria.cloud.views/learn {:curriculum/name "x"})
 (bidi/match-route @!routes "/curriculum/x")

 (match-route "/curriculum/x"))