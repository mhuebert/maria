(ns lark.editors.codemirror
  (:require
   ["codemirror" :as CM]
   [fast-zip.core :as z]
   [goog.events :as events]
   [lark.tree.core :as tree]
   [goog.dom :as gdom]

   ;; for protocols:
   [lark.editor :as Editor]
   [clojure.string :as string]
   [lark.tree.range :as range]
   [lark.tree.node :as node]
   [lark.tree.nav :as nav]
   [applied-science.js-interop :as j]))

(def ^:dynamic *get-ns* (fn [] (symbol "cljs.user")))

(def Pos CM/Pos)
(def changeEnd CM/changeEnd)

(extend-type CM/Pos
  IComparable
  (-compare [x y]
    (CM/cmpPos x y))
  IEquiv
  (-equiv [x y]
    (and y
         (= (.-ch x) (get y :column))
         (= (.-line x) (get y :line))))
  IPrintWithWriter
  (-pr-writer [pos writer _]
    (-write writer (str "#Pos[" (.-line pos) ", " (.-ch pos) "]")))
  IAssociative
  (-assoc [o k v]
    (case k
      :line (CM/Pos v (.-ch o))
      :column (CM/Pos (.-line o) v)))
  ILookup
  (-lookup
    ([o k]
     (case k :line (.-line o)
             :column (.-ch o)
             (j/get o k)))
    ([o k not-found]
     (case k :line (.-line o)
             :column (.-column o)
             (j/get o k not-found)))))

(defn range->Pos
  "Coerces Clojure maps to CodeMirror positions."
  [{:keys [line column]}]
  (CM/Pos line column))

(defn Pos->range [cursor]
  {:line (.-line cursor)
   :column (.-ch cursor)
   :end-line (.-line cursor)
   :end-column (.-ch cursor)})

