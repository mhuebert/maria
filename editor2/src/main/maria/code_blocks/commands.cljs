(ns maria.code-blocks.commands
  (:require [applied-science.js-interop :as j]
            ["prosemirror-model" :refer [Fragment Slice]]
            ["prosemirror-state" :refer [TextSelection Selection NodeSelection insertPoint]]
            ["prosemirror-commands" :as pm.cmd]
            [clojure.string :as str]
            [nextjournal.clojure-mode.node :as n]
            [maria.prosemirror.schema :refer [schema]]
            [maria.code-blocks.sci :as sci]
            [promesa.core :as p]
            [maria.code-blocks.eval-region :as eval-region]
            [sci.async :as a]
            [maria.util :as u]
            [sci.impl.namespaces :as sci.ns]))


(defonce !result-key (volatile! 0))
(j/defn set-result! [^js {:keys [!result]} v]
  (reset! !result (assoc v :key (vswap! !result-key inc))))

(j/js
  (defn bind-prose-command [cmd]
    (fn [{{{:keys [state dispatch]} :proseView} :node-view}]
      (cmd state dispatch)))

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
  (defn code:cursors [{{:keys [ranges]} :selection}]
    (reduce (fn [out {:keys [from to]}]
              (if (not= from to)
                (reduced nil)
                (j/push! out from))) [] ranges))
  (defn code:cursor [state]
    (-> (code:cursors state)
        (u/guard #(= (count %) 1))
        first))

  (defn code:split [{:keys [state]
                     {:keys [getPos proseView]} :node-view}]
    (if-let [cursor-prose-pos (some-> (code:cursor state)
                                      (u/guard #(n/program? (n/tree state %)))
                                      u/type:number ;; cljs bug doesn't like use of `guard` above
                                      (+ 1 (getPos)))]
      (do
        (.dispatch proseView (.. proseView -state -tr
                                 (split cursor-prose-pos)
                                 (scrollIntoView)))
        true)
      false))

  (defn code:length [state] (.. state -doc -length))

  (defn code:insert-another-code-block [{:keys [state]
                                    {:keys [proseView getPos]} :node-view}]
    (let [code-length (code:length state)]
      (if (= (code:cursor state) code-length)
        (let [insert-pos (+ (getPos) code-length 1)
              {{:keys [code_block]} :nodes} schema]
          (.dispatch proseView
                     (doto (.. proseView -state -tr)
                       (.insert insert-pos (.create code_block))
                       (as-> tr
                             (.setSelection tr (.near TextSelection
                                                      (.. tr -doc (resolve (+ insert-pos 2))) 1)))
                       (.scrollIntoView)))
          (.focus proseView)
          true)
        false)))

  (defn code:arrow-handler
    "Moves cursor out of code block when navigating out of an edge"
    [unit dir]
    (fn [{{:keys [getPos
                  codeView
                  proseNode
                  proseView]} :node-view}]
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
                  (.focus)))))))

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

  (defn code:remove-on-backspace [{:keys [state]
                                   this :node-view}]
    (when (zero? (code:cursor state))
      (when (code:empty? state)
        (code:remove this))
      true))

  (defn code:convert-to-paragraph [{:keys [state]
                                    {:keys [proseView getPos]} :node-view}]
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

  (defn code:block-str [{:keys [codeView]}]
    (.. codeView -state -doc (toString)))

  )

(j/js
  (defn index [{:keys [proseView getPos]}]
    (.. proseView -state -doc (resolve (getPos)) (index 0))))

(j/defn code-nodes [^js {:as this :keys [proseView]}]
  (into []
        (comp (map (j/get :spec))
              (filter (j/get :!result)))
        (.. proseView -docView -children)))

(j/defn code-node-by-id [this id]
  (first (filter #(= (j/get % :id) id)
                 (code-nodes this))))

(j/defn code:ns
  "Returns the first evaluated namespace above this code block, or user"
  [^js {:as this :keys [proseView]}]
  (or (u/akeep-first #(some-> (j/get-in % [:spec :!result])
                              deref
                              :value
                              (u/guard (partial instance? sci.lang/Namespace)))
                     (dec (index this))
                     -1
                     (.. proseView -docView -children))
      (sci.ns/sci-find-ns @(j/get proseView :!sci-ctx) 'user)))

(j/defn code:eval-string!
  ([node-view source] (code:eval-string! nil node-view source))
  ([opts node-view source]
   (let [opts (-> opts
                  (update :ns #(or % (code:ns node-view)))
                  (assoc :clojure.core/eval-file (j/get node-view :id)))
         ctx @(j/get-in node-view [:proseView :!sci-ctx])
         _ (assert "code:eval-string! requires sci context")
         result (try (sci/eval-string ctx opts source)
                     (catch js/Error e ^:clj {:error e}))]
     (if (a/await? result)
       (do (set-result! node-view {:value :maria.show-values/loading})
           (a/await
            (-> result
                (.then (fn [result] (set-result! node-view result)))
                (.catch (fn [e] (set-result! node-view {:error e}))))))
       (set-result! node-view result)))))

(defn code:eval-block! [this]
  (code:eval-string! this (code:block-str this))
  true)


(j/defn prose:eval-doc! [^js proseView]
  (let [node-views (->> proseView
                        .-docView
                        .-children
                        (keep (j/get :spec))
                        (filterv (j/get :codeView)))
        {:as ctx :keys [last-ns]} @(j/get proseView :!sci-ctx)]
    (vreset! last-ns (sci.ns/sci-find-ns ctx 'user))
    ((fn continue [i]
       (when-let [node-view (nth node-views i nil)]
         (let [value (code:eval-string! node-view (code:block-str node-view))]
           (if (a/await? value)
             (p/do value (continue (inc i)))
             (continue (inc i)))))
       nil) 0)))

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

  #_(defn code:eval-current-region [{:as this {:keys [state]} :codeView}]
      (when-let [source (u/guard (eval-region/current-selection-str state) (complement str/blank?))]
        (code:eval-string! this source))
      true)

  (defn code:copy-current-region [{:keys [state]}]
    (j/call-in js/navigator [:clipboard :writeText] (eval-region/current-selection-str state))
    true))