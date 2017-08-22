(ns paredit.test-utils
  (:require [magic-tree-codemirror.addons]
            [clojure.string :as string]))


(def editor
  (memoize (fn []
             (js/CodeMirror (.appendChild js/document.body (.createElement js/document "div"))
                            (clj->js {:mode          "clojure"
                                      :magicBrackets true})))))

(defn replace-selections
  [cm s]
  (let [sels (.listSelections cm)]
    (.replaceSelections cm (clj->js (take (count sels) (repeat s))))
    cm))

(defn select-all
  "Select occurrences of `s`, replace with `r`, leave cursors in post-deletion position"
  [cm s]
  (let [cur (.getSearchCursor cm s #js {:line 0 :ch 0} (= s (.toLowerCase s)))
        ranges (loop [ranges []]
                 (if-not (.findNext cur)
                   ranges
                   (recur (conj ranges #js {:anchor (.from cur) :head (.to cur)}))))]
    (.setSelections cm (clj->js ranges))
    cm))

(defn exec [cm command]
  (command cm)
  cm)

(defn test-exec [command pre-source]
  (.setValue (editor) (string/replace pre-source "'" \"))
  (-> (editor)
      (select-all "|")
      (replace-selections "")
      (exec command)
      (replace-selections "|")
      (.getValue)
      (string/replace \" "'")))
