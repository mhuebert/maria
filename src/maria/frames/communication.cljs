(ns maria.frames.communication
  (:require [maria.transit :as t]
            [re-db.d :as d]))

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

  (defonce send-queue (atom {}))

  (defn send
    "Send a message to the another frame. If the frame is not yet register, queue the message."
    [frame-id message]
    (if-let [the-window (@frame-index frame-id)]
      (.postMessage the-window
                    (t/serialize [current-frame-id message])
                    other-origin)
      (swap! send-queue update frame-id conj message)))

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
                           (let [source (.-source e)]
                             (swap! frame-index assoc id source)
                             (doseq [message (get @send-queue id)]
                               (send id message)))
                           (when (= (.-origin e) other-origin)
                             (doseq [f (concat (get @listeners id) (get @listeners "*"))]
                               (f data)))))))

  (when-not is-parent?
    (send parent-frame ::ready)))