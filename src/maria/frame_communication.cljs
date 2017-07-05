(ns maria.frame-communication
  (:require [cognitect.transit :as t]
            [re-view.core :as v :refer [defview]]
            [clojure.string :as string]
            [cljs.core.match :refer-macros [match]]
            [re-db.d :as d]))

(enable-console-print!)

;; NOTES
;; - you can `console.log`, but not `print`, cross-origin windows.

(def deserialize (partial t/read (t/reader :json)))
(def serialize (partial t/write (t/writer :json)))

(def dev? (string/includes? (aget js/window "location" "origin") ":5000"))

(def is-parent? (= js/window (.-parent js/window)))
(def is-child? (not is-parent?))

(def parent-window (when is-child? (.-parent js/window)))
(def parent-origin (if dev?
                     "http://localhost:5000"
                     "https://www.maria.cloud"))

(def child-origin
  (if dev?
    "http://0.0.0.0:5000"
    "https://env.maria.cloud"))

(def other-origin (if is-parent? child-origin parent-origin))

(def current-frame-id (if is-parent?
                        "parent"
                        (do

                          (some->> (.. js/window -location -hash)
                                   (re-find #"#frame_(.*)")
                                   (second)))))

(def windows-loaded #js [(.-parent js/window) js/window])
(defn js-contains? [c x] (> (.indexOf c x) -1))

(def send-message-queue (atom {}))

(defn send
  "Send a message to the other frame."
  [target-window message]
  (if (js-contains? windows-loaded target-window)
    (.postMessage target-window
                  (serialize [current-frame-id message])
                  other-origin)
    (swap! send-message-queue update target-window conj message)))

(def listeners (atom {}))

(defn listen
  "Listen for messages from the other frame."
  [id f]
  (swap! listeners update id (fnil conj #{}) f))

(defn unlisten
  [id f]
  (swap! listeners update id disj f))

(.addEventListener js/window "message"
                   (fn [e]
                     (let [event-data (.-data e)]
                       (when (and (string? event-data)
                                  (string/starts-with? event-data "["))
                         (let [[id data] (deserialize (.-data e))
                               source (.-source e)]
                           (if (= data ::ready)
                             (do (.push windows-loaded source)
                                 (doseq [message (get @send-message-queue source)]
                                   (send source message)))
                             (when (= (.-origin e) other-origin)
                               (.log js/console " :getting-listeners-for-id" id event-data)
                               (doseq [f (concat (get @listeners id) (get @listeners "*"))]
                                 (f data)))))))))

(when-not is-parent?
  (send (.-parent js/window) ::ready))

(defview frame-view
  {:spec/props        {:id         {:spec :String
                                    :doc  "unique ID for frame"}
                       :on-message {:spec :Function
                                    :doc  "Function to be called with messages from iFrame."}
                       #_:on-change  #_{:spec :Function
                                        :doc  "Function to be called with source, whenever it changes."}
                       #_:on-save    #_{:spec :Function
                                        :doc  "Function to be called with source, whenever 'save' command is fired"}}
   :frame-window      (fn [this]
                        (.. (v/dom-node this) -contentWindow))
   :send              (fn [this message]
                        (send (.frameWindow this) message))
   :life/did-mount    (fn [{:keys [id on-message messages] :as this}]
                        (when on-message
                          (listen id on-message))
                        (doseq [message messages]
                          (send (.frameWindow this) message)))
   :life/will-unmount (fn [{:keys [id on-message] :as this}]
                        (unlisten id on-message))}
  [{:keys [id]}]
  [:iframe.maria-editor-frame
   {:src     (str child-origin "/eval#frame_" id)
    :sandbox "allow-scripts allow-same-origin allow-popups allow-top-navigation"}])

(def init-local-storage
  "Given a unique id, initialize a local-storage backed source"
  (memoize (fn
             [id]
             (d/transact! [[:db/add id :local (aget js/window "localStorage" id)]])
             (d/listen {:ea_ [[id :local]]}
                       #(aset js/window "localStorage" id (d/get id :local)))
             id)))

(defview editor-frame-view
  {:spec/props              {:default-value :String}
   :life/will-mount         #(init-local-storage (:id %))
   :life/will-receive-props #(init-local-storage (:id %))}
  [{:keys [id on-update on-save default-value]}]
  (frame-view {:id         id
               :messages   [[:source/reset {:id        id
                                            :local     (d/get id :local)
                                            :persisted (d/get id :persisted)
                                            :default   default-value}]]
               :on-message (fn [message]
                             (match message
                                    [:editor/update id value] (d/transact! [[:db/add id :local value]])
                                    [:editor/save id value] (on-save value)))}))

;; editor frames...
;; - pass source text
;; - pass a callback for when the source text changes
;; - pass a callback for when 'save' action is activated