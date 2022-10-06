(ns cells.lib
  (:refer-clojure :exclude [delay])
  (:require [applied-science.js-interop :as j]
            [goog.net.XhrIo :as xhr]
            [goog.net.ErrorCode :as errors]
            [re-db.hooks :as hooks]
            [re-db.reactive :as r]
            [promesa.core]
            [cells.cell :as cell]
            [clojure.string :as str])
  #_#_#_(:require [cells.cell :as cell :refer [cell]]
         [goog.net.XhrIo :as xhr]
         [goog.net.ErrorCode :as errors]
         [applied-science.js-interop :as j]
         [clojure.string :as str])
          (:require-macros [cells.lib])
          (:import [goog Uri]))

(defn some-str [s] (when-not (str/blank? s) s))

(defn -on-frame
  ([f] (-on-frame f nil))
  ([f initial-value]
   (let [[value set-value!] (hooks/use-state initial-value)
         [disposed? dispose!] (hooks/use-volatile false)
         cell r/*owner*]
     (hooks/use-effect (fn [] (dispose! true)))
     (hooks/use-effect
      (fn []
        (when-not disposed?
          (let [frame (js/requestAnimationFrame
                       #(binding [r/*owner* cell]
                          (set-value! f)))]
            #(.cancelAnimationFrame js/window frame))))
      #js[value])
     value)))

(defn interval
  ([n f] (interval n f nil))
  ([n f initial-value]
   (let [[value set-value!] (hooks/use-state initial-value)
         cell r/*owner*]
     (hooks/use-effect
      (fn []
        (let [i (js/setInterval #(binding [r/*owner* cell]
                                   (set-value! f)) n)]
          #(js/clearInterval i))))
     value)))

(def ^:private parse-fns
  {:json->clj (comp #(js->clj % :keywordize-keys true) js/JSON.parse)
   :json js/JSON.parse
   :text identity})

(j/defn xhrio-error-message [^js XhrIo]
  (when-not (.isSuccess XhrIo)
    (str (-> (.getLastErrorCode XhrIo)
             (errors/getDebugMessage))
         \newline
         "(check your browser console for more details)")))

(defn- query-string [query]
  #_(-> ^js Uri
        .-QueryData
        (.createFromMap (clj->js query)) (.toString)))

(defn fetch
  "Fetch a resource from a url. By default, response is parsed as JSON and converted to Clojure via clj->js with :keywordize-keys true.
  Accepts options :format, which may be :json or :text"

  ;; todo
  ;; , and :query, a map which will be  appended to url as a query parameter string

  ([url]
   (fetch url {}))
  ([url & {:keys [format query]
           :or {format :json->clj}}]
   (let [[v v!] (hooks/use-state nil)
         self r/*owner*]
     (hooks/use-effect
      (fn []
        (let [parse (parse-fns format)
              XhrIo (xhr/send url
                              (j/fn [^js {XhrIo :target}]
                                (cond (= errors/ABORT (.getLastErrorCode XhrIo)) nil

                                      (.isSuccess XhrIo)
                                      (let [formatted-value (some-> (j/call XhrIo :getResponseText)
                                                                    some-str
                                                                    (parse))]
                                        (v! formatted-value)
                                        (cell/complete! self))

                                      :else
                                      (do
                                        (j/log XhrIo)
                                        (aset js/window "x" XhrIo)
                                        (if-let [error-message (xhrio-error-message XhrIo)]
                                          (cell/error! self (ex-info error-message {:cell self}))
                                          (v! "No error message"))))))]
          #(j/call XhrIo :abort)))
      [url])
     v)))

(defn geo-location
  []
  ;; TODO
  #_(let [self cell/*self*]
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

(defn -timeout
  ([n f] (-timeout n f nil))
  ([n f initial-value]
   (let [[v v!] (hooks/use-state initial-value)
         self r/*owner*]
     (hooks/use-effect
      (fn []
        (cell/loading! self)
        (let [t (js/setTimeout (fn []
                                 (v! (f))
                                 (cell/complete! self)) n)]
          #(js/clearTimeout t))))
     v)))

(defn ^:macro timeout
  "Returns cell with body wrapped in timeout of n milliseconds."
  [&form &env n & body]
  `(~'cells.lib/-timeout ~n (fn [] ~@body)))