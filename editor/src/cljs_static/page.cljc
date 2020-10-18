(ns cljs-static.page)

(defn map<> [f coll]
  #?(:cljs
     (into [:<>] (map f coll))
     :clj
     (doall (map f coll))))

;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; HTML generation

(defn element-tag [kw-or-hiccup]
  (cond-> kw-or-hiccup
          (keyword? kw-or-hiccup) (vector)))

(defn script-tag [str-or-map]
  [:script str-or-map])

(defn meta-tag [k v]
  [:meta {(if (some #{"Expires"
                      "Pragma"
                      "Cache-Control"
                      "imagetoolbar"
                      "x-dns-prefetch-control"} (name k))
            :http-equiv
            :name) (name k)
          :content v}])

(defn style-tag [str-or-map]
  (if (string? str-or-map)
    [:style str-or-map]
    [:link (-> str-or-map
               (assoc :rel "stylesheet"))]))

(defn root
  "Return HTML string for title and props"
  ([title page-props]
   (root (assoc page-props :title title)))
  ([{:as          page-props
     :keys        [lang
                   title
                   charset
                   styles
                   meta
                   head
                   body]
     body-scripts :scripts/body
     head-scripts :scripts/head
     :or          {lang    "en"
                   charset "UTF-8"}}]
   [:html {:lang lang}
    [:head

     (map<> (fn [[k v]] (meta-tag k v)) meta)

     [:meta {:http-equiv "Content-Type"
             :content    (str "text/html; charset=" charset)}]

     (when title
       [:title title])

     (map<> style-tag styles)
     (map<> element-tag head)
     (map<> script-tag head-scripts)]

    [:body
     (map<> element-tag body)
     (map<> script-tag body-scripts)]]))

(def doctype "<!DOCTYPE html>\n")

(comment
 (html "Welcome"
       {:styles       [{:href "/some/styles.css"}
                       ".black {color: #000}"]
        :body         [:div#app]
        :scripts/body [{:src "/some/script.js"}
                       "alert('hi!')"]}))

