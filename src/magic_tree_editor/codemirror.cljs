(ns magic-tree-editor.codemirror
  (:require [cljsjs.codemirror :as CM]
            [fast-zip.core :as z]
            [goog.events :as events]
            [magic-tree.core :as tree]
            [magic-tree-editor.util :as cm]
            [goog.events.KeyCodes :as KeyCodes]
            [maria-commands.registry :as registry]
            [cljs.core.match :refer-macros [match]]
            [re-db.d :as d]
            [goog.dom :as gdom]
            [maria.eval :as e]
            [magic-tree-editor.util :as cm-util]))

(def Pos CM/Pos)
(def changeEnd CM/changeEnd)

(defn cm-pos
  "Return a CodeMirror position."
  [pos]
  (if (map? pos)
    (CM/Pos (:line pos) (:column pos))
    pos))

(defn cursor-bookmark []
  (gdom/createDom "div" #js {"className" "cursor-marker"}))

(extend-type js/CodeMirror
  ILookup
  (-lookup
    ([this k] (get (aget this "cljs$state") k))
    ([this k not-found] (get (aget this "cljs$state") k not-found)))
  ISwap
  (-swap!
    ([this f] (aset this "cljs$state" (f (aget this "cljs$state"))))
    ([this f a] (aset this "cljs$state" (f (aget this "cljs$state") a)))
    ([this f a b] (aset this "cljs$state" (f (aget this "cljs$state") a b)))
    ([this f a b xs]
     (aset this "cljs$state" (apply f (concat (list (aget this "cljs$state") a b) xs))))))

(extend-type js/CodeMirror.Pos
  IComparable
  (-compare [x y]
    (CM/cmpPos x y))
  IPrintWithWriter
  (-pr-writer [pos writer _]
    (-write writer (str "#Pos[" (.-line pos) ", " (.-ch pos) "]"))))

(.defineOption js/CodeMirror "cljsState" false
               (fn [cm] (aset cm "cljs$state" (or (aget cm "cljs$state") {}))))

(def M1 (registry/modifier-keycode "M1"))
(def SHIFT (registry/modifier-keycode "SHIFT"))

(defn modifier-down? [k]
  (contains? (d/get :commands :modifiers-down) k))

(defn cursor-loc
  "Current sexp, or nearest sexp to the left, or parent."
  [pos loc]
  (let [cursor-loc (if-not (tree/whitespace? (z/node loc))
                     loc
                     (if (and (= pos (select-keys (z/node loc) [:line :column]))
                              (z/left loc)
                              (not (tree/whitespace? (z/node (z/left loc)))))
                       (z/left loc)
                       loc))
        up-tag (some-> cursor-loc
                       (z/up)
                       (z/node)
                       (:tag))]
    (cond-> cursor-loc
            (#{:quote :deref
               :reader-conditional} up-tag)
            (z/up))))

(defn set-cursor-root! [cm]
  (let [cursor (.getCursor cm)]
    (swap! cm assoc
           :cursor/cursor-root cursor
           :cursor/clear-marker (let [marker (.setBookmark cm cursor
                                                           #js {:widget (cursor-bookmark)})]
                                  (fn []
                                    (.clear marker)
                                    (swap! cm dissoc :cursor/cursor-root :cursor/clear-marker))))))

(defn unset-cursor-root! [cm]
  (when-let [clear-marker (:cursor/clear-marker cm)]
    (clear-marker)))

(defn return-cursor-to-root! [cm]
  (when (.somethingSelected cm)
    (some->> (:cursor/cursor-root cm)
             (.setCursor cm)))
  (unset-cursor-root! cm))

(defn get-cursor [cm]
  (or (:cursor/cursor-root cm)
      (.getCursor cm)))

(defn selection? [cm]
  (.somethingSelected cm))

(defn set-cursor! [cm pos]
  (unset-cursor-root! cm)
  (let [pos (cm-pos pos)]
    (.setCursor cm pos pos)))

(defn set-preserve-cursor!
  "If value is different from editor's current value, set value, retain cursor position"
  [editor value]
  (when-not (identical? value (.getValue editor))
    (let [cursor-pos (get-cursor editor)]
      (.setValue editor (str value))
      (if (-> editor (aget "state" "focused"))
        (.setCursor editor cursor-pos)))))

(defn parse-range
  "Given a Clojure-style column and line range, return Codemirror-compatible `from` and `to` positions"
  [{:keys [line column end-line end-column]}]
  [(CM/Pos line column)
   (CM/Pos end-line end-column)])

(defn mark-ranges!
  "Add marks to a collection of Clojure-style ranges"
  [cm ranges payload]
  (doall (for [[from to] (map parse-range ranges)]
           (.markText cm from to payload))))

(defn get-range [cm range]
  (let [[from to] (parse-range range)]
    (.getRange cm from to)))

(defn select-range
  "Copy a {:line .. :column ..} range from a CodeMirror instance."
  [cm range]
  (let [[from to] (parse-range range)]
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
                 (:cursor/cursor-root cm)))
    (when-not (:cursor/cursor-root cm)
      (set-cursor-root! cm))
    (select-range cm (tree/bounds node))))

(defn selection-bounds
  [cm]
  (if (.somethingSelected cm)
    (let [sel (first (.listSelections cm))
          from (.from sel)
          to (.to sel)]
      {:line       (.-line from)
       :column     (.-ch from)
       :end-line   (.-line to)
       :end-column (.-ch to)})
    (let [cur (get-cursor cm)]
      {:line       (.-line cur)
       :column     (.-ch cur)
       :end-line   (.-line cur)
       :end-column (.-ch cur)})))

(defn update-selection! [cm e]
  (let [key-code (KeyCodes/normalizeKeyCode (.-keyCode e))
        evt-type (.-type e)
        m-down? (modifier-down? M1)
        shift-down? (modifier-down? SHIFT)]
    (match [m-down? evt-type key-code]
           [true _ (:or 16 91)] (let [loc (cond-> (get-in cm [:magic/cursor :bracket-loc])
                                                  shift-down? (tree/top-loc))]
                                  (some->> loc
                                           (z/node)
                                           (select-node! cm)))
           [_ "keyup" 91] (return-cursor-to-root! cm)
           :else (when-not (contains? #{16 M1} key-code)
                   (unset-cursor-root! cm)))))

(defn clear-brackets! [cm]
  (doseq [handle (get-in cm [:magic/cursor :handles])]
    (.clear handle))
  (swap! cm update :magic/cursor dissoc :handles))

(defn match-brackets! [cm node]
  (let [prev-node (get-in cm [:magic/cursor :node])]
    (when (not= prev-node node)
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
  (when-let [{:keys [errors] :as next-ast} (try (tree/ast (:ns @e/c-env) (.getValue cm))
                                                (catch js/Error e (.debug js/console e)))]
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

(defn cursor-pos
  "Return map with :line and :column of cursor"
  [editor-or-position]
  (let [cm-pos (if (instance? js/CodeMirror editor-or-position)
                 (.getCursor editor-or-position)
                 editor-or-position)]
    {:line   (.-line cm-pos)
     :column (.-ch cm-pos)}))

(defn cursor-activity!
  [{:keys [zipper magic/brackets?] :as cm}]
  (let [position (cursor-pos (or (:cursor/cursor-root cm) cm))]
    (when-let [loc (and zipper
                        (some->> position
                                 (tree/node-at zipper)))]
      (let [bracket-loc (cursor-loc position loc)
            bracket-node (z/node bracket-loc)]
        (when brackets? (match-brackets! cm bracket-node))
        (swap! cm update :magic/cursor merge {:loc          loc
                                              :node         (z/node loc)
                                              :bracket-loc  bracket-loc
                                              :bracket-node bracket-node
                                              :pos          position})))))

(defn require-opts [cm opts]
  (doseq [opt opts] (.setOption cm opt true)))

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
                   (.on cm "cursorActivity" cursor-activity!)
                   (cursor-activity! cm))))

(.defineOption js/CodeMirror "magicBrackets" false
               (fn [cm on?]
                 (when on?
                   (require-opts cm ["magicCursor"])

                   (.on cm "keyup" update-selection!)
                   (.on cm "keydown" update-selection!)
                   (events/listen js/window "blur" #(return-cursor-to-root! cm))
                   (events/listen js/window "blur" #(clear-brackets! cm))

                   (swap! cm assoc :magic/brackets? true))))