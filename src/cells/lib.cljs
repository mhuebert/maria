(ns cells.lib
  (:require [cells.cell :as cell
             :refer [*cell-stack* -set-async-state!]
             :refer-macros [cell-fn]
             :include-macros true]
            [cells.eval-context :refer [on-dispose handle-error]]
            [goog.net.XhrIo :as xhr]
            [goog.net.ErrorCode :as errors]
            [maria.show :as show])
  (:require-macros [cells.lib])
  (:import [goog Uri]))

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

(defn on-animation-frame [f]
  (let [the-cell (first *cell-stack*)
        stop? (volatile! false)
        f (fn frame-f []
            (binding [*cell-stack* (cons the-cell *cell-stack*)]
              (reset! the-cell (f @the-cell))
              (when-not @stop?
                (.requestAnimationFrame js/window frame-f))))]
    (on-dispose the-cell #(vreset! stop? true))
    (f)))

(defn interval [n f]
  (if (= n :frame)
    (on-animation-frame f)
    (let [the-cell (first *cell-stack*)
          clear-key (volatile! nil)
          f #(binding [*cell-stack* (cons the-cell *cell-stack*)]
               (try
                 (reset! the-cell (f @the-cell))
                 (catch js/Error e
                   (js/clearInterval @clear-key)
                   (throw e))))]
      (vreset! clear-key (js/setInterval f n))
      (on-dispose the-cell #(js/clearInterval @clear-key))
      (f))))

(defn timeout [n f]
  (let [the-cell (first *cell-stack*)
        clear-key (js/setTimeout #(binding [*cell-stack* (cons the-cell *cell-stack*)]
                                     (reset! the-cell (f @the-cell))) n)]
    (on-dispose the-cell #(js/clearTimeout clear-key))
    (f @the-cell)))

(defn fetch
  "Fetch a resource from a url. By default, response is parsed as JSON and converted to Clojure via clj->js with :keywordize-keys true.
  Accepts options :parse, an alternate function which will be passed the response text, and :query, a map which will be
  appended to url as a query parameter string."
  ([url]
   (fetch url {} nil))
  ([url options] (fetch url options nil))
  ([url {:keys [parse query]
         :or   {parse (comp #(js->clj % :keywordize-keys true) js/JSON.parse)}} f]
   (let [the-cell (first *cell-stack*)
         url (cond-> url
                     query (str "?" (query-string query)))]
     (cell/-set-async-state! the-cell :loading)
     (xhr/send url (cell-fn [event]
                            (let [xhrio (.-target event)]
                              (if-not (.isSuccess xhrio)
                                (-set-async-state! the-cell :error {:message (-> xhrio .getLastErrorCode (errors/getDebugMessage))
                                                                    :xhrio   xhrio})
                                (let [formatted-value (try (-> xhrio (.getResponseText) (parse))
                                                           (catch js/Error error
                                                             (handle-error cell/*eval-context* error)))]
                                  (do (-set-async-state! the-cell nil)
                                      (reset! the-cell (cond->> formatted-value
                                                                          f (f @the-cell)))))))))
     (or @the-cell nil))))

(defn geo-location
  []
  (js/navigator.geolocation.getCurrentPosition
    (cell-fn [location]
             (->> {:latitude  (.. location -coords -latitude)
                   :longitude (.. location -coords -longitude)}
                  (reset! (first *cell-stack*))))))

IMeta
