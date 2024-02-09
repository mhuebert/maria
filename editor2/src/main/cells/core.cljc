(ns cells.core
  (:require [cells.async :as async]
            [cells.hooks :as hooks]
            [cells.impl :as impl]
            [re-db.reactive :as r]
            [maria.cloud.macros :refer [sci-macro]])
  #?(:cljs (:require-macros cells.core)))

(defn- defn-opts [body]
  (let [[docstring body] (if (string? (first body))
                           [(first body) (rest body)]
                           [nil body])
        [options body] (if (and (map? (first body)) (> (count body) 1))
                         [(first body) (rest body)]
                         [nil body])]
    [docstring options body]))


(sci-macro cell
  ([key expr]
   `(impl/make-cell ~key (fn [~'self] ~expr)))
  ([expr]
   `(impl/make-cell (fn [~'self] ~expr))))

(sci-macro defcell [the-name & body]
  (let [[doc options body] (defn-opts body)]
    `(r/redef
       ~(vary-meta the-name merge options (when doc {:doc doc}))
       (impl/make-cell (fn [~'self] ~@body)))))

(sci-macro with-view [the-cell view-expr]
  `(~'cells.impl/->WithMeta ~the-cell {'~'cells.impl/view (fn [~'self] ~view-expr)}))

(defn get-view [cell] (get (meta cell) 'cells.impl/view))

(sci-macro timeout
  "Runs body after timeout of n milliseconds."
  [n & body]
  `(~'cells.hooks/use-timeout ~n (fn [] ~@body)))

#?(:cljs
   (defn fetch
     "Fetch a resource from a url. By default, response is parsed as JSON and converted to Clojure via clj->js with :keywordize-keys true.
     Accepts a :format option (:json, :text, or a function) and :query for supplying query parameters.
     Accepts options :format, which may be :json or :text"
     ([url] (fetch url nil))
     ([url & {:as opts :keys [format query]}]
      (hooks/use-fetch url opts))))

#?(:cljs
   (defn interval
     "Calls `f` on interval of n milliseconds, with previous value, starting with optional init value."
     ([n f] (interval n f (f nil)))
     ([n f init]
      (hooks/use-interval n f init))))

#?(:cljs
   (defn geo-location
     "Requests a user's geographic location using the browser's Navigator.geolocation api."
     []
     (hooks/use-geo-location)))

(def loading? (comp async/loading? deref async/!loading?))
(def message (comp first r/deref-result))
(def error? (comp some? message))

(defn status [cell]
  (cond (loading? cell) :loading
        (error? cell) :error))

(defn dependencies [cell] (filter impl/cell? (r/get-derefs cell)))

(defn dependents [cell] (filter impl/cell? (keys (r/get-watches cell))))

(def cell? impl/cell?)