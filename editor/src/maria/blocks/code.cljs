(ns maria.blocks.code
  (:require [re-view.core :as v :refer [defview]]

            [lark.structure.codemirror :as cm]
            [lark.editors.editor :as Editor]
            [lark.commands.exec :as exec]

            [cells.cell :as cell]
            [maria.blocks.blocks :as Block]
            [lark.tree.core :as tree]
            [maria.eval :as e]
            [cells.eval-context :as eval-context]
            [maria.editors.code :as code]
            [goog.dom.classes :as classes]
            [maria.editors.code :as code]
            [maria.views.error :as error]
            [maria.views.values :as value-views]
            [maria.util :as util]
            [maria.frames.frame-communication :as frame]
            [maria.commands.doc :as doc]))



(defn vec-take [coll n]
  (cond-> coll (> (count coll) n)
          (subvec (- (count coll) n) (count coll))))

(def -dispose-callbacks (volatile! {}))

(defview CodeRow
  {:key                :id
   :view/should-update #(not= (:block %) (:block (:view/prev-props %)))
   :view/did-mount     Editor/mount
   :view/will-unmount  Editor/unmount
   :get-editor         #(.getEditor (:editor-view @(:view/state %)))}
  [{:keys [view/state block-list block before-change on-selection-activity] :as this}]
  (let [doc        (:current-doc @exec/context)
        can-share? (:persisted (:project doc))]
    [:.flex.pv2.cursor-text
     {:on-click #(when (= (.-target %) (.-currentTarget %))
                   (Editor/focus! (.getEditor this)))}
     [:.w-50.flex-none
      (error/error-boundary
        {:on-error (fn [{:keys [error info]}]
                     (e/handle-block-error (:id block) error))}
        (code/CodeView {:class                 "pa3 bg-white"
                        :ref                   #(v/swap-silently! state assoc :editor-view %)
                        :value                 (Block/emit (:block this))
                        :on-ast                (fn [node]
                                                 (.splice block-list block [(assoc block :node node)]))
                        :before-change         before-change
                        :on-selection-activity on-selection-activity
                        :capture-event/focus   #(exec/set-context! {:block/code true
                                                                    :block-view this})
                        :capture-event/blur    #(exec/set-context! {:block/code nil
                                                                    :block-view nil})}))]

     [:.w-50.flex-none.code.overflow-y-hidden.overflow-x-auto.f6.relative.code-block-result
      (when can-share?
        [:.share.absolute.top-0.right-0.bg-darken.pa1.pointer.hover-bg-darken-more.f7
         {:on-click (fn []
                      (let [{node-tag   :tag
                             node-value :value} (:node block)
                            index (->> (Block/emit-list (.getBlocks block-list))
                                       (tree/ast)
                                       (:value)
                                       (remove #(or (tree/whitespace? %)
                                                    (tree/comment? %)))
                                       (take-while (fn [{:keys [tag value]}]
                                                     (or (not= value node-value)
                                                         (not= tag node-tag))))
                                       (count))
                            {:keys [id version]} (get-in doc [:project :persisted])]
                        (frame/send frame/trusted-frame [:window/navigate
                                                         (str "http://share.maria.cloud/gist/" id "/" version "/" index) {:popup? true}])))}
         "share"])
      (some-> (first (Block/eval-log block))
              (assoc :block-id (:id block))
              (value-views/display-result))]]))

(extend-type Block/CodeBlock

  IFn
  (-invoke
    ([this props] (CodeRow (assoc props :block this
                                        :id (:id this)))))

  Block/IBlock
  (kind [this] :code)
  (empty? [this]
    (let [{:keys [tag] :as node} (:node this)]
      (= 0 (count (filter (complement tree/whitespace?) (if (= :base tag)
                                                          (:value node)
                                                          [node]))))))

  (emit [this] (tree/string (:node this)))

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
    (vswap! e/-eval-logs update (:id this) #(take 2 (cons value %)))
    value)
  (eval-log [this]
    (get @e/-eval-logs (:id this)))
  (eval!
    ([this]
     (let [editor (Editor/of-block this)
           source (or (cm/selection-text editor)
                      (Block/emit this))]
       (when (.somethingSelected editor)
         (let [div (.. editor -display -wrapper)]
           (classes/add div "post-eval")
           (js/setTimeout #(classes/remove div "post-eval") 200)))
       (Block/eval! this :string source)))
    ([this mode form]
     (eval-context/dispose! this)
     (binding [cell/*eval-context* this]
       (Block/eval-log! this ((case mode :form e/eval-form
                                         :string e/eval-str) form))
       (Block/update-view this)))))