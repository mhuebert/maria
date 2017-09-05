(ns maria.views.codemirror
  (:require [cljsjs.codemirror :as CM]
            [maria.block-views.editor :as Editor]
            [codemirror.addon.markselection]
            [codemirror.mode.clojure]

            [magic-tree-editor.codemirror :as cm]
            [re-view.core :as v :refer-macros [defview]]
            [maria.util :as util]))

(def options
  {:theme             "maria-light"
   :lineNumbers       false
   :lineWrapping      true
   :mode              "clojure"
   :keyMap            "default"
   :styleSelectedText true
   :magicBrackets     true
   :magicEdit         true})

(defview editor
  {:view/spec               {:props {:event/mousedown :Function
                                     :event/keydown   :Function
                                     :on-ast          :Function
                                     :keymap          :Map}}
   :view/did-mount          (fn [{:keys [default-value
                                         auto-focus
                                         value
                                         on-update
                                         read-only?
                                         on-mount
                                         cm-opts
                                         view/state
                                         view/props
                                         error-ranges
                                         before-change
                                         on-selection-activity
                                         keymap]
                                  :as   this}]
                              (let [dom-node (v/dom-node this)
                                    editor (js/CodeMirror dom-node
                                                          (clj->js (merge cm-opts
                                                                          {:value (str (or value default-value))}
                                                                          (cond-> options
                                                                                  keymap (assoc :extraKeys (clj->js keymap))
                                                                                  read-only? (-> (select-keys [:theme :mode :lineWrapping])
                                                                                                 (assoc
                                                                                                   :readOnly true
                                                                                                   :tabindex -1))))))]
                                (set! (.-view editor) this)
                                (swap! editor assoc :view this)
                                (set! (.-setValueAndRefresh editor) #(do (cm/set-preserve-cursor! editor %)
                                                                         (.refresh editor)))

                                (swap! state assoc :editor editor)

                                (when-not read-only?

                                  ;; event handlers are passed in as props with keys like :event/mousedown
                                  (util/handle-captured-events this)

                                  (when on-mount (on-mount editor this))

                                  (when on-update
                                    (.on editor "change" #(on-update (.getValue %1))))

                                  (when before-change
                                    (.on editor "beforeChange" before-change))

                                  (when on-selection-activity
                                    (.on editor "cursorActivity" on-selection-activity)))

                                (when auto-focus (.focus editor))

                                (cm/mark-ranges! editor error-ranges #js {"className" "error-text"})))
   :get-editor              #(:editor @(:view/state %))
   :set-value               (fn [{:keys [view/state value]}]
                              (when-let [editor (:editor @state)]
                                (cm/set-preserve-cursor! editor value)))
   :reset-value             (fn [{:keys [default-value value view/state]}]
                              (.setValueAndRefresh (:editor @state) (or value default-value)))
   :view/will-receive-props (fn [{value                       :value
                                  source-id                   :source-id
                                  {prev-source-id :source-id} :view/prev-props
                                  :as                         this}]
                              (if (or (not= source-id prev-source-id)
                                      (not= value (.getValue (.getEditor this))))
                                (.resetValue this)
                                nil))
   :view/should-update      (fn [_] false)}
  [{:keys [view/state on-focus on-blur view/props] :as this}]
  [:.cursor-text
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
