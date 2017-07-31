(ns re-view-prosemirror.core
  (:require [pack.prosemirror]
            [cljsjs.markdown-it]
            [clojure.set :as set]
            [clojure.string :as string]))

;; javacript interop with the Prosemirror bundle
;; (type hints for externs inference)

(set! *warn-on-infer* true)

(def pm (.-pm js/window))
(def commands (.-commands pm))
(def chain (.-chainCommands commands))
(def history (.. pm -history))

(def ^js/pm.EditorView EditorView (.-EditorView pm))
(def ^js/pm.EditorState EditorState (.-EditorState pm))

(defn ^js/pm.Schema ensure-schema [state-or-schema]
  (cond-> state-or-schema
          (= (aget state-or-schema "constructor") EditorState) (aget "schema")))

(defn ^js/pm.MarkType get-mark [state-or-schema mark-name]
  (aget (ensure-schema state-or-schema) "marks" (name mark-name)))

(defn ^js/pm.NodeType get-node [state-or-schema node-name]
  (aget (ensure-schema state-or-schema) "nodes" (name node-name)))

(defn ^js/pm.Transaction scroll-into-view [^js/pm.Transaction tr]
  (.scrollIntoView tr))

(defn toggle-mark
  ([mark]
   (.toggleMark commands mark))
  ([state mark-name]
   (.toggleMark commands (get-mark state mark-name))))

(def auto-join #(.autoJoin commands % (fn [] true)))
(def wrap-in-list (comp auto-join (.-wrapInList pm)))
(def input-rule-wrap (.-wrappingInputRule pm))
(def input-rule-text (.-textblockTypeInputRule pm))

(def lift (.-lift commands))
(defn lift-list-item [list-item] (.liftListItem pm list-item))
(defn sink-list-item [list-item] (.sinkListItem pm list-item))

#_(defn delete-empty [state dispatch]
  (let [^js/pm.Selection selection (.-selection state)
        ^js/pm.Node node (or (.-node selection)
                             (.-parent (.-$from selection)))]


    (if (and node
             (.-isBlock (.-type node))
             (= 0 (.. node -content -size)))
      (do

        (.log js/console selection (dec (.. selection -$anchor -pos)) (inc (.. selection -$head -pos)) )
        (.log js/console (.-$from selection))
        (.log js/console (.blockRange (.-$from selection) (.-$to selection)) (dec (.. selection -$anchor -pos)) (inc (.. selection -$head -pos)))
        (.log js/console (-> (.-tr state)
                             (.delete (dec (.. selection -$anchor -pos)) (inc (.. selection -$head -pos)))
                             (dispatch)))
        #_(-> (.-tr state)
            (.delete 79 81)
            (dispatch))
        true)
      false)))

