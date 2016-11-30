(ns maria.tree.source-edit
  (:require [maria.tree.core :as tree]
            [maria.codemirror :as cm-util]
            [fast-zip.core :as z]
            [goog.dom :as dom]))

(defonce paste-element (let [textarea (doto (dom/createElement "input")
                                        (dom/setProperties #js {:id        "maria.tree.pasteHelper"
                                                                :className "fixed o-0 z-0 bottom-0 right-0"}))]
                         (dom/appendChild js/document.body textarea)
                         textarea))

(def pass (.-Pass js/CodeMirror))

(defn copy
  "Copy text to clipboard using a hidden input element."
  [text]
  (let [hadFocus (.-activeElement js/document)]
    (doto paste-element
      (.setAttribute "value" text)
      (.select))
    (try (.execCommand js/document "copy")
         (catch js/Error e (.error js/console "Copy command didn't work. Maybe a browser incompatibility?")))
    (.focus hadFocus)))

(defn cm-pos
  "Return a javascript object with `line` and `ch` keys, for CodeMirror. Prefer to pass around
  Clojure {:line .. :column ..} maps until performing an action on the editor."
  [{:keys [line column]}]
  #js {:line line :ch column})

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
    (.setSelection cm from to)))

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

(defn boundaries
  "Returns position map for left or right boundary of the node."
  ([node] (select-keys node [:line :column :end-line :end-column]))
  ([node side]
   (case side :left (select-keys node [:line :column])
              :right {:line   (:end-line node)
                      :column (:end-column node)})))

(defn at-boundary? [node pos]
  (or (= pos (boundaries node :left))
      (= pos (boundaries node :right))))

(defn get-kill-range
  "Returns range beginning at cursor, ending at newline or inner boundary of current node."
  [pos loc]
  (let [locs-to-delete (->> (cons loc (tree/right-locs loc))
                            (take-while (comp not tree/newline? z/node)))]
    (merge (select-keys pos [:line :column])
           (select-keys (some-> (or (last locs-to-delete) loc) z/node) [:end-line :end-column]))))

(defn cursor-boundary-skip
  "Returns function for moving cursor left or right, touching only node boundaries."
  [side]
  (fn [cm {{{:keys [pos loc]} :cursor} :state}]
    (let [next-loc (case side :left z/left :right z/right)]
      (loop [loc loc]
        (cond (not= pos (boundaries (z/node loc) side)) (.setCursor cm (cm-pos (boundaries (z/node loc) side)))
              (next-loc loc) (recur (next-loc loc))
              :else (some->> (z/up loc) recur))))))

(defn pos= [p1 p2]
  (= (boundaries p1)
     (boundaries p2)))

(def commands {:kill
               (fn [cm {{{pos :pos loc :loc} :cursor} :state}]
                 (if (.somethingSelected cm)
                   pass
                   (->> (get-kill-range pos loc)
                        (cut-range cm))))
               :copy-at-point
               (fn [cm {{{:keys [bracket-loc]} :cursor} :state}]
                 (if (.somethingSelected cm)
                   pass
                   (some->> bracket-loc
                            z/node
                            (copy-range cm))))
               :cut-at-point
               (fn [cm {{{:keys [bracket-loc]} :cursor} :state}]
                 (if (.somethingSelected cm)
                   pass
                   (some->> bracket-loc
                            z/node
                            (cut-range cm))))
               :hop-left
               (cursor-boundary-skip :left)
               :hop-right
               (cursor-boundary-skip :right)
               :expand-selection
               (fn [cm {{{:keys [bracket-loc node] cursor-pos :pos} :cursor zipper :zipper} :state :as this}]
                 (let [sel (selection-boundaries cm)
                       loc (tree/node-at zipper sel)
                       node (z/node loc)
                       parent (z/node (z/up loc))
                       select #(when-not (= :base (get % :tag))
                                 (select-range cm %)
                                 (swap! this update-in [:cursor :stack] conj (boundaries %)))]
                   (cond
                     (not (.somethingSelected cm))
                     (do (swap! this assoc-in [:cursor :stack] (list (selection-boundaries cm)))
                         (.clearHighlight this)
                         (select (let [node (if (tree/comment? node)
                                              node
                                              (z/node bracket-loc))]
                                   (if (at-boundary? node cursor-pos)
                                     node
                                     (or (tree/inner-range node) node)))))
                     (= sel (tree/inner-range parent)) (select parent)
                     (pos= sel (boundaries node)) (select (or (tree/inner-range parent) parent))
                     (= sel (tree/inner-range node)) (select node)
                     :else nil)))

               :shrink-selection
               (fn [cm this]
                 (when-let [stack (get-in this [:state :cursor :stack])]
                   (let [stack (cond-> stack
                                       (= (selection-boundaries cm) (first stack)) rest)]
                     (some->> (first stack) (select-range cm))
                     (swap! this update-in [:cursor :stack] rest))))})

(def key-commands
  (reduce-kv (fn [m k command]
               (assoc m k (get commands command #(prn command))))
             {}
             {"Ctrl-K"        :kill                         ;; cut to end of line / node

              "Cmd-X"         :cut-at-point                 ;; cut/copy selection or
              "Cmd-Backspace" :cut-at-point                 ;; nearest node (highlighted).
              "Cmd-C"         :copy-at-point

              "Alt-Left"      :hop-left                     ;; move cursor left/right,
              "Alt-Right"     :hop-right                    ;; touch only node boundaries.

              "Cmd-1"         :expand-selection
              "Cmd-2"         :shrink-selection}))