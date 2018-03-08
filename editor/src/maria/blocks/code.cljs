(ns maria.blocks.code
  (:require [re-view.core :as v :refer [defview]]

            [lark.editors.codemirror :as cm]
            [lark.editor :as Editor]
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

(defn get-share-url [doc block block-list]
  (when-not (doc/unsaved-changes? doc)
    (let [the-node (cond-> (:node block)
                           (= :base (:tag (:node block)))
                           (-> :children first))
          the-string (tree/string the-node)
          ast (->> (Block/emit-list (.getBlocks block-list))
                   (tree/ast)
                   (:children)
                   (remove #(or (tree/whitespace? %)
                                (tree/comment? %))))
          index (->> ast
                     (take-while (fn [{:keys [tag] :as node}]
                                   (or (not= tag (:tag the-node))
                                       (not= the-string (tree/string node)))))
                     (count))
          {:keys [id version]} (get-in doc [:project :persisted])]
      (str "http://share.maria.cloud/gist/" id "/" version "/" index))))

(defview ShareLink
  [{:keys [view/state doc block block-list]}]
  (let [{:keys [hovered]} @state]
    (when (= :gist (get-in doc [:project :persisted :persistence/provider]))
      (let [unsaved-changes (and hovered (doc/unsaved-changes? doc))
            url (when (and hovered (not unsaved-changes))
                  (get-share-url doc block block-list))]
        [(if url :a :div)
         {:classes ["share absolute top-0 right-0 bg-darken pa1 hover-bg-darken-more f7 z-5 black no-underline"
                    (if unsaved-changes "o-50" "pointer")]
          :href url
          :target (when url "_blank")
          :on-mouse-enter #(swap! state assoc :hovered true)
          :on-mouse-leave #(swap! state dissoc :hovered)}
         (if unsaved-changes
           "please save first!"
           "share")]))))

(defview CodeRow
  {:key :id
   :view/should-update #(not= (:block %) (:block (:view/prev-props %)))
   :view/did-mount Editor/mount
   :view/will-unmount Editor/unmount
   :get-editor #(.getEditor (:editor-view @(:view/state %)))}
  [{:keys [view/state block-list block before-change on-selection-activity] :as this}]
  (let [doc (:current-doc @exec/context)]
    [:.flex.pv2.cursor-text
     {:on-click #(when (= (.-target %) (.-currentTarget %))
                   (Editor/focus! (.getEditor this)))}
     [:.w-50.flex-none
      (error/error-boundary
       {:on-error (fn [{:keys [error info]}]
                    (e/handle-block-error (:id block) error))}
       (code/CodeView {:class "pa3 bg-white"
                       :ref #(v/swap-silently! state assoc :editor-view %)
                       :value (str (:block this))
                       :on-ast (fn [node]
                                 (.splice block-list block [(assoc block :node node)]))
                       :before-change before-change
                       :on-selection-activity on-selection-activity
                       :capture-event/focus #(exec/set-context! {:block/code true
                                                                 :block-view this})
                       :capture-event/blur #(exec/set-context! {:block/code nil
                                                                :block-view nil})}))]

     [:.w-50.flex-none.code.overflow-y-hidden.overflow-x-auto.f6.relative.code-block-result
      #_(ShareLink {:doc doc
                    :block block
                    :block-list block-list})
      (some-> (first (Block/eval-log block))
              (assoc :block-id (:id block))
              (value-views/display-result))]]))

(extend-type Block/CodeBlock

  Block/IBlock
  (render [this props] (CodeRow (assoc props :block this
                                             :id (:id this))))
  (kind [this] :code)
  (empty? [this]
    (let [{:keys [tag] :as node} (:node this)]
      (= 0 (count (filter (complement tree/whitespace?)
                          (if (= :base tag)
                            (:children node)
                            [node]))))))

  Object
  (toString [{:keys [node]}]
    (or (get node :source)
        (tree/string node)))

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
    (Block/update-view this)
    value)
  (eval-log [this]
    (get @e/-eval-logs (:id this)))
  (eval!
    ([this]
     (let [editor (Editor/of-block this)
           source (or (cm/selection-text editor)
                      (str this))]
       (when (.somethingSelected editor)
         (let [div (.. editor -display -wrapper)]
           (classes/add div "post-eval")
           (js/setTimeout #(classes/remove div "post-eval") 200)))
       (Block/eval! this :string source))
     true)
    ([this mode form]
     (eval-context/dispose! this)
     (binding [cell/*eval-context* this]
       (Block/eval-log! this ((case mode :form e/eval-form
                                         :string e/eval-str) form)))
     true)))