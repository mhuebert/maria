(ns cljs-static.page
  (:require [cljs-static.assets :as assets]
            [hiccup.util :as hu]
            [hiccup2.core :as hiccup]))

(defn map<> [f coll]
  #?(:cljs
     (into [:<>] (map f coll))
     :clj
     (doall (map f coll))))

;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; HTML generation

(defn update-some [m updaters]
  (reduce-kv (fn [m k update-f]
               (cond-> m
                       (contains? m k) (update k update-f))) m updaters))

(defn element-tag [kw-or-hiccup]
  (cond-> kw-or-hiccup
          (keyword? kw-or-hiccup) (vector)))

(defn script-tag
  ([opts content]
   [:script
    (update-some opts {:src assets/path*})
    (some-> content hiccup/raw)])
  ([opts-or-content]
   (if-let [value (:value opts-or-content)]
     (script-tag (dissoc opts-or-content :value) value)
     (if (string? opts-or-content)
       (script-tag {} opts-or-content)
       (script-tag opts-or-content nil)))))

(defn style-tag [str-or-map]
  (if (string? str-or-map)
    [:style str-or-map]
    [:link (-> str-or-map
               (assoc :rel "stylesheet")
               (update-some {:href assets/path*}))]))

(defn meta-tag [k v]
  [:meta {(if (some #{"Expires"
                      "Pragma"
                      "Cache-Control"
                      "imagetoolbar"
                      "x-dns-prefetch-control"} (name k))
            :http-equiv
            :name) (name k)
          :content v}])

(def doctype "<!DOCTYPE html>\n")

(defn root
  "Return html string for title and props"
  ([title page-props]
   (root (assoc page-props :title title)))
  ([{:as page-props
     :keys [lang
            title
            charset
            styles
            meta
            head
            body]
     body-scripts :scripts/body
     head-scripts :scripts/head
     :or {lang "en"
          charset "UTF-8"}}]
   (hiccup/html {:mode :html}
                (hu/raw-string doctype)
                [:html (merge {:lang lang} (:props/html page-props))
                 [:head

                  (map<> (fn [[k v]] (meta-tag k v)) meta)

                  [:meta {:http-equiv "Content-Type"
                          :content (str "text/html; charset=" charset)}]

                  (when title
                    [:title title])

                  (map<> style-tag styles)
                  (map<> element-tag head)
                  (map<> script-tag head-scripts)]

                 [:body (:props/body page-props {})
                  (map<> element-tag body)
                  (map<> script-tag body-scripts)]])))


(comment
 (html "Welcome"
       {:styles [{:href "/some/styles.css"}
                 ".black {color: #000}"]
        :body [:div#app]
        :scripts/body [{:src "/some/script.js"}
                       "alert('hi!')"]}))

