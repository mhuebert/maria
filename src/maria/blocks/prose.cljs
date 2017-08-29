(ns maria.blocks.prose
  (:require [re-view.core :as v :refer [defview]]
            [re-view-prosemirror.core :as pm]
            [re-view-prosemirror.commands :refer [apply-command]]
            [maria.commands.prose :as prose-commands]
            [maria-commands.exec :as exec]
            [re-view-prosemirror.commands :as commands]
            [maria.util :as util]
            [maria.blocks.blocks :as Block]
            [magic-tree.core :as tree]
            [goog.dom.classes :as classes]
            [re-view-routing.core :as r]
            [maria.views.icons :as icons]
            [re-db.d :as d]
            [re-view-prosemirror.markdown :as markdown]))

(defview link-dropdown [{:keys [href editor close!]}]
  [:.flex.items-center
   [:.dib.pointer
    {:on-click (fn [e]
                 (.preventDefault e)
                 (.stopPropagation e)
                 (commands/apply-command editor (partial commands/open-link true))
                 (close!))}
    (-> icons/ModeEdit
        (icons/size 16)
        (icons/class "mr1 o-50"))]
   [:a.pm-link {:href href} href]])

(defn make-editor-state [doc input-rules]
  (.create pm/EditorState
           #js {"doc"     doc
                "schema"  markdown/schema
                "plugins" (to-array [(.history pm/history)
                                     #_(pm/keymap pm/keymap-base)
                                     (.inputRules pm/pm
                                                  #js {:rules (->> (.-allInputRules pm/pm)
                                                                   (into input-rules)
                                                                   (to-array))})])}))

(defn make-editor-view [{:keys [editor-view-props before-change after-change on-dispatch on-selection-activity view/state] :as component}
                        editor-state]
  (-> (v/dom-node component)
      (pm/EditorView. (->> {:state      editor-state
                            :spellcheck false
                            :attributes {:class "outline-0"}
                            :dispatchTransaction
                                        (fn [tr]
                                          (let [^js/pm.EditorView pm-view (get @state :pm-view)
                                                prev-state (.-state pm-view)]
                                            (when before-change (before-change))
                                            (pm/transact! pm-view tr)
                                            (when-not (nil? on-dispatch)
                                              (on-dispatch pm-view prev-state))
                                            (when (and after-change (not= (.-doc prev-state)
                                                                          (.-doc (.-state pm-view))))
                                              (after-change))
                                            (when (and on-selection-activity
                                                       (not= (.-selection (.-state pm-view))
                                                             (.-selection prev-state)))
                                              (on-selection-activity pm-view (.-selection (.-state pm-view))))))}
                           (merge editor-view-props)
                           (clj->js)))))

(defview Editor
  {:spec/props              {:on-dispatch :Function
                             :input-rules :Object
                             :doc         :Object}
   :view/did-mount          (fn [{:keys [view/state
                                         input-rules
                                         doc] :as this}]
                              (let [editor-view (make-editor-view this (make-editor-state doc input-rules))]
                                (set! (.-reView editor-view) this)
                                (reset! state {:pm-view editor-view})))

   :reset-doc               (fn [{:keys [view/state parse]} new-value]
                              (let [view (:pm-view @state)]
                                (.updateState view
                                              (.create pm/EditorState #js {"doc"     (cond-> new-value
                                                                                             (string? new-value) (parse))
                                                                           "schema"  markdown/schema
                                                                           "plugins" (aget view "state" "plugins")}))))

   :view/will-receive-props (fn [{:keys [doc block view/state]
                                  :as   this}]
                              (when (not= doc (.-doc (.-state (:pm-view @state))))
                                (.resetDoc this doc)))

   :pm-view                 #(:pm-view @(:view/state %))

   :view/will-unmount       (fn [{:keys [view/state]}]
                              (pm/destroy! (:pm-view @state)))}
  [this]
  [:.prosemirror-content
   (-> (v/pass-props this)
       (assoc :dangerouslySetInnerHTML {:__html ""}))])

