(ns maria.blocks.prose
  (:require [re-view.core :as v :refer [defview]]
            [re-view-prosemirror.markdown :as prose]
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
            [cljs.pprint :refer [pprint]]
            [re-view-material.icons :as icons]

            [re-db.d :as d]))

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



(defview prose-view
  {:key                :id
   :get-editor         #(.pmView (:prose-editor-view @(:view/state %)))
   :scroll-into-view   #(apply-command (.getEditor %) (fn [state dispatch]
                                                        (dispatch (.scrollIntoView (.-tr state)))))
   :focus              (fn [this position]
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
   :view/initial-state (fn [this]
                         {:last-update (:block this)})
   :view/should-update (fn [{:keys [view/state view/prev-state block]}]
                         (or (not= (:dropdown @state)
                                   (:dropdown prev-state))
                             (not= block (:last-update @state))))
   :view/did-mount     #(Block/mount (:block %) %)
   :view/will-unmount  (fn [this]
                         (Block/unmount (:block this))
                         ;; prosemirror doesn't fire `blur` command when unmounted
                         (when (= (:block-view @exec/context) this)
                           (exec/set-context! {:block/prose nil
                                               :block-view  nil})))}
  [{:keys [view/state block-list block] :as this}]
  (prose/Editor {:value       (:value (:node block))
                 :class       " serif f4 ph3 cb"
                 :input-rules (prose-commands/input-rules this)
                 :on-focus    #(exec/set-context! {:block/prose true
                                                   :block-view  this})
                 :on-blur     #(exec/set-context! {:block/prose nil
                                                   :block-view  nil})
                 :on-click    (fn [e]
                                (when-let [a (r/closest (.-target e) r/link?)]
                                  (when-not (classes/has a "pm-link")
                                    (do (.stopPropagation e)
                                        (d/transact! [[:db/add :ui/globals :dropdown {:rect      (.getBoundingClientRect a)
                                                                                      :component link-dropdown
                                                                                      :props     {:href   (.-href a)
                                                                                                  :editor (.getEditor this)}}]])))))
                 :ref         #(v/swap-silently! state assoc :prose-editor-view %)
                 :on-dispatch (fn [editor-view pm-view prev-state]
                                (when (not= (.-doc (.-state pm-view))
                                            (.-doc prev-state))
                                  (let [out (.serialize editor-view)
                                        updated-block (assoc-in block [:node :value] out)]
                                    (v/swap-silently! state assoc :last-update updated-block)
                                    (.splice block-list block [updated-block]))))}))



(extend-type Block/ProseBlock

  Block/ICursor

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

  Block/IBlock

  (kind [this] :prose)

  (empty? [this]
    (util/whitespace-string? (:value (:node this))))

  (append? [this other-block]
    (= :prose (Block/kind other-block)))

  (append [this other-block]
    (when (Block/append? this other-block)
      (update-in this [:node :value] str "\n\n" (or (util/some-str (get-in other-block [:node :value])) "\n"))))



  (emit [this]
    (tree/string (:node this)))



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
                      (.insert 0 (.createAndFill (pm/get-node state :paragraph)))
                      (.scrollIntoView))))))
  (trim-paragraph-left [this]
    (when-let [prose-view (:prose-editor-view @(:view/state (Block/get-view this)))]
      (let [new-value (.replace (:value this) #"^[\n\s]*" "")]
        (.resetDoc prose-view new-value)
        (assoc-in this [:node :value] new-value)))))

(comment
  (js/setTimeout
    #(do (let [blocks [(Block/create :prose "A")
                      (Block/create :prose "B")
                      (Block/create :prose "C")
                      (Block/create :code)
                      (Block/create :prose "D")
                      (Block/create :prose "E")]
               A-id (:id (nth blocks 0))
               B-id (:id (nth blocks 1))
               C-id (:id (nth blocks 2))
               code-id (:id (nth blocks 3))
               D-id (:id (nth blocks 4))
               E-id (:id (nth blocks 5))
               test (fn [spliced before-value after-value first-value spliced-count]
                      (let [{:keys [before after]} (meta spliced)]
                        (try
                          (assert (= spliced-count (count spliced)))
                          (assert (= before-value (:value (:node before))))
                          (assert (= first-value (:value (:node (first spliced)))))
                          (assert (= after-value (:value (:node after))))
                          (catch js/Error e

                            (pprint {:before  [(:value (:node before)) :expected before-value]
                                     :after   [(:value (:node after)) :expected after-value]
                                     :spliced spliced})
                            (throw e)))))]
           (assert (= 3 (count (Block/join-blocks blocks))))

           ;;
           ;; Removals

           (test (Block/splice-by-id blocks A-id [])
                 nil "B\n\nC" "B\n\nC" 3)

           (test (Block/splice-by-id blocks B-id [])
                 "A\n\nC" [] "A\n\nC" 3)

           (test (Block/splice-by-id blocks C-id [])
                 "A\n\nB" [] "A\n\nB" 3)

           ;; NOTE
           ;; if 'before' block was merged with 'after' block,
           ;;    'after' is nil.
           (test (Block/splice-by-id blocks code-id [])
                 "A\n\nB\n\nC\n\nD\n\nE" nil "A\n\nB\n\nC\n\nD\n\nE" 1)

           (test (Block/splice-by-id blocks D-id [])
                 [] "E" "A\n\nB\n\nC" 3)

           ;;
           ;; Insertions

           (test (Block/splice-by-id blocks A-id [(Block/create :prose "X")])
                 nil [] "X\n\nB\n\nC" 3)
           ;; replacing 'A' has side-effect of joining with B and C.
           ;; in the real world, prose blocks will always be joined.


           (test (Block/splice-by-id blocks B-id 1 [])
                 "A" [] "A" 3)

           (test (Block/splice-by-id blocks B-id -1 [])
                 nil "C" "C" 3)

           (test (Block/splice-by-id blocks B-id 2 [])
                 "A\n\nD\n\nE" nil "A\n\nD\n\nE" 1)

           (test (Block/splice-by-id blocks A-id 5 [])
                 nil nil nil 0)

           (test (Block/splice-by-id blocks E-id -5 [])
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

