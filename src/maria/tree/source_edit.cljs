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

(defn copy [cm text]
  (let [old-sel (when (.somethingSelected cm) (.getSelections cm))
        hadFocus (.-activeElement js/document)]
    (doto paste-element
      (.setAttribute "value" text)
      (.select))
    (try (.execCommand js/document "copy")
         (catch js/Error e (.error js/console "Copy command didn't work. Maybe a browser incompatibility?")))

    (when old-sel (.setSelections cm old-sel))
    (.focus hadFocus)))

(defn cut-range [cm range]
  (let [[from to] (cm-util/parse-range range)]
    (copy cm (.getRange cm from to))
    (.replaceRange cm "" from to)))

(defn copy-range [cm range]
  (let [[from to] (cm-util/parse-range range)]
    (copy cm (.getRange cm from to))))

(defn cm-pos [{:keys [line column]}]
  #js {:line line :ch column})

(defn node-boundary
  [node side]
  (case side :left (select-keys node [:line :column])
             :right {:line   (:end-line node)
                     :column (:end-column node)}))

(defn get-kill-range [pos loc]
  (let [locs-to-delete (->> (cons loc (tree/right-locs loc))
                            (take-while (comp not tree/newline? z/node)))]
    (merge (select-keys pos [:line :column])
           (select-keys (some-> (or (last locs-to-delete) loc) z/node) [:end-line :end-column]))))

(defn cursor-move-fn [side]
  (fn [cm {{{:keys [pos loc]} :cursor} :state}]
    (let [next-loc (case side :left z/left :right z/right)]
      (loop [loc loc]
        (cond (not= pos (node-boundary (z/node loc) side)) (.setCursor cm (cm-pos (node-boundary (z/node loc) side)))
              (next-loc loc) (recur (next-loc loc))
              :else (some->> (z/up loc) recur))))))

(def commands {:kill
               (fn [cm {{{pos :pos loc :loc} :cursor} :state}]
                 ;; if selection, cut selection instead of kill
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
               :move-left
               (cursor-move-fn :left)
               :move-right
               (cursor-move-fn :right)})



(def key-commands
  (reduce-kv (fn [m k command]
               (assoc m k (get commands command #(prn command))))
             {}
             {"Ctrl-K"        :kill
              "Cmd-X"         :cut-at-point
              "Cmd-Backspace" :cut-at-point
              "Cmd-C"         :copy-at-point
              "Alt-Left"      :move-left
              "Alt-Right"     :move-right


              "Cmd-1"         :expand-selection
              "Cmd-2"         :shrink-selection
              }))