(ns maria.code.node-view
  (:require [applied-science.js-interop :as j]
            [maria.prose.schema :as prose-schema]
            ["prosemirror-state" :refer [TextSelection Selection]]
            ["@codemirror/view" :as view :refer [EditorView]]
            ["prosemirror-history" :as history]
            ["@codemirror/commands" :as cmd]
            ["react-dom/client" :as react.client]
            ["react" :as react]
            [reagent.core :as reagent]
            [tools.maria.react-roots :as roots]
            [nextjournal.clojure-mode.util :as u]))

(defn use-watch [ref]
  (let [[value set-value!] (react/useState [nil @ref])]
    (react/useEffect
     (fn []
       (add-watch ref set-value! (fn [_ _ old new] (set-value! [old new])))
       #(remove-watch ref set-value!)))
    value))

(defn value-viewer [!result]
  (when-let [result (second (use-watch !result))]
    (if-let [error (:error result)]
      (str "Error: " error) ;; TODO  format error
      (pr-str (:value result)))))

(j/js
  (defn focus! [{:keys [code-view on-mount]}]
    (on-mount #(.focus code-view))))

(j/js
  (defn set-initial-focus!
    "If ProseMirror cursor is within code view, focus it."
    [{:as this :keys [get-node-pos prose-view code-view]}]
    (let [cursor (dec (.. prose-view -state -selection -$anchor -pos))
          start (get-node-pos)
          length (.. code-view -state -doc -length)
          end (+ (get-node-pos) length)]
      (when (and (>= cursor start)
                 (< cursor end))
        (focus! this)))))

(j/defn code-row [!result ^js {:as this :keys [code-view prose-view mounted!]}]
  (let [ref (react/useCallback (fn [^js el]
                                 (when el
                                   (.appendChild (.-firstChild el) (.-dom code-view))
                                   (set-initial-focus! this)
                                   (mounted!))))]
    [:div.-mx-4.mb-4.md:flex
     {:ref ref}
     [:div {:class "md:w-1/2 text-base bg-white"
            :style {:color "#c9c9c9"}}]

     [:div
      {:class "md:w-1/2 text-sm bg-slate-300"}
      [value-viewer !result]]]))

(j/js

  (defn code:forward-update
    "When the code-editor is focused, forward events from it to ProseMirror."
    [{:as this :keys [code-view prose-view get-node-pos updating?]} code-update]
    (let [{prose-state :state} prose-view]
      (when (and (.-hasFocus code-view) (not updating?))
        (let [start-pos (inc (get-node-pos))
              {from' :from to' :to} (.. code-update -state -selection -main)
              {code-changed? :docChanged
               code-changes :changes} code-update
              {:keys [tr doc]} prose-state]
          (when (or code-changed? (not (.eq (.. prose-state -selection)
                                            (.create TextSelection
                                                     doc
                                                     (+ start-pos from')
                                                     (+ start-pos to')))))

            ;; handle code changes
            (let [!offset (volatile! start-pos)]
              (.iterChanges code-changes
                            (fn [from-a to-a from-b to-b {:as text :keys [length]}]
                              (let [offset @!offset]
                                (if (pos-int? length)
                                  (.replaceWith tr
                                                (+ offset from-a)
                                                (+ offset to-a)
                                                (.text prose-schema/schema (.toString text)))
                                  (.delete tr
                                           (+ offset from-a)
                                           (+ offset to-a))))
                              ;; adjust offset for changes in length caused by the change,
                              ;; so further steps are in correct position
                              (vswap! !offset + (- (- to-b from-b)
                                                   (- to-a from-a))))))

            ;; handle selection changes
            (.setSelection tr (.create TextSelection
                                       (.-doc tr)
                                       (+ start-pos from')
                                       (+ start-pos to')))
            (.dispatch prose-view tr))))))

  (defn- controlled-update [this f]
    (j/!set this :updating? true)
    (f)
    (j/!set this :updating? false))

  (defn code-text [code-view] (.. code-view -state -doc (toString)))

  (defn prose:set-selection
    "Called when ProseMirror tries to put the selection inside the node."
    [{:as this :keys [code-view]} anchor head]
    (controlled-update this
      #(do (.dispatch code-view {:selection {:anchor anchor
                                             :head head}})
           (focus! this))))

  (defn text-diff [old-text new-text]
    (let [old-end (.-length old-text)
          new-end (.-length new-text)
          start (loop [start 0]
                  (if (and (< start old-end)
                           (== (.charCodeAt old-text start)
                               (.charCodeAt new-text start)))
                    (recur (inc start))
                    start))]
      (loop [old-end old-end
             new-end new-end]
        (if (and (> old-end start)
                 (> new-end start)
                 (== (.charCodeAt old-text (dec old-end))
                     (.charCodeAt new-text (dec new-end))))
          (recur (dec old-end)
                 (dec new-end))
          {:from start
           :to old-end
           :insert (.slice new-text start new-end)}))))

  (defn prose:forward-update [{:as this :keys [code-view]} prose-node]
    (j/!set this :prose-node prose-node)
    (boolean
     (when (= (.-type (j/get this :prose-node)) (.-type prose-node))
       (let [new-text (.-textContent prose-node)
             old-text (code-text code-view)]
         (when (not= new-text old-text)
           (controlled-update this
             (fn []
               (.dispatch code-view {:changes (text-diff old-text new-text)
                                     :annotations [(u/user-event-annotation "noformat")]})))))
       true)))

  (defn prose:select-node [{:keys [code-view]}]
    (.focus code-view))

  (defn init [this code-view]
    (let [parent (js/document.createElement "div")
          root (react.client/createRoot parent)
          {:keys [!result]} this
          this (j/extend! this
                 {;; NodeView API
                  :dom parent
                  :update (partial prose:forward-update this)
                  :selectNode (partial prose:select-node this)
                  :deselectNode (fn [] (prn :not-implemented--deselect-node))
                  :setSelection (partial prose:set-selection this)
                  :stopEvent (constantly true)
                  :destroy #(do (.destroy code-view)
                                (roots/unmount! root))

                  ;; Internal
                  :updating? false
                  :code-view code-view})]
      ;; TODO - implement eval, putting results into right-panel
      (roots/init! root #(reagent/as-element ^:clj [code-row !result this]))
      this)))