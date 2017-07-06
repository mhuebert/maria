(ns maria.frames.communication
  (:require [maria.transit :as t]
            [re-db.d :as d]
            [cljs.core.match :refer-macros [match]]))

(enable-console-print!)

;; NOTE: you can `console.log`, but not `print`, cross-origin windows.

(def port (.. js/window -location -port))
(def port-suffix (when (and port (not= "" port))
                   (str ":" port)))

(def parent-frame "parent")

(def is-parent?
  (= js/window (.-parent js/window)))
(def is-child?
  (not is-parent?))

(def queue (atom {}))
(defn queue-add [the-queue id item] (swap! queue update-in [the-queue id] (fnil conj []) item))
(defn queue-process [the-queue id f]
  (doseq [item (get-in @queue [the-queue id])]
    (f item))
  (swap! queue update the-queue dissoc id))

(let [this-hostname (.. js/window -location -hostname)
      other-hostname-lookup {"0.0.0.0"         "localhost"
                             "localhost"       "0.0.0.0"
                             "www.maria.cloud" "env.maria.cloud"
                             "env.maria.cloud" "www.maria.cloud"}
      parent-hostname (cond->> this-hostname
                               is-child? (get other-hostname-lookup))
      child-hostname (cond->> this-hostname
                              is-parent? (get other-hostname-lookup))
      protocol-prefix (str (.. js/window -location -protocol) "//")
      parent-origin (str protocol-prefix
                         parent-hostname
                         port-suffix)
      child-origin (str protocol-prefix
                        child-hostname
                        port-suffix)
      other-origin (if is-parent? child-origin parent-origin)
      current-frame-id (if is-parent?
                         "parent"
                         (some->> (.. js/window -location -hash)
                                  (re-find #"#frame_(.*)")
                                  (second)))
      frame-index (atom (cond-> {}
                                is-child?
                                (assoc "parent" (.. js/window -parent))))]

  (def child-origin child-origin)

  (defn send
    "Send a message to the another frame. If the frame is not yet register, queue the message."
    [frame-id message]
    {:pre [frame-id]}
    (if-let [the-window (get @frame-index frame-id)]
      (.postMessage the-window
                    (t/serialize [current-frame-id message])
                    other-origin)
      (queue-add :send frame-id message)))

  (def listeners (atom {}))

  (defn listen
    "Listen for messages from another frame, given its id."
    [id f]
    (swap! listeners update id (fnil conj #{}) f))

  (defn unlisten
    [id f]
    (swap! listeners update id disj f))

  (.addEventListener js/window "message"
                     (fn [e]
                       (when-let [[id data] (t/deserialize (.-data e))]
                         (if (= data ::ready)
                           (do
                             (swap! frame-index assoc id (.-source e))
                             (queue-process :send id #(send id %)))
                           (when (= (.-origin e) other-origin)
                             (doseq [f (concat (get @listeners id) (get @listeners "*"))]
                               (f data)))))))

  (when-not is-parent?
    (do
      (listen "parent" #(match % [:db/transactions txs] (d/transact! txs)))
      (send parent-frame ::ready))))