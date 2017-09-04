(ns maria.blocks.code
  (:require [re-view.core :as v :refer [defview]]
            [maria-commands.exec :as exec]
            [maria.views.values :as repl-values]
            [maria.views.codemirror :as codemirror]
            [magic-tree-editor.codemirror :as cm]
            [cells.cell :as cell]
            [maria.blocks.blocks :as Block]
            [maria.util :as util]
            [magic-tree.core :as tree]
            [magic-tree-editor.edit :as edit]
            [maria.eval :as e]
            [cells.eval-context :as eval-context]))

(defview code-view
  {:key                :id
   :view/should-update #(not= (:block %) (:block (:view/prev-props %)))
   :view/did-mount     #(Block/mount (:block %) %)
   :view/will-unmount  #(Block/unmount (:block %))
   :get-editor         #(.getEditor (:editor-view @(:view/state %)))
   :scroll-into-view   #(util/scroll-to-cursor (.getEditor %))
   :focus              (fn [this coords]
                         (.focus (:editor-view @(:view/state this)) coords))}
  [{:keys [view/state block-list block before-change after-change on-selection-activity] :as this}]
  [:.flex.pv3.cursor-text
   {:on-click #(when (= (.-target %) (.-currentTarget %))
                 (.focus this))}
   [:.w-50.flex-none
    (codemirror/editor {:class                 "pa3 bg-white"
                        :ref                   #(v/swap-silently! state assoc :editor-view %)
                        :value                 (Block/emit (:block this))
                        :on-ast                (fn [node]
                                                 (.splice block-list block [(assoc block :node node)]))
                        :before-change         before-change
                        :after-change          after-change
                        :on-selection-activity on-selection-activity
                        :capture-event/focus   #(exec/set-context! {:block/code true
                                                                    :block-view this})
                        :capture-event/blur    #(exec/set-context! {:block/code nil
                                                                    :block-view nil})})]

   [:.w-50.flex-none.code.overflow-y-hidden.overflow-x-auto
    (some-> (first (Block/eval-log block))
            (assoc :block-id (:id block))
            (repl-values/display-result))]])

(defn vec-take [coll n]
  (cond-> coll (> (count coll) n)
          (subvec (- (count coll) n) (count coll))))

(def -dispose-callbacks (volatile! {}))

(extend-type Block/CodeBlock

  Block/ICursor
  (get-cursor [this]
    (when-let [cm (Block/editor this)]
      (when-not (.somethingSelected cm)
        (cm/get-cursor cm))))
  (get-history-selections [this]
    (let [editor (Block/editor this)]
      (if-let [root-cursor (cm/cursor-root editor)]
        #js [#js {:anchor root-cursor
                  :head   root-cursor}]
        (.listSelections editor))))
  (put-selections! [this selections]
    (v/flush!)
    (some-> (Block/editor this)
            (.setSelections selections)))

  (cursor-coords [this]
    (.cursorCoords (Block/editor this)))

  (start [this] (cm/Pos 0 0))
  (end [this] (let [cm (Block/editor this)]
                (cm/Pos (.lastLine cm) (count (.getLine cm (.lastLine cm))))))

  (selection-expand [this]
    (edit/expand-selection (Block/editor this)))

  (selection-contract [this]
    (edit/shrink-selection (Block/editor this)))

  Block/IAppend
  (append? [this other-block] false)

  Block/IBlock
  (state [this] (:node this))
  (kind [this] :code)
  (empty? [this]
    (let [{:keys [tag] :as node} (:node this)]
      (= 0 (count (filter (complement tree/whitespace?) (if (= :base tag)
                                                          (:value node)
                                                          [node]))))))

  (emit [this] (tree/string (:node this)))

  (render [this props]
    (code-view (assoc props
                 :block this
                 :id (:id this))))

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
     (let [editor (Block/editor this)
           source (or (cm/selection-text editor)
                      (->> editor
                           :magic/cursor
                           :bracket-node
                           (tree/string (:ns @e/c-env)))
                      (Block/emit this))]
       (Block/eval! this :string source)))
    ([this kind value]
     (eval-context/dispose! this)
     (binding [cell/*eval-context* this]
       (Block/eval-log! this ((case kind :form e/eval-form
                                         :string e/eval-str) value))
       (Block/update-view this)))))