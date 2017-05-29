(ns maria.editor
  (:require [cljsjs.codemirror]
            [cljsjs.codemirror.mode.clojure]
            [cljsjs.codemirror.addon.edit.closebrackets]
            [magic-tree.codemirror.addons]
            [magic-tree.codemirror.util :as cm]
            [re-view.core :as v :refer-macros [defview]]

            [goog.events :as events]
            [cljs.pprint :refer [pprint]]
            [re-db.d :as d]))

;; to support multiple editors
(defn init-local-storage
  "Given a unique id, initialize a local-storage backed source"
  [uid default-src]
  (d/transact! [[:db/add uid :source (or (aget js/window "localStorage" uid) default-src)]])
  (d/listen {:ea_ [[uid :source]]} (fn []
                                     (aset js/window "localStorage" uid (d/get uid :source))))
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
  {:view/spec          {:props {:local-storage   :Vector
                                :event/mousedown :Function
                                :event/keydown   :Function}}
   :life/initial-state {:editor nil}
   :life/will-mount
                       #(some->> (:local-storage %)
                                 (apply init-local-storage))
   :life/did-mount
                       (fn [{:keys                [value read-only? on-mount cm-opts view/state view/props error-ranges]
                             [local-storage-id _] :local-storage
                             :as                  this}]
                         (let [dom-node (v/dom-node this)
                               editor (js/CodeMirror dom-node
                                                     (clj->js (merge cm-opts
                                                                     (cond-> options
                                                                             read-only? (-> (select-keys [:theme :mode :lineWrapping])
                                                                                            (assoc :readOnly "nocursor"))))))]
                           (set! (.-view editor) this)

                           (swap! state assoc :editor editor)

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

                           (when-let [initial-source (or value (d/get local-storage-id :source))]
                             (.setValue editor (str initial-source)))
                           (when local-storage-id
                             (.on editor "change" #(d/transact! [[:db/add local-storage-id :source (.getValue %1)]])))
                           (cm/mark-ranges! editor error-ranges #js {"className" "error-text"})))
   :set-value          (fn [{:keys [view/state value]}]
                         (when-let [editor (:editor @state)]
                           (cm/set-preserve-cursor editor value)))
   :life/will-receive-props
                       (fn [{next-value      :value
                             {:keys [value]} :view/prev-props
                             :as             this}]
                         (when (not= next-value value)
                           (.setValue this)))
   :life/should-update (fn [_] false)}
  [{:keys [view/state] :as this}]
  [:.h-100])

(v/defn viewer [props source]
  (editor (merge {:read-only? true
                  :value      source}
                 props)))


