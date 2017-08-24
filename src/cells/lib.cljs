(ns cells.lib
  (:require [cells.cell :as cell
             :refer [*cell-stack* -set-async-state!]
             :refer-macros [cell-fn cell]
             :include-macros true]
            [cells.eval-context :refer [on-dispose handle-error]]
            [goog.net.XhrIo :as xhr]
            [goog.net.ErrorCode :as errors])
  (:require-macros [cells.lib])
  (:import [goog Uri]))

(declare -on-frame
         interval
         timeout
         fetch
         geo-location)

(def status cell/status)
(def message cell/message)
(def error? cell/error?)
(def loading? cell/loading?)
(def dependencies cell/dependencies)
(def dependents cell/dependents)

#_(defn restricted-swap! [specified-name cell & args]
    (if (instance? cell/Cell cell)
      (do (assert (= specified-name (name cell)))
          (apply cell/swap-cell! cell args))
      (apply swap! cell args)))

#_(defn restricted-reset! [specified-name cell newval]
    (if (instance? cell/Cell cell)
      (do (assert (= specified-name (name cell)))
          (cell/reset-cell! cell newval))
      (reset! cell newval)))

(defn- query-string [query]
  (-> Uri .-QueryData (.createFromMap (clj->js query)) (.toString)))

(defn -on-frame
  ([f] (-on-frame f nil))
  ([f initial-value]
   (let [self (first cell/*cell-stack*)
         stop? (volatile! false)
         interval-f (cell-fn frame-f []
                             (reset! self (f @self))
                             (when-not @stop?
                               (.requestAnimationFrame js/window frame-f)))]
     (on-dispose self #(vreset! stop? true))
     (reset! self initial-value)
     (.requestAnimationFrame js/window interval-f))))

(defn interval
  ([n f] (interval n f nil))
  ([n f initial-value]
   (if (= n :frame)
     (-on-frame f initial-value)
     (let [self (first cell/*cell-stack*)
           clear-key (volatile! nil)
           _ (on-dispose self #(some-> @clear-key (js/clearInterval)))
           interval-f (cell-fn [] (reset! self (f @self)))]
       (vreset! clear-key (js/setInterval interval-f n))
       (reset! self (f initial-value))))))

(defn- timeout
  ([n f] (timeout n f nil))
  ([n f initial-value]
   (let [self (first cell/*cell-stack*)
         clear-key (js/setTimeout (cell-fn [] (reset! self (f @self))) n)]
     (on-dispose self #(js/clearTimeout clear-key))
     initial-value)))

(def parse-fns {:json->clj (comp #(js->clj % :keywordize-keys true) js/JSON.parse)
                :json      js/JSON.parse
                :text      identity})

(defn fetch
  "Fetch a resource from a url. By default, response is parsed as JSON and converted to Clojure via clj->js with :keywordize-keys true.
  Accepts options :format, which may be :json or :text, and :query, a map which will be
  appended to url as a query parameter string."
  ([url]
   (fetch url {}))
  ([url {:keys [format query]
         :or   {format :json->clj}
         :as   options}]
   [url options]
   (let [self (first cell/*cell-stack*)
         url (cond-> url
                     query (str "?" (query-string query)))
         parse (get parse-fns format)]
     (cell/-set-async-state! self :loading)
     (xhr/send url (cell-fn [event]
                            (let [xhrio (.-target event)]
                              (if-not (.isSuccess xhrio)
                                (-set-async-state! self :error {:message (-> xhrio .getLastErrorCode (errors/getDebugMessage))
                                                                :xhrio   xhrio})
                                (let [formatted-value (-> xhrio (.getResponseText) (parse))]
                                  (-set-async-state! self nil)
                                  (reset! self formatted-value))))))
     @self)))

(defn geo-location
  []
  (js/navigator.geolocation.getCurrentPosition
    (cell-fn [location]
             (->> {:latitude  (.. location -coords -latitude)
                   :longitude (.. location -coords -longitude)}
                  (reset! (first *cell-stack*))))))
