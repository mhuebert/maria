(ns cells.hooks
  (:refer-clojure :exclude [delay])
  (:require [applied-science.js-interop :as j]
            [goog.net.XhrIo :as xhr]
            [goog.net.ErrorCode :as errors]
            [re-db.hooks :as hooks]
            [re-db.reactive :as r]
            [promesa.core]
            [maria.util :refer [some-str guard]]
            [cells.async :as a]
            [maria.util :as u]))

(defn -on-frame
  ([f] (-on-frame f nil))
  ([f initial-value]
   (let [[value set-value!] (hooks/use-state initial-value)
         [disposed? dispose!] (hooks/use-ref false)
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

(defn use-interval
  [n f initial-value]
  (let [[value set-value!] (hooks/use-state initial-value)]
    (hooks/use-effect
     (fn []
       (let [i (js/setInterval #(set-value! f) n)]
         #(js/clearInterval i))))
    value))

(def ^:private parse-fns
  {:json->clj (comp #(js->clj % :keywordize-keys true) js/JSON.parse)
   :json js/JSON.parse
   :text identity})

(j/defn ^:private xhrio-error-message [^js XhrIo]
  (when-not (.isSuccess XhrIo)
    (str (-> (.getLastErrorCode XhrIo)
             (errors/getDebugMessage))
         \newline
         "(check your browser console for more details)")))

(defn- add-query [url query]
  (j/let [^js {:as url :keys [searchParams]} (js/URL. url)]
    (when (seq query)
      (doseq [[k v] (js/Object.entries (clj->js query))]
        (.set searchParams k v)))
    (str url)))

(defn use-fetch [url {:keys [format query]
                      :or {format :json->clj}}]
  (let [[v v!] (hooks/use-state nil)
        self r/*owner*
        url (add-query url query)]
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
                                       (a/complete! self))

                                     :else
                                     (do
                                       (j/log XhrIo)
                                       (aset js/window "x" XhrIo)
                                       (if-let [error-message (xhrio-error-message XhrIo)]
                                         (a/error! self (ex-info error-message {:cell self}))
                                         (v! "No error message"))))))]
         #(j/call XhrIo :abort)))
     [url])
    v))

(defn use-geo-location
  []
  (let [[v v!] (hooks/use-state nil)
        self r/*owner*]
    (a/loading! self)
    (-> (j/get js/navigator :geolocation)
        (j/call :getCurrentPosition
          (fn [location]
            (binding [r/*owner* self]
              (a/complete! self)
              (-> (j/get location :coords)
                  (j/select-keys [:latitude :longitude])
                  (js->clj :keywordize-keys true)
                  v!)))
          (fn [error] (a/error! self error))))
    v))

(defn use-timeout
  ([n f] (use-timeout n f nil))
  ([n f initial-value]
   (let [[v v!] (hooks/use-state initial-value)
         self r/*owner*]
     (hooks/use-effect
      (fn []
        (a/loading! self)
        (let [t (js/setTimeout (fn []
                                 (v! (f))
                                 (a/complete! self)) n)]
          #(js/clearTimeout t))))
     v)))