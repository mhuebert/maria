(ns magic-tree-editor.util
  (:require [cljsjs.codemirror :as CM]
            [goog.dom.Range :as Range]
            [goog.dom :as dom]
            [clojure.string :as string]))

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
  [(CM/Pos line column)
   (CM/Pos end-line end-column)])

(defn mark-ranges!
  "Add marks to a collection of Clojure-style ranges"
  [cm ranges payload]
  (doall (for [[from to] (map parse-range ranges)]
           (.markText cm from to payload))))

(defn define-extension [k f]
  (.defineExtension js/CodeMirror k (fn [& args] (this-as this (apply f (cons this args))))))


(def paste-element
  (memoize (fn []
             (let [textarea (doto (dom/createElement "div")
                              (dom/setProperties #js {:id              "magic-tree.pasteHelper"
                                                      :contentEditable true
                                                      :className       "fixed pre o-0 z-0 bottom-0 right-0"}))]
               (dom/appendChild js/document.body textarea)
               textarea))))

(defn copy
  "Copy text to clipboard using a hidden input element."
  [text]
  (let [hadFocus (.-activeElement js/document)
        text (string/replace text #"[\n\r]" "<br/>")
        _ (aset (paste-element) "innerHTML" text)]
    (doto (Range/createFromNodeContents (paste-element))
      (.select))
    (try (.execCommand js/document "copy")
         (catch js/Error e (.error js/console "Copy command didn't work. Maybe a browser incompatibility?")))
    (.focus hadFocus)))