(ns maria.blocks.code
  (:require [re-view.core :as v :refer [defview]]
            [maria-commands.exec :as exec]
            [maria.views.values :as repl-values]
            [maria.views.codemirror :as codemirror]
            [re-db.d :as d]
            [cells.cell :as cell]
            [maria.blocks.blocks :as Block]
            [maria.util :as util]
            [magic-tree.core :as tree]
            [magic-tree-codemirror.edit :as edit]
            [maria.eval :as e]
            [magic-tree-codemirror.util :as cm]
            [cells.eval-context :as eval-context]))

(defview code-view
  {:key                :id
   :view/should-update #(do false)
   :view/did-mount     #(Block/mount (:block %) %)
   :view/will-unmount  #(Block/unmount (:block %))
   :get-editor         #(.getEditor (:editor-view @(:view/state %)))
   :scroll-into-view   #(util/scroll-to-cursor (.getEditor %))
   :focus              (fn [this coords]
                         (.focus (:editor-view @(:view/state this)) coords))}
  [{:keys [view/state block-list block] :as this}]
  [:.flex.pv3.cursor-text
   {:on-click #(when (= (.-target %) (.-currentTarget %))
                 (.focus this))
    #_:on-focus #_(.scrollIntoView this)}
   [:.w-50.flex-none
    (codemirror/editor {:class               "pa3 bg-white"
                        :ref                 #(v/swap-silently! state assoc :editor-view %)
                        :default-value       (Block/emit (:block this))
                        :on-ast              (fn [node]
                                               (when (not= (tree/string node)
                                                           (tree/string (:node (:block this))))
                                                 (.splice block-list block [(assoc block :node node)])))
                        :capture-event/focus #(exec/set-context! {:block/code true
                                                                  :block-view this})
                        :capture-event/blur  #(exec/set-context! {:block/code nil
                                                                  :block-view nil})})]

   [:.w-50.flex-none.code.overflow-hidden (some-> (first (Block/eval-log block))
                                                  (assoc :block-id (:id block))
                                                  (repl-values/display-result))]])

(defn vec-take [coll n]
  (cond-> coll (> (count coll) n)
          (subvec (- (count coll) n) (count coll))))

(def -dispose-callbacks (volatile! {}))

(extend-type Block/CodeBlock

  Block/ICursor
  (cursor-edge [this]
    (let [editor (Block/editor this)
          last-line (.lastLine editor)
          {cursor-line :line
           cursor-ch   :ch} (util/js-lookup (.getCursor editor))]
      (cond (Block/empty? this) :empty
            (and (= cursor-line last-line)
                 (= cursor-ch (count (.getLine editor last-line))))
            :end
            (and (= cursor-line 0)
                 (= cursor-ch 0))
            :start
            :else nil)))

  (cursor-coords [this]
    (.cursorCoords (Block/editor this)))

  (at-end? [this]
    (let [cm (Block/editor this)
          cursor (.getCursor cm)]
      (= [(.lastLine cm)
          (count (.getLine cm (.lastLine cm)))]
         [(.-line cursor) (.-ch cursor)])))

  (at-start? [this]
    (let [cursor (.getCursor (Block/editor this))]
      (= [0 0] [(.-line cursor) (.-ch cursor)])))

  (selection-expand [this]
    (edit/expand-selection (Block/editor this)))

  (selection-contract [this]
    (edit/shrink-selection (Block/editor this)))

  Block/IJoin
  (join-forward? [this other-block] false)

  Block/IBlock
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
    (d/transact! [[:db/update-attr (:id this) :eval-log #(take 2 (cons value %))]])
    value)
  (eval-log [this]
    (d/get (:id this) :eval-log))
  (eval
    ([this]
     (let [editor (Block/editor this)
           source (or (cm/selection-text editor)
                      (->> editor
                           :magic/cursor
                           :bracket-loc
                           (tree/string (:ns @e/c-env)))
                      (Block/emit this))]
       (Block/eval this :string source)))
    ([this kind value]
     (eval-context/dispose! this)
     (binding [cell/*eval-context* this]
       (Block/eval-log! this ((case kind :form e/eval-form
                                         :string e/eval-str) value))))))