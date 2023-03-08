(ns tools.maria.query-params
  (:require [applied-science.js-interop :as j]
            [clojure.string :as str]))

(defn params->map
  "Returns query map from URL.searchParams"
  [search-params]
  (->> search-params
       (reduce (fn [m [k v]]
                 (cond-> m
                         (not (str/blank? v))
                         (assoc (keyword k) v))) {})))

(defn path->map
  "Returns query map from url"
  [url]
  (-> (js/URL. url "https://example.com")
      (j/get :searchParams)
      (params->map)))

(defn query-string
  "Returns query string from map, including '?'. Removes empty values. Returns nil if empty."
  [m]
  (some->> m
           (reduce-kv (fn [out k v]
                        (cond-> out
                                (not (str/blank? v))
                                (conj (str (js/encodeURIComponent (name k))
                                           "="
                                           (js/encodeURIComponent v))))) [])
           (str/join "&")
           (str "?")))

(defn merge-query
  "Returns map of updated path and query map"
  [path params]
  (j/let [^js {:keys [pathname hash searchParams]} (js/URL. path "https://example.com")
          new-query-map (merge (params->map searchParams)
                               params)
          new-path (str pathname
                        (query-string new-query-map)
                        hash)]
    {:query-params new-query-map
     :path new-path }))



(defn parse-query-string
  "Returns query parameters as map."
  [query-string]
  (->> query-string
       (reduce (fn [m [k v]]
                 (cond-> m
                         (not (str/blank? v))
                         (assoc (keyword k) v))) {})))

(defn query-string
  "Returns query string, including '?', omitting blank values. Returns nil if empty."
  [m]
  (some->> m
           (reduce-kv (fn [out k v]
                        (cond-> out
                                (not (str/blank? v))
                                (conj (str (js/encodeURIComponent (name k))
                                           "="
                                           (js/encodeURIComponent v))))) [])
           (str/join "&")
           (str "?")))

(defn merge-query
  "Merges query parameters into URL (default: window.location.href). Empty values are omitted."
  ([query] (merge-query (.-href js/location) query))
  ([href query]
   (j/let [^js {:keys [pathname searchParams hash]} (js/URL. href)]
     (str pathname
          (query-string (merge (parse-query-string searchParams) query))
          hash))))