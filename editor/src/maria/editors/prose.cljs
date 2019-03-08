(ns maria.editors.prose
  (:require [chia.view :as v]
            [chia.view.legacy :as vlegacy]
            [chia.prosemirror.commands :as commands]
            [chia.prosemirror.commands :refer [apply-command]]
            [maria.views.floating.float-ui :as hint]
            [maria.views.icons :as icons]
            [chia.prosemirror.core :as pm]
            [chia.prosemirror.markdown :as markdown]
            [goog.dom.classes :as classes]
            [maria.blocks.blocks :as Block]

            [maria.commands.prose :as prose-commands]
            [chia.routing :as routing]


            [lark.commands.exec :as exec]
            [lark.editor :as Editor]
            ["prosemirror-view" :refer [EditorView]]
            ["prosemirror-state" :as state :refer [EditorState]]
            ["prosemirror-inputrules" :as input-rules]
            [maria.util :as util]
            [lark.editor :as editor]
            [applied-science.js-interop :as j]
            [chia.reactive :as r]))


(vlegacy/defview link-dropdown [{:keys [href editor]}]
  [:.flex.items-center.bg-white.br2.shadow-4.overflow-hidden
   [:.dib.pointer.hover-bg-darken.pa2.bg-darken-lightly
    {:on-mouse-down (fn [e]
                      (.preventDefault e)
                      (.stopPropagation e)
                      (commands/apply-command editor (partial commands/open-link true))
                      (hint/clear!))}
    (-> icons/ModeEdit
        (icons/size 16)
        (icons/class "o-50"))]
   [:a.pm-link.ph2.no-underline.black.f6.hover-underline {:href href} href]])

(defn make-editor-state [doc input-rules]
  (.create EditorState
           #js {"doc" doc
                "schema" markdown/schema
                "plugins" (to-array [(input-rules/inputRules
                                      #js {:rules (->> input-rules/allInputRules
                                                       (into input-rules)
                                                       (to-array))})])}))

(defn make-editor-view [{:keys [before-change
                                editor-props
                                on-dispatch
                                on-selection-activity
                                view/state] :as component}
                        editor-state]
  (new EditorView (vlegacy/dom-node component)
       (->> (merge editor-props
                   {:state editor-state
                    :spellcheck false
                    :attributes {:class "outline-0"}
                    :handleScrollToSelection (fn [view] true)
                    :dispatchTransaction (fn [tr]
                                           (let [^js pm-view (get @state :pm-view)
                                                 prev-state (.-state pm-view)]
                                             (when before-change (before-change))
                                             (pm/transact! pm-view tr)
                                             (when-not (nil? on-dispatch)
                                               (on-dispatch pm-view prev-state))
                                             (when (and on-selection-activity
                                                        (not= (.-selection (.-state pm-view))
                                                              (.-selection prev-state)))
                                               (on-selection-activity pm-view (.-selection (.-state pm-view))))))})
            (clj->js))))

(vlegacy/defview ProseEditor
  {#_#_:spec/props {:on-dispatch :Function
                :before-change :Function
                :input-rules sequential?
                :doc any?
                :editor-props :Map}
   :view/did-mount (fn [{:keys [view/state
                                input-rules
                                doc
                                view/props] :as this}]
                     (let [editor-view (make-editor-view this (make-editor-state doc input-rules))]
                       (reset! state {:pm-view editor-view}))
                     (.resetDoc this doc))
   :view/should-update (constantly true)
   :view/did-update (fn [{:keys [doc block view/state]
                          :as this}]
                      (when (not= doc (j/get-in (:pm-view @state) [:state :doc]))
                        (.resetDoc this doc)))
   :view/will-unmount (fn [{:keys [view/state]}]
                        (pm/destroy! (:pm-view @state)))}
  [{:keys [view/props]}]
  [:.prosemirror-content
   (-> props
       (dissoc :on-dispatch
               :before-change
               :input-rules
               :doc
               :editor-props)
       (assoc :dangerouslySetInnerHTML {:__html ""}))])

