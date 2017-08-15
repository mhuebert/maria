(ns maria.cells.prose
  (:require [re-view.core :as v :refer [defview]]
            [re-view-prosemirror.markdown :as prose]
            [re-view-prosemirror.core :as pm]
            [re-view-prosemirror.commands :refer [apply-command]]
            [maria.commands.prose :as prose-commands]
            [maria-commands.exec :as exec]
            [re-view-prosemirror.commands :as commands]
            [maria.util :as util]
            [maria.cells.core :as Cell]
            [clojure.string :as string]
            [goog.dom.classes :as classes]
            [re-view-routing.core :as r]
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
   :focus              (fn [this coords]
                         (let [pm-view (.getEditor this)
                               state (.-state pm-view)]
                           (when coords
                             (.dispatch pm-view (-> (.-tr state)
                                                    (.setSelection (case coords :start (.atStart pm/Selection (.-doc state))
                                                                                :end (.atEnd pm/Selection (.-doc state)))))))
                           (.focus pm-view)
                           (js/setTimeout (.-scrollIntoView this) 0)))
   :view/initial-state (fn [this]
                         {:last-update (:value (:cell this))})
   :view/should-update (fn [this]
                         (or (not= (:dropdown @(:view/state this))
                                   (:dropdown (:view/prev-state this)))
                             (not= (-> this :cell :value)
                                   (-> this :view/state (deref) :last-update))))
   :view/did-mount     #(Cell/mount (:cell %) %)
   :view/will-unmount  (fn [this]
                         (Cell/unmount (:cell this))
                         ;; prosemirror doesn't fire `blur` command when unmounted
                         (when (= (:cell-view @exec/context) this)
                           (exec/set-context! {:cell/prose nil
                                               :cell-view  nil})))}
  [{:keys [view/state cell-list cell] :as this}]
  (prose/Editor {:value       (:value cell)
                 :class       " serif f4 ph3 cb"
                 :input-rules (prose-commands/input-rules this)
                 :on-focus    #(exec/set-context! {:cell/prose true
                                                   :cell-view  this})
                 :on-blur     #(exec/set-context! {:cell/prose nil
                                                   :cell-view  nil})
                 :on-click    (fn [e]
                                (when-let [a (r/closest (.-target e) r/link?)]
                                  (when-not (classes/has a "pm-link")
                                    (do (.stopPropagation e)
                                        (d/transact! [[:db/add :ui/globals :dropdown {:rect    (.getBoundingClientRect a)
                                                                                      :component link-dropdown
                                                                                      :props   {:href   (.-href a)
                                                                                                :editor (.getEditor this)}}]])))))
                 :ref         #(v/swap-silently! state assoc :prose-editor-view %)
                 :on-dispatch (fn [editor-view pm-view prev-state]
                                (when (not= (.-doc (.-state pm-view))
                                            (.-doc prev-state))
                                  (let [out (.serialize editor-view)]
                                    (v/swap-silently! state assoc :last-update out)
                                    (.splice cell-list cell [(assoc cell :value out)]))))}))



(extend-type Cell/ProseCell
  Cell/ICell

  (empty? [this]
    (util/whitespace-string? (:value this)))

  (at-start? [this]
    (let [state (.-state (Cell/editor this))]
      (= (.-pos (pm/cursor-$pos state))
         (.-pos (pm/start-$pos state)))))

  (at-end? [this]
    (let [state (.-state (Cell/editor this))]
      (= (.-pos (pm/cursor-$pos state))
         (.-pos (pm/end-$pos state)))))

  (emit [this]
    (when-not (empty? this)
      (.replace (->> (string/split (:value this) #"\n")
                     (mapv #(string/replace % #"^ ?" "\n;; "))
                     (clojure.string/join)) #"^\n" "")))

  (selection-expand [this]
    (commands/apply-command (Cell/editor this) commands/expand-selection))

  (selection-contract [this]
    (commands/apply-command (Cell/editor this) commands/contract-selection))

  (render [this props]
    (prose-view (assoc props
                  :cell this
                  :id (:id this))))

  Cell/IText
  (prepend-paragraph [this]
    (when-let [prose-view (Cell/editor this)]
      (let [state (.-state prose-view)
            dispatch (.-dispatch prose-view)]
        (dispatch (-> (.-tr state)
                      (.insert 0 (.createAndFill (pm/get-node state :paragraph)))
                      (.scrollIntoView))))))
  (trim-paragraph-left [this]
    (when-let [prose-view (:prose-editor-view @(:view/state (Cell/get-view this)))]
      (let [new-value (.replace (:value this) #"^[\n\s]*" "")]
        (.resetDoc prose-view new-value)
        (assoc this :value new-value)))))


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

