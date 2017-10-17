(ns maria.blocks.prose
  (:require [re-view.core :as v :refer [defview]]
            [re-view.prosemirror.core :as pm]
            [maria.util :as util]
            [maria.blocks.blocks :as Block]
            [lark.tree.core :as tree]
            [re-view.prosemirror.markdown :as markdown]
            [maria.editors.prose :refer [ProseRow]]
            [lark.editors.editor :as Editor]))

(defn serialize-block [this]
  (.serialize markdown/serializer (Block/state this)))

(extend-type Block/ProseBlock
  IFn
  (-invoke
    ([this props]
     (ProseRow (assoc props
                 :block this
                 :id (:id this)))))

  Block/IBlock
  (kind [this] :prose)

  (empty? [this]
    (and (< (.. (:doc this) -content -size) 10)
         (util/whitespace-string? (serialize-block this))))

  (emit [this]
    (tree/string {:tag   :comment-block
                  :value (serialize-block this)})))

(defn prepend-paragraph [this]
  (when-let [prose-view (Editor/of-block this)]
    (let [state (.-state prose-view)
          dispatch (.-dispatch prose-view)]
      (dispatch (-> (.-tr state)
                    (.insert 0 (.createAndFill (pm/get-node state :paragraph))))))))
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
           #_(assert (= 3 (count (Block/join-blocks blocks))))

           ;;
           ;; Removals

           (test (Block/splice-blocks blocks A [])
                 nil "B\n\nC" "B\n\nC" 3)

           (test (Block/splice-blocks blocks B [])
                 "A\n\nC" [] "A\n\nC" 3)

           (test (Block/splice-blocks blocks C [])
                 "A\n\nB" [] "A\n\nB" 3)

           ;; NOTE
           ;; if 'before' block was merged with 'after' block,
           ;;    'after' is nil.
           (test (Block/splice-blocks blocks Code_ [])
                 "A\n\nB\n\nC\n\nD\n\nE" nil "A\n\nB\n\nC\n\nD\n\nE" 1)

           (test (Block/splice-blocks blocks D [])
                 [] "E" "A\n\nB\n\nC" 3)

           ;;
           ;; Insertions

           (test (Block/splice-blocks blocks A [(Block/create :prose "X")])
                 nil [] "X\n\nB\n\nC" 3)
           ;; replacing 'A' has side-effect of joining with B and C.
           ;; in the real world, prose blocks will always be joined.


           (test (Block/splice-blocks blocks B 1 [])
                 "A" [] "A" 3)

           (test (Block/splice-blocks blocks B -1 [])
                 nil "C" "C" 3)

           (test (Block/splice-blocks blocks B 2 [])
                 "A\n\nD\n\nE" nil "A\n\nD\n\nE" 1)

           (test (Block/splice-blocks blocks A 5 [])
                 nil nil nil 0)

           (test (Block/splice-blocks blocks E -5 [])
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

