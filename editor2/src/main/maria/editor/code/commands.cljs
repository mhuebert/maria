(ns maria.editor.code.commands
  (:require ["prosemirror-model" :refer [Fragment Slice]]
            ["prosemirror-state" :refer [TextSelection Selection NodeSelection insertPoint]]
            ["prosemirror-commands" :as pm.cmd]
            [applied-science.js-interop :as j]
            [applied-science.js-interop.alpha :refer [js]]
            [clojure.string :as str]
            [maria.editor.code.eval-region :as eval-region]
            [maria.editor.code.parse-clj :as parse-clj]
            [maria.editor.code.sci :as sci]
            [maria.editor.prosemirror.schema :refer [schema markdown->doc]]
            [maria.editor.util :as u]
            [nextjournal.clojure-mode.commands :refer [paredit-index]]
            [nextjournal.clojure-mode.node :as n]
            [promesa.core :as p]
            [sci.async :as a]
            [sci.impl.namespaces :as sci.ns]))


(defonce !result-key (volatile! 0))

(js
  (defn prose:replace-doc [{:as ProseView :keys [state]} new-source]
    (let [new-doc (-> new-source
                      parse-clj/clojure->markdown
                      markdown->doc)]
      (.dispatch ProseView
                 (.. state -tr
                     (replace 0
                              (.. state -doc -content -size)
                              (new Slice (.-content new-doc) 0 0))))
      true)))

(js
  (defn prose:cursor-node [{:as state :keys [selection]}]
    (or (.-node selection) (.-parent (.-$from selection)))))

(js
  (defn prose:convert-to-code [state dispatch]
    (let [{:as node :keys [type]} (prose:cursor-node state)
          {{:keys [heading paragraph code_block]} :nodes} schema]
      (when (and (#{heading paragraph} type)
                 (= 0 (.-size (.-content node))))
        ((pm.cmd/setBlockType code_block) state dispatch)
        true))))


(js
  (defn code:cursors [{{:keys [ranges]} :selection}]
    (reduce (fn [out {:keys [from to]}]
              (if (not= from to)
                (reduced nil)
                (j/push! out from))) [] ranges)))

(js
  (defn code:cursor [state]
    (-> (code:cursors state)
        (u/guard #(= (count %) 1))
        first)))

(js
  (defn code:split [{:keys [state]
                     {:keys [getPos ProseView]} :NodeView}]
    (if-let [cursor-prose-pos (some-> (code:cursor state)
                                      (u/guard #(n/program? (n/tree state %)))
                                      u/type:number         ;; cljs bug doesn't like use of `guard` above
                                      (+ 1 (getPos)))]
      (do
        (.dispatch ProseView (.. ProseView -state -tr
                                 (split cursor-prose-pos)
                                 (scrollIntoView)))
        true)
      false)))

(js
  (defn code:length [state] (.. state -doc -length)))

(js
  (defn code:empty? [{:as state {:keys [length text]} :doc}]
    (or (zero? length)
        (and (= 1 (count text))
             (str/blank? (first text))))))

(js
  (defn code:insert-another-code-block [{:as CodeView CodeState :state
                                         {:keys [ProseView getPos]} :NodeView}]
    (let [insert-pos (+ (getPos) (code:length CodeState) 1)
          {{:keys [code_block]} :nodes} schema]
      (.dispatch ProseView
                 (doto (.. ProseView -state -tr)
                   (.insert insert-pos (.create code_block))
                   (as-> tr
                         (.setSelection tr (.near TextSelection
                                                  (.. tr -doc (resolve (+ insert-pos 2))) 1)))
                   (.scrollIntoView)))
      (.focus ProseView)
      true)))

(js
  (defn code:arrow-handler
    "Moves cursor out of code block when navigating out of an edge"
    [unit dir]
    (fn [{{:keys [getPos
                  CodeView
                  proseNode
                  ProseView]} :NodeView}]
      (let [{{:keys [doc selection]} :state} CodeView
            {:keys [main]} selection
            {:keys [from to]} (if (= unit "line")
                                (.lineAt doc (.-head main))
                                main)
            at-end-of-block? (>= to (.-length doc))
            at-end-of-doc? (= (+ (getPos) (.-nodeSize proseNode))
                              (.. ProseView -state -doc -content -size))]
        (cond (not (.-empty main)) false                    ;; something is selected
              (and (neg? dir) (pos? from)) false            ;; moving backwards but not at beginning
              (and (pos? dir) (not at-end-of-block?)) false ;; moving forwards, not at end
              (and (= "line" unit)
                   at-end-of-block?
                   at-end-of-doc?
                   (pos? dir)) (when (pm.cmd/exitCode (.-state ProseView) (.-dispatch ProseView))
                                 (.focus ProseView)
                                 true)
              :else
              (let [next-pos (if (neg? dir)
                               (getPos)
                               (+ (getPos) (.-nodeSize proseNode)))
                    {:keys [doc tr]} (.-state ProseView)
                    selection (.near Selection (.resolve doc next-pos) dir)]
                (doto ProseView
                  (.dispatch (.. tr
                                 (setSelection selection)
                                 (scrollIntoView)))
                  (.focus))))))))

(js
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
              true)))))))