(defview prose-view
  {:key               :id
   :get-editor        #(.pmView (:prose-editor-view @(:view/state %)))
   :scroll-into-view  #(apply-command (.getEditor %) (fn [state dispatch]
                                                       (dispatch (.scrollIntoView (.-tr state)))))
   :focus             (fn [this position]
                        (v/flush!)
                        (let [pm-view (.getEditor this)
                              state (.-state pm-view)]
                          (when-let [selection (cond (keyword? position)
                                                     (case position :start (.atStart pm/Selection (.-doc state))
                                                                    :end (.atEnd pm/Selection (.-doc state)))

                                                     (object? position)
                                                     (pm/coords-selection pm-view position)
                                                     :else nil)]
                            (.dispatch pm-view (.setSelection (.-tr state) selection)))
                          (.focus pm-view)
                          (js/setTimeout (.-scrollIntoView this) 0)))
   :view/did-mount    #(Block/mount (:block %) %)
   :view/will-unmount (fn [this]
                        (Block/unmount (:block this))
                        ;; prosemirror doesn't fire `blur` command when unmounted
                        (when (= (:block-view @exec/context) this)
                          (exec/set-context! {:block/prose nil
                                              :block-view  nil})))}
  [{:keys [view/state block-list block before-change after-change] :as this}]

  (Editor {:doc           (Block/state block)
           :block         block
           :class         " serif f4 ph3 cb"
           :input-rules   (prose-commands/input-rules this)
           :on-focus      #(exec/set-context! {:block/prose true
                                               :block-view  this})
           :on-blur       #(exec/set-context! {:block/prose nil
                                               :block-view  nil})
           :on-click      (fn [e]
                            (when-let [a (r/closest (.-target e) r/link?)]
                              (when-not (classes/has a "pm-link")
                                (do (.stopPropagation e)
                                    (d/transact! [[:db/add :ui/globals :dropdown {:rect      (.getBoundingClientRect a)
                                                                                  :component link-dropdown
                                                                                  :props     {:href   (.-href a)
                                                                                              :editor (.getEditor this)}}]])))))
           :ref           #(v/swap-silently! state assoc :prose-editor-view %)
           :before-change before-change
           :after-change  after-change
           :on-dispatch   (fn [pm-view prev-state]
                            (when (not= (.-doc (.-state pm-view))
                                        (.-doc prev-state))
                              (.splice block-list block [(assoc block :doc (.-doc (.-state pm-view)))])))}))

(defn serialize-block [this]
  (.serialize markdown/serializer (Block/state this)))

(extend-type Block/ProseBlock

  Block/ICursor
  (get-history-selections [this]
    (.. (Block/editor this)
        -state
        -selection))
  (put-selections! [this selections]
    (some-> (Block/editor this)
            (commands/apply-command
              (fn [state dispatch]
                (dispatch (.setSelection (.-tr state) selections))))))

  (cursor-coords [this]
    (pm/cursor-coords (Block/editor this)))

  (at-start? [this]
    (let [state (.-state (Block/editor this))]
      (= (.-pos (pm/cursor-$pos state))
         (.-pos (pm/start-$pos state)))))


  (at-end? [this]
    (let [state (.-state (Block/editor this))]
      (= (.-pos (pm/cursor-$pos state))
         (.-pos (pm/end-$pos state)))))

  (selection-expand [this]
    (commands/apply-command (Block/editor this) commands/expand-selection))

  (selection-contract [this]
    (commands/apply-command (Block/editor this) commands/contract-selection))


  Block/IAppend
  (append? [this other-block]
    (= :prose (Block/kind other-block)))

  (append [this other-block]
    (when (Block/append? this other-block)
      ;; TODO
      ;; insert (.-doc (:state other-block)) into doc
      (assoc this :doc (.parse markdown/parser
                               (str (serialize-block this)
                                    "\n\n"
                                    (or (util/some-str (serialize-block other-block))
                                        "\n"))))))

  Block/IBlock

  (kind [this] :prose)

  (empty? [this]
    (util/whitespace-string? (serialize-block this)))

  (emit [this]
    (tree/string {:tag   :comment
                  :value (serialize-block this)}))

  (render [this props]
    (prose-view (assoc props
                  :block this
                  :id (:id this))))

  Block/IParagraph
  (prepend-paragraph [this]
    (when-let [prose-view (Block/editor this)]
      (let [state (.-state prose-view)
            dispatch (.-dispatch prose-view)]
        (dispatch (-> (.-tr state)
                      (.insert 0 (.createAndFill (pm/get-node state :paragraph)))))))))

