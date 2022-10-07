(ns maria.code.commands
  (:require [applied-science.js-interop :as j]
            ["prosemirror-model" :refer [Fragment Slice]]
            ["prosemirror-state" :refer [TextSelection Selection NodeSelection]]
            ["prosemirror-commands" :as pm.cmd]
            [clojure.string :as str]
            [nextjournal.clojure-mode.node :as n]
            [maria.prose.schema :refer [schema]]
            [maria.eval.sci :as sci]
            [promesa.core :as p]
            [nextjournal.clojure-mode.extensions.eval-region :as eval-region]
            [sci.async :as a]
            [re-db.reactive :as r]))

(j/js

  (defn bind-prose-command [this cmd]
    (fn []
      (let [{{:keys [state dispatch]} :proseView} this]
        (cmd state dispatch))))

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

  (defn set-result! [!result v]
    (reset! !result v))

  (defn code:eval-string! [{:as this :keys [!result]} source]
    (set-result! !result (sci/eval-string source)))

  (defn code:cursors [{{:keys [ranges]} :selection}]
    (reduce (fn [out {:keys [from to]}]
              (if (not= from to)
                (reduced nil)
                (j/push! out from))) [] ranges))

  (defn code:cursor [state]
    (-> (code:cursors state)
        (guard #(= (count %) 1))
        first))

  (defn code:paragraph-after-code [{:keys [proseView]} {:keys [state]}]
    (boolean
     (when (and (= (code:cursor state)
                   (.. state -doc -length))
                (pm.cmd/exitCode (.-state proseView) (.-dispatch proseView)))
       (.focus proseView)
       true)))

  (defn code:arrow-handler
    "Moves cursor out of code block when navigating out of an edge"
    [{:keys [getPos
             codeView
             proseNode
             proseView]} unit dir]
    (let [{{:keys [doc selection]} :state} codeView
          {:keys [main]} selection
          {:keys [from to]} (if (= unit "line")
                              (.lineAt doc (.-head main))
                              main)
          at-end-of-block? (>= to (.-length doc))
          at-end-of-doc? (= (+ (getPos) (.-nodeSize proseNode))
                            (.. proseView -state -doc -content -size))]
      (cond (not (.-empty main)) false ;; something is selected
            (and (neg? dir) (pos? from)) false ;; moving backwards but not at beginning
            (and (pos? dir) (not at-end-of-block?)) false ;; moving forwards, not at end
            (and (= "line" unit)
                 at-end-of-block?
                 at-end-of-doc?
                 (pos? dir)) (when (pm.cmd/exitCode (.-state proseView) (.-dispatch proseView))
                               (.focus proseView)
                               true)
            :else
            (let [next-pos (if (neg? dir)
                             (getPos)
                             (+ (getPos) (.-nodeSize proseNode)))
                  {:keys [doc tr]} (.-state proseView)
                  selection (.near Selection (.resolve doc next-pos) dir)]
              (doto proseView
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

  (defn code:split [{:keys [getPos proseView]} {:keys [state]}]
    (if-let [cursor-prose-pos (some-> (code:cursor state)
                                      (guard #(n/program? (n/tree state %)))
                                      (+ 1 (getPos)))]
      (do
        (.dispatch proseView (.. proseView -state -tr
                                 (split cursor-prose-pos)
                                 (scrollIntoView)))
        true)
      false))

  (defn code:remove [{:keys [getPos proseView codeView proseNode]}]
    (let [pos (getPos)
          tr (.. proseView -state -tr)]
      (doto tr
        (.deleteRange pos (+ pos (.-nodeSize proseNode)))
        (as-> tr
              (.setSelection tr (.near TextSelection (.resolve (.-doc tr) pos) -1)))
        (.scrollIntoView))

      (doto proseView
        (.dispatch tr)
        (.focus)))
    true)

  (defn code:remove-on-backspace [this {:keys [state]}]
    (when (zero? (code:cursor state))
      (when (code:empty? state)
        (code:remove this))
      true))

  (defn code:convert-to-paragraph [{:as this :keys [proseView getPos]} {:keys [state]}]
    (when (code:empty? state)
      (let [{{:keys [paragraph]} :nodes} schema
            {{:keys [tr selection]} :state} proseView
            node (.. selection -$from -parent)
            pos (getPos)
            tr (.setBlockType tr pos (+ pos (.-nodeSize node)) paragraph)]
        (doto proseView
          (.dispatch tr)
          (.focus))
        true)))

  (defn code:eval-block [{:as this :keys [codeView !result]}]
    (let [value (code:eval-string! this (.. codeView -state -doc (toString)))]
      (if (sci/await? value)
        (a/await
         (p/let [value* value]
           (set-result! !result value*)))
        value)))

  (defn code:eval-block! [this]
    (code:eval-block this)
    true)

  )
(defn prose:eval-doc! [^js prose-view]
  ;; TODO
  ;; use funcool/promesa to do this async so that any block which
  ;; returns a promise causes the doc to 'wait'
  (let [code-views (->> prose-view
                        .-docView
                        .-children
                        (keep (j/get :spec))
                        (filterv (j/get :codeView)))
        end (count code-views)]
    (p/loop [i 0]
      (when (< i end)
        (let [value (code:eval-block (nth code-views i))]
          (if (sci/await? value)
            (p/do value
                  (p/recur (inc i)))
            (p/recur (inc i))))))))

(j/defn prose:next-code-cell [proseView]
  (when-let [index (.. proseView -state -selection -$anchor (index 0))]
    (when-let [next-node (->> proseView
                              .-docView
                              .-children
                              (drop (inc index))
                              (keep (j/get :spec))
                              first)]
      (j/js (let [{:keys [codeView dom]} next-node]
              (.dispatch codeView
                         {:selection {:anchor 0
                                      :head 0}})
              (.focus codeView)
              (.scrollIntoView dom {:block :center})))
      true)))

(j/js

  (defn code:eval-current-region [{:as this {:keys [state]} :codeView}]
    (when-let [source (guard (eval-region/current-str state) (complement str/blank?))]
      (code:eval-string! this source))
    true)

  (defn code:copy-current-region [{:keys [state]}]
    (j/call-in js/navigator [:clipboard :writeText] (eval-region/current-str state))
    true))