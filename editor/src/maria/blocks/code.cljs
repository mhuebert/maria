(ns maria.blocks.code
  (:require [chia.view :as v]
            [chia.reactive :as r]

            [lark.editors.codemirror :as cm]
            [lark.editor :as Editor]
            [lark.commands.exec :as exec]

            [maria.blocks.blocks :as Block]
            [lark.tree.core :as tree]
            [maria.eval :as e]
            [maria.editors.code :as code]
            [goog.dom.classes :as classes]
            [maria.editors.code :as code]
            [maria.views.error :as error]
            [maria.views.values :as value-views]
            [maria.util :as util]
            [maria.frames.frame-communication :as frame]
            [maria.commands.doc :as doc]
            [fast-zip.core :as z]
            [lark.tree.node :as node]
            [lark.tree.emit :as emit]
            [maria.views.icons :as icons]
            [cells.cell :as cell]))



(defn vec-take [coll n]
  (cond-> coll (> (count coll) n)
          (subvec (- (count coll) n) (count coll))))

(def -dispose-callbacks (volatile! {}))

(defn get-share-url [doc block block-list]
  (when-not (doc/unsaved-changes? doc)
    (let [the-node (cond-> (:node block)
                           (= :base (:tag (:node block)))
                           (-> :children first))
          the-string (emit/string the-node)
          ast (->> (Block/emit-list (.getBlocks ^clj block-list))
                   (tree/ast)
                   (:children)
                   (remove #(or (node/whitespace? %)
                                (node/comment? %))))
          index (->> ast
                     (take-while (fn [{:keys [tag] :as node}]
                                   (or (not= tag (:tag the-node))
                                       (not= the-string (emit/string node)))))
                     (count))
          {:keys [id version]} (get-in doc [:project :persisted])]
      (str "http://share.maria.cloud/gist/" id "/" version "/" index))))

(v/defclass ShareLink
  [{:keys [view/state doc block block-list]}]
  (let [{:keys [hovered]} @state]
    (when (= :gist (get-in doc [:project :persisted :persistence/provider]))
      (let [unsaved-changes (and hovered (doc/unsaved-changes? doc))
            url (when (and hovered (not unsaved-changes))
                  (get-share-url doc block block-list))]
        [(if url :a :div)
         {:class          (str "share absolute top-0 right-0 bg-darken pa1 hover-bg-darken-more f7 z-5 black no-underline"
                               (if unsaved-changes "o-50" "pointer"))
          :href           url
          :target         (when url "_blank")
          :on-mouse-enter #(swap! state assoc :hovered true)
          :on-mouse-leave #(swap! state dissoc :hovered)}
         (if unsaved-changes
           "please save first!"
           "share")]))))

(v/defclass CodeRow
  {:key :id
   :view/should-update #(not= (:block %) (:block (:view/prev-props %)))
   :view/did-mount Editor/mount
   :view/will-unmount (fn [this]
                        (Editor/unmount this)
                        (when (= (:block-view @exec/context) this)
                          (exec/set-context! {:block/code nil
                                              :block-view nil})))
   :get-editor #(.getEditor (:editor-view @(:view/state %)))}
  [{:keys [view/state block-list block before-change on-selection-activity] :as this}]
  (let [doc (:current-doc @exec/context)]
    [:.flex.pv2.cursor-text.flex-column.flex-row-ns
     {:on-click #(when (= (.-target %) (.-currentTarget %))
                   (Editor/focus! (.getEditor this)))}
     [:.w-100.w-50-ns.flex-none.relative.cm-s-maria-light
      [:.absolute.bottom-0.right-0.pa2.z-3.dib.dn-ns.pointer
       {:on-click #(Block/eval! block)}
       (-> icons/Play
           (icons/size 18)
           (icons/class "cm-variable"))]
      (error/error-boundary
       {:on-error (fn [error info]
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

     [:.w-100.w-50-ns.flex-none.code.overflow-y-hidden.overflow-x-auto.f6.relative.code-block-result.pt3.pt0-ns
      #_(ShareLink {:doc        doc
                    :block      block
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
    (let [node (:node this)
          children (if (= :base (:tag node))
                     (:children node)
                     [node])]
      (= 0 (count (remove node/whitespace? children)))))

  Object
  (toString [{:keys [node]}]
    (or (get node :source)
        (emit/string node)))

  Block/IDispose
  (on-dispose [this key f]
    (vswap! -dispose-callbacks update (:id this) assoc key f))
  (-dispose! [this]
    (doseq [f (vals (get @-dispose-callbacks (:id this)))]
      (f))
    (vswap! -dispose-callbacks dissoc (:id this)))

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
     (Block/dispose! this)
     (binding [cell/*error-handler* (partial e/handle-block-error (:id this))]
       (Block/eval-log! this ((case mode :form e/eval-form
                                         :string e/eval-str) form)))
     true)))