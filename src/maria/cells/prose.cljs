(ns maria.cells.prose
  (:require [re-view.core :as v :refer [defview]]
            [re-view-prosemirror.markdown :as prose]
            [re-view-prosemirror.core :as pm]
            [re-view-prosemirror.commands :refer [apply-command]]
            [maria.commands.prose :as prose-commands]
            [maria-commands.exec :as exec]))

(defview prose-cell-view
  {:key                :id
   :pm-view            (fn [this] (.pmView (:prose-editor-view @(:view/state this))))
   :get-editor         #(.pmView (:prose-editor-view @(:view/state %)))
   :scroll-into-view   #(apply-command (.getEditor %) (fn [state dispatch]
                                                        (dispatch (.scrollIntoView (.-tr state)))))
   :focus              (fn [{:keys [view/state] :as this} coords]
                         (let [prose-editor-view (.pmView this)
                               state (.-state prose-editor-view)]
                           (when coords
                             (.dispatch prose-editor-view (-> (.-tr state)
                                                              (.setSelection (case coords :start (.atStart pm/Selection (.-doc state))
                                                                                          :end (.atEnd pm/Selection (.-doc state)))))))
                           (.focus prose-editor-view)
                           (js/setTimeout (.-scrollIntoView this) 0)))
   :view/initial-state (fn [this]
                         {:last-update (:value (:cell this))})
   :view/should-update (fn [this]
                         (not= (-> this :cell :value)
                               (-> this :view/state (deref) :last-update)))
   :view/will-unmount  (fn [this]
                         ;; prosemirror doesn't fire `blur` command when unmounted
                         (when (= (:cell-view @exec/context) this)
                           (exec/set-context! {:cell/prose nil
                                               :cell-view  nil})))}
  [{:keys [view/state on-update splice-self! cell id] :as this}]
  (prose/Editor {:value       (:value cell)
                 :class       " serif f4 ph3 w-50 cf"
                 :input-rules (prose-commands/input-rules this)
                 :on-focus    #(do (exec/set-context! {:cell/prose true
                                                       :cell-view  this})
                                   (.scrollIntoView this))
                 :on-blur     #(exec/set-context! {:cell/prose nil
                                                   :cell-view  nil})
                 :ref         #(v/swap-silently! state assoc :prose-editor-view %)
                 :on-dispatch #(let [out (.serialize (:prose-editor-view @state))]
                                 (v/swap-silently! state assoc :last-update out)
                                 (on-update out))}))






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
                                              :clicked-coords {:left (.-clientX %)
                                                               :top  (.-clientY %)})
             :dangerouslySetInnerHTML {:__html (.render md s)}}]))