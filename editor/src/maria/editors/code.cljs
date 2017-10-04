(ns maria.editors.code
  (:require

    #_["codemirror/addon/selection/mark-selection"]
    #_["codemirror/mode/clojure/clojure"]
    [lark.structure.codemirror :as cm]

    [re-view.core :as v :refer [defview]]

    [maria.util :as util]
    [maria.views.floating.float-ui :as hint]
    [lark.editors.editor :as Editor]
    [lark.tree.core :as tree]
    [maria.live.ns-utils :as ns-utils]
    [maria.views.dropdown :as dropdown]
    [maria.views.bottom-bar :as bottom-bar]
    [lark.structure.edit :as edit]
    [goog.functions :as gf]))

(defn eldoc-view [sym]
  (some->> sym
           (ns-utils/resolve-var-or-special)
           (bottom-bar/ShowVar)))

(def update-completions!
  (fn [{{node :bracket-node
         pos  :pos :as cursor} :magic/cursor :as editor}]
    (if (and (not (.somethingSelected editor))
             node
             (= :symbol (:tag node))
             (= (tree/bounds node :right)
                (tree/bounds pos :left)))
      (hint/floating-view! {:component    dropdown/numbered-list
                            :props        {:on-selection (fn [[alias completion full-name]]
                                                           (some->> (eldoc-view full-name)
                                                                    (bottom-bar/add-bottom-bar! :eldoc/completion)))
                                           :class        "shadow-4 bg-white"
                                           :on-select!   (fn [[alias completion full-name]]
                                                           (hint/clear!)
                                                           (cm/replace-range! editor completion node))
                                           :items        (for [[alias completion full-name] (ns-utils/ns-completions node)]
                                                           {:value [alias completion full-name]
                                                            :label [:.flex.items-center.w-100.monospace.f7.ma2.ml0
                                                                    alias
                                                                    [:.flex-auto]
                                                                    [:.gray.pl3 (str (or (namespace full-name)
                                                                                         full-name))]]})}
                            :float/pos    (util/rect->abs-pos (Editor/cursor-coords editor)
                                                              [:right :bottom])
                            :float/offset [0 10]})
      (do (hint/clear!)
          (bottom-bar/retract-bottom-bar! :eldoc/completion)))))

(def options
  {:theme              "maria-light"
   :lineNumbers        false
   :lineWrapping       true
   :cursorScrollMargin 40
   :mode               "clojure"
   :keyMap             "default"
   :styleSelectedText  true
   :magicBrackets      true
   :magicEdit          true
   :configureMouse     (fn [cm repeat e]
                         #js {:moveOnDrag (if (.-shiftKey e)
                                            false
                                            true)})})

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
                                                {{loc :loc pos :pos :as cursor} :magic/cursor}]
                                             (when (.hasFocus editor)
                                               (when (not= prev-loc loc)
                                                 (some->> (edit/eldoc-symbol loc pos)
                                                          (eldoc-view)
                                                          (bottom-bar/add-bottom-bar! :eldoc/cursor)))
                                               (when-not (= cursor prev-cursor)
                                                 (update-completions! editor)))))

                                (.on editor "blur" #(bottom-bar/retract-bottom-bar! :eldoc/cursor))

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


