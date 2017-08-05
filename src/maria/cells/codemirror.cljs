(ns maria.cells.codemirror
  (:require [cljsjs.codemirror]
            [cljsjs.codemirror.mode.clojure]
            [cljsjs.codemirror.addon.edit.closebrackets]
            [magic-tree-codemirror.addons]
            [magic-tree-codemirror.util :as cm]
            [re-view.core :as v :refer-macros [defview]]
            [goog.events :as events]
            [maria.util :as util]))

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
                                         keymap]
                                  :as   this}]
                              (let [dom-node (v/dom-node this)
                                    editor (js/CodeMirror dom-node
                                                          (clj->js (merge cm-opts
                                                                          (cond-> options
                                                                                  keymap (assoc :extraKeys (clj->js keymap))
                                                                                  read-only? (-> (select-keys [:theme :mode :lineWrapping])
                                                                                                 (assoc :readOnly true))))))]
                                (set! (.-view editor) this)
                                (swap! editor assoc :view this)
                                (set! (.-setValueAndRefresh editor) #(do (.setValue editor %)
                                                                         (.refresh editor)))

                                (swap! state assoc :editor editor)

                                (some->> (or value default-value) (str) (.setValue editor))
                                (js/setTimeout #(.refresh editor) 0)

                                (when-not read-only?

                                  ;; event handlers are passed in as props with keys like :event/mousedown
                                  (util/handle-captured-events this)

                                  (when on-mount (on-mount editor this))

                                  (when on-update
                                    (.on editor "change" #(on-update (.getValue %1)))))

                                (when auto-focus (.focus editor))

                                (cm/mark-ranges! editor error-ranges #js {"className" "error-text"})))
   :get-editor              #(:editor @(:view/state %))
   :set-value               (fn [{:keys [view/state value]}]
                              (when-let [editor (:editor @state)]
                                (cm/set-preserve-cursor editor value)))
   :reset-value             (fn [{:keys [default-value value view/state]}]
                              (.setValueAndRefresh (:editor @state) (or value default-value)))
   :focus                   (fn [this coords]
                              (let [cm (:editor @(:view/state this))
                                    coords (if (keyword? coords)
                                             (case coords :end #js {:line (.lineCount cm)
                                                                    :ch   (count (.getLine cm (.lineCount cm)))}
                                                          :start #js {:line 0
                                                                      :ch   0})
                                             coords)]
                                (doto cm
                                  (.focus)
                                  (cond-> coords (.setCursor coords)))))
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
