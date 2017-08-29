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

(defn mouse-pos
  "Given mouse event, return position of character under cursor"
  [editor e]
  (let [cm-pos (.coordsChar editor #js {:left (.-clientX e)
                                        :top  (.-clientY e)})]
    {:line   (.-line cm-pos)
     :column (.-ch cm-pos)}))

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