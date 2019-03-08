(ns maria.blocks.code
  (:require [chia.view :as v]
            [chia.view.legacy :as vlegacy]
            [chia.reactive :as r]

            [lark.editors.codemirror :as cm]
            [lark.editor :as Editor]
            [lark.commands.exec :as exec]
            [lark.tree.node :as node]
            [lark.tree.emit :as emit]
            [lark.editor :as editor]

            [cells.cell :as cell]
            [cells.eval-context :as eval-context]

            [maria.blocks.blocks :as Block]
            [maria.eval :as e]
            [maria.editors.code :as code]
            [maria.editors.code :as code]
            [maria.views.error :as error]
            [maria.views.values :as value-views]
            [maria.views.icons :as icons]

            [goog.dom.classes :as classes]))

(def -dispose-callbacks (volatile! {}))

(vlegacy/defview CodeRow
  {:key :id
   :view/should-update #(not= (:block %) (:block (:view/prev-props %)))
   :view/did-mount Editor/mount
   :view/will-unmount Editor/unmount}
  [{:keys [view/state
           block-list
           block
           before-change
           on-selection-activity] :as this}]
  [:.flex.pv2.cursor-text.flex-column.flex-row-ns
   {:on-click #(when (= (.-target %) (.-currentTarget %))
                 (Editor/focus! (editor/get-editor this)))}
   [:.w-100.w-50-ns.flex-none.relative.cm-s-maria-light
    [:.absolute.bottom-0.right-0.pa2.z-3.dib.dn-ns.pointer
     {:on-click #(Block/eval! block)}
     (-> icons/Play
         (icons/size 18)
         (icons/class "cm-variable"))]
    (error/error-boundary
     {:on-error (fn [{:keys [error]}]
                  (e/handle-block-error (:id block) error))}
     (code/CodeView {:class "pa3 bg-white"
                     :ref #(r/silently
                            (swap! state assoc :editor-view %))
                     :value (str (:block this))
                     :on-ast (fn [node]
                               (.splice block-list block [(assoc block :node node)]))
                     :before-change before-change
                     :on-selection-activity on-selection-activity
                     :capture-event/focus #(exec/set-context! {:block/code true
                                                               :block-view this})
                     :capture-event/blur #(exec/set-context! {:block/code nil
                                                              :block-view nil})}))]

   [:.w-100.w-50-ns.flex-none.code.overflow-y-hidden.overflow-x-auto.f6.relative.code-block-result.pt3.pt0-ns
    #_(share/ShareLink {:doc doc
                        :block block
                        :block-list block-list})
    (some-> (first (Block/eval-log block))
            (assoc :block-id (:id block))
            (value-views/display-result))]])

(vlegacy/extend-view CodeRow
  editor/IEditor
  (get-editor [this]
    (some-> @(:view/state this)
            :editor-view
            (editor/get-editor))))

(extend-type Block/CodeBlock

  Block/IBlock

  (state [this] (.-node this))
  (kind [this] :code)
  (render [this props] (CodeRow (assoc props :block this
                                             :id (:id this))))

  (empty? [this]
    (let [node (:node this)
          children (if (= :base (:tag node))
                     (:children node)
                     [node])]
      (= 0 (count (remove node/whitespace? children)))))

  IEquiv
  (-equiv [this other] (and (= (:id this) (:id other))
                            (= (Block/state this) (Block/state other))))

  Object
  (toString [{:keys [node]}]
    (emit/string node))

  eval-context/IDispose
  (on-dispose [this f]
    (vswap! -dispose-callbacks update (:id this) conj f))
  (-dispose! [this]
    (doseq [f (get @-dispose-callbacks (:id this))]
      (f))
    (vswap! -dispose-callbacks dissoc (:id this)))

  eval-context/IHandleError
  (handle-error [this error]
    (e/handle-block-error (:id this) error))

  Block/IEval
  (eval-log! [this value]
    (vswap! e/-block-eval-log update (:id this) #(take 2 (cons value %)))
    (Block/update-view this)
    value)
  (eval-log [this]
    (get @e/-block-eval-log (:id this)))
  (eval!
    ([this]
     (let [editor (Editor/of-block this)
           source (or (cm/selection-text editor)
                      (str this))]
       (when-not (.somethingSelected editor)
         (cm/set-temp-marker! editor)
         (.execCommand editor "selectAll")
         (js/setTimeout #(cm/return-to-temp-marker! editor) 210))
       (let [div (.. editor -display -wrapper)]
         (classes/add div "post-eval")
         (js/setTimeout #(classes/remove div "post-eval") 200))
       (Block/eval! this :string source))
     true)
    ([this mode form]
     (eval-context/dispose! this)
     (binding [cell/*eval-context* this]
       (Block/eval-log! this ((case mode :form e/eval-form
                                         :string e/eval-str) form)))
     true)))