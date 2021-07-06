(ns maria.editors.code
  (:require

   [lark.editors.codemirror :as cm]
   ["codemirror" :as CM]
   ["codemirror/addon/selection/mark-selection"]
   ["codemirror/mode/clojure/clojure"]

   [chia.view :as v]

   [maria.util :as util]
   [maria.views.floating.float-ui :as hint]
   [lark.editor :as Editor]
   [lark.tree.core :as tree]
   [maria.live.ns-utils :as ns-utils]
   [maria.views.dropdown :as dropdown]
   [maria.views.bottom-bar :as bottom-bar]
   [lark.structure.edit :as edit]
   [goog.functions :as gf]
   [fast-zip.core :as z]
   [lark.tree.range :as range]
   [lark.editor :as editor]))

(defn eldoc-view [sym]
  (some->> sym
           (ns-utils/resolve-var-or-special)
           (bottom-bar/ShowVar)))

(def update-completions!
  (-> (fn [{:as editor
            {:keys [loc pos]} :magic/cursor} & [hide?]]
        (when-let [node (some-> (cm/sexp-near pos loc)
                                (z/node))]
          (if-let [completion-data (and (not hide?)
                                        (not (.somethingSelected editor))
                                        node
                                        (= (range/bounds node :right)
                                           (range/bounds pos :left))
                                        (ns-utils/completion-data node))]
            (hint/floating-view! {:component dropdown/numbered-list
                                  :props {:on-selection (fn [[alias completion full-name]]
                                                          (some->> (eldoc-view full-name)
                                                                   (bottom-bar/add-bottom-bar! :eldoc/completion)))
                                          :class "shadow-4 bg-white"
                                          :on-select! (fn [[alias completion full-name]]
                                                        (hint/clear!)
                                                        (cm/replace-range! editor completion node))
                                          :select-on-enter true
                                          :items (for [[alias completion full-name] (ns-utils/ns-completions completion-data)]
                                                   {:value [alias completion full-name]
                                                    :label [:.flex.items-center.w-100.monospace.f7.ma2.ml0
                                                            alias
                                                            [:.flex-auto]
                                                            [:.gray.pl3 (str (or (namespace full-name)
                                                                                 full-name))]]})}
                                  :float/pos (util/rect->abs-pos (Editor/cursor-coords editor)
                                                                 [:right :bottom])
                                  :float/offset [0 10]})
            (do (hint/clear!)
                (bottom-bar/retract-bottom-bar! :eldoc/completion)))))
      (gf/debounce 75)))

(def options
  {:theme "maria-light"
   :lineNumbers false
   :lineWrapping true
   :cursorScrollMargin 40
   :mode "clojure"
   :keyMap "default"
   :styleSelectedText true
   :magicBrackets true
   :magicEdit true
   :flattenSpans true
   :configureMouse (fn [cm repeat e]
                     #js {:moveOnDrag (if (.-shiftKey e)
                                        false
                                        true)})})

(defn reset-value! [{:keys [default-value value view/state]}]
  (cm/set-value-and-refresh! (:editor @state) (or value default-value)))

(v/defclass CodeView
            {#_#_:view/spec {:props {:event/mousedown :Function
                                     :event/keydown   :Function
                                     :on-ast          :Function
                                     :keymap          :Map}}
             :view/did-mount     (fn [{:keys [default-value
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
                                         editor (CM dom-node
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
                                                        {{prev-loc :loc prev-pos :pos} :magic/cursor}
                                                        {{loc :loc pos :pos} :magic/cursor}]
                                                     (js/setTimeout
                                                      #(when (.hasFocus editor)
                                                         (some->> (edit/eldoc-symbol loc pos)
                                                                  (eldoc-view)
                                                                  (bottom-bar/add-bottom-bar! :eldoc/cursor))
                                                         (when-not (= pos prev-pos)
                                                           (update-completions! editor (or (not= (:line pos) (:line prev-pos))
                                                                                           (< (:column pos)
                                                                                              (:column prev-pos))))))
                                                      0)))

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
                                            (.on editor "change" #(do (prn :on-update (.getValue %1)) (on-update (.getValue %1)))))

                                          (when before-change
                                            (.on editor "beforeChange" before-change))

                                          (when on-selection-activity
                                            (.on editor "cursorActivity" on-selection-activity)))

                                        (when auto-focus (.focus editor))

                                        (cm/mark-ranges! editor error-ranges #js {"className" "error-text"})))
             :get-editor         #(:editor @(:view/state %))
             :set-value          (fn [{:keys [view/state value]}]
                                   (when-let [editor (:editor @state)]
                                     (cm/set-preserve-cursor! editor value)))
             :reset-value        (fn [{:keys [default-value value view/state]}]
                                   (.setValueAndRefresh (:editor @state) (or value default-value)))
             :view/should-update (fn [{:keys [value] :as this}]
                                   (let [editor (.getEditor this)]
                                        (if (not= value (.getValue editor))
                                          (.resetValue this)
                                          nil))
                                   false)}
  [{:keys [view/state view/props] :as this}]
  [:.cursor-text
   (-> (select-keys props [:style :class])
       (merge {:on-click #(when (= (.-target %) (.-currentTarget %))
                            (let [editor (:editor @state)]
                              (doto editor
                                (.setCursor (.lineCount editor) 0)
                                (.focus))))
               :dangerouslySetInnerHTML {:__html ""}}))])

(v/defclass viewer [{:keys [view/props]} source]
  (CodeView (merge {:read-only? true
                    :value source}
                   props)))


