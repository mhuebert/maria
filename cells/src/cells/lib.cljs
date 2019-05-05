(ns cells.lib
  (:refer-clojure :exclude [delay])
  (:require [cells.cell :as cell :refer [cell]]
            [goog.net.XhrIo :as xhr]
            [goog.net.ErrorCode :as errors]
            [cells.util]
            [applied-science.js-interop :as j]
            [cells.owner :as owner])
  (:require-macros [cells.lib])
  (:import [goog Uri]))

(defn -on-frame
  ([f] (-on-frame f nil))
  ([f initial-value]
   (let [self cell/*cell*
         stop? (volatile! false)
         interval-f (cell/bound-fn frame-f []
                      (reset! self (f @self))
                      (when-not @stop?
                        (.requestAnimationFrame js/window frame-f)))]
     (owner/on-dispose self :on-frame #(vreset! stop? true))
     (reset! self initial-value)
     (.requestAnimationFrame js/window interval-f))))

(defn interval
  ([n f] (interval n f nil))
  ([n f initial-value]
   (if (= n :frame)
     (-on-frame f initial-value)
     (let [self cell/*cell*
           clear-key (volatile! nil)
           _ (owner/on-dispose self :interval #(some-> @clear-key (js/clearInterval)))
           interval-f (cell/bound-fn [] (reset! self (f @self)))]
       (vreset! clear-key (js/setInterval interval-f n))
       (reset! self (f initial-value))))))

(defn delay
  [n value]
  (let [self cell/*cell*
        clear-key (volatile! nil)
        _ (owner/on-dispose self :delay #(some-> @clear-key (js/clearTimeout)))
        timeout-f (cell/bound-fn [] (reset! self value))]
    (vreset! clear-key (js/setTimeout timeout-f n))
    nil))

(def ^:private parse-fns
  {:json->clj (comp #(js->clj % :keywordize-keys true) js/JSON.parse)
   :json      js/JSON.parse
   :text      identity})

(defn- xhrio-error-message [xhrio]
  (when-not (j/get xhrio :isSuccess)
    (str (-> (j/call xhrio :getLastErrorCode)
             (errors/getDebugMessage))
         \newline
         "(check your browser console for more details)")))

(defn- query-string [query]
  (-> Uri
      .-QueryData
      (.createFromMap (clj->js query)) (.toString)))

(defn fetch
  "Fetch a resource from a url. By default, response is parsed as JSON and converted to Clojure via clj->js with :keywordize-keys true.
  Accepts options :format, which may be :json or :text, and :query, a map which will be
  appended to url as a query parameter string."
  ([url]
   (fetch url {}))
  ([url {:keys [format query]
         :or   {format :json->clj}}]
   (let [self cell/*cell*
         url (cond-> url
                     query (str "?" (query-string query)))
         parse (get parse-fns format)]

     (cell/loading! self)

     (xhr/send url
               (cell/bound-fn [event]
                 (let [xhrio (j/get event :target)]
                   (if-let [error-message (xhrio-error-message xhrio)]
                     (cell/error! self error-message)
                     (let [formatted-value (-> (j/call xhrio :getResponseText)
                                               (parse))]
                       (cell/complete! self)
                       (reset! self formatted-value))))))
     @self)))

(defn geo-location
  []
  (let [self cell/*cell*]
    (cell/loading! self)
    (-> (j/get js/navigator :geolocation)
        (j/call :getCurrentPosition
                (cell/bound-fn [location]
                  (cell/complete! self)
                  (-> (j/get location :coords)
                      (j/select-keys [:latitude :longitude])
                      (js->clj :keywordize-keys true)
                      (->> (reset! self))))
                (cell/bound-fn [error]
                  (cell/error! self (str error)))))))

(defn loading? [cell] (:async/loading? cell))
(defn error [cell] (:async/error cell))

(def loading! cell/loading!)
(def error! cell/error!)
(def complete! cell/complete!)

(defn status [cell] (cond (:async/error cell) :error
                          (:async/loading? cell) :loading
                          :else nil))


(defn -timeout
  ([n f] (-timeout n f nil))
  ([n f initial-value]
   (let [self cell/*cell*
         _ (cell/loading! self)
         clear-key (js/setTimeout (cell/bound-fn []
                                    (cell/complete! self)
                                    (reset! self (f @self))) n)]
     (owner/on-dispose self #js{} #(js/clearTimeout clear-key))
     initial-value)))