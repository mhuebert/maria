(ns magic-tree-codemirror.addons
  (:require [cljsjs.codemirror]
            [fast-zip.core :as z]
            [goog.events :as events]
            [cljs.pprint :refer [pprint]]
            [magic-tree.core :as tree]
            [magic-tree-codemirror.util :as cm]
            [goog.events.KeyCodes :as KeyCodes]
            [maria.commands.registry :as registry]
            [re-db.d :as d]))

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

(defn clear-highlight! [cm]
  (doseq [handle (get-in cm [:magic/highlight :handles])]
    (.clear handle))
  (swap! cm dissoc :magic/highlight))

(defn highlight-node! [cm node]
  (when (and (not= node (get-in cm [:magic/highlight :node]))
             (not (.somethingSelected cm))
             (tree/sexp? node))
    (clear-highlight! cm)
    (swap! cm assoc :magic/highlight
           {:node    node
            :handles (cm/mark-ranges! cm (tree/node-highlights node) #js {:className "CodeMirror-eval-highlight"})})))

(defn update-bracket-loc! [cm]
  (let [cursor-loc (get-in cm [:magic/cursor :loc])
        bracket-loc (when cursor-loc
                      (case [(modifier-down? M1) (modifier-down? SHIFT)]
                        [true false] (tree/nearest-bracket-region cursor-loc)
                        [true true] (tree/top-loc cursor-loc)
                        nil))]
    (swap! cm update :magic/cursor merge {:bracket-loc bracket-loc
                                          :bracket-node (some-> bracket-loc (z/node))})))

(defn reset-highlight! [cm]
  (some->> (get-in cm [:magic/cursor :bracket-loc])
           (z/node)
           (highlight-node! cm)))

(defn update-highlights! [cm e]
  (let [key-code (KeyCodes/normalizeKeyCode (.-keyCode e))]
    (when (and (contains? #{"keyup" "keydown"} (.-type e))
               (contains? #{16 M1} key-code))
      (update-bracket-loc! cm)
      (if (modifier-down? M1)
        (reset-highlight! cm)
        (clear-highlight! cm)))))

(defn clear-brackets! [cm]
  (doseq [handle (get-in cm [:magic/cursor :handles])]
    (.clear handle))
  (swap! cm update :magic/cursor dissoc :handles))

(defn match-brackets! [cm node]
  (let [prev-node (get-in cm [:magic/cursor :node])]
    (when (not= prev-node node)
      (clear-brackets! cm)
      (when (tree/may-contain-children? node)
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
  [{:keys [ast on-ast-update] :as cm}]
  (when-let [{:keys [errors] :as next-ast} (try (tree/ast (.getValue cm))
                                                (catch js/Error e (.debug js/console e)))]
    (when (not= next-ast ast)
      (let [next-zip (tree/ast-zip next-ast)]
        (clear-parse-errors! cm)
        (when-let [error (first errors)]
          (highlight-parse-errors! cm [error]))
        (when on-ast-update
          (on-ast-update cm next-ast next-zip))
        (if (seq errors)
          (swap! cm dissoc :ast :zipper)
          (swap! cm assoc
                 :ast next-ast
                 :zipper next-zip))))))

(defn update-cursor!
  [{:keys [zipper magic/brackets?] :as cm}]
  (let [position (cm/cursor-pos cm)]
    (when-let [loc (and zipper
                        (some->> position
                                 (tree/node-at zipper)))]
      (let [bracket-loc (tree/nearest-bracket-region loc)
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
                   (.on cm "change" update-ast!))))

(.defineOption js/CodeMirror "magicCursor" false
               (fn [cm on?]
                 (when on?
                   (require-opts cm ["magicTree"])
                   (.on cm "cursorActivity" update-cursor!))))

(.defineOption js/CodeMirror "magicBrackets" false
               (fn [cm on?]
                 (when on?
                   (require-opts cm ["magicCursor"])

                   (cm/define-extension "magicClearHighlight" clear-highlight!)
                   (cm/define-extension "magicUpdateHighlight" update-highlights!)


                   (.on cm "keyup" update-highlights!)
                   (.on cm "keydown" update-highlights!)
                   (events/listen js/window "blur" #(clear-highlight! cm))
                   (events/listen js/window "blur" #(clear-brackets! cm))

                   (swap! cm assoc :magic/brackets? true))))