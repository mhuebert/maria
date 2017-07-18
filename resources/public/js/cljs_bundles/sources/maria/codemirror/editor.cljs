(ns maria.codemirror.editor
  (:require [cljsjs.codemirror]
            [cljsjs.codemirror.mode.clojure]
            [cljsjs.codemirror.addon.edit.closebrackets]
            [magic-tree-codemirror.addons]
            [magic-tree-codemirror.util :as cm]
            [re-view.core :as v :refer-macros [defview]]
            [goog.events :as events]))

(def options
  {:theme             "maria-light"
   :autoCloseBrackets "()[]{}\"\""
   :lineNumbers       false
   :lineWrapping      true
   :mode              "clojure"
   :keyMap            "default"
   :magicBrackets     true
   :magicEdit         true})

(defview editor
  {:view/spec               {:props {:event/mousedown :Function
                                     :event/keydown   :Function}}
   :view/did-mount          (fn [{:keys [default-value on-ast-update value on-update read-only? on-mount cm-opts view/state view/props error-ranges]
                                  :as   this}]
                              (let [dom-node (v/dom-node this)
                                    editor (js/CodeMirror dom-node
                                                          (clj->js (merge cm-opts
                                                                          (cond-> options
                                                                                  read-only? (-> (select-keys [:theme :mode :lineWrapping])
                                                                                                 (assoc :readOnly "nocursor"))))))]
                                (set! (.-view editor) this)
                                (set! (.-setValueAndRefresh editor) #(do
                                                                       (.setValue editor %)
                                                                       (.refresh editor)))

                                (swap! state assoc :editor editor)

                                (some->> on-ast-update
                                         (swap! editor assoc :on-ast-update))

                                (when-not read-only?

                                  ;; event handlers are passed in as props with keys like :event/mousedown
                                  (doseq [[event-key f] (filter (fn [[k v]] (= (namespace k) "event")) props)]
                                    (let [event-key (name event-key)]
                                      (if (#{"mousedown" "click" "mouseup" "focus" "blur"} event-key)
                                        ;; attach mouse handlers to dom node, preventing CodeMirror selection by using goog.events capture phase
                                        (events/listen dom-node event-key #(f % editor) true)
                                        ;; attach other handlers to CodeMirror instance
                                        (.on editor event-key f))))
                                  (when on-mount (on-mount editor this)))

                                (some->> (or value default-value) (str) (.setValueAndRefresh editor))

                                (.focus editor)

                                (when on-update
                                  (.on editor "change" #(on-update (.getValue %1))))

                                (cm/mark-ranges! editor error-ranges #js {"className" "error-text"})))
   :set-value               (fn [{:keys [view/state value]}]
                              (when-let [editor (:editor @state)]
                                (cm/set-preserve-cursor editor value)))
   :reset-value             (fn [{:keys [default-value value view/state]}]
                              (.setValueAndRefresh (:editor @state) (or value default-value)))
   :view/will-receive-props (fn [{value                       :value
                                  source-id                   :source-id
                                  {prev-source-id :source-id} :view/prev-props
                                  state                       :view/state
                                  :as                         this}]
                              (cond (not= source-id prev-source-id)
                                    (.resetValue this)
                                    :else nil))
   :life/should-update      (fn [_] false)}
  [{:keys [view/state on-focus on-blur view/props] :as this}]
  [:.flex-auto
   (-> (select-keys props [:style :class :classes])
       (merge {:on-click #(when (= (.-target %) (.-currentTarget %))
                            (let [editor (:editor @state)]
                              (doto editor
                                (.setCursor (.lineCount editor) 0)
                                (.focus))))}))])

(v/defn viewer [props source]
  (editor (merge {:read-only? true
                  :value      source}
                 props)))
