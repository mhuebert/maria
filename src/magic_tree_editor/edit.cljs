(ns magic-tree-editor.edit
  (:refer-clojure :exclude [char])
  (:require [magic-tree.core :as tree]
            [magic-tree-editor.codemirror :as cm]
            [magic-tree-editor.util :as cm-util]
            [fast-zip.core :as z])
  (:require-macros [magic-tree-editor.edit :refer [operation]]))

(defn pos= [p1 p2]
  (= (tree/bounds p1)
     (tree/bounds p2)))

(def other-bracket {\( \) \[ \] \{ \} \" \"})

(def pass (.-Pass js/CodeMirror))

(defn copy-range
  "Copy a {:line .. :column ..} range from a CodeMirror instance."
  [cm range]
  (cm-util/copy (cm/range-text cm range)))

(defn cut-range
  "Cut a {:line .. :column ..} range from a CodeMirror instance."
  [cm range]
  (cm-util/copy (cm/range-text cm range))
  (cm/replace-range! cm "" range))

(defn get-kill-range
  "Returns range beginning at cursor, ending at newline or inner boundary of current node."
  [pos loc]
  (let [start-pos (select-keys pos [:line :column])]
    (merge start-pos
           (if (and (= :string (get (z/node loc) :tag))
                    (not= start-pos (tree/bounds (z/node loc) :left)))
             (-> (select-keys (z/node loc) [:end-line :end-column])
                 (update :end-column dec))
             (let [locs-to-delete (->> (cons loc (tree/right-locs loc))
                                       (take-while (comp not tree/newline? z/node)))]
               (select-keys (some-> (or (last locs-to-delete) loc) z/node) [:end-line :end-column]))))))

(defn cursor-skip-bounds
  [{{:keys [pos loc]} :magic/cursor :as cm} side]
  (let [next-loc (case side :left z/left :right z/right)]
    (loop [loc loc]
      (cond (not= pos (tree/bounds (z/node loc) side)) (tree/bounds (z/node loc) side)
            (next-loc loc) (recur (next-loc loc))
            :else (some->> (z/up loc) recur)))))

(defn cursor-skip!
  "Returns function for moving cursor left or right, touching only node boundaries."
  [cm side]
  (some->> (cursor-skip-bounds cm side)
           (cm/set-cursor! cm)))

(defn move-char [cm pos amount]
  (.findPosH cm pos amount "char" false))

(defn char-at [cm pos]
  (.getRange cm pos (move-char cm pos 1)))

(defprotocol IPointer
  (get-range [this i])
  (move [this amount])
  (insert! [this s] [this replace-i s])
  (set-editor-cursor! [this])
  (adjust-for-changes! [this changes]))

(def ^:dynamic *changes* nil)

(defn log-editor-changes [cm changes]
  (.apply (.-push *changes*) *changes* changes))

(defn adjust-for-change [pos change]
  (cond (<= (compare pos (.-from change)) 0) pos
        (<= (compare pos (.-to change)) 0) (cm/changeEnd change)
        :else
        (let [line (-> (.-line pos)
                       (+ (-> change .-text .-length))
                       (- (-> (.. change -to -line)
                              (- (.. change -from -line))))
                       (- 1))
              ch (cond-> (.-ch pos)
                         (= (.-line pos) (.. change -to -line)) (+ (-> (.-ch (cm/changeEnd change))
                                                                       (- (.. change -to -ch)))))]
          (cm/Pos line ch))))

(defn adjust-for-changes [pos changes]
  (loop [pos pos
         i 0]
    (if (= i (.-length changes))
      pos
      (recur (adjust-for-change pos (aget changes i))
             (inc i)))))

(defrecord Pointer [editor ^:mutable pos]
  IPointer
  (get-range [this i] (if (neg? i)
                        (.getRange editor (:pos (move this i)) pos)
                        (.getRange editor pos (:pos (move this i)))))
  (move [this amount]
    (assoc this :pos (move-char editor pos amount)))
  (insert! [this text]
    (.replaceRange editor text pos pos)
    this)
  (insert! [this amount text]
    (.replaceRange editor text pos (move-char editor pos amount))
    this)
  (set-editor-cursor! [this]
    (.setCursor editor pos)
    this)
  (adjust-for-changes! [this changes]
    (set! pos (adjust-for-changes pos changes))
    this))

(defn pointer
  ([editor] (pointer editor (cm/get-cursor editor)))
  ([editor pos] (->Pointer editor pos)))

(defn uneval [{:keys [zipper] :as cm}]
  (let [selection-bounds (cm/selection-bounds cm)
        loc (tree/node-at zipper (tree/bounds selection-bounds :left))
        node (z/node loc)
        replace! (fn [original-loc replacement-node]
                   (let [loc (z/replace original-loc replacement-node)
                         string (tree/string (z/node loc))]
                     (cm/replace-range! cm string (z/node original-loc)))
                   true)]
    ;; TODO
    ;; after executing a command, the cursor/bracket locs do not update.
    (when loc
      (let [pointer (pointer cm)
            changes (operation cm
                               (or (and (= :uneval (:tag node))
                                        (replace! loc (first (:value node)))
                                        true)
                                   (and (= :uneval (some-> (z/up loc) (z/node) :tag))
                                        (replace! (z/up loc) (first (:value (z/node (z/up loc))))))
                                   (replace! loc {:tag   :uneval
                                                  :value [node]})))]
        (adjust-for-changes! pointer changes)
        (set-editor-cursor! pointer)))))


(def kill
  (fn [{{pos :pos loc :loc} :magic/cursor :as cm}]
    (if (cm/selection? cm)
      pass
      (->> (get-kill-range pos loc)
           (cut-range cm)))))

(defn unwrap [{{:keys [pos bracket-loc bracket-node]} :magic/cursor :as cm}]
  (when (and bracket-loc (not (cm/selection? cm)))
    (when-let [closest-edges-node (loop [loc (cond-> bracket-loc
                                                     (not (tree/inside? bracket-node pos)) (z/up))]
                                    (cond (not loc) nil
                                          (tree/has-edges? (z/node loc)) (z/node loc)
                                          :else (recur (z/up loc))))]
      (let [pos (cm/get-cursor cm)
            goal (move-char cm pos -1)]
        (operation cm
                   (cm/replace-range! cm (cm/range-text cm (tree/inner-range closest-edges-node)) closest-edges-node)
                   (cm/set-cursor! cm goal)))

      true)))

(defn raise [{{:keys [pos bracket-loc bracket-node]} :magic/cursor :as cm}]
  ;; TODO
  ;; highlight bracket node for raise
  (when (and bracket-loc (z/up bracket-loc))
    (let [up (z/node (z/up bracket-loc))]
      (operation cm
                 (cm/replace-range! cm (tree/string bracket-node) up)
                 (cm/set-cursor! cm (tree/bounds up :left))))))

(def copy-form
  (fn [cm] (if (cm/selection? cm)
             pass
             (copy-range cm (get-in cm [:magic/cursor :bracket-node])))))

(def cut-form
  (fn [cm] (if (cm/selection? cm)
             pass
             (cut-range cm (get-in cm [:magic/cursor :bracket-node])))))

(def delete-form
  (fn [cm] (if (cm/selection? cm)
             pass
             (cm/replace-range! cm "" (get-in cm [:magic/cursor :bracket-node])))))

(def hop-left
  #(cursor-skip! % :left))

(def hop-right
  #(cursor-skip! % :right))

(defn pop-stack! [cm]
  (when-let [stack (get-in cm [:magic/cursor :stack])]
    (let [stack (cond-> stack
                        (or (:base (first stack))
                            (= (cm/selection-bounds cm) (first stack))) rest)
          item (first stack)]
      (swap! cm update-in [:magic/cursor :stack] rest)
      item)))

(defn push-stack! [cm node]
  (when (tree/empty-range? node)
    (swap! cm update-in [:magic/cursor :stack] empty))
  (when-not (= node (first (get-in cm [:magic/cursor :stack])))
    (swap! cm update-in [:magic/cursor :stack] conj (tree/bounds node))))

(defn tracked-select [cm node]
  (when node
    (cm/select-range cm node)
    (push-stack! cm (tree/bounds node))))

(defn push-cursor! [cm]
  (push-stack! cm (cm/cursor->range (:cursor/cursor-root cm)))
  (some-> (:cursor/clear-marker cm)
          (apply nil)))

(def expand-selection
  (fn [{{:keys [bracket-node] cursor-pos :pos} :magic/cursor
        zipper                                 :zipper
        :as                                    cm}]
    (let [sel (cm/selection-bounds cm)
          loc (tree/node-at zipper sel)
          node (z/node loc)
          select! (partial tracked-select cm)
          cursor (:cursor/cursor-root cm)]
      (when cursor (push-cursor! cm))
      (if
        (or cursor (not (cm/selection? cm)))
        (do
          (push-stack! cm (cm/selection-bounds cm))
          (select! (let [node (if (tree/comment? node) node bracket-node)]
                     (or (when (tree/inside? node cursor-pos)
                           (let [inner-range (tree/inner-range node)]
                             (when-not (and (= (:line inner-range) (:end-line inner-range))
                                            (= (:column inner-range) (:end-column inner-range)))
                               inner-range)))
                         node))))

        (loop [loc loc]
          (if-not loc
            sel
            (let [node (z/node loc)
                  inner-range (tree/inner-range node)]
              (cond (pos= sel (tree/inner-range node)) (select! node)
                    (tree/within? inner-range sel) (select! inner-range)
                    (pos= sel node) (recur (z/up loc))
                    (tree/within? node sel) (select! node)
                    :else (recur (z/up loc))))))))))

(def shrink-selection
  (fn [cm]
    (some->> (pop-stack! cm)
             (cm/select-range cm))))

(defn expand-selection-left [{{:keys [bracket-node] pos :pos} :magic/cursor
                              zipper                          :zipper
                              :as                             cm}]
  (let [selection-bounds (cm/selection-bounds cm)
        selection-loc (tree/node-at zipper (tree/bounds selection-bounds :left))
        cursor (:cursor/cursor-root cm)]
    (when cursor (push-cursor! cm))
    (if (and cursor
             (not= (tree/bounds pos :left)
                   (tree/bounds bracket-node :left)))
      (tracked-select cm (merge {:end-line   (:line pos)
                                 :end-column (:column pos)}
                                (tree/bounds bracket-node :left)))
      (if-let [left-loc (first (filter (comp (complement tree/whitespace?) z/node) (tree/left-locs selection-loc)))]
        (tracked-select cm (merge (tree/bounds (z/node left-loc) :left)
                                  (select-keys selection-bounds [:end-line :end-column])))
        (expand-selection cm)))))

(defn expand-selection-right [{{:keys [bracket-node] pos :pos} :magic/cursor
                               zipper                          :zipper
                               :as                             cm}]
  (let [selection-bounds (cm/selection-bounds cm)
        selection-loc (tree/node-at zipper (tree/bounds selection-bounds :right))
        cursor (:cursor/cursor-root cm)]
    (when cursor
      (push-cursor! cm))

    (if (and cursor (not= (tree/bounds pos :right)
                          (tree/bounds bracket-node :right)))
      (tracked-select cm (merge (tree/bounds pos :left)
                                {:end-line   (:end-line bracket-node)
                                 :end-column (:end-column bracket-node)}))
      (if-let [right-loc (first (filter (comp (complement tree/whitespace?) z/node) (tree/right-locs selection-loc)))]
        (tracked-select cm (merge (select-keys (z/node right-loc) [:end-line :end-column])
                                  (tree/bounds selection-bounds :left)))
        (expand-selection cm)))))

(def comment-line
  (fn [{zipper :zipper :as cm}]
    (operation cm
               (let [{line-n :line column-n :column} (get-in cm [:magic/cursor :pos])
                     [spaces semicolons] (rest (re-find #"^(\s*)(;+)?" (.getLine cm line-n)))
                     [space-n semicolon-n] (map count [spaces semicolons])]
                 (if (> semicolon-n 0)
                   (cm/replace-range! cm "" {:line line-n :column space-n :end-column (+ space-n semicolon-n)})
                   (let [{:keys [end-line end-column]} (some-> (tree/node-at zipper {:line line-n :column 0})
                                                               z/up
                                                               z/node)]
                     (when (= line-n end-line)
                       (cm/replace-range! cm (str "\n" spaces) {:line line-n :column (dec end-column)}))
                     (cm/replace-range! cm ";;" {:line line-n :column space-n})))
                 (.setCursor cm (cm/Pos (inc line-n) column-n))))))

(def slurp
  (fn [{{:keys [loc pos]} :magic/cursor
        :as               cm}]
    (let [node (z/node loc)
          loc (cond-> loc
                      (and (not (= :string (:tag node)))
                           (or (not (tree/may-contain-children? node))
                               (not (tree/inside? node pos)))) z/up)
          {:keys [tag] :as node} (z/node loc)]
      (when-not (= :base tag)
        (when-let [next-form (some->> (z/rights loc)
                                      (filter tree/sexp?)
                                      first)]
          (operation cm (let [right-bracket (second (get tree/edges tag))]
                          (cm/replace-range! cm right-bracket (tree/bounds next-form :right))
                          (cm/replace-range! cm "" (-> (tree/bounds node :right)
                                                       (assoc :end-column (dec (:end-column node))))))))))))


(defn cursor-selection-edge [editor side]
  (cm/set-cursor! editor (-> (cm/selection-bounds editor)
                             (tree/bounds side))))

