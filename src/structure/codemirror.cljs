(ns structure.codemirror
  (:require [cljsjs.codemirror :as CM]
            [fast-zip.core :as z]
            [goog.events :as events]
            [magic-tree.core :as tree]
            [goog.events.KeyCodes :as KeyCodes]
            [cljs.core.match :refer-macros [match]]
            [goog.dom :as gdom]
            [goog.object :as gobj]


    ;; for AST:
            [maria.eval :as eval]
    ;; for protocols:
            [maria.editors.editor :as Editor]
    ;; for `flush!`:
            [re-view.core :as v]
    ;; for M1 modifier differentiation
            [commands.registry :as registry :refer-macros [defcommand]]))

(def Pos CM/Pos)
(def changeEnd CM/changeEnd)

(extend-type js/CodeMirror.Pos
  IComparable
  (-compare [x y]
    (CM/cmpPos x y))
  IEquiv
  (-equiv [x y]
    (and y
         (= (.-line x) (.-line y))
         (= (.-ch x) (.-ch y))))
  IPrintWithWriter
  (-pr-writer [pos writer _]
    (-write writer (str "#Pos[" (.-line pos) ", " (.-ch pos) "]")))
  ILookup
  (-lookup
    ([o k] (gobj/get o k))
    ([o k not-found] (gobj/get o k not-found))))

(defn range->Pos
  "Coerces Clojure maps to CodeMirror positions."
  [{:keys [line column]}]
  (CM/Pos line column))

(defn Pos->range [cursor]
  {:line       (.-line cursor)
   :column     (.-ch cursor)
   :end-line   (.-line cursor)
   :end-column (.-ch cursor)})