(def keymap (.-keymap pm))
(def keymap-base (.-baseKeymap commands))
(defn keymap-markdown [schema]
  (let [cmd-hard-break (chain
                         (.-exitCode commands)
                         (fn [^js/pm.EditorState state dispatch]
                           (dispatch (->> (.create (get-node schema :hard_break))
                                          (.replaceSelectionWith (.-tr state))
                                          (scroll-into-view)))
                           true))
        lift (chain (lift-list-item (get-node schema :list_item)) lift)]
    (.keymap pm (-> (merge {"Mod-z"        (aget history "undo")
                            "Mod-y"        (aget history "redo")
                            "Shift-Mod-z"  (aget history "redo")
                            "Backspace"    (.-undoInputRule pm)
                            "Mod-b"        (toggle-mark (get-mark schema :strong))
                            "Mod-i"        (toggle-mark (get-mark schema :em))
                            "Mod-`"        (toggle-mark (get-mark schema :code))
                            "Shift-Ctrl-8" (wrap-in-list (get-node schema :bullet_list))
                            "Shift-Ctrl-9" (wrap-in-list (get-node schema :ordered_list))
                            "Ctrl->"       (.wrapIn commands (get-node schema :list_item))
                            "Shift-Ctrl-0" (.setBlockType commands (get-node schema :paragraph))
                            "Enter"        (.splitListItem pm (get-node schema :list_item))
                            "Mod-["        lift
                            "Shift-Tab"    lift
                            "Mod-]"        (sink-list-item (get-node schema :list_item))
                            "Tab"          (sink-list-item (get-node schema :list_item))
                            "Mod-Enter"    cmd-hard-break
                            "Shift-Enter"  cmd-hard-break
                            "Ctrl-Enter"   cmd-hard-break}
                           (reduce (fn [m i]
                                     (assoc m (str "Shift-Ctrl-" i) (.setBlockType commands (get-node schema :heading) #js {"level" i}))) {} (range 1 7)))
                    (clj->js)))))
(defn user-keymap [m]
  (.keymap pm (clj->js m)))


(defn input-rules [schema]
  (.inputRules pm
               #js {"rules" (-> #js [(input-rule-wrap #"^>\s" (get-node schema :blockquote))
                                     (input-rule-wrap #"^(\d+)\.\s$"
                                                      (get-node schema :ordered_list)
                                                      (fn [match] #js {"order" (second match)}))
                                     (input-rule-wrap #"^\s*([-+*])\s$" (get-node schema :bullet_list))
                                     (input-rule-text #"^```$" (get-node schema :code_block))
                                     (input-rule-text #"^(#{1,6})\s$" (get-node schema :heading) (fn [match]
                                                                                                      #js {"level" (count (second match))}))]
                                (.concat (.-allInputRules pm)))}))

(defn range-nodes [^js/pm.Node node start end]
  (let [out #js []]
    (.nodesBetween node start end #(.push out %))
    (vec out)))

(defn selection-nodes [^js/pm.Node doc ^js/pm.Selection selection]
  (->> (.-ranges selection)
       (reduce (fn [nodes ^js/pm.NodeRange range]
                 (into nodes (range-nodes doc (.. range -$from -pos) (.. range -$to -pos)))) [])))

(defn mark-name [^js/pm.Mark mark]
  (.. mark -type -name))

(defn has-mark? [^js/pm.EditorState pm-state mark-name]
  (let [^js/MarkType mark (get-mark pm-state mark-name)]
    (if-let [cursor (.. pm-state -selection -$cursor)]
      (.isInSet mark (or (.-storedMarks pm-state) (.marks cursor)))
      (every? true? (map (fn [^js/pm.SelectionRange range]
                           (.rangeHasMark (.-doc pm-state)
                                          (.. range -$from -pos)
                                          (.. range -$to -pos)
                                          mark))
                         (.. pm-state -selection -ranges))))))

(defn state [^js/pm.EditorView pm-view]
  (.-state pm-view))

(defn transact! [^js/pm.EditorView pm-view tr]
  (.updateState pm-view (.apply (.-state pm-view) tr)))

(defn destroy! [^js/pm.EditorView pm-view]
  (.destroy pm-view))

(defn is-list? [^js/pm.Node node]
  (string/ends-with? (aget node "type" "name") "list"))

(defn first-ancestor [^js/pm.ResolvedPos pos pred]
  (loop [^js/pm.Node node (.node pos)
         depth (some-> pos .-depth)]
    (cond (not node) nil
          (pred node) node
          :else (recur (.node pos (dec depth))
                       (dec depth)))))

(defn descends-from? [^js/pm.ResolvedPos $from kind attrs]
  (first-ancestor $from (fn [^js/pm.Node node]
                          (.hasMarkup node kind attrs))))

(defn is-block-type? [^js/pm.EditorState state node-type-name attrs]
  (let [^js/pm.Selection selection (.-selection state)
        ^js/pm.NodeType kind (get-node state node-type-name)]
    (if-let [^js/pm.Node node (.-node selection)]
      (.hasMarkup node kind attrs)
      (let [$from ^js/pm.ResolvedPos (.-$from selection)]
        (and (<= (.-to selection) (.end $from))
             (.hasMarkup (.-parent $from) kind attrs))))))

(defn set-block-type [kind attrs]
  (.setBlockType commands kind attrs))

(defn wrap-in [state type-name]
  (.wrapIn commands (get-node state type-name)))

(defn ^js/pm.NodeType node-type [^js/pm.Node node]
  (aget node "type"))

(defn in-list? [^js/pm.EditorState state ^js/pm.NodeType list-type-name]
  (= list-type-name (some-> (first-ancestor (.. state -selection -$from) is-list?)
                            (aget "type" "name"))))