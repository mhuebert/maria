(ns magic-tree.test-utils
  (:require [magic-tree-editor.codemirror]
            [cljsjs.codemirror :as CM]
            [codemirror.addon.search.searchcursor]
            [clojure.string :as string]))

(def editor
  (memoize (fn []
             (js/CodeMirror (doto (.createElement js/document "div")
                              (->> (.appendChild js/document.body))) (clj->js {:mode          "clojure"
                                                                               :magicBrackets true})))))

(defn regex-replace [cm pattern replace-f]
  (let [search-cursor (.getSearchCursor cm pattern (CM/Pos 0 0) true)]
    (loop [results []]
      (if (not (.findNext search-cursor))
        (do
          (.setSelections cm (clj->js results))
          (.replaceSelections cm (clj->js (mapv replace-f results)) "around")
          results)
        (recur (conj results (let [anchor (.from search-cursor)
                                   head (.to search-cursor)]
                               {:anchor anchor
                                :head   head
                                :text   (.getRange cm anchor head)})))))))

(defn replace-selections [cm f]
  (.replaceSelections cm (clj->js (mapv (fn [sel]
                                          (f {:anchor (.-anchor sel)
                                              :head (.-head sel)
                                              :text (.getRange cm (.-anchor sel) (.-head sel))})) (.listSelections cm))) "around"))

(defn deserialize-selections
  "Turn <ranges> into selected ranges."
  [cm]
  (regex-replace cm #"(<[^>]*>)|\|" (fn [{:keys [text]}]
                                      (if (= text "|")
                                        ""
                                        (subs text 1 (dec (count text))))))
  cm)

(defn serialize-selections
  [cm]
  (replace-selections cm (fn [{:keys [text]}]
                           (if (= text "")
                             "|"
                             (str "<" text ">"))))
  cm)

(defn exec [cm command]
  (command cm)
  cm)

(defn test-exec [command pre-source]
  (.setValue (editor) (string/replace pre-source "'" \"))
  (-> (editor)
      (deserialize-selections)
      (exec command)
      (serialize-selections)
      (.getValue)
      (string/replace \" "'")))
