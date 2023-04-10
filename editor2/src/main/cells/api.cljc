(ns cells.api
  (:require [cells.async :as async]
            [cells.hooks :as hooks]
            [cells.impl :as impl]
            [re-db.reactive :as r]))

(defn- defn-opts [body]
  (let [[docstring body] (if (string? (first body))
                           [(first body) (rest body)]
                           [nil body])
        [options body] (if (and (map? (first body)) (> (count body) 1))
                         [(first body) (rest body)]
                         [nil body])]
    [docstring options body]))

(defn ^:macro cell
  ([&form &env key expr]
   `(impl/make-cell ~key (fn [~'self] ~expr)))
  ([&form &env expr]
   `(impl/make-cell (fn [~'self] ~expr))))

(defn ^:macro defcell [&form &env the-name & body]
  (let [[doc options body] (defn-opts body)]
    `(r/redef
       ~(vary-meta the-name merge options (when doc {:doc doc}))
       (impl/make-cell (fn [~'self] ~@body)))))

(defn ^:macro timeout
  "Runs body after timeout of n milliseconds."
  [&form &env n & body]
  `(~'cells.hooks/use-timeout ~n (fn [] ~@body)))

(defn fetch
  "Fetch a resource from a url. By default, response is parsed as JSON and converted to Clojure via clj->js with :keywordize-keys true.
  Accepts a :format option (:json, :text, or a function) and :query for supplying query parameters.
  Accepts options :format, which may be :json or :text"
  ([url] (fetch url nil))
  ([url & {:as opts :keys [format query]}]
   (hooks/use-fetch url opts)))

(defn interval
  "Calls `f` on interval of n milliseconds, with previous value, starting with optional init value."
  ([n f] (interval n f nil))
  ([n f init]
   (hooks/use-interval n f init)))

(defn geo-location
  "Requests a user's geographic location using the browser's Navigator.geolocation api."
  []
  (hooks/use-geo-location))

(def loading? (comp async/loading? deref async/!status))
(def message (comp async/error deref async/!status))
(def error? (comp boolean message))
(defn status [cell] (some #{:error :loading} @(async/!status cell)))


(defn dependencies [cell] (filter impl/cell? (r/get-derefs cell)))

(defn dependents [cell] (filter impl/cell? (keys (r/get-watches cell))))

(def cell? impl/cell?)