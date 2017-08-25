(ns magic-tree-codemirror.edit
  (:require [cljsjs.codemirror]
            [goog.dom.Range :as Range]
            [magic-tree.core :as tree]
            [magic-tree-codemirror.util :as cm-util]
            [fast-zip.core :as z]
            [goog.dom :as dom]
            [clojure.string :as string]))

(def paste-element nil)
(aset js/window "onload"
      #(set! paste-element (let [textarea (doto (dom/createElement "div")
                                            (dom/setProperties #js {:id              "magic-tree.pasteHelper"
                                                                    :contentEditable true
                                                                    :className       "fixed pre o-0 z-0 bottom-0 right-0"}))]
                             (dom/appendChild js/document.body textarea)
                             textarea)))

(def pass (.-Pass js/CodeMirror))

(defn copy
  "Copy text to clipboard using a hidden input element."
  [text]
  (let [hadFocus (.-activeElement js/document)
        text (string/replace text #"[\n\r]" "<br/>")
        _ (aset paste-element "innerHTML" text)]
    (doto (Range/createFromNodeContents paste-element)
      (.select))
    (try (.execCommand js/document "copy")
         (catch js/Error e (.error js/console "Copy command didn't work. Maybe a browser incompatibility?")))
    (.focus hadFocus)))

(defn cm-pos
  "Return a javascript object with `line` and `ch` keys, for CodeMirror. Prefer to pass around
  Clojure {:line .. :column ..} maps until performing an action on the editor."
  [{:keys [line column]}]
  #js {:line line :ch column})

(defn replace-range
  ([cm s from {:keys [line column]}]
   (replace-range cm s (merge from {:end-line line :end-column column})))
  ([cm s {:keys [line column end-line end-column]}]
   (.replaceRange cm s
                  #js {:line line :ch column}
                  #js {:line (or end-line line) :ch (or end-column column)})))

(defn cut-range
  "Cut a {:line .. :column ..} range from a CodeMirror instance."
  [cm range]
  (let [[from to] (cm-util/parse-range range)]
    (copy (.getRange cm from to))
    (.replaceRange cm "" from to)))

(defn copy-range
  "Copy a {:line .. :column ..} range from a CodeMirror instance."
  [cm range]
  (let [[from to] (cm-util/parse-range range)]
    (copy (.getRange cm from to))))

(defn select-range
  "Copy a {:line .. :column ..} range from a CodeMirror instance."
  [cm range]
  (let [[from to] (cm-util/parse-range range)]
    (.setSelection cm from to #js {:scroll false})))

(defn selection-boundaries
  [cm]
  (if (.somethingSelected cm)
    (let [sel (first (.listSelections cm))
          from (.from sel)
          to (.to sel)]
      {:line       (.-line from)
       :column     (.-ch from)
       :end-line   (.-line to)
       :end-column (.-ch to)})
    (let [cur (.getCursor cm)]
      {:line       (.-line cur)
       :column     (.-ch cur)
       :end-line   (.-line cur)
       :end-column (.-ch cur)})))

(defn get-kill-range
  "Returns range beginning at cursor, ending at newline or inner boundary of current node."
  [pos loc]
  (let [start-pos (select-keys pos [:line :column])]
    (merge start-pos
           (if (and (= :string (get (z/node loc) :tag))
                    (not= start-pos (tree/boundaries (z/node loc) :left)))
             (-> (select-keys (z/node loc) [:end-line :end-column])
                 (update :end-column dec))
             (let [locs-to-delete (->> (cons loc (tree/right-locs loc))
                                       (take-while (comp not tree/newline? z/node)))]
               (select-keys (some-> (or (last locs-to-delete) loc) z/node) [:end-line :end-column]))))))

(defn cursor-boundary-skip
  "Returns function for moving cursor left or right, touching only node boundaries."
  [side]
  (fn [{{:keys [pos loc]} :magic/cursor :as cm}]
    (let [next-loc (case side :left z/left :right z/right)]
      (loop [loc loc]
        (cond (not= pos (tree/boundaries (z/node loc) side)) (.setCursor cm (cm-pos (tree/boundaries (z/node loc) side)))
              (next-loc loc) (recur (next-loc loc))
              :else (some->> (z/up loc) recur))))))

(defn pos= [p1 p2]
  (= (tree/boundaries p1)
     (tree/boundaries p2)))

(defn move-char [cm pos amount]
  (.findPosH cm pos amount "char" false))

(defn char-at [cm pos]
  (.getRange cm pos (move-char cm pos 1)))

(defn uneval [cm bracket-loc]
  (let [add-uneval (fn [pos] (replace-range cm "#_" (select-keys pos [:line :column])))
        remove-uneval (fn [pos] (replace-range cm "" (assoc (select-keys pos [:line :column]) :end-column (+ 2 (:column pos)))))
        bracket-node (z/node bracket-loc)]
    (if (.somethingSelected cm)
      (let [{:keys [line end-line] :as sel-pos} (selection-boundaries cm)]
        (if (= "#_" (subs (.getSelection cm) 0 2))
          (remove-uneval sel-pos)
          (do (add-uneval sel-pos)
              (select-range cm (cond-> sel-pos
                                       (= line end-line) (update :end-column #(+ % 2)))))))

      (if-let [uneval-node (first (filter #(= :uneval (get % :tag))
                                          (list bracket-node (some-> bracket-loc z/up z/node))))]
        (remove-uneval uneval-node)
        (add-uneval bracket-node)))))


(def kill
  (fn [{{pos :pos loc :loc} :magic/cursor :as cm}]
    (if (.somethingSelected cm)
      pass
      (->> (get-kill-range pos loc)
           (cut-range cm)))))

#_:delete
;;(def arly, awkward attempt at handling brackets
#_(fn [cm]
    (if (.somethingSelected cm)
      pass
      (doseq [selection (reverse (.listSelections cm))]
        (let [pos (move-char cm (.-head selection) -1)
              char (char-at cm pos)]
          (do (when-not (#{\) \] \}} char)
                (.replaceRange cm "" (.-head selection) pos))
              #js {"anchor" (.-head selection)
                   "head"   (.-head selection)})

          ))))

(def copy-form
  (fn [cm] (if (.somethingSelected cm)
             pass
             (copy-range cm (get-in cm [:magic/cursor :bracket-node])))))

(def cut-form
  (fn [cm] (if (.somethingSelected cm)
             pass
             (cut-range cm (get-in cm [:magic/cursor :bracket-node])))))

(def delete-form
  (fn [cm] (if (.somethingSelected cm)
             pass
             (replace-range cm "" (get-in cm [:magic/cursor :bracket-node])))))

(def hop-left
  (cursor-boundary-skip :left))

(def hop-right
  (cursor-boundary-skip :right))

(defn pop-stack! [cm]
  (when-let [stack (get-in cm [:magic/cursor :stack])]
    (let [stack (cond-> stack
                        (or (:base (first stack))
                            (= (selection-boundaries cm) (first stack))) rest)
          item (first stack)]
      (swap! cm update-in [:magic/cursor :stack] rest)
      item)))

(defn push-stack! [cm node]
  (when (tree/empty-range? node)
    (swap! cm update-in [:magic/cursor :stack] empty))
  (when-not (= node (first (get-in cm [:magic/cursor :stack])))
    (swap! cm update-in [:magic/cursor :stack] conj (tree/boundaries node))))

(defn cursor->range [cursor]
  {:line       (.-line cursor)
   :column     (.-ch cursor)
   :end-line   (.-line cursor)
   :end-column (.-ch cursor)})

(defn tracked-select [cm node]
  (when node
    (select-range cm node)
    (push-stack! cm (tree/boundaries node))))

(def expand-selection
  (fn [{{:keys [bracket-node] cursor-pos :pos} :magic/cursor
        zipper                                 :zipper
        :as                                    cm}]
    (let [sel (selection-boundaries cm)
          loc (tree/node-at zipper sel)
          node (z/node loc)
          parent (some-> (z/up loc) z/node)
          select (partial tracked-select cm)]
      (cond
        (or (:cursor/cursor-root cm)
            (not (.somethingSelected cm)))
        (let [cursor-start (:cursor/cursor-root cm)]
          (when-let [clear-marker (:cursor/clear-marker cm)]
            (clear-marker))
          (when cursor-start (push-stack! cm (cursor->range cursor-start)))
          (push-stack! cm (selection-boundaries cm))

          (select (let [node (if (tree/comment? node) node bracket-node)]
                    (or (when (tree/inside? node cursor-pos)
                          (let [inner-range (tree/inner-range node)]
                            (when-not (and (= (:line inner-range) (:end-line inner-range))
                                           (= (:column inner-range) (:end-column inner-range)))
                              inner-range)))
                        node))))
        (= sel (tree/inner-range parent)) (select parent)
        (pos= sel (tree/boundaries node)) (select (or (tree/inner-range parent) parent))
        (or (= sel (tree/inner-range node))
            (tree/inside? node sel)) (select node)
        :else (some-> (z/up loc) z/node (select))))))

(def shrink-selection
  (fn [cm]
    (some->> (pop-stack! cm)
             (select-range cm))))

(defn expand-selection-left [{{:keys [bracket-node] cursor-pos :pos} :magic/cursor
                              zipper                                 :zipper
                              :as                                    cm}]
  (if (and (:cursor/cursor-root cm)
           (not= (tree/boundaries cursor-pos :left)
                 (tree/boundaries bracket-node :left)))
    (do (some-> (:cursor/clear-marker cm) (apply nil))
        (tracked-select cm (merge {:end-line   (:line cursor-pos)
                                   :end-column (:column cursor-pos)}
                                  (tree/boundaries bracket-node :left))))
    (let [selection-bounds (selection-boundaries cm)
          loc (tree/node-at zipper (tree/boundaries selection-bounds :left))]
      (if-let [left-loc (first (filter (comp (complement tree/whitespace?) z/node) (tree/left-locs loc)))]
        (tracked-select cm (merge (select-keys selection-bounds [:end-line :end-column])
                                  (tree/boundaries (z/node left-loc) :left)))
        (tracked-select cm (some-> loc z/up z/node))))))

(defn expand-selection-right [{{:keys [bracket-node] cursor-pos :pos} :magic/cursor
                               zipper                                 :zipper
                               :as                                    cm}]
  (if (and (:cursor/cursor-root cm)
           (not= (tree/boundaries cursor-pos :right)
                 (tree/boundaries bracket-node :right)))
    (do (some-> (:cursor/clear-marker cm) (apply nil))
        (tracked-select cm (merge (tree/boundaries cursor-pos :left)
                                  {:end-line   (:end-line bracket-node)
                                   :end-column (:end-column bracket-node)})))
    (let [selection-bounds (selection-boundaries cm)
          selection-loc (tree/node-at zipper (tree/boundaries selection-bounds :right))]
      (if-let [right-loc (first (filter (comp (complement tree/whitespace?) z/node) (tree/right-locs selection-loc)))]
        (tracked-select cm (merge (select-keys (z/node right-loc) [:end-line :end-column])
                                  (tree/boundaries selection-bounds :left)))
        (expand-selection cm)))))

(def comment-line
  (fn [{zipper :zipper :as cm}]
    (.operation cm
                #(let [{line-n :line column-n :column} (get-in cm [:magic/cursor :pos])
                       [spaces semicolons] (rest (re-find #"^(\s*)(;+)?" (.getLine cm line-n)))
                       [space-n semicolon-n] (map count [spaces semicolons])]
                   (if (> semicolon-n 0)
                     (replace-range cm "" {:line line-n :column space-n :end-column (+ space-n semicolon-n)})
                     (let [{:keys [end-line end-column]} (some-> (tree/node-at zipper {:line line-n :column 0})
                                                                 z/up
                                                                 z/node)]
                       (when (= line-n end-line)
                         (replace-range cm (str "\n" spaces) {:line line-n :column (dec end-column)}))
                       (replace-range cm ";;" {:line line-n :column space-n})))
                   (.setCursor cm #js {:line (inc line-n)
                                       :ch   column-n})))))

(def uneval-form
  (fn [{{:keys [bracket-loc]} :magic/cursor
        zipper                :zipper
        :as                   cm}]
    (uneval cm bracket-loc)))

(def slurp
  (fn [{{:keys [loc pos]} :magic/cursor
        :as               cm}]
    (let [loc (cond-> loc
                      (and (or (not (tree/may-contain-children? (z/node loc)))
                               (not (tree/inside? (z/node loc) pos)))) z/up)
          {:keys [tag] :as node} (z/node loc)]
      (when-not (= :base tag)
        (when-let [next-form (some->> (z/rights loc)
                                      (filter tree/sexp?)
                                      first)]
          (.operation cm #(let [[_ rb] (get tree/edges tag)]
                            (replace-range cm rb (tree/boundaries next-form :right))
                            (replace-range cm "" (-> (tree/boundaries node :right)
                                                     (assoc :end-column (dec (:end-column node))))))))))))

(def select-pipes
  (fn [cm]
    (let [sels (.listSelections cm)]
      (.replaceSelections cm (clj->js (take (count sels) (repeat "|")))))))


