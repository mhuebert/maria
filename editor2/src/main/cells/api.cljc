(ns cells.api
  (:require [cells.impl :as impl]
            [cells.hooks :as lib]))

(defn- defn-opts [body]
  (let [[docstring body] (if (string? (first body))
                           [(first body) (rest body)]
                           [nil body])
        [options body] (if (and (map? (first body)) (> (count body) 1))
                         [(first body) (rest body)]
                         [nil body])]
    [docstring options body]))

(defn ^:macro cell [&form &env expr]
  `(impl/make-cell (fn [~'self] ~expr)))

(defn ^:macro defcell [&form &env the-name & body]
  (let [[docstring options body] (defn-opts body)]
    `(do
       (defonce ~the-name nil)
       (let [prev-cell# ~the-name]
         (def ~(vary-meta the-name merge options)
           ~@(when docstring (list docstring))
           (impl/make-cell (fn [~'self] ~@body)))
         (impl/after-redef prev-cell# ~the-name)
         ~the-name))))

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
   (lib/use-fetch url opts)))

(defn interval
  "Calls `f` on interval of n milliseconds, with previous value, starting with optional init value."
  ([n f] (interval n f nil))
  ([n f init]
   (lib/use-interval n f init)))

(defn geo-location
  "Requests a user's geographic location using the browser's Navigator.geolocation api."
  []
  (lib/use-geo-location))