(comment
  (js/setTimeout
    #(do (let [[A B C Code_ D E :as blocks] [(Block/create :prose "A")
                                             (Block/create :prose "B")
                                             (Block/create :prose "C")
                                             (Block/create :code)
                                             (Block/create :prose "D")
                                             (Block/create :prose "E")]
               test (fn [spliced before-value after-value first-value spliced-count]
                      (let [{:keys [before after]} (meta spliced)]
                        (try
                          (assert (= spliced-count (count spliced)))
                          (assert (= before-value (:value (:node before))))
                          (assert (= first-value (:value (:node (first spliced)))))
                          (assert (= after-value (:value (:node after))))
                          (catch js/Error e

                            (println {:before  [(:value (:node before)) :expected before-value]
                                      :after   [(:value (:node after)) :expected after-value]
                                      :spliced spliced})
                            (throw e)))))]
           (assert (= 3 (count (Block/join-blocks blocks))))

           ;;
           ;; Removals

           (test (Block/splice-block blocks A [])
                 nil "B\n\nC" "B\n\nC" 3)

           (test (Block/splice-block blocks B [])
                 "A\n\nC" [] "A\n\nC" 3)

           (test (Block/splice-block blocks C [])
                 "A\n\nB" [] "A\n\nB" 3)

           ;; NOTE
           ;; if 'before' block was merged with 'after' block,
           ;;    'after' is nil.
           (test (Block/splice-block blocks Code_ [])
                 "A\n\nB\n\nC\n\nD\n\nE" nil "A\n\nB\n\nC\n\nD\n\nE" 1)

           (test (Block/splice-block blocks D [])
                 [] "E" "A\n\nB\n\nC" 3)

           ;;
           ;; Insertions

           (test (Block/splice-block blocks A [(Block/create :prose "X")])
                 nil [] "X\n\nB\n\nC" 3)
           ;; replacing 'A' has side-effect of joining with B and C.
           ;; in the real world, prose blocks will always be joined.


           (test (Block/splice-block blocks B 1 [])
                 "A" [] "A" 3)

           (test (Block/splice-block blocks B -1 [])
                 nil "C" "C" 3)

           (test (Block/splice-block blocks B 2 [])
                 "A\n\nD\n\nE" nil "A\n\nD\n\nE" 1)

           (test (Block/splice-block blocks A 5 [])
                 nil nil nil 0)

           (test (Block/splice-block blocks E -5 [])
                 nil nil nil 0))) 0))

;; TODO

;; Commands
;; =====================
;; select word
;; split prose into prose/code/prose
;; split code into code/prose/code
;;
;; Structure
;; =====================
;;
;; give each form a unique id;
;; allows for inserting forms into AST
;; without re-parsing the whole damn thing.
;;
;;
;;
;; Edits inside a form don't propagate upwards until one of:
;; - `eval`
;; - `blur`
;; (warning: sometimes you have 2 top-level forms just because
;;  you are in the middle of editing & want to slurp, etc.)
;;
;; Shortcuts allow quick jumping between code and comment block
;;



;(def md (js/markdownit "default" #js {"html" false}))
;; attempt at rendering pure markdown and only instantiating ProseMirror when the user clicks on the field.
;; does not work due to probable bug in ProseMirror where .focus() sets the cursor position to zero.
#_(defn get-pm [editor-view]
    (:prose-editor-view @(:view/state editor-view)))
#_(defview markdown
    {:view/initial-state {:editing? false
                          :pm-view  nil}}
    [{:keys [view/state view/prev-state]} s]
    (if (:editing? @state)
      (prose/Editor {:value   s
                     :ref     #(let [pm-view (some-> % (get-pm))]
                                 (v/swap-silently! state assoc :pm-view pm-view)

                                 (when (and pm-view (not (:pm-view prev-state)))
                                   (when-let [selection (some->> (:clicked-coords @state)
                                                                 (clj->js)
                                                                 (.posAtCoords pm-view)
                                                                 (.-pos)
                                                                 (.resolve (.. pm-view -state -doc))
                                                                 (.near js/pm.Selection))]
                                     (.dispatch pm-view (.. pm-view -state -tr (setSelection selection))))

                                   (some-> pm-view (.focus))))
                     :on-blur #(do (.log js/console "prose/Editor on-blur")
                                   (swap! state assoc
                                          :editing? false
                                          :clicked-coords nil))})
      [:.cf {:ref                     #(v/swap-silently! state assoc :md-view %)
             :on-click                #(swap! state assoc
                                              :editing? true
                                              :clicked-coords {:left (.-mouseX %)
                                                               :top  (.-mouseY %)})
             :dangerouslySetInnerHTML {:__html (.render md s)}}]))

#_(defn select-near-click [e pm-view]
    (let [{:keys [bottom right left top]} (util/js-lookup (.getBoundingClientRect (.-dom pm-view)))
          {mouseX :clientX
           mouseY :clientY} (util/js-lookup e)
          $pos (some->> (.posAtCoords pm-view #js {:left (cond (> mouseX right) (dec right)
                                                               (< mouseX left) (inc left)
                                                               :else mouseX)
                                                   :top  (cond (> mouseY bottom) (dec bottom)
                                                               (< mouseY top) (inc top)
                                                               :else mouseY)})
                        (.-pos)
                        (.resolve (.. pm-view -state -doc)))]
      (when $pos
        (commands/apply-command pm-view
                                (fn [state dispatch]
                                  (dispatch (.setSelection (.-tr state)
                                                           (.near pm/Selection $pos -1)))
                                  (.focus pm-view))))))

