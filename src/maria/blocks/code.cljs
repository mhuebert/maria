(ns maria.blocks.code
  (:require [re-view.core :as v :refer [defview]]
            [magic-tree-editor.codemirror :as cm]
            [cells.cell :as cell]
            [maria.blocks.blocks :as Block]
            [magic-tree.core :as tree]
            [maria.eval :as e]
            [cells.eval-context :as eval-context]
            [maria.block-views.code :refer [CodeView]]
            [goog.dom.classes :as classes]
            [maria.block-views.editor :as Editor]))

(defn vec-take [coll n]
  (cond-> coll (> (count coll) n)
          (subvec (- (count coll) n) (count coll))))

(def -dispose-callbacks (volatile! {}))

(extend-type Block/CodeBlock

  IFn
  (-invoke
    ([this props] (CodeView (assoc props :block this
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