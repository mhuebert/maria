(ns maria.cloud.query-params
  (:require [applied-science.js-interop :as j]
            [clojure.string :as str])
  #?(:clj (:import [java.net URL URI])))

#?(:cljs
   (defn params->map
     "Returns query map from URL.searchParams"
     [search-params]
     (->> search-params
          (reduce (fn [m [k v]]
                    (cond-> m
                            (not (str/blank? v))
                            (assoc (keyword k) v))) {}))))

(defn path->map
  "Returns query map from url"
  [url]
  (not-empty
   #?(:cljs
      (-> (js/URL. url "https://example.com")
          (j/get :searchParams)
          (params->map))
      :clj
      (when-let [query (:query (bean (URI. url)))]
        (->> (str/split query #"&")
             (map #(str/split % #"="))
             (keep (fn [[k v]] (when-not (str/blank? v)
                                 [(keyword k) v])))
             (into {}))))))

(defn url-encode [x]
  #?(:cljs (js/encodeURIComponent x)
     :clj  (-> x
               (java.net.URLEncoder/encode "UTF-8")
               (str/replace "+" "%20"))))

(defn query-string
  "Returns query string from map, including '?'. Removes empty values. Returns nil if empty."
  [m]
  (some->> m
           (reduce-kv (fn [out k v]
                        (cond-> out
                                (not (str/blank? v))
                                (conj (str (url-encode (name k))
                                           "="
                                           (url-encode v))))) [])
           (str/join "&")
           (str "?")))

(defn merge-query
  "Returns map of updated path and query map"
  [path params]
  #?(:cljs
     (j/let [^js {:keys [pathname hash searchParams]} (js/URL. path "https://example.com")
             new-query-map (merge (params->map searchParams)
                                  params)
             new-path (str pathname
                           (query-string new-query-map)
                           hash)]
       {:query-params new-query-map
        :path new-path})))

