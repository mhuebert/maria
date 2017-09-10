(ns maria.editors.code
  (:require

    [codemirror.addon.markselection]
    [codemirror.mode.clojure]
    [structure.codemirror :as cm]

    [re-view.core :as v :refer [defview]]

    [maria.util :as util]
    [maria.views.floating.float-ui :as hint]
    [maria.editors.editor :as Editor]
    [magic-tree.core :as tree]
    [maria.live.ns-utils :as ns-utils]
    [maria.views.dropdown :as dropdown]
    [maria.views.bottom-bar :as bottom-bar]
    [structure.edit :as edit]))

(defn show-eldoc! [the-sym]
  (bottom-bar/show-var! (some-> the-sym
                                (ns-utils/resolve-var-or-special))))

(defn update-completions! [{{node :bracket-node
                             pos  :pos :as cursor} :magic/cursor :as editor}]
  (if (and node
           (= :token (:tag node))
           (symbol? (tree/sexp node))
           (= (tree/bounds node :right)
              (tree/bounds pos :left)))
    (hint/floating-hint! {:component     dropdown/numbered-list
                          :cancel-events ["mousedown" "scroll" "focus"]
                          :props         {:on-selection (fn [[completion full-name]]
                                                          (show-eldoc! full-name))
                                          :class        "shadow-4 bg-white"
                                          :on-select!   (fn [[completion full-name]]
                                                          (hint/clear-hint!)
                                                          (cm/replace-range! editor completion node))
                                          :items        (for [[completion full-name] (ns-utils/ns-completions (tree/string node))]
                                                          {:value [completion full-name]
                                                           :label [:.flex.items-center.w-100.monospace.f7.ma2.ml0
                                                                   (str completion)
                                                                   [:.flex-auto]
                                                                   [:.gray.pl3 (str (or (namespace full-name)
                                                                                        full-name))]]})}
                          :rect          (Editor/cursor-coords editor)})
    (hint/clear-hint!)))

(def options
  {:theme              "maria-light"
   :lineNumbers        false
   :lineWrapping       true
   :cursorScrollMargin 40
   :mode               "clojure"
   :keyMap             "default"
   :styleSelectedText  true
   :magicBrackets      true
   :magicEdit          true})

(defview CodeView
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

                                (add-watch editor :maria
                                           (fn [editor
                                                {{prev-loc :loc :as prev-cursor} :magic/cursor}
                                                {{loc :loc :as cursor} :magic/cursor}]
                                             (when (.hasFocus editor)
                                               (when (not= prev-loc loc)
                                                 (show-eldoc! (edit/eldoc-symbol loc)))
                                               (when-not (= cursor prev-cursor)
                                                 (update-completions! editor)))))

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
  [{:keys [view/state view/props] :as this}]
  [:.cursor-text
   (-> (select-keys props [:style :class :classes])
       (merge {:on-click #(when (= (.-target %) (.-currentTarget %))
                            (let [editor (:editor @state)]
                              (doto editor
                                (.setCursor (.lineCount editor) 0)
                                (.focus))))}))])

(v/defn viewer [props source]
  (CodeView (merge {:read-only? true
                    :value      source}
                   props)))