(vlegacy/extend-view ProseEditor
  Object
  (pmView [this]
    (:pm-view @(:view/state this)))
  (resetDoc [{:keys [view/state parse]} doc]
    (let [view (:pm-view @state)]
      (.updateState view
                    (.create EditorState #js {"doc" doc
                                              "schema" markdown/schema
                                              "plugins" (j/get-in view [:state :plugins])})))))

(comment
 (defn Toolbar
   [pm-view]
   (let [[state dispatch] [(.-state pm-view) (.-dispatch pm-view)]]
     (when-not (.. state -selection -empty)
       [:.bg-darken-ligxhtly.h-100
        (->> [toolbar/mark-strong
              toolbar/mark-em
              toolbar/mark-code
              #_toolbar/tab-outdent
              #_toolbar/tab-indent
              #_toolbar/block-code
              #_toolbar/list-bullet
              #_toolbar/list-ordered
              #_toolbar/wrap-quote
              ]

             (map (fn [menu-item] (menu-item state dispatch))))]))))


(vlegacy/defview ProseRow
  {:key :id
   :view/should-update #(not= (:block %) (:block (:view/prev-props %)))
   :view/did-mount Editor/mount
   :view/will-unmount (fn [this]
                        (Editor/unmount this)
                        ;; prosemirror doesn't fire `blur` command when unmounted
                        (when (= (:block-view @exec/context) this)
                          (exec/set-context! {:block/prose nil
                                              :block-view nil})))}
  [{:keys [view/state block-list block before-change] :as this}]
  (ProseEditor {:doc (Block/state block)
                :editor-props {:handleDOMEvents #js {:focus #(exec/set-context! {:block/prose true
                                                                                 :block-view this})
                                                     :blur #(exec/set-context! {:block/prose nil
                                                                                :block-view nil})}}
                :block block
                :class " serif f-body ph3 cb"
                :input-rules (prose-commands/input-rules this)

                :on-click (fn [e]
                            (when-let [a (routing/closest (.-target e) routing/link?)]
                              (when-not (classes/has a "pm-link")
                                (do (.stopPropagation e)
                                    (hint/floating-view! {:float/pos (util/rect->abs-pos (.getBoundingClientRect a)
                                                                                         [:left :bottom])
                                                          :float/offset [0 10]
                                                          :component link-dropdown
                                                          :props {:href (.-href a)
                                                                  :editor (editor/get-editor this)}})))))
                :ref #(r/silently
                       (swap! state assoc :prose-editor-view %))
                :before-change before-change
                :on-dispatch (fn [pm-view prev-state]
                               #_(when (not= (.-selection (.-state pm-view))
                                             (.-selection prev-state))
                                   (bottom-bar/set-bottom-bar! (Toolbar pm-view)))
                               (when (not= (.-doc (.-state pm-view))
                                           (.-doc prev-state))
                                 (.splice block-list block [(Block/update-prose-block-state block (.-doc (.-state pm-view)))])))}))

(vlegacy/extend-view ProseRow
  editor/IEditor
  (get-editor [this] (.pmView (:prose-editor-view @(:view/state this)))))

(specify! (.-prototype EditorView)

  Editor/IHistory

  (get-selections [this]
    (.. this -state -selection))

  (put-selections! [this selections]
    (commands/apply-command this
                            (fn [state dispatch]
                              (dispatch (.setSelection (.-tr state) selections)))))

  Editor/ICursor

  (cursor-coords [this]
    (pm/cursor-coords this))

  (start [this] (pm/start-$pos (.-state this)))
  (end [this] (pm/end-$pos (.-state this)))
  (get-cursor [this] (pm/cursor-$pos (.-state this)))

  (-focus! [this coords]
    (v/flush!)
    (let [state (.-state this)
          doc (.-doc state)]
      (.focus this)
      (when-let [selection (cond (keyword? coords)
                                 (case coords :start (.atStart pm/Selection doc)
                                              :end (.atEnd pm/Selection doc))

                                 (object? coords)
                                 (pm/coords-selection this coords)
                                 :else nil)]
        (.dispatch this (-> (.-tr state)
                            (.setSelection selection)))
        (Editor/scroll-into-view (Editor/cursor-coords this))))))