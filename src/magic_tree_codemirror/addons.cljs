(ns magic-tree-codemirror.addons
  (:require [cljsjs.codemirror]
            [fast-zip.core :as z]
            [goog.events :as events]
            [magic-tree.core :as tree]
            [magic-tree-codemirror.util :as cm]
            [goog.events.KeyCodes :as KeyCodes]
            [maria-commands.registry :as registry]
            [cljs.core.match :refer-macros [match]]
            [re-db.d :as d]
            [magic-tree-codemirror.edit :as edit]))

(specify! (.-prototype js/CodeMirror)
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

(.defineOption js/CodeMirror "cljsState" false
               (fn [cm] (aset cm "cljs$state" (or (aget cm "cljs$state") {}))))



(def M1 (registry/modifier-keycode "M1"))
(def SHIFT (registry/modifier-keycode "SHIFT"))

(defn modifier-down? [k]
  (contains? (d/get :commands :modifiers-down) k))

(defn nearest-bracket-region
  "Current sexp, or nearest sexp to the left, or parent."
  [pos loc]
  (let [bracket-loc (if-not (tree/whitespace? (z/node loc))
                      loc
                      (if (and (= pos (select-keys (z/node loc) [:line :column]))
                               (z/left loc)
                               (not (tree/whitespace? (z/node (z/left loc)))))
                        (z/left loc)
                        loc))
        up-tag (some-> bracket-loc
                       (z/up)
                       (z/node)
                       (:tag))]
    (cond-> bracket-loc
            (#{:quote :deref
               :reader-conditional} up-tag)
            (z/up))))

(defn top-loc [loc]
  (first (filter #(or (= :base (get (z/node %) :tag))
                      (= :base (get (z/node (z/up %)) :tag))) (iterate z/up loc))))

(defn update-bracket-loc! [cm]
  (let [{cursor-loc  :loc
         bracket-loc :bracket-loc} (get cm :magic/cursor)
        bracket-loc (when cursor-loc
                      (case [(modifier-down? M1) (modifier-down? SHIFT)]
                        [true false] bracket-loc
                        [true true] (top-loc bracket-loc)
                        nil))]
    (swap! cm update :magic/cursor merge {:bracket-loc  bracket-loc
                                          :bracket-node (some-> bracket-loc (z/node))})))

(defn clear-selection! [cm]
  (some->> (:cursor/start cm)
           (.setCursor cm))
  (swap! cm dissoc :cursor/start))

(defn select-node! [cm node]
  (when (and (not (tree/whitespace? node))
             (or (not (.somethingSelected cm))
                 (:cursor/start cm)))
    (when-not (:cursor/start cm)
      (swap! cm assoc :cursor/start (.getCursor cm)))
    (edit/select-range cm (tree/boundaries node))))

(defn update-selection! [cm e]
  (let [key-code (KeyCodes/normalizeKeyCode (.-keyCode e))
        evt-type (.-type e)
        m-down? (modifier-down? M1)
        shift-down? (modifier-down? SHIFT)]
    (match [m-down? evt-type key-code]
           [true _ (:or 16 M1)] (let [loc (cond-> (get-in cm [:magic/cursor :bracket-loc])
                                                  shift-down? (top-loc))]
                                  (some->> loc
                                           (z/node)
                                           (select-node! cm)))
           [_ "keyup" M1] (clear-selection! cm)
           [_ _ (_ :guard (complement #{16 M1}))] (swap! cm dissoc :cursor/start)

           :else nil)))

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
               (cm/mark-ranges! cm (tree/node-highlights node) #js {:className "CodeMirror-matchingbracket"}))))))

(defn clear-parse-errors! [cm]
  (doseq [handle (get-in cm [:magic/errors :handles])]
    (.clear handle))
  (swap! cm update :magic/errors dissoc :handles))

(defn highlight-parse-errors! [cm errors]
  (let [error-ranges (map (comp :position second) errors)
        ;; TODO
        ;; derive className from error name, not all errors are unmatched brackets.
        ;; (somehow) add a tooltip or other attribute to the marker (for explanation).
        handles (cm/mark-ranges! cm error-ranges #js {:className "CodeMirror-unmatchedBracket"})]
    (swap! cm assoc-in [:magic/errors :handles] handles)))

(defn update-ast!
  [{:keys [ast] :as cm}]
  (when-let [{:keys [errors] :as next-ast} (try (tree/ast (.getValue cm))
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

(defn update-cursor!
  [{:keys [zipper magic/brackets?] :as cm}]
  (when-not (:cursor/start cm)
    (let [position (cm/cursor-pos cm)]
      (when-let [loc (and zipper
                          (some->> position
                                   (tree/node-at zipper)))]
        (let [bracket-loc (nearest-bracket-region position loc)
              bracket-node (z/node bracket-loc)]
          (when brackets? (match-brackets! cm bracket-node))
          (swap! cm update :magic/cursor merge {:loc          loc
                                                :node         (z/node loc)
                                                :bracket-loc  bracket-loc
                                                :bracket-node bracket-node
                                                :pos          position}))))))

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
                   (.on cm "cursorActivity" update-cursor!))))

(.defineOption js/CodeMirror "magicBrackets" false
               (fn [cm on?]
                 (when on?
                   (require-opts cm ["magicCursor"])

                   (.on cm "keyup" update-selection!)
                   (.on cm "keydown" update-selection!)
                   (events/listen js/window "blur" #(clear-selection! cm))
                   (events/listen js/window "blur" #(clear-brackets! cm))

                   (swap! cm assoc :magic/brackets? true))))