(defn- cursor-bookmark []
  (gdom/createDom "div" #js {"className" "cursor-marker"}))

(def M1 (registry/modifier-keycode "M1"))

(defn cursor-loc
  "Current sexp, or nearest sexp to the left, or parent."
  [pos loc]
  (let [the-loc (if-not (tree/whitespace? (z/node loc))
                  loc
                  (if (and (= pos (select-keys (z/node loc) [:line :column]))
                           (z/left loc)
                           (not (tree/whitespace? (z/node (z/left loc)))))
                    (z/left loc)
                    loc))
        up-tag (some-> the-loc
                       (z/up)
                       (z/node)
                       (:tag))]
    (cond-> the-loc
            (#{:quote
               :deref
               :reader-conditional} up-tag)
            (z/up))))


(defn set-cursor-root! [cm]
  (swap! cm assoc :cursor-root/marker (.setBookmark cm
                                                    (.getCursor cm)
                                                    #js {:widget (cursor-bookmark)})))

(defn unset-cursor-root! [cm]
  (when-let [marker (:cursor-root/marker cm)]
    (.clear marker)
    (swap! cm dissoc :cursor-root/marker)))

(defn cursor-root [cm]
  (when-let [marker (:cursor-root/marker cm)]
    (.find marker)))

(defn return-cursor-to-root! [cm]
  (when (.somethingSelected cm)
    (some->> (cursor-root cm)
             (.setCursor cm)))
  (unset-cursor-root! cm))

(defn get-cursor [cm]
  (or (cursor-root cm)
      (.getCursor cm)))

(defn selection? [cm]
  (.somethingSelected cm))

(defn selection-text
  "Return selected text, or nil"
  [cm]
  (when (.somethingSelected cm)
    (.getSelection cm)))

(defn set-cursor! [cm pos]
  (unset-cursor-root! cm)
  (let [pos (cond-> pos
                    (map? pos) (range->Pos))]
    (.setCursor cm pos pos))
  cm)

(defn set-preserve-cursor!
  "If value is different from editor's current value, set value, retain cursor position"
  [editor value]
  (when-not (identical? value (.getValue editor))
    (let [cursor-pos (get-cursor editor)]
      (.setValue editor (str value))
      (if (-> editor (aget "state" "focused"))
        (.setCursor editor cursor-pos))))
  editor)

(defn range->positions
  "Given a Clojure-style column and line range, return Codemirror-compatible `from` and `to` positions"
  [{:keys [line column end-line end-column]}]
  [(CM/Pos line column)
   (CM/Pos (or end-line line) (or end-column column))])

(defn mark-ranges!
  "Add marks to a collection of Clojure-style ranges"
  [cm ranges payload]
  (doall (for [[from to] (map range->positions ranges)]
           (.markText cm from to payload))))

(defn range-text [cm range]
  (let [[from to] (range->positions range)]
    (.getRange cm from to)))

(defn select-range
  "Copy a {:line .. :column ..} range from a CodeMirror instance."
  [cm range]
  (let [[from to] (range->positions range)]
    (.setSelection cm from to #js {:scroll false})))

(defn replace-range!
  ([cm s from {:keys [line column]}]
   (replace-range! cm s (merge from {:end-line line :end-column column})))
  ([cm s {:keys [line column end-line end-column]}]
   (.replaceRange cm s
                  (Pos line column)
                  (Pos (or end-line line) (or end-column column)))))

(defn select-node! [cm node]
  (when (and (not (tree/whitespace? node))
             (or (not (.somethingSelected cm))
                 (cursor-root cm)))
    (when-not (cursor-root cm)
      (set-cursor-root! cm))
    (select-range cm (tree/bounds node))))

(defn pos->boundary
  ([pos]
   {:line   (or (.-line pos) 0)
    :column (.-ch pos)})
  ([pos side]
   (case side :left {:line   (or (.-line pos) 0)
                     :column (.-ch pos)}
              :right {:end-line   (or (.-line pos) 0)
                      :end-column (.-ch pos)})))

(defn selection-bounds
  [cm]
  (if (.somethingSelected cm)
    (let [sel (first (.listSelections cm))]
      (pos->boundary (.from sel))
      (merge (pos->boundary (.from sel) :left)
             (pos->boundary (.to sel) :right)))
    (let [cur (get-cursor cm)]
      (pos->boundary cur))))

(defn highlight-range [pos node]
  (if (and (tree/has-edges? node)
           (tree/within? (tree/inner-range node) pos))
    (tree/inner-range node)
    node))

(defn update-selection! [cm e]
  (let [key-code (KeyCodes/normalizeKeyCode (.-keyCode e))
        evt-type (.-type e)
        primary registry/M1
        secondary registry/SHIFT
        primary-down? (registry/M1-down? e)
        secondary-down? (and (= "keydown" evt-type) (= key-code secondary))]
    (cond (and primary-down? (#{secondary primary} key-code))
          (let [pos (Pos->range (get-cursor cm))
                loc (cond-> (get-in cm [:magic/cursor :bracket-loc])
                            secondary-down? (tree/top-loc))]
            (some->> loc
                     (z/node)
                     (highlight-range pos)
                     (select-node! cm)))

          :else #_(and (= evt-type "keyup") (= key-code primary))
          (return-cursor-to-root! cm)

          #_:else #_(when-not (contains? registry/modifiers key-code)
                      (unset-cursor-root! cm)))))

(defn clear-brackets! [cm]
  (doseq [handle (get-in cm [:magic/cursor :handles])]
    (.clear handle))
  (swap! cm update :magic/cursor dissoc :handles))

(defn match-brackets! [cm node]
  (let [prev-node (get-in cm [:magic/cursor :node])]
    (when (not= prev-node node)
      #_(.log js/console "match-brackets!")
      (clear-brackets! cm)
      (when (some-> node (tree/may-contain-children?))
        (swap! cm assoc-in [:magic/cursor :handles]
               (mark-ranges! cm (tree/node-highlights node) #js {:className "CodeMirror-matchingbracket"}))))))

(defn clear-parse-errors! [cm]
  (doseq [handle (get-in cm [:magic/errors :handles])]
    (.clear handle))
  (swap! cm update :magic/errors dissoc :handles))

(defn highlight-parse-errors! [cm errors]
  (let [error-ranges (map (comp :position second) errors)
        ;; TODO
        ;; derive className from error name, not all errors are unmatched brackets.
        ;; (somehow) add a tooltip or other attribute to the marker (for explanation).
        handles (mark-ranges! cm error-ranges #js {:className "CodeMirror-unmatchedBracket"})]
    (swap! cm assoc-in [:magic/errors :handles] handles)))

(defn update-ast!
  [{:keys [ast] :as cm}]
  (when-let [{:keys [errors] :as next-ast} (try (tree/ast (:ns @eval/c-env) (.getValue cm))
                                                (catch js/Error e (.debug js/console e)))]
    #_(.log js/console "update-ast!" next-ast)
    (when (not= next-ast ast)
      (when-let [on-ast (-> cm :view :on-ast)]
        (on-ast next-ast))
      (let [next-zip (tree/ast-zip next-ast)]
        (clear-parse-errors! cm)
        (when-let [error (first errors)]
          (highlight-parse-errors! cm [error]))
        (if (seq errors)
          (swap! cm dissoc :ast :zipper)
          (swap! cm assoc
                 :ast next-ast
                 :zipper next-zip))))))

(defn update-cursor!
  [{:keys                                    [zipper magic/brackets?]
    {prev-pos :pos prev-zipper :prev-zipper} :magic/cursor
    :as                                      cm}]
  (when (or (.hasFocus cm) (nil? prev-zipper))
    (when-let [pos (pos->boundary (get-cursor cm))]
      (when (or (not= pos prev-pos)
                (not= prev-zipper zipper))
        (when-let [loc (some-> zipper (tree/node-at pos))]
          (let [bracket-loc (cursor-loc pos loc)
                bracket-node (z/node bracket-loc)]
            (when brackets? (match-brackets! cm bracket-node))
            (swap! cm update :magic/cursor merge {:loc          loc
                                                  :node         (z/node loc)
                                                  :bracket-loc  bracket-loc
                                                  :bracket-node bracket-node
                                                  :pos          pos
                                                  :prev-zipper  zipper})))))))

(defn require-opts [cm opts]
  (doseq [opt opts] (.setOption cm opt true)))


(specify! (.-prototype js/CodeMirror)

  ILookup
  (-lookup
    ([this k] (get (aget this "cljs$state") k))
    ([this k not-found] (get (aget this "cljs$state") k not-found)))

  IDeref
  (-deref [this] (gobj/get this "cljs$state"))

  IWatchable
  (-add-watch [this key f]
    (swap! this update ::watches assoc key f))
  (-remove-watch [this key]
    (swap! this update ::watches dissoc key))
  (-notify-watches [this oldval newval]
    (doseq [watcher (vals (::watches @this))]
      (watcher this oldval newval)))

  IReset
  (-reset! [this newval]
    (let [old-val @this]
      (gobj/set this "cljs$state" newval)
      (-notify-watches this old-val newval)))

  ISwap
  (-swap!
    ([this f] (-reset! this (f @this)))
    ([this f a] (-reset! this (f @this a)))
    ([this f a b] (-reset! this (f @this a b)))
    ([this f a b xs] (-reset! this (apply f (concat (list @this a b) xs)))))

  Editor/IKind
  (kind [this] :code)

  Editor/IHistory

  (get-selections [cm]
    (if-let [root-cursor (cursor-root cm)]
      #js [#js {:anchor root-cursor
                :head   root-cursor}]
      (.listSelections cm)))

  (put-selections! [cm selections]
    (v/flush!)
    (.setSelections cm selections))

  Editor/ICursor

  (-focus! [this coords]
    (let [coords (if (keyword? coords)
                   (case coords :end (Pos (.lineCount this) (count (.getLine this (.lineCount this))))
                                :start (Pos 0 0))
                   coords)]
      (doto this
        (.focus)
        (cond-> coords (.setCursor coords)))
      (Editor/scroll-into-view (Editor/cursor-coords this))))

  (get-cursor [this]
    (when-not (.somethingSelected this)
      (get-cursor this)))

  (cursor-coords [this]
    (let [coords (.cursorCoords this)]
      ;; TODO
      ;; these coords don't seem to be correct when using them
      ;; to scroll the cursor into view.
      #_(.log js/console "cm" #js {:left   (- (.-left coords) (.-scrollX js/window))
                                   :right  (- (.-right coords) (.-scrollX js/window))
                                   :top    (- (.-top coords) (.-scrollY js/window))
                                   :bottom (- (.-bottom coords) (.-scrollY js/window))})
      #js {:left   (- (.-left coords) (.-scrollX js/window))
           :right  (- (.-right coords) (.-scrollX js/window))
           :top    (- (.-top coords) (.-scrollY js/window))
           :bottom (- (.-bottom coords) (.-scrollY js/window))}))

  (start [this] (Pos 0 0))
  (end [this] (Pos (.lastLine this) (count (.getLine this (.lastLine this))))))


(.defineOption js/CodeMirror "magicTree" false
               (fn [cm on?]
                 (when on?
                   (require-opts cm ["cljsState"])
                   (.on cm "change" update-ast!)
                   (update-ast! cm))))

(.defineOption js/CodeMirror "magicCursor" false
               (fn [cm on?]
                 (when on?
                   (require-opts cm ["magicTree"])
                   (.on cm "cursorActivity" update-cursor!)
                   (.on cm "change" update-cursor!)
                   (update-cursor! cm))))

(.defineOption js/CodeMirror "magicBrackets" false
               (fn [cm on?]
                 (when on?
                   (require-opts cm ["magicCursor"])

                   (.on cm "keyup" update-selection!)
                   (.on cm "keydown" update-selection!)
                   (events/listen js/window "blur" #(return-cursor-to-root! cm))
                   (events/listen js/window "blur" #(clear-brackets! cm))

                   (swap! cm assoc :magic/brackets? true))))

(.defineOption js/CodeMirror "cljsState" false
               (fn [cm] (aset cm "cljs$state" (or (aget cm "cljs$state") {::watches {}}))))