(defn- cursor-bookmark []
  (gdom/createDom "div" #js {"className" "cursor-marker"}))

(defn sexp-near
  "Current sexp, or nearest sexp to the left, or parent."
  ([pos loc] (sexp-near pos loc nil))
  ([pos loc {:keys [direction ignore?]
             :or {direction :left
                  ignore? node/whitespace?}}]
   (let [nav (case direction :left z/left :right z/right)
         the-loc (if-not (ignore? (z/node loc))
                   loc
                   (if (and (= pos (select-keys (z/node loc) [:line :column]))
                            (nav loc)
                            (not (ignore? (z/node (nav loc)))))
                     (nav loc)
                     loc))]
     (nav/include-prefix-parents the-loc))))


(defn set-temp-marker! [cm]
  (when-not (::temp-marker cm)
    (swap! cm assoc ::temp-marker (if (.somethingSelected cm)
                                    [:selections (.listSelections cm)]
                                    [:cursor (.setBookmark cm
                                                           (.getCursor cm)
                                                           #js {:widget (cursor-bookmark)})]))))

(defn unset-temp-marker! [cm]
  (let [[kind marker] (::temp-marker cm)]
    (when (= kind :cursor)
      (.clear marker)))
  (swap! cm dissoc ::temp-marker))

(defn temp-marker-cursor-pos [cm]
  (let [[kind marker] (::temp-marker cm)]
    (when (= kind :cursor)
      (.find marker))))

(defn temp-marker-selections [cm]
  (let [[kind marker] (::temp-marker cm)]
    (when (= kind :selections)
      marker)))

(defn return-to-temp-marker! [editor]
  (when-let [[kind data] (::temp-marker editor)]
    (case kind :cursor (when-let [pos (.find data)]
                         (.setCursor editor pos nil #js {:scroll false}))
               :selections (when-let [sels data]
                             (.setSelections editor sels nil #js {:scroll false})))
    (unset-temp-marker! editor)))

(defn ^js get-cursor [cm]
  (or (temp-marker-cursor-pos cm)
      (.getCursor cm)))

(defn selection? [cm]
  (.somethingSelected cm))

(defn selection-text
  "Return selected text, or nil"
  [cm]
  (when (.somethingSelected cm)
    (.getSelection cm)))

(defn set-cursor! [cm pos]
  (unset-temp-marker! cm)
  (let [pos (cond-> pos
                    (map? pos) (range->Pos))]
    (.setCursor cm pos nil #js {:scroll false}))
  cm)

(defn set-preserve-cursor!
  "If value is different from editor's current value, set value, retain cursor position"
  [editor value]
  (when-not (identical? value (.getValue editor))
    (let [cursor-pos (get-cursor editor)]
      (.setValue editor (str value))
      (if (-> editor (aget "state" "focused"))
        (.setCursor editor cursor-pos nil #js {:scroll false}))))
  editor)

(defn range->positions
  "Given a Clojure-style column and line range, return Codemirror-compatible `from` and `to` positions"
  [{:keys [line column end-line end-column] :as node}]
  [(CM/Pos line column)
   (CM/Pos (or end-line line) (or end-column column))])

(defn mark-range!
  "Add marks to a collection of Clojure-style ranges"
  [cm range payload]
  (let [[from to] (range->positions range)]
    (.markText cm from to payload)))

(defn mark-ranges!
  "Add marks to a collection of Clojure-style ranges"
  [cm ranges payload]
  (->> (mapv range->positions ranges)
       (reduce (fn [out [from to]]
                 (conj out (.markText cm from to payload))) [])))

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

(defn temp-select-node! [cm node]
  (set-temp-marker! cm)
  (select-range cm (range/bounds node)))

(defn pos->boundary
  ([pos]
   {:line (or (.-line pos) 0)
    :column (.-ch pos)})
  ([pos side]
   (case side :left {:line (or (.-line pos) 0)
                     :column (.-ch pos)}
              :right {:end-line (or (.-line pos) 0)
                      :end-column (.-ch pos)})))

(defn selection-bounds [sel]
  (merge (pos->boundary (.from sel) :left)
         (pos->boundary (.to sel) :right)))

(defn current-selection-bounds
  [cm]
  (if (.somethingSelected cm)
    (let [sel (first (.listSelections cm))]
      (selection-bounds sel))
    (let [cur (get-cursor cm)]
      (pos->boundary cur))))

(defn highlight-range [pos node]
  (if (and (node/has-edges? node)
           (not= :string (:tag node))
           (range/within-inner? node pos)
           (not (nav/prefix-parents (:tag node))))
    (range/inner-range node)
    node))

(defn select-at-cursor [{{:keys [loc pos]} :magic/cursor :as cm} top-loc?]
  (when-let [cursor-loc (sexp-near pos loc {:direction :left})]
    (let [pos (Pos->range (get-cursor cm))
          node (if top-loc? (z/node (nav/top-loc cursor-loc))
                            (->> (nav/include-prefix-parents cursor-loc)
                                 (z/node)
                                 (highlight-range pos)))]
      (when (and node (not (node/whitespace? node)))
        (temp-select-node! cm node)))))

(def mac? (let [platform (.. js/navigator -platform)]
            (or (string/starts-with? platform "Mac")
                (string/starts-with? platform "iP"))))

(defn keyup-selection-update! [cm e]
  (when-not (if mac? (.-metaKey e)
                     (.-ctrlKey e))
    (return-to-temp-marker! cm)))

(defn clear-brackets! [cm]
  (doseq [handle (get-in cm [:magic/cursor :handles])]
    (.clear handle))
  (swap! cm update :magic/cursor dissoc :handles))

(defn match-brackets! [cm node]
  (clear-brackets! cm)
  (when (some-> node (node/may-contain-children?))
    (swap! cm assoc-in [:magic/cursor :handles]
           (mark-ranges! cm (range/node-highlights node) #js {:className "CodeMirror-matchingbracket"}))))

(defn clear-parse-errors! [cm]
  (doseq [handle (get-in cm [:magic/errors :handles])]
    (.clear handle))
  (swap! cm update :magic/errors dissoc :handles))

(defn highlight-parse-errors! [cm error-ranges]
  (clear-parse-errors! cm)
  (let [handles (into [] (for [node error-ranges]
                           (mark-range! cm node #js {:className (str "error-text"
                                                                     (when-let [tag (some-> (get-in node [:info :tag])
                                                                                            (name))]
                                                                       (str " cm-" tag)))})))]
    (swap! cm assoc-in [:magic/errors :handles] handles)))

;; todo
;; cursor tracking w/ AST
#_(defn highlight-cursor! [cm cursor]
    (some-> (get cm :cursor/handle)
            (.clear))
    (swap! cm assoc :cursor/handle
           (.setBookmark cm (range->Pos cursor) #js {:widget (cursor-bookmark)})))

(defn set-value-and-refresh! [editor value]
      (set-preserve-cursor! editor value)
      (.refresh editor))

(defn set-zipper!
  ([editor zipper & [{:keys [decorate?]
                      :or {decorate? true}}]]
   (let [ast (z/node zipper)]
     (swap! editor merge {:zipper zipper
                          :ast ast})
     (when-let [on-ast (get-in editor [:view :on-ast])]
       (on-ast ast))
     (when decorate?
       (highlight-parse-errors! editor (get (z/node zipper) :invalid-nodes))))))

(defn update-ast!
  [{{:as ast
     ast-source :source} :ast :as editor}]
  (let [value (.getValue editor)]
    (when (or (nil? ast-source)
              (not= ast-source value))
      (let [{:keys [invalid-nodes] :as next-ast} (try (tree/ast value)
                                                      (catch js/Error e
                                                        (prn "error in update-ast!" e)
                                                        (js/console.log (.-stack e))
                                                        {:errors []}))]
        (highlight-parse-errors! editor invalid-nodes)
        (when (not= next-ast ast)
          (set-zipper! editor (tree/ast-zip next-ast) {:decorate? false}))))))

(defn update-cursor!
  [{:keys [zipper magic/brackets?]
    {prev-pos :pos prev-zipper :prev-zipper} :magic/cursor
    :as cm} & [force?]]
  (when (or (.hasFocus cm) (nil? prev-zipper) force?)
    (when-let [pos (pos->boundary (get-cursor cm))]
      (when (or (not= pos prev-pos)
                (not= prev-zipper zipper))
        (when-let [loc (some-> zipper (nav/navigate pos))]
          (let [bracket-loc (sexp-near pos loc {:ignore? #(or (node/whitespace? %)
                                                              (get % :invalid?))})
                bracket-node (z/node bracket-loc)]
            (when brackets? (match-brackets! cm bracket-node))
            (swap! cm update :magic/cursor merge {:loc loc
                                                  :node (z/node loc)
                                                  :bracket-loc bracket-loc
                                                  :bracket-node bracket-node
                                                  :pos pos
                                                  :prev-zipper zipper})))))))

(defn require-opts [cm opts]
  (doseq [opt opts] (.setOption cm opt true)))


(specify! (.-prototype CM)

  ILookup
  (-lookup
    ([this k] (get (j/get this :cljs$state) k))
    ([this k not-found] (get (aget this "cljs$state") k not-found)))

  IDeref
  (-deref [this] (j/get this :cljs$state))

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
      (j/assoc! this :cljs$state newval)
      (-notify-watches this old-val newval)))

  ISwap
  (-swap!
    ([this f] (-reset! this (f @this)))
    ([this f a] (-reset! this (f @this a)))
    ([this f a b] (-reset! this (f @this a b)))
    ([this f a b xs] (-reset! this (apply f (concat (list @this a b) xs)))))

  ITransientAssociative
  (-assoc! [this key val]
    (assert (= key :ast))
    (swap! this assoc :ast val)
    (update-ast! this))

  Editor/IKind
  (kind [this] :code)

  Editor/IHistory

  (get-selections [cm]
    (if-let [root-cursor (temp-marker-cursor-pos cm)]
      #js [#js {:anchor root-cursor
                :head root-cursor}]
      (.listSelections cm)))

  (put-selections! [cm selections]
    (.setSelections cm selections))

  Editor/ICursor

  (-focus! [this coords]
    (let [coords (if (keyword? coords)
                   (case coords :end (Pos (.lineCount this) (count (.getLine this (.lineCount this))))
                                :start (Pos 0 0))
                   coords)]
      (doto this
        (.focus)
        (cond-> coords (.setCursor coords nil #js {:scroll false})))
      (Editor/scroll-into-view (Editor/cursor-coords this))))

  (get-cursor [this]
    (when-not (.somethingSelected this)
      (get-cursor this)))
  (set-cursor [this position]
    (.setCursor this position))
  (coords-cursor [this client-x client-y]
    (.coordsChar this #js {:left client-x
                           :top client-y} "window"))
  (cursor-coords [this]
    (let [coords (.cursorCoords this)]
      ;; TODO
      ;; these coords don't seem to be correct when using them
      ;; to scroll the cursor into view.
      #_(.log js/console "cm" #js {:left (- (.-left coords) (.-scrollX js/window))
                                   :right (- (.-right coords) (.-scrollX js/window))
                                   :top (- (.-top coords) (.-scrollY js/window))
                                   :bottom (- (.-bottom coords) (.-scrollY js/window))})
      #js {:left (- (.-left coords) (.-scrollX js/window))
           :right (- (.-right coords) (.-scrollX js/window))
           :top (- (.-top coords) (.-scrollY js/window))
           :bottom (- (.-bottom coords) (.-scrollY js/window))}))

  (start [this] (Pos 0 0))
  (end [this] (Pos (.lastLine this) (count (.getLine this (.lastLine this))))))


(.defineOption CM "magicTree" false
               (fn [cm on?]
                 (when on?
                   (require-opts cm ["cljsState"])
                   (.on cm "change" update-ast!)
                   (update-ast! cm))))

(.defineOption CM "magicCursor" false
               (fn [cm on?]
                 (when on?
                   (require-opts cm ["magicTree"])
                   (.on cm "focus" update-cursor!)
                   (.on cm "cursorActivity" update-cursor!)
                   (.on cm "change" update-cursor!)
                   (update-cursor! cm))))

(.defineOption CM "magicBrackets" false
               (fn [cm on?]
                 (when on?
                   (require-opts cm ["magicCursor"])

                   (.on cm "keyup" keyup-selection-update!)
                   #_(.on cm "keydown" keyup-selection-update!)
                   (events/listen js/window "blur" #(return-to-temp-marker! cm))
                   (events/listen js/window "blur" #(clear-brackets! cm))

                   (swap! cm assoc :magic/brackets? true))))

(.defineOption CM "cljsState" false
               (fn [cm] (aset cm "cljs$state" (or (aget cm "cljs$state") {::watches {}}))))