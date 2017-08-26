(ns magic-tree-codemirror.util)

(defn selection-text
  "Return selected text, or nil"
  [cm]
  (when (.somethingSelected cm)
    (.getSelection cm)))

(defn set-preserve-cursor
  "If value is different from editor's current value, set value, retain cursor position"
  [editor value]
  (when-not (identical? value (.getValue editor))
    (let [cursor-pos (.getCursor editor)]
      (.setValue editor (str value))
      (if (-> editor (aget "state" "focused"))
        (.setCursor editor cursor-pos)))))

(defn cursor-pos
  "Return map with :line and :column of cursor"
  [editor-or-position]
  (let [cm-pos (if (instance? js/CodeMirror editor-or-position)
                 (.getCursor editor-or-position)
                 editor-or-position)]
    {:line   (.-line cm-pos)
     :column (.-ch cm-pos)}))

(defn mouse-pos
  "Given mouse event, return position of character under cursor"
  [editor e]
  (let [cm-pos (.coordsChar editor #js {:left (.-clientX e)
                                        :top  (.-clientY e)})]
    {:line   (.-line cm-pos)
     :column (.-ch cm-pos)}))

(defn parse-range
  "Given a Clojure-style column and line range, return Codemirror-compatible `from` and `to` positions"
  [{:keys [line column end-line end-column]}]
  [#js {:line line :ch column}
   #js {:line end-line :ch end-column}])

(defn mark-ranges!
  "Add marks to a collection of Clojure-style ranges"
  [cm ranges payload]
  (doall (for [[from to] (map parse-range ranges)]
           (.markText cm from to payload))))

(defn define-extension [k f]
  (.defineExtension js/CodeMirror k (fn [& args] (this-as this (apply f (cons this args))))))