(js
  (defn code:remove [{:as CodeView
                      {:keys [getPos ProseView proseNode]} :NodeView}]
    (let [pos (getPos)
          tr (.. ProseView -state -tr)]
      (doto tr
        (.deleteRange pos (+ pos (.-nodeSize proseNode)))
        (as-> tr
              (.setSelection tr (.near TextSelection (.resolve (.-doc tr) pos) -1)))
        (.scrollIntoView))

      (doto ProseView
        (.dispatch tr)
        (.focus)))
    true))

(js
  (defn code:remove-on-backspace [{:as CodeView
                                   :keys [state]}]
    (when (zero? (code:cursor state))
      (when (code:empty? state)
        (code:remove CodeView))
      true)))

(js
  (defn code:convert-to-paragraph [{:as CodeView
                                    :keys [state]
                                    {:keys [ProseView getPos]} :NodeView}]
    (when (code:empty? state)
      (let [{{:keys [paragraph]} :nodes} schema
            {{:keys [tr selection]} :state} ProseView
            node (.. selection -$from -parent)
            pos (getPos)
            tr (.setBlockType tr pos (+ pos (.-nodeSize node)) paragraph)]
        (doto ProseView
          (.dispatch tr)
          (.focus))
        true))))

(js
  (defn code:handle-escape [{:as CodeView :keys [state]}]
    (when (code:empty? state)
      (code:remove CodeView)
      true)))
(js
  (defn code:handle-enter [{:as CodeView :keys [state]}]
    (or (and (code:empty? state)
             (code:convert-to-paragraph CodeView))
        (and (= (code:cursor state) (code:length state))
             (code:insert-another-code-block CodeView))
        (code:split CodeView)
        ((:enter-and-indent paredit-index) CodeView))))

(js
  (defn code:block-str [{:keys [CodeView]}]
    (.. CodeView -state -doc (toString))))



(js
  (defn index [this]
    (reduce (j/fn [out ^js {:keys [spec]}]
              (if (identical? spec this)
                (reduced out)
                (inc out))) 0
            (.. this -ProseView -docView -children))))

(j/defn code-nodes [^js {:as this :keys [ProseView]}]
  (into []
        (comp (map (j/get :spec))
              (filter (j/get :!result)))
        (.. ProseView -docView -children)))

(j/defn code-node-by-id [this id]
  (first (filter #(= (j/get % :id) id)
                 (code-nodes this))))

(j/defn code:ns
  "Returns the first evaluated namespace above this code block, or user"
  [^js {:as this :keys [ProseView]}]
  (let [children (.. ProseView -docView -children)]
    (or (u/akeep-first #(some-> (j/get-in % [:spec :!result])
                                deref
                                :value
                                (u/guard (partial instance? sci.lang/Namespace)))
                       (dec (index this))
                       -1
                       children)
        (sci.ns/sci-find-ns @(j/get ProseView :!sci-ctx) 'user))))

(j/defn code:eval-form-in-show
  [{:sci/keys [context get-ns]} form]
  (try (sci/eval-form-sync context (get-ns) form)
       (catch js/Error e ^:clj {:error e})))

(j/defn code:eval-result! [{:sci/keys [context get-ns file]} source on-result]
  (let [_ (assert "sci context not found")
        result (try (sci/eval-string context
                                     {:ns (get-ns)
                                      :clojure.core/eval-file file}
                                     source)
                    (catch js/Error e ^:clj {:error e}))]
    (if (a/await? result)
      (do (on-result {:value :maria.editor.code.show-values/loading})
          (a/await
            (-> result
                (.then (fn [result] (on-result result)))
                (.catch (fn [e] (on-result {:error e}))))))
      (on-result result))))

(j/defn code:eval-NodeView! [^js {:as this :keys [ProseView !result]}]
  (code:eval-result! {:sci/context @(j/get ProseView :!sci-ctx)
                      :sci/get-ns (partial code:ns this)
                      :sci/file (str "eval_" (j/get this :id))}
                     (code:block-str this)
                     (fn [v]
                       (when-not (j/get ProseView :isDestroyed)
                         (reset! !result (assoc v :key (vswap! !result-key inc)))))))

(j/defn prose:eval-prose-view! [^js ProseView]
  (let [NodeViews (->> ProseView
                       .-docView
                       .-children
                       (keep (j/get :spec))
                       (filterv (j/get :CodeView)))
        {:as ctx :keys [last-ns]} @(j/get ProseView :!sci-ctx)]
    (vreset! last-ns (sci.ns/sci-find-ns ctx 'user))
    ((fn continue [i]
       (when-not (j/get ProseView :isDestroyed)
         (when-let [NodeView (nth NodeViews i nil)]
           (let [value (code:eval-NodeView! NodeView)]
             (if (a/await? value)
               (p/do value (continue (inc i)))
               (continue (inc i))))))
       nil) 0)))

(js

  (defn code:copy-current-region [{:keys [state]}]
    (j/call-in js/navigator [:clipboard :writeText] (eval-region/current-selection-str state))
    true)

  (defn code:cut-current-region [{:as CodeView :keys [state]}]
    (j/call-in js/navigator [:clipboard :writeText] (eval-region/current-selection-str state))
    (eval-region/handle-backspace CodeView)))