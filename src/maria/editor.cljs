(ns maria.editor
  (:require [cljsjs.codemirror]
            [cljsjs.codemirror.mode.clojure]
            [cljsjs.codemirror.addon.edit.closebrackets]
            [magic-tree.codemirror.addons]
            [magic-tree.codemirror.util :as cm]
            [re-view.core :as v :refer-macros [defview]]
            [re-view.subscriptions :as subs :include-macros true]

            [goog.events :as events]
            [cljs.pprint :refer [pprint]]
            [re-db.d :as d]))

;; to support multiple editors
(defn init-local-storage
  "Given a unique id, initialize a local-storage backed source"
  [uid default-src]
  (d/transact! [[:db/add uid :source (or (aget js/window "localStorage" uid) default-src)]])
  (d/listen! [uid :source] (fn [datom]
                             (aset js/window "localStorage" uid (datom 2))))
  uid)

(def options
  {:theme             "solarized light"
   :autoCloseBrackets "()[]{}\"\""
   :lineNumbers       false
   :lineWrapping      true
   :mode              "clojure"
   :keyMap            "macDefault"
   :magicBrackets     true
   :magicEdit         true})

(defview editor
  {:subscriptions
   {:source (subs/db [this] (some-> this
                                    (get-in [:props :local-storage])
                                    first
                                    (d/get :source)))}
   :will-mount
   #(some->> (get-in % [:props :local-storage])
             (apply init-local-storage))
   :did-mount
   (fn [{{:keys [value read-only? on-mount cm-opts local-storage] :as props} :props :as this}]
     (let [dom-node (js/ReactDOM.findDOMNode (v/ref this "editor-container"))
           editor (js/CodeMirror dom-node
                                 (clj->js (merge cm-opts
                                                 (cond-> options
                                                         read-only? (-> (select-keys [:theme :mode :lineWrapping])
                                                                        (assoc :readOnly "nocursor"))))))]
       (set! (.-view editor) this)

       (swap! this assoc :editor editor)

       (when-not read-only?

         ;; event handlers are passed in as props with keys like :event/mousedown
         (doseq [[event-key f] (filter (fn [[k v]] (= (namespace k) "event")) props)]
           (let [event-key (name event-key)]
             (if (#{"mousedown" "click" "mouseup"} event-key)
               ;; attach mouse handlers to dom node, preventing CodeMirror selection by using goog.events capture phase
               (events/listen dom-node event-key f true)
               ;; attach other handlers to CodeMirror instance
               (.on editor event-key f))))
         (when on-mount (on-mount editor this)))

       (when-let [initial-source (or value (get-in this [:state :source] value))]
         (.setValue editor (str initial-source)))
       (when-let [local-storage-uid (first local-storage)]
         (.on editor "change" #(d/transact! [[:db/add local-storage-uid :source (.getValue %1)]])))))

   :will-receive-props
   (fn [{{next-value :value} :props
         {:keys [value]}     :prev-props
         :as                 this}]
     (when (not= next-value value)
       (when-let [editor (get-in this [:state :editor])]
         (cm/set-preserve-cursor editor next-value))))

   :will-receive-state
   (fn [{{next-source :source}   :state
         {:keys [source editor]} :prev-state}]
     (when (and (not= next-source source) editor)
       (cm/set-preserve-cursor editor next-source)))

   :should-update
   (fn [_] false)}
  [:.h-100 {:ref "editor-container"}])

(defn viewer [source]
  (editor {:read-only? true
           :value      source}))


