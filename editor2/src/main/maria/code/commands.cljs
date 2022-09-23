(ns maria.code.commands
  (:require [applied-science.js-interop :as j]
            ["prosemirror-model" :refer [ Fragment Slice]]
            ["prosemirror-state" :refer [TextSelection Selection NodeSelection]]
            ["prosemirror-commands" :as pm.cmd]
            [clojure.string :as str]
            [nextjournal.clojure-mode.node :as n]
            [maria.prose.schema :refer [schema]]))

(j/js

  (defn prose:cursor-node [state]
    (let [sel (.-selection state)]
      (or (.-node sel) (.-parent (.-$from sel)))))

  (defn prose:convert-to-code [state dispatch]
    (let [{:as node :keys [type]} (prose:cursor-node state)
          {{:keys [heading paragraph code_block]} :nodes} schema]
      (when (and (#{heading paragraph} type)
                 (= 0 (.-size (.-content node))))
        ((pm.cmd/setBlockType code_block) state dispatch)
        true)))

  (defn guard [x f] (when (f x) x))

  (defn code:cursors [{{:keys [ranges]} :selection}]
    (reduce (fn [out {:keys [from to]}]
              (if (not= from to)
                (reduced nil)
                (j/push! out from))) [] ranges))

  (defn code:cursor [state]
    (-> (code:cursors state)
        (guard #(= (count %) 1))
        first))

  (defn code:paragraph-after-code [{:keys [prose-view]} {:keys [state]}]
    (if (and (= (code:cursor state)
                (.. state -doc -length))
             (pm.cmd/exitCode (.-state prose-view) (.-dispatch prose-view)))
      (do
        (.focus prose-view)
        true)
      false))

  (defn code:arrow-handler
    "Moves cursor out of code block when navigating out of an edge"
    [{:keys [get-node-pos
             code-view
             prose-node
             prose-view]} unit dir]
    (let [{{:keys [doc selection]} :state} code-view
          {:keys [main]} selection
          {:keys [from to]} (if (= unit "line")
                              (.lineAt doc (.-head main))
                              main)
          at-end-of-block? (>= to (.-length doc))
          at-end-of-doc? (= (+ (get-node-pos) (.-nodeSize prose-node))
                            (.. prose-view -state -doc -content -size))]
      (cond (not (.-empty main)) false ;; something is selected
            (and (neg? dir) (pos? from)) false ;; moving backwards but not at beginning
            (and (pos? dir) (not at-end-of-block?)) false ;; moving forwards, not at end
            (and (= "line" unit)
                 at-end-of-block?
                 at-end-of-doc?) (when (pm.cmd/exitCode (.-state prose-view) (.-dispatch prose-view))
                                   (.focus prose-view)
                                   true)
            :else
            (let [node-pos (+ (get-node-pos) (if (neg? dir) 0 (.-nodeSize prose-node)))
                  {:keys [doc tr]} (.-state prose-view)
                  selection (.near Selection (.resolve doc node-pos) dir)]
              (doto prose-view
                (.dispatch (.. tr
                               (setSelection selection)
                               (scrollIntoView)))
                (.focus))))))

  (defn prose:arrow-handler [dir]
    (fn [state dispatch view]
      (boolean
       (when (and (.. state -selection -empty) (.endOfTextblock view dir))
         (let [$head (.. state -selection -$head)
               {:keys [doc]} state
               pos (if (pos? dir)
                     (.after $head)
                     (.before $head))
               next-pos (.near Selection (.resolve doc pos) dir)]
           (when (= :code_block (j/get-in next-pos [:$head :parent :type :name]))
             (dispatch (.. state -tr (setSelection next-pos)))
             true))))))

  (defn code:empty? [{{:keys [length text]} :doc}]
    (or (zero? length)
        (and (= 1 (count text))
             (str/blank? (first text)))))

  (defn code:length [state] (.. state -doc -length))

  (defn code:split [{:keys [get-node-pos prose-view]} {:keys [state]}]
    (if-let [cursor-prose-pos (some-> (code:cursor state)
                                      (guard #(n/program? (n/tree state %)))
                                      (+ 1 (get-node-pos)))]
      (do
        (.dispatch prose-view (.. prose-view -state -tr
                                  (split cursor-prose-pos)
                                  (scrollIntoView)))
        true)
      false))

  (defn code:remove [{:keys [get-node-pos prose-view code-view prose-node]}]
    (let [pos (get-node-pos)
          tr (.. prose-view -state -tr)]
      (doto tr
        (.deleteRange pos (+ pos (.-nodeSize prose-node)))
        (as-> tr
              (.setSelection tr (.near TextSelection (.resolve (.-doc tr) pos) -1)))
        (.scrollIntoView))

      (doto prose-view
        (.dispatch tr)
        (.focus)))
    true)

  (defn code:remove-on-backspace [this {:keys [state]}]
    (when (zero? (code:cursor state))
      (when (code:empty? state)
        (code:remove this))
      true))


  (comment
   ;; does not work, not sure why
   (defn code:convert-to-paragraph [{:keys [prose-view get-node-pos]} {:keys [state]}]
     (when (code:empty? state)
       (let [{{:keys [paragraph]} :nodes} schema
             {:as prose-state :keys [doc tr]} (.. prose-view -state)
             node (prose:cursor-node prose-state)
             pos (get-node-pos)]
         (.replaceRange tr
                        pos
                        (+ pos (.-nodeSize node))
                        (new Slice (.from Fragment (.create paragraph)) 0 0))
         (.dispatch prose-view tr)
         true)))))

(comment
 (j/log -tr